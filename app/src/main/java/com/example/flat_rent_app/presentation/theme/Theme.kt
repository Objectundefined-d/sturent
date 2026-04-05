package com.example.flat_rent_app.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    background = LightBackground,
    surface = LightSurface,
    onBackground = LightOnBackground,
    onSurface = LightOnSurface,
    secondary = LightSecondary,
    error = LightError
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    background = DarkBackground,
    surface = DarkSurface,
    onBackground = DarkOnBackground,
    onSurface = DarkOnSurface,
    secondary = DarkSecondary,
    error = DarkError
)

val LocalThemeController = compositionLocalOf<ThemeController> {
    error("ThemeController not provided")
}

class ThemeController(initialDark: Boolean) {
    val isDark = mutableStateOf(initialDark)

    fun toggle() {
        isDark.value = !isDark.value
    }

    fun setDark(dark: Boolean) {
        isDark.value = dark
    }
}

@Composable
fun FlatrentappTheme(
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val themeController = remember { ThemeController(systemDark) }
    val isDark by themeController.isDark

    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalThemeController provides themeController) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}