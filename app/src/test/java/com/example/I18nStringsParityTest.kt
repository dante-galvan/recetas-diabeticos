package com.example

import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pure-JVM parity check: every locale must define exactly the same set of
 * string keys as the default (English) values/strings.xml.
 */
class I18nStringsParityTest {

  private val localeDirs =
    listOf("values", "values-es", "values-pt", "values-fr", "values-de", "values-it")

  private fun resolveResDir(): File {
    val candidates =
      listOf(
        File("src/main/res"),
        File("app/src/main/res"),
        File("../app/src/main/res")
      )
    return candidates.firstOrNull { it.isDirectory }
      ?: error(
        "Could not locate src/main/res from working dir=${File(".").absolutePath}. " +
          "Tried: ${candidates.map { it.absolutePath }}"
      )
  }

  private fun parseStringNames(file: File): Set<String> {
    assertTrue("Missing strings file: ${file.absolutePath}", file.isFile)
    val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)
    val nodes = doc.getElementsByTagName("string")
    val names = mutableSetOf<String>()
    for (i in 0 until nodes.length) {
      val el = nodes.item(i) as org.w3c.dom.Element
      val name = el.getAttribute("name")
      assertTrue("Empty string name in ${file.absolutePath}", name.isNotBlank())
      names.add(name)
    }
    return names
  }

  @Test
  fun allLocalesDefineExactlyTheSameKeysAsDefault() {
    val resDir = resolveResDir()
    val defaultKeys = parseStringNames(File(resDir, "values/strings.xml"))
    assertTrue("Default strings.xml should not be empty", defaultKeys.isNotEmpty())

    for (locale in localeDirs.drop(1)) {
      val localeKeys = parseStringNames(File(resDir, "$locale/strings.xml"))
      val missing = defaultKeys - localeKeys
      val extra = localeKeys - defaultKeys
      assertEquals(
        "Keys missing in $locale compared to default",
        emptySet<String>(),
        missing
      )
      assertEquals(
        "Extra keys in $locale not present in default",
        emptySet<String>(),
        extra
      )
    }
  }

  @Test
  fun defaultStringsContainsNewI18nKeys() {
    val resDir = resolveResDir()
    val defaultKeys = parseStringNames(File(resDir, "values/strings.xml"))
    val required =
      setOf(
        "no_recipe_selected",
        "sort_relevance",
        "cd_favorite",
        "cd_close",
        "cd_back",
        "cd_search",
        "cd_clear_search",
        "cd_decrease_servings",
        "cd_increase_servings",
        "cd_change",
        "cd_remove",
        "cd_completed",
        "diet_low_carb",
        "diet_mediterranean",
        "diet_gluten_free",
        "allergen_gluten",
        "allergen_lactose"
      )
    val missing = required - defaultKeys
    assertEquals("New i18n keys missing from default strings.xml", emptySet<String>(), missing)
  }
}
