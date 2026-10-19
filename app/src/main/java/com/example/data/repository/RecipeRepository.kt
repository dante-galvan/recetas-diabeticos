package com.example.data.repository

import android.content.Context
import com.example.data.local.CollectionEntity
import com.example.data.local.DiaRecipesDao
import com.example.data.local.FavoriteEntity
import com.example.data.local.MealPlanEntity
import com.example.data.local.RecipeJsonLoader
import com.example.data.local.UserProfileEntity
import com.example.data.model.DefaultCollections
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
import java.util.UUID
import kotlin.random.Random

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
  // Only real, persisted collections are exposed. (The previous hardcoded
  // "starter collections" referenced SampleData ids that do not exist in the
  // JSON catalog, so they always resolved to zero recipes.)
  val collections: Flow<List<RecipeCollection>> = dao.getAllCollections().map { list ->
    list.map { it.toModel() }
  }

  /** Recipes resolved from the catalog, in catalog order. Missing ids are skipped. */
  fun getRecipesByIds(ids: Collection<String>): List<Recipe> =
    recipeList.filter { it.id in ids }

  /**
   * Creates the built-in collections that ship with the app when they are
   * missing. Designed to run once on every launch:
   *
   * - **Idempotent**: a default is matched by its stable [DefaultCollection.id],
   *   so running this again never creates a duplicate.
   * - **Non-destructive**: user collections and their saved recipes are never
   *   touched, and a default is skipped when the user already owns a collection
   *   with the same (Spanish or English) name, to avoid redundant entries.
   * - **Empty on purpose**: defaults start with no recipes. Recipes are never
   *   invented or auto-assigned; the user adds them through the save action.
   *
   * @return true when at least one default collection had to be created.
   */
  suspend fun ensureDefaultCollections(): Boolean {
    return try {
      val existing = dao.getCollectionsOnce()
      val existingIds = existing.mapTo(HashSet()) { it.id }
      val existingNames = HashSet<String>()
      for (collection in existing) {
        existingNames.add(collection.nameEs.trim().lowercase(Locale.ROOT))
        existingNames.add(collection.nameEn.trim().lowercase(Locale.ROOT))
      }

      var created = false
      for (default in DefaultCollections.all) {
        if (default.id in existingIds) continue
        if (existingNames.contains(default.nameEs.lowercase(Locale.ROOT)) ||
          existingNames.contains(default.nameEn.lowercase(Locale.ROOT))
        ) {
          continue
        }
        dao.insertCollection(
          CollectionEntity(
            id = default.id,
            nameEs = default.nameEs,
            nameEn = default.nameEn,
            icon = default.icon,
            recipeIdsCsv = ""
          )
        )
        created = true
      }
      created
    } catch (e: Exception) {
      e.printStackTrace()
      false
    }
  }

  suspend fun createCollection(name: String): CreateCollectionResult {
    val trimmed = name.trim()
    if (trimmed.isBlank()) return CreateCollectionResult.BlankName
    return try {
      val duplicate = dao.getCollectionsOnce().any { it.matchesName(trimmed) }
      if (duplicate) {
        CreateCollectionResult.DuplicateName
      } else {
        val entity = CollectionEntity(
          id = "col_${UUID.randomUUID()}",
          nameEs = trimmed,
          nameEn = trimmed,
          icon = "folder",
          recipeIdsCsv = ""
        )
        dao.insertCollection(entity)
        CreateCollectionResult.Success(entity.toModel())
      }
    } catch (e: Exception) {
      e.printStackTrace()
      CreateCollectionResult.Failure
    }
  }

  /**
   * Adds a recipe to a collection. Idempotent: saving the same recipe twice does
   * not create a duplicate entry. Returns [CollectionActionResult.NotFound] when
   * the collection or the recipe does not exist.
   */
  suspend fun addRecipeToCollection(collectionId: String, recipeId: String): CollectionActionResult {
    return try {
      val entity = dao.getCollectionById(collectionId) ?: return CollectionActionResult.NotFound
      if (getRecipeById(recipeId) == null) return CollectionActionResult.NotFound
      val ids = entity.recipeIdSet()
      if (recipeId in ids) return CollectionActionResult.Success
      dao.insertCollection(entity.copy(recipeIdsCsv = (ids + recipeId).joinToString(",")))
      CollectionActionResult.Success
    } catch (e: Exception) {
      e.printStackTrace()
      CollectionActionResult.Failure
    }
  }

  suspend fun removeRecipeFromCollection(collectionId: String, recipeId: String): CollectionActionResult {
    return try {
      val entity = dao.getCollectionById(collectionId) ?: return CollectionActionResult.NotFound
      val ids = entity.recipeIdSet()
      dao.insertCollection(entity.copy(recipeIdsCsv = (ids - recipeId).joinToString(",")))
      CollectionActionResult.Success
    } catch (e: Exception) {
      e.printStackTrace()
      CollectionActionResult.Failure
    }
  }

  suspend fun deleteCollection(collectionId: String): CollectionActionResult {
    return try {
      dao.deleteCollection(collectionId)
      CollectionActionResult.Success
    } catch (e: Exception) {
      e.printStackTrace()
      CollectionActionResult.Failure
    }
  }

  private fun CollectionEntity.matchesName(name: String): Boolean =
    nameEs.equals(name, ignoreCase = true) || nameEn.equals(name, ignoreCase = true)

  private fun CollectionEntity.recipeIdSet(): Set<String> =
    if (recipeIdsCsv.isBlank()) {
      emptySet()
    } else {
      recipeIdsCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()
    }

  private fun CollectionEntity.toModel(): RecipeCollection = RecipeCollection(
    id = id,
    nameEs = nameEs,
    nameEn = nameEn,
    icon = icon,
    recipeIds = recipeIdSet()
  )

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

  /**
   * Builds a fresh, randomised seven-day menu from the real catalog and replaces
   * the persisted plan. Recipes are drawn per meal slot so that, whenever a slot
   * has enough recipes, none is repeated within the week. When a slot has too few
   * recipes (e.g. only breakfasts exist), the whole catalog is used as a fallback
   * and repeats are allowed on purpose.
   *
   * The plan is only replaced after it has been fully built, and never when the
   * catalog is empty, so a failed/empty generation cannot wipe an existing plan.
   *
   * @param random injectable source of randomness so tests are reproducible.
   */
  suspend fun generateWeeklyMenu(random: Random = Random.Default): WeeklyMenuResult {
    val catalog = recipeList
    if (catalog.isEmpty()) return WeeklyMenuResult.NoRecipesAvailable

    return try {
      val picksBySlot = MealSlot.values().associateWith { slot ->
        pickForWeek(poolForSlot(catalog, slot), DAYS_PER_WEEK, random)
      }

      val items = buildList {
        for (day in 1..DAYS_PER_WEEK) {
          for (slot in MealSlot.values()) {
            val recipe = picksBySlot.getValue(slot)[day - 1]
            add(MealPlanEntity(dayOfWeek = day, slotKey = slot.key, recipeId = recipe.id))
          }
        }
      }

      dao.clearMealPlan()
      items.forEach { dao.insertMealPlanItem(it) }
      WeeklyMenuResult.Success(items.size)
    } catch (e: Exception) {
      e.printStackTrace()
      WeeklyMenuResult.Failure
    }
  }

  private fun poolForSlot(catalog: List<Recipe>, slot: MealSlot): List<Recipe> {
    val matches = when (slot) {
      MealSlot.BREAKFAST -> catalog.filter { it.matchesCategory(RecipeCategory.BREAKFAST) }
      MealSlot.LUNCH -> catalog.filter { it.matchesCategory(RecipeCategory.LUNCH) }
      MealSlot.DINNER -> catalog.filter { it.matchesCategory(RecipeCategory.DINNER) }
      MealSlot.SNACK ->
        catalog.filter { it.matchesCategory(RecipeCategory.SNACKS) || it.matchesCategory(RecipeCategory.DESSERTS) }
    }
    // Fall back to the full catalog so a slot is never left empty.
    return matches.ifEmpty { catalog }
  }

  /**
   * Returns [count] recipes from [pool]. The pool order is shuffled; once every
   * recipe has been used it is reshuffled for the next round, so repetitions only
   * happen when the pool is smaller than [count].
   */
  private fun pickForWeek(pool: List<Recipe>, count: Int, random: Random): List<Recipe> {
    if (pool.isEmpty()) return emptyList()
    val picks = ArrayList<Recipe>(count)
    var bag = pool.shuffled(random).toMutableList()
    while (picks.size < count) {
      if (bag.isEmpty()) bag = pool.shuffled(random).toMutableList()
      picks.add(bag.removeAt(0))
    }
    return picks
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

/** Number of days a generated weekly menu covers (Monday..Sunday). */
const val DAYS_PER_WEEK = 7

/** Result of creating a personal collection. */
sealed interface CreateCollectionResult {
  data class Success(val collection: RecipeCollection) : CreateCollectionResult
  data object BlankName : CreateCollectionResult
  data object DuplicateName : CreateCollectionResult
  data object Failure : CreateCollectionResult
}

/** Result of mutating a personal collection (add/remove recipe, delete). */
sealed interface CollectionActionResult {
  data object Success : CollectionActionResult
  data object NotFound : CollectionActionResult
  data object Failure : CollectionActionResult
}

/** Result of generating a weekly menu. */
sealed interface WeeklyMenuResult {
  data class Success(val mealCount: Int) : WeeklyMenuResult
  data object NoRecipesAvailable : WeeklyMenuResult
  data object Failure : WeeklyMenuResult
}

enum class SortOption {
  RELEVANCE,
  LOWEST_CARBS,
  LOWEST_CALORIES,
  QUICKEST,
  HIGHEST_PROTEIN
}
