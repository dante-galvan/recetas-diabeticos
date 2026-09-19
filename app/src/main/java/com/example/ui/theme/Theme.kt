package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = SageGreenPrimaryDark,
  onPrimary = SageGreenOnPrimaryDark,
  primaryContainer = SageGreenContainerDark,
  onPrimaryContainer = SageGreenOnContainerDark,
  secondary = CoralSecondaryDark,
  onSecondary = CoralOnSecondaryDark,
  secondaryContainer = CoralContainerDark,
  onSecondaryContainer = CoralOnContainerDark,
  tertiary = HoneyTertiaryDark,
  onTertiary = HoneyOnTertiaryDark,
  tertiaryContainer = HoneyContainerDark,
  onTertiaryContainer = HoneyOnContainerDark,
  background = NeutralDarkBackground,
  surface = NeutralDarkSurface,
  surfaceVariant = NeutralDarkSurfaceVariant,
  outline = NeutralDarkOutline,
  outlineVariant = NeutralDarkOutlineVariant,
)

private val LightColorScheme = lightColorScheme(
  primary = ForestGreenPrimary,
  onPrimary = ForestGreenOnPrimary,
  primaryContainer = ForestGreenContainer,
  onPrimaryContainer = ForestGreenOnContainer,
  secondary = TerracottaSecondary,
  onSecondary = TerracottaOnSecondary,
  secondaryContainer = TerracottaContainer,
  onSecondaryContainer = TerracottaOnContainer,
  tertiary = AmberWheatTertiary,
  onTertiary = AmberWheatOnTertiary,
  tertiaryContainer = AmberWheatContainer,
  onTertiaryContainer = AmberWheatOnContainer,
  background = NeutralLightBackground,
  surface = NeutralLightSurface,
  surfaceVariant = NeutralLightSurfaceVariant,
  outline = NeutralLightOutline,
  outlineVariant = NeutralLightOutlineVariant,
)

@Composable
fun DiaRecipesTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
