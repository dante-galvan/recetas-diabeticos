package com.example

import android.app.Application
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.MealSlot
import com.example.ui.screens.MealPlanScreen
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper

/**
 * Guards the "generate weekly menu" UI wiring end to end: the button must react,
 * and the replace-confirmation dialog must actually reach the screen.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "en")
class MealPlanUiTest {

  @get:Rule
  val composeRule = createComposeRule()

  @Test
  fun generateButton_asksForConfirmation_whenAPlanAlreadyExists() {
    val application = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = MainViewModel(application)

    // Persist a plan first so the screen treats it as "already there".
    val recipeId = viewModel.repository.getRecipes().first().id
    runBlocking { viewModel.repository.setMealPlanSlot(1, MealSlot.BREAKFAST, recipeId) }

    composeRule.setContent { MealPlanScreen(viewModel = viewModel) }

    // Pump the main looper until Room's plan reaches the ViewModel/UI, without
    // blocking the main thread (which would deadlock the flow collection).
    val deadline = System.currentTimeMillis() + 5_000
    while (viewModel.mealPlanItems.value.isEmpty() && System.currentTimeMillis() < deadline) {
      ShadowLooper.idleMainLooper()
      composeRule.waitForIdle()
    }
    assertTrue("The persisted plan should reach the UI", viewModel.mealPlanItems.value.isNotEmpty())

    composeRule.onNodeWithTag("generate_menu_button").performClick()

    // Regression guard: the dialog used to live in an off-screen LazyColumn item
    // and therefore never composed, making the button look broken.
    composeRule.onNodeWithText("Replace current menu?").assertIsDisplayed()
  }
}
