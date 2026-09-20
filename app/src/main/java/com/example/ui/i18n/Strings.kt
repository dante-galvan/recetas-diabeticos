package com.example.ui.i18n

enum class Language(val code: String, val displayName: String) {
  SPANISH("es", "Español"),
  ENGLISH("en", "English");

  companion object {
    fun fromCode(code: String): Language {
      return if (code.lowercase().startsWith("es")) SPANISH else ENGLISH
    }
  }
}

object AppStrings {
  fun appName(lang: Language) = if (lang == Language.SPANISH) "DiaRecipes" else "DiaRecipes"

  // Navigation tabs
  fun navHome(lang: Language) = if (lang == Language.SPANISH) "Inicio" else "Home"
  fun navExplore(lang: Language) = if (lang == Language.SPANISH) "Explorar" else "Explore"
  fun navPlan(lang: Language) = if (lang == Language.SPANISH) "Plan" else "Plan"
  fun navFavorites(lang: Language) = if (lang == Language.SPANISH) "Favoritos" else "Favorites"
  fun navProfile(lang: Language) = if (lang == Language.SPANISH) "Perfil" else "Profile"

  // Home Screen
  fun greetingMorning(lang: Language) = if (lang == Language.SPANISH) "¡Buenos días!" else "Good morning!"
  fun greetingAfternoon(lang: Language) = if (lang == Language.SPANISH) "¡Buenas tardes!" else "Good afternoon!"
  fun greetingEvening(lang: Language) = if (lang == Language.SPANISH) "¡Buenas noches!" else "Good evening!"
  fun homeSubtitle(lang: Language) = if (lang == Language.SPANISH) 
    "Comida rica, sencilla y saludable para hoy" 
  else 
    "Delicious, simple and healthy food for today"

  fun recipeOfTheDay(lang: Language) = if (lang == Language.SPANISH) "Receta del Día" else "Recipe of the Day"
  fun quickCategories(lang: Language) = if (lang == Language.SPANISH) "Categorías rápidas" else "Quick Categories"
  fun recommendedForYou(lang: Language) = if (lang == Language.SPANISH) "Recomendadas para ti" else "Recommended for you"
  fun popularRecipes(lang: Language) = if (lang == Language.SPANISH) "Recetas populares" else "Popular Recipes"
  fun newRecipes(lang: Language) = if (lang == Language.SPANISH) "Nuevas recetas" else "New Recipes"
  fun todayMealPlan(lang: Language) = if (lang == Language.SPANISH) "Tu menú de hoy" else "Today's Meal Plan"
  fun viewFullPlan(lang: Language) = if (lang == Language.SPANISH) "Ver plan semanal" else "View weekly plan"
  fun viewAll(lang: Language) = if (lang == Language.SPANISH) "Ver todas" else "View all"

  // Nutrition labels
  fun totalCarbs(lang: Language) = if (lang == Language.SPANISH) "Carbohidratos Totales" else "Total Carbohydrates"
  fun totalCarbsShort(lang: Language) = if (lang == Language.SPANISH) "Carbos Totales" else "Total Carbs"
  fun calories(lang: Language) = if (lang == Language.SPANISH) "Calorías" else "Calories"
  fun sugars(lang: Language) = if (lang == Language.SPANISH) "Azúcares" else "Sugars"
  fun protein(lang: Language) = if (lang == Language.SPANISH) "Proteínas" else "Protein"
  fun totalFat(lang: Language) = if (lang == Language.SPANISH) "Grasas Totales" else "Total Fat"
  fun saturatedFat(lang: Language) = if (lang == Language.SPANISH) "Grasas Saturadas" else "Saturated Fat"
  fun fiber(lang: Language) = if (lang == Language.SPANISH) "Fibra Dietética" else "Dietary Fiber"
  fun sodium(lang: Language) = if (lang == Language.SPANISH) "Sodio" else "Sodium"
  fun perServing(lang: Language) = if (lang == Language.SPANISH) "por porción" else "per serving"

