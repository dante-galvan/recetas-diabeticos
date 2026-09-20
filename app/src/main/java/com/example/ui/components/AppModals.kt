package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MealSlot
import com.example.data.model.Recipe
import com.example.ui.i18n.AppStrings
import com.example.ui.i18n.Language
import com.example.ui.theme.CarbAccent

@Composable
fun AddToPlanDialog(
  language: Language,
  onConfirm: (dayOfWeek: Int, slot: MealSlot) -> Unit,
  onDismiss: () -> Unit
) {
  var selectedDay by remember { mutableStateOf(1) }
  var selectedSlot by remember { mutableStateOf(MealSlot.LUNCH) }

  val days = listOf(
    1 to AppStrings.dayMonday(language),
    2 to AppStrings.dayTuesday(language),
    3 to AppStrings.dayWednesday(language),
    4 to AppStrings.dayThursday(language),
    5 to AppStrings.dayFriday(language),
    6 to AppStrings.daySaturday(language),
    7 to AppStrings.daySunday(language)
  )

  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(24.dp),
    title = {
      Text(
        text = AppStrings.addToPlan(language),
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
      )
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Day of Week
        Text(
          text = if (language == Language.SPANISH) "Día de la semana" else "Day of week",
          style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.primary
        )

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          days.chunked(4).forEach { rowDays ->
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              rowDays.forEach { (num, name) ->
                FilterChip(
                  selected = selectedDay == num,
                  onClick = { selectedDay = num },
                  label = { Text(name, fontSize = 11.sp) },
                  modifier = Modifier.weight(1f)
                )
              }
            }
          }
        }

        // Meal Slot
        Text(
          text = if (language == Language.SPANISH) "Momento de comida" else "Meal slot",
          style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.primary
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          MealSlot.values().forEach { slot ->
            FilterChip(
              selected = selectedSlot == slot,
              onClick = { selectedSlot = slot },
              label = { Text(slot.localizedName(language), fontSize = 11.sp) },
              modifier = Modifier.weight(1f)
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = { onConfirm(selectedDay, selectedSlot) },
        shape = RoundedCornerShape(12.dp)
      ) {
        Text(text = AppStrings.confirm(language), fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(text = AppStrings.cancel(language))
      }
    }
  )
}

@Composable
fun NewCollectionDialog(
  language: Language,
  onConfirm: (name: String) -> Unit,
  onDismiss: () -> Unit
) {
  var name by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(24.dp),
    title = {
      Text(
        text = AppStrings.newCollection(language),
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
      )
    },
    text = {
      OutlinedTextField(
        value = name,
        onValueChange = { name = it },
        placeholder = { Text(AppStrings.collectionName(language)) },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
      )
    },
    confirmButton = {
      Button(
        onClick = { onConfirm(name) },
        enabled = name.isNotBlank(),
        shape = RoundedCornerShape(12.dp)
      ) {
        Text(text = AppStrings.confirm(language), fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(text = AppStrings.cancel(language))
      }
    }
  )
}

@Composable
fun RecipePickerForSlotDialog(
  language: Language,
  recipes: List<Recipe>,
  slot: MealSlot,
  onRecipeSelected: (String) -> Unit,
  onDismiss: () -> Unit
) {
  var filterQuery by remember { mutableStateOf("") }
  val filteredRecipes = if (filterQuery.isBlank()) recipes else {
    recipes.filter {
      it.titleEs.contains(filterQuery, ignoreCase = true) ||
        it.titleEn.contains(filterQuery, ignoreCase = true)
    }
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(24.dp),
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "${AppStrings.addRecipeToSlot(language)} (${slot.localizedName(language)})",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
          Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .height(400.dp)
      ) {
        OutlinedTextField(
          value = filterQuery,
          onValueChange = { filterQuery = it },
          placeholder = { Text(AppStrings.searchPlaceholder(language), fontSize = 12.sp) },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
        )

        LazyColumn(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(filteredRecipes) { recipe ->
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .clickable { onRecipeSelected(recipe.id) },
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              border = CardDefaults.outlinedCardBorder()
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Image(
                  painter = painterResource(id = recipe.imageRes),
                  contentDescription = recipe.localizedTitle(language),
                  modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp)),
                  contentScale = ContentScale.Crop
                )

                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = recipe.localizedTitle(language),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                  )
                  Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                      text = "${recipe.nutrition.totalCarbsGrams.toInt()}g carb",
                      fontSize = 11.sp,
                      color = CarbAccent,
                      fontWeight = FontWeight.Bold
                    )
                    Text(
                      text = "${recipe.nutrition.calories} kcal",
                      fontSize = 11.sp,
                      color = MaterialTheme.colorScheme.outline
                    )
                  }
                }
              }
            }
          }
        }
      }
    },
    confirmButton = {}
  )
}
