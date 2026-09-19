package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AddToPlanDialog
import com.example.ui.components.FilterBottomSheet
import com.example.ui.components.MedicalDisclaimerDialog
import com.example.ui.components.NewCollectionDialog
import com.example.ui.components.RecipePickerForSlotDialog
import com.example.ui.i18n.AppStrings
import com.example.ui.i18n.Language
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MealPlanScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.RecipeDetailScreen
import com.example.ui.theme.DiaRecipesTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  private val viewModel: MainViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val userProfile by viewModel.userProfile.collectAsState()
      val isDark = when (userProfile.isDarkTheme) {
        true -> true
        false -> false
        null -> isSystemInDarkTheme()
      }

      DiaRecipesTheme(darkTheme = isDark) {
        DiaRecipesApp(viewModel = viewModel)
      }
    }
  }
}

sealed class BottomNavItem(
  val screen: AppScreen,
  val titleKey: (Language) -> String,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector,
  val testTag: String
) {
  object Home : BottomNavItem(
    AppScreen.HOME,
    { AppStrings.navHome(it) },
    Icons.Filled.Home,
    Icons.Outlined.Home,
    "nav_home"
  )

  object Explore : BottomNavItem(
    AppScreen.EXPLORE,
    { AppStrings.navExplore(it) },
    Icons.Filled.Search,
    Icons.Outlined.Search,
    "nav_explore"
  )

  object Plan : BottomNavItem(
    AppScreen.PLAN,
    { AppStrings.navPlan(it) },
    Icons.Filled.CalendarMonth,
    Icons.Outlined.CalendarMonth,
    "nav_plan"
  )

  object Favorites : BottomNavItem(
    AppScreen.FAVORITES,
    { AppStrings.navFavorites(it) },
    Icons.Filled.Favorite,
    Icons.Outlined.FavoriteBorder,
    "nav_favorites"
  )

  object Profile : BottomNavItem(
    AppScreen.PROFILE,
    { AppStrings.navProfile(it) },
    Icons.Filled.Person,
    Icons.Outlined.Person,
    "nav_profile"
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaRecipesApp(viewModel: MainViewModel) {
  val currentScreen by viewModel.currentScreen.collectAsState()
  val language by viewModel.currentLanguage.collectAsState()
  val userProfile by viewModel.userProfile.collectAsState()
  val filterSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val snackbarHostState = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()

  // Listen for ViewModel toast/snackbar messages
  LaunchedEffect(Unit) {
    viewModel.toastMessage.collect { message ->
      snackbarHostState.showSnackbar(message)
    }
  }

  val navItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Explore,
    BottomNavItem.Plan,
    BottomNavItem.Favorites,
    BottomNavItem.Profile
  )

  val isBottomBarVisible = currentScreen in listOf(
    AppScreen.HOME,
    AppScreen.EXPLORE,
    AppScreen.PLAN,
    AppScreen.FAVORITES,
    AppScreen.PROFILE
  )

  // Handle back navigation
  BackHandler(enabled = currentScreen != AppScreen.HOME) {
    when (currentScreen) {
      AppScreen.RECIPE_DETAIL -> viewModel.navigateTo(AppScreen.HOME)
      AppScreen.ONBOARDING -> viewModel.navigateTo(AppScreen.HOME)
      else -> viewModel.navigateTo(AppScreen.HOME)
    }
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    bottomBar = {
      if (isBottomBarVisible) {
        NavigationBar(
          containerColor = MaterialTheme.colorScheme.surface,
          tonalElevation = 6.dp,
          modifier = Modifier.testTag("bottom_nav_bar")
        ) {
          navItems.forEach { item ->
            val isSelected = currentScreen == item.screen
            NavigationBarItem(
              selected = isSelected,
              onClick = { viewModel.navigateTo(item.screen) },
              icon = {
                Icon(
                  imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                  contentDescription = item.titleKey(language)
                )
              },
              label = {
                Text(
                  text = item.titleKey(language),
                  fontSize = 11.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
              },
              colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
              ),
              modifier = Modifier.testTag(item.testTag)
            )
          }
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      Crossfade(targetState = currentScreen, label = "screen_transition") { screen ->
        when (screen) {
          AppScreen.HOME -> HomeScreen(viewModel = viewModel)
          AppScreen.EXPLORE -> ExploreScreen(viewModel = viewModel)
          AppScreen.PLAN -> MealPlanScreen(viewModel = viewModel)
          AppScreen.FAVORITES -> FavoritesScreen(viewModel = viewModel)
          AppScreen.PROFILE -> ProfileScreen(viewModel = viewModel)
          AppScreen.RECIPE_DETAIL -> RecipeDetailScreen(viewModel = viewModel)
          AppScreen.ONBOARDING -> OnboardingScreen(
            viewModel = viewModel,
            language = language,
            onComplete = { viewModel.navigateTo(AppScreen.HOME) }
          )
        }
      }
    }
  }

  // DIALOGS & BOTTOM SHEETS

  // 1. Add to Meal Plan Dialog
  val showAddToPlan by viewModel.showAddToPlanDialog.collectAsState()
  if (showAddToPlan) {
    AddToPlanDialog(
      language = language,
      onConfirm = { day, slot ->
        viewModel.addCurrentRecipeToMealPlan(
          day,
          slot,
          if (language == Language.SPANISH) "Receta añadida al plan semanal" else "Recipe added to weekly plan"
        )
      },
      onDismiss = { viewModel.showAddToPlanDialog.value = false }
    )
  }

  // 2. New Collection Dialog
  val showNewCollection by viewModel.showNewCollectionDialog.collectAsState()
  if (showNewCollection) {
    NewCollectionDialog(
      language = language,
      onConfirm = { name -> viewModel.createNewCollection(name) },
      onDismiss = { viewModel.showNewCollectionDialog.value = false }
    )
  }

  // 3. Medical Disclaimer Dialog
  val showDisclaimer by viewModel.showDisclaimerDialog.collectAsState()
  if (showDisclaimer) {
    MedicalDisclaimerDialog(
      language = language,
      onDismiss = { viewModel.showDisclaimerDialog.value = false }
    )
  }

  // 4. Filter Bottom Sheet (Explore screen)
  val showFilters by viewModel.showFilterSheet.collectAsState()
  val currentFilters by viewModel.filterState.collectAsState()
  if (showFilters) {
    FilterBottomSheet(
      sheetState = filterSheetState,
      currentFilters = currentFilters,
      language = language,
      onApplyFilters = { newFilters -> viewModel.filterState.value = newFilters },
      onResetFilters = { viewModel.resetFilters() },
      onDismiss = { viewModel.showFilterSheet.value = false }
    )
  }

  // 5. Pick Recipe for Slot Dialog
  val slotToPick = viewModel.showRecipePickerForPlanSlot.collectAsState().value
  if (slotToPick != null) {
    val (day, slot) = slotToPick
    RecipePickerForSlotDialog(
      language = language,
      recipes = viewModel.repository.getRecipes(),
      slot = slot,
      onRecipeSelected = { recipeId ->
        viewModel.assignRecipeToMealPlan(day, slot, recipeId)
      },
      onDismiss = { viewModel.showRecipePickerForPlanSlot.value = null }
    )
  }
}
