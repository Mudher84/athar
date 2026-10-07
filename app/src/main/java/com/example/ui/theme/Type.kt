package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

// أربع عائلات (DESIGN_SPEC §2): Amiri للمتون، El Messiri للعناوين والأسماء،
// Tajawal لعناصر الواجهة، Almarai للنصوص الشارحة. كلها مضمّنة في res/font (لا تنزيل).
val Amiri = FontFamily(Font(R.font.amiri, FontWeight.Normal))
val ElMessiri = FontFamily(Font(R.font.el_messiri, FontWeight.Bold))
val Tajawal = FontFamily(Font(R.font.tajawal, FontWeight.Normal))
val Almarai = FontFamily(Font(R.font.almarai, FontWeight.Normal))

/** حدود تكبير خط المتن: من 19 إلى 29 بخطوة 2، وارتفاع السطر = الحجم × 1.7 (≥ 38 عند الافتراضي). */
object AtharMatnScale {
    const val Min = 19
    const val Max = 29
    const val Step = 2
    const val Default = 23
    const val LineHeightRatio = 1.7f

    fun clamp(size: Int) = size.coerceIn(Min, Max)
}

object AtharType {
    // El Messiri — العناوين والأسماء
    val Brand = TextStyle(fontFamily = ElMessiri, fontWeight = FontWeight.Bold, fontSize = 32.sp, lineHeight = 36.sp)
    val ScreenTitle = TextStyle(fontFamily = ElMessiri, fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 36.sp)
    val SheetTitle = TextStyle(fontFamily = ElMessiri, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 30.sp)
    val BookTitle = TextStyle(fontFamily = ElMessiri, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 32.sp)
    val CardTitle = TextStyle(fontFamily = ElMessiri, fontWeight = FontWeight.Bold, fontSize = 18.sp, lineHeight = 26.sp)
    val NarratorName = TextStyle(fontFamily = ElMessiri, fontWeight = FontWeight.SemiBold, fontSize = 16.5.sp, lineHeight = 24.sp)
    val ChainNodeName = TextStyle(fontFamily = ElMessiri, fontWeight = FontWeight.SemiBold, fontSize = 14.5.sp, lineHeight = 18.sp)
    val StatNumber = TextStyle(fontFamily = ElMessiri, fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 34.sp)
    val FactValue = TextStyle(fontFamily = ElMessiri, fontWeight = FontWeight.Bold, fontSize = 16.sp, lineHeight = 22.sp)

    // Amiri — المتون والاقتباسات
    fun matn(sizeSp: Int = AtharMatnScale.Default) = TextStyle(
        fontFamily = Amiri,
        fontWeight = FontWeight.Normal,
        fontSize = sizeSp.sp,
        lineHeight = (sizeSp * AtharMatnScale.LineHeightRatio).toInt().sp,
    )
    val MatnCompact = TextStyle(fontFamily = Amiri, fontWeight = FontWeight.Normal, fontSize = 20.sp, lineHeight = 34.sp)
    val Sanad = TextStyle(fontFamily = Amiri, fontWeight = FontWeight.Normal, fontSize = 18.sp, lineHeight = 32.sp)
    val Callout = TextStyle(fontFamily = Amiri, fontWeight = FontWeight.Bold, fontSize = 17.sp, lineHeight = 26.sp)
    val TashkeelGlyph = TextStyle(fontFamily = Amiri, fontWeight = FontWeight.Normal, fontSize = 22.sp)

    // Tajawal — عناصر الواجهة
    val SearchInput = TextStyle(fontFamily = Tajawal, fontWeight = FontWeight.Normal, fontSize = 16.sp)
    fun capsule(selected: Boolean) = TextStyle(
        fontFamily = Tajawal,
        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
        fontSize = 14.sp,
    )
    val Button = TextStyle(fontFamily = Tajawal, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    val ButtonSmall = TextStyle(fontFamily = Tajawal, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    val Counter = TextStyle(fontFamily = Tajawal, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    val Link = TextStyle(fontFamily = Tajawal, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    val FilterLabel = TextStyle(fontFamily = Tajawal, fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
    val Badge = TextStyle(fontFamily = Tajawal, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    val Secondary = TextStyle(fontFamily = Tajawal, fontWeight = FontWeight.Normal, fontSize = 12.5.sp, lineHeight = 18.sp)
    val Caption = TextStyle(fontFamily = Tajawal, fontWeight = FontWeight.Normal, fontSize = 11.5.sp, lineHeight = 15.sp)
    fun navLabel(active: Boolean) = TextStyle(
        fontFamily = Tajawal,
        fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
        fontSize = 12.sp,
    )

    // Almarai — النصوص الشارحة
    val Body = TextStyle(fontFamily = Almarai, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 25.sp)
    val Description = TextStyle(fontFamily = Almarai, fontWeight = FontWeight.Normal, fontSize = 13.5.sp, lineHeight = 24.sp)
    val Lineage = TextStyle(fontFamily = Almarai, fontWeight = FontWeight.Normal, fontSize = 12.5.sp, lineHeight = 18.sp)
}

/** Typography الافتراضي لمكوّنات Material التي لا نمرّر لها أسلوباً صريحاً */
val AtharTypography = Typography(
    titleLarge = AtharType.SheetTitle,
    titleMedium = AtharType.CardTitle,
    bodyLarge = AtharType.SearchInput,
    bodyMedium = AtharType.Body,
    bodySmall = AtharType.Secondary,
    labelLarge = AtharType.ButtonSmall,
    labelMedium = AtharType.Badge,
    labelSmall = AtharType.Caption,
)
