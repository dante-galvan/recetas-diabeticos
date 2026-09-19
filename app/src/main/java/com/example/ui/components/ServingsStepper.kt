package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.i18n.AppStrings
import com.example.ui.i18n.Language

@Composable
fun ServingsStepper(
  servings: Int,
  language: Language,
  onServingsChange: (Int) -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(16.dp),
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Text(
        text = "${AppStrings.servings(language)}:",
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 4.dp)
      )

      IconButton(
        onClick = { if (servings > 1) onServingsChange(servings - 1) },
        enabled = servings > 1,
        modifier = Modifier
          .size(32.dp)
          .background(
            if (servings > 1) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
            CircleShape
          )
      ) {
        Icon(
          imageVector = Icons.Default.Remove,
          contentDescription = "Decrease servings",
          tint = if (servings > 1) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline,
          modifier = Modifier.size(16.dp)
        )
      }

      Text(
        text = "$servings",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(horizontal = 4.dp)
      )

      IconButton(
        onClick = { if (servings < 12) onServingsChange(servings + 1) },
        enabled = servings < 12,
        modifier = Modifier
          .size(32.dp)
          .background(
            if (servings < 12) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
            CircleShape
          )
      ) {
        Icon(
          imageVector = Icons.Default.Add,
          contentDescription = "Increase servings",
          tint = if (servings < 12) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline,
          modifier = Modifier.size(16.dp)
        )
      }
    }
  }
}
