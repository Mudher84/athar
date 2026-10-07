package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.example.domain.model.BookWithCount
import com.example.domain.model.HadithFilters
import com.example.domain.model.HadithListItem
import com.example.domain.model.NarratorWithCount
import com.example.domain.model.Region
import com.example.domain.model.UiState
import com.example.ui.AtharViewModel
import com.example.ui.components.AtharCapsuleDropdown
import com.example.ui.components.AtharSearchField
import com.example.ui.components.DropdownOption
import com.example.ui.components.EmptyState
import com.example.ui.components.ErrorState
import com.example.ui.components.HadithCard
import com.example.ui.components.LoadingState
import com.example.ui.components.NarratorDirectory
import com.example.ui.components.OutlinedSquareButton
import com.example.ui.components.RegionDot
import com.example.ui.theme.AtharColors
import com.example.ui.theme.AtharDimens
import com.example.ui.theme.AtharMatnScale
import com.example.ui.theme.AtharShapes
import com.example.ui.theme.AtharType
import com.example.ui.theme.NeutralPalette
import com.example.ui.theme.RegionPalette
import com.example.ui.theme.palette
import com.example.util.arabicDigits

private enum class FilterMenu { Region, Book, Narrator, Connection }

@Composable
fun HadithsScreen(
    viewModel: AtharViewModel,
    narrators: NarratorDirectory,
    onOpenChain: (HadithListItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val query by viewModel.query.collectAsStateWithLifecycle()
    val filters by viewModel.filters.collectAsStateWithLifecycle()
    val count by viewModel.resultCount.collectAsStateWithLifecycle()
    val reading by viewModel.readingSettings.collectAsStateWithLifecycle()
    val booksState by viewModel.books.collectAsStateWithLifecycle()
    val narratorsState by viewModel.narrators.collectAsStateWithLifecycle()
    val items = viewModel.hadiths.collectAsLazyPagingItems()
    val listState = rememberLazyListState()

    val books = (booksState as? UiState.Success)?.data.orEmpty()
    val narratorList = (narratorsState as? UiState.Success)?.data.orEmpty()

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item(key = "header", contentType = "header") {
            MainHeader(
                showTashkeel = reading.showTashkeel,
                matnSize = reading.matnSize,
                onToggleTashkeel = viewModel::toggleTashkeel,
                onSmaller = viewModel::decreaseMatnSize,
                onLarger = viewModel::increaseMatnSize
            )
        }
        item(key = "search", contentType = "search") {
            AtharSearchField(
                value = query,
                onValueChange = viewModel::onQueryChange,
                placeholder = "ابحث في المتن أو السند",
                modifier = Modifier.padding(horizontal = AtharDimens.ScreenPadding, vertical = 12.dp)
            )
        }
        item(key = "filters", contentType = "filters") {
            FilterBlock(
                filters = filters,
                books = books,
                narrators = narratorList,
                onRegion = viewModel::setRegion,
                onBook = viewModel::setBook,
                onNarrator = viewModel::setNarrator,
                onConnected = viewModel::setConnectedOnly
            )
        }
        item(key = "results", contentType = "results") {
            ResultsBar(
                count = count,
                filters = filters,
                books = books,
                narrators = narrators,
                onClearRegion = { viewModel.setRegion(null) },
                onClearBook = { viewModel.setBook(null) },
                onClearNarrator = { viewModel.setNarrator(null) },
                onClearConnected = { viewModel.setConnectedOnly(false) },
                onResetAll = viewModel::resetFilters
            )
        }

        when (val refresh = items.loadState.refresh) {
            is LoadState.Loading -> if (items.itemCount == 0) {
                item(key = "loading", contentType = "state") { LoadingState() }
            }
            is LoadState.Error -> item(key = "error", contentType = "state") {
                ErrorState(refresh.error.message ?: "خطأ في القاعدة المحلية", onRetry = items::retry)
            }
            is LoadState.NotLoading -> if (items.itemCount == 0) {
                item(key = "empty", contentType = "state") {
                    EmptyState(
                        title = "لا نتائج",
                        message = if (query.isNotBlank()) "جرّب كلمة أقصر أو أزل بعض الفلاتر؛ البحث لا يتأثر بالتشكيل."
                        else "لا روايات تطابق هذه الفلاتر مجتمعة."
                    )
                }
            }
        }

        items(
            count = items.itemCount,
            key = items.itemKey { it.id },
            contentType = items.itemContentType { "hadith" }
        ) { index ->
            val item = items[index] ?: return@items
            HadithCard(
                item = item,
                narrators = narrators,
                matnSize = reading.matnSize,
                showTashkeel = reading.showTashkeel,
                onToggleFavorite = { viewModel.setFavorite(it.id, !it.isFavorite) },
                onOpenChain = onOpenChain,
                modifier = Modifier.padding(horizontal = AtharDimens.ListPadding, vertical = 7.dp)
            )
        }

        if (items.loadState.append is LoadState.Loading) {
            item(key = "append", contentType = "state") { LoadingState() }
        }
    }
}

