package com.example.data.repository

import com.example.data.local.CollectionEntity
import com.example.data.local.DiaRecipesDao
import com.example.data.local.FavoriteEntity
import com.example.data.local.MealPlanEntity
import com.example.data.local.SampleData
import com.example.data.local.UserProfileEntity
import com.example.data.model.Difficulty
import com.example.data.model.MealPlanItem
import com.example.data.model.MealSlot
import com.example.data.model.Recipe
import com.example.data.model.RecipeCategory
import com.example.data.model.RecipeCollection
import com.example.data.model.UserProfile
import com.example.ui.i18n.Language
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.util.Locale

class RecipeRepository(private val dao: DiaRecipesDao) {

  // All catalog recipes
  private val allRecipes: List<Recipe> = SampleData.recipes

  fun getRecipes(): List<Recipe> = allRecipes

  fun getRecipeById(id: String): Recipe? = allRecipes.find { it.id == id }

  // Filtered & Searched Recipes
  fun searchRecipes(
    query: String,
    category: RecipeCategory?,
    maxCarbs: Double?,
    maxCalories: Int?,
    maxPrepTime: Int?,
    difficulty: Difficulty?,
    dietaryPreference: String?,
    sortBy: SortOption,
    language: Language
  ): List<Recipe> {
    var result = allRecipes.asSequence()

    if (query.isNotBlank()) {
      val cleanQuery = query.trim().lowercase(Locale.ROOT)
      result = result.filter { recipe ->
        recipe.titleEs.lowercase(Locale.ROOT).contains(cleanQuery) ||
          recipe.titleEn.lowercase(Locale.ROOT).contains(cleanQuery) ||
          recipe.descriptionEs.lowercase(Locale.ROOT).contains(cleanQuery) ||
          recipe.descriptionEn.lowercase(Locale.ROOT).contains(cleanQuery) ||
          recipe.category.nameEs.lowercase(Locale.ROOT).contains(cleanQuery) ||
          recipe.category.nameEn.lowercase(Locale.ROOT).contains(cleanQuery) ||
          recipe.ingredients.any { 
            it.nameEs.lowercase(Locale.ROOT).contains(cleanQuery) || 
            it.nameEn.lowercase(Locale.ROOT).contains(cleanQuery) 
          } ||
          recipe.tags.any { it.lowercase(Locale.ROOT).contains(cleanQuery) }
      }
    }

    if (category != null) {
      result = result.filter { 
        it.category == category || it.secondaryCategories.contains(category) 
      }
    }

    if (maxCarbs != null && maxCarbs > 0) {
      result = result.filter { it.nutrition.totalCarbsGrams <= maxCarbs }
    }

    if (maxCalories != null && maxCalories > 0) {
      result = result.filter { it.nutrition.calories <= maxCalories }
    }

    if (maxPrepTime != null && maxPrepTime > 0) {
      result = result.filter { it.totalTimeMinutes <= maxPrepTime }
    }

    if (difficulty != null) {
      result = result.filter { it.difficulty == difficulty }
    }

    if (!dietaryPreference.isNullOrBlank()) {
      result = result.filter { recipe ->
        recipe.tags.any { it.equals(dietaryPreference, ignoreCase = true) } ||
          recipe.secondaryCategories.any { 
            it.nameEs.equals(dietaryPreference, ignoreCase = true) || 
            it.nameEn.equals(dietaryPreference, ignoreCase = true) 
          }
      }
    }

    val filtered = result.toList()
    return when (sortBy) {
      SortOption.RELEVANCE -> filtered
      SortOption.LOWEST_CARBS -> filtered.sortedBy { it.nutrition.totalCarbsGrams }
      SortOption.LOWEST_CALORIES -> filtered.sortedBy { it.nutrition.calories }
      SortOption.QUICKEST -> filtered.sortedBy { it.totalTimeMinutes }
      SortOption.HIGHEST_PROTEIN -> filtered.sortedByDescending { it.nutrition.proteinGrams }
    }
  }

  // Favorites
  val favoriteRecipeIds: Flow<Set<String>> = dao.getAllFavorites().map { list ->
    list.map { it.recipeId }.toSet()
  }

  suspend fun toggleFavorite(recipeId: String, isCurrentlyFavorite: Boolean) {
    if (isCurrentlyFavorite) {
      dao.deleteFavorite(recipeId)
    } else {
      dao.insertFavorite(FavoriteEntity(recipeId = recipeId))
    }
  }

  // Collections
  val collections: Flow<List<RecipeCollection>> = dao.getAllCollections().map { list ->
    if (list.isEmpty()) {
      // Return default starter collections
      listOf(
        RecipeCollection("col_breakfast", "Mis Desayunos", "My Breakfasts", "free_breakfast", setOf("greek_yogurt_berry_parfait", "spinach_feta_omelet")),
        RecipeCollection("col_quick_dinners", "Cenas Rápidas", "Quick Dinners", "dinner_dining", setOf("salmon_asparagus_quinoa", "turkey_lettuce_tacos")),
        RecipeCollection("col_weekend", "Fin de Semana", "Weekend Special", "celebration", setOf("rustic_lentil_vegetable_stew"))
      )
    } else {
      list.map { entity ->
        RecipeCollection(
          id = entity.id,
          nameEs = entity.nameEs,
          nameEn = entity.nameEn,
          icon = entity.icon,
          recipeIds = if (entity.recipeIdsCsv.isBlank()) emptySet() else entity.recipeIdsCsv.split(",").toSet()
        )
      }
    }
  }

