package com.example.ui.i18n

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.app.diabeticos.R

object AppStrings {
  @Composable
  fun appName() = stringResource(R.string.app_name)

  // Navigation tabs
  @Composable
  fun navHome() = stringResource(R.string.nav_home)

  @Composable
  fun navExplore() = stringResource(R.string.nav_explore)

  @Composable
  fun navPlan() = stringResource(R.string.nav_plan)

  @Composable
  fun navFavorites() = stringResource(R.string.nav_favorites)

  @Composable
  fun navProfile() = stringResource(R.string.nav_profile)

  // Home Screen
  @Composable
  fun greetingMorning() = stringResource(R.string.greeting_morning)

  @Composable
  fun greetingAfternoon() = stringResource(R.string.greeting_afternoon)

  @Composable
  fun greetingEvening() = stringResource(R.string.greeting_evening)

  @Composable
  fun homeSubtitle() = stringResource(R.string.home_subtitle)

  @Composable
  fun recipeOfTheDay() = stringResource(R.string.recipe_of_the_day)

  @Composable
  fun quickCategories() = stringResource(R.string.quick_categories)

  @Composable
  fun recommendedForYou() = stringResource(R.string.recommended_for_you)

  @Composable
  fun popularRecipes() = stringResource(R.string.popular_recipes)

  @Composable
  fun newRecipes() = stringResource(R.string.new_recipes)

  @Composable
  fun todayMealPlan() = stringResource(R.string.today_meal_plan)

  @Composable
  fun viewFullPlan() = stringResource(R.string.view_full_plan)

  @Composable
  fun viewAll() = stringResource(R.string.view_all)

  // Nutrition labels
  @Composable
  fun totalCarbs() = stringResource(R.string.total_carbs)

  @Composable
  fun totalCarbsShort() = stringResource(R.string.total_carbs_short)

  @Composable
  fun calories() = stringResource(R.string.calories)

  @Composable
  fun sugars() = stringResource(R.string.sugars)

  @Composable
  fun protein() = stringResource(R.string.protein)

  @Composable
  fun totalFat() = stringResource(R.string.total_fat)

  @Composable
  fun saturatedFat() = stringResource(R.string.saturated_fat)

  @Composable
  fun fiber() = stringResource(R.string.fiber)

  @Composable
  fun sodium() = stringResource(R.string.sodium)

  @Composable
  fun perServing() = stringResource(R.string.per_serving)

  // Recipe detail
  @Composable
  fun prepTime() = stringResource(R.string.prep_time)

  @Composable
  fun cookTime() = stringResource(R.string.cook_time)

  @Composable
  fun totalTime() = stringResource(R.string.total_time)

  @Composable
  fun difficulty() = stringResource(R.string.difficulty)

  @Composable
  fun servings() = stringResource(R.string.servings)

  @Composable
  fun nutritionalInfo() = stringResource(R.string.nutritional_info)

  @Composable
  fun ingredients() = stringResource(R.string.ingredients)

  @Composable
  fun instructions() = stringResource(R.string.instructions)

  @Composable
  fun addToPlan() = stringResource(R.string.add_to_plan)

  @Composable
  fun shareRecipe() = stringResource(R.string.share_recipe)

  @Composable
  fun addedToPlanSuccess() = stringResource(R.string.added_to_plan_success)

  @Composable
  fun step(num: Int) = stringResource(R.string.step, num)

  // Explore & Filters
  @Composable
  fun searchPlaceholder() = stringResource(R.string.search_placeholder)

  @Composable
  fun filters() = stringResource(R.string.filters)

  @Composable
  fun filterAll() = stringResource(R.string.filter_all)

  @Composable
  fun filterCategories() = stringResource(R.string.filter_categories)

  @Composable
  fun filterDietary() = stringResource(R.string.filter_dietary)

  @Composable
  fun filterMaxCarbs() = stringResource(R.string.filter_max_carbs)

  @Composable
  fun filterMaxCalories() = stringResource(R.string.filter_max_calories)

  @Composable
  fun filterMaxTime() = stringResource(R.string.filter_max_time)

  @Composable
  fun filterDifficulty() = stringResource(R.string.filter_difficulty)

  @Composable
  fun sortOrder() = stringResource(R.string.sort_order)

  @Composable
  fun sortLowestCarbs() = stringResource(R.string.sort_lowest_carbs)

  @Composable
  fun sortLowestCalories() = stringResource(R.string.sort_lowest_calories)

  @Composable
  fun sortQuickest() = stringResource(R.string.sort_quickest)

  @Composable
  fun sortHighestProtein() = stringResource(R.string.sort_highest_protein)

  @Composable
  fun resetFilters() = stringResource(R.string.reset_filters)

  @Composable
  fun applyFilters() = stringResource(R.string.apply_filters)

  // Weekly Planner
  @Composable
  fun weeklyMealPlanner() = stringResource(R.string.weekly_meal_planner)

  @Composable
  fun generateWeeklyMenu() = stringResource(R.string.generate_weekly_menu)

  @Composable
  fun generateMenuDesc() = stringResource(R.string.generate_menu_desc)

