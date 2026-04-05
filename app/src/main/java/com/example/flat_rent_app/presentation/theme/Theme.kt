package com.example.flat_rent_app.presentation.theme

import android.content.res.Configuration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext

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
    fun toggle() {
        isDark.value = !isDark.value
    }

    fun setDark(dark: Boolean) {
        isDark.value = dark
    }

    val isDark = mutableStateOf(initialDark)
}

@Composable
fun FlatrentappTheme(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isSystemDark = configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

    val savedDark = remember {
        context.getSharedPreferences("notification_prefs", android.content.Context.MODE_PRIVATE)
            .getBoolean("dark_theme", isSystemDark)
    }
    val themeController = remember { ThemeController(savedDark) }
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