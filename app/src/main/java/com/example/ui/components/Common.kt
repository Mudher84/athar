package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.domain.model.Region
import com.example.ui.theme.AtharColors
import com.example.ui.theme.AtharDimens
import com.example.ui.theme.AtharShapes
import com.example.ui.theme.AtharType
import com.example.ui.theme.palette
import com.example.util.ArabicText

/**
 * النوافذ المنبثقة (الحوار، النافذة السفلية، القائمة) تُركَّب في نافذة مستقلة تأخذ اتجاهها
 * من لغة الجهاز؛ نعيد فرض RTL داخلها.
 */
@Composable
fun Rtl(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl, content = content)
}

/** ظل بلون الأخضر الأساسي لا الأسود (DESIGN_SPEC §3) */
fun Modifier.atharShadow(elevation: Dp, shape: Shape): Modifier =
    shadow(elevation, shape, clip = false, ambientColor = AtharColors.Cypress, spotColor = AtharColors.Cypress)

@Composable
fun RegionDot(color: Color, size: Dp = 7.dp) {
    Box(Modifier.size(size).clip(CircleShape).background(color))
}

/** شارة الإقليم: ارتفاع 26، نقطة 7 + الاسم. اللون لا يُستعمل وحده. */
@Composable
fun RegionBadge(region: Region, modifier: Modifier = Modifier, short: Boolean = false) {
    val p = region.palette
    Row(
        modifier = modifier
            .height(26.dp)
            .clip(AtharShapes.RegionBadge)
            .background(p.container)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        RegionDot(p.dot)
        Text(if (short) region.shortLabel else region.label, style = AtharType.Badge, color = p.onContainer)
    }
}

/** شارة «مرسل»/«بلاغ»: تنبيه نصي لا يعتمد على اللون */
@Composable
fun TransmissionBadge(note: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(26.dp)
            .clip(AtharShapes.RegionBadge)
            .border(1.dp, AtharColors.Amber, AtharShapes.RegionBadge)
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(note, style = AtharType.Badge, color = AtharColors.AmberDeep)
    }
}

@Composable
fun ScreenHeader(title: String, subtitle: String?, modifier: Modifier = Modifier, trailing: @Composable () -> Unit = {}) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = AtharDimens.ScreenPadding, end = AtharDimens.ScreenPadding, top = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = AtharType.ScreenTitle, color = AtharColors.Cypress)
            if (subtitle != null) {
                Text(subtitle, style = AtharType.Secondary, color = AtharColors.Muted)
            }
        }
        trailing()
    }
}

/** حقل البحث: ارتفاع 52، r18، أيقونة بحث، وزر مسح عند وجود نص (DESIGN_SPEC §4.2) */
@Composable
fun AtharSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(AtharDimens.SearchFieldHeight)
            .atharShadow(1.dp, AtharShapes.SearchField)
            .clip(AtharShapes.SearchField)
            .background(AtharColors.Surface)
            .border(1.dp, AtharColors.Outline, AtharShapes.SearchField)
            .padding(start = 14.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Rounded.Search, contentDescription = null, tint = AtharColors.Cypress, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(10.dp))
        Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
            if (value.isEmpty()) {
                Text(placeholder, style = AtharType.SearchInput, color = AtharColors.Muted, maxLines = 1)
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = AtharType.SearchInput.copy(color = AtharColors.Ink),
                cursorBrush = SolidColor(AtharColors.Cypress),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (value.isNotEmpty()) {
            IconButton(onClick = { onValueChange("") }) {
                Icon(Icons.Rounded.Close, contentDescription = "مسح البحث", tint = AtharColors.Muted, modifier = Modifier.size(20.dp))
            }
        }
    }
}

/** المتن بين « » عنبريتين */
fun quotedMatn(matn: String): AnnotatedString = buildAnnotatedString {
    withStyle(SpanStyle(color = AtharColors.Amber)) { append("« ") }
    append(matn.trim())
    withStyle(SpanStyle(color = AtharColors.Amber)) { append(" »") }
}

/** النص للعرض: مشكول أو مجرّد بحسب إعداد القارئ */
fun displayText(text: String, showTashkeel: Boolean): String =
    if (showTashkeel) text else ArabicText.stripTashkeel(text)

@Composable
fun LoadingState(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth().padding(vertical = 48.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = AtharColors.Cypress, strokeWidth = 2.5.dp, modifier = Modifier.size(32.dp))
    }
}

@Composable
fun EmptyState(title: String, message: String, modifier: Modifier = Modifier, action: (@Composable () -> Unit)? = null) {
    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(title, style = AtharType.CardTitle, color = AtharColors.Cypress, textAlign = TextAlign.Center)
        Text(message, style = AtharType.Description, color = AtharColors.Muted, textAlign = TextAlign.Center)
        action?.invoke()
    }
}

@Composable
fun ErrorState(message: String, onRetry: (() -> Unit)?, modifier: Modifier = Modifier) {
    EmptyState(
        title = "تعذّر تحميل البيانات",
        message = message,
        modifier = modifier,
        action = if (onRetry == null) null else {
            @Composable {
                TextButton(onClick = onRetry) { Text("إعادة المحاولة", style = AtharType.ButtonSmall, color = AtharColors.Cypress) }
            }
        }
    )
}

/** زر أيقونة مربّع 48 بإطار (زر التشكيل وأمثاله) */
@Composable
fun OutlinedSquareButton(
    onClick: () -> Unit,
    selected: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier.size(AtharDimens.MinTouchTarget),
        shape = AtharShapes.IconButton,
        color = if (selected) AtharColors.CypressContainer else AtharColors.Surface,
        border = BorderStroke(1.dp, if (selected) AtharColors.Cypress else AtharColors.Outline),
    ) {
        Box(contentAlignment = Alignment.Center) { content() }
    }
}

/** زر أساسي ممتلئ: ارتفاع 50، r16 */
@Composable
fun PrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().heightIn(min = 50.dp),
        shape = AtharShapes.PrimaryButton,
        color = AtharColors.Cypress,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text, style = AtharType.Button, color = Color.White)
        }
    }
}
