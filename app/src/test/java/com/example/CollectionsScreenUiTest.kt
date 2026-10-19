package com.example

import android.app.Application
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.DefaultCollections
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.RecipeDetailScreen
import com.example.ui.viewmodel.MainViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper

/**
 * Drives the real screen against the real ViewModel/database so it is proven
 * that the built-in collections reach the UI and the save action is connected
 * from both the card and the detail screen.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "en")
class CollectionsScreenUiTest {

  @get:Rule
  val composeRule = createComposeRule()

  private fun newViewModel(): MainViewModel =
    MainViewModel(ApplicationProvider.getApplicationContext<Application>())

  /** Pumps the main looper until Room's defaults reach the ViewModel. */
  private fun awaitCollections(viewModel: MainViewModel) {
    val deadline = System.currentTimeMillis() + 5_000
    while (viewModel.collections.value.isEmpty() && System.currentTimeMillis() < deadline) {
      ShadowLooper.idleMainLooper()
      composeRule.waitForIdle()
    }
    assertTrue("Default collections should reach the UI", viewModel.collections.value.isNotEmpty())
  }

  @Test
  fun favoritesScreen_listsDefaultCollections_withoutCreatingThem() {
    val viewModel = newViewModel()

    composeRule.setContent { FavoritesScreen(viewModel = viewModel) }
    awaitCollections(viewModel)

    composeRule.onNodeWithText("Breakfast (0)").assertIsDisplayed()
    composeRule.onNodeWithText("Dinner (0)").assertIsDisplayed()
  }

  @Test
  fun openingAnEmptyCollection_showsAUsefulEmptyState() {
    val viewModel = newViewModel()

    composeRule.setContent { FavoritesScreen(viewModel = viewModel) }
    awaitCollections(viewModel)

    viewModel.selectedCollectionId.value = DefaultCollections.BREAKFAST_ID
    composeRule.waitForIdle()

    composeRule.onNodeWithText("This collection is empty").assertIsDisplayed()
    // The empty state offers an action to go add recipes.
    composeRule.onNodeWithTag("add_recipes_to_collection_button").assertIsDisplayed()
  }

  @Test
  fun togglingMembership_updatesTheOpenCollection_live() {
    val viewModel = newViewModel()

    composeRule.setContent { FavoritesScreen(viewModel = viewModel) }
    awaitCollections(viewModel)

    viewModel.selectedCollectionId.value = DefaultCollections.BREAKFAST_ID
    composeRule.waitForIdle()
    composeRule.onNodeWithText("This collection is empty").assertIsDisplayed()

    val recipe = viewModel.repository.getRecipes().first()

    // Add: the open collection must show the recipe without leaving the screen.
    viewModel.toggleRecipeInCollection(DefaultCollections.BREAKFAST_ID, recipe.id, true)
    awaitRecipes(viewModel, present = true)
    composeRule.onNodeWithText(recipe.titleEn).assertIsDisplayed()
    composeRule.onNodeWithText("This collection is empty").assertDoesNotExist()

    // Remove: pressing again must take it back out, again live.
    viewModel.toggleRecipeInCollection(DefaultCollections.BREAKFAST_ID, recipe.id, false)
    awaitRecipes(viewModel, present = false)
    composeRule.onNodeWithText(recipe.titleEn).assertDoesNotExist()
    composeRule.onNodeWithText("This collection is empty").assertIsDisplayed()
  }

  private fun awaitRecipes(viewModel: MainViewModel, present: Boolean) {
    val deadline = System.currentTimeMillis() + 5_000
    while (viewModel.selectedCollectionRecipes.value.isNotEmpty() != present &&
      System.currentTimeMillis() < deadline
    ) {
      ShadowLooper.idleMainLooper()
      composeRule.waitForIdle()
    }
    assertEquals(present, viewModel.selectedCollectionRecipes.value.isNotEmpty())
  }

  @Test
  fun detailScreen_saveButton_opensTheCollectionPicker() {
    val viewModel = newViewModel()
    val recipe = viewModel.repository.getRecipes().first()
    viewModel.openRecipeDetail(recipe)

    composeRule.setContent { RecipeDetailScreen(viewModel = viewModel) }
    composeRule.waitForIdle()

    composeRule.onNodeWithTag("detail_save_button").performClick()
    composeRule.waitForIdle()

    assertEquals(recipe.id, viewModel.saveToCollectionFor.value?.id)
  }
}
