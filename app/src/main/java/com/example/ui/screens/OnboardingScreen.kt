package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DiabetesType
import com.example.ui.components.MedicalDisclaimerBanner
import com.example.ui.i18n.AppStrings
import com.example.ui.i18n.Language
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(
  viewModel: MainViewModel,
  language: Language,
  onComplete: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedDiabetesType by remember { mutableStateOf(DiabetesType.TYPE_2) }
  var selectedPreferences by remember { mutableStateOf(setOf("Low Carb", "Mediterranean")) }
  var selectedAllergies by remember { mutableStateOf(emptySet<String>()) }

  val dietaryOptions = listOf(
    "Low Carb", "Mediterranean", "Balanced", "Vegetarian", "Vegan", "High Fiber"
  )

  val allergyOptions = listOf(
    "Gluten", "Lactose", "Nuts", "Shellfish", "Eggs", "Soy"
  )

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 24.dp, vertical = 32.dp)
      .testTag("onboarding_screen"),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Top Skip Button
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.End
    ) {
      TextButton(onClick = {
        viewModel.completeOnboarding(DiabetesType.NOT_SPECIFIED, emptySet(), emptySet())
        onComplete()
      }) {
        Text(
          text = AppStrings.skip(language),
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    // App Logo / Welcome Icon
    Surface(
      modifier = Modifier.size(68.dp),
      shape = CircleShape,
      color = MaterialTheme.colorScheme.primaryContainer
    ) {
      Box(contentAlignment = Alignment.Center) {
        Icon(
          imageVector = Icons.Default.LocalDining,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(36.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
      text = AppStrings.onboardingTitle(language),
      style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
      textAlign = TextAlign.Center,
      color = MaterialTheme.colorScheme.onBackground
    )

    Spacer(modifier = Modifier.height(6.dp))

    Text(
      text = AppStrings.onboardingSubtitle(language),
      style = MaterialTheme.typography.bodyMedium,
      textAlign = TextAlign.Center,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      lineHeight = 20.sp
    )

    Spacer(modifier = Modifier.height(24.dp))

    // Step 1: Diabetes Type
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      border = CardDefaults.outlinedCardBorder()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Text(
          text = AppStrings.onboardingStep1(language),
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(10.dp))

        listOf(
          DiabetesType.TYPE_1,
          DiabetesType.TYPE_2,
          DiabetesType.GESTATIONAL,
          DiabetesType.NOT_SPECIFIED
        ).forEach { type ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { selectedDiabetesType = type }
              .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            RadioButton(
              selected = selectedDiabetesType == type,
              onClick = { selectedDiabetesType = type }
            )
            Text(
              text = type.localizedName(language),
              fontSize = 14.sp,
              fontWeight = if (selectedDiabetesType == type) FontWeight.Bold else FontWeight.Normal
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Step 2: Dietary Preferences
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      border = CardDefaults.outlinedCardBorder()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Text(
          text = AppStrings.onboardingStep2(language),
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(10.dp))

        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          dietaryOptions.forEach { opt ->
            val isSelected = selectedPreferences.contains(opt)
            FilterChip(
              selected = isSelected,
              onClick = {
                selectedPreferences = if (isSelected) {
                  selectedPreferences - opt
                } else {
                  selectedPreferences + opt
                }
              },
              label = { Text(opt) }
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Step 3: Allergies / to avoid
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      border = CardDefaults.outlinedCardBorder()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Text(
          text = AppStrings.onboardingStep3(language),
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(10.dp))

        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          allergyOptions.forEach { allergy ->
            val isSelected = selectedAllergies.contains(allergy)
            FilterChip(
              selected = isSelected,
              onClick = {
                selectedAllergies = if (isSelected) {
                  selectedAllergies - allergy
                } else {
                  selectedAllergies + allergy
                }
              },
              label = { Text(allergy) }
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Medical disclaimer reminder
    MedicalDisclaimerBanner(language = language)

    Spacer(modifier = Modifier.height(24.dp))

    // Continue CTA Button
    Button(
      onClick = {
        viewModel.completeOnboarding(selectedDiabetesType, selectedPreferences, selectedAllergies)
        onComplete()
      },
      shape = RoundedCornerShape(16.dp),
      modifier = Modifier
        .fillMaxWidth()
        .height(52.dp)
        .testTag("onboarding_continue_btn")
    ) {
      Text(
        text = AppStrings.continueText(language),
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.width(8.dp))
      Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null)
    }

    Spacer(modifier = Modifier.height(24.dp))
  }
}
