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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MealSlot
import com.example.data.model.Recipe
import com.example.data.model.RecipeCategory
import com.example.ui.components.MedicalDisclaimerBanner
import com.example.ui.components.RecipeCard
import com.example.ui.components.RecipeHorizontalCard
import com.example.ui.i18n.AppStrings
import com.example.ui.i18n.Language
import com.example.ui.theme.CarbAccent
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel
import java.util.Calendar

@Composable
fun HomeScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val language by viewModel.currentLanguage.collectAsState()
  val userProfile by viewModel.userProfile.collectAsState()
  val favoriteIds by viewModel.favoriteRecipeIds.collectAsState()
  val allRecipes = viewModel.repository.getRecipes()
  val mealPlanItems by viewModel.mealPlanItems.collectAsState()

  val featuredRecipe = allRecipes.find { it.isFeatured } ?: allRecipes.first()
  val popularRecipes = allRecipes.filter { it.isPopular }
  val newRecipes = allRecipes.filter { it.isNew }
  val recommendedRecipes = allRecipes.filter { 
    it.suitableForType1 || it.suitableForType2 
  }.take(4)

  // Determine greeting based on current time
  val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
  val greeting = when (hour) {
    in 5..11 -> AppStrings.greetingMorning(language)
    in 12..18 -> AppStrings.greetingAfternoon(language)
    else -> AppStrings.greetingEvening(language)
  }

  // Quick categories
  val quickCategories = listOf(
    RecipeCategory.BREAKFAST,
    RecipeCategory.LUNCH,
    RecipeCategory.DINNER,
    RecipeCategory.SNACKS,
    RecipeCategory.LOW_CARB,
    RecipeCategory.SALADS,
    RecipeCategory.SOUPS,
    RecipeCategory.DESSERTS
  )

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("home_screen"),
    contentPadding = PaddingValues(bottom = 90.dp),
    verticalArrangement = Arrangement.spacedBy(22.dp)
  ) {
    // 1. Header & Greeting
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
          Column {
            Text(
              text = greeting,
              style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = AppStrings.homeSubtitle(language),
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          // Diabetes Type Badge
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
            modifier = Modifier.clickable { viewModel.navigateTo(AppScreen.PROFILE) }
          ) {
            Text(
              text = userProfile.diabetesType.localizedName(language),
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
          }
        }
      }
    }

    // 2. Recipe of the Day / Featured Hero Card
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier.padding(bottom = 10.dp)
        ) {
          Icon(
            imageVector = Icons.Default.LocalFireDepartment,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(20.dp)
          )
          Text(
            text = AppStrings.recipeOfTheDay(language),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
          )
        }

        FeaturedRecipeCard(
          recipe = featuredRecipe,
          isFavorite = favoriteIds.contains(featuredRecipe.id),
          language = language,
          onRecipeClick = { viewModel.openRecipeDetail(featuredRecipe) },
          onFavoriteClick = { viewModel.toggleFavorite(featuredRecipe.id) }
        )
      }
    }

    // 3. Quick Categories Horizontal Chips
    item {
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = AppStrings.quickCategories(language),
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onBackground,
          modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
        )

        LazyRow(
          contentPadding = PaddingValues(horizontal = 20.dp),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.padding(top = 8.dp)
        ) {
          items(quickCategories) { category ->
            Surface(
              shape = RoundedCornerShape(14.dp),
              color = MaterialTheme.colorScheme.surface,
              tonalElevation = 1.dp,
              border = CardDefaults.outlinedCardBorder(),
              modifier = Modifier
                .clickable {
                  viewModel.filterByCategory(category)
                  viewModel.navigateTo(AppScreen.EXPLORE)
                }
                .testTag("home_cat_${category.id}")
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(8.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                )
                Text(
                  text = category.localizedName(language),
                  fontSize = 13.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = MaterialTheme.colorScheme.onSurface
                )
              }
            }
          }
        }
      }
    }

    // 4. Today's Meal Plan Access Widget
    item {
      val today = viewModel.selectedDayOfWeek.collectAsState().value
      val todayMeals = mealPlanItems.filter { it.dayOfWeek == today }

      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
        )
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
              )
              Text(
                text = AppStrings.todayMealPlan(language),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimaryContainer
              )
            }

            TextButton(onClick = { viewModel.navigateTo(AppScreen.PLAN) }) {
              Text(
                text = AppStrings.viewFullPlan(language),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          if (todayMeals.isEmpty()) {
            Text(
              text = AppStrings.emptyDayPlan(language),
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(vertical = 6.dp)
            )
          } else {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              todayMeals.take(3).forEach { item ->
                Surface(
                  modifier = Modifier
                    .weight(1f)
                    .clickable { viewModel.openRecipeDetail(item.recipe) },
                  shape = RoundedCornerShape(12.dp),
                  color = MaterialTheme.colorScheme.surface
                ) {
                  Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                      text = item.slot.localizedName(language),
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                      text = item.recipe.localizedTitle(language),
                      fontSize = 11.sp,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis
                    )
                    Text(
                      text = "${item.recipe.nutrition.totalCarbsGrams.toInt()}g carb",
                      fontSize = 10.sp,
                      color = CarbAccent,
                      fontWeight = FontWeight.SemiBold
                    )
                  }
                }
              }
            }
          }
        }
      }
    }

    // 5. Recommended Recipes (Horizontal Scroll)
    item {
      Column(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = AppStrings.recommendedForYou(language),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
          )
          TextButton(onClick = { viewModel.navigateTo(AppScreen.EXPLORE) }) {
            Text(text = AppStrings.viewAll(language), fontSize = 12.sp)
          }
        }

        LazyRow(
          contentPadding = PaddingValues(horizontal = 20.dp),
          horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          items(recommendedRecipes) { recipe ->
            RecipeHorizontalCard(
              recipe = recipe,
              isFavorite = favoriteIds.contains(recipe.id),
              language = language,
              onRecipeClick = { viewModel.openRecipeDetail(recipe) },
              onFavoriteClick = { viewModel.toggleFavorite(recipe.id) }
            )
          }
        }
      }
    }

    // 6. Popular Recipes
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = AppStrings.popularRecipes(language),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
          )
          TextButton(onClick = { viewModel.navigateTo(AppScreen.EXPLORE) }) {
            Text(text = AppStrings.viewAll(language), fontSize = 12.sp)
          }
        }

        popularRecipes.take(2).forEach { recipe ->
          RecipeCard(
            recipe = recipe,
            isFavorite = favoriteIds.contains(recipe.id),
            language = language,
            onRecipeClick = { viewModel.openRecipeDetail(recipe) },
            onFavoriteClick = { viewModel.toggleFavorite(recipe.id) }
          )
        }
      }
    }

    // 7. New Recipes
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        Text(
          text = AppStrings.newRecipes(language),
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onBackground
        )

        newRecipes.take(2).forEach { recipe ->
          RecipeCard(
            recipe = recipe,
            isFavorite = favoriteIds.contains(recipe.id),
            language = language,
            onRecipeClick = { viewModel.openRecipeDetail(recipe) },
            onFavoriteClick = { viewModel.toggleFavorite(recipe.id) }
          )
        }
      }
    }

    // 8. Discreet Health Notice
    item {
      Box(modifier = Modifier.padding(horizontal = 20.dp)) {
        MedicalDisclaimerBanner(language = language)
      }
    }
  }
}

