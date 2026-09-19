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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GroceryCategory
import com.example.data.model.ShoppingItem
import com.example.ui.components.EmptyStateView
import com.example.ui.i18n.AppStrings
import com.example.ui.i18n.Language
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel

@Composable
fun ShoppingListScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val language by viewModel.currentLanguage.collectAsState()
  val items by viewModel.shoppingItems.collectAsState()

  val groupedItems = items.groupBy { it.category }
  val totalItemsCount = items.size
  val purchasedCount = items.count { it.isChecked }

  Column(
    modifier = modifier
      .fillMaxSize()
      .testTag("shopping_list_screen")
  ) {
    // Top Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { viewModel.navigateTo(AppScreen.PLAN) }) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = MaterialTheme.colorScheme.onSurface
          )
        }
        Text(
          text = AppStrings.shoppingList(language),
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
      }

      if (totalItemsCount > 0) {
        Row {
          if (purchasedCount > 0) {
            IconButton(
              onClick = { viewModel.clearPurchasedShoppingItems() },
              modifier = Modifier.size(38.dp)
            ) {
              Icon(
                imageVector = Icons.Default.DeleteSweep,
                contentDescription = AppStrings.clearPurchased(language),
                tint = MaterialTheme.colorScheme.outline
              )
            }
          }
          IconButton(
            onClick = { viewModel.clearAllShoppingItems() },
            modifier = Modifier.size(38.dp)
          ) {
            Icon(
              imageVector = Icons.Default.ClearAll,
              contentDescription = AppStrings.clearAll(language),
              tint = MaterialTheme.colorScheme.outline
            )
          }
        }
      }
    }

    // Progress Bar / Subheader summary
    if (totalItemsCount > 0) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        val progressText = if (language == Language.SPANISH) {
          "$purchasedCount de $totalItemsCount comprados"
        } else {
          "$purchasedCount of $totalItemsCount purchased"
        }
        Text(
          text = progressText,
          fontSize = 13.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        TextButton(onClick = { viewModel.showAddCustomGroceryDialog.value = true }) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Text(text = AppStrings.addItem(language), fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // Action Row: "Generate from Plan" & "+ Add Custom Item"
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Button(
        onClick = {
          viewModel.generateShoppingListFromPlan(
            if (language == Language.SPANISH) "Ingredientes agregados desde el menú semanal" else "Ingredients added from weekly meal plan"
          )
        },
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.weight(1.3f).height(44.dp)
      ) {
        Icon(
          imageVector = Icons.Default.AutoAwesome,
          contentDescription = null,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = AppStrings.generateFromPlan(language),
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold
        )
      }

      OutlinedButton(
        onClick = { viewModel.showAddCustomGroceryDialog.value = true },
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.weight(1f).height(44.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Add,
          contentDescription = null,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = AppStrings.addItem(language),
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold
        )
      }
    }

    // Main Shopping List grouped by Categories
    if (items.isEmpty()) {
      EmptyStateView(
        icon = Icons.Outlined.ShoppingBag,
        title = AppStrings.emptyShoppingList(language),
        description = AppStrings.emptyShoppingDesc(language),
        actionText = AppStrings.generateFromPlan(language),
        onActionClick = {
          viewModel.generateShoppingListFromPlan(
            if (language == Language.SPANISH) "Ingredientes agregados desde el menú semanal" else "Ingredients added from weekly meal plan"
          )
        }
      )
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        groupedItems.forEach { (category, categoryItems) ->
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(18.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              border = CardDefaults.outlinedCardBorder()
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                // Category Header
                Text(
                  text = category.localizedName(language).uppercase(),
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 0.5.sp,
                  color = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                )

                categoryItems.forEachIndexed { index, item ->
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .clickable { viewModel.toggleShoppingItem(item) }
                      .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      modifier = Modifier.weight(1f)
                    ) {
                      Checkbox(
                        checked = item.isChecked,
                        onCheckedChange = { viewModel.toggleShoppingItem(item) },
                        colors = CheckboxDefaults.colors(
                          checkedColor = MaterialTheme.colorScheme.primary
                        )
                      )

                      Column {
                        Text(
                          text = item.localizedName(language),
                          fontSize = 14.sp,
                          color = if (item.isChecked) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface,
                          textDecoration = if (item.isChecked) TextDecoration.LineThrough else TextDecoration.None
                        )

                        if (item.sourceRecipe.isNotBlank()) {
                          Text(
                            text = item.sourceRecipe,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outline
                          )
                        }
                      }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                      if (item.formattedAmount().isNotBlank()) {
                        Text(
                          text = item.formattedAmount(),
                          fontSize = 12.sp,
                          fontWeight = FontWeight.Bold,
                          color = MaterialTheme.colorScheme.secondary,
                          modifier = Modifier.padding(end = 6.dp)
                        )
                      }

                      IconButton(
                        onClick = { viewModel.deleteShoppingItem(item.id) },
                        modifier = Modifier.size(30.dp)
                      ) {
                        Icon(
                          imageVector = Icons.Default.Delete,
                          contentDescription = "Delete",
                          tint = MaterialTheme.colorScheme.outline,
                          modifier = Modifier.size(16.dp)
                        )
                      }
                    }
                  }

                  if (index < categoryItems.size - 1) {
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
