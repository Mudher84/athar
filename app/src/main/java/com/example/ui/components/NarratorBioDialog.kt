package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entity.NarratorEntity
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

@Composable
fun NarratorBioDialog(
    narrator: NarratorEntity,
    onDismiss: () -> Unit
) {
    val (nodeColor, nodeBg, nodeBorder) = when {
        narrator.region.contains("المدينة") -> Triple(MadinahEmeraldGreen, MadinahEmeraldBg, MadinahEmeraldBorder)
        narrator.region.contains("الشام") -> Triple(ShamSapphireNavy, ShamSapphireBg, ShamSapphireBorder)
        narrator.region.contains("الأندلس") -> Triple(AndalusGarnetPlum, AndalusGarnetBg, AndalusGarnetBorder)
        else -> Triple(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.outline)
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("narrator_bio_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = nodeBg,
                        border = BorderStroke(1.dp, nodeBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.LocationOn,
                                contentDescription = null,
                                tint = nodeColor,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = narrator.region,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = ArabicSansFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    color = nodeColor,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "إغلاق",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Narrator Name
                Text(
                    text = narrator.name,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = ArabicSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 18.sp
                    )
                )

                narrator.popularName?.let { pop ->
                    Text(
                        text = "المشهور بـ: $pop",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = ArabicSansFontFamily,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp
                        ),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Info Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = "الحقبة والطبقة",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = ArabicSansFontFamily,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 10.sp
                                )
                            )
                            Text(
                                text = narrator.era,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = ArabicSansFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = "حكم الجرح والتعديل",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = ArabicSansFontFamily,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 10.sp
                                )
                            )
                            Text(
                                text = if (narrator.isTrusted) "ثقة ثبت حجة" else "صدوق حسن الحديث",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = ArabicSansFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (narrator.isTrusted) MadinahEmeraldGreen else MaterialTheme.colorScheme.primary,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Biography Text Box
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "نبذة وسيرة المترجم",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = ArabicSansFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = narrator.notes ?: "من أئمة ورواة الحديث الثقات المشهورين بالعدالة والضبط ورواية السنن والآثار.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = ArabicSansFontFamily,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp,
                                lineHeight = 21.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "إغلاق الترجمة",
                        fontFamily = ArabicSansFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
