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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.ArabicDisplaySerifFontFamily
import com.example.ui.theme.ArabicSansFontFamily
import com.example.ui.theme.ModernPrimaryGradient

/**
 * Ultra-Modern App Header.
 */
@Composable
fun AtharTopHeader(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    selectedRegion: String?,
    onSelectRegion: (String?) -> Unit,
    selectedEra: String?,
    onSelectEra: (String?) -> Unit,
    selectedNarrator: String?,
    onSelectNarrator: (String?) -> Unit,
    isAdvancedSearchExpanded: Boolean,
    onToggleExpandAdvancedSearch: () -> Unit,
    onResetAllFilters: () -> Unit,
    resultCount: Int,
    activeFilterCount: Int,
    showTashkeel: Boolean,
    onToggleTashkeel: () -> Unit,
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit,
    onAdjustFontSize: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                ambientColor = Color(0x0A000000),
                spotColor = Color(0x144F46E5)
            ),
        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            // ----------------------------------------------------------------
            // 1. MODERN BRAND MONOGRAM & READING CONTROLS
            // ----------------------------------------------------------------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Modern Gradient Brand Badge
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(ModernPrimaryGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "أثر",
                            fontFamily = ArabicDisplaySerifFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = stringResource(R.string.app_title_ar),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = ArabicSansFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 20.sp
                            )
                        )
                        Text(
                            text = "ديوان مرويات أهل المدينة والشام والأندلس",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = ArabicSansFontFamily,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                // Controls Row (Theme & Tashkeel)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Theme Switch
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clickable(onClick = onToggleTheme)
                            .testTag("toggle_theme_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isDarkMode) Icons.Outlined.DarkMode else Icons.Outlined.LightMode,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isDarkMode) "داكن" else "فاتح",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = ArabicSansFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }

                    // Tashkeel Toggle
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (showTashkeel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(
                            1.dp,
                            if (showTashkeel) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f) else Color.Transparent
                        ),
                        modifier = Modifier
                            .clickable(onClick = onToggleTashkeel)
                            .testTag("header_toggle_tashkeel_btn")
                    ) {
                        Text(
                            text = if (showTashkeel) "حركات" else "بدون",
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = ArabicSansFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp,
                                color = if (showTashkeel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ----------------------------------------------------------------
            // 2. MODERN SEARCH BAR
            // ----------------------------------------------------------------
            AtharAdvancedSearchBar(
                searchQuery = searchQuery,
                onSearchChange = onSearchChange,
                onClearSearch = onClearSearch,
                selectedRegion = selectedRegion,
                onSelectRegion = onSelectRegion,
                selectedEra = selectedEra,
                onSelectEra = onSelectEra,
                selectedNarrator = selectedNarrator,
                onSelectNarrator = onSelectNarrator,
                isExpanded = isAdvancedSearchExpanded,
                onToggleExpand = onToggleExpandAdvancedSearch,
                onResetAllFilters = onResetAllFilters,
                resultCount = resultCount,
                activeFilterCount = activeFilterCount
            )

            Spacer(modifier = Modifier.height(10.dp))

            // ----------------------------------------------------------------
            // 3. FONT RESIZE & STATS PILL SUB-ROW
            // ----------------------------------------------------------------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = "مذهب الأثر والسنن • الشام والمدينة والأندلس",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = ArabicSansFontFamily,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    )
                }

                // Font Resizer
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .clickable { onAdjustFontSize(-0.1f) }
                                .testTag("font_scale_decrease"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("A−", fontFamily = ArabicSansFontFamily, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .clickable { onAdjustFontSize(0.1f) }
                                .testTag("font_scale_increase"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("A+", fontFamily = ArabicSansFontFamily, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}
