package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

object AtharShapes {
    val HadithCard = RoundedCornerShape(26.dp)
    val CompactCard = RoundedCornerShape(24.dp)
    val ListContainer = RoundedCornerShape(22.dp)
    val Capsule = RoundedCornerShape(22.dp)
    val SearchField = RoundedCornerShape(18.dp)
    val ReadingPanel = RoundedCornerShape(18.dp)
    val InnerCard = RoundedCornerShape(18.dp)
    val PrimaryButton = RoundedCornerShape(16.dp)
    val IconButton = RoundedCornerShape(14.dp)
    val BottomSheet = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    val Dialog = RoundedCornerShape(28.dp)
    val RegionBadge = RoundedCornerShape(13.dp)
    val FilterPill = RoundedCornerShape(15.dp)
}

object AtharDimens {
    val ScreenPadding = 20.dp
    val ListPadding = 16.dp
    val CardPadding = 18.dp
    val CardGap = 14.dp
    val FilterLabelWidth = 48.dp
    val CapsuleHeight = 48.dp // التصميم 44؛ Android يطلب 48 لهدف اللمس
    val CapsuleBorder = 1.5.dp
    val DropdownMaxHeight = 290.dp
    val SearchFieldHeight = 52.dp
    val ChainTreeNodeSize = 36.dp
    val ChainLineWidth = 2.dp
    val MinTouchTarget = 48.dp
}

private val AtharLightColorScheme = lightColorScheme(
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
    surface = AtharColors.Ivory,
    onSurface = AtharColors.Ink,
    surfaceVariant = AtharColors.SurfaceTint,
    onSurfaceVariant = AtharColors.Muted,
    // القوائم المنسدلة والنوافذ تأخذ هذه الطبقات في Material 3
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color.White,
    surfaceContainer = Color.White,
    surfaceContainerHigh = AtharColors.Ivory,
    surfaceContainerHighest = AtharColors.SurfaceTint,
    outline = AtharColors.Outline,
    outlineVariant = AtharColors.OutlineVariant,
    scrim = AtharColors.Scrim,
)

private val AtharMaterialShapes = Shapes(
    // DropdownMenu يستعمل extraSmall
    extraSmall = RoundedCornerShape(24.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(26.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

/**
 * ثيم «أثر»: فاتح فقط (الوضع الليلي غير مصمَّم)، والاتجاه من اليمين إلى اليسار في كل الشاشات
 * بصرف النظر عن لغة الجهاز.
 */
@Composable
fun AtharTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AtharLightColorScheme,
        typography = AtharTypography,
        shapes = AtharMaterialShapes,
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl, content = content)
    }
}
