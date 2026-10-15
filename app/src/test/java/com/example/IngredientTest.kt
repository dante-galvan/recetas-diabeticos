package com.example

import com.example.data.model.GroceryCategory
import com.example.data.model.Ingredient
import com.example.ui.i18n.Language
import org.junit.Assert.assertEquals
import org.junit.Test

class IngredientTest {

  private fun ing(unit: String, unitEn: String, amount: Double) =
    Ingredient(
      nameEs = "Tomate natural triturado",
      nameEn = "Crushed natural tomato",
      baseAmount = amount,
      unit = unit,
      unitEn = unitEn,
      category = GroceryCategory.VEGETABLES
    )

  @Test
  fun localizedName_returnsSpanishForSpanish_andEnglishOtherwise() {
    val i = ing("g", "g", 100.0)
    assertEquals("Tomate natural triturado", i.localizedName(Language.SPANISH))
    assertEquals("Crushed natural tomato", i.localizedName(Language.ENGLISH))
    assertEquals("Crushed natural tomato", i.localizedName(Language.FRENCH))
  }

  @Test
  fun localizedUnit_returnsSpanishUnitForSpanish_andEnglishUnitOtherwise() {
    val i = ing("cucharadas", "tablespoons", 2.0)
    assertEquals("cucharadas", i.localizedUnit(Language.SPANISH))
    assertEquals("tablespoons", i.localizedUnit(Language.ENGLISH))
    assertEquals("tablespoons", i.localizedUnit(Language.GERMAN))
  }

  @Test
  fun formattedAmount_wholeNumber_dropsDecimals() {
    val i = ing("g", "g", 200.0)
    assertEquals("200 g", i.formattedAmount(1.0, Language.ENGLISH))
    assertEquals("200 g", i.formattedAmount(1.0, Language.SPANISH))
  }

  @Test
  fun formattedAmount_scalesWithServingsMultiplier() {
    val i = ing("g", "g", 100.0)
    assertEquals("50 g", i.formattedAmount(0.5, Language.ENGLISH))
    assertEquals("300 g", i.formattedAmount(3.0, Language.ENGLISH))
  }

  @Test
  fun formattedAmount_fractionalAmount_usesOneDecimal() {
    val i = ing("ml", "ml", 2.5)
    assertEquals("2.5 ml", i.formattedAmount(1.0, Language.ENGLISH))
  }

  @Test
  fun formattedAmount_usesLocalizedUnit() {
    val i = ing("cucharadita", "teaspoon", 1.0)
    assertEquals("1 teaspoon", i.formattedAmount(1.0, Language.ENGLISH))
    assertEquals("1 cucharadita", i.formattedAmount(1.0, Language.SPANISH))
  }
}
