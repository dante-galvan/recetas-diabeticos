package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.CollectionEntity
import com.example.data.local.DiaRecipesDatabase
import com.example.data.model.DefaultCollections
import com.example.data.repository.CollectionActionResult
import com.example.data.repository.CreateCollectionResult
import com.example.data.repository.RecipeRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Verifies the built-in collections: they must exist on a fresh install, survive
 * being initialised more than once, never duplicate or wipe user data, and never
 * be pre-filled with invented recipes.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class DefaultCollectionsTest {

  private lateinit var context: Context
  private lateinit var db: DiaRecipesDatabase
  private lateinit var repository: RecipeRepository

  @Before
  fun setUp() {
    context = ApplicationProvider.getApplicationContext()
    db = Room.inMemoryDatabaseBuilder(context, DiaRecipesDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    repository = RecipeRepository(db.dao(), context)
  }

  @After
  fun tearDown() {
    db.close()
  }

  private fun realRecipeId(index: Int = 0): String = repository.getRecipes()[index].id

  // 1. A brand new install shows every built-in collection, empty.
  @Test
  fun freshInstall_createsEveryDefaultCollection_empty() = runBlocking {
    val created = repository.ensureDefaultCollections()

    assertTrue("First run must create defaults", created)
    val collections = repository.collections.first()
    assertEquals(DefaultCollections.all.size, collections.size)
    assertEquals(
      DefaultCollections.all.map { it.id }.toSet(),
      collections.map { it.id }.toSet()
    )
    assertTrue("Defaults must start with no recipes", collections.all { it.recipeIds.isEmpty() })
  }

  // 2. Re-running initialisation never duplicates collections.
  @Test
  fun ensureDefaultCollections_isIdempotent() = runBlocking {
    repository.ensureDefaultCollections()
    val secondRunCreated = repository.ensureDefaultCollections()

    assertFalse("Second run must not create anything", secondRunCreated)
    assertEquals(DefaultCollections.all.size, repository.collections.first().size)
  }

  // 10. User-created collections and their saved recipes survive initialisation.
  @Test
  fun ensureDefaultCollections_preservesUserCollectionsAndRecipes() = runBlocking {
    val userResult = repository.createCollection("Mías") as CreateCollectionResult.Success
    val userCollectionId = userResult.collection.id
    val recipeId = realRecipeId()
    repository.addRecipeToCollection(userCollectionId, recipeId)

    repository.ensureDefaultCollections()

    val userCollection = repository.collections.first().find { it.id == userCollectionId }
    assertTrue("User collection must remain", userCollection != null)
    assertEquals(setOf(recipeId), userCollection!!.recipeIds)
    assertEquals(DefaultCollections.all.size + 1, repository.collections.first().size)
  }

  // Redundant defaults are not recreated when the user already owns the same name.
  @Test
  fun ensureDefaultCollections_skipsDuplicateNames() = runBlocking {
    repository.createCollection("Desayunos")

    repository.ensureDefaultCollections()

    val collections = repository.collections.first()
    // The user's "Desayunos" stays and the built-in breakfast is not duplicated.
    assertEquals(1, collections.count { it.nameEs.equals("Desayunos", ignoreCase = true) })
    assertFalse(DefaultCollections.BREAKFAST_ID in collections.map { it.id })
  }

  // 4 & 5. Saving then removing a recipe toggles membership reliably.
  @Test
  fun addThenRemoveRecipe_updatesCollection() = runBlocking {
    repository.ensureDefaultCollections()
    val recipeId = realRecipeId()

    assertEquals(
      CollectionActionResult.Success,
      repository.addRecipeToCollection(DefaultCollections.BREAKFAST_ID, recipeId)
    )
    assertEquals(
      setOf(recipeId),
      repository.collections.first().first { it.id == DefaultCollections.BREAKFAST_ID }.recipeIds
    )

    assertEquals(
      CollectionActionResult.Success,
      repository.removeRecipeFromCollection(DefaultCollections.BREAKFAST_ID, recipeId)
    )
    assertTrue(
      repository.collections.first().first { it.id == DefaultCollections.BREAKFAST_ID }.recipeIds.isEmpty()
    )
  }

  // 8. A recipe can live in two collections; removing it from one keeps the other.
  @Test
  fun removingFromOneCollection_keepsTheOther() = runBlocking {
    repository.ensureDefaultCollections()
    val recipeId = realRecipeId()
    repository.addRecipeToCollection(DefaultCollections.LUNCH_ID, recipeId)
    repository.addRecipeToCollection(DefaultCollections.DINNER_ID, recipeId)

    repository.removeRecipeFromCollection(DefaultCollections.LUNCH_ID, recipeId)

    val collections = repository.collections.first()
    assertTrue(collections.first { it.id == DefaultCollections.LUNCH_ID }.recipeIds.isEmpty())
    assertEquals(setOf(recipeId), collections.first { it.id == DefaultCollections.DINNER_ID }.recipeIds)
  }

  // 9. The same recipe cannot be stored twice in a single collection.
  @Test
  fun addingSameRecipeTwice_doesNotDuplicate() = runBlocking {
    repository.ensureDefaultCollections()
    val recipeId = realRecipeId()

    repository.addRecipeToCollection(DefaultCollections.SNACKS_ID, recipeId)
    repository.addRecipeToCollection(DefaultCollections.SNACKS_ID, recipeId)

    assertEquals(
      setOf(recipeId),
      repository.collections.first().first { it.id == DefaultCollections.SNACKS_ID }.recipeIds
    )
  }

  // 7. Collections and their recipes survive rebuilding the repository (i.e. a
  // process restart reading the same database).
  @Test
  fun collectionsAndRecipes_persistAcrossRepositoryRecreation() = runBlocking {
    repository.ensureDefaultCollections()
    val recipeId = realRecipeId()
    repository.addRecipeToCollection(DefaultCollections.DESSERTS_ID, recipeId)

    val recreated = RecipeRepository(db.dao(), context)
    recreated.ensureDefaultCollections()

    val desserts = recreated.collections.first().single { it.id == DefaultCollections.DESSERTS_ID }
    assertEquals(setOf(recipeId), desserts.recipeIds)
  }

  // 11. A storage failure is reported instead of silently succeeding.
  @Test
  fun ensureDefaultCollections_afterStorageFailure_reportsNothingCreated() = runBlocking {
    db.close()

    val created = repository.ensureDefaultCollections()

    assertFalse("A failed initialisation must not report success", created)
  }

  // Malformed rows already in storage are tolerated (no crash, no wipe).
  @Test
  fun ensureDefaultCollections_toleratesPreExistingRows() = runBlocking {
    db.dao().insertCollection(
      CollectionEntity(
        id = "col_whatever",
        nameEs = "Personal",
        nameEn = "Personal",
        icon = "folder",
        recipeIdsCsv = "recipe_1,recipe_1"
      )
    )

    repository.ensureDefaultCollections()

    val personal = repository.collections.first().single { it.id == "col_whatever" }
    assertEquals(setOf("recipe_1"), personal.recipeIds)
  }

  @Test
  fun isDefault_distinguishesBuiltInFromUserCollections() {
    assertTrue(DefaultCollections.isDefault(DefaultCollections.SOUPS_ID))
    assertFalse(DefaultCollections.isDefault("col_user_created"))
  }
}