@Composable
private fun MainHeader(
    showTashkeel: Boolean,
    matnSize: Int,
    onToggleTashkeel: () -> Unit,
    onSmaller: () -> Unit,
    onLarger: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = AtharDimens.ScreenPadding, end = AtharDimens.ScreenPadding, top = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text("أثر", style = AtharType.Brand, color = AtharColors.Cypress)
            Text("آثار أهل المدينة والشام", style = AtharType.Caption, color = AtharColors.Muted)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedSquareButton(
                onClick = onToggleTashkeel,
                selected = showTashkeel,
                modifier = Modifier.semantics {
                    contentDescription = if (showTashkeel) "إخفاء التشكيل" else "إظهار التشكيل"
                }
            ) {
                Text("ضَ", style = AtharType.TashkeelGlyph, color = if (showTashkeel) AtharColors.Cypress else AtharColors.InkSoft)
            }
            Row(
                Modifier
                    .height(AtharDimens.MinTouchTarget)
                    .clip(AtharShapes.IconButton)
                    .background(AtharColors.Surface)
                    .border(1.dp, AtharColors.Outline, AtharShapes.IconButton),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FontStepButton("A−", "تصغير الخط", enabled = matnSize > AtharMatnScale.Min, onClick = onSmaller)
                Box(Modifier.width(1.dp).fillMaxHeight().padding(vertical = 10.dp).background(AtharColors.Outline))
                FontStepButton("A+", "تكبير الخط", enabled = matnSize < AtharMatnScale.Max, onClick = onLarger)
            }
        }
    }
}

@Composable
private fun FontStepButton(label: String, description: String, enabled: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .size(AtharDimens.MinTouchTarget)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center
    ) {
        Text(label, style = AtharType.ButtonSmall, color = if (enabled) AtharColors.InkSoft else AtharColors.Outline)
    }
}

