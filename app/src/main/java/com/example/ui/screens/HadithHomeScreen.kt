package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.domain.model.HadithDetail
import com.example.ui.components.AtharTopHeader
import com.example.ui.components.HadithCard
import com.example.ui.theme.AndalusGarnetPlum
import com.example.ui.theme.ArabicDisplaySerifFontFamily
import com.example.ui.theme.ArabicSansFontFamily
import com.example.ui.theme.ArabicSerifFontFamily
import com.example.ui.theme.MadinahEmeraldGreen
import com.example.ui.theme.RoyalGoldContainerLight
import com.example.ui.theme.RoyalGoldLight
import com.example.ui.theme.RoyalGoldPrimary
import com.example.ui.theme.RoyalNavyDark
import com.example.ui.theme.RoyalNavyLight
import com.example.ui.theme.RoyalNavyPrimary
import com.example.ui.theme.ShamSapphireNavy
import com.example.ui.viewmodel.HadithUiState
import com.example.ui.viewmodel.SanadFilterType

@Composable
fun HadithHomeScreen(
    uiState: HadithUiState,
    onSearchChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onFilterSelect: (SanadFilterType) -> Unit,
    onSelectRegion: (String?) -> Unit,
    onSelectEra: (String?) -> Unit,
    onSelectNarrator: (String?) -> Unit,
    onToggleExpandAdvancedSearch: () -> Unit,
    onResetAllFilters: () -> Unit,
    onToggleTashkeel: () -> Unit,
    onToggleTheme: () -> Unit,
    onAdjustFontSize: (Float) -> Unit,
    onDeconstructSanad: (HadithDetail) -> Unit,
    onToggleFavorite: (Int, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("hadith_home_screen"),
        contentPadding = PaddingValues(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // --------------------------------------------------------------------
        // 1. TOP HEADER WITH INTEGRATED ADVANCED SEARCH
        // --------------------------------------------------------------------
        item {
            AtharTopHeader(
                searchQuery = uiState.searchQuery,
                onSearchChange = onSearchChange,
                onClearSearch = onClearSearch,
                selectedRegion = uiState.selectedRegion,
                onSelectRegion = onSelectRegion,
                selectedEra = uiState.selectedEra,
                onSelectEra = onSelectEra,
                selectedNarrator = uiState.selectedNarrator,
                onSelectNarrator = onSelectNarrator,
                isAdvancedSearchExpanded = uiState.isAdvancedSearchExpanded,
                onToggleExpandAdvancedSearch = onToggleExpandAdvancedSearch,
                onResetAllFilters = onResetAllFilters,
                resultCount = uiState.filteredHadiths.size,
                activeFilterCount = uiState.activeFilterCount,
                showTashkeel = uiState.showTashkeel,
                onToggleTashkeel = onToggleTashkeel,
                isDarkMode = uiState.isDarkMode,
                onToggleTheme = onToggleTheme,
                onAdjustFontSize = onAdjustFontSize
            )
        }

        // --------------------------------------------------------------------
        // 2. PRIMARY SANAD TRACK FILTER CHIPS ROW
        // --------------------------------------------------------------------
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterAlt,
                            contentDescription = null,
                            tint = RoyalGoldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "غربلة المسارات السندية والمدارس الفقهية:",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = ArabicSansFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp
                            )
                        )
                    }

                    if (uiState.isAnyFilterActive) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = RoyalGoldContainerLight,
                            border = BorderStroke(0.8.dp, RoyalGoldPrimary.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .clickable(onClick = onResetAllFilters)
                                .testTag("home_reset_all_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.RestartAlt,
                                    contentDescription = null,
                                    tint = RoyalGoldPrimary,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "تصفير",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = ArabicSansFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        color = RoyalGoldPrimary,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(SanadFilterType.values(), key = { it.name }) { filterType ->
                        val isSelected = uiState.activeFilter == filterType

                        val (activeBg, activeColor, activeBorder) = when (filterType) {
                            SanadFilterType.ALL -> Triple(RoyalNavyPrimary, Color.White, RoyalGoldPrimary)
                            SanadFilterType.MADINAH_ONLY -> Triple(MadinahEmeraldGreen, Color.White, MadinahEmeraldGreen)
                            SanadFilterType.SHAM_ONLY -> Triple(ShamSapphireNavy, Color.White, ShamSapphireNavy)
                            SanadFilterType.EXCLUDE_KUFA_TASHAYYU -> Triple(RoyalGoldPrimary, Color.White, RoyalGoldLight)
                        }

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) activeBg else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            border = BorderStroke(
                                1.2.dp,
                                if (isSelected) activeBorder else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            ),
                            shadowElevation = if (isSelected) 3.dp else 0.dp,
                            modifier = Modifier
                                .clickable { onFilterSelect(filterType) }
                                .testTag("filter_chip_${filterType.name}")
                        ) {
                            Text(
                                text = filterType.labelAr,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontFamily = ArabicSansFontFamily,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) activeColor else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // --------------------------------------------------------------------
        // 3. RESULTS STATUS HEADER
        // --------------------------------------------------------------------
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "الأحاديث والآثار المروية (${uiState.filteredHadiths.size})",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = ArabicDisplaySerifFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = RoyalNavyPrimary,
                        fontSize = 16.sp
                    )
                )

                if (uiState.activeFilter != SanadFilterType.ALL) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = RoyalGoldContainerLight,
                        border = BorderStroke(0.8.dp, RoyalGoldPrimary.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = uiState.activeFilter.labelAr,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = ArabicSansFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = RoyalGoldPrimary,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }

        // --------------------------------------------------------------------
        // 4. EMPTY RESULTS STATE
        // --------------------------------------------------------------------
        if (uiState.filteredHadiths.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.2.dp, RoyalGoldPrimary.copy(alpha = 0.35f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "لم يتم العثور على مرويات مطابقة لمعايير البحث",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = ArabicDisplaySerifFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "جرب إلغاء فلاتر الراوي أو المنطقة أو تغيير مصطلح البحث في السند والمتن",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = ArabicSansFontFamily,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = RoyalNavyPrimary,
                            border = BorderStroke(1.dp, RoyalGoldPrimary),
                            modifier = Modifier
                                .clickable(onClick = onResetAllFilters)
                                .testTag("empty_state_reset_filters_btn")
                        ) {
                            Text(
                                text = "إعادة ضبط جميع خيارات البحث والفرز",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontFamily = ArabicSansFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = RoyalGoldLight,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // --------------------------------------------------------------------
        // 5. HADITH FLOATING CARDS WITH CONTINUOUS TIMELINES
        // --------------------------------------------------------------------
        items(uiState.filteredHadiths, key = { it.hadith.id }) { detail ->
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