@Composable
private fun FeaturedRecipeCard(
  recipe: Recipe,
  isFavorite: Boolean,
  language: Language,
  onRecipeClick: () -> Unit,
  onFavoriteClick: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("featured_recipe_card")
      .clickable(onClick = onRecipeClick),
    shape = RoundedCornerShape(24.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(240.dp)
    ) {
      Image(
        painter = painterResource(id = recipe.imageRes),
        contentDescription = recipe.localizedTitle(language),
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
      )

      // Gradient overlay for contrast
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            Brush.verticalGradient(
              colors = listOf(
                Color.Black.copy(alpha = 0.2f),
                Color.Black.copy(alpha = 0.4f),
                Color.Black.copy(alpha = 0.85f)
              )
            )
          )
      )

      // Total Carbs Badge (Top Left)
      Surface(
        modifier = Modifier
          .padding(16.dp)
          .align(Alignment.TopStart),
        shape = RoundedCornerShape(12.dp),
        color = CarbAccent
      ) {
        Text(
          text = "${recipe.nutrition.totalCarbsGrams.toInt()}g ${AppStrings.totalCarbs(language)}",
          color = Color.White,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
      }

      // Favorite button (Top Right)
      IconButton(
        onClick = onFavoriteClick,
        modifier = Modifier
          .padding(12.dp)
          .align(Alignment.TopEnd)
          .background(Color.Black.copy(alpha = 0.35f), CircleShape)
      ) {
        Icon(
          imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
          contentDescription = "Favorite",
          tint = if (isFavorite) MaterialTheme.colorScheme.error else Color.White
        )
      }

      // Bottom Content: Title, time, difficulty, calories
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .align(Alignment.BottomStart)
          .padding(18.dp)
      ) {
        Text(
          text = recipe.localizedTitle(language),
          color = Color.White,
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Schedule,
              contentDescription = null,
              tint = Color.White.copy(alpha = 0.9f),
              modifier = Modifier.size(14.dp)
            )
            Text(
              text = "${recipe.totalTimeMinutes} ${AppStrings.min(language)}",
              color = Color.White.copy(alpha = 0.9f),
              fontSize = 12.sp
            )
          }

          Text(
            text = "•",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 12.sp
          )

          Text(
            text = "${recipe.nutrition.calories} ${AppStrings.kcal(language)}",
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          )

          Text(
            text = "•",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 12.sp
          )

          Text(
            text = "${recipe.nutrition.proteinGrams.toInt()}g ${AppStrings.protein(language)}",
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 12.sp
          )
        }
      }
    }
  }
}
