package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Difficulty
import com.example.data.model.RecipeCategory
import com.example.data.repository.SortOption
import com.example.ui.i18n.AppStrings
import com.example.ui.i18n.Language
import com.example.ui.viewmodel.FilterState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
  sheetState: SheetState,
  currentFilters: FilterState,
  language: Language,
  onApplyFilters: (FilterState) -> Unit,
  onResetFilters: () -> Unit,
  onDismiss: () -> Unit
) {
  var tempCategory by remember { mutableStateOf(currentFilters.category) }
  var tempMaxCarbs by remember { mutableStateOf(currentFilters.maxCarbs) }
  var tempMaxTime by remember { mutableStateOf(currentFilters.maxPrepTime) }
  var tempDifficulty by remember { mutableStateOf(currentFilters.difficulty) }
  var tempDietary by remember { mutableStateOf(currentFilters.dietaryPreference) }
  var tempSortBy by remember { mutableStateOf(currentFilters.sortBy) }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    containerColor = MaterialTheme.colorScheme.surface
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
        .verticalScroll(rememberScrollState())
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = AppStrings.filters(language),
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )

        OutlinedButton(
          onClick = {
            tempCategory = null
            tempMaxCarbs = null
            tempMaxTime = null
            tempDifficulty = null
            tempDietary = null
            tempSortBy = SortOption.RELEVANCE
            onResetFilters()
          },
          shape = RoundedCornerShape(8.dp)
        ) {
          Text(text = AppStrings.resetFilters(language), fontSize = 12.sp)
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Sorting
      Text(
        text = AppStrings.sortOrder(language),
        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.primary
      )
      Spacer(modifier = Modifier.height(8.dp))
      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        FilterChip(
          selected = tempSortBy == SortOption.RELEVANCE,
          onClick = { tempSortBy = SortOption.RELEVANCE },
          label = { Text("Normal / Top") }
        )
        FilterChip(
          selected = tempSortBy == SortOption.LOWEST_CARBS,
          onClick = { tempSortBy = SortOption.LOWEST_CARBS },
          label = { Text(AppStrings.sortLowestCarbs(language)) }
        )
        FilterChip(
          selected = tempSortBy == SortOption.QUICKEST,
          onClick = { tempSortBy = SortOption.QUICKEST },
          label = { Text(AppStrings.sortQuickest(language)) }
        )
        FilterChip(
          selected = tempSortBy == SortOption.LOWEST_CALORIES,
          onClick = { tempSortBy = SortOption.LOWEST_CALORIES },
          label = { Text(AppStrings.sortLowestCalories(language)) }
        )
        FilterChip(
          selected = tempSortBy == SortOption.HIGHEST_PROTEIN,
          onClick = { tempSortBy = SortOption.HIGHEST_PROTEIN },
          label = { Text(AppStrings.sortHighestProtein(language)) }
        )
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Maximum Total Carbohydrates
      Text(
        text = AppStrings.filterMaxCarbs(language),
        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.primary
      )
      Spacer(modifier = Modifier.height(8.dp))
      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        listOf(
          null to AppStrings.filterAll(language),
          10.0 to "≤ 10g",
          15.0 to "≤ 15g",
          25.0 to "≤ 25g",
          35.0 to "≤ 35g"
        ).forEach { (value, label) ->
          FilterChip(
            selected = tempMaxCarbs == value,
            onClick = { tempMaxCarbs = value },
            label = { Text(label) }
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Max Prep Time
      Text(
        text = AppStrings.filterMaxTime(language),
        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.primary
      )
      Spacer(modifier = Modifier.height(8.dp))
      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        listOf(
          null to AppStrings.filterAll(language),
          15 to "≤ 15 ${AppStrings.min(language)}",
          25 to "≤ 25 ${AppStrings.min(language)}",
          40 to "≤ 40 ${AppStrings.min(language)}"
        ).forEach { (time, label) ->
          FilterChip(
            selected = tempMaxTime == time,
            onClick = { tempMaxTime = time },
            label = { Text(label) }
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Difficulty
      Text(
        text = AppStrings.filterDifficulty(language),
        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.primary
      )
      Spacer(modifier = Modifier.height(8.dp))
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(
          selected = tempDifficulty == null,
          onClick = { tempDifficulty = null },
          label = { Text(AppStrings.filterAll(language)) }
        )
        FilterChip(
          selected = tempDifficulty == Difficulty.EASY,
          onClick = { tempDifficulty = Difficulty.EASY },
          label = { Text(AppStrings.diffEasy(language)) }
        )
        FilterChip(
          selected = tempDifficulty == Difficulty.MEDIUM,
          onClick = { tempDifficulty = Difficulty.MEDIUM },
          label = { Text(AppStrings.diffMedium(language)) }
        )
        FilterChip(
          selected = tempDifficulty == Difficulty.HARD,
          onClick = { tempDifficulty = Difficulty.HARD },
          label = { Text(AppStrings.diffHard(language)) }
        )
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Dietary Preferences
      Text(
        text = AppStrings.filterDietary(language),
        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.primary
      )
      Spacer(modifier = Modifier.height(8.dp))
      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        listOf(
          "Low Carb", "Gluten Free", "Vegetarian", "Vegan", "High Protein", "High Fiber"
        ).forEach { pref ->
          FilterChip(
            selected = tempDietary == pref,
            onClick = { tempDietary = if (tempDietary == pref) null else pref },
            label = { Text(pref) }
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Apply Button
      Button(
        onClick = {
          onApplyFilters(
            FilterState(
              category = tempCategory,
              maxCarbs = tempMaxCarbs,
              maxPrepTime = tempMaxTime,
              difficulty = tempDifficulty,
              dietaryPreference = tempDietary,
              sortBy = tempSortBy
            )
          )
          onDismiss()
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp),
        shape = RoundedCornerShape(14.dp)
      ) {
        Text(
          text = AppStrings.applyFilters(language),
          fontWeight = FontWeight.Bold,
          fontSize = 16.sp
        )
      }

      Spacer(modifier = Modifier.height(30.dp))
    }
  }
}
