package com.example.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.example.domain.model.Region

/** ألوان «أثر» — مطابقة لـ design-handoff/DESIGN_SPEC.md §1. الوضع الليلي غير مصمَّم بعد. */
object AtharColors {
    // الأساس
    val Ivory = Color(0xFFFAF7EE)
    val Surface = Color(0xFFFFFFFF)
    val ReadingPanel = Color(0xFFFBF8F0)
    val ReadingPanelBorder = Color(0xFFEFE8D6)
    val SurfaceTint = Color(0xFFF3EEDF)
    val SurfaceDim = Color(0xFFF1EBDA)
    val Outline = Color(0xFFE1D9C3)
    val OutlineVariant = Color(0xFFEAE3CF)
    val Handle = Color(0xFFD5CCB3)

    // الأخضر الأساسي
    val Cypress = Color(0xFF1B4533)
    val CypressPressed = Color(0xFF123324)
    val CypressContainer = Color(0xFFE4ECE5)
    val NavIndicator = Color(0xFFDCE8DF)
    val OnCypressMuted = Color(0xFFD5E3D9)
    val OnCypressDot = Color(0xFF8FD0AC)

    // العنبري — Amber للزخرفة فقط، و AmberDeep للنص
    val Amber = Color(0xFFC98628)
    val AmberDeep = Color(0xFF8A5A12)
    val AmberContainer = Color(0xFFF6EAD2)
    val OnAmberContainer = Color(0xFF3F2C06)
    val OnAmberContainerMuted = Color(0xFF6B4A10)
    val CoverGold = Color(0xFFF3DDAE)

    // النص
    val Ink = Color(0xFF1E2823)
    val InkSoft = Color(0xFF3C4841)
    val Muted = Color(0xFF5F6B63)
    val NeutralDot = Color(0xFF8A8F86)

    // الستار خلف النافذة السفلية والحوار
    val Scrim = Color(0xFF0F2319)
}

/** ألوان الإقليم: النقطة/الحدّ، الحاوية، والنص فوق الحاوية. */
@Immutable
data class RegionPalette(val dot: Color, val container: Color, val onContainer: Color)

private val MadinahPalette = RegionPalette(Color(0xFF2E7355), Color(0xFFE3EFE8), Color(0xFF1B4533))
private val ShamPalette = RegionPalette(Color(0xFF25426E), Color(0xFFE2E8F2), Color(0xFF25426E))

// «مشترك» غير موجود في حزمة التصميم: عنبري يجمع اللونين دون أن يشبه أحدهما
private val SharedPalette = RegionPalette(Color(0xFF8A5A12), Color(0xFFF6EAD2), Color(0xFF6B4A10))

/** خيار «الكل» */
val NeutralPalette = RegionPalette(AtharColors.NeutralDot, AtharColors.SurfaceTint, AtharColors.InkSoft)

/** اللون لا يُستعمل وحده أبداً: كل شارة تحمل اسم الإقليم نصاً. */
val Region.palette: RegionPalette
    get() = when (this) {
        Region.Madinah -> MadinahPalette
        Region.Sham -> ShamPalette
        Region.Shared -> SharedPalette
    }
