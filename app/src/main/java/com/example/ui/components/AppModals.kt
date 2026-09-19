package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GroceryCategory
import com.example.data.model.MealSlot
import com.example.data.model.Recipe
import com.example.ui.i18n.AppStrings
import com.example.ui.i18n.Language
import com.example.ui.theme.CarbAccent
import com.example.ui.theme.GoldPro

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
fun AddCustomGroceryDialog(
  language: Language,
  onConfirm: (name: String, category: GroceryCategory) -> Unit,
  onDismiss: () -> Unit
) {
  var name by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf(GroceryCategory.VEGETABLES) }

  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(24.dp),
    title = {
      Text(
        text = AppStrings.addCustomItem(language),
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
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text(AppStrings.productName(language)) },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Text(
          text = AppStrings.productCategory(language),
          style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.primary
        )

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          GroceryCategory.values().toList().chunked(2).forEach { rowCats ->
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              rowCats.forEach { cat ->
                FilterChip(
                  selected = selectedCategory == cat,
                  onClick = { selectedCategory = cat },
                  label = { Text(cat.localizedName(language), fontSize = 11.sp) },
                  modifier = Modifier.weight(1f)
                )
              }
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = { onConfirm(name, selectedCategory) },
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
fun MedicalDisclaimerDialog(
  language: Language,
  onDismiss: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(24.dp),
    title = {
      Text(
        text = AppStrings.medicalNoticeTitle(language),
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
      )
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
      ) {
        Text(
          text = AppStrings.medicalNoticeText(language),
          style = MaterialTheme.typography.bodyMedium,
          lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = if (language == Language.SPANISH) {
            "Todos los valores nutricionales, carbohidratos totales y cálculos calóricos son estimaciones basadas en ingredientes estándar de preparación culinaria y tablas de referencia nutricional. Cada persona con diabetes (Tipo 1, Tipo 2 o Gestacional) tiene una sensibilidad individual a la insulina y requerimientos específicos."
          } else {
            "All nutritional estimates, total carbohydrates, and caloric values are approximations based on standard culinary preparations and reference tables. Every individual with Type 1, Type 2, or Gestational diabetes has unique insulin sensitivity and specific medical requirements."
          },
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          lineHeight = 18.sp
        )
      }
    },
    confirmButton = {
      Button(onClick = onDismiss, shape = RoundedCornerShape(12.dp)) {
        Text(text = AppStrings.understood(language), fontWeight = FontWeight.Bold)
      }
    }
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProUpgradeSheet(
  language: Language,
  onDismiss: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState()
  var isAnnualSelected by remember { mutableStateOf(true) }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    containerColor = MaterialTheme.colorScheme.surface
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)
        .verticalScroll(rememberScrollState()),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Box(
        modifier = Modifier
          .size(60.dp)
          .background(GoldPro.copy(alpha = 0.2f), CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Star,
          contentDescription = null,
          tint = GoldPro,
          modifier = Modifier.size(34.dp)
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      Text(
        text = "DiaRecipes Pro",
        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = if (language == Language.SPANISH) {
          "Disfruta de la experiencia culinaria completa para el control glucémico diario."
        } else {
          "Enjoy the complete culinary experience for daily glycemic management."
        },
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(20.dp))

      // Feature checklist
      listOf(
        if (language == Language.SPANISH) "Acceso ilimitado a cientos de recetas exclusivas" else "Unlimited access to hundreds of exclusive recipes",
        if (language == Language.SPANISH) "Generador inteligente de menús semanales automáticos" else "Smart automated weekly meal plan generator",
        if (language == Language.SPANISH) "Sincronización instantánea con listas de compras inteligentes" else "Instant sync with categorized smart shopping lists",
        if (language == Language.SPANISH) "Exportación en PDF para compartir con tu nutricionista" else "PDF meal planner export for your nutritionist",
        if (language == Language.SPANISH) "Experiencia 100% libre de anuncios y sin distracciones" else "100% ad-free distractionless experience"
      ).forEach { feature ->
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
          )
          Text(
            text = feature,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Plan Selection Cards (Monthly vs Annual)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Card(
          modifier = Modifier
            .weight(1f)
            .clickable { isAnnualSelected = false },
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (!isAnnualSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
          ),
          border = CardDefaults.outlinedCardBorder()
        ) {
          Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = if (language == Language.SPANISH) "Mensual" else "Monthly",
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "$3.99",
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = if (language == Language.SPANISH) "/ mes" else "/ mo",
              fontSize = 10.sp,
              color = MaterialTheme.colorScheme.outline
            )
          }
        }

        Card(
          modifier = Modifier
            .weight(1f)
            .clickable { isAnnualSelected = true },
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (isAnnualSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
          ),
          border = CardDefaults.outlinedCardBorder()
        ) {
          Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = MaterialTheme.colorScheme.secondary
            ) {
              Text(
                text = if (language == Language.SPANISH) "-35% AHORRO" else "SAVE 35%",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "$29.99",
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = if (language == Language.SPANISH) "/ año" else "/ year",
              fontSize = 10.sp,
              color = MaterialTheme.colorScheme.outline
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      Button(
        onClick = onDismiss,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
      ) {
        Text(
          text = if (language == Language.SPANISH) "Probar 7 días gratis" else "Start 7-day free trial",
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = if (language == Language.SPANISH) "Cancela en cualquier momento sin compromiso." else "Cancel anytime with no obligation.",
        fontSize = 11.sp,
        color = MaterialTheme.colorScheme.outline,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(30.dp))
    }
  }
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
