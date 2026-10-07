package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.HadithDetail
import com.example.domain.model.NarratorInChain
import com.example.ui.theme.AndalusGarnetBg
import com.example.ui.theme.AndalusGarnetBorder
import com.example.ui.theme.AndalusGarnetPlum
import com.example.ui.theme.ArabicSansFontFamily
import com.example.ui.theme.ArabicSerifFontFamily
import com.example.ui.theme.MadinahEmeraldBg
import com.example.ui.theme.MadinahEmeraldBorder
import com.example.ui.theme.MadinahEmeraldGreen
import com.example.ui.theme.ShamSapphireBg
import com.example.ui.theme.ShamSapphireBorder
import com.example.ui.theme.ShamSapphireNavy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NarratorChainBottomSheet(
    detail: HadithDetail,
    onDismiss: () -> Unit,
    onNarratorClick: (Int) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val hadith = detail.hadith
    val book = detail.book
    val chain = detail.chain

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = Modifier.testTag("narrator_chain_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 36.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.AccountTree,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "تفكيك السند والرجال",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = ArabicSansFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 16.sp
                            )
                        )
                        Text(
                            text = "${book.title} • ${hadith.chapter}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = ArabicSansFontFamily,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_sheet_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "إغلاق",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Matn Summary Card
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "نص الأثر الشريف",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = ArabicSansFontFamily,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "« ${hadith.matn} »",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = ArabicSerifFontFamily,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 15.sp,
                            lineHeight = 26.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "طبقات وسلسلة الرواة (${chain.size} رواة في الإسناد)",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontFamily = ArabicSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 13.sp
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Narrator Nodes
            chain.forEachIndexed { index, narrator ->
                val isLast = index == chain.size - 1
                NarratorChainStepItem(
                    stepNumber = index + 1,
                    narrator = narrator,
                    isLast = isLast,
                    onItemClick = { onNarratorClick(narrator.narratorId) }
                )
            }
        }
    }
}

@Composable
private fun NarratorChainStepItem(
    stepNumber: Int,
    narrator: NarratorInChain,
    isLast: Boolean,
    onItemClick: () -> Unit
) {
    val (nodeColor, nodeBg, nodeBorder) = when {
        narrator.region.contains("المدينة") -> Triple(MadinahEmeraldGreen, MadinahEmeraldBg, MadinahEmeraldBorder)
        narrator.region.contains("الشام") -> Triple(ShamSapphireNavy, ShamSapphireBg, ShamSapphireBorder)
        narrator.region.contains("الأندلس") -> Triple(AndalusGarnetPlum, AndalusGarnetBg, AndalusGarnetBorder)
        else -> Triple(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.outline)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Vertical Timeline Track
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(36.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(nodeBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$stepNumber",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = ArabicSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = nodeColor,
                        fontSize = 11.sp
                    )
                )
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(1.5.dp)
                        .height(52.dp)
                        .background(MaterialTheme.colorScheme.outline)
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Scholar Card
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLast) 0.dp else 12.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onItemClick)
                .testTag("narrator_chain_step_$stepNumber"),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, nodeBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = narrator.name,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontFamily = ArabicSansFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 13.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = narrator.region,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = ArabicSansFontFamily,
                                color = nodeColor,
                                fontSize = 10.sp
                            )
                        )
                        Text(
                            text = "•",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            fontSize = 8.sp
                        )
                        Text(
                            text = narrator.era,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = ArabicSansFontFamily,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = nodeBg
                ) {
                    Text(
                        text = if (narrator.isTrusted) "ثقة" else "مقبول",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = ArabicSansFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            color = nodeColor,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }
    }
}
