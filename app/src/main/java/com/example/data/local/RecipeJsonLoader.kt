package com.example.data.local

import android.content.Context
import com.app.diabeticos.R
import com.example.data.model.Difficulty
import com.example.data.model.GroceryCategory
import com.example.data.model.Ingredient
import com.example.data.model.NutritionInfo
import com.example.data.model.Recipe
import com.example.data.model.RecipeCategory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object RecipeJsonLoader {

  private val jsonFiles = listOf(
    "25-Desayunos.json",
    "25-Almuerzos.json",
    "25-Cenas.json",
    "25-Snacks.json",
    "25-Postres.json",
    "25-Ensaladas.json",
    "25-Sopas.json",
    "25-Bajas en carbohidratos.json"
  )

  private val categoryImageMap = mapOf(
    "Desayunos" to R.drawable.img_recipe_parfait,
    "Almuerzos" to R.drawable.img_recipe_med_bowl,
    "Cenas" to R.drawable.img_recipe_salmon,
    "Snacks" to R.drawable.img_recipe_parfait,
    "Postres" to R.drawable.img_recipe_parfait,
    "Ensaladas" to R.drawable.img_recipe_med_bowl,
    "Sopas" to R.drawable.img_recipe_soup,
    "Bajas en carbohidratos" to R.drawable.img_recipe_salmon
  )

  private val recipeImageMap = mapOf(
    1 to R.drawable.recipe_1,
    2 to R.drawable.recipe_2,
    3 to R.drawable.recipe_3,
    4 to R.drawable.recipe_4,
    5 to R.drawable.recipe_5,
    6 to R.drawable.recipe_6,
    7 to R.drawable.recipe_7,
    8 to R.drawable.recipe_8,
    9 to R.drawable.recipe_9,
    10 to R.drawable.recipe_10,
    11 to R.drawable.recipe_11,
    12 to R.drawable.recipe_12,
    13 to R.drawable.recipe_13,
    14 to R.drawable.recipe_14,
    15 to R.drawable.recipe_15,
    16 to R.drawable.recipe_16,
    17 to R.drawable.recipe_17,
    18 to R.drawable.recipe_18,
    19 to R.drawable.recipe_19,
    20 to R.drawable.recipe_20,
    21 to R.drawable.recipe_21,
    22 to R.drawable.recipe_22,
    23 to R.drawable.recipe_23,
    24 to R.drawable.recipe_24,
    25 to R.drawable.recipe_25,
    26 to R.drawable.recipe_26,
    27 to R.drawable.recipe_27,
    28 to R.drawable.recipe_28,
    29 to R.drawable.recipe_29,
    30 to R.drawable.recipe_30,
    31 to R.drawable.recipe_31,
    32 to R.drawable.recipe_32,
    33 to R.drawable.recipe_33,
    34 to R.drawable.recipe_34,
    35 to R.drawable.recipe_35,
    36 to R.drawable.recipe_36,
    37 to R.drawable.recipe_37,
    38 to R.drawable.recipe_38,
    39 to R.drawable.recipe_39,
    40 to R.drawable.recipe_40,
    41 to R.drawable.recipe_41,
    42 to R.drawable.recipe_42,
    43 to R.drawable.recipe_43,
    44 to R.drawable.recipe_44,
    45 to R.drawable.recipe_45,
    46 to R.drawable.recipe_46,
    47 to R.drawable.recipe_47,
    48 to R.drawable.recipe_48,
    49 to R.drawable.recipe_49,
    50 to R.drawable.recipe_50,
    51 to R.drawable.recipe_51,
    52 to R.drawable.recipe_52,
    53 to R.drawable.recipe_53,
    54 to R.drawable.recipe_54,
    55 to R.drawable.recipe_55,
    56 to R.drawable.recipe_56,
    57 to R.drawable.recipe_57,
    58 to R.drawable.recipe_58,
    59 to R.drawable.recipe_59,
    60 to R.drawable.recipe_60,
    61 to R.drawable.recipe_61,
    62 to R.drawable.recipe_62,
    63 to R.drawable.recipe_63,
    64 to R.drawable.recipe_64,
    65 to R.drawable.recipe_65,
    66 to R.drawable.recipe_66,
    67 to R.drawable.recipe_67,
    68 to R.drawable.recipe_68,
    69 to R.drawable.recipe_69,
    70 to R.drawable.recipe_70,
    71 to R.drawable.recipe_71,
    72 to R.drawable.recipe_72,
    73 to R.drawable.recipe_73,
    74 to R.drawable.recipe_74,
    75 to R.drawable.recipe_75,
    76 to R.drawable.recipe_76,
    77 to R.drawable.recipe_77,
    78 to R.drawable.recipe_78,
    79 to R.drawable.recipe_79,
    80 to R.drawable.recipe_80,
    81 to R.drawable.recipe_81,
    82 to R.drawable.recipe_82,
    83 to R.drawable.recipe_83,
    84 to R.drawable.recipe_84,
    85 to R.drawable.recipe_85,
    86 to R.drawable.recipe_86,
    87 to R.drawable.recipe_87,
    88 to R.drawable.recipe_88,
    89 to R.drawable.recipe_89,
    90 to R.drawable.recipe_90,
    91 to R.drawable.recipe_91,
    92 to R.drawable.recipe_92,
    93 to R.drawable.recipe_93,
    94 to R.drawable.recipe_94,
    95 to R.drawable.recipe_95,
    96 to R.drawable.recipe_96,
    97 to R.drawable.recipe_97,
    98 to R.drawable.recipe_98,
    99 to R.drawable.recipe_99,
    100 to R.drawable.recipe_100,
    101 to R.drawable.recipe_101,
    102 to R.drawable.recipe_102,
    103 to R.drawable.recipe_103,
    104 to R.drawable.recipe_104,
    105 to R.drawable.recipe_105,
    106 to R.drawable.recipe_106,
    107 to R.drawable.recipe_107,
    108 to R.drawable.recipe_108,
    109 to R.drawable.recipe_109,
    110 to R.drawable.recipe_110,
    111 to R.drawable.recipe_111,
    112 to R.drawable.recipe_112,
    113 to R.drawable.recipe_113,
    114 to R.drawable.recipe_114,
    115 to R.drawable.recipe_115,
    116 to R.drawable.recipe_116,
    117 to R.drawable.recipe_117,
    118 to R.drawable.recipe_118,
    119 to R.drawable.recipe_119,
    120 to R.drawable.recipe_120,
    121 to R.drawable.recipe_121,
    122 to R.drawable.recipe_122,
    123 to R.drawable.recipe_123,
    124 to R.drawable.recipe_124,
    125 to R.drawable.recipe_125,
    126 to R.drawable.recipe_126,
    127 to R.drawable.recipe_127,
    128 to R.drawable.recipe_128,
    129 to R.drawable.recipe_129,
    130 to R.drawable.recipe_130,
    131 to R.drawable.recipe_131,
    132 to R.drawable.recipe_132,
    133 to R.drawable.recipe_133,
    134 to R.drawable.recipe_134,
    135 to R.drawable.recipe_135,
    136 to R.drawable.recipe_136,
    137 to R.drawable.recipe_137,
    138 to R.drawable.recipe_138,
    139 to R.drawable.recipe_139,
    140 to R.drawable.recipe_140,
    141 to R.drawable.recipe_141,
    142 to R.drawable.recipe_142,
    143 to R.drawable.recipe_143,
    144 to R.drawable.recipe_144,
    145 to R.drawable.recipe_145,
    146 to R.drawable.recipe_146,
    147 to R.drawable.recipe_147,
    148 to R.drawable.recipe_148,
    149 to R.drawable.recipe_149,
    150 to R.drawable.recipe_150,
    151 to R.drawable.recipe_151,
    152 to R.drawable.recipe_152,
    153 to R.drawable.recipe_153,
    154 to R.drawable.recipe_154,
    155 to R.drawable.recipe_155,
    156 to R.drawable.recipe_156,
    157 to R.drawable.recipe_157,
    158 to R.drawable.recipe_158,
    159 to R.drawable.recipe_159,
    160 to R.drawable.recipe_160,
    161 to R.drawable.recipe_161,
    162 to R.drawable.recipe_162,
    163 to R.drawable.recipe_163,
    164 to R.drawable.recipe_164,
    165 to R.drawable.recipe_165,
    166 to R.drawable.recipe_166,
    167 to R.drawable.recipe_167,
    168 to R.drawable.recipe_168,
    169 to R.drawable.recipe_169,
    170 to R.drawable.recipe_170,
    171 to R.drawable.recipe_171,
    172 to R.drawable.recipe_172,
    173 to R.drawable.recipe_173,
    174 to R.drawable.recipe_174,
    175 to R.drawable.recipe_175,
    176 to R.drawable.recipe_176,
    177 to R.drawable.recipe_177,
    178 to R.drawable.recipe_178,
    179 to R.drawable.recipe_179,
    180 to R.drawable.recipe_180,
    181 to R.drawable.recipe_181,
    182 to R.drawable.recipe_182,
    183 to R.drawable.recipe_183,
    184 to R.drawable.recipe_184,
    185 to R.drawable.recipe_185,
    186 to R.drawable.recipe_186,
    187 to R.drawable.recipe_187,
    188 to R.drawable.recipe_188,
    189 to R.drawable.recipe_189,
    190 to R.drawable.recipe_190,
    191 to R.drawable.recipe_191,
    192 to R.drawable.recipe_192,
    193 to R.drawable.recipe_193,
    194 to R.drawable.recipe_194,
    195 to R.drawable.recipe_195,
    196 to R.drawable.recipe_196,
    197 to R.drawable.recipe_197,
    198 to R.drawable.recipe_198,
    199 to R.drawable.recipe_199,
    200 to R.drawable.recipe_200
  )

  private val categoryEnumMap = mapOf(
    "Desayunos" to RecipeCategory.BREAKFAST,
    "Almuerzos" to RecipeCategory.LUNCH,
    "Cenas" to RecipeCategory.DINNER,
    "Snacks" to RecipeCategory.SNACKS,
    "Postres" to RecipeCategory.DESSERTS,
    "Ensaladas" to RecipeCategory.SALADS,
    "Sopas" to RecipeCategory.SOUPS,
    "Bajas en carbohidratos" to RecipeCategory.LOW_CARB
  )

  private val difficultyMap = mapOf(
    "Fácil" to Difficulty.EASY,
    "Facil" to Difficulty.EASY,
    "Media" to Difficulty.MEDIUM,
    "Avanzada" to Difficulty.HARD
  )

  // English translation overlays. Mirrors the base JSON structure but only
  // carries the human-readable fields (name, description, instructions).
  // Missing overlay files or fields fall back to the Spanish source text.
  internal data class RecipeTranslationRoot(val recipes: List<RecipeTranslation> = emptyList())

  internal data class RecipeTranslation(
    val id: Int,
    val name: String? = null,
    val description: String? = null,
    val instructions: List<String>? = null
  )

  internal data class IngredientGlossary(val ingredients: Map<String, String> = emptyMap())

  internal fun loadRecipes(context: Context): List<Recipe> {
    val moshi = Moshi.Builder()
      .addLast(KotlinJsonAdapterFactory())
      .build()

    val adapter = moshi.adapter(RecipeJsonRoot::class.java)
    val translationAdapter = moshi.adapter(RecipeTranslationRoot::class.java)
    val glossaryAdapter = moshi.adapter(IngredientGlossary::class.java)

    val translationsById = mutableMapOf<Int, RecipeTranslation>()
    val glossary = mutableMapOf<String, String>()

    // Best-effort load: a missing translation file simply means "fall back to Spanish".
    try {
      val json = context.assets.open("en/ingredients_en.json").bufferedReader().use { it.readText() }
      glossaryAdapter.fromJson(json)?.ingredients?.let { glossary.putAll(it) }
    } catch (e: Exception) {
      e.printStackTrace()
    }

    val allRecipes = mutableListOf<Recipe>()

    for (fileName in jsonFiles) {
      try {
        val json = context.assets.open(fileName).bufferedReader().use { it.readText() }
        val root = adapter.fromJson(json) ?: continue

        try {
          val translationJson =
            context.assets.open("en/$fileName").bufferedReader().use { it.readText() }
          translationAdapter.fromJson(translationJson)?.recipes?.forEach { translation ->
            translationsById[translation.id] = translation
          }
        } catch (e: Exception) {
          e.printStackTrace()
        }

        val recipes = root.recipes.mapNotNull { jsonRecipe ->
          mapToRecipe(jsonRecipe, translationsById[jsonRecipe.id], glossary)
        }
        allRecipes.addAll(recipes)
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }

    return allRecipes
  }

  internal fun mapToRecipe(
    json: RecipeJson,
    translation: RecipeTranslation? = null,
    glossary: Map<String, String> = emptyMap()
  ): Recipe? {
    val categoryEnum = categoryEnumMap[json.category] ?: return null
    val imageRes = recipeImageMap[json.id] ?: categoryImageMap[json.category] ?: R.drawable.img_recipe_parfait
    val difficulty = difficultyMap[json.difficulty] ?: Difficulty.EASY

    val tags = buildTags(json)

    return Recipe(
      id = "recipe_${json.id}",
      titleEs = json.name,
      titleEn = translation?.name ?: json.name,
      descriptionEs = json.description,
      descriptionEn = translation?.description ?: json.description,
      category = categoryEnum,
      secondaryCategories = emptyList(),
      prepTimeMinutes = json.prep_time_minutes,
      cookTimeMinutes = json.cook_time_minutes,
      difficulty = difficulty,
      defaultServings = json.servings,
      nutrition = NutritionInfo(
        calories = json.nutrition.calories_kcal,
        totalCarbsGrams = json.nutrition.carbohydrates_g,
        sugarsGrams = json.nutrition.sugars_g,
        proteinGrams = json.nutrition.protein_g,
        fatGrams = json.nutrition.total_fat_g,
        saturatedFatGrams = json.nutrition.saturated_fat_g,
        fiberGrams = json.nutrition.fiber_g,
        sodiumMg = json.nutrition.sodium_mg
      ),
      ingredients = json.ingredients.map { ing ->
        Ingredient(
          nameEs = ing.name,
          nameEn = glossary[ing.name] ?: ing.name,
          baseAmount = ing.quantity,
          unit = mapUnit(ing.unit),
          unitEn = mapUnitEn(ing.unit),
          category = GroceryCategory.OTHER
        )
      },
      stepsEs = json.instructions,
      stepsEn = translation?.instructions?.takeIf { it.size == json.instructions.size }
        ?: json.instructions,
      tags = tags,
      imageRes = imageRes,
      isFeatured = json.id == 1,
      isPopular = json.id % 5 == 0,
      isNew = json.id % 7 == 0
    )
  }

  private fun buildTags(json: RecipeJson): List<String> {
    val tags = mutableListOf<String>()
    tags.add(json.category.lowercase())

    if (json.nutrition.carbohydrates_g < 15) tags.add("low_carb")
    if (json.nutrition.calories_kcal < 300) tags.add("bajas_en_carbohidratos")
    if (json.difficulty == "Fácil") tags.add("faciles")
    if (json.total_time_minutes <= 20) tags.add("rapidas")

    return tags
  }

  private fun mapUnit(unit: String): String {
    return when (unit.lowercase()) {
      "unidades", "unidad" -> "unidades"
      "g" -> "g"
      "ml" -> "ml"
      "pizca" -> "pizca"
      "cucharadas" -> "cucharadas"
      "cucharaditas" -> "cucharaditas"
      else -> unit
    }
  }

  private fun mapUnitEn(unit: String): String {
    return when (unit.lowercase()) {
      "unidades", "unidad" -> "units"
      "g" -> "g"
      "ml" -> "ml"
      "pizca" -> "pinch"
      "cucharadas" -> "tablespoons"
      "cucharaditas" -> "teaspoons"
      "hojas" -> "leaves"
      "ramitas" -> "sprigs"
      "trozo" -> "piece"
      "lonchas" -> "slices"
      else -> unit
    }
  }
}