  @Composable
  fun dayMonday() = stringResource(R.string.day_monday)

  @Composable
  fun dayTuesday() = stringResource(R.string.day_tuesday)

  @Composable
  fun dayWednesday() = stringResource(R.string.day_wednesday)

  @Composable
  fun dayThursday() = stringResource(R.string.day_thursday)

  @Composable
  fun dayFriday() = stringResource(R.string.day_friday)

  @Composable
  fun daySaturday() = stringResource(R.string.day_saturday)

  @Composable
  fun daySunday() = stringResource(R.string.day_sunday)

  @Composable
  fun mealBreakfast() = stringResource(R.string.meal_breakfast)

  @Composable
  fun mealLunch() = stringResource(R.string.meal_lunch)

  @Composable
  fun mealDinner() = stringResource(R.string.meal_dinner)

  @Composable
  fun mealSnack() = stringResource(R.string.meal_snack)

  @Composable
  fun addRecipeToSlot() = stringResource(R.string.add_recipe_to_slot)

  @Composable
  fun changeRecipe() = stringResource(R.string.change_recipe)

  @Composable
  fun removeRecipe() = stringResource(R.string.remove_recipe)

  @Composable
  fun emptyDayPlan() = stringResource(R.string.empty_day_plan)

  @Composable
  fun selectDayAndSlot() = stringResource(R.string.select_day_and_slot)

  // Favorites & Collections
  @Composable
  fun myFavorites() = stringResource(R.string.my_favorites)

  @Composable
  fun collections() = stringResource(R.string.collections)

  @Composable
  fun newCollection() = stringResource(R.string.new_collection)

  @Composable
  fun createCollection() = stringResource(R.string.create_collection)

  @Composable
  fun collectionNamePrompt() = stringResource(R.string.collection_name_prompt)

  @Composable
  fun emptyFavorites() = stringResource(R.string.empty_favorites)

  @Composable
  fun emptyFavoritesDesc() = stringResource(R.string.empty_favorites_desc)

  @Composable
  fun exploreRecipesCta() = stringResource(R.string.explore_recipes_cta)

  // Collections management
  @Composable
  fun saveToCollection() = stringResource(R.string.save_to_collection)

  @Composable
  fun cdSaveToCollection() = stringResource(R.string.cd_save_to_collection)

  @Composable
  fun addToCollection() = stringResource(R.string.add_to_collection)

  @Composable
  fun alreadySaved() = stringResource(R.string.already_saved)

  @Composable
  fun deleteCollection() = stringResource(R.string.delete_collection)

  @Composable
  fun deleteCollectionTitle() = stringResource(R.string.delete_collection_title)

  @Composable
  fun deleteCollectionMessage() = stringResource(R.string.delete_collection_message)

  @Composable
  fun removeFromCollection() = stringResource(R.string.remove_from_collection)

  @Composable
  fun emptyCollection() = stringResource(R.string.empty_collection)

  @Composable
  fun emptyCollectionDesc() = stringResource(R.string.empty_collection_desc)

  @Composable
  fun noCollectionsYet() = stringResource(R.string.no_collections_yet)

  @Composable
  fun noCollectionsDesc() = stringResource(R.string.no_collections_desc)

  // Weekly menu generation
  @Composable
  fun generatingMenu() = stringResource(R.string.generating_menu)

  @Composable
  fun replaceMenuTitle() = stringResource(R.string.replace_menu_title)

  @Composable
  fun replaceMenuMessage() = stringResource(R.string.replace_menu_message)

  @Composable
  fun replace() = stringResource(R.string.replace)

  // Onboarding
  @Composable
  fun onboardingWelcomeTitle() = stringResource(R.string.onboarding_welcome_title)

  @Composable
  fun onboardingWelcomeSub() = stringResource(R.string.onboarding_welcome_sub)

  @Composable
  fun onboardingDietTitle() = stringResource(R.string.onboarding_diet_title)

  @Composable
  fun onboardingDietSub() = stringResource(R.string.onboarding_diet_sub)

  @Composable
  fun onboardingAllergiesTitle() = stringResource(R.string.onboarding_allergies_title)

  @Composable
  fun onboardingAllergiesSub() = stringResource(R.string.onboarding_allergies_sub)

  @Composable
  fun continueBtn() = stringResource(R.string.continue_btn)

  @Composable
  fun startCooking() = stringResource(R.string.start_cooking)

  @Composable
  fun skip() = stringResource(R.string.skip)

  // Profile & Settings
  @Composable
  fun userProfile() = stringResource(R.string.user_profile)

  @Composable
  fun dietaryGoals() = stringResource(R.string.dietary_goals)

  @Composable
  fun allergies() = stringResource(R.string.allergies)

  @Composable
  fun noneRecorded() = stringResource(R.string.none_recorded)

  @Composable
  fun editPreferences() = stringResource(R.string.edit_preferences)

  @Composable
  fun settingsTitle() = stringResource(R.string.settings_title)

  @Composable
  fun languageSetting() = stringResource(R.string.language_setting)

  @Composable
  fun themeSetting() = stringResource(R.string.theme_setting)

