package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.BookWithCount
import com.example.domain.model.UiState
import com.example.ui.components.EmptyState
import com.example.ui.components.ErrorState
import com.example.ui.components.LoadingState
import com.example.ui.components.PrimaryButton
import com.example.ui.components.ScreenHeader
import com.example.ui.components.atharShadow
import com.example.ui.theme.Amiri
import com.example.ui.theme.AtharColors
import com.example.ui.theme.AtharDimens
import com.example.ui.theme.AtharShapes
import com.example.ui.theme.AtharType
import com.example.util.arabicDigits

/**
 * خزانة الكتب (DESIGN_SPEC §4.10). الكتب نفسها لا تُنسب إلى إقليم (كلها تجمع مسارات مختلفة)،
 * فلا منسدلة إقليم هنا؛ الإقليم فلتر على الروايات في تبويب الآثار.
 */
@Composable
fun BooksScreen(
    state: UiState<List<BookWithCount>>,
    onBrowseBook: (BookWithCount) -> Unit,
    modifier: Modifier = Modifier
) {
    val books = (state as? UiState.Success)?.data.orEmpty()
    val total = books.sumOf { it.hadithCount }
    LazyColumn(
        modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(key = "header") {
            ScreenHeader(
                title = "خزانة الكتب",
                subtitle = if (books.isEmpty()) "مصادر الموسوعة"
                else "${books.size.arabicDigits()} مصادر · ${total.arabicDigits()} رواية مختارة على معايير المسار"
            )
        }
        when (state) {
            UiState.Loading -> item(key = "loading") { LoadingState() }
            is UiState.Error -> item(key = "error") { ErrorState(state.message, onRetry = null) }
            UiState.Empty -> item(key = "empty") { EmptyState("لا كتب", "القاعدة المحلية لا تحوي كتباً.") }
            is UiState.Success -> {
                val featured = books.first()
                item(key = "featured-${featured.book.id}") { FeaturedBook(featured) { onBrowseBook(featured) } }
                items(books.drop(1), key = { it.book.id }) { book ->
                    BookRow(book) { onBrowseBook(book) }
                }
            }
        }
    }
}

@Composable
private fun BookCover(title: String, author: String?, width: Dp, height: Dp, titleSize: Int) {
    Box(
        Modifier
            .size(width, height)
            .clip(AtharShapes.IconButton)
            .background(AtharColors.Cypress)
            .padding(if (author != null) 8.dp else 5.dp)
            .border(1.dp, AtharColors.Amber, AtharShapes.IconButton)
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(Modifier.width(18.dp).height(1.dp).background(AtharColors.Amber))
            Text(
                title,
                style = AtharType.Callout.copy(fontFamily = Amiri, fontSize = titleSize.sp, lineHeight = (titleSize * 1.25f).sp),
                color = AtharColors.CoverGold,
                textAlign = TextAlign.Center
            )
            Box(Modifier.width(18.dp).height(1.dp).background(AtharColors.Amber))
            if (author != null) {
                Text(author, style = AtharType.Caption.copy(fontSize = 11.sp), color = AtharColors.OnCypressMuted, textAlign = TextAlign.Center)
            }
        }
    }
}

/** العنوان المختصر للغلاف: «موطأ الإمام مالك» ← «الموطأ» */
private fun coverTitle(title: String): String = when {
    title.startsWith("موطأ") -> "الموطأ"
    else -> title
}

@Composable
private fun FeaturedBook(book: BookWithCount, onBrowse: () -> Unit) {
    Column(
        Modifier
            .padding(horizontal = AtharDimens.ListPadding)
            .fillMaxWidth()
            .atharShadow(10.dp, AtharShapes.HadithCard)
            .clip(AtharShapes.HadithCard)
            .background(AtharColors.Surface)
            .padding(AtharDimens.CardPadding),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            BookCover(coverTitle(book.book.title), book.book.author, 108.dp, 152.dp, 24)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(book.book.title, style = AtharType.BookTitle, color = AtharColors.Cypress)
                Text(book.book.author, style = AtharType.Secondary, color = AtharColors.InkSoft)
                Text(book.book.era, style = AtharType.Secondary, color = AtharColors.Muted)
                Spacer(Modifier.height(4.dp))
                Text(
                    "${book.hadithCount.arabicDigits()} رواية في الموسوعة",
                    style = AtharType.Counter,
                    color = AtharColors.Ink
                )
            }
        }
        Text(
            "الروايات المدرجة هي ما وافق رواة مساري المدينة والشام من هذا الكتاب، مع وسم المراسيل والبلاغات.",
            style = AtharType.Description,
            color = AtharColors.InkSoft
        )
        PrimaryButton("تصفّح آثار الكتاب", onClick = onBrowse)
    }
}

@Composable
private fun BookRow(book: BookWithCount, onClick: () -> Unit) {
    Row(
        Modifier
            .padding(horizontal = AtharDimens.ListPadding)
            .fillMaxWidth()
            .atharShadow(6.dp, AtharShapes.ListContainer)
            .clip(AtharShapes.ListContainer)
            .background(AtharColors.Surface)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        BookCover(book.book.title.substringAfter(' ').ifBlank { book.book.title }, null, 60.dp, 84.dp, 13)
        Column(Modifier.weight(1f)) {
            Text(book.book.title, style = AtharType.CardTitle, color = AtharColors.Ink)
            Text(book.book.author, style = AtharType.Secondary, color = AtharColors.InkSoft)
            Text(
                "${book.book.era} · ${book.hadithCount.arabicDigits()} رواية",
                style = AtharType.Secondary,
                color = AtharColors.Muted
            )
        }
        Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null, tint = AtharColors.Muted, modifier = Modifier.size(18.dp))
    }
}
