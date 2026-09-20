package com.example.data.local

import android.content.Context
import com.app.diabeticos.R
import com.example.data.model.Difficulty
import com.example.data.model.GroceryCategory
import com.example.data.model.Ingredient
import com.example.data.model.NutritionInfo
import com.example.data.model.Recipe
import com.example.data.model.RecipeCategory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object RecipeJsonLoader {

  private val jsonFiles = listOf(
    "25-Desayunos.json",
    "25-Almuerzos.json",
    "25-Cenas.json",
    "25-Snacks.json",
    "25-Postres.json",
    "25-Ensaladas.json",
    "25-Sopas.json",
    "25-Bajas en carbohidratos.json"
  )

  private val categoryImageMap = mapOf(
    "Desayunos" to R.drawable.img_recipe_parfait,
    "Almuerzos" to R.drawable.img_recipe_med_bowl,
    "Cenas" to R.drawable.img_recipe_salmon,
    "Snacks" to R.drawable.img_recipe_parfait,
    "Postres" to R.drawable.img_recipe_parfait,
    "Ensaladas" to R.drawable.img_recipe_med_bowl,
    "Sopas" to R.drawable.img_recipe_soup,
    "Bajas en carbohidratos" to R.drawable.img_recipe_salmon
  )

  private val categoryEnumMap = mapOf(
    "Desayunos" to RecipeCategory.BREAKFAST,
    "Almuerzos" to RecipeCategory.LUNCH,
    "Cenas" to RecipeCategory.DINNER,
    "Snacks" to RecipeCategory.SNACKS,
    "Postres" to RecipeCategory.DESSERTS,
    "Ensaladas" to RecipeCategory.SALADS,
    "Sopas" to RecipeCategory.SOUPS,
    "Bajas en carbohidratos" to RecipeCategory.LOW_CARB
  )

  private val difficultyMap = mapOf(
    "Fácil" to Difficulty.EASY,
    "Facil" to Difficulty.EASY,
    "Media" to Difficulty.MEDIUM,
    "Avanzada" to Difficulty.HARD
  )

  fun loadRecipes(context: Context): List<Recipe> {
    val moshi = Moshi.Builder()
      .addLast(KotlinJsonAdapterFactory())
      .build()

    val adapter = moshi.adapter(RecipeJsonRoot::class.java)

    val allRecipes = mutableListOf<Recipe>()

    for (fileName in jsonFiles) {
      try {
        val json = context.assets.open(fileName).bufferedReader().use { it.readText() }
        val root = adapter.fromJson(json) ?: continue
        val recipes = root.recipes.mapNotNull { jsonRecipe ->
          mapToRecipe(jsonRecipe)
        }
        allRecipes.addAll(recipes)
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }

    return allRecipes
  }

  private fun mapToRecipe(json: RecipeJson): Recipe? {
    val categoryEnum = categoryEnumMap[json.category] ?: return null
    val imageRes = categoryImageMap[json.category] ?: R.drawable.img_recipe_parfait
    val difficulty = difficultyMap[json.difficulty] ?: Difficulty.EASY

    val tags = buildTags(json)

    return Recipe(
      id = "recipe_${json.id}",
      titleEs = json.name,
      titleEn = json.name,
      descriptionEs = json.description,
      descriptionEn = json.description,
      category = categoryEnum,
      secondaryCategories = emptyList(),
      prepTimeMinutes = json.prep_time_minutes,
      cookTimeMinutes = json.cook_time_minutes,
      difficulty = difficulty,
      defaultServings = json.servings,
      nutrition = NutritionInfo(
        calories = json.nutrition.calories_kcal,
        totalCarbsGrams = json.nutrition.carbohydrates_g,
        sugarsGrams = json.nutrition.sugars_g,
        proteinGrams = json.nutrition.protein_g,
        fatGrams = json.nutrition.total_fat_g,
        saturatedFatGrams = json.nutrition.saturated_fat_g,
        fiberGrams = json.nutrition.fiber_g,
        sodiumMg = json.nutrition.sodium_mg
      ),
      ingredients = json.ingredients.map { ing ->
        Ingredient(
          nameEs = ing.name,
          nameEn = ing.name,
          baseAmount = ing.quantity,
          unit = mapUnit(ing.unit),
          category = GroceryCategory.OTHER
        )
      },
      stepsEs = json.instructions,
      stepsEn = json.instructions,
      tags = tags,
      imageRes = imageRes,
      isFeatured = json.id == 1,
      isPopular = json.id % 5 == 0,
      isNew = json.id % 7 == 0
    )
  }

  private fun buildTags(json: RecipeJson): List<String> {
    val tags = mutableListOf<String>()
    tags.add(json.category.lowercase())

    if (json.nutrition.carbohydrates_g < 15) tags.add("low_carb")
    if (json.nutrition.calories_kcal < 300) tags.add("bajas_en_carbohidratos")
    if (json.difficulty == "Fácil") tags.add("faciles")
    if (json.total_time_minutes <= 20) tags.add("rapidas")

    return tags
  }

  private fun mapUnit(unit: String): String {
    return when (unit.lowercase()) {
      "unidades", "unidad" -> "unidades"
      "g" -> "g"
      "ml" -> "ml"
      "pizca" -> "pizca"
      "cucharadas" -> "cucharadas"
      "cucharaditas" -> "cucharaditas"
      else -> unit
    }
  }
}
