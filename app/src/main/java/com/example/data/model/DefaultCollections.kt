package com.example.data.model

/**
 * A collection that ships with the app and must always be available to the user.
 *
 * The [id] is a stable internal identifier and is used as the Room primary key.
 * It must never be derived from (or replaced by) a translated name, so switching
 * the app language cannot duplicate a collection or change its stored identity.
 * The localized display name is resolved from string resources using [id]; the
 * [nameEs]/[nameEn] values are the persisted fallbacks kept for non-UI callers
 * and for data written before localization was wired up.
 */
data class DefaultCollection(
  val id: String,
  val nameEs: String,
  val nameEn: String,
  val icon: String,
  val category: RecipeCategory?
)

/**
 * The catalogue of built-in collections. See [DefaultCollection] for why the ids
 * are constants rather than translated strings.
 */
object DefaultCollections {
  const val BREAKFAST_ID = "default_breakfast"
  const val LUNCH_ID = "default_lunch"
  const val DINNER_ID = "default_dinner"
  const val SNACKS_ID = "default_snacks"
  const val SALADS_ID = "default_salads"
  const val SOUPS_ID = "default_soups"
  const val DESSERTS_ID = "default_desserts"
  const val DRINKS_ID = "default_drinks"

  /** Shown in the order the user should see them. */
  val all: List<DefaultCollection> = listOf(
    DefaultCollection(
      id = BREAKFAST_ID,
      nameEs = "Desayunos",
      nameEn = "Breakfast",
      icon = RecipeCategory.BREAKFAST.iconName,
      category = RecipeCategory.BREAKFAST
    ),
    DefaultCollection(
      id = LUNCH_ID,
      nameEs = "Almuerzos",
      nameEn = "Lunch",
      icon = RecipeCategory.LUNCH.iconName,
      category = RecipeCategory.LUNCH
    ),
    DefaultCollection(
      id = DINNER_ID,
      nameEs = "Cenas",
      nameEn = "Dinner",
      icon = RecipeCategory.DINNER.iconName,
      category = RecipeCategory.DINNER
    ),
    DefaultCollection(
      id = SNACKS_ID,
      nameEs = "Snacks",
      nameEn = "Snacks",
      icon = RecipeCategory.SNACKS.iconName,
      category = RecipeCategory.SNACKS
    ),
    DefaultCollection(
      id = SALADS_ID,
      nameEs = "Ensaladas",
      nameEn = "Salads",
      icon = RecipeCategory.SALADS.iconName,
      category = RecipeCategory.SALADS
    ),
    DefaultCollection(
      id = SOUPS_ID,
      nameEs = "Sopas",
      nameEn = "Soups",
      icon = RecipeCategory.SOUPS.iconName,
      category = RecipeCategory.SOUPS
    ),
    DefaultCollection(
      id = DESSERTS_ID,
      nameEs = "Postres",
      nameEn = "Desserts",
      icon = RecipeCategory.DESSERTS.iconName,
      category = RecipeCategory.DESSERTS
    ),
    DefaultCollection(
      id = DRINKS_ID,
      nameEs = "Bebidas",
      nameEn = "Drinks",
      icon = RecipeCategory.DRINKS.iconName,
      category = RecipeCategory.DRINKS
    )
  )

  /** Returns the built-in definition for [id], or null when it is a user collection. */
  fun find(id: String): DefaultCollection? = all.firstOrNull { it.id == id }

  /** True when [id] belongs to a built-in collection and therefore cannot be removed. */
  fun isDefault(id: String): Boolean = find(id) != null
}
