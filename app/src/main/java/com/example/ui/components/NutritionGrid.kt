package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NutritionInfo
import com.example.ui.i18n.AppStrings
import com.example.ui.i18n.Language
import com.example.ui.theme.CalorieAccent
import com.example.ui.theme.CarbAccent
import com.example.ui.theme.FiberAccent
import com.example.ui.theme.ProteinAccent

@Composable
fun NutritionGrid(
  nutrition: NutritionInfo,
  language: Language,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    // Top Row: Featured Highlights (Total Carbohydrates & Calories)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // PROMINENT TOTAL CARBOHYDRATES CARD
      Card(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        ),
        border = CardDefaults.outlinedCardBorder().copy(
          brush = androidx.compose.ui.graphics.SolidColor(CarbAccent.copy(alpha = 0.5f))
        )
      ) {
        Column(
          modifier = Modifier.padding(14.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .background(CarbAccent, CircleShape)
            )
            Text(
              text = AppStrings.totalCarbs(language).uppercase(),
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.5.sp,
              color = MaterialTheme.colorScheme.onPrimaryContainer
            )
          }

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = "${nutrition.totalCarbsGrams}g",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = CarbAccent
          )

          Text(
            text = AppStrings.perServing(language),
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      // CALORIES CARD
      Card(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
      ) {
        Column(
          modifier = Modifier.padding(14.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .background(CalorieAccent, CircleShape)
            )
            Text(
              text = AppStrings.calories(language).uppercase(),
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.5.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = "${nutrition.calories}",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = CalorieAccent
          )

          Text(
            text = "${AppStrings.kcal(language)} ${AppStrings.perServing(language)}",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }

    // 2x3 Grid of secondary nutritional indicators
    // Row 1: Sugars & Dietary Fiber
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      NutrientItem(
        label = AppStrings.sugars(language),
        value = "${nutrition.sugarsGrams}g",
        accentColor = Color(0xFFD97706),
        modifier = Modifier.weight(1f)
      )
      NutrientItem(
        label = AppStrings.fiber(language),
        value = "${nutrition.fiberGrams}g",
        accentColor = FiberAccent,
        modifier = Modifier.weight(1f)
      )
    }

    // Row 2: Protein & Total Fat
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      NutrientItem(
        label = AppStrings.protein(language),
        value = "${nutrition.proteinGrams}g",
        accentColor = ProteinAccent,
        modifier = Modifier.weight(1f)
      )
      NutrientItem(
        label = AppStrings.totalFat(language),
        value = "${nutrition.fatGrams}g",
        accentColor = Color(0xFF8B5CF6),
        modifier = Modifier.weight(1f)
      )
    }

    // Row 3: Saturated Fat & Sodium
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      NutrientItem(
        label = AppStrings.saturatedFat(language),
        value = "${nutrition.saturatedFatGrams}g",
        accentColor = MaterialTheme.colorScheme.outline,
        modifier = Modifier.weight(1f)
      )
      NutrientItem(
        label = AppStrings.sodium(language),
        value = "${nutrition.sodiumMg.toInt()}mg",
        accentColor = MaterialTheme.colorScheme.outline,
        modifier = Modifier.weight(1f)
      )
    }
  }
}

@Composable
private fun NutrientItem(
  label: String,
  value: String,
  accentColor: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(12.dp),
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 1.dp,
    border = CardDefaults.outlinedCardBorder()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 10.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = label,
          fontSize = 12.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
      Text(
        text = value,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )
    }
  }
}