  // Recipe detail
  fun prepTime(lang: Language) = if (lang == Language.SPANISH) "Preparación" else "Prep time"
  fun cookTime(lang: Language) = if (lang == Language.SPANISH) "Cocción" else "Cook time"
  fun totalTime(lang: Language) = if (lang == Language.SPANISH) "Tiempo total" else "Total time"
  fun difficulty(lang: Language) = if (lang == Language.SPANISH) "Dificultad" else "Difficulty"
  fun servings(lang: Language) = if (lang == Language.SPANISH) "Porciones" else "Servings"
  fun nutritionalInfo(lang: Language) = if (lang == Language.SPANISH) "Información Nutricional" else "Nutritional Information"
  fun ingredients(lang: Language) = if (lang == Language.SPANISH) "Ingredientes" else "Ingredients"
  fun instructions(lang: Language) = if (lang == Language.SPANISH) "Preparación Paso a Paso" else "Step-by-Step Instructions"
  fun addToPlan(lang: Language) = if (lang == Language.SPANISH) "Agregar al Plan" else "Add to Plan"
  fun shareRecipe(lang: Language) = if (lang == Language.SPANISH) "Compartir" else "Share"
  fun addedToPlanSuccess(lang: Language) = if (lang == Language.SPANISH) "¡Receta agregada a tu plan semanal!" else "Recipe added to weekly plan!"
  fun step(lang: Language, num: Int) = if (lang == Language.SPANISH) "Paso $num" else "Step $num"

  // Explore & Filters
  fun searchPlaceholder(lang: Language) = if (lang == Language.SPANISH) "Buscar por nombre, ingrediente..." else "Search by recipe, ingredient..."
  fun filters(lang: Language) = if (lang == Language.SPANISH) "Filtros" else "Filters"
  fun filterAll(lang: Language) = if (lang == Language.SPANISH) "Todos" else "All"
  fun filterCategories(lang: Language) = if (lang == Language.SPANISH) "Categorías" else "Categories"
  fun filterDietary(lang: Language) = if (lang == Language.SPANISH) "Preferencias alimentarias" else "Dietary preferences"
  fun filterMaxCarbs(lang: Language) = if (lang == Language.SPANISH) "Carbohidratos máximos" else "Max carbohydrates"
  fun filterMaxCalories(lang: Language) = if (lang == Language.SPANISH) "Calorías máximas" else "Max calories"
  fun filterMaxTime(lang: Language) = if (lang == Language.SPANISH) "Tiempo máximo de preparación" else "Max prep time"
  fun filterDifficulty(lang: Language) = if (lang == Language.SPANISH) "Nivel de dificultad" else "Difficulty level"
  fun sortOrder(lang: Language) = if (lang == Language.SPANISH) "Ordenar por" else "Sort by"
  fun sortLowestCarbs(lang: Language) = if (lang == Language.SPANISH) "Menos carbohidratos" else "Lowest carbs"
  fun sortLowestCalories(lang: Language) = if (lang == Language.SPANISH) "Menos calorías" else "Lowest calories"
  fun sortQuickest(lang: Language) = if (lang == Language.SPANISH) "Más rápidas" else "Quickest"
  fun sortHighestProtein(lang: Language) = if (lang == Language.SPANISH) "Más proteínas" else "Highest protein"
  fun resetFilters(lang: Language) = if (lang == Language.SPANISH) "Limpiar filtros" else "Reset filters"
  fun applyFilters(lang: Language) = if (lang == Language.SPANISH) "Aplicar filtros" else "Apply filters"

  // Weekly Planner
  fun weeklyMealPlanner(lang: Language) = if (lang == Language.SPANISH) "Planificador Semanal" else "Weekly Meal Planner"
  fun generateWeeklyMenu(lang: Language) = if (lang == Language.SPANISH) "Generar menú semanal" else "Generate weekly menu"
  fun generateMenuDesc(lang: Language) = if (lang == Language.SPANISH) 
    "Arma automáticamente un menú semanal equilibrado y adaptado a tus necesidades." 
  else 
    "Automatically generate a balanced weekly menu tailored to your needs."
  fun dayMonday(lang: Language) = if (lang == Language.SPANISH) "Lunes" else "Monday"
  fun dayTuesday(lang: Language) = if (lang == Language.SPANISH) "Martes" else "Tuesday"
  fun dayWednesday(lang: Language) = if (lang == Language.SPANISH) "Miércoles" else "Wednesday"
  fun dayThursday(lang: Language) = if (lang == Language.SPANISH) "Jueves" else "Thursday"
  fun dayFriday(lang: Language) = if (lang == Language.SPANISH) "Viernes" else "Friday"
  fun daySaturday(lang: Language) = if (lang == Language.SPANISH) "Sábado" else "Saturday"
  fun daySunday(lang: Language) = if (lang == Language.SPANISH) "Domingo" else "Sunday"

