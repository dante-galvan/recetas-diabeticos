package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Difficulty
import com.example.data.model.Recipe
import com.example.ui.i18n.AppStrings
import com.example.ui.i18n.Language
import com.example.ui.theme.CarbAccent
import com.example.ui.theme.CarbAccentLight

@Composable
fun RecipeCard(
  recipe: Recipe,
  isFavorite: Boolean,
  language: Language,
  onRecipeClick: () -> Unit,
  onFavoriteClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("recipe_card_${recipe.id}")
      .clickable(onClick = onRecipeClick),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column {
      // Photo Container with Favorite Button and Carb Badge Overlay
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(180.dp)
      ) {
        Image(
          painter = painterResource(id = recipe.imageRes),
          contentDescription = recipe.localizedTitle(language),
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop
        )

        // Subtle gradient overlay for better text/badge legibility
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.verticalGradient(
                colors = listOf(
                  Color.Black.copy(alpha = 0.35f),
                  Color.Transparent,
                  Color.Black.copy(alpha = 0.25f)
                )
              )
            )
        )

        // Total Carbs Prominent Badge (Top-Left)
        Surface(
          modifier = Modifier
            .padding(12.dp)
            .align(Alignment.TopStart),
          shape = RoundedCornerShape(12.dp),
          color = CarbAccent,
          shadowElevation = 3.dp
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Text(
              text = "${recipe.nutrition.totalCarbsGrams.toInt()}g",
              color = Color.White,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = AppStrings.totalCarbsShort(language),
              color = Color.White.copy(alpha = 0.9f),
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }

        // Favorite Toggle Button (Top-Right)
        val favScale by animateFloatAsState(
          targetValue = if (isFavorite) 1.15f else 1f,
          label = "favScale"
        )
        val favColor by animateColorAsState(
          targetValue = if (isFavorite) MaterialTheme.colorScheme.error else Color.White,
          label = "favColor"
        )

        IconButton(
          onClick = onFavoriteClick,
          modifier = Modifier
            .padding(8.dp)
            .size(40.dp)
            .align(Alignment.TopEnd)
            .background(Color.Black.copy(alpha = 0.3f), CircleShape)
            .testTag("fav_btn_${recipe.id}")
        ) {
          Icon(
            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
            tint = favColor,
            modifier = Modifier
              .size(22.dp)
              .scale(favScale)
          )
        }

        // Time and Difficulty Pill (Bottom-Left)
        Row(
          modifier = Modifier
            .padding(12.dp)
            .align(Alignment.BottomStart),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.Black.copy(alpha = 0.65f)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(13.dp)
              )
              Text(
                text = "${recipe.totalTimeMinutes} ${AppStrings.min(language)}",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
              )
            }
          }

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.Black.copy(alpha = 0.65f)
          ) {
            val diffText = when (recipe.difficulty) {
              Difficulty.EASY -> AppStrings.diffEasy(language)
              Difficulty.MEDIUM -> AppStrings.diffMedium(language)
              Difficulty.HARD -> AppStrings.diffHard(language)
            }
            Text(
              text = diffText,
              color = Color.White,
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }
      }

      // Title & Nutritional Subtitle
      Column(
        modifier = Modifier.padding(16.dp)
      ) {
        Text(
          text = recipe.localizedTitle(language),
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            lineHeight = 22.sp
          ),
          color = MaterialTheme.colorScheme.onSurface,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Nutritional quick stats: Calories, Protein, Sugars
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "${recipe.nutrition.calories} ${AppStrings.kcal(language)}",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.secondary
          )

          Text(
            text = "•",
            color = MaterialTheme.colorScheme.outlineVariant,
            fontSize = 12.sp
          )

          Text(
            text = "${recipe.nutrition.proteinGrams.toInt()}g ${AppStrings.protein(language)}",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Text(
            text = "•",
            color = MaterialTheme.colorScheme.outlineVariant,
            fontSize = 12.sp
          )

          Text(
            text = "${recipe.nutrition.fiberGrams.toInt()}g ${AppStrings.fiber(language)}",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }
  }
}

@Composable
fun RecipeHorizontalCard(
  recipe: Recipe,
  isFavorite: Boolean,
  language: Language,
  onRecipeClick: () -> Unit,
  onFavoriteClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .width(280.dp)
      .testTag("recipe_horiz_card_${recipe.id}")
      .clickable(onClick = onRecipeClick),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(140.dp)
      ) {
        Image(
          painter = painterResource(id = recipe.imageRes),
          contentDescription = recipe.localizedTitle(language),
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop
        )

        // Gradient
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.verticalGradient(
                colors = listOf(Color.Black.copy(alpha = 0.3f), Color.Transparent, Color.Black.copy(alpha = 0.3f))
              )
            )
        )

        // Total Carbs Badge
        Surface(
          modifier = Modifier
            .padding(10.dp)
            .align(Alignment.TopStart),
          shape = RoundedCornerShape(10.dp),
          color = CarbAccent
        ) {
          Text(
            text = "${recipe.nutrition.totalCarbsGrams.toInt()}g ${AppStrings.totalCarbsShort(language)}",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }

        IconButton(
          onClick = onFavoriteClick,
          modifier = Modifier
            .padding(6.dp)
            .size(36.dp)
            .align(Alignment.TopEnd)
            .background(Color.Black.copy(alpha = 0.3f), CircleShape)
        ) {
          Icon(
            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = "Favorite",
            tint = if (isFavorite) MaterialTheme.colorScheme.error else Color.White,
            modifier = Modifier.size(20.dp)
          )
        }

        // Time pill
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = Color.Black.copy(alpha = 0.65f),
          modifier = Modifier
            .padding(10.dp)
            .align(Alignment.BottomStart)
        ) {
          Text(
            text = "${recipe.totalTimeMinutes} ${AppStrings.min(language)}",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
          )
        }
      }

      Column(
        modifier = Modifier.padding(12.dp)
      ) {
        Text(
          text = recipe.localizedTitle(language),
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "${recipe.nutrition.calories} ${AppStrings.kcal(language)}",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.secondary
          )
          Text(
            text = "• ${recipe.nutrition.proteinGrams.toInt()}g ${AppStrings.protein(language)}",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }
  }
}
