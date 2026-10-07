package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.HadithDetail
import com.example.ui.components.HadithCard
import com.example.ui.theme.ArabicSansFontFamily
import com.example.ui.viewmodel.HadithUiState

@Composable
fun FavoritesScreen(
    uiState: HadithUiState,
    onDeconstructSanad: (HadithDetail) -> Unit,
    onToggleFavorite: (Int, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("favorites_screen"),
        contentPadding = PaddingValues(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Minimalist Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 4.dp)
            ) {
                Text(
                    text = "الأحاديث والمرويات المحفوظة",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = ArabicSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 22.sp
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "مجموعتك الخاصة من الأحاديث المحفوظة (${uiState.favoriteHadiths.size})",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = ArabicSansFontFamily,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                )
            }
        }

        if (uiState.favoriteHadiths.isEmpty()) {
            item {
                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(36.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.BookmarkBorder,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            Text(
                                text = "لم تقم بحفظ أي أثر بعد",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = ArabicSansFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 15.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "انقر على أيقونة الإشارة المرجعية أو القلب في بطاقات الأحاديث لحفظها هنا والرجوع إليها لاحقاً.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = ArabicSansFontFamily,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                            )
                        }
                    }
                }
            }
        } else {
            items(uiState.favoriteHadiths, key = { it.hadith.id }) { detail ->
                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    HadithCard(
                        detail = detail,
                        showTashkeel = uiState.showTashkeel,
                        fontSizeScale = uiState.fontSizeScale,
                        onDeconstructSanadClick = { onDeconstructSanad(detail) },
                        onToggleFavorite = onToggleFavorite
                    )
                }
            }
        }
    }
}
