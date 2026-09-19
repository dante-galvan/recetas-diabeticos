package com.example.data.model

import com.example.ui.i18n.Language

enum class RecipeCategory(
  val id: String,
  val nameEs: String,
  val nameEn: String,
  val iconName: String
) {
  BREAKFAST("breakfast", "Desayunos", "Breakfast", "free_breakfast"),
  LUNCH("lunch", "Almuerzos", "Lunch", "lunch_dining"),
  DINNER("dinner", "Cenas", "Dinner", "dinner_dining"),
  SNACKS("snacks", "Snacks", "Snacks", "bakery_dining"),
  DESSERTS("desserts", "Postres", "Desserts", "cake"),
  DRINKS("drinks", "Bebidas", "Drinks", "local_cafe"),
  SALADS("salads", "Ensaladas", "Salads", "eco"),
  SOUPS("soups", "Sopas", "Soups", "ramen_dining"),
  BEEF("beef", "Carnes", "Meat & Beef", "restaurant"),
  CHICKEN("chicken", "Pollo", "Chicken", "set_meal"),
  FISH("fish", "Pescado", "Fish", "water"),
  SEAFOOD("seafood", "Mariscos", "Seafood", "set_meal"),
  VEGETARIAN("vegetarian", "Vegetariano", "Vegetarian", "spa"),
  VEGAN("vegan", "Vegano", "Vegan", "grass"),
  BAKED_GOODS("baked", "Panificados", "Baked Goods", "breakfast_dining"),
  QUICK_MEALS("quick_meals", "Comidas rápidas", "Quick Meals", "schedule"),
  LOW_CARB("low_carb", "Bajas en carbos", "Low Carb", "monitor_weight"),
  GLUTEN_FREE("gluten_free", "Sin gluten", "Gluten Free", "grain"),
  LACTOSE_FREE("lactose_free", "Sin lactosa", "Lactose Free", "water_drop"),
  EASY("easy", "Fáciles", "Easy Recipes", "thumb_up"),
  FAST("fast", "Rápidas", "Fast Recipes", "bolt");

  fun localizedName(lang: Language): String = if (lang == Language.SPANISH) nameEs else nameEn
}

enum class Difficulty {
  EASY, MEDIUM, HARD
}

enum class GroceryCategory(val nameEs: String, val nameEn: String) {
  VEGETABLES("Vegetales", "Vegetables"),
  FRUITS("Frutas", "Fruits"),
  MEAT_PROTEIN("Carnes y Proteínas", "Meat & Protein"),
  DAIRY("Lácteos", "Dairy"),
  GRAINS("Cereales y Legumbres", "Grains & Legumes"),
  PANTRY("Despensa y Especias", "Pantry & Spices"),
  OTHER("Otros", "Other");

  fun localizedName(lang: Language): String = if (lang == Language.SPANISH) nameEs else nameEn
}
