package com.example.data.model

import com.example.ui.i18n.Language

data class Ingredient(
  val nameEs: String,
  val nameEn: String,
  val baseAmount: Double,
  val unit: String,
  val category: GroceryCategory
) {
  fun localizedName(lang: Language): String = if (lang == Language.SPANISH) nameEs else nameEn

  fun formattedAmount(servingsMultiplier: Double): String {
    val adjusted = baseAmount * servingsMultiplier
    return if (adjusted == adjusted.toLong().toDouble()) {
      "${adjusted.toLong()} $unit".trim()
    } else {
      String.format("%.1f %s", adjusted, unit).trim()
    }
  }
}

data class NutritionInfo(
  val calories: Int,
  val totalCarbsGrams: Double, // CRITICAL: explicit Total Carbs
  val sugarsGrams: Double,
  val proteinGrams: Double,
  val fatGrams: Double,
  val saturatedFatGrams: Double,
  val fiberGrams: Double,
  val sodiumMg: Double
)

data class Recipe(
  val id: String,
  val titleEs: String,
  val titleEn: String,
  val descriptionEs: String,
  val descriptionEn: String,
  val category: RecipeCategory,
  val secondaryCategories: List<RecipeCategory> = emptyList(),
  val prepTimeMinutes: Int,
  val cookTimeMinutes: Int,
  val difficulty: Difficulty,
  val defaultServings: Int = 2,
  val nutrition: NutritionInfo,
  val ingredients: List<Ingredient>,
  val stepsEs: List<String>,
  val stepsEn: List<String>,
  val tags: List<String>,
  val imageRes: Int,
  val isFeatured: Boolean = false,
  val isPopular: Boolean = false,
  val isNew: Boolean = false,
  val suitableForType1: Boolean = true,
  val suitableForType2: Boolean = true
) {
  val totalTimeMinutes: Int get() = prepTimeMinutes + cookTimeMinutes

  fun localizedTitle(lang: Language): String = if (lang == Language.SPANISH) titleEs else titleEn
  fun localizedDescription(lang: Language): String = if (lang == Language.SPANISH) descriptionEs else descriptionEn
  fun localizedSteps(lang: Language): List<String> = if (lang == Language.SPANISH) stepsEs else stepsEn
}

enum class MealSlot(val key: String, val nameEs: String, val nameEn: String) {
  BREAKFAST("breakfast", "Desayuno", "Breakfast"),
  LUNCH("lunch", "Almuerzo", "Lunch"),
  DINNER("dinner", "Cena", "Dinner"),
  SNACK("snack", "Snack", "Snack");

  fun localizedName(lang: Language): String = if (lang == Language.SPANISH) nameEs else nameEn
}

data class MealPlanItem(
  val id: Long = 0,
  val dayOfWeek: Int, // 1 = Monday ... 7 = Sunday
  val slot: MealSlot,
  val recipe: Recipe
)

data class ShoppingItem(
  val id: Long = 0,
  val nameEs: String,
  val nameEn: String,
  val amount: Double,
  val unit: String,
  val category: GroceryCategory,
  val isChecked: Boolean = false,
  val sourceRecipe: String = ""
) {
  fun localizedName(lang: Language): String = if (lang == Language.SPANISH) nameEs else nameEn

  fun formattedAmount(): String {
    if (amount <= 0.0) return ""
    return if (amount == amount.toLong().toDouble()) {
      "${amount.toLong()} $unit".trim()
    } else {
      String.format("%.1f %s", amount, unit).trim()
    }
  }
}

data class RecipeCollection(
  val id: String,
  val nameEs: String,
  val nameEn: String,
  val icon: String = "favorite",
  val recipeIds: Set<String> = emptySet()
) {
  fun localizedName(lang: Language): String = if (lang == Language.SPANISH) nameEs else nameEn
}

enum class DiabetesType(val nameEs: String, val nameEn: String) {
  TYPE_1("Diabetes Tipo 1", "Type 1 Diabetes"),
  TYPE_2("Diabetes Tipo 2", "Type 2 Diabetes"),
  GESTATIONAL("Diabetes Gestacional", "Gestational Diabetes"),
  NOT_SPECIFIED("No especificar", "Not specified");

  fun localizedName(lang: Language): String = if (lang == Language.SPANISH) nameEs else nameEn
}

data class UserProfile(
  val diabetesType: DiabetesType = DiabetesType.TYPE_2,
  val dietaryPreferences: Set<String> = setOf("Low Carb", "Bajas en carbohidratos"),
  val allergies: Set<String> = emptySet(),
  val language: Language = Language.SPANISH,
  val isDarkTheme: Boolean? = null, // null = system default
  val mealRemindersEnabled: Boolean = true,
  val isOnboardingCompleted: Boolean = true
)
