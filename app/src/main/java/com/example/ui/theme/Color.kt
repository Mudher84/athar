package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ============================================================================
// ULTRA-MODERN CONTEMPORARY PALETTE (نمط مودرن عصري أنيق وفائق التطور)
// ============================================================================

// --- Modern Electric Indigo & Cyber Teal (الألوان الرئيسية العصرية) ---
val ModernIndigoPrimary = Color(0xFF4F46E5)       // Electric Indigo
val ModernIndigoLight = Color(0xFF818CF8)
val ModernIndigoContainer = Color(0xFFEEF2FF)
val ModernIndigoOnContainer = Color(0xFF312E81)

val ModernTealAccent = Color(0xFF0D9488)          // Modern Cyber Teal
val ModernTealLight = Color(0xFF2DD4BF)
val ModernTealContainer = Color(0xFFF0FDFA)

val ModernEmerald = Color(0xFF10B981)             // Modern Emerald Green
val ModernEmeraldBg = Color(0xFFECFDF5)
val ModernEmeraldBorder = Color(0xFFA7F3D0)

val ModernSkyNavy = Color(0xFF2563EB)             // Modern Electric Blue
val ModernSkyNavyBg = Color(0xFFEFF6FF)
val ModernSkyNavyBorder = Color(0xFFBFDBFE)

val ModernViolet = Color(0xFF8B5CF6)              // Modern Royal Violet
val ModernVioletBg = Color(0xFFF5F3FF)
val ModernVioletBorder = Color(0xFFDDD6FE)

// --- Light Theme Canvas (Porcelain Glass) ---
val ModernBgLight = Color(0xFFF8FAFC)             // Pure soft modern slate canvas
val ModernSurfaceLight = Color(0xFFFFFFFF)        // Pure crisp white card
val ModernSurfaceVariantLight = Color(0xFFF1F5F9) // Subtle container
val ModernTextPrimaryLight = Color(0xFF0F172A)    // Deep modern charcoal
val ModernTextSecondaryLight = Color(0xFF64748B)  // Muted slate
val ModernTextMutedLight = Color(0xFF94A3B8)
val ModernBorderLight = Color(0xFFE2E8F0)         // Clean modern border
val ModernDividerLight = Color(0xFFF1F5F9)

// --- Dark Theme Canvas (OLED Tech Graphite) ---
val ModernBgDark = Color(0xFF090D16)              // Deep OLED tech dark
val ModernSurfaceDark = Color(0xFF111827)         // Modern graphite card
val ModernSurfaceVariantDark = Color(0xFF1F2937)  // Elevated container
val ModernTextPrimaryDark = Color(0xFFF9FAFB)     // Pure crisp white
val ModernTextSecondaryDark = Color(0xFF9CA3AF)   // Light slate
val ModernTextMutedDark = Color(0xFF6B7280)
val ModernBorderDark = Color(0xFF1F2937)          // Dark subtle border
val ModernDividerDark = Color(0xFF161E2E)

// --- Modern Gradients ---
val ModernPrimaryGradient = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFF4F46E5),
        Color(0xFF7C3AED),
        Color(0xFF06B6D4)
    )
)

val ModernCardGradientLight = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFFFFFFF),
        Color(0xFFF8FAFC)
    )
)

val ModernCardGradientDark = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF111827),
        Color(0xFF0D1424)
    )
)

val ModernAccentGradient = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFF10B981),
        Color(0xFF06B6D4)
    )
)

val ModernHeartActive = Color(0xFFEF4444)

// Backward Compatibility Aliases
val MinimalPrimary = ModernIndigoPrimary
val MinimalPrimaryAccent = ModernIndigoPrimary
val MinimalPrimaryAccentLight = ModernIndigoLight
val MinimalPrimaryAccentSoft = ModernIndigoContainer
val MinimalPrimaryAccentDark = ModernIndigoOnContainer