  fun mealBreakfast(lang: Language) = if (lang == Language.SPANISH) "Desayuno" else "Breakfast"
  fun mealLunch(lang: Language) = if (lang == Language.SPANISH) "Almuerzo" else "Lunch"
  fun mealDinner(lang: Language) = if (lang == Language.SPANISH) "Cena" else "Dinner"
  fun mealSnack(lang: Language) = if (lang == Language.SPANISH) "Snack / Merienda" else "Snack"
  fun addRecipeToSlot(lang: Language) = if (lang == Language.SPANISH) "+ Agregar receta" else "+ Add recipe"
  fun changeRecipe(lang: Language) = if (lang == Language.SPANISH) "Cambiar receta" else "Change recipe"
  fun removeRecipe(lang: Language) = if (lang == Language.SPANISH) "Quitar receta" else "Remove recipe"
  fun emptyDayPlan(lang: Language) = if (lang == Language.SPANISH) "No has agregado comidas para este día" else "No meals added for this day yet"
  fun selectDayAndSlot(lang: Language) = if (lang == Language.SPANISH) "Selecciona día y horario" else "Select day and meal slot"

  // Favorites & Collections
  fun myFavorites(lang: Language) = if (lang == Language.SPANISH) "Mis Favoritos" else "My Favorites"
  fun collections(lang: Language) = if (lang == Language.SPANISH) "Colecciones" else "Collections"
  fun newCollection(lang: Language) = if (lang == Language.SPANISH) "+ Nueva colección" else "+ New collection"
  fun createCollection(lang: Language) = if (lang == Language.SPANISH) "Crear colección" else "Create collection"
  fun collectionNamePrompt(lang: Language) = if (lang == Language.SPANISH) "Nombre de la colección (ej. Cenas rápidas)" else "Collection name (e.g. Quick dinners)"
  fun emptyFavorites(lang: Language) = if (lang == Language.SPANISH) "Aún no tienes recetas favoritas" else "No favorite recipes yet"
  fun emptyFavoritesDesc(lang: Language) = if (lang == Language.SPANISH) 
    "Toca el corazón en cualquier receta para guardarla aquí y tenerla siempre a mano." 
  else 
    "Tap the heart icon on any recipe to save it here for quick everyday access."
  fun exploreRecipesCta(lang: Language) = if (lang == Language.SPANISH) "Explorar recetas" else "Explore recipes"

  // Onboarding
  fun onboardingWelcomeTitle(lang: Language) = if (lang == Language.SPANISH) "Bienvenido a DiaRecipes" else "Welcome to DiaRecipes"
  fun onboardingWelcomeSub(lang: Language) = if (lang == Language.SPANISH) 
    "Recetas saludables, control de carbohidratos y planificación sencilla para tu día a día." 
  else 
    "Healthy recipes, clear carb tracking, and simple meal planning for your everyday life."
  fun onboardingDietTitle(lang: Language) = if (lang == Language.SPANISH) "Preferencias alimentarias" else "Dietary preferences"
  fun onboardingDietSub(lang: Language) = if (lang == Language.SPANISH) 
    "Elige los estilos de alimentación que más te gustan." 
  else 
    "Select the eating styles you enjoy the most."
  fun onboardingAllergiesTitle(lang: Language) = if (lang == Language.SPANISH) "Alergias o ingredientes a evitar" else "Allergies or ingredients to avoid"
  fun onboardingAllergiesSub(lang: Language) = if (lang == Language.SPANISH) 
    "Ocultaremos o destacaremos avisos en recetas que contengan estos ingredientes." 
  else 
    "We will highlight or adapt recipes containing these ingredients."
  fun continueBtn(lang: Language) = if (lang == Language.SPANISH) "Continuar" else "Continue"
  fun startCooking(lang: Language) = if (lang == Language.SPANISH) "Comenzar" else "Get Started"
  fun skip(lang: Language) = if (lang == Language.SPANISH) "Omitir por ahora" else "Skip for now"

