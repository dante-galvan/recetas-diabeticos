package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Recipe
import com.example.data.model.RecipeCollection
import com.example.ui.components.EmptyStateView
import com.example.ui.components.RecipeCard
import com.example.ui.i18n.AppStrings
import com.example.ui.i18n.Language
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel

@Composable
fun FavoritesScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val language by viewModel.currentLanguage.collectAsState()
  val favorites by viewModel.favoriteRecipes.collectAsState()
  val collections by viewModel.collections.collectAsState()
  val selectedCollectionId by viewModel.selectedCollectionId.collectAsState()
  val allRecipes = viewModel.repository.getRecipes()

  // If a collection is selected, show recipes from that collection; otherwise show all favorites
  val displayedRecipes: List<Recipe> = if (selectedCollectionId == null) {
    favorites
  } else {
    val selectedCol = collections.find { it.id == selectedCollectionId }
    val ids = selectedCol?.recipeIds ?: emptySet()
    allRecipes.filter { ids.contains(it.id) }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .testTag("favorites_screen")
  ) {
    // Header
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
      Text(
        text = AppStrings.favorites(language),
        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onBackground
      )

      Spacer(modifier = Modifier.height(2.dp))

      Text(
        text = AppStrings.favoritesSubtitle(language),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    // Collections Horizontal Row
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = AppStrings.myCollections(language),
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )

      TextButton(onClick = { viewModel.showNewCollectionDialog.value = true }) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Text(text = AppStrings.newCollection(language), fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    LazyRow(
      contentPadding = PaddingValues(horizontal = 20.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.padding(bottom = 12.dp)
    ) {
      // "All Favorites" chip
      item {
        FilterChip(
          selected = selectedCollectionId == null,
          onClick = { viewModel.selectedCollectionId.value = null },
          label = { Text(AppStrings.allFavorites(language)) },
          shape = RoundedCornerShape(12.dp)
        )
      }

      items(collections) { col ->
        FilterChip(
          selected = selectedCollectionId == col.id,
          onClick = {
            viewModel.selectedCollectionId.value = if (selectedCollectionId == col.id) null else col.id
          },
          label = { Text("${col.localizedName(language)} (${col.recipeIds.size})") },
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.Folder,
              contentDescription = null,
              modifier = Modifier.size(16.dp)
            )
          },
          shape = RoundedCornerShape(12.dp)
        )
      }
    }

    // Recipe List or Empty State
    if (displayedRecipes.isEmpty()) {
      EmptyStateView(
        icon = Icons.Outlined.FavoriteBorder,
        title = AppStrings.noFavoritesYet(language),
        description = AppStrings.noFavoritesDesc(language),
        actionText = AppStrings.discoverRecipes(language),
        onActionClick = { viewModel.navigateTo(AppScreen.EXPLORE) }
      )
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        items(displayedRecipes, key = { it.id }) { recipe ->
          RecipeCard(
            recipe = recipe,
            isFavorite = true,
            language = language,
            onRecipeClick = { viewModel.openRecipeDetail(recipe) },
            onFavoriteClick = { viewModel.toggleFavorite(recipe.id) }
          )
        }
      }
    }
  }
}
