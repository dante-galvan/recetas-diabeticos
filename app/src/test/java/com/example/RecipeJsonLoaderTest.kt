package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.data.local.RecipeJsonLoader
import com.example.data.model.Ingredient
import com.example.ui.i18n.Language
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [36])
class RecipeJsonLoaderTest {

  private val context: Context = ApplicationProvider.getApplicationContext()

  @Test
  fun loadRecipes_loadsAll200Recipes() {
    val recipes = RecipeJsonLoader.loadRecipes(context)
    assertEquals(200, recipes.size)
  }

  @Test
  fun loadRecipes_englishTitlesAreTranslatedAndNonEmpty() {
    val recipes = RecipeJsonLoader.loadRecipes(context)
    for (r in recipes) {
      assertTrue("Empty titleEn for ${r.id}", r.titleEn.isNotBlank())
      assertTrue(
        "titleEn should differ from titleEs for ${r.id} (got same: '${r.titleEs}')",
        r.titleEn != r.titleEs
      )
    }
  }

  @Test
  fun loadRecipes_englishDescriptionsAreTranslatedAndNonEmpty() {
    val recipes = RecipeJsonLoader.loadRecipes(context)
    for (r in recipes) {
      assertTrue("Empty descriptionEn for ${r.id}", r.descriptionEn.isNotBlank())
      assertTrue(
        "descriptionEn should differ from descriptionEs for ${r.id}",
        r.descriptionEn != r.descriptionEs
      )
    }
  }

  @Test
  fun loadRecipes_stepsEnSameSizeAsStepsEs_andNonEmpty() {
    val recipes = RecipeJsonLoader.loadRecipes(context)
    for (r in recipes) {
      assertTrue("Empty stepsEs for ${r.id}", r.stepsEs.isNotEmpty())
      assertEquals(
        "stepsEn size mismatch for ${r.id}",
        r.stepsEs.size,
        r.stepsEn.size
      )
      for ((index, step) in r.stepsEn.withIndex()) {
        assertTrue("Empty step $index in ${r.id}", step.isNotBlank())
      }
    }
  }

  @Test
  fun loadRecipes_ingredientGlossaryApplied() {
    val recipes = RecipeJsonLoader.loadRecipes(context)
    val allIngredients: List<Ingredient> = recipes.flatMap { it.ingredients }
    assertTrue(allIngredients.isNotEmpty())
    val translated = allIngredients.count { it.nameEn != it.nameEs }
    assertTrue(
      "Expected most ingredients translated via glossary, got $translated/${allIngredients.size}",
      translated > allIngredients.size / 2
    )
  }

  @Test
  fun loadRecipes_ingredientUnitsHaveEnglishFallback() {
    val recipes = RecipeJsonLoader.loadRecipes(context)
    for (r in recipes) {
      for (ing in r.ingredients) {
        assertTrue("Empty unitEn for ${r.id}/${ing.nameEs}", ing.unitEn.isNotBlank())
      }
    }
  }

  @Test
  fun mapToRecipe_withoutTranslation_fallsBackToSpanish() {
    val json = com.example.data.local.RecipeJson(
      id = 999,
      category = "Sopas",
      name = "Sopa de prueba",
      description = "Descripción de prueba",
      servings = 2,
      prep_time_minutes = 5,
      cook_time_minutes = 10,
      total_time_minutes = 15,
      difficulty = "Fácil",
      nutrition = com.example.data.local.NutritionJson(
        carbohydrates_g = 10.0,
        calories_kcal = 200,
        sugars_g = 2.0,
        fiber_g = 3.0,
        protein_g = 15.0,
        total_fat_g = 8.0,
        saturated_fat_g = 2.0,
        sodium_mg = 300.0
      ),
      ingredients = listOf(
        com.example.data.local.IngredientJson("Cebolla", 1.0, "unidades")
      ),
      instructions = listOf("Paso uno.", "Paso dos.")
    )
    val recipe = RecipeJsonLoader.mapToRecipe(json, translation = null, glossary = emptyMap())
    assertNotNull(recipe)
    assertEquals("Sopa de prueba", recipe!!.titleEs)
    assertEquals(recipe.titleEs, recipe.titleEn)
    assertEquals(recipe.descriptionEs, recipe.descriptionEn)
    assertEquals(recipe.stepsEs, recipe.stepsEn)
  }