  suspend fun createCollection(name: String) {
    val id = "col_${System.currentTimeMillis()}"
    dao.insertCollection(
      CollectionEntity(
        id = id,
        nameEs = name,
        nameEn = name,
        icon = "folder",
        recipeIdsCsv = ""
      )
    )
  }

  suspend fun addRecipeToCollection(collectionId: String, recipeId: String, currentCollection: RecipeCollection) {
    val updatedIds = (currentCollection.recipeIds + recipeId).joinToString(",")
    dao.insertCollection(
      CollectionEntity(
        id = collectionId,
        nameEs = currentCollection.nameEs,
        nameEn = currentCollection.nameEn,
        icon = currentCollection.icon,
        recipeIdsCsv = updatedIds
      )
    )
  }

  // Meal Planner
  val mealPlanItems: Flow<List<MealPlanItem>> = dao.getAllMealPlanItems().map { list ->
    list.mapNotNull { entity ->
      val recipe = getRecipeById(entity.recipeId)
      val slot = MealSlot.values().find { it.key == entity.slotKey } ?: MealSlot.LUNCH
      if (recipe != null) {
        MealPlanItem(
          id = entity.id,
          dayOfWeek = entity.dayOfWeek,
          slot = slot,
          recipe = recipe
        )
      } else null
    }
  }

  suspend fun setMealPlanSlot(dayOfWeek: Int, slot: MealSlot, recipeId: String) {
    dao.deleteMealPlanSlot(dayOfWeek, slot.key)
    dao.insertMealPlanItem(
      MealPlanEntity(
        dayOfWeek = dayOfWeek,
        slotKey = slot.key,
        recipeId = recipeId
      )
    )
  }

  suspend fun removeMealPlanItem(id: Long) {
    dao.deleteMealPlanItemById(id)
  }

  suspend fun generateWeeklyMenu() {
    dao.clearMealPlan()
    val breakfasts = allRecipes.filter { it.category == RecipeCategory.BREAKFAST || it.secondaryCategories.contains(RecipeCategory.BREAKFAST) }
    val lunches = allRecipes.filter { it.category == RecipeCategory.LUNCH || it.secondaryCategories.contains(RecipeCategory.LUNCH) }
    val dinners = allRecipes.filter { it.category == RecipeCategory.DINNER || it.secondaryCategories.contains(RecipeCategory.DINNER) }
    val snacks = allRecipes.filter { it.category == RecipeCategory.SNACKS || it.category == RecipeCategory.DESSERTS }

    for (day in 1..7) {
      val b = breakfasts.getOrNull((day - 1) % breakfasts.size) ?: allRecipes.first()
      val l = lunches.getOrNull((day - 1) % lunches.size) ?: allRecipes[1 % allRecipes.size]
      val d = dinners.getOrNull((day - 1) % dinners.size) ?: allRecipes[2 % allRecipes.size]
      val s = snacks.getOrNull((day - 1) % snacks.size) ?: allRecipes[3 % allRecipes.size]

      dao.insertMealPlanItem(MealPlanEntity(dayOfWeek = day, slotKey = MealSlot.BREAKFAST.key, recipeId = b.id))
      dao.insertMealPlanItem(MealPlanEntity(dayOfWeek = day, slotKey = MealSlot.LUNCH.key, recipeId = l.id))
      dao.insertMealPlanItem(MealPlanEntity(dayOfWeek = day, slotKey = MealSlot.DINNER.key, recipeId = d.id))
      dao.insertMealPlanItem(MealPlanEntity(dayOfWeek = day, slotKey = MealSlot.SNACK.key, recipeId = s.id))
    }
  }

  // User Profile
  val userProfile: Flow<UserProfile> = dao.getUserProfile().map { entity ->
    if (entity == null) {
      UserProfile()
    } else {
      UserProfile(
        dietaryPreferences = if (entity.dietaryPrefsCsv.isBlank()) emptySet() else entity.dietaryPrefsCsv.split(",").toSet(),
        allergies = if (entity.allergiesCsv.isBlank()) emptySet() else entity.allergiesCsv.split(",").toSet(),
        language = Language.fromCode(entity.langCode),
        isDarkTheme = when (entity.isDarkTheme) {
          0 -> false
          1 -> true
          else -> null
        },
        mealRemindersEnabled = entity.mealReminders,
        isOnboardingCompleted = entity.onboardingDone,
        profilePhotoPath = entity.profilePhotoPath
      )
    }
  }

  suspend fun saveUserProfile(profile: UserProfile) {
    dao.saveUserProfile(
      UserProfileEntity(
        id = 1,
        dietaryPrefsCsv = profile.dietaryPreferences.joinToString(","),
        allergiesCsv = profile.allergies.joinToString(","),
        langCode = profile.language.code,
        isDarkTheme = when (profile.isDarkTheme) {
          false -> 0
          true -> 1
          null -> null
        },
        mealReminders = profile.mealRemindersEnabled,
        onboardingDone = profile.isOnboardingCompleted,
        profilePhotoPath = profile.profilePhotoPath
      )
    )
  }
}

enum class SortOption {
  RELEVANCE,
  LOWEST_CARBS,
  LOWEST_CALORIES,
  QUICKEST,
  HIGHEST_PROTEIN
}
