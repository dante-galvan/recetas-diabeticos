package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.RecipeJsonLoader
import com.example.data.model.RecipeCollection
import com.example.ui.components.RecipeCard
import com.example.ui.components.SaveToCollectionDialog
import com.example.ui.i18n.Language
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Verifies that the collection actions are actually rendered and wired to their
 * callbacks, so they cannot silently become decorative buttons.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CollectionUiTest {

  @get:Rule
  val composeRule = createComposeRule()

  private val context: Context = ApplicationProvider.getApplicationContext()

  @Test
  fun saveDialog_listsCollections_andSelectingOneSaves() {
    var selected: String? = null
    composeRule.setContent {
      SaveToCollectionDialog(
        language = Language.ENGLISH,
        recipeId = "recipe_1",
        collections = listOf(
          RecipeCollection("c1", "Desayunos", "Breakfasts"),
          RecipeCollection("c2", "Cenas", "Dinners", recipeIds = setOf("recipe_1"))
        ),
        onSelectCollection = { selected = it },
        onCreateCollection = {},
        onDismiss = {}
      )
    }

    composeRule.onNodeWithTag("save_to_collection_c1").assertIsDisplayed().performClick()

    assertEquals("c1", selected)
  }

  @Test
  fun saveDialog_withNoCollections_letsUserCreateOne() {
    var createdName: String? = null
    composeRule.setContent {
      SaveToCollectionDialog(
        language = Language.ENGLISH,
        recipeId = "recipe_1",
        collections = emptyList(),
        onSelectCollection = {},
        onCreateCollection = { createdName = it },
        onDismiss = {}
      )
    }

    composeRule.onNodeWithTag("new_collection_name_field").performTextInput("My list")
    composeRule.onNodeWithTag("create_collection_confirm").performClick()

    assertEquals("My list", createdName)
  }

  @Test
  fun recipeCard_exposesWorkingSaveToCollectionButton() {
    val recipe = RecipeJsonLoader.loadRecipes(context).first()
    var saveClicked = false

    composeRule.setContent {
      RecipeCard(
        recipe = recipe,
        isFavorite = false,
        isSaved = false,
        language = Language.ENGLISH,
        onRecipeClick = {},
        onFavoriteClick = {},
        onSaveToCollectionClick = { saveClicked = true }
      )
    }

    composeRule.onNodeWithTag("save_btn_${recipe.id}").assertIsDisplayed().performClick()

    assertTrue("Save-to-collection must be wired", saveClicked)
  }

  @Test
  fun recipeCard_showsRemoveAction_whenInsideACollection() {
    val recipe = RecipeJsonLoader.loadRecipes(context).first()
    var removed = false

    composeRule.setContent {
      RecipeCard(
        recipe = recipe,
        isFavorite = false,
        isSaved = true,
        language = Language.ENGLISH,
        onRecipeClick = {},
        onFavoriteClick = {},
        onSaveToCollectionClick = {},
        onRemoveFromCollection = { removed = true }
      )
    }

    composeRule.onNodeWithTag("remove_from_collection_${recipe.id}").assertIsDisplayed().performClick()

    assertTrue("Remove-from-collection must be wired", removed)
  }
}
