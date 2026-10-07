package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.AtharDatabase
import com.example.data.repository.AtharRepositoryImpl
import com.example.ui.components.NarratorBioDialog
import com.example.ui.components.NarratorChainBottomSheet
import com.example.ui.screens.BooksScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.HadithHomeScreen
import com.example.ui.screens.NarratorsScreen
import com.example.ui.theme.ArabicSansFontFamily
import com.example.ui.theme.AtharTheme
import com.example.ui.theme.RoyalGoldLight
import com.example.ui.theme.RoyalGoldPrimary
import com.example.ui.theme.RoyalNavyDark
import com.example.ui.theme.RoyalNavyPrimary
import com.example.ui.viewmodel.HadithFilterViewModel

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val context = applicationContext
            val database = remember { AtharDatabase.getInstance(context) }
            val repository = remember { AtharRepositoryImpl(database.atharDao()) }
            val viewModel: HadithFilterViewModel = viewModel(
                factory = HadithFilterViewModel.Factory(repository)
            )

            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val snackbarHostState = remember { SnackbarHostState() }

            LaunchedEffect(uiState.userNotice) {
                uiState.userNotice?.let { notice ->
                    snackbarHostState.showSnackbar(notice)
                    viewModel.clearNotice()
                }
            }

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                AtharTheme(darkTheme = uiState.isDarkMode) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = MaterialTheme.colorScheme.background,
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        bottomBar = {
                            Surface(
                                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
                                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                                shadowElevation = 2.dp
                            ) {
                                NavigationBar(
                                    modifier = Modifier.testTag("main_bottom_nav"),
                                    containerColor = Color.Transparent,
                                    tonalElevation = 0.dp
                                ) {
                                    NavigationBarItem(
                                        selected = uiState.currentTab == 0,
                                        onClick = { viewModel.setTab(0) },
                                        icon = {
                                            Icon(
                                                imageVector = if (uiState.currentTab == 0) Icons.Default.MenuBook else Icons.Outlined.MenuBook,
                                                contentDescription = stringResource(R.string.tab_hadiths)
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = stringResource(R.string.tab_hadiths),
                                                fontFamily = ArabicSansFontFamily,
                                                fontWeight = if (uiState.currentTab == 0) FontWeight.Bold else FontWeight.Medium,
                                                fontSize = 11.sp
                                            )
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                            selectedTextColor = MaterialTheme.colorScheme.primary,
                                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        modifier = Modifier.testTag("nav_tab_hadiths")
                                    )

                                    NavigationBarItem(
                                        selected = uiState.currentTab == 1,
                                        onClick = { viewModel.setTab(1) },
                                        icon = {
                                            Icon(
                                                imageVector = if (uiState.currentTab == 1) Icons.Default.People else Icons.Outlined.People,
                                                contentDescription = stringResource(R.string.tab_narrators)
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = stringResource(R.string.tab_narrators),
                                                fontFamily = ArabicSansFontFamily,
                                                fontWeight = if (uiState.currentTab == 1) FontWeight.Bold else FontWeight.Medium,
                                                fontSize = 11.sp
                                            )
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                            selectedTextColor = MaterialTheme.colorScheme.primary,
                                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        modifier = Modifier.testTag("nav_tab_narrators")
                                    )

                                    NavigationBarItem(
                                        selected = uiState.currentTab == 2,
                                        onClick = { viewModel.setTab(2) },
                                        icon = {
                                            Icon(
                                                imageVector = if (uiState.currentTab == 2) Icons.Default.AutoStories else Icons.Outlined.AutoStories,
                                                contentDescription = stringResource(R.string.tab_books)
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = stringResource(R.string.tab_books),
                                                fontFamily = ArabicSansFontFamily,
                                                fontWeight = if (uiState.currentTab == 2) FontWeight.Bold else FontWeight.Medium,
                                                fontSize = 11.sp
                                            )
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                            selectedTextColor = MaterialTheme.colorScheme.primary,
                                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        modifier = Modifier.testTag("nav_tab_books")
                                    )

                                    NavigationBarItem(
                                        selected = uiState.currentTab == 3,
                                        onClick = { viewModel.setTab(3) },
                                        icon = {
                                            Icon(
                                                imageVector = if (uiState.currentTab == 3) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                                                contentDescription = stringResource(R.string.tab_favorites)
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = stringResource(R.string.tab_favorites),
                                                fontFamily = ArabicSansFontFamily,
                                                fontWeight = if (uiState.currentTab == 3) FontWeight.Bold else FontWeight.Medium,
                                                fontSize = 11.sp
                                            )
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                            selectedTextColor = MaterialTheme.colorScheme.primary,
                                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        modifier = Modifier.testTag("nav_tab_favorites")
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.Center),
                                    color = RoyalGoldPrimary
                                )
                            } else {
                                Crossfade(
                                    targetState = uiState.currentTab,
                                    label = "tab_transition"
                                ) { tab ->
                                    when (tab) {
                                        0 -> HadithHomeScreen(
                                            uiState = uiState,
                                            onSearchChange = viewModel::onSearchQueryChanged,
                                            onClearSearch = viewModel::clearSearch,
                                            onFilterSelect = viewModel::setFilterType,
                                            onSelectRegion = viewModel::setSelectedRegion,
                                            onSelectEra = viewModel::setSelectedEra,
                                            onSelectNarrator = viewModel::setSelectedNarrator,
                                            onToggleExpandAdvancedSearch = viewModel::toggleAdvancedSearch,
                                            onResetAllFilters = viewModel::resetAllFilters,
                                            onToggleTashkeel = viewModel::toggleTashkeel,
                                            onToggleTheme = viewModel::toggleTheme,
                                            onAdjustFontSize = viewModel::adjustFontSize,
                                            onDeconstructSanad = viewModel::openNarratorChain,
                                            onToggleFavorite = viewModel::toggleFavorite
                                        )
                                        1 -> NarratorsScreen(
                                            uiState = uiState,
                                            onNarratorClick = viewModel::openNarratorBio
                                        )
                                        2 -> BooksScreen(
                                            uiState = uiState,
                                            onBookSelect = { _ ->
                                                viewModel.setTab(0)
                                            }
                                        )
                                        3 -> FavoritesScreen(
                                            uiState = uiState,
                                            onDeconstructSanad = viewModel::openNarratorChain,
                                            onToggleFavorite = viewModel::toggleFavorite
                                        )
                                    }
                                }
                            }

                            // Modal Bottom Sheet for Narrator Chain
                            uiState.selectedHadithForChain?.let { hadithDetail ->
                                NarratorChainBottomSheet(
                                    detail = hadithDetail,
                                    onDismiss = viewModel::closeNarratorChain,
                                    onNarratorClick = viewModel::openNarratorBio
                                )
                            }

                            // Dialog for Narrator Bio
                            uiState.selectedNarratorForModal?.let { narrator ->
                                NarratorBioDialog(
                                    narrator = narrator,
                                    onDismiss = viewModel::closeNarratorBio
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
