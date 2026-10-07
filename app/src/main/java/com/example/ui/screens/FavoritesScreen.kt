package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DownloadDone
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.example.domain.model.HadithListItem
import com.example.ui.AtharViewModel
import com.example.ui.components.AtharSearchField
import com.example.ui.components.EmptyState
import com.example.ui.components.ErrorState
import com.example.ui.components.FavoriteButton
import com.example.ui.components.LoadingState
import com.example.ui.components.NarratorDirectory
import com.example.ui.components.RegionBadge
import com.example.ui.components.ScreenHeader
import com.example.ui.components.TransmissionBadge
import com.example.ui.components.atharShadow
import com.example.ui.components.displayText
import com.example.ui.components.quotedMatn
import com.example.ui.components.shortChain
import com.example.ui.theme.AtharColors
import com.example.ui.theme.AtharDimens
import com.example.ui.theme.AtharShapes
import com.example.ui.theme.AtharType
import com.example.util.arabicDigits

/** المحفوظات (DESIGN_SPEC §4.11): كلها في القاعدة المحلية، فهي متاحة دون اتصال دائماً. */
@Composable
fun FavoritesScreen(
    viewModel: AtharViewModel,
    narrators: NarratorDirectory,
    onOpenChain: (HadithListItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val query by viewModel.favoritesQuery.collectAsStateWithLifecycle()
    val total by viewModel.favoritesTotal.collectAsStateWithLifecycle()
    val reading by viewModel.readingSettings.collectAsStateWithLifecycle()
    val items = viewModel.favorites.collectAsLazyPagingItems()

    LazyColumn(
        modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(key = "header") {
            ScreenHeader(
                title = "المحفوظات",
                subtitle = total?.let { if (it == 0) "لم تحفظ شيئاً بعد" else "${it.arabicDigits()} أثراً محفوظاً" }
            ) { OfflineBadge() }
        }
        item(key = "search") {
            AtharSearchField(
                value = query,
                onValueChange = viewModel::onFavoritesQueryChange,
                placeholder = "ابحث في محفوظاتك",
                modifier = Modifier.padding(horizontal = AtharDimens.ScreenPadding)
            )
        }

        when (val refresh = items.loadState.refresh) {
            is LoadState.Loading -> if (items.itemCount == 0) item(key = "loading") { LoadingState() }
            is LoadState.Error -> item(key = "error") {
                ErrorState(refresh.error.message ?: "خطأ في القاعدة المحلية", onRetry = items::retry)
            }
            is LoadState.NotLoading -> if (items.itemCount == 0) item(key = "empty") {
                if (query.isBlank()) {
                    EmptyState("لا محفوظات بعد", "اضغط علامة الحفظ في أي بطاقة أثر لتجده هنا.")
                } else {
                    EmptyState("لا نتائج", "لا أثر في محفوظاتك يطابق هذا البحث.")
                }
            }
        }

        items(
            count = items.itemCount,
            key = items.itemKey { it.id },
            contentType = items.itemContentType { "favorite" }
        ) { index ->
            val item = items[index] ?: return@items
            FavoriteCard(
                item = item,
                narrators = narrators,
                showTashkeel = reading.showTashkeel,
                onRemove = { viewModel.setFavorite(item.id, false) },
                onClick = { onOpenChain(item) }
            )
        }
    }
}

@Composable
private fun OfflineBadge() {
    Row(
        Modifier
            .height(30.dp)
            .clip(AtharShapes.FilterPill)
            .background(AtharColors.CypressContainer)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Rounded.DownloadDone, contentDescription = null, tint = AtharColors.Cypress, modifier = Modifier.size(15.dp))
        Text(" متاحة دون اتصال", style = AtharType.FilterLabel, color = AtharColors.Cypress)
    }
}

@Composable
private fun FavoriteCard(
    item: HadithListItem,
    narrators: NarratorDirectory,
    showTashkeel: Boolean,
    onRemove: () -> Unit,
    onClick: () -> Unit
) {
    val matn = remember(item.matn, showTashkeel) { quotedMatn(displayText(item.matn, showTashkeel)) }
    val chain = remember(item.chainPacked, narrators) { shortChain(item, narrators) }
    Column(
        Modifier
            .padding(horizontal = AtharDimens.ListPadding)
            .fillMaxWidth()
            .atharShadow(6.dp, AtharShapes.CompactCard)
            .clip(AtharShapes.CompactCard)
            .background(AtharColors.Surface)
            .clickable(onClick = onClick)
            .padding(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            item.region?.let { RegionBadge(it, short = true) }
            item.transmissionNote?.let {
                Spacer(Modifier.size(6.dp))
                TransmissionBadge(it)
            }
            Spacer(Modifier.weight(1f))
            FavoriteButton(isFavorite = true, onClick = onRemove)
        }
        Text(
            matn,
            style = AtharType.MatnCompact,
            color = AtharColors.Ink,
            maxLines = 8,
            overflow = TextOverflow.Ellipsis
        )
        HorizontalDivider(color = AtharColors.ReadingPanelBorder, modifier = Modifier.padding(vertical = 10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                chain,
                style = AtharType.Secondary.copy(fontWeight = FontWeight.Bold),
                color = AtharColors.InkSoft,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Text(
                item.bookTitle,
                style = AtharType.Secondary,
                color = AtharColors.Muted,
                maxLines = 1,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}
