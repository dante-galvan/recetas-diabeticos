package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.DiaRecipesDatabase
import com.example.data.model.MealSlot
import com.example.data.model.RecipeCategory
import com.example.data.repository.RecipeRepository
import com.example.data.repository.WeeklyMenuResult
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.random.Random
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Tests the weekly menu generator against the real JSON catalog using a
 * reproducible [Random] so every assertion is deterministic.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class WeeklyMenuRepositoryTest {

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

  private fun persistedRecipeIds(): List<String> =
    runBlocking { db.dao().getAllMealPlanItems().first().map { it.recipeId } }

  // 1 & 2 & 3 & 4. Real catalog -> seven complete days, valid recipe ids.
  @Test
  fun generateMenu_withRealCatalog_producesSevenValidDays() = runBlocking {
    val result = repository.generateWeeklyMenu(Random(1))

    assertTrue(result is WeeklyMenuResult.Success)
    assertEquals(28, (result as WeeklyMenuResult.Success).mealCount)

    val items = repository.mealPlanItems.first()
    assertEquals(28, items.size)

    val days = items.map { it.dayOfWeek }.distinct().sorted()
    assertEquals((1..7).toList(), days)

    val catalogIds = repository.getRecipes().map { it.id }.toSet()
    assertTrue("Every meal must reference a real recipe", items.all { it.recipe.id in catalogIds })
    assertTrue(items.none { it.recipe.id.isBlank() })

    (1..7).forEach { day ->
      assertEquals("Each day must have 4 meals", 4, items.count { it.dayOfWeek == day })
    }
  }

  // 5. With enough recipes per slot, the same recipe is not repeated in a week.
  @Test
  fun generateMenu_withEnoughRecipes_avoidsRepeatsPerSlot() = runBlocking {
    repository.generateWeeklyMenu(Random(42))

    val items = repository.mealPlanItems.first()
    MealSlot.values().forEach { slot ->
      val ids = items.filter { it.slot == slot }.map { it.recipe.id }
      assertEquals("Slot $slot should have 7 meals", 7, ids.size)
      assertEquals("Slot $slot should not repeat recipes", 7, ids.toSet().size)
    }
  }

  // 9. Different randomness yields different combinations.
  @Test
  fun generateMenu_isNotAlwaysTheSame() = runBlocking {
    repository.generateWeeklyMenu(Random(1))
    val first = persistedRecipeIds()

    repository.generateWeeklyMenu(Random(2))
    val second = persistedRecipeIds()

    assertNotEquals("Different seeds should produce different menus", first, second)
  }

  @Test
  fun repeatedGeneration_producesMoreThanOneCombination() = runBlocking {
    val menus = (1L..6L).map { seed ->
      repository.generateWeeklyMenu(Random(seed))
      persistedRecipeIds()
    }

    assertTrue("Expected at least two distinct menus", menus.toSet().size > 1)
  }

  // 6 & 8. A small / partially-covered catalog is handled explicitly.
  @Test
  fun generateMenu_withSmallCatalog_stillFillsEverySlot() = runBlocking {
    val small = repository.getRecipes().take(3)
    val smallIds = small.map { it.id }.toSet()
    repository.updateRecipeCatalog(small)

    val result = repository.generateWeeklyMenu(Random(7))

    assertTrue(result is WeeklyMenuResult.Success)
    val ids = persistedRecipeIds()
    assertEquals(28, ids.size)
    assertTrue("Only real recipes may be used", ids.all { it in smallIds })
    // Repetitions are unavoidable with 3 recipes for 28 slots, so all are used.
    assertEquals(3, ids.toSet().size)
  }

  @Test
  fun generateMenu_withSingleSlotCategories_fallsBackInsteadOfLeavingGaps() = runBlocking {
    // Only breakfast recipes exist: lunch/dinner/snack pools are empty and must
    // fall back to the catalog rather than producing empty meals.
    val breakfastsOnly = repository.getRecipes().filter { it.matchesCategory(RecipeCategory.BREAKFAST) }
    val breakfastIds = breakfastsOnly.map { it.id }.toSet()
    repository.updateRecipeCatalog(breakfastsOnly)

    val result = repository.generateWeeklyMenu(Random(5))

    assertTrue(result is WeeklyMenuResult.Success)
    val ids = persistedRecipeIds()
    assertEquals(28, ids.size)
    assertTrue(ids.all { it in breakfastIds })
  }

  // 7 & 8. An empty catalog reports a clear result and does not wipe the plan.
  @Test
  fun generateMenu_withEmptyCatalog_reportsNoRecipesAndKeepsExistingPlan() = runBlocking {
    repository.generateWeeklyMenu(Random(1))
    val before = persistedRecipeIds()
    assertEquals(28, before.size)

    repository.updateRecipeCatalog(emptyList())
    val result = repository.generateWeeklyMenu(Random(2))

    assertEquals(WeeklyMenuResult.NoRecipesAvailable, result)
    assertEquals("An empty generation must not clear the stored plan", before, persistedRecipeIds())
  }

  // 11. The generated menu integrates with the existing planner storage.
  @Test
  fun generatedMenu_persistsAcrossRepositoryRecreation() = runBlocking {
    repository.generateWeeklyMenu(Random(3))
    val count = persistedRecipeIds().size

    val recreated = RecipeRepository(db.dao(), context)

    assertEquals(count, recreated.mealPlanItems.first().size)
  }

  // 12. Generating a menu must not touch favourites or collections.
  @Test
  fun generateMenu_doesNotAffectFavoritesOrCollections() = runBlocking {
    val recipeId = repository.getRecipes().first().id
    repository.toggleFavorite(recipeId, isCurrentlyFavorite = false)
    val collectionId =
      ((repository.createCollection("Keep me")) as com.example.data.repository.CreateCollectionResult.Success)
        .collection
        .id
    repository.addRecipeToCollection(collectionId, recipeId)

    repository.generateWeeklyMenu(Random(4))

    assertEquals(setOf(recipeId), repository.favoriteRecipeIds.first())
    val collection = repository.collections.first().single()
    assertEquals(setOf(recipeId), collection.recipeIds)
  }
}