  @Test
  fun mapToRecipe_withTranslation_appliesEnglishFields() {
    val json = com.example.data.local.RecipeJson(
      id = 998,
      category = "Sopas",
      name = "Sopa de prueba",
      description = "Descripción de prueba",
      servings = 2,
      prep_time_minutes = 5,
      cook_time_minutes = 10,
      total_time_minutes = 15,
      difficulty = "Fácil",
      nutrition = com.example.data.local.NutritionJson(
        carbohydrates_g = 10.0,
        calories_kcal = 200,
        sugars_g = 2.0,
        fiber_g = 3.0,
        protein_g = 15.0,
        total_fat_g = 8.0,
        saturated_fat_g = 2.0,
        sodium_mg = 300.0
      ),
      ingredients = listOf(
        com.example.data.local.IngredientJson("Cebolla", 1.0, "unidades")
      ),
      instructions = listOf("Paso uno.", "Paso dos.")
    )
    val translation = RecipeJsonLoader.RecipeTranslation(
      id = 998,
      name = "Test Soup",
      description = "Test description",
      instructions = listOf("Step one.", "Step two.")
    )
    val glossary = mapOf("Cebolla" to "Onion")
    val recipe = RecipeJsonLoader.mapToRecipe(json, translation, glossary)
    assertNotNull(recipe)
    assertEquals("Test Soup", recipe!!.titleEn)
    assertEquals("Test description", recipe.descriptionEn)
    assertEquals(listOf("Step one.", "Step two."), recipe.stepsEn)
    assertEquals("Onion", recipe.ingredients.single().nameEn)
    assertEquals("units", recipe.ingredients.single().unitEn)
  }

  @Test
  fun mapToRecipe_translationWithMismatchedInstructionCount_fallsBackToSpanishSteps() {
    val json = com.example.data.local.RecipeJson(
      id = 997,
      category = "Sopas",
      name = "Sopa",
      description = "Desc",
      servings = 2,
      prep_time_minutes = 1,
      cook_time_minutes = 1,
      total_time_minutes = 2,
      difficulty = "Fácil",
      nutrition = com.example.data.local.NutritionJson(
        carbohydrates_g = 1.0,
        calories_kcal = 10,
        sugars_g = 0.0,
        fiber_g = 0.0,
        protein_g = 1.0,
        total_fat_g = 0.5,
        saturated_fat_g = 0.1,
        sodium_mg = 5.0
      ),
      ingredients = emptyList(),
      instructions = listOf("A", "B", "C")
    )
    val translation = RecipeJsonLoader.RecipeTranslation(
      id = 997,
      instructions = listOf("Only one step")
    )
    val recipe = RecipeJsonLoader.mapToRecipe(json, translation)
    assertNotNull(recipe)
    assertEquals(json.instructions, recipe!!.stepsEn)
  }

  @Test
  fun localizedAccessors_switchByLanguage() {
    val recipes = RecipeJsonLoader.loadRecipes(context)
    val sample = recipes.first()
    assertEquals(sample.titleEs, sample.localizedTitle(Language.SPANISH))
    assertEquals(sample.titleEn, sample.localizedTitle(Language.ENGLISH))
    assertEquals(sample.descriptionEs, sample.localizedDescription(Language.SPANISH))
    assertEquals(sample.descriptionEn, sample.localizedDescription(Language.ENGLISH))
    assertEquals(sample.stepsEs, sample.localizedSteps(Language.SPANISH))
    assertEquals(sample.stepsEn, sample.localizedSteps(Language.ENGLISH))
  }
}
