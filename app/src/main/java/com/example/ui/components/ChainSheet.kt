package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.domain.model.HadithListItem
import com.example.domain.model.NarratorWithCount
import com.example.ui.theme.AtharColors
import com.example.ui.theme.AtharDimens
import com.example.ui.theme.AtharShapes
import com.example.ui.theme.AtharType
import com.example.ui.theme.palette

/**
 * شجرة السند (DESIGN_SPEC §4.7) بتكييف أمين للبيانات:
 * - نص الإسناد كاملاً كما في المصدر، لأن narrator_ids هي رواة المسار المطابَقون فقط لا السند كله.
 * - رواة المسار عمودياً بترتيب ورودهم، بلا ترقيم طبقات (لا نعرف موضع كل راوٍ من السند الكامل).
 * - التنويه وصف لمعيار الإدراج أو للانقطاع، لا حكم مأثور على الإسناد.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NarratorChainBottomSheet(
    item: HadithListItem,
    narrators: NarratorDirectory,
    showTashkeel: Boolean,
    onDismiss: () -> Unit,
    onOpenNarrator: (NarratorWithCount) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = AtharShapes.BottomSheet,
        containerColor = AtharColors.Ivory,
        contentColor = AtharColors.Ink,
        scrimColor = AtharColors.Scrim.copy(alpha = 0.5f),
        dragHandle = {
            Box(
                Modifier
                    .padding(top = 10.dp, bottom = 6.dp)
                    .size(width = 36.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(AtharColors.Handle)
            )
        }
    ) {
        Rtl {
            ChainSheetContent(item, narrators, showTashkeel, onDismiss, onOpenNarrator)
        }
    }
}

@Composable
private fun ChainSheetContent(
    item: HadithListItem,
    narrators: NarratorDirectory,
    showTashkeel: Boolean,
    onDismiss: () -> Unit,
    onOpenNarrator: (NarratorWithCount) -> Unit
) {
    val sanad = remember(item.rawSanad, showTashkeel) { displayText(item.rawSanad.trim(), showTashkeel) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // الترويسة
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("شجرة السند", style = AtharType.SheetTitle, color = AtharColors.Cypress)
                Text(sourceLine(item), style = AtharType.Secondary, color = AtharColors.Muted)
            }
            IconButton(
                onClick = onDismiss,
                colors = IconButtonDefaults.iconButtonColors(containerColor = AtharColors.SurfaceDim)
            ) {
                Icon(Icons.Rounded.Close, contentDescription = "إغلاق", tint = AtharColors.InkSoft)
            }
        }

        TransmissionCallout(item)

        // نص الإسناد كاملاً
        SectionLabel("الإسناد كما في المصدر")
        Text(
            sanad,
            style = AtharType.Sanad,
            color = AtharColors.Ink,
            modifier = Modifier
                .fillMaxWidth()
                .clip(AtharShapes.ReadingPanel)
                .background(AtharColors.Surface)
                .border(1.dp, AtharColors.OutlineVariant, AtharShapes.ReadingPanel)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        )

        // رواة المسار
        val chain = item.chain
        if (chain.isNotEmpty()) {
            SectionLabel("رواة المسار في هذا الإسناد")
            Column {
                chain.forEachIndexed { index, node ->
                    val narrator = narrators[node.narratorId]
                    ChainTimelineRow(
                        name = narrator?.narrator?.name ?: node.name,
                        narrator = narrator,
                        isLast = index == chain.lastIndex,
                        onOpenBio = narrator?.let { n -> { onOpenNarrator(n) } }
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, style = AtharType.Link, color = AtharColors.AmberDeep)
}

/** تنويه عنبري: الانقطاع إن وُجد، وإلا معيار الإدراج (ليس حكماً بالصحة) */
@Composable
private fun TransmissionCallout(item: HadithListItem) {
    val (title, body) = when (item.transmissionNote) {
        "بلاغ" -> "بلاغ" to "قال فيه المصنِّف «بلغني» أو نحوها دون أن يسوق الإسناد متصلاً."
        "مرسل" -> "مرسل" to "رفعه التابعي إلى النبي ﷺ دون ذكر الصحابي، فالإسناد غير متصل."
        else -> if (item.isMursalOrBalagh) {
            "إسناد غير متصل" to "كشفٌ آلي يدل على انقطاع في هذا الإسناد."
        } else {
            "مقبول حسب معايير المسار" to "تصنيف آلي بحسب رواة المسار المطابَقين، وليس حكماً على صحة الإسناد."
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AtharShapes.InnerCard)
            .background(AtharColors.AmberContainer)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            Modifier.size(36.dp).clip(CircleShape).background(AtharColors.Amber),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.Info, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(title, style = AtharType.Callout, color = AtharColors.OnAmberContainer)
            Text(body, style = AtharType.Secondary, color = AtharColors.OnAmberContainerMuted)
        }
    }
}

@Composable
private fun ChainTimelineRow(
    name: String,
    narrator: NarratorWithCount?,
    isLast: Boolean,
    onOpenBio: (() -> Unit)?
) {
    val palette = narrator?.region?.palette
    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        // السكة: دائرة ثم خط عنبري إلى العقدة التالية
        Column(Modifier.width(AtharDimens.ChainTreeNodeSize).fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                Modifier
                    .padding(top = 6.dp)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(palette?.container ?: AtharColors.SurfaceTint)
                    .border(2.dp, palette?.dot ?: AtharColors.NeutralDot, CircleShape)
            )
            if (!isLast) {
                Box(
                    Modifier
                        .weight(1f)
                        .width(AtharDimens.ChainLineWidth)
                        .background(AtharColors.Amber)
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLast) 0.dp else 12.dp)
                .clip(AtharShapes.InnerCard)
                .background(AtharColors.Surface)
                .border(1.dp, AtharColors.OutlineVariant, AtharShapes.InnerCard)
                .padding(start = 14.dp, end = 8.dp, top = 8.dp, bottom = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(name, style = AtharType.CardTitle, color = AtharColors.Ink, modifier = Modifier.weight(1f))
                if (onOpenBio != null) {
                    Row(
                        modifier = Modifier
                            .clip(AtharShapes.IconButton)
                            .clickable(role = Role.Button, onClick = onOpenBio)
                            .heightIn(min = AtharDimens.MinTouchTarget)
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("الترجمة", style = AtharType.Link, color = AtharColors.Cypress)
                        Icon(
                            Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            contentDescription = null,
                            tint = AtharColors.Cypress,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            if (narrator != null) {
                Text(
                    listOfNotNull(narrator.narrator.region, narrator.narrator.era).joinToString(" · "),
                    style = AtharType.Lineage,
                    color = AtharColors.Muted
                )
            }
        }
    }
}
