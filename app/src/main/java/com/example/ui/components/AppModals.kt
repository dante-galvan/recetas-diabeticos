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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Folder
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MealSlot
import com.example.data.model.Recipe
import com.example.data.model.RecipeCollection
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
    1 to AppStrings.dayMonday(),
    2 to AppStrings.dayTuesday(),
    3 to AppStrings.dayWednesday(),
    4 to AppStrings.dayThursday(),
    5 to AppStrings.dayFriday(),
    6 to AppStrings.daySaturday(),
    7 to AppStrings.daySunday()
  )

  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(24.dp),
    title = {
      Text(
        text = AppStrings.addToPlan(),
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
          text = AppStrings.dayOfWeek(),
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
          text = AppStrings.mealSlot(),
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
        Text(text = AppStrings.confirm(), fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(text = AppStrings.cancel())
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
        text = AppStrings.newCollection(),
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
      )
    },
    text = {
      OutlinedTextField(
        value = name,
        onValueChange = { name = it },
        placeholder = { Text(AppStrings.collectionNamePrompt()) },
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
        Text(text = AppStrings.confirm(), fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(text = AppStrings.cancel())
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
          text = "${AppStrings.addRecipeToSlot()} (${slot.localizedName(language)})",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
          Icon(imageVector = Icons.Default.Close, contentDescription = AppStrings.cdClose())
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
          placeholder = { Text(AppStrings.searchPlaceholder(), fontSize = 12.sp) },
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
                      text = "${recipe.nutrition.totalCarbsGrams.toInt()}g ${AppStrings.totalCarbsShort()}",
                      fontSize = 11.sp,
                      color = CarbAccent,
                      fontWeight = FontWeight.Bold
                    )
                    Text(
                      text = "${recipe.nutrition.calories} ${AppStrings.kcal()}",
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

/**
 * Lets the user pick (or create) a personal collection to save [recipeId] into.
 * Collections that already contain the recipe are marked so duplicates are obvious.
 */
@Composable
fun SaveToCollectionDialog(
  language: Language,
  recipeId: String,
  collections: List<RecipeCollection>,
  onSelectCollection: (String) -> Unit,
  onCreateCollection: (String) -> Unit,
  onDismiss: () -> Unit
) {
  var showCreate by remember { mutableStateOf(collections.isEmpty()) }
  var newName by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(24.dp),
    title = {
      Text(
        text = AppStrings.saveToCollection(),
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
      )
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        if (collections.isEmpty()) {
          Text(
            text = AppStrings.noCollectionsDesc(),
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        collections.forEach { col ->
          val alreadySaved = recipeId in col.recipeIds
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .testTag("save_to_collection_${col.id}")
              .clickable { onSelectCollection(col.id) },
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = CardDefaults.outlinedCardBorder()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Folder,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
              )
              Text(
                text = col.localizedName(language),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
              )
              if (alreadySaved) {
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = AppStrings.alreadySaved(),
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(18.dp)
                )
              }
            }
          }
        }

        if (showCreate) {
          OutlinedTextField(
            value = newName,
            onValueChange = { newName = it },
            placeholder = { Text(AppStrings.collectionNamePrompt(), fontSize = 13.sp) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("new_collection_name_field")
          )
          Button(
            onClick = { onCreateCollection(newName) },
            enabled = newName.isNotBlank(),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("create_collection_confirm")
          ) {
            Text(text = AppStrings.createCollection(), fontWeight = FontWeight.Bold)
          }
        } else {
          TextButton(onClick = { showCreate = true }) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
              Text(text = AppStrings.newCollection(), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    },
    confirmButton = {},
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(text = AppStrings.close())
      }
    }
  )
}

/** Reusable confirmation dialog used for destructive actions. */
@Composable
fun ConfirmDialog(
  title: String,
  message: String,
  confirmText: String,
  cancelText: String,
  onConfirm: () -> Unit,
  onDismiss: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(24.dp),
    title = {
      Text(
        text = title,
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
      )
    },
    text = { Text(text = message) },
    confirmButton = {
      Button(
        onClick = onConfirm,
        shape = RoundedCornerShape(12.dp)
      ) {
        Text(text = confirmText, fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(text = cancelText)
      }
    }
  )
}
