package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DefaultCollections
import com.example.data.model.RecipeCollection
import com.example.ui.components.ConfirmDialog
import com.example.ui.components.EmptyStateView
import com.example.ui.components.RecipeCard
import com.example.ui.i18n.AppStrings
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel

@Composable
fun FavoritesScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val language by viewModel.currentLanguage.collectAsState()
  val favorites by viewModel.favoriteRecipes.collectAsState()
  val favoriteIds by viewModel.favoriteRecipeIds.collectAsState()
  val collections by viewModel.collections.collectAsState()
  val selectedCollectionId by viewModel.selectedCollectionId.collectAsState()
  val collectionRecipes by viewModel.selectedCollectionRecipes.collectAsState()
  val savedRecipeIds by viewModel.savedRecipeIds.collectAsState()

  val selectedCollection = collections.find { it.id == selectedCollectionId }

  // Favorites and collections are independent: with no collection selected we
  // show favourites, otherwise we show the recipes stored in that collection.
  val displayedRecipes = if (selectedCollection == null) favorites else collectionRecipes

  var collectionToDelete by remember { mutableStateOf<RecipeCollection?>(null) }

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
        text = AppStrings.myFavorites(),
        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onBackground
      )

      Spacer(modifier = Modifier.height(2.dp))

      Text(
        text = AppStrings.favoritesSubtitle(),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    // Collections header + create action
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = AppStrings.collections(),
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )

      TextButton(
        onClick = { viewModel.showNewCollectionDialog.value = true },
        modifier = Modifier.testTag("new_collection_button")
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Text(text = AppStrings.newCollection(), fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    if (collections.isEmpty()) {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 4.dp)
          .testTag("no_collections_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        border = CardDefaults.outlinedCardBorder()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = AppStrings.noCollectionsYet(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = AppStrings.noCollectionsDesc(),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
      Spacer(modifier = Modifier.height(8.dp))
    } else {
      LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(bottom = 8.dp)
      ) {
        // "All Favorites" chip
        item {
          FilterChip(
            selected = selectedCollectionId == null,
            onClick = { viewModel.selectedCollectionId.value = null },
            label = { Text(AppStrings.allFavorites()) },
            shape = RoundedCornerShape(12.dp)
          )
        }

        items(collections, key = { it.id }) { col ->
          FilterChip(
            selected = selectedCollectionId == col.id,
            onClick = {
              viewModel.selectedCollectionId.value = if (selectedCollectionId == col.id) null else col.id
            },
            label = { Text("${AppStrings.collectionName(col, language)} (${col.recipeIds.size})") },
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
    }

    // Selected collection title + delete action
    if (selectedCollection != null) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = 20.dp, end = 12.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = AppStrings.collectionName(selectedCollection, language),
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onBackground
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
          // Evident way to add recipes to the currently open collection.
          TextButton(
            onClick = { viewModel.navigateTo(AppScreen.EXPLORE) },
            modifier = Modifier.testTag("add_recipes_to_collection_button")
          ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Text(text = AppStrings.discoverRecipes(), fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
          // Built-in collections are permanent, so only user collections can be
          // deleted (deleting a default would just be recreated on next launch).
          if (!DefaultCollections.isDefault(selectedCollection.id)) {
            IconButton(
              onClick = { collectionToDelete = selectedCollection },
              modifier = Modifier.testTag("delete_collection_button")
            ) {
              Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = AppStrings.deleteCollection(),
                tint = MaterialTheme.colorScheme.error
              )
            }
          }
        }
      }
    }

    // Recipe List or Empty State
    if (displayedRecipes.isEmpty()) {
      if (selectedCollection != null) {
        EmptyStateView(
          icon = Icons.Outlined.BookmarkBorder,
          title = AppStrings.emptyCollection(),
          description = AppStrings.emptyCollectionDesc(),
          actionText = AppStrings.discoverRecipes(),
          onActionClick = { viewModel.navigateTo(AppScreen.EXPLORE) }
        )
      } else {
        EmptyStateView(
          icon = Icons.Outlined.FavoriteBorder,
          title = AppStrings.noFavoritesYet(),
          description = AppStrings.noFavoritesDesc(),
          actionText = AppStrings.discoverRecipes(),
          onActionClick = { viewModel.navigateTo(AppScreen.EXPLORE) }
        )
      }
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        items(displayedRecipes, key = { it.id }) { recipe ->
          RecipeCard(
            recipe = recipe,
            isFavorite = favoriteIds.contains(recipe.id),
            isSaved = savedRecipeIds.contains(recipe.id),
            language = language,
            onRecipeClick = { viewModel.openRecipeDetail(recipe) },
            onFavoriteClick = { viewModel.toggleFavorite(recipe.id) },
            onSaveToCollectionClick = { viewModel.requestSaveToCollection(recipe) },
            onRemoveFromCollection = selectedCollection?.let { col ->
              { viewModel.removeRecipeFromCollection(col.id, recipe.id) }
            }
          )
        }
      }
    }
  }

  // Delete-collection confirmation
  collectionToDelete?.let { col ->
    ConfirmDialog(
      title = AppStrings.deleteCollectionTitle(),
      message = AppStrings.deleteCollectionMessage(),
      confirmText = AppStrings.delete(),
      cancelText = AppStrings.cancel(),
      onConfirm = {
        viewModel.deleteCollection(col.id)
        collectionToDelete = null
      },
      onDismiss = { collectionToDelete = null }
    )
  }
}