@Composable
private fun FilterBlock(
    filters: HadithFilters,
    books: List<BookWithCount>,
    narrators: List<NarratorWithCount>,
    onRegion: (Region?) -> Unit,
    onBook: (Int?) -> Unit,
    onNarrator: (Int?) -> Unit,
    onConnected: (Boolean) -> Unit
) {
    var openMenu by rememberSaveable { mutableStateOf<FilterMenu?>(null) }
    val regionOptions = remember {
        listOf(DropdownOption<Region>(null, "كل الأقاليم", NeutralPalette)) +
            Region.entries.map { DropdownOption(it, it.label, it.palette) }
    }
    val bookOptions = remember(books) {
        listOf(DropdownOption<Int>(null, "كل الكتب")) + books.map { DropdownOption(it.book.id, it.book.title) }
    }
    val narratorOptions = remember(narrators) {
        listOf(DropdownOption<Int>(null, "كل الرواة")) +
            narrators.map { DropdownOption(it.narrator.id, it.displayName) }
    }
    val connectionOptions = remember {
        listOf(DropdownOption<Boolean>(null, "كل الروايات"), DropdownOption(true, "المتصل فقط (دون المراسيل والبلاغات)"))
    }

    fun toggler(menu: FilterMenu): (Boolean) -> Unit = { open -> openMenu = if (open) menu else null }

    Column(
        Modifier.padding(horizontal = AtharDimens.ScreenPadding).padding(bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AtharCapsuleDropdown(
            label = "الإقليم",
            options = regionOptions,
            selected = filters.region,
            expanded = openMenu == FilterMenu.Region,
            onExpandedChange = toggler(FilterMenu.Region),
            onSelect = onRegion
        )
        AtharCapsuleDropdown(
            label = "الكتاب",
            options = bookOptions,
            selected = filters.bookId,
            expanded = openMenu == FilterMenu.Book,
            onExpandedChange = toggler(FilterMenu.Book),
            onSelect = onBook
        )
        AtharCapsuleDropdown(
            label = "الراوي",
            options = narratorOptions,
            selected = filters.narratorId,
            expanded = openMenu == FilterMenu.Narrator,
            onExpandedChange = toggler(FilterMenu.Narrator),
            onSelect = onNarrator
        )
        AtharCapsuleDropdown(
            label = "الاتصال",
            options = connectionOptions,
            selected = if (filters.connectedOnly) true else null,
            expanded = openMenu == FilterMenu.Connection,
            onExpandedChange = toggler(FilterMenu.Connection),
            onSelect = { onConnected(it == true) }
        )
    }
}

@Composable
private fun ResultsBar(
    count: Int?,
    filters: HadithFilters,
    books: List<BookWithCount>,
    narrators: NarratorDirectory,
    onClearRegion: () -> Unit,
    onClearBook: () -> Unit,
    onClearNarrator: () -> Unit,
    onClearConnected: () -> Unit,
    onResetAll: () -> Unit
) {
    Column(Modifier.padding(horizontal = AtharDimens.ScreenPadding).padding(bottom = 8.dp)) {
        HorizontalDivider(color = AtharColors.OutlineVariant)
        Row(
            Modifier.fillMaxWidth().heightIn(min = AtharDimens.MinTouchTarget),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                if (count == null) "…" else "${count.arabicDigits()} رواية",
                style = AtharType.Counter,
                color = AtharColors.Ink
            )
            Spacer(Modifier.width(10.dp))
            Row(
                Modifier.weight(1f).horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                filters.region?.let { FilterPill(it.label, it.palette, onClearRegion) }
                filters.bookId?.let { id ->
                    FilterPill(books.firstOrNull { it.book.id == id }?.book?.title ?: "كتاب", null, onClearBook)
                }
                filters.narratorId?.let { id ->
                    FilterPill(narrators[id]?.displayName ?: "راوٍ", null, onClearNarrator)
                }
                if (filters.connectedOnly) FilterPill("المتصل فقط", null, onClearConnected)
            }
            if (filters.isActive) {
                Text(
                    "تصفير الكل",
                    style = AtharType.Link,
                    color = AtharColors.AmberDeep,
                    modifier = Modifier
                        .clip(AtharShapes.IconButton)
                        .clickable(role = Role.Button, onClick = onResetAll)
                        .padding(horizontal = 8.dp, vertical = 14.dp)
                )
            }
        }
    }
}

/** شارة فلتر قابلة للإلغاء: ارتفاع 30، r15 (DESIGN_SPEC §4.4) */
@Composable
private fun FilterPill(label: String, palette: RegionPalette?, onClear: () -> Unit) {
    val bg = palette?.container ?: AtharColors.CypressContainer
    val fg = palette?.onContainer ?: AtharColors.Cypress
    Surface(
        onClick = onClear,
        shape = AtharShapes.FilterPill,
        color = bg,
        border = palette?.let { BorderStroke(1.dp, it.dot.copy(alpha = 0.35f)) },
        modifier = Modifier.semantics { contentDescription = "إزالة فلتر $label" }
    ) {
        Row(
            Modifier.height(30.dp).padding(start = 10.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            palette?.let { RegionDot(it.dot) }
            Text(label, style = AtharType.Secondary, color = fg, maxLines = 1)
            Icon(Icons.Rounded.Close, contentDescription = null, tint = fg, modifier = Modifier.size(12.dp))
        }
    }
}
