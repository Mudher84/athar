package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AccountTree
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.domain.model.HadithListItem
import com.example.domain.model.NarratorWithCount
import com.example.ui.theme.AtharColors
import com.example.ui.theme.AtharDimens
import com.example.ui.theme.AtharShapes
import com.example.ui.theme.AtharType
import com.example.ui.theme.palette
import com.example.util.arabicDigits

/** فهرس الرواة بالمعرّف، مستقر حتى لا يُعيد تركيب كل البطاقات */
@Immutable
class NarratorDirectory(private val byId: Map<Int, NarratorWithCount>) {
    operator fun get(id: Int): NarratorWithCount? = byId[id]

    companion object {
        val Empty = NarratorDirectory(emptyMap())
        fun of(list: List<NarratorWithCount>) = NarratorDirectory(list.associateBy { it.narrator.id })
    }
}

/** المتون الأطول من هذا تُطوى في القائمة مع زر «اقرأ كاملاً» */
private const val COLLAPSE_THRESHOLD = 700
private const val COLLAPSED_LINES = 10

/**
 * بطاقة الأثر (DESIGN_SPEC §4.5): بلا إطار ثقيل؛ المتن في حرم قراءة عاجي، ثم شريط رواة المسار،
 * ثم الأزرار.
 */
@Composable
fun HadithCard(
    item: HadithListItem,
    narrators: NarratorDirectory,
    matnSize: Int,
    showTashkeel: Boolean,
    onToggleFavorite: (HadithListItem) -> Unit,
    onOpenChain: (HadithListItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val matnText = remember(item.matn, showTashkeel) { quotedMatn(displayText(item.matn, showTashkeel)) }
    val collapsible = item.matn.length > COLLAPSE_THRESHOLD
    var expanded by rememberSaveable(item.id) { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .atharShadow(10.dp, AtharShapes.HadithCard)
            .clip(AtharShapes.HadithCard)
            .background(AtharColors.Surface)
            .padding(AtharDimens.CardPadding),
        verticalArrangement = Arrangement.spacedBy(AtharDimens.CardGap)
    ) {
        // 1. الإقليم والمصدر
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            item.region?.let { RegionBadge(it, short = true) }
            item.transmissionNote?.let { TransmissionBadge(it) }
            Spacer(Modifier.weight(1f))
            Text(
                sourceLine(item),
                style = AtharType.Secondary,
                color = AtharColors.Muted,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(2f, fill = false)
            )
        }

        // 2. حرم القراءة
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(AtharShapes.ReadingPanel)
                .background(AtharColors.ReadingPanel)
                .border(1.dp, AtharColors.ReadingPanelBorder, AtharShapes.ReadingPanel)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Text(
                text = matnText,
                style = AtharType.matn(matnSize),
                color = AtharColors.Ink,
                textAlign = TextAlign.Center,
                maxLines = if (collapsible && !expanded) COLLAPSED_LINES else Int.MAX_VALUE,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
            if (collapsible) {
                Text(
                    if (expanded) "طيّ المتن" else "اقرأ كاملاً",
                    style = AtharType.Link,
                    color = AtharColors.Cypress,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clip(AtharShapes.IconButton)
                        .clickable(role = Role.Button) { expanded = !expanded }
                        .heightIn(min = AtharDimens.MinTouchTarget)
                        .padding(horizontal = 12.dp, vertical = 14.dp)
                )
            }
        }

        // 3. رواة المسار
        ChainStrip(item = item, narrators = narrators, onClick = { onOpenChain(item) })

        // 4. الأزرار
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {
                clipboard.setText(AnnotatedString(shareText(item)))
                Toast.makeText(context, "نُسخ الأثر مع مصدره", Toast.LENGTH_SHORT).show()
            }) {
                Icon(Icons.Rounded.ContentCopy, contentDescription = "نسخ الأثر مع المصدر", tint = AtharColors.InkSoft)
            }
            IconButton(onClick = { shareHadith(context, item) }) {
                Icon(Icons.Rounded.Share, contentDescription = "مشاركة الأثر", tint = AtharColors.InkSoft)
            }
            FavoriteButton(item.isFavorite) { onToggleFavorite(item) }
            Spacer(Modifier.weight(1f))
            Surface(
                onClick = { onOpenChain(item) },
                shape = AtharShapes.IconButton,
                color = AtharColors.CypressContainer,
                modifier = Modifier.heightIn(min = AtharDimens.MinTouchTarget)
            ) {
                Row(
                    Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Rounded.AccountTree, contentDescription = null, tint = AtharColors.Cypress, modifier = Modifier.size(18.dp))
                    Text("شجرة السند", style = AtharType.ButtonSmall, color = AtharColors.Cypress)
                }
            }
        }
    }
}

