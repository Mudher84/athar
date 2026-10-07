package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AtharColors
import com.example.ui.theme.AtharDimens
import com.example.ui.theme.AtharShapes
import com.example.ui.theme.AtharType
import com.example.ui.theme.RegionPalette

/**
 * خيار في [AtharCapsuleDropdown]. [value] = null هو خيار «الكل».
 * [palette] لخيارات الأقاليم فقط (نقطة ملوّنة وحاوية الإقليم عند الاختيار)؛ «كل الأقاليم» تأخذ NeutralPalette.
 */
@Immutable
data class DropdownOption<T>(val value: T?, val label: String, val palette: RegionPalette? = null)

/**
 * صف فلتر: تسمية بعرض ثابت + كبسولة تفتح قائمة بعرضها نفسه (DESIGN_SPEC §4.3).
 * الفتح يُدار من الخارج ([expanded]) حتى تبقى قائمة واحدة مفتوحة في كل مرة.
 */
@Composable
fun <T> AtharCapsuleDropdown(
    label: String,
    options: List<DropdownOption<T>>,
    selected: T?,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onSelect: (T?) -> Unit,
    modifier: Modifier = Modifier
) {
    val current = options.firstOrNull { it.value == selected } ?: options.first()
    val isAll = current.value == null
    val palette = current.palette
    val background = when {
        isAll -> AtharColors.Surface
        palette != null -> palette.container
        else -> AtharColors.CypressContainer
    }
    val border = when {
        isAll -> AtharColors.Outline
        palette != null -> palette.dot
        else -> AtharColors.Cypress
    }
    val textColor = when {
        isAll -> AtharColors.InkSoft
        palette != null -> palette.onContainer
        else -> AtharColors.Cypress
    }
    val arrowRotation by animateFloatAsState(if (expanded) 180f else 0f, tween(150), label = "arrow")
    var capsuleWidthPx by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current

    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = AtharType.FilterLabel, color = AtharColors.Muted, modifier = Modifier.width(AtharDimens.FilterLabelWidth))
        Box(Modifier.weight(1f)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AtharDimens.CapsuleHeight)
                    .onSizeChanged { capsuleWidthPx = it.width }
                    .clip(AtharShapes.Capsule)
                    .background(background)
                    .border(AtharDimens.CapsuleBorder, border, AtharShapes.Capsule)
                    .clickable(role = Role.DropdownList) { onExpandedChange(!expanded) }
                    .semantics {
                        contentDescription = "$label: ${current.label}"
                        stateDescription = if (expanded) "مفتوحة" else "مغلقة"
                    }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                current.palette?.let { RegionDot(it.dot, 9.dp) }
                Text(
                    current.label,
                    style = AtharType.capsule(!isAll),
                    color = textColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    Icons.Rounded.KeyboardArrowDown,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(18.dp).rotate(arrowRotation)
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) },
                offset = DpOffset(0.dp, 6.dp),
                modifier = Modifier
                    .width(with(density) { capsuleWidthPx.toDp() })
                    .heightIn(max = AtharDimens.DropdownMaxHeight)
                    .border(1.dp, AtharColors.OutlineVariant, AtharShapes.CompactCard)
            ) {
                Rtl {
                    options.forEach { option ->
                        DropdownRow(
                            option = option,
                            isSelected = option.value == current.value,
                            onClick = {
                                onSelect(option.value)
                                onExpandedChange(false)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun <T> DropdownRow(option: DropdownOption<T>, isSelected: Boolean, onClick: () -> Unit) {
    val p = option.palette
    val bg = when {
        !isSelected -> Color.Transparent
        option.value == null -> AtharColors.SurfaceTint
        p != null -> p.container
        else -> AtharColors.CypressContainer
    }
    val fg = when {
        !isSelected -> AtharColors.Ink
        p != null && option.value != null -> p.onContainer
        else -> AtharColors.Cypress
    }
    Row(
        modifier = Modifier
            .padding(horizontal = 6.dp, vertical = 1.dp)
            .fillMaxWidth()
            .heightIn(min = AtharDimens.MinTouchTarget)
            .clip(AtharShapes.Capsule)
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        p?.let { RegionDot(it.dot, 9.dp) }
        Text(
            option.label,
            style = AtharType.capsule(isSelected),
            color = fg,
            modifier = Modifier.weight(1f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        if (isSelected) {
            Icon(Icons.Rounded.Check, contentDescription = "مختار", tint = fg, modifier = Modifier.size(18.dp))
        }
    }
}
