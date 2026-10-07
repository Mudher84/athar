package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

// ============================================================================
// LUXURIOUS DUAL-HARMONY ARABIC TYPOGRAPHY SYSTEM
// - Classical Serif (Amiri & El Messiri): Royal, high-contrast, graceful Serif for
//   grand headlines, section titles, Hadith Matn, and calligraphic badges.
// - Modern Sans-serif (Tajawal & Almarai): Clear, warm, ultra-legible Sans-serif
//   for reading body text, sanad chains, UI controls, navigation, and badges.
// ============================================================================

// Classical Arabic Serif Fonts
val AmiriFontFamily = FontFamily(
    Font(R.font.amiri, FontWeight.Normal)
)

val ElMessiriFontFamily = FontFamily(
    Font(R.font.el_messiri, FontWeight.Bold)
)

// Master Serif Reference for Titles, Headers & Matn Sanctuary
val ArabicSerifFontFamily = AmiriFontFamily
val ArabicDisplaySerifFontFamily = ElMessiriFontFamily

// Contemporary Arabic Sans-serif Fonts
val TajawalFontFamily = FontFamily(
    Font(R.font.tajawal, FontWeight.Normal)
)

val AlmaraiFontFamily = FontFamily(
    Font(R.font.almarai, FontWeight.Normal)
)

// Master Sans-serif Reference for Body, UI, Labels, Chips & Inputs
val ArabicSansFontFamily = TajawalFontFamily
val ArabicBodySansFontFamily = AlmaraiFontFamily

// Alias for backwards compatibility
val CairoFontFamily = ArabicSansFontFamily

val AppTypography = Typography(
    // ------------------------------------------------------------------------
    // DISPLAY (Serif - Majestic Hero & Screen Banners)
    // ------------------------------------------------------------------------
    displayLarge = TextStyle(
        fontFamily = ArabicDisplaySerifFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 46.sp,
        letterSpacing = 0.sp
    ),
    displayMedium = TextStyle(
        fontFamily = ArabicDisplaySerifFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = ArabicSerifFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),

    // ------------------------------------------------------------------------
    // HEADLINE (Serif - Section Headings & Major Card Titles)
    // ------------------------------------------------------------------------
    headlineLarge = TextStyle(
        fontFamily = ArabicSerifFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 34.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = ArabicSerifFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 30.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = ArabicSerifFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),

    // ------------------------------------------------------------------------
    // TITLE (Serif / Sans-serif - Chapter Names, Scholar Names & Dialog Headers)
    // ------------------------------------------------------------------------
    titleLarge = TextStyle(
        fontFamily = ArabicSerifFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = ArabicSerifFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    titleSmall = TextStyle(
        fontFamily = ArabicSansFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    ),

    // ------------------------------------------------------------------------
    // BODY (Sans-serif - High-Legibility, Flowing Body Text & Hadith Commentary)
    // ------------------------------------------------------------------------
    bodyLarge = TextStyle(
        fontFamily = ArabicSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = ArabicSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 23.sp,
        letterSpacing = 0.sp
    ),
    bodySmall = TextStyle(
        fontFamily = ArabicBodySansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 19.sp,
        letterSpacing = 0.sp
    ),

    // ------------------------------------------------------------------------
    // LABEL (Sans-serif - Interactive Buttons, Chips, Badges & Micro-Copy)
    // ------------------------------------------------------------------------
    labelLarge = TextStyle(
        fontFamily = ArabicSansFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    ),
    labelMedium = TextStyle(
        fontFamily = ArabicSansFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 17.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = ArabicSansFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 15.sp,
        letterSpacing = 0.sp
    )
)
