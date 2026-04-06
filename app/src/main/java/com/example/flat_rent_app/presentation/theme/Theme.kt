package com.example.flat_rent_app.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = BrandPrimary,
    onPrimary = BackgroundDark,
    primaryContainer = Color(0xFF2E2346),
    onPrimaryContainer = Color(0xFFE9DDFF),
    secondary = BrandSecondary,
    onSecondary = BackgroundDark,
    tertiary = BrandTertiary,
    background = BackgroundDark,
    onBackground = Color(0xFFF2F4F8),
    surface = SurfaceDark,
    onSurface = Color(0xFFF2F4F8),
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceMutedDark,
    outline = OutlineDark,
    error = Color(0xFFFF6B81),
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = BrandPrimary,
    secondary = BrandSecondary,
    tertiary = BrandTertiary,
    background = BackgroundDark,
    onBackground = Color(0xFFF2F4F8),
    surface = SurfaceDark,
    onSurface = Color(0xFFF2F4F8),
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceMutedDark,
    outline = OutlineDark
)

@Composable
fun FlatrentappTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
