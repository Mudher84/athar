// رموز تصميم «أثر» — مرجع مطابق لـ DESIGN_SPEC.md.
// لم يُجرَّب بناء هذا الملف؛ ادمجه مع الثيم الموجود في المشروع وغيّر اسم الحزمة.
package com.example.athar.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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

/** الترميز الإقليمي: النقطة/الحدّ، الحاوية، والنص فوق الحاوية. */
enum class AtharRegion(
    val label: String,
    val dot: Color,
    val container: Color,
    val onContainer: Color,
) {
    Madinah("أهل المدينة", Color(0xFF2E7355), Color(0xFFE3EFE8), Color(0xFF1B4533)),
    Sham("أهل الشام", Color(0xFF25426E), Color(0xFFE2E8F2), Color(0xFF25426E)),
    Andalus("أهل الأندلس", Color(0xFF6D3B66), Color(0xFFEFE4EC), Color(0xFF6D3B66)),
}

val AtharLightColorScheme: ColorScheme = lightColorScheme(
    primary = AtharColors.Cypress,
    onPrimary = Color.White,
    primaryContainer = AtharColors.CypressContainer,
    onPrimaryContainer = AtharColors.Cypress,
    secondary = AtharColors.AmberDeep,
    onSecondary = Color.White,
    secondaryContainer = AtharColors.NavIndicator,
    onSecondaryContainer = AtharColors.Cypress,
    tertiary = AtharColors.Amber,
    onTertiary = Color.White,
    tertiaryContainer = AtharColors.AmberContainer,
    onTertiaryContainer = AtharColors.OnAmberContainer,
    background = AtharColors.Ivory,
    onBackground = AtharColors.Ink,
    surface = AtharColors.Surface,
    onSurface = AtharColors.Ink,
    surfaceVariant = AtharColors.SurfaceTint,
    onSurfaceVariant = AtharColors.Muted,
    outline = AtharColors.Outline,
    outlineVariant = AtharColors.OutlineVariant,
    scrim = AtharColors.Scrim,
)

object AtharShapes {
    val HadithCard = RoundedCornerShape(26.dp)
    val CompactCard = RoundedCornerShape(24.dp)
    val DropdownMenu = RoundedCornerShape(24.dp)
    val ListContainer = RoundedCornerShape(22.dp)
    val Capsule = RoundedCornerShape(22.dp)
    val SearchField = RoundedCornerShape(18.dp)
    val ReadingPanel = RoundedCornerShape(18.dp)
    val InnerCard = RoundedCornerShape(18.dp)
    val PrimaryButton = RoundedCornerShape(16.dp)
    val QuoteCard = RoundedCornerShape(16.dp)
    val IconButton = RoundedCornerShape(14.dp)
    val BottomSheet = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    val Dialog = RoundedCornerShape(28.dp)
    val RegionBadge = RoundedCornerShape(13.dp)
    val FilterPill = RoundedCornerShape(15.dp)
    val NavIndicator = RoundedCornerShape(14.dp)
}

object AtharDimens {
    val ScreenPadding = 20.dp
    val ListPadding = 16.dp
    val CardPadding = 18.dp
    val CardGap = 14.dp
    val FilterRowGap = 8.dp
    val FilterLabelWidth = 38.dp
    val CapsuleHeight = 44.dp
    val CapsuleBorder = 1.5.dp
    val DropdownMenuPadding = 6.dp
    val DropdownMenuMaxHeight = 290.dp
    val DropdownItemHeight = 44.dp
    val SearchFieldHeight = 52.dp
    val ChainNodeSize = 28.dp
    val ChainTreeNodeSize = 36.dp
    val ChainLineWidth = 2.dp
    val MinTouchTarget = 48.dp // التصميم مرسوم على 44؛ Android يطلب 48
}

/** حدود تكبير خط المتن: من 19 إلى 29 بخطوة 2، وارتفاع السطر = الحجم × 1.7. */
object AtharMatnScale {
    const val Min = 19
    const val Max = 29
    const val Step = 2
    const val Default = 23
    const val LineHeightRatio = 1.7f
}

