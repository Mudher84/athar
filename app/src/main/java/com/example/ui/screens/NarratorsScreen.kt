package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.domain.model.NarratorWithCount
import com.example.domain.model.Region
import com.example.domain.model.UiState
import com.example.ui.components.AtharSearchField
import com.example.ui.components.EmptyState
import com.example.ui.components.ErrorState
import com.example.ui.components.LoadingState
import com.example.ui.components.RegionDot
import com.example.ui.components.ScreenHeader
import com.example.ui.components.atharShadow
import com.example.ui.theme.AtharColors
import com.example.ui.theme.AtharDimens
import com.example.ui.theme.AtharShapes
import com.example.ui.theme.AtharType
import com.example.ui.theme.palette
import com.example.util.ArabicText
import com.example.util.arabicDigits

/** معجم الرواة (DESIGN_SPEC §4.8): بحث بالاسم، إحصائيات الأقاليم، وفهرس يفتح الترجمة. */
@Composable
fun NarratorsScreen(
    state: UiState<List<NarratorWithCount>>,
    onOpenNarrator: (NarratorWithCount) -> Unit,
    modifier: Modifier = Modifier
) {
    var query by rememberSaveable { mutableStateOf("") }
    var regionFilter by rememberSaveable { mutableStateOf<Region?>(null) }
    val all = (state as? UiState.Success)?.data.orEmpty()

    val visible = remember(all, query, regionFilter) {
        val q = ArabicText.normalizeForSearch(query)
        all.filter { n ->
            (regionFilter == null || n.region == regionFilter) &&
                (q.isEmpty() || ArabicText.normalizeForSearch(n.narrator.name + " " + n.narrator.popularName.orEmpty()).contains(q))
        }
    }

    LazyColumn(modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)) {
        item(key = "header") {
            ScreenHeader(
                title = "معجم الرواة",
                subtitle = if (all.isEmpty()) "رواة المسار في الموسوعة" else "رواة المسار في الموسوعة · ${all.size.arabicDigits()} راوياً"
            )
        }
        item(key = "search") {
            AtharSearchField(
                value = query,
                onValueChange = { query = it },
                placeholder = "ابحث باسم الراوي أو شهرته",
                modifier = Modifier.padding(horizontal = AtharDimens.ScreenPadding, vertical = 12.dp)
            )
        }

        when (state) {
            UiState.Loading -> item(key = "loading") { LoadingState() }
            is UiState.Error -> item(key = "error") { ErrorState(state.message, onRetry = null) }
            UiState.Empty -> item(key = "empty") { EmptyState("لا رواة", "القاعدة المحلية لا تحوي رواة.") }
            is UiState.Success -> {
                item(key = "stats") {
                    RegionStats(all, regionFilter) { regionFilter = if (regionFilter == it) null else it }
                }
                item(key = "index-title") {
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = AtharDimens.ScreenPadding, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("الفهرس", style = AtharType.Link, color = AtharColors.Ink, modifier = Modifier.weight(1f))
                        Text("بحسب عدد المرويات", style = AtharType.Secondary, color = AtharColors.Muted)
                    }
                }
                if (visible.isEmpty()) {
                    item(key = "no-match") { EmptyState("لا تطابق", "لا راوي بهذا الاسم في الإقليم المختار.") }
                }
                itemsIndexed(visible, key = { _, n -> n.narrator.id }) { index, narrator ->
                    NarratorRow(
                        narrator = narrator,
                        isFirst = index == 0,
                        isLast = index == visible.lastIndex,
                        onClick = { onOpenNarrator(narrator) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RegionStats(all: List<NarratorWithCount>, selected: Region?, onSelect: (Region) -> Unit) {
    val counts = remember(all) { all.groupingBy { it.region }.eachCount() }
    Row(
        Modifier.fillMaxWidth().padding(horizontal = AtharDimens.ScreenPadding),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Region.entries.forEach { region ->
            val p = region.palette
            val isSelected = selected == region
            Column(
                Modifier
                    .weight(1f)
                    .clip(AtharShapes.InnerCard)
                    .background(p.container)
                    .border(if (isSelected) 1.5.dp else 0.dp, if (isSelected) p.dot else p.container, AtharShapes.InnerCard)
                    .clickable(role = Role.Tab) { onSelect(region) }
                    .semantics { this.selected = isSelected }
                    .padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RegionDot(p.dot, 8.dp)
                    Text(" " + region.label, style = AtharType.FilterLabel, color = p.onContainer, maxLines = 1)
                }
                Row(verticalAlignment = Alignment.Bottom) {
                    Text((counts[region] ?: 0).arabicDigits(), style = AtharType.StatNumber, color = p.onContainer)
                    Text(" راوياً", style = AtharType.Secondary, color = p.onContainer, modifier = Modifier.padding(bottom = 6.dp))
                }
            }
        }
    }
}

@Composable
private fun NarratorRow(narrator: NarratorWithCount, isFirst: Boolean, isLast: Boolean, onClick: () -> Unit) {
    val p = narrator.region.palette
    val shape = RoundedCornerShape(
        topStart = if (isFirst) 22.dp else 0.dp,
        topEnd = if (isFirst) 22.dp else 0.dp,
        bottomStart = if (isLast) 22.dp else 0.dp,
        bottomEnd = if (isLast) 22.dp else 0.dp
    )
    val name = narrator.displayName
    Column(
        Modifier
            .padding(horizontal = AtharDimens.ListPadding)
            .then(if (isFirst || isLast) Modifier.atharShadow(2.dp, shape) else Modifier)
            .clip(shape)
            .background(AtharColors.Surface)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().heightIn(min = 64.dp).padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.size(40.dp).clip(CircleShape).background(p.container),
                contentAlignment = Alignment.Center
            ) {
                Text(initialOf(name), style = AtharType.CardTitle, color = p.dot)
            }
            Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(name, style = AtharType.NarratorName, color = AtharColors.Ink)
                Text(
                    "${narrator.narrator.era} · ${narrator.hadithCount.arabicDigits()} رواية",
                    style = AtharType.Secondary,
                    color = AtharColors.Muted
                )
            }
            Text(narrator.region.shortLabel, style = AtharType.Badge, color = p.onContainer)
            Icon(
                Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = AtharColors.Muted,
                modifier = Modifier.size(16.dp)
            )
        }
        if (!isLast) HorizontalDivider(color = AtharColors.ReadingPanelBorder)
    }
}

/** الحرف الأول بعد «ال» و«أبو/ابن» حتى لا تتشابه الدوائر */
private fun initialOf(name: String): String {
    val word = name.trim().split(' ').firstOrNull { it !in setOf("أبو", "ابن", "الإمام") } ?: name
    val stem = if (word.startsWith("ال") && word.length > 2) word.substring(2) else word
    return stem.take(1)
}
