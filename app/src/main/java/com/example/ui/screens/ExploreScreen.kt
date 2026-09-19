package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RecipeCategory
import com.example.ui.components.EmptyStateView
import com.example.ui.components.RecipeCard
import com.example.ui.i18n.AppStrings
import com.example.ui.i18n.Language
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val language by viewModel.currentLanguage.collectAsState()
  val query by viewModel.searchQuery.collectAsState()
  val filters by viewModel.filterState.collectAsState()
  val recipes by viewModel.exploreRecipes.collectAsState()
  val favoriteIds by viewModel.favoriteRecipeIds.collectAsState()

  val hasActiveFilters = filters.category != null ||
    filters.maxCarbs != null ||
    filters.maxCalories != null ||
    filters.maxPrepTime != null ||
    filters.difficulty != null ||
    filters.dietaryPreference != null

  val allCategories = RecipeCategory.values().toList()

  Column(
    modifier = modifier
      .fillMaxSize()
      .testTag("explore_screen")
  ) {
    // Top Bar: Search Input & Filter Button
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      OutlinedTextField(
        value = query,
        onValueChange = { viewModel.searchQuery.value = it },
        placeholder = {
          Text(
            text = AppStrings.searchPlaceholder(language),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        },
        leadingIcon = {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = MaterialTheme.colorScheme.primary
          )
        },
        trailingIcon = {
          if (query.isNotEmpty()) {
            IconButton(onClick = { viewModel.searchQuery.value = "" }) {
              Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = "Clear search",
                tint = MaterialTheme.colorScheme.outline
              )
            }
          }
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = MaterialTheme.colorScheme.surface,
          unfocusedContainerColor = MaterialTheme.colorScheme.surface,
          focusedBorderColor = MaterialTheme.colorScheme.primary,
          unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        ),
        modifier = Modifier
          .weight(1f)
          .testTag("search_input")
      )

      // Filter Action Button
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (hasActiveFilters) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        modifier = Modifier
          .size(52.dp)
          .clickable { viewModel.showFilterSheet.value = true }
          .testTag("filter_button")
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Default.FilterList,
            contentDescription = AppStrings.filters(language),
            tint = if (hasActiveFilters) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
          )
        }
      }
    }

    // Horizontal Scrollable Category Chips
    LazyRow(
      contentPadding = PaddingValues(horizontal = 20.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.padding(bottom = 12.dp)
    ) {
      // "All" chip
      item {
        FilterChip(
          selected = filters.category == null,
          onClick = { viewModel.filterByCategory(null) },
          label = { Text(AppStrings.filterAll(language)) },
          shape = RoundedCornerShape(12.dp)
        )
      }

      items(allCategories) { category ->
        FilterChip(
          selected = filters.category == category,
          onClick = {
            viewModel.filterByCategory(if (filters.category == category) null else category)
          },
          label = { Text(category.localizedName(language)) },
          shape = RoundedCornerShape(12.dp)
        )
      }
    }

    // Results Header Count
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      val countText = if (language == Language.SPANISH) {
        "${recipes.size} recetas disponibles"
      } else {
        "${recipes.size} recipes available"
      }
      Text(
        text = countText,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      if (hasActiveFilters || query.isNotEmpty()) {
        Text(
          text = AppStrings.resetFilters(language),
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary,
          modifier = Modifier.clickable { viewModel.resetFilters() }
        )
      }
    }

    // Recipe List / Grid
    if (recipes.isEmpty()) {
      EmptyStateView(
        icon = Icons.Outlined.SearchOff,
        title = AppStrings.noSearchResults(language),
        description = AppStrings.noSearchResultsDesc(language),
        actionText = AppStrings.clearSearch(language),
        onActionClick = { viewModel.resetFilters() }
      )
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        items(recipes, key = { it.id }) { recipe ->
          RecipeCard(
            recipe = recipe,
            isFavorite = favoriteIds.contains(recipe.id),
            language = language,
            onRecipeClick = { viewModel.openRecipeDetail(recipe) },
            onFavoriteClick = { viewModel.toggleFavorite(recipe.id) }
          )
        }
      }
    }
  }
}
