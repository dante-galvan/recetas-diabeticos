package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.RecipeJsonLoader
import com.example.data.model.Difficulty
import com.example.data.model.NutritionInfo
import com.example.data.model.Recipe
import com.example.data.model.RecipeCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Covers the rule used by the Explore section: a category is visible only when at
 * least one real recipe is associated with it (either as primary or secondary).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AvailableCategoriesTest {

  private val context: Context = ApplicationProvider.getApplicationContext()

  @Test
  fun realData_onlyCategoriesWithRecipesAreAvailable() {
    val recipes = RecipeJsonLoader.loadRecipes(context)

    val expected = listOf(
      RecipeCategory.BREAKFAST,
      RecipeCategory.LUNCH,
      RecipeCategory.DINNER,
      RecipeCategory.SNACKS,
      RecipeCategory.DESSERTS,
      RecipeCategory.SALADS,
      RecipeCategory.SOUPS,
      RecipeCategory.LOW_CARB
    )

    assertEquals(expected, RecipeCategory.withRecipes(recipes))
  }

  @Test
  fun realData_emptyCategoriesFromTheBugReportAreHidden() {
    val available = RecipeCategory.withRecipes(RecipeJsonLoader.loadRecipes(context))

    val previouslyShownButEmpty = listOf(
      RecipeCategory.DRINKS,
      RecipeCategory.BEEF,
      RecipeCategory.CHICKEN,
      RecipeCategory.FISH,
      RecipeCategory.SEAFOOD,
      RecipeCategory.VEGETARIAN,
      RecipeCategory.VEGAN,
      RecipeCategory.BAKED_GOODS,
      RecipeCategory.QUICK_MEALS,
      RecipeCategory.GLUTEN_FREE,
      RecipeCategory.LACTOSE_FREE,
      RecipeCategory.EASY,
      RecipeCategory.FAST
    )

    previouslyShownButEmpty.forEach { category ->
      assertFalse("$category should be hidden (zero recipes)", category in available)
    }
  }

  @Test
  fun realData_everyVisibleCategoryHasAtLeastOneRecipe_andHiddenOnesHaveNone() {
    val recipes = RecipeJsonLoader.loadRecipes(context)
    val available = RecipeCategory.withRecipes(recipes)

    for (category in RecipeCategory.values()) {
      val matches = recipes.filter { it.matchesCategory(category) }
      if (category in available) {
        assertTrue("$category is visible but has no recipes", matches.isNotEmpty())
      } else {
        assertTrue("$category is hidden but has ${matches.size} recipes", matches.isEmpty())
      }
    }
  }

  @Test
  fun categoryWithMultipleRecipes_isAvailable() {
    val recipes = RecipeJsonLoader.loadRecipes(context)
    val salads = recipes.filter { it.matchesCategory(RecipeCategory.SALADS) }

    assertTrue("Expected several salad recipes in the real data", salads.size > 1)
    assertTrue(RecipeCategory.SALADS in RecipeCategory.withRecipes(recipes))
  }

  @Test
  fun categoryWithZeroRecipes_isHidden() {
    val recipes = listOf(recipe("only-breakfast", RecipeCategory.BREAKFAST))

    val available = RecipeCategory.withRecipes(recipes)

    assertEquals(listOf(RecipeCategory.BREAKFAST), available)
    assertFalse(RecipeCategory.CHICKEN in available)
  }

  @Test
  fun categoryWithExactlyOneRecipe_isAvailable() {
    val recipes = listOf(recipe("single-soup", RecipeCategory.SOUPS))

    assertEquals(listOf(RecipeCategory.SOUPS), RecipeCategory.withRecipes(recipes))
  }

  @Test
  fun categoryReappearsWhenAValidRecipeIsAssociated() {
    val breakfast = recipe("breakfast", RecipeCategory.BREAKFAST)
    assertFalse(RecipeCategory.SOUPS in RecipeCategory.withRecipes(listOf(breakfast)))

    val withSoup = listOf(breakfast, recipe("new-soup", RecipeCategory.SOUPS))

    assertTrue(RecipeCategory.SOUPS in RecipeCategory.withRecipes(withSoup))
  }

  @Test
  fun secondaryCategoriesAreCountedAsAssociations() {
    val recipes = listOf(
      recipe(
        id = "lunch-with-secondary",
        primary = RecipeCategory.LUNCH,
        secondary = listOf(RecipeCategory.CHICKEN, RecipeCategory.SALADS)
      )
    )

    val available = RecipeCategory.withRecipes(recipes)

    assertEquals(
      listOf(RecipeCategory.LUNCH, RecipeCategory.SALADS, RecipeCategory.CHICKEN),
      available
    )
  }

  @Test
  fun emptyRecipeListProducesNoCategories() {
    assertEquals(emptyList<RecipeCategory>(), RecipeCategory.withRecipes(emptyList()))
  }

  private fun recipe(
    id: String,
    primary: RecipeCategory,
    secondary: List<RecipeCategory> = emptyList()
  ) = Recipe(
    id = id,
    titleEs = "Receta $id",
    titleEn = "Recipe $id",
    descriptionEs = "Descripción",
    descriptionEn = "Description",
    category = primary,
    secondaryCategories = secondary,
    prepTimeMinutes = 5,
    cookTimeMinutes = 5,
    difficulty = Difficulty.EASY,
    defaultServings = 2,
    nutrition = NutritionInfo(
      calories = 100,
      totalCarbsGrams = 10.0,
      sugarsGrams = 2.0,
      proteinGrams = 5.0,
      fatGrams = 3.0,
      saturatedFatGrams = 1.0,
      fiberGrams = 2.0,
      sodiumMg = 100.0
    ),
    ingredients = emptyList(),
    stepsEs = listOf("Paso"),
    stepsEn = listOf("Step"),
    tags = emptyList(),
    imageRes = 0
  )
}
