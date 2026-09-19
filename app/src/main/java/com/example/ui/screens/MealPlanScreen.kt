package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MealPlanItem
import com.example.data.model.MealSlot
import com.example.data.model.Recipe
import com.example.ui.i18n.AppStrings
import com.example.ui.i18n.Language
import com.example.ui.theme.CarbAccent
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel

@Composable
fun MealPlanScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val language by viewModel.currentLanguage.collectAsState()
  val selectedDay by viewModel.selectedDayOfWeek.collectAsState()
  val mealPlanItems by viewModel.mealPlanItems.collectAsState()

  val days = listOf(
    1 to AppStrings.dayMonday(language),
    2 to AppStrings.dayTuesday(language),
    3 to AppStrings.dayWednesday(language),
    4 to AppStrings.dayThursday(language),
    5 to AppStrings.dayFriday(language),
    6 to AppStrings.daySaturday(language),
    7 to AppStrings.daySunday(language)
  )

  val dayMeals = mealPlanItems.filter { it.dayOfWeek == selectedDay }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("meal_plan_screen"),
    contentPadding = PaddingValues(bottom = 90.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header & "Generate Menu" CTA
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 10.dp)
      ) {
        Text(
          text = AppStrings.weeklyMealPlanner(language),
          style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = AppStrings.generateMenuDesc(language),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Prominent Friendly Generate Button & Shopping List Shortcut
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Button(
            onClick = {
              viewModel.generateWeeklyMenu(
                if (language == Language.SPANISH) "¡Menú semanal equilibrado generado!" else "Balanced weekly menu generated!"
              )
            },
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier
              .weight(1.3f)
              .height(48.dp)
              .testTag("generate_menu_button")
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = AppStrings.generateWeeklyMenu(language),
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
          }

          OutlinedButton(
            onClick = { viewModel.navigateTo(AppScreen.SHOPPING_LIST) },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .testTag("open_shopping_list_button")
          ) {
            Icon(
              imageVector = Icons.Default.ShoppingCart,
              contentDescription = null,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = AppStrings.shoppingList(language),
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }
    }

    // Days Tabs Selector (Monday to Sunday)
    item {
      LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(days) { (dayNum, dayName) ->
          val isSelected = selectedDay == dayNum
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
            border = if (isSelected) null else CardDefaults.outlinedCardBorder(),
            modifier = Modifier
              .clickable { viewModel.selectedDayOfWeek.value = dayNum }
              .testTag("day_tab_$dayNum")
          ) {
            Column(
              modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = dayName,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }
      }
    }

    // 4 Meal Slots: Breakfast, Lunch, Dinner, Snack
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        MealSlot.values().forEach { slot ->
          val plannedItem = dayMeals.find { it.slot == slot }

          MealSlotCard(
            slot = slot,
            plannedItem = plannedItem,
            language = language,
            onRecipeClick = { plannedItem?.recipe?.let { viewModel.openRecipeDetail(it) } },
            onAddClick = { viewModel.showRecipePickerForPlanSlot.value = Pair(selectedDay, slot) },
            onChangeClick = { viewModel.showRecipePickerForPlanSlot.value = Pair(selectedDay, slot) },
            onRemoveClick = { plannedItem?.let { viewModel.removeMealPlanItem(it.id) } }
          )
        }
      }
    }
  }
}

@Composable
private fun MealSlotCard(
  slot: MealSlot,
  plannedItem: MealPlanItem?,
  language: Language,
  onRecipeClick: () -> Unit,
  onAddClick: () -> Unit,
  onChangeClick: () -> Unit,
  onRemoveClick: () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    border = CardDefaults.outlinedCardBorder()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Slot Header (e.g. Desayuno / Breakfast)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = slot.localizedName(language),
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary
        )

        if (plannedItem != null) {
          Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            IconButton(onClick = onChangeClick, modifier = Modifier.size(32.dp)) {
              Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Change",
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(16.dp)
              )
            }
            IconButton(onClick = onRemoveClick, modifier = Modifier.size(32.dp)) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(16.dp)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      if (plannedItem != null) {
        val recipe = plannedItem.recipe
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onRecipeClick),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Image(
            painter = painterResource(id = recipe.imageRes),
            contentDescription = recipe.localizedTitle(language),
            modifier = Modifier
              .size(65.dp)
              .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
          )

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = recipe.localizedTitle(language),
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              // Carbs highlight
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = CarbAccent.copy(alpha = 0.15f)
              ) {
                Text(
                  text = "${recipe.nutrition.totalCarbsGrams.toInt()}g ${AppStrings.totalCarbsShort(language)}",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = CarbAccent,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }

              Text(
                text = "${recipe.nutrition.calories} ${AppStrings.kcal(language)}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      } else {
        // Empty slot placeholder
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onAddClick),
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ) {
          Row(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = AppStrings.addRecipeToSlot(language),
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.primary
            )
          }
        }
      }
    }
  }
}