  @Composable
  fun themeLight() = stringResource(R.string.theme_light)

  @Composable
  fun themeDark() = stringResource(R.string.theme_dark)

  @Composable
  fun themeSystem() = stringResource(R.string.theme_system)

  @Composable
  fun notifications() = stringResource(R.string.notifications)

  @Composable
  fun privacyPolicy() = stringResource(R.string.privacy_policy)

  @Composable
  fun termsOfService() = stringResource(R.string.terms_of_service)

  @Composable
  fun aboutApp() = stringResource(R.string.about_app)

  @Composable
  fun appVersion() = stringResource(R.string.app_version)

  // Empty & Error states
  @Composable
  fun noSearchResults() = stringResource(R.string.no_search_results)

  @Composable
  fun noSearchResultsDesc() = stringResource(R.string.no_search_results_desc)

  @Composable
  fun clearSearch() = stringResource(R.string.clear_search)

  @Composable
  fun close() = stringResource(R.string.close)

  @Composable
  fun cancel() = stringResource(R.string.cancel)

  @Composable
  fun save() = stringResource(R.string.save)

  @Composable
  fun delete() = stringResource(R.string.delete)

  @Composable
  fun confirm() = stringResource(R.string.confirm)

  @Composable
  fun understood() = stringResource(R.string.understood)

  @Composable
  fun min() = stringResource(R.string.min)

  @Composable
  fun kcal() = stringResource(R.string.kcal)

  @Composable
  fun grams() = stringResource(R.string.grams)

  @Composable
  fun mg() = stringResource(R.string.mg)

  // Difficulties
  @Composable
  fun diffEasy() = stringResource(R.string.diff_easy)

  @Composable
  fun diffMedium() = stringResource(R.string.diff_medium)

  @Composable
  fun diffHard() = stringResource(R.string.diff_hard)

  // Additional Screen Strings
  @Composable
  fun favoritesSubtitle() = stringResource(R.string.favorites_subtitle)

  @Composable
  fun allFavorites() = stringResource(R.string.all_favorites)

  @Composable
  fun noFavoritesYet() = stringResource(R.string.no_favorites_yet)

  @Composable
  fun noFavoritesDesc() = stringResource(R.string.no_favorites_desc)

  @Composable
  fun discoverRecipes() = stringResource(R.string.discover_recipes)

  // Dialog specific
  @Composable
  fun dayOfWeek() = stringResource(R.string.day_of_week)

  @Composable
  fun mealSlot() = stringResource(R.string.meal_slot)

  @Composable
  fun recipesAvailable(count: Int) = stringResource(R.string.recipes_available, count)

  // Content descriptions & misc
  @Composable
  fun noRecipeSelected() = stringResource(R.string.no_recipe_selected)

  @Composable
  fun sortRelevance() = stringResource(R.string.sort_relevance)

  @Composable
  fun cdFavorite() = stringResource(R.string.cd_favorite)

  @Composable
  fun cdRemoveFavorite() = stringResource(R.string.cd_remove_favorite)

  @Composable
  fun cdAddFavorite() = stringResource(R.string.cd_add_favorite)

  @Composable
  fun cdClose() = stringResource(R.string.cd_close)

  @Composable
  fun cdBack() = stringResource(R.string.cd_back)

  @Composable
  fun cdSearch() = stringResource(R.string.cd_search)

  @Composable
  fun cdClearSearch() = stringResource(R.string.cd_clear_search)

  @Composable
  fun cdDecreaseServings() = stringResource(R.string.cd_decrease_servings)

  @Composable
  fun cdIncreaseServings() = stringResource(R.string.cd_increase_servings)

  @Composable
  fun cdChange() = stringResource(R.string.cd_change)

  @Composable
  fun cdRemove() = stringResource(R.string.cd_remove)

  @Composable
  fun cdCompleted() = stringResource(R.string.cd_completed)

  // Dietary / allergen display labels (values stay stable English keys)
  @Composable
  fun dietaryLabel(value: String): String = when (value) {
    "Low Carb" -> stringResource(R.string.diet_low_carb)
    "Mediterranean" -> stringResource(R.string.diet_mediterranean)
    "Balanced" -> stringResource(R.string.diet_balanced)
    "Vegetarian" -> stringResource(R.string.diet_vegetarian)
    "Vegan" -> stringResource(R.string.diet_vegan)
    "High Fiber" -> stringResource(R.string.diet_high_fiber)
    "Gluten Free" -> stringResource(R.string.diet_gluten_free)
    "High Protein" -> stringResource(R.string.diet_high_protein)
    else -> value
  }

  @Composable
  fun allergenLabel(value: String): String = when (value) {
    "Gluten" -> stringResource(R.string.allergen_gluten)
    "Lactose" -> stringResource(R.string.allergen_lactose)
    "Nuts" -> stringResource(R.string.allergen_nuts)
    "Shellfish" -> stringResource(R.string.allergen_shellfish)
    "Eggs" -> stringResource(R.string.allergen_eggs)
    "Soy" -> stringResource(R.string.allergen_soy)
    else -> value
  }
}