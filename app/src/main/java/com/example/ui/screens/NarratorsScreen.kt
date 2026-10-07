package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AndalusGarnetBg
import com.example.ui.theme.AndalusGarnetBorder
import com.example.ui.theme.AndalusGarnetPlum
import com.example.ui.theme.ArabicSansFontFamily
import com.example.ui.theme.MadinahEmeraldBg
import com.example.ui.theme.MadinahEmeraldBorder
import com.example.ui.theme.MadinahEmeraldGreen
import com.example.ui.theme.ShamSapphireBg
import com.example.ui.theme.ShamSapphireBorder
import com.example.ui.theme.ShamSapphireNavy
import com.example.ui.viewmodel.HadithUiState

@Composable
fun NarratorsScreen(
    uiState: HadithUiState,
    onNarratorClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedRegion by remember { mutableStateOf<String?>(null) }

    val filteredNarrators = if (selectedRegion == null) {
        uiState.narrators
    } else {
        uiState.narrators.filter { it.region.contains(selectedRegion!!) }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("narrators_screen"),
        contentPadding = PaddingValues(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Minimalist Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 4.dp)
            ) {
                Text(
                    text = "معجم الرواة والرجال",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = ArabicSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 22.sp
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "تراجم الرواة الثقات من أهل المدينة والشام والأندلس (${uiState.narrators.size} راوٍ)",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = ArabicSansFontFamily,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                )
            }
        }

        // Region Filter Pills
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        val isSelected = selectedRegion == null
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                            modifier = Modifier.clickable { selectedRegion = null }
                        ) {
                            Text(
                                text = "كافة الأقاليم",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = ArabicSansFontFamily,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    item {
                        val isSelected = selectedRegion == "المدينة"
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MadinahEmeraldBg else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, if (isSelected) MadinahEmeraldGreen else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                            modifier = Modifier.clickable { selectedRegion = if (isSelected) null else "المدينة" }
                        ) {
                            Text(
                                text = "أهل المدينة",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = ArabicSansFontFamily,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MadinahEmeraldGreen else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    item {
                        val isSelected = selectedRegion == "الشام"
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) ShamSapphireBg else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, if (isSelected) ShamSapphireNavy else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                            modifier = Modifier.clickable { selectedRegion = if (isSelected) null else "الشام" }
                        ) {
                            Text(
                                text = "أهل الشام",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = ArabicSansFontFamily,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) ShamSapphireNavy else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    item {
                        val isSelected = selectedRegion == "الأندلس"
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) AndalusGarnetBg else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, if (isSelected) AndalusGarnetPlum else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                            modifier = Modifier.clickable { selectedRegion = if (isSelected) null else "الأندلس" }
                        ) {
                            Text(
                                text = "رواة الأندلس",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = ArabicSansFontFamily,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) AndalusGarnetPlum else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // Clean Minimalist Scholar Cards
        items(filteredNarrators, key = { it.id }) { narrator ->
            val (nodeColor, nodeBg, nodeBorder) = when {
                narrator.region.contains("المدينة") -> Triple(MadinahEmeraldGreen, MadinahEmeraldBg, MadinahEmeraldBorder)
                narrator.region.contains("الشام") -> Triple(ShamSapphireNavy, ShamSapphireBg, ShamSapphireBorder)
                narrator.region.contains("الأندلس") -> Triple(AndalusGarnetPlum, AndalusGarnetBg, AndalusGarnetBorder)
                else -> Triple(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.outline)
            }

            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 1.dp, shape = RoundedCornerShape(16.dp), spotColor = Color(0x08000000))
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onNarratorClick(narrator.id) }
                        .testTag("narrator_card_${narrator.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Clean Avatar Circle
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(nodeBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = narrator.name.firstOrNull()?.toString() ?: "ر",
                                fontFamily = ArabicSansFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = nodeColor,
                                fontSize = 18.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = narrator.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = ArabicSansFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 15.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(3.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = nodeBg,
                                    border = BorderStroke(0.8.dp, nodeBorder)
                                ) {
                                    Text(
                                        text = narrator.region,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = ArabicSansFontFamily,
                                            fontWeight = FontWeight.SemiBold,
                                            color = nodeColor,
                                            fontSize = 10.sp
                                        )
                                    )
                                }

                                Text(
                                    text = "•",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                    fontSize = 10.sp
                                )

                                Text(
                                    text = narrator.era,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = ArabicSansFontFamily,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        // Trusted Status Pill
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = if (narrator.isTrusted) "ثقة ثبت" else "مقبول",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = ArabicSansFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