/** مرّر عائلات الخطوط الموجودة في مشروعك (res/font أو Downloadable Fonts). */
class AtharTypography(
    amiri: FontFamily,
    elMessiri: FontFamily,
    private val tajawal: FontFamily,
    almarai: FontFamily,
) {
    private val matnFamily = amiri

    // El Messiri — العناوين والأسماء
    val brand = TextStyle(fontFamily = elMessiri, fontWeight = FontWeight.Bold, fontSize = 32.sp, lineHeight = 36.sp)
    val screenTitle = TextStyle(fontFamily = elMessiri, fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 36.sp)
    val sheetTitle = TextStyle(fontFamily = elMessiri, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 30.sp)
    val bookTitle = TextStyle(fontFamily = elMessiri, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 32.sp)
    val cardTitle = TextStyle(fontFamily = elMessiri, fontWeight = FontWeight.Bold, fontSize = 18.sp, lineHeight = 26.sp)
    val narratorName = TextStyle(fontFamily = elMessiri, fontWeight = FontWeight.SemiBold, fontSize = 16.5.sp, lineHeight = 24.sp)
    val chainNodeName = TextStyle(fontFamily = elMessiri, fontWeight = FontWeight.SemiBold, fontSize = 14.5.sp, lineHeight = 18.sp)
    val statNumber = TextStyle(fontFamily = elMessiri, fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 34.sp)

    // Amiri — المتون والاقتباسات
    fun matn(sizeSp: Int = AtharMatnScale.Default) = TextStyle(
        fontFamily = matnFamily,
        fontWeight = FontWeight.Normal,
        fontSize = sizeSp.sp,
        lineHeight = (sizeSp * AtharMatnScale.LineHeightRatio).sp,
    )
    val matnCompact = TextStyle(fontFamily = amiri, fontWeight = FontWeight.Normal, fontSize = 20.sp, lineHeight = 34.sp)
    val quote = TextStyle(fontFamily = amiri, fontWeight = FontWeight.Normal, fontSize = 18.sp, lineHeight = 30.sp)
    val callout = TextStyle(fontFamily = amiri, fontWeight = FontWeight.Bold, fontSize = 17.sp, lineHeight = 26.sp)
    val prophetLabelTree = TextStyle(fontFamily = amiri, fontWeight = FontWeight.Bold, fontSize = 21.sp, lineHeight = 30.sp)
    val prophetLabelTrack = TextStyle(fontFamily = amiri, fontWeight = FontWeight.Bold, fontSize = 16.sp, lineHeight = 18.sp)

    // Tajawal — عناصر الواجهة
    val searchInput = TextStyle(fontFamily = tajawal, fontWeight = FontWeight.Normal, fontSize = 16.sp)
    fun capsule(selected: Boolean) = TextStyle(
        fontFamily = tajawal,
        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
        fontSize = 14.sp,
    )
    val button = TextStyle(fontFamily = tajawal, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    val filterLabel = TextStyle(fontFamily = tajawal, fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
    val badge = TextStyle(fontFamily = tajawal, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    val secondary = TextStyle(fontFamily = tajawal, fontWeight = FontWeight.Normal, fontSize = 12.5.sp)
    val chainMeta = TextStyle(fontFamily = tajawal, fontWeight = FontWeight.Normal, fontSize = 11.5.sp, lineHeight = 15.sp)
    fun navLabel(active: Boolean) = TextStyle(
        fontFamily = tajawal,
        fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
        fontSize = 12.sp,
    )

    // Almarai — النصوص الشارحة
    val bioBody = TextStyle(fontFamily = almarai, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 25.sp)
    val bookDescription = TextStyle(fontFamily = almarai, fontWeight = FontWeight.Normal, fontSize = 13.5.sp, lineHeight = 24.sp)
    val lineage = TextStyle(fontFamily = almarai, fontWeight = FontWeight.Normal, fontSize = 12.5.sp, lineHeight = 18.sp)
}
