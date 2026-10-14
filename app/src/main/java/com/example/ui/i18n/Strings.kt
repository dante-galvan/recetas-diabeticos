package com.example.ui.i18n

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.app.diabeticos.R

enum class Language(val code: String, val displayNameRes: Int) {
  ENGLISH("en", R.string.lang_english),
  SPANISH("es", R.string.lang_spanish),
  PORTUGUESE("pt", R.string.lang_portuguese),
  FRENCH("fr", R.string.lang_french),
  GERMAN("de", R.string.lang_german),
  ITALIAN("it", R.string.lang_italian);

  companion object {
    fun fromCode(code: String): Language {
      return when (code.lowercase()) {
        "es" -> SPANISH
        "pt" -> PORTUGUESE
        "fr" -> FRENCH
        "de" -> GERMAN
        "it" -> ITALIAN
        else -> ENGLISH
      }
    }

    val allLanguages: List<Language> = listOf(ENGLISH, SPANISH, PORTUGUESE, FRENCH, GERMAN, ITALIAN)
  }

  fun displayName(context: Context): String = context.getString(displayNameRes)

  @Composable
  fun displayNameCompose(): String = stringResource(displayNameRes)
}

class StringsHelper(private val context: Context) {
  fun getString(@StringRes id: Int): String = context.getString(id)

  fun getString(@StringRes id: Int, vararg formatArgs: Any): String = context.getString(id, *formatArgs)
}

@Composable
fun stringRes(@StringRes id: Int): String = stringResource(id)

@Composable
fun stringRes(@StringRes id: Int, vararg formatArgs: Any): String = stringResource(id, *formatArgs)

// Non-composable string accessor for use in click handlers and other non-composable contexts
fun getString(context: Context, @StringRes id: Int): String = context.getString(id)

fun getString(context: Context, @StringRes id: Int, vararg formatArgs: Any): String = context.getString(id, *formatArgs)
