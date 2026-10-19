package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.CollectionEntity
import com.example.data.local.DiaRecipesDatabase
import com.example.data.repository.CollectionActionResult
import com.example.data.repository.CreateCollectionResult
import com.example.data.repository.RecipeRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * End-to-end (repository + Room) tests for personal collections. A real catalog is
 * loaded from the JSON assets so recipe ids used here match the shipped app.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CollectionRepositoryTest {

  private lateinit var context: Context
  private lateinit var db: DiaRecipesDatabase
  private lateinit var repository: RecipeRepository
  private var dbClosed = false

  @Before
  fun setUp() {
    context = ApplicationProvider.getApplicationContext()
    db = Room.inMemoryDatabaseBuilder(context, DiaRecipesDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    repository = RecipeRepository(db.dao(), context)
    dbClosed = false
  }

  @After
  fun tearDown() {
    if (!dbClosed) db.close()
  }

  private fun realRecipeId(index: Int = 0): String = repository.getRecipes()[index].id

  private fun createCollection(name: String): String {
    val result = runBlocking { repository.createCollection(name) }
    assertTrue("Expected success for '$name'", result is CreateCollectionResult.Success)
    return (result as CreateCollectionResult.Success).collection.id
  }

  // 1. Create collection with a valid name.
  @Test
  fun createCollection_withValidName_isPersisted() = runBlocking {
    val result = repository.createCollection("Quick dinners")

    assertTrue(result is CreateCollectionResult.Success)
    val collections = repository.collections.first()
    assertEquals(1, collections.size)
    assertEquals("Quick dinners", collections.first().nameEs)
    assertTrue(collections.first().recipeIds.isEmpty())
  }

  // 2. Blank / whitespace-only names are rejected.
  @Test
  fun createCollection_withBlankName_isRejected() = runBlocking {
    assertEquals(CreateCollectionResult.BlankName, repository.createCollection("   "))
    assertEquals(CreateCollectionResult.BlankName, repository.createCollection(""))
    assertTrue(repository.collections.first().isEmpty())
  }

  @Test
  fun createCollection_withDuplicateName_isRejected() = runBlocking {
    repository.createCollection("Weekend")

    val duplicate = repository.createCollection("  weekend  ")

    assertEquals(CreateCollectionResult.DuplicateName, duplicate)
    assertEquals(1, repository.collections.first().size)
  }

  // 3 & 10. Add resolver: recipes are resolved by their stable ids.
  @Test
  fun addRecipeToCollection_resolvesRecipeById() = runBlocking {
    val id = createCollection("Favs")
    val recipeId = realRecipeId()

    assertEquals(CollectionActionResult.Success, repository.addRecipeToCollection(id, recipeId))

    val collection = repository.collections.first().first()
    val resolved = repository.getRecipesByIds(collection.recipeIds)
    assertEquals(listOf(recipeId), resolved.map { it.id })
  }

  // 4. The same recipe cannot be stored twice in one collection.
  @Test
  fun addSameRecipeTwice_doesNotDuplicate() = runBlocking {
    val id = createCollection("Favs")
    val recipeId = realRecipeId()

    repository.addRecipeToCollection(id, recipeId)
    repository.addRecipeToCollection(id, recipeId)

    assertEquals(setOf(recipeId), repository.collections.first().first().recipeIds)
  }

  // 5. Different recipes can be added to the same collection.
  @Test
  fun addDifferentRecipes_allStored() = runBlocking {
    val id = createCollection("Favs")
    val first = realRecipeId(0)
    val second = realRecipeId(1)

    repository.addRecipeToCollection(id, first)
    repository.addRecipeToCollection(id, second)

    assertEquals(setOf(first, second), repository.collections.first().first().recipeIds)
  }

  // 6. Removing a recipe from a collection never removes it from the catalog.
  @Test
  fun removeRecipeFromCollection_keepsCatalogIntact() = runBlocking {
    val id = createCollection("Favs")
    val recipeId = realRecipeId()
    repository.addRecipeToCollection(id, recipeId)

    val result = repository.removeRecipeFromCollection(id, recipeId)

    assertEquals(CollectionActionResult.Success, result)
    assertTrue(repository.collections.first().first().recipeIds.isEmpty())
    assertNotNull("Recipe must remain in the catalog", repository.getRecipeById(recipeId))
  }

  // 7. Deleting a collection never deletes the original recipes.
  @Test
  fun deleteCollection_keepsRecipesInCatalog() = runBlocking {
    val id = createCollection("Favs")
    val recipeId = realRecipeId()
    repository.addRecipeToCollection(id, recipeId)

    val result = repository.deleteCollection(id)

    assertEquals(CollectionActionResult.Success, result)
    assertTrue(repository.collections.first().isEmpty())
    assertNotNull(repository.getRecipeById(recipeId))
  }

  // 8. Collections survive recreating the repository over the same storage.
  @Test
  fun collections_persistWhenRepositoryIsRecreated() = runBlocking {
    val id = createCollection("Persisted")
    val recipeId = realRecipeId()
    repository.addRecipeToCollection(id, recipeId)

    val recreated = RecipeRepository(db.dao(), context)

    val collection = recreated.collections.first().single()
    assertEquals("Persisted", collection.nameEs)
    assertEquals(setOf(recipeId), collection.recipeIds)
  }

  // 9. Missing recipe ids are skipped instead of crashing the screen.
  @Test
  fun resolvingCollection_skipsMissingRecipeIds() = runBlocking {
    val recipeId = realRecipeId()
    db.dao().insertCollection(
      CollectionEntity(
        id = "col_with_missing",
        nameEs = "Mixed",
        nameEn = "Mixed",
        icon = "folder",
        recipeIdsCsv = "recipe_that_does_not_exist,$recipeId"
      )
    )

    val collection = repository.collections.first().single()
    val resolved = repository.getRecipesByIds(collection.recipeIds)

    assertEquals(listOf(recipeId), resolved.map { it.id })
  }

  // 10. Persistence failures must not be reported as success.
  @Test
  fun createCollection_afterStorageFailure_reportsFailure() {
    db.close()
    dbClosed = true

    val result = runBlocking { repository.createCollection("Will fail") }

    assertTrue("Storage errors must surface as Failure", result is CreateCollectionResult.Failure)
  }

  @Test
  fun addRecipeToCollection_afterStorageFailure_reportsFailure() = runBlocking {
    val id = createCollection("Favs")
    db.close()
    dbClosed = true

    val result = repository.addRecipeToCollection(id, realRecipeId())

    assertFalse(result == CollectionActionResult.Success)
  }
}
