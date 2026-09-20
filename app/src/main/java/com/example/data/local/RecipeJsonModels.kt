package com.example.data.local

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RecipeJsonRoot(
  val recipes: List<RecipeJson>
)

@JsonClass(generateAdapter = true)
data class RecipeJson(
  val id: Int,
  val category: String,
  val name: String,
  val description: String,
  val servings: Int,
  val prep_time_minutes: Int,
  val cook_time_minutes: Int,
  val total_time_minutes: Int,
  val difficulty: String,
  val nutrition: NutritionJson,
  val ingredients: List<IngredientJson>,
  val instructions: List<String>
)

@JsonClass(generateAdapter = true)
data class NutritionJson(
  val carbohydrates_g: Double,
  val calories_kcal: Int,
  val sugars_g: Double,
  val fiber_g: Double,
  val protein_g: Double,
  val total_fat_g: Double,
  val saturated_fat_g: Double,
  val sodium_mg: Double
)

@JsonClass(generateAdapter = true)
data class IngredientJson(
  val name: String,
  val quantity: Double,
  val unit: String
)