@Composable
fun FavoriteButton(isFavorite: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            if (isFavorite) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
            contentDescription = if (isFavorite) "إزالة من المحفوظات" else "حفظ في المحفوظات",
            tint = if (isFavorite) AtharColors.Amber else AtharColors.InkSoft
        )
    }
}

/**
 * شريط رواة المسار الأفقي الخافت: الأسماء بترتيب السند (من جهة المصنِّف) بأسهم عنبرية
 * نحو أعلى الإسناد، وينتهي بوسم الإقليم. هؤلاء رواة المسار المطابَقون فقط لا السند كله،
 * لذا لا ترقيم طبقات هنا؛ نص الإسناد كاملاً في النافذة السفلية.
 */
@Composable
fun ChainStrip(item: HadithListItem, narrators: NarratorDirectory, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val chain = remember(item.chainPacked) { item.chain }
    Column(modifier.fillMaxWidth()) {
        HorizontalDivider(color = AtharColors.ReadingPanelBorder)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = AtharDimens.MinTouchTarget)
                .clickable(role = Role.Button, onClick = onClick)
                .semantics { contentDescription = "رواة المسار: " + chain.joinToString("، ") { it.name } + ". افتح شجرة السند" }
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            chain.forEachIndexed { index, node ->
                val narrator = narrators[node.narratorId]
                val region = narrator?.region
                if (region != null) RegionDot(region.palette.dot)
                Text(
                    narrator?.displayName ?: node.name,
                    style = AtharType.ChainNodeName,
                    color = AtharColors.InkSoft,
                    maxLines = 1
                )
                if (index < chain.lastIndex || item.region != null) {
                    Icon(
                        Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = null,
                        tint = AtharColors.Amber,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            if (chain.isEmpty()) {
                Text("لا رواة مطابَقون", style = AtharType.Secondary, color = AtharColors.Muted)
            }
            item.region?.let {
                Spacer(Modifier.width(2.dp))
                RegionBadge(it)
            }
        }
    }
}

fun sourceLine(item: HadithListItem): String = buildString {
    append(item.bookTitle)
    if (item.chapter.isNotBlank()) append(" · ").append(item.chapter)
    item.sourceNumber?.let { append(" · ").append(it.arabicDigits()) }
}

/** نص النسخ والمشاركة: المتن والإسناد والمصدر، دون أي حكم غير موجود في البيانات */
fun shareText(item: HadithListItem): String = buildString {
    append(item.rawSanad.trim())
    append("\n\n«").append(item.matn.trim()).append("»\n\n")
    append("المصدر: ").append(sourceLine(item))
    item.transmissionNote?.let { append("\n").append("ملاحظة: ").append(it) }
    append("\n— من تطبيق أثر")
}

private fun shareHadith(context: Context, item: HadithListItem) {
    val send = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText(item))
    }
    context.startActivity(Intent.createChooser(send, "مشاركة الأثر"))
}

/** يُستعمل في المحفوظات لعرض المسار مختصراً: «مالك ← نافع ← ابن عمر» */
fun shortChain(item: HadithListItem, narrators: NarratorDirectory): String =
    item.chain.joinToString(" ← ") { narrators[it.narratorId]?.displayName ?: it.name }
