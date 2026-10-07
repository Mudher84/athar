package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.HistoryEdu
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AndalusGarnetPlum
import com.example.ui.theme.ArabicSansFontFamily
import com.example.ui.theme.MadinahEmeraldGreen
import com.example.ui.theme.ShamSapphireNavy

/**
 * Ultra-Clean Minimalist Advanced Search Bar.
 */
@Composable
fun AtharAdvancedSearchBar(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    selectedRegion: String?,
    onSelectRegion: (String?) -> Unit,
    selectedEra: String?,
    onSelectEra: (String?) -> Unit,
    selectedNarrator: String?,
    onSelectNarrator: (String?) -> Unit,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onResetAllFilters: () -> Unit,
    resultCount: Int,
    activeFilterCount: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("athar_advanced_search_bar")
    ) {
        // --------------------------------------------------------------------
        // 1. PRIMARY INPUT ROW (Search field + Filter Toggle Button)
        // --------------------------------------------------------------------
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier
                    .weight(1f)
                    .testTag("athar_search_input"),
                placeholder = {
                    Text(
                        text = "ابحث في المتون، الأبواب، أو الرواة...",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = ArabicSansFontFamily,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            fontSize = 13.sp
                        )
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "بحث",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(19.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = onClearSearch,
                            modifier = Modifier.testTag("clear_search_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "مسح البحث",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = ArabicSansFontFamily,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Filter Expand Button
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (isExpanded || activeFilterCount > 0) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                border = BorderStroke(
                    1.dp,
                    if (isExpanded || activeFilterCount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                ),
                modifier = Modifier
                    .size(52.dp)
                    .clickable(onClick = onToggleExpand)
                    .testTag("toggle_advanced_search_btn")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (activeFilterCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ) {
                                    Text(text = "$activeFilterCount", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Tune,
                                contentDescription = "فلاتر متقدمة",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Tune,
                            contentDescription = "فلاتر متقدمة",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // --------------------------------------------------------------------
        // 2. ACTIVE FILTERS SUMMARY CHIPS (Dismissible)
        // --------------------------------------------------------------------
        if (activeFilterCount > 0) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Clear All Filter Chip
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .clickable(onClick = onResetAllFilters)
                        .testTag("reset_all_filters_chip")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.RestartAlt,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "إلغاء الفلاتر",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = ArabicSansFontFamily,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                // Region Chip
                selectedRegion?.let { region ->
                    MinimalActiveFilterChip(
                        label = "المنطقة: $region",
                        onDismiss = { onSelectRegion(null) }
                    )
                }

                // Era Chip
                selectedEra?.let { era ->
                    MinimalActiveFilterChip(
                        label = "الحقبة: $era",
                        onDismiss = { onSelectEra(null) }
                    )
                }

                // Narrator Chip
                selectedNarrator?.let { narrator ->
                    MinimalActiveFilterChip(
                        label = "الراوي: $narrator",
                        onDismiss = { onSelectNarrator(null) }
                    )
                }

                // Match Count Indicator
                Text(
                    text = "($resultCount أثر مطابق)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = ArabicSansFontFamily,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                )
            }
        }

        // --------------------------------------------------------------------
        // 3. EXPANDABLE ADVANCED FILTER DRAWER
        // --------------------------------------------------------------------
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                // Section 1: Geographical Regions
                MinimalFilterSectionTitle(
                    title = "المنطقة الجغرافية ومراكز الرواية",
                    icon = Icons.Outlined.LocationOn
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    MinimalFilterPill(
                        label = "الكل",
                        isSelected = selectedRegion == null,
                        onClick = { onSelectRegion(null) }
                    )
                    MinimalFilterPill(
                        label = "أهل المدينة",
                        isSelected = selectedRegion == "المدينة",
                        accentColor = MadinahEmeraldGreen,
                        onClick = { onSelectRegion(if (selectedRegion == "المدينة") null else "المدينة") }
                    )
                    MinimalFilterPill(
                        label = "أهل الشام",
                        isSelected = selectedRegion == "الشام",
                        accentColor = ShamSapphireNavy,
                        onClick = { onSelectRegion(if (selectedRegion == "الشام") null else "الشام") }
                    )
                    MinimalFilterPill(
                        label = "أهل الأندلس",
                        isSelected = selectedRegion == "الأندلس",
                        accentColor = AndalusGarnetPlum,
                        onClick = { onSelectRegion(if (selectedRegion == "الأندلس") null else "الأندلس") }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Section 2: Historical Eras
                MinimalFilterSectionTitle(
                    title = "الحقبة الزمنية والطبقات",
                    icon = Icons.Outlined.HistoryEdu
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    MinimalFilterPill(
                        label = "كافة الحقب",
                        isSelected = selectedEra == null,
                        onClick = { onSelectEra(null) }
                    )
                    MinimalFilterPill(
                        label = "صحابي جليل",
                        isSelected = selectedEra == "صحابي",
                        onClick = { onSelectEra(if (selectedEra == "صحابي") null else "صحابي") }
                    )
                    MinimalFilterPill(
                        label = "كبار التابعين",
                        isSelected = selectedEra == "تابعي",
                        onClick = { onSelectEra(if (selectedEra == "تابعي") null else "تابعي") }
                    )
                    MinimalFilterPill(
                        label = "العصر الأموي",
                        isSelected = selectedEra == "أموي",
                        onClick = { onSelectEra(if (selectedEra == "أموي") null else "أموي") }
                    )
                    MinimalFilterPill(
                        label = "أئمة الأندلس",
                        isSelected = selectedEra == "أندلسي",
                        onClick = { onSelectEra(if (selectedEra == "أندلسي") null else "أندلسي") }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Section 3: Prominent Imams
                MinimalFilterSectionTitle(
                    title = "أعلام وأئمة الإسناد",
                    icon = Icons.Outlined.Person
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    MinimalFilterPill(
                        label = "كافة الأعلام",
                        isSelected = selectedNarrator == null,
                        onClick = { onSelectNarrator(null) }
                    )
                    MinimalFilterPill(
                        label = "الإمام مالك",
                        isSelected = selectedNarrator == "مالك",
                        onClick = { onSelectNarrator(if (selectedNarrator == "مالك") null else "مالك") }
                    )
                    MinimalFilterPill(
                        label = "الإمام الأوزاعي",
                        isSelected = selectedNarrator == "الأوزاعي",
                        onClick = { onSelectNarrator(if (selectedNarrator == "الأوزاعي") null else "الأوزاعي") }
                    )
                    MinimalFilterPill(
                        label = "نافع مولى ابن عمر",
                        isSelected = selectedNarrator == "نافع",
                        onClick = { onSelectNarrator(if (selectedNarrator == "نافع") null else "نافع") }
                    )
                    MinimalFilterPill(
                        label = "ابن شهاب الزهري",
                        isSelected = selectedNarrator == "الزهري",
                        onClick = { onSelectNarrator(if (selectedNarrator == "الزهري") null else "الزهري") }
                    )
                    MinimalFilterPill(
                        label = "يحيى بن يحيى الليثي",
                        isSelected = selectedNarrator == "الليثي",
                        onClick = { onSelectNarrator(if (selectedNarrator == "الليثي") null else "الليثي") }
                    )
                }
            }
        }
    }
}

@Composable
private fun MinimalFilterSectionTitle(
    title: String,
    icon: ImageVector
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = ArabicSansFontFamily,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 11.sp
            )
        )
    }
}

@Composable
private fun MinimalFilterPill(
    label: String,
    isSelected: Boolean,
    accentColor: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) accentColor else MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            1.dp,
            if (isSelected) accentColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        ),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = ArabicSansFontFamily,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                fontSize = 11.sp
            )
        )
    }
}

@Composable
private fun MinimalActiveFilterChip(
    label: String,
    onDismiss: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = ArabicSansFontFamily,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp
                )
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "إزالة الفلتر",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(12.dp)
                    .clickable(onClick = onDismiss)
            )
        }
    }
}
