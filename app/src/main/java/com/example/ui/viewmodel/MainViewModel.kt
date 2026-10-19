package com.example.ui.viewmodel

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.app.diabeticos.R
import com.example.data.local.DiaRecipesDatabase
import com.example.data.model.Difficulty
import com.example.data.model.MealPlanItem
import com.example.data.model.MealSlot
import com.example.data.model.Recipe
import com.example.data.model.RecipeCategory
import com.example.data.model.RecipeCollection
import com.example.data.model.UserProfile
import com.example.data.repository.CollectionActionResult
import com.example.data.repository.CreateCollectionResult
import com.example.data.repository.RecipeRepository
import com.example.data.repository.SortOption
import com.example.data.repository.WeeklyMenuResult
import com.example.ui.i18n.Language
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

enum class AppScreen {
  HOME,
  EXPLORE,
  PLAN,
  FAVORITES,
  PROFILE,
  RECIPE_DETAIL,
  ONBOARDING
}

data class FilterState(
  val category: RecipeCategory? = null,
  val maxCarbs: Double? = null,
  val maxCalories: Int? = null,
  val maxPrepTime: Int? = null,
  val difficulty: Difficulty? = null,
  val dietaryPreference: String? = null,
  val sortBy: SortOption = SortOption.RELEVANCE
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

  private val database = Room.databaseBuilder(
    application,
    DiaRecipesDatabase::class.java,
    "dia_recipes_db"
  ).fallbackToDestructiveMigration().build()

  val repository = RecipeRepository(database.dao(), application)

  // Navigation & Screen
  private val _currentScreen = MutableStateFlow(AppScreen.HOME)
  val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

  // Navigation stack for back navigation
  private val _navigationStack = ArrayList<AppScreen>()

  // Selected Recipe Detail
  private val _selectedRecipe = MutableStateFlow<Recipe?>(null)
  val selectedRecipe: StateFlow<Recipe?> = _selectedRecipe.asStateFlow()

  private val _servingsMultiplier = MutableStateFlow(1.0)
  val servingsMultiplier: StateFlow<Double> = _servingsMultiplier.asStateFlow()

  // User Profile & Settings (null until Room emits the persisted profile).
  // SharingStarted.Eagerly so the real language is known as early as possible.
  val userProfile: StateFlow<UserProfile?> = repository.userProfile
    .stateIn(viewModelScope, SharingStarted.Eagerly, null)

  // Current Language
  val currentLanguage: StateFlow<Language> = userProfile
    .map { profile -> profile?.language ?: Language.ENGLISH }
    .stateIn(viewModelScope, SharingStarted.Eagerly, Language.ENGLISH)

  init {
    // Seed the default profile (language = ENGLISH) on first launch. Without a
    // persisted row, MainActivity's LaunchedEffect never receives a language
    // and the app silently stays in the system locale on fresh installs.
    viewModelScope.launch {
      if (repository.userProfile.first() == null) {
        repository.saveUserProfile(UserProfile())
      }
      // Make sure the built-in collections exist for this install. The call is
      // idempotent and non-destructive, so it is safe on every launch.
      repository.ensureDefaultCollections()
    }
  }

  // Search & Filters for Explore
  val searchQuery = MutableStateFlow("")
  val filterState = MutableStateFlow(FilterState())

  // Explore Recipes Result
  val exploreRecipes = combine(
    searchQuery,
    filterState,
    currentLanguage
  ) { query, filters, lang ->
    repository.searchRecipes(
      query = query,
      category = filters.category,
      maxCarbs = filters.maxCarbs,
      maxCalories = filters.maxCalories,
      maxPrepTime = filters.maxPrepTime,
      difficulty = filters.difficulty,
      dietaryPreference = filters.dietaryPreference,
      sortBy = filters.sortBy,
      language = lang
    )
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), repository.getRecipes())

  // Categories that currently have at least one recipe. Derived from the live
  // catalog so they are recalculated automatically whenever the recipe set
  // changes; a category is never offered while it would lead to an empty screen.
  val exploreCategories: StateFlow<List<RecipeCategory>> = repository.availableCategories
    .stateIn(viewModelScope, SharingStarted.Eagerly, repository.getAvailableCategories())

  // Favorites
  val favoriteRecipeIds: StateFlow<Set<String>> = repository.favoriteRecipeIds
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

  val favoriteRecipes = combine(
    favoriteRecipeIds,
    searchQuery
  ) { ids, query ->
    val all = repository.getRecipes().filter { ids.contains(it.id) }
    if (query.isBlank()) all else {
      all.filter { 
        it.titleEs.contains(query, ignoreCase = true) || 
        it.titleEn.contains(query, ignoreCase = true) 
      }
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Collections
  val collections: StateFlow<List<RecipeCollection>> = repository.collections
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val selectedCollectionId = MutableStateFlow<String?>(null)

  /** Every recipe id that is stored in at least one collection. */
  val savedRecipeIds: StateFlow<Set<String>> = collections
    .map { cols -> cols.flatMap { it.recipeIds }.toSet() }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

  /** Recipes of the currently selected collection (missing ids are skipped). */
  val selectedCollectionRecipes: StateFlow<List<Recipe>> = combine(
    collections,
    selectedCollectionId
  ) { cols, selectedId ->
    val ids = cols.find { it.id == selectedId }?.recipeIds ?: emptySet()
    repository.getRecipesByIds(ids)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  /** When non-null, the "save to collection" dialog is shown for that recipe. */
  val saveToCollectionFor = MutableStateFlow<Recipe?>(null)

  // Meal Planner
  val mealPlanItems: StateFlow<List<MealPlanItem>> = repository.mealPlanItems
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  private val initialDay = when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
    Calendar.MONDAY -> 1
    Calendar.TUESDAY -> 2
    Calendar.WEDNESDAY -> 3
    Calendar.THURSDAY -> 4
    Calendar.FRIDAY -> 5
    Calendar.SATURDAY -> 6
    Calendar.SUNDAY -> 7
    else -> 1
  }
  val selectedDayOfWeek = MutableStateFlow(initialDay)

  // UI Dialog States
  val showAddToPlanDialog = MutableStateFlow(false)
  val showNewCollectionDialog = MutableStateFlow(false)
  val showFilterSheet = MutableStateFlow(false)
  val showRecipePickerForPlanSlot = MutableStateFlow<Pair<Int, MealSlot>?>(null)

  // Snackbar Toast Events
  private val _toastMessage = MutableSharedFlow<String>()
  val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

  fun navigateTo(screen: AppScreen) {
    _navigationStack.add(_currentScreen.value)
    _currentScreen.value = screen
  }

  fun navigateToTopLevel(screen: AppScreen) {
    _navigationStack.clear()
    _currentScreen.value = screen
  }

  fun navigateBack() {
    if (_navigationStack.isNotEmpty()) {
      _currentScreen.value = _navigationStack.removeAt(_navigationStack.lastIndex)
    }
  }

  fun openRecipeDetail(recipe: Recipe) {
    _selectedRecipe.value = recipe
    _servingsMultiplier.value = 1.0
    navigateTo(AppScreen.RECIPE_DETAIL)
  }

  fun setServings(newServings: Int) {
    val recipe = _selectedRecipe.value ?: return
    val base = recipe.defaultServings
    if (newServings in 1..12) {
      _servingsMultiplier.value = newServings.toDouble() / base
    }
  }

  fun toggleFavorite(recipeId: String) {
    viewModelScope.launch {
      val isFav = favoriteRecipeIds.value.contains(recipeId)
      repository.toggleFavorite(recipeId, isFav)
    }
  }

  fun addCurrentRecipeToMealPlan(day: Int, slot: MealSlot, successMessage: String) {
    val recipe = _selectedRecipe.value ?: return
    viewModelScope.launch {
      repository.setMealPlanSlot(day, slot, recipe.id)
      showAddToPlanDialog.value = false
      _toastMessage.emit(successMessage)
    }
  }

  fun assignRecipeToMealPlan(day: Int, slot: MealSlot, recipeId: String) {
    viewModelScope.launch {
      repository.setMealPlanSlot(day, slot, recipeId)
      showRecipePickerForPlanSlot.value = null
    }
  }

  fun removeMealPlanItem(id: Long) {
    viewModelScope.launch {
      repository.removeMealPlanItem(id)
    }
  }

  val isGeneratingMenu = MutableStateFlow(false)

  fun generateWeeklyMenu() {
    if (isGeneratingMenu.value) return
    viewModelScope.launch {
      isGeneratingMenu.value = true
      val result = try {
        repository.generateWeeklyMenu()
      } finally {
        isGeneratingMenu.value = false
      }
      val message = when (result) {
        is WeeklyMenuResult.Success -> str(R.string.menu_generated)
        WeeklyMenuResult.NoRecipesAvailable -> str(R.string.menu_empty_catalog)
        WeeklyMenuResult.Failure -> str(R.string.menu_generation_failed)
      }
      _toastMessage.emit(message)
    }
  }

  // Collections
  fun requestSaveToCollection(recipe: Recipe) {
    saveToCollectionFor.value = recipe
  }

  fun dismissSaveToCollection() {
    saveToCollectionFor.value = null
  }

  /**
   * Adds or removes [recipeId] from [collectionId] depending on [add]. The
   * dialog stays open so the user can keep toggling other collections, and the
   * UI reflects the change as soon as Room emits the updated collections. A
   * real success/failure message is shown; a failed write never fakes success.
   */
  fun toggleRecipeInCollection(collectionId: String, recipeId: String, add: Boolean) {
    viewModelScope.launch {
      val result = if (add) {
        repository.addRecipeToCollection(collectionId, recipeId)
      } else {
        repository.removeRecipeFromCollection(collectionId, recipeId)
      }
      val messageRes = when (result) {
        CollectionActionResult.Success ->
          if (add) R.string.saved_to_collection else R.string.removed_from_collection
        CollectionActionResult.NotFound,
        CollectionActionResult.Failure -> R.string.collection_action_failed
      }
      _toastMessage.emit(str(messageRes))
    }
  }

  fun createCollectionAndSave(name: String, recipeId: String) {
    viewModelScope.launch {
      when (val created = repository.createCollection(name)) {
        is CreateCollectionResult.Success -> {
          // Keep the dialog open so the freshly created collection shows up as
          // selected and the user can continue organising the recipe.
          val added = repository.addRecipeToCollection(created.collection.id, recipeId)
          if (added == CollectionActionResult.Success) {
            _toastMessage.emit(str(R.string.saved_to_collection))
          } else {
            _toastMessage.emit(str(R.string.collection_action_failed))
          }
        }
        CreateCollectionResult.BlankName -> _toastMessage.emit(str(R.string.collection_name_empty))
        CreateCollectionResult.DuplicateName -> _toastMessage.emit(str(R.string.collection_name_duplicate))
        CreateCollectionResult.Failure -> _toastMessage.emit(str(R.string.collection_action_failed))
      }
    }
  }

  fun createNewCollection(name: String) {
    viewModelScope.launch {
      when (repository.createCollection(name)) {
        is CreateCollectionResult.Success -> {
          showNewCollectionDialog.value = false
          _toastMessage.emit(str(R.string.collection_created))
        }
        CreateCollectionResult.BlankName -> _toastMessage.emit(str(R.string.collection_name_empty))
        CreateCollectionResult.DuplicateName -> _toastMessage.emit(str(R.string.collection_name_duplicate))
        CreateCollectionResult.Failure -> _toastMessage.emit(str(R.string.collection_action_failed))
      }
    }
  }

  fun deleteCollection(collectionId: String) {
    viewModelScope.launch {
      when (repository.deleteCollection(collectionId)) {
        CollectionActionResult.Success -> {
          if (selectedCollectionId.value == collectionId) selectedCollectionId.value = null
          _toastMessage.emit(str(R.string.collection_deleted))
        }
        CollectionActionResult.NotFound,
        CollectionActionResult.Failure -> _toastMessage.emit(str(R.string.collection_action_failed))
      }
    }
  }

  fun removeRecipeFromCollection(collectionId: String, recipeId: String) {
    viewModelScope.launch {
      when (repository.removeRecipeFromCollection(collectionId, recipeId)) {
        CollectionActionResult.Success -> _toastMessage.emit(str(R.string.removed_from_collection))
        CollectionActionResult.NotFound,
        CollectionActionResult.Failure -> _toastMessage.emit(str(R.string.collection_action_failed))
      }
    }
  }

  private fun str(@StringRes id: Int): String = getApplication<Application>().getString(id)

  // Language & Theme & Profile Settings
  fun setLanguage(lang: Language) {
    viewModelScope.launch {
      val updated = (userProfile.value ?: UserProfile()).copy(language = lang)
      repository.saveUserProfile(updated)
    }
  }

  fun setThemeMode(isDark: Boolean?) {
    viewModelScope.launch {
      val updated = (userProfile.value ?: UserProfile()).copy(isDarkTheme = isDark)
      repository.saveUserProfile(updated)
    }
  }

  fun completeOnboarding(
    dietaryPrefs: Set<String>,
    allergies: Set<String>
  ) {
    viewModelScope.launch {
      val updated = (userProfile.value ?: UserProfile()).copy(
        dietaryPreferences = dietaryPrefs,
        allergies = allergies,
        isOnboardingCompleted = true
      )
      repository.saveUserProfile(updated)
    }
  }

  fun filterByCategory(cat: RecipeCategory?) {
    filterState.value = filterState.value.copy(category = cat)
  }

  fun resetFilters() {
    filterState.value = FilterState()
    searchQuery.value = ""
  }
}
