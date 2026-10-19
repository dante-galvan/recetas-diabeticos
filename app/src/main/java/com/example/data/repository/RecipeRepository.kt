package com.example.data.repository

import android.content.Context
import com.example.data.local.CollectionEntity
import com.example.data.local.DiaRecipesDao
import com.example.data.local.FavoriteEntity
import com.example.data.local.MealPlanEntity
import com.example.data.local.RecipeJsonLoader
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.util.Locale

class RecipeRepository(private val dao: DiaRecipesDao, context: Context? = null) {

  // Set to true only when the JSON catalog could not be read at all. Used to
  // avoid treating an I/O failure as "every category is empty".
  private var catalogLoadFailed: Boolean = false

  // All catalog recipes from JSON only (SampleData is for previews/tests only).
  // Backed by a StateFlow so derived data (like the visible categories) is
  // recalculated automatically whenever the recipe set changes.
  private val allRecipes: MutableStateFlow<List<Recipe>> =
    MutableStateFlow(loadJsonRecipes(context))

  private val recipeList: List<Recipe> get() = allRecipes.value

  /** Emits the full catalog, so observers can react to recipe changes. */
  val recipes: StateFlow<List<Recipe>> = allRecipes.asStateFlow()

  /**
   * Emits the categories that currently have at least one recipe. Recomputed on
   * every catalog change, so categories disappear/reappear without restarting.
   */
  val availableCategories: Flow<List<RecipeCategory>> = allRecipes
    .map { categoryListFor(it) }
    .distinctUntilChanged()

  private fun loadJsonRecipes(context: Context?): List<Recipe> {
    if (context == null) return emptyList()
    return try {
      RecipeJsonLoader.loadRecipes(context)
    } catch (e: Exception) {
      e.printStackTrace()
      catalogLoadFailed = true
      emptyList()
    }
  }

  /** Replaces the catalog (e.g. after a data refresh) and recomputes categories. */
  fun updateRecipeCatalog(recipes: List<Recipe>) {
    catalogLoadFailed = false
    allRecipes.value = recipes
  }

  fun getRecipes(): List<Recipe> = recipeList

  fun getRecipeById(id: String): Recipe? = recipeList.find { it.id == id }

  /**
   * Categories that currently have at least one associated recipe, in the enum's
   * declaration order. Derived from the real recipes using the same association
   * rules as [searchRecipes], so a category is only offered when selecting it can
   * actually return results.
   */
  fun getAvailableCategories(): List<RecipeCategory> = categoryListFor(recipeList)

  private fun categoryListFor(recipes: List<Recipe>): List<RecipeCategory> {
    // A total load failure is not proof that the categories are empty: keep the
    // definitions visible instead of silently hiding every one of them.
    if (recipes.isEmpty() && catalogLoadFailed) return RecipeCategory.values().toList()
    return RecipeCategory.withRecipes(recipes)
  }

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
    var result = recipeList.asSequence()

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
      result = result.filter { it.matchesCategory(category) }
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
    val menuRecipes = recipeList
    val breakfasts = menuRecipes.filter { it.matchesCategory(RecipeCategory.BREAKFAST) }
    val lunches = menuRecipes.filter { it.matchesCategory(RecipeCategory.LUNCH) }
    val dinners = menuRecipes.filter { it.matchesCategory(RecipeCategory.DINNER) }
    val snacks = menuRecipes.filter {
      it.matchesCategory(RecipeCategory.SNACKS) || it.matchesCategory(RecipeCategory.DESSERTS)
    }

    if (menuRecipes.isEmpty()) return

    for (day in 1..7) {
      val b = breakfasts.getOrNull((day - 1) % breakfasts.size) ?: menuRecipes.first()
      val l = lunches.getOrNull((day - 1) % lunches.size) ?: menuRecipes[1 % menuRecipes.size]
      val d = dinners.getOrNull((day - 1) % dinners.size) ?: menuRecipes[2 % menuRecipes.size]
      val s = snacks.getOrNull((day - 1) % snacks.size) ?: menuRecipes[3 % menuRecipes.size]

      dao.insertMealPlanItem(MealPlanEntity(dayOfWeek = day, slotKey = MealSlot.BREAKFAST.key, recipeId = b.id))
      dao.insertMealPlanItem(MealPlanEntity(dayOfWeek = day, slotKey = MealSlot.LUNCH.key, recipeId = l.id))
      dao.insertMealPlanItem(MealPlanEntity(dayOfWeek = day, slotKey = MealSlot.DINNER.key, recipeId = d.id))
      dao.insertMealPlanItem(MealPlanEntity(dayOfWeek = day, slotKey = MealSlot.SNACK.key, recipeId = s.id))
    }
  }

  // User Profile (null when no profile row has been persisted yet)
  val userProfile: Flow<UserProfile?> = dao.getUserProfile().map { entity ->
    if (entity == null) {
      null
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
        isOnboardingCompleted = entity.onboardingDone
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
        onboardingDone = profile.isOnboardingCompleted
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
