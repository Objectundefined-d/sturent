package com.example.flat_rent_app.presentation.theme

import com.example.flat_rent_app.presentation.theme.TextSizes

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = TextSizes.sp16,
        lineHeight = TextSizes.sp24,
        letterSpacing = TextSizes.sp0_5
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = TextSizes.sp22,
        lineHeight = TextSizes.sp28,
        letterSpacing = TextSizes.sp0
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = TextSizes.sp11,
        lineHeight = TextSizes.sp16,
        letterSpacing = TextSizes.sp0_5
    )
    */
)
