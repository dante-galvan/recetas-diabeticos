package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.data.local.DiaRecipesDatabase
import com.example.data.local.UserProfileEntity
import com.example.ui.i18n.Language
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [36])
class UserProfilePersistenceTest {

  private lateinit var db: DiaRecipesDatabase

  @Before
  fun setUp() {
    val context: Context = ApplicationProvider.getApplicationContext()
    db = Room.inMemoryDatabaseBuilder(context, DiaRecipesDatabase::class.java)
      .allowMainThreadQueries()
      .build()
  }

  @After
  fun tearDown() {
    db.close()
  }

  private fun profile(langCode: String) = UserProfileEntity(
    id = 1,
    dietaryPrefsCsv = "Low Carb",
    allergiesCsv = "",
    langCode = langCode,
    isDarkTheme = null,
    mealReminders = true,
    onboardingDone = true
  )

  @Test
  fun noProfileRow_emitsNull() = runBlocking {
    assertNull(db.dao().getUserProfile().first())
  }

  @Test
  fun savedLangCode_isPersistedAcrossReads() = runBlocking {
    db.dao().saveUserProfile(profile("de"))
    val loaded = db.dao().getUserProfile().first()
    assertEquals("de", loaded?.langCode)
    assertEquals(Language.GERMAN, Language.fromCode(loaded!!.langCode))
  }

  @Test
  fun savedLangCode_canBeOverwritten() = runBlocking {
    db.dao().saveUserProfile(profile("es"))
    assertEquals("es", db.dao().getUserProfile().first()?.langCode)

    db.dao().saveUserProfile(profile("fr"))
    assertEquals("fr", db.dao().getUserProfile().first()?.langCode)
    assertEquals(Language.FRENCH, Language.fromCode(db.dao().getUserProfile().first()!!.langCode))
  }

  @Test
  fun allSixLanguageCodes_roundTripThroughPersistence() = runBlocking {
    for (lang in Language.allLanguages) {
      db.dao().saveUserProfile(profile(lang.code))
      val loaded = db.dao().getUserProfile().first()
      assertEquals(lang.code, loaded?.langCode)
      assertEquals(lang, Language.fromCode(loaded!!.langCode))
    }
  }

  @Test
  fun defaultEnglishProfile_roundTrips() = runBlocking {
    db.dao().saveUserProfile(profile(Language.ENGLISH.code))
    val loaded = db.dao().getUserProfile().first()
    assertEquals("en", loaded?.langCode)
    assertEquals(Language.ENGLISH, Language.fromCode(loaded!!.langCode))
  }
}