val MinimalBgLight = ModernBgLight
val MinimalSurfaceLight = ModernSurfaceLight
val MinimalSurfaceElevatedLight = ModernSurfaceVariantLight
val MinimalTextPrimaryLight = ModernTextPrimaryLight
val MinimalTextSecondaryLight = ModernTextSecondaryLight
val MinimalTextMutedLight = ModernTextMutedLight
val MinimalBorderSubtleLight = ModernBorderLight
val MinimalDividerLight = ModernDividerLight

val MinimalBgDark = ModernBgDark
val MinimalSurfaceDark = ModernSurfaceDark
val MinimalSurfaceElevatedDark = ModernSurfaceVariantDark
val MinimalTextPrimaryDark = ModernTextPrimaryDark
val MinimalTextSecondaryDark = ModernTextSecondaryDark
val MinimalTextMutedDark = ModernTextMutedDark
val MinimalBorderSubtleDark = ModernBorderDark
val MinimalDividerDark = ModernDividerDark

val MadinahEmeraldGreen = ModernEmerald
val MadinahEmeraldBg = ModernEmeraldBg
val MadinahEmeraldBorder = ModernEmeraldBorder

val ShamSapphireNavy = ModernSkyNavy
val ShamSapphireBg = ModernSkyNavyBg
val ShamSapphireBorder = ModernSkyNavyBorder

val AndalusGarnetPlum = ModernViolet
val AndalusGarnetBg = ModernVioletBg
val AndalusGarnetBorder = ModernVioletBorder

val MinimalHeartActive = ModernHeartActive
val MinimalHeartInactive = ModernTextMutedLight

val RoyalGoldPrimary = ModernIndigoPrimary
val RoyalGoldLight = ModernIndigoLight
val RoyalGoldDark = ModernIndigoOnContainer
val RoyalGoldShimmer = ModernIndigoContainer
val RoyalGoldContainerLight = ModernIndigoContainer
val RoyalGoldOnContainerLight = ModernIndigoOnContainer
val RoyalGoldContainerDark = ModernIndigoOnContainer
val RoyalGoldOnContainerDark = ModernIndigoLight

val RoyalNavyPrimary = ModernIndigoPrimary
val RoyalNavyLight = Color(0xFF4338CA)
val RoyalNavyDark = ModernBgDark
val RoyalNavyDeep = Color(0xFF030712)
val RoyalNavyCardBg = ModernSurfaceDark
val RoyalNavyElevated = ModernSurfaceVariantDark
val RoyalNavySurfaceLight = ModernSurfaceLight
val RoyalNavyBorderLight = ModernBorderLight

val RoyalChalkPrimary = ModernTextPrimaryDark
val RoyalSlateSecondary = ModernTextSecondaryDark
val RoyalMutedTertiary = ModernTextMutedDark
val RoyalBorderSubtle = ModernBorderDark
val RoyalHairlineDivider = ModernDividerDark

val RoyalNavyHeaderGradient = ModernPrimaryGradient
val GildedGoldBrush = ModernPrimaryGradient
val AndalusianGoldBorderBrush = ModernPrimaryGradient
val RoyalCardGlowBrush = ModernCardGradientDark
val RoyalHeartActive = ModernHeartActive

val CypressPrimary = ModernIndigoPrimary
val WarmAmberAccent = ModernIndigoPrimary
val WarmAmberLight = ModernIndigoLight
val WarmAmberContainer = ModernIndigoContainer
val MadinahGardenGreen = ModernEmerald
val MadinahGardenBg = ModernEmeraldBg
val MadinahGardenBorder = ModernEmeraldBorder
val ShamSkyNavy = ModernSkyNavy
val ShamSkyBg = ModernSkyNavyBg
val ShamSkyBorder = ModernSkyNavyBorder
val AndalusPlumPurple = ModernViolet
val AndalusPlumBg = ModernVioletBg
val AndalusPlumBorder = ModernVioletBorder
