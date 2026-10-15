package com.example

import com.example.ui.i18n.Language
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class LanguageTest {

  @Test
  fun fromCode_mapsAllSixLanguages() {
    assertSame(Language.ENGLISH, Language.fromCode("en"))
    assertSame(Language.SPANISH, Language.fromCode("es"))
    assertSame(Language.PORTUGUESE, Language.fromCode("pt"))
    assertSame(Language.FRENCH, Language.fromCode("fr"))
    assertSame(Language.GERMAN, Language.fromCode("de"))
    assertSame(Language.ITALIAN, Language.fromCode("it"))
  }

  @Test
  fun fromCode_isCaseInsensitive() {
    assertSame(Language.SPANISH, Language.fromCode("ES"))
    assertSame(Language.GERMAN, Language.fromCode("De"))
  }

  @Test
  fun fromCode_unknownFallsBackToEnglish() {
    assertSame(Language.ENGLISH, Language.fromCode("xx"))
    assertSame(Language.ENGLISH, Language.fromCode(""))
  }

  @Test
  fun allLanguages_hasSixEntriesWithExpectedCodes() {
    assertEquals(6, Language.allLanguages.size)
    assertEquals(listOf("en", "es", "pt", "fr", "de", "it"), Language.allLanguages.map { it.code })
  }

  @Test
  fun codes_areUnique() {
    assertEquals(6, Language.allLanguages.map { it.code }.toSet().size)
  }
}
