package com.example.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// ============================================================================
// ULTRA-MODERN COLOR SCHEMES
// ============================================================================

private val ModernLightColorScheme = lightColorScheme(
    primary = ModernIndigoPrimary,
    onPrimary = Color.White,
    primaryContainer = ModernIndigoContainer,
    onPrimaryContainer = ModernIndigoOnContainer,
    secondary = ModernTealAccent,
    onSecondary = Color.White,
    secondaryContainer = ModernTealContainer,
    onSecondaryContainer = ModernTealAccent,
    tertiary = ModernViolet,
    onTertiary = Color.White,
    background = ModernBgLight,
    onBackground = ModernTextPrimaryLight,
    surface = ModernSurfaceLight,
    onSurface = ModernTextPrimaryLight,
    surfaceVariant = ModernSurfaceVariantLight,
    onSurfaceVariant = ModernTextSecondaryLight,
    outline = ModernBorderLight,
    outlineVariant = ModernDividerLight
)

private val ModernDarkColorScheme = darkColorScheme(
    primary = ModernIndigoLight,
    onPrimary = ModernBgDark,
    primaryContainer = ModernIndigoOnContainer,
    onPrimaryContainer = ModernIndigoContainer,
    secondary = ModernTealLight,
    onSecondary = ModernBgDark,
    secondaryContainer = Color(0xFF134E4A),
    onSecondaryContainer = ModernTealLight,
    tertiary = ModernViolet,
    onTertiary = Color.White,
    background = ModernBgDark,
    onBackground = ModernTextPrimaryDark,
    surface = ModernSurfaceDark,
    onSurface = ModernTextPrimaryDark,
    surfaceVariant = ModernSurfaceVariantDark,
    onSurfaceVariant = ModernTextSecondaryDark,
    outline = ModernBorderDark,
    outlineVariant = ModernDividerDark
)

@Composable
fun AtharTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> ModernDarkColorScheme
        else -> ModernLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
