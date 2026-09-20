package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.local.DiaRecipesDatabase
import com.example.data.model.Difficulty
import com.example.data.model.MealPlanItem
import com.example.data.model.MealSlot
import com.example.data.model.Recipe
import com.example.data.model.RecipeCategory
import com.example.data.model.RecipeCollection
import com.example.data.model.UserProfile
import com.example.data.repository.RecipeRepository
import com.example.data.repository.SortOption
import com.example.ui.i18n.Language
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
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

  // Selected Recipe Detail
  private val _selectedRecipe = MutableStateFlow<Recipe?>(null)
  val selectedRecipe: StateFlow<Recipe?> = _selectedRecipe.asStateFlow()

  private val _servingsMultiplier = MutableStateFlow(1.0)
  val servingsMultiplier: StateFlow<Double> = _servingsMultiplier.asStateFlow()

  // User Profile & Settings
  val userProfile: StateFlow<UserProfile> = repository.userProfile
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile())

  // Current Language
  val currentLanguage: StateFlow<Language> = userProfile
    .combine(MutableStateFlow(Unit)) { profile, _ -> profile.language }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Language.SPANISH)

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

  init {
    // Check initial device locale for default language if first time
    val defaultDeviceLang = Language.fromCode(java.util.Locale.getDefault().language)
    viewModelScope.launch {
      userProfile.collect { profile ->
        if (!profile.isOnboardingCompleted && profile.language != defaultDeviceLang) {
          repository.saveUserProfile(profile.copy(language = defaultDeviceLang))
        }
      }
    }
  }

  fun navigateTo(screen: AppScreen) {
    _currentScreen.value = screen
  }

  fun openRecipeDetail(recipe: Recipe) {
    _selectedRecipe.value = recipe
    _servingsMultiplier.value = 1.0
    _currentScreen.value = AppScreen.RECIPE_DETAIL
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

  fun generateWeeklyMenu(successMessage: String) {
    viewModelScope.launch {
      repository.generateWeeklyMenu()
      _toastMessage.emit(successMessage)
    }
  }

  // Collections
  fun createNewCollection(name: String) {
    if (name.isBlank()) return
    viewModelScope.launch {
      repository.createCollection(name)
      showNewCollectionDialog.value = false
    }
  }

  // Language & Theme & Profile Settings
  fun setLanguage(lang: Language) {
    viewModelScope.launch {
      val updated = userProfile.value.copy(language = lang)
      repository.saveUserProfile(updated)
    }
  }

  fun setThemeMode(isDark: Boolean?) {
    viewModelScope.launch {
      val updated = userProfile.value.copy(isDarkTheme = isDark)
      repository.saveUserProfile(updated)
    }
  }

  fun completeOnboarding(
    dietaryPrefs: Set<String>,
    allergies: Set<String>
  ) {
    viewModelScope.launch {
      val updated = userProfile.value.copy(
        dietaryPreferences = dietaryPrefs,
        allergies = allergies,
        isOnboardingCompleted = true
      )
      repository.saveUserProfile(updated)
      _currentScreen.value = AppScreen.HOME
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
