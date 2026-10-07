package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.example.ui.theme.ModernHeartActive
import com.example.ui.theme.ModernPrimaryGradient
import com.example.ui.theme.ShamSapphireBg
import com.example.ui.theme.ShamSapphireBorder
import com.example.ui.theme.ShamSapphireNavy
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Removes Arabic diacritics (Tashkeel: Fatha, Damma, Kasra, Tanween, Sukun, Shaddah, etc.).
 */
fun String.removeArabicTashkeel(): String {
    val tashkeelRegex = Regex("[\\u064B-\\u065F\\u0670]")
    return this.replace(tashkeelRegex, "")
}

/**
 * Ultra-Modern Bento-style Hadith Card with dynamic animations and full Tashkeel support.
 */
@Composable
fun HadithCard(
    detail: HadithDetail,
    showTashkeel: Boolean,
    fontSizeScale: Float,
    onDeconstructSanadClick: () -> Unit,
    onToggleFavorite: (Int, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val hadith = detail.hadith
    val book = detail.book
    val chain = detail.chain

    // Animated heart bounce scale
    var isHeartBouncing by remember { mutableStateOf(false) }
    val heartScale by animateFloatAsState(
        targetValue = if (isHeartBouncing) 1.35f else 1.0f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = 400f),
        label = "heart_scale"
    )

    // Regional Modern Pill Colors
    val (pillBg, pillTextColor, pillBorder) = when {
        hadith.pathBadge.contains("مدني") -> Triple(MadinahEmeraldBg, MadinahEmeraldGreen, MadinahEmeraldBorder)
        hadith.pathBadge.contains("شامي") -> Triple(ShamSapphireBg, ShamSapphireNavy, ShamSapphireBorder)
        hadith.pathBadge.contains("أندلس") -> Triple(AndalusGarnetBg, AndalusGarnetPlum, AndalusGarnetBorder)
        else -> Triple(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.outline)
    }

    val favColor by animateColorAsState(
        targetValue = if (detail.isFavorite) ModernHeartActive else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
        animationSpec = spring(),
        label = "fav_hadith_color"
    )

    val displayedMatn = if (showTashkeel) hadith.matn else hadith.matn.removeArabicTashkeel()
    val displayedChapter = if (showTashkeel) hadith.chapter else hadith.chapter.removeArabicTashkeel()
    val displayedBookTitle = if (showTashkeel) book.title else book.title.removeArabicTashkeel()
    val displayedAuthor = if (showTashkeel) book.author else book.author.removeArabicTashkeel()

    // Modern Bento Card
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(22.dp),
                ambientColor = Color(0x0A000000),
                spotColor = Color(0x144F46E5)
            )
            .clip(RoundedCornerShape(22.dp))
            .testTag("hadith_card_${hadith.id}"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // ----------------------------------------------------------------
            // 1. TOP MODERN HEADER: Book Chip & Glowing Region Pill
            // ----------------------------------------------------------------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Book Info Chip
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.AutoStories,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = displayedChapter,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = ArabicSansFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 15.sp
                            ),
                            maxLines = 1
                        )
                        Text(
                            text = "$displayedBookTitle • $displayedAuthor",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = ArabicSansFontFamily,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            ),
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Modern Pill
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = pillBg,
                    border = BorderStroke(1.dp, pillBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(pillTextColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = hadith.pathBadge,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = ArabicSansFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                color = pillTextColor,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ----------------------------------------------------------------
            // 2. MODERN EDITORIAL MATN READING SECTION (With Smooth Crossfade)
            // ----------------------------------------------------------------
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp)
                ) {
                    Crossfade(
                        targetState = displayedMatn,
                        animationSpec = tween(durationMillis = 250),
                        label = "matn_tashkeel_crossfade"
                    ) { matnText ->
                        Text(
                            text = "« $matnText »",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontFamily = ArabicSerifFontFamily,
                                fontSize = (20 * fontSizeScale).sp,
                                lineHeight = (38 * fontSizeScale).sp,
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ----------------------------------------------------------------
            // 3. INTERACTIVE MODERN SANAD METRO-LINE TIMELINE
            // ----------------------------------------------------------------
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)), RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AccountTree,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "مسار السند التفاعلي",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = ArabicSansFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp
                            )
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "${if (chain.isNotEmpty()) chain.size else 4} محطات",
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = ArabicSansFontFamily,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Metro Timeline Track
                if (chain.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        chain.forEachIndexed { index, narrator ->
                            val isLast = index == chain.size - 1

                            val (nodeColor, nodeBg, nodeBorder) = when {
                                narrator.region.contains("المدينة") -> Triple(MadinahEmeraldGreen, MadinahEmeraldBg, MadinahEmeraldBorder)
                                narrator.region.contains("الشام") -> Triple(ShamSapphireNavy, ShamSapphireBg, ShamSapphireBorder)
                                narrator.region.contains("الأندلس") -> Triple(AndalusGarnetPlum, AndalusGarnetBg, AndalusGarnetBorder)
                                else -> Triple(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.outline)
                            }

                            val displayedNarratorName = if (showTashkeel) {
                                narrator.popularName ?: narrator.name
                            } else {
                                (narrator.popularName ?: narrator.name).removeArabicTashkeel()
                            }

                            // Metro Station Pill
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, nodeBorder),
                                shadowElevation = 1.dp,
                                modifier = Modifier
                                    .clickable(onClick = onDeconstructSanadClick)
                                    .testTag("timeline_node_${hadith.id}_${narrator.narratorId}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Number Tag
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(nodeBg),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${index + 1}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = ArabicSansFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                color = nodeColor,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Column {
                                        Text(
                                            text = displayedNarratorName,
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontFamily = ArabicSansFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                fontSize = 12.sp
                                            ),
                                            maxLines = 1
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = narrator.region,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontFamily = ArabicSansFontFamily,
                                                    color = nodeColor,
                                                    fontWeight = FontWeight.SemiBold,
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
                                }
                            }

                            // Metro Flow Connector
                            if (!isLast) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .width(10.dp)
                                            .height(1.5.dp)
                                            .background(MaterialTheme.colorScheme.outline)
                                    )
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                        modifier = Modifier.size(11.dp)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Text(
                        text = hadith.rawSanad,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = ArabicSansFontFamily,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        ),
                        maxLines = 2
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ----------------------------------------------------------------
            // 4. MODERN ACTION TOOLBAR (Gradient Pill & Action Buttons)
            // ----------------------------------------------------------------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gradient Deconstruct Button
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = onDeconstructSanadClick)
                        .testTag("btn_deconstruct_sanad_${hadith.id}")
                ) {
                    Box(
                        modifier = Modifier
                            .background(ModernPrimaryGradient)
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AccountTree,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = "تفكيك السند والرجال",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontFamily = ArabicSansFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }

                // Interactive Action Icons
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Copy
                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Hadith", "${displayedMatn}\n[${displayedBookTitle} - ${displayedChapter}]")
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "تم نسخ الأثر بنجاح", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("btn_copy_hadith_${hadith.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ContentCopy,
                            contentDescription = "نسخ الأثر",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    // Share
                    IconButton(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "«${displayedMatn}»\n\nتخريج: ${displayedBookTitle} (${displayedChapter})\nتطبيق أثر: ديوان مرويات أهل المدينة والشام والأندلس"
                                )
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "مشاركة الأثر"))
                        },
                        modifier = Modifier.testTag("btn_share_hadith_${hadith.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = "مشاركة الأثر",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    // Favorite with Spring Bounce
                    IconButton(
                        onClick = {
                            scope.launch {
                                isHeartBouncing = true
                                delay(120)
                                isHeartBouncing = false
                            }
                            onToggleFavorite(hadith.id, detail.isFavorite)
                        },
                        modifier = Modifier
                            .scale(heartScale)
                            .testTag("btn_fav_hadith_${hadith.id}")
                    ) {
                        Icon(
                            imageVector = if (detail.isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "حفظ في المفضلة",
                            tint = favColor,
                            modifier = Modifier.size(21.dp)
                        )
                    }
                }
            }
        }
    }
}
