package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Difficulty
import com.example.data.model.Recipe
import com.example.ui.components.NutritionGrid
import com.example.ui.components.ServingsStepper
import com.example.ui.i18n.AppStrings
import com.example.ui.i18n.Language
import com.example.ui.theme.CarbAccent
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel

@Composable
fun RecipeDetailScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val language by viewModel.currentLanguage.collectAsState()
  val recipe = viewModel.selectedRecipe.collectAsState().value
  val servingsMultiplier by viewModel.servingsMultiplier.collectAsState()
  val favoriteIds by viewModel.favoriteRecipeIds.collectAsState()

  if (recipe == null) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text("No recipe selected")
    }
    return
  }

  val isFavorite = favoriteIds.contains(recipe.id)
  val currentServings = (recipe.defaultServings * servingsMultiplier).toInt().coerceAtLeast(1)

  // Interactive checked steps for easy cooking mode
  val checkedSteps = remember { mutableStateMapOf<Int, Boolean>() }
  val checkedIngredients = remember { mutableStateMapOf<Int, Boolean>() }

  Box(modifier = modifier.fillMaxSize().testTag("recipe_detail_screen")) {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(bottom = 120.dp)
    ) {
      // 1. Large Hero Photo with back button & actions
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
        ) {
          Image(
            painter = painterResource(id = recipe.imageRes),
            contentDescription = recipe.localizedTitle(language),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
          )

          // Subtle gradient overlay for top bar contrast
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(
                Brush.verticalGradient(
                  colors = listOf(
                    Color.Black.copy(alpha = 0.5f),
                    Color.Transparent,
                    Color.Black.copy(alpha = 0.2f)
                  )
                )
              )
          )

          // Top Action Row
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 12.dp)
              .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Back Button
            IconButton(
              onClick = { viewModel.navigateBack() },
              modifier = Modifier
                .size(42.dp)
                .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                .testTag("detail_back_button")
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
              )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              // Share Button
              IconButton(
                onClick = {
                  val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                      Intent.EXTRA_TEXT,
                      "${recipe.localizedTitle(language)} - ${recipe.localizedDescription(language)} | DiaRecipes"
                    )
                    type = "text/plain"
                  }
                  context.startActivity(Intent.createChooser(sendIntent, AppStrings.shareRecipe(language)))
                },
                modifier = Modifier
                  .size(42.dp)
                  .background(Color.Black.copy(alpha = 0.4f), CircleShape)
              ) {
                Icon(
                  imageVector = Icons.Default.Share,
                  contentDescription = AppStrings.shareRecipe(language),
                  tint = Color.White
                )
              }

              // Favorite Button
              IconButton(
                onClick = { viewModel.toggleFavorite(recipe.id) },
                modifier = Modifier
                  .size(42.dp)
                  .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                  .testTag("detail_fav_button")
              ) {
                Icon(
                  imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                  contentDescription = "Favorite",
                  tint = if (isFavorite) MaterialTheme.colorScheme.error else Color.White
                )
              }
            }
          }
        }
      }

      // 2. Title, Description, & Badges
      item {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
          // Category Pill
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
          ) {
            Text(
              text = recipe.category.localizedName(language),
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = recipe.localizedTitle(language),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
          )

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = recipe.localizedDescription(language),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 22.sp
          )

          Spacer(modifier = Modifier.height(18.dp))

          // Key Stats Row: Prep Time, Cook Time, Total Time, Difficulty
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            DetailStatItem(
              label = AppStrings.prepTime(language),
              value = "${recipe.prepTimeMinutes} ${AppStrings.min(language)}"
            )
            DetailStatItem(
              label = AppStrings.cookTime(language),
              value = "${recipe.cookTimeMinutes} ${AppStrings.min(language)}"
            )
            DetailStatItem(
              label = AppStrings.totalTime(language),
              value = "${recipe.totalTimeMinutes} ${AppStrings.min(language)}"
            )
            DetailStatItem(
              label = AppStrings.difficulty(language),
              value = when (recipe.difficulty) {
                Difficulty.EASY -> AppStrings.diffEasy(language)
                Difficulty.MEDIUM -> AppStrings.diffMedium(language)
                Difficulty.HARD -> AppStrings.diffHard(language)
              }
            )
          }
        }
      }

      // 3. Servings Stepper Control
      item {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp)
        ) {
          ServingsStepper(
            servings = currentServings,
            language = language,
            onServingsChange = { viewModel.setServings(it) },
            modifier = Modifier.fillMaxWidth()
          )
        }
      }

      // 4. Nutritional Information Section
      item {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
          Text(
            text = AppStrings.nutritionalInfo(language),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
          )

          Spacer(modifier = Modifier.height(10.dp))

          NutritionGrid(
            nutrition = recipe.nutrition,
            language = language
          )
        }
      }

      // 5. Ingredients Section with dynamic recalculation
      item {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "${AppStrings.ingredients(language)} (${recipe.ingredients.size})",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onBackground
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              recipe.ingredients.forEachIndexed { index, ingredient ->
                val isChecked = checkedIngredients[index] == true
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { checkedIngredients[index] = !isChecked }
                    .padding(vertical = 8.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                  ) {
                    Checkbox(
                      checked = isChecked,
                      onCheckedChange = { checkedIngredients[index] = it },
                      colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary
                      )
                    )
                    Text(
                      text = ingredient.localizedName(language),
                      fontSize = 14.sp,
                      color = if (isChecked) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface,
                      textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None
                    )
                  }

                  Text(
                    text = ingredient.formattedAmount(servingsMultiplier),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                  )
                }

                if (index < recipe.ingredients.size - 1) {
                  Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                }
              }
            }
          }
        }
      }

      // 6. Numbered Preparation Instructions
      item {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
          Text(
            text = AppStrings.instructions(language),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
          )

          Spacer(modifier = Modifier.height(12.dp))

          val steps = recipe.localizedSteps(language)
          steps.forEachIndexed { index, stepText ->
            val stepNumber = index + 1
            val isStepDone = checkedSteps[stepNumber] == true

            Card(
              modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .clickable { checkedSteps[stepNumber] = !isStepDone },
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(
                containerColor = if (isStepDone) {
                  MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                } else {
                  MaterialTheme.colorScheme.surface
                }
              ),
              border = CardDefaults.outlinedCardBorder()
            ) {
              Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
              ) {
                // Numbered Circle or Checkmark
                Surface(
                  modifier = Modifier.size(32.dp),
                  shape = CircleShape,
                  color = if (isStepDone) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    if (isStepDone) {
                      Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(18.dp)
                      )
                    } else {
                      Text(
                        text = "$stepNumber",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                      )
                    }
                  }
                }

                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = AppStrings.step(language, stepNumber),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                  )

                  Spacer(modifier = Modifier.height(4.dp))

                  Text(
                    text = stepText,
                    fontSize = 14.sp,
                    color = if (isStepDone) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface,
                    lineHeight = 21.sp,
                    textDecoration = if (isStepDone) TextDecoration.LineThrough else TextDecoration.None
                  )
                }
              }
            }
          }
        }
      }
    }

    // Bottom Sticky Action Bar: "Add to Plan"
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.BottomCenter),
      shadowElevation = 8.dp,
      color = MaterialTheme.colorScheme.surface
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Button(
          onClick = { viewModel.showAddToPlanDialog.value = true },
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
          Icon(
            imageVector = Icons.Default.CalendarMonth,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = AppStrings.addToPlan(language),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

@Composable
private fun DetailStatItem(label: String, value: String) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(
      text = label,
      fontSize = 11.sp,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(2.dp))
    Text(
      text = value,
      fontSize = 13.sp,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.onSurface
    )
  }
}