  // Profile & Settings
  fun userProfile(lang: Language) = if (lang == Language.SPANISH) "Mi Perfil" else "My Profile"
  fun dietaryGoals(lang: Language) = if (lang == Language.SPANISH) "Preferencias de dieta" else "Dietary preferences"
  fun allergies(lang: Language) = if (lang == Language.SPANISH) "Alergias registradas" else "Registered allergies"
  fun noneRecorded(lang: Language) = if (lang == Language.SPANISH) "Ninguna registrada" else "None specified"
  fun editPreferences(lang: Language) = if (lang == Language.SPANISH) "Editar preferencias" else "Edit preferences"
  fun settingsTitle(lang: Language) = if (lang == Language.SPANISH) "Configuración" else "Settings"
  fun languageSetting(lang: Language) = if (lang == Language.SPANISH) "Idioma de la aplicación" else "App Language"
  fun themeSetting(lang: Language) = if (lang == Language.SPANISH) "Tema de pantalla" else "Display Theme"
  fun themeLight(lang: Language) = if (lang == Language.SPANISH) "Claro" else "Light"
  fun themeDark(lang: Language) = if (lang == Language.SPANISH) "Oscuro" else "Dark"
  fun themeSystem(lang: Language) = if (lang == Language.SPANISH) "Automático del sistema" else "System default"
  fun notifications(lang: Language) = if (lang == Language.SPANISH) "Recordatorios de comida" else "Meal reminders"
  fun privacyPolicy(lang: Language) = if (lang == Language.SPANISH) "Política de Privacidad" else "Privacy Policy"
  fun termsOfService(lang: Language) = if (lang == Language.SPANISH) "Términos y Condiciones" else "Terms of Service"
  fun aboutApp(lang: Language) = if (lang == Language.SPANISH) "Acerca de DiaRecipes" else "About DiaRecipes"
  fun appVersion(lang: Language) = if (lang == Language.SPANISH) "Versión 1.0.0 (Build 2026)" else "Version 1.0.0 (Build 2026)"

  // Empty & Error states
  fun noSearchResults(lang: Language) = if (lang == Language.SPANISH) "No encontramos recetas para tu búsqueda" else "No recipes found matching your search"
  fun noSearchResultsDesc(lang: Language) = if (lang == Language.SPANISH) 
    "Prueba con otros términos o relaja algunos de los filtros seleccionados." 
  else 
    "Try searching with different terms or adjust your selected filters."
  fun clearSearch(lang: Language) = if (lang == Language.SPANISH) "Restablecer búsqueda" else "Clear search"
  fun close(lang: Language) = if (lang == Language.SPANISH) "Cerrar" else "Close"
  fun cancel(lang: Language) = if (lang == Language.SPANISH) "Cancelar" else "Cancel"
  fun save(lang: Language) = if (lang == Language.SPANISH) "Guardar" else "Save"
  fun delete(lang: Language) = if (lang == Language.SPANISH) "Eliminar" else "Delete"
  fun min(lang: Language) = if (lang == Language.SPANISH) "min" else "min"
  fun kcal(lang: Language) = if (lang == Language.SPANISH) "kcal" else "kcal"
  fun grams(lang: Language) = if (lang == Language.SPANISH) "g" else "g"
  fun mg(lang: Language) = if (lang == Language.SPANISH) "mg" else "mg"

  // Difficulties
  fun diffEasy(lang: Language) = if (lang == Language.SPANISH) "Fácil" else "Easy"
  fun diffMedium(lang: Language) = if (lang == Language.SPANISH) "Media" else "Medium"
  fun diffHard(lang: Language) = if (lang == Language.SPANISH) "Avanzada" else "Advanced"

  // Aliases & Additional Screen Strings
  fun favorites(lang: Language) = myFavorites(lang)
  fun favoritesSubtitle(lang: Language) = if (lang == Language.SPANISH) "Tus recetas preferidas organizadas" else "Your preferred recipes organized"
  fun myCollections(lang: Language) = collections(lang)
  fun allFavorites(lang: Language) = if (lang == Language.SPANISH) "Todas" else "All"
  fun noFavoritesYet(lang: Language) = emptyFavorites(lang)
  fun noFavoritesDesc(lang: Language) = emptyFavoritesDesc(lang)
  fun discoverRecipes(lang: Language) = exploreRecipesCta(lang)

  fun profile(lang: Language) = userProfile(lang)
  fun language(lang: Language) = languageSetting(lang)
  fun darkMode(lang: Language) = themeDark(lang)
  fun confirm(lang: Language) = if (lang == Language.SPANISH) "Confirmar" else "Confirm"
  fun understood(lang: Language) = if (lang == Language.SPANISH) "Entendido" else "Understood"
  fun collectionName(lang: Language) = collectionNamePrompt(lang)

  fun onboardingTitle(lang: Language) = onboardingWelcomeTitle(lang)
  fun onboardingSubtitle(lang: Language) = onboardingWelcomeSub(lang)
  fun onboardingStep1(lang: Language) = onboardingDietTitle(lang)
  fun onboardingStep2(lang: Language) = onboardingAllergiesTitle(lang)
  fun continueText(lang: Language) = continueBtn(lang)
}
