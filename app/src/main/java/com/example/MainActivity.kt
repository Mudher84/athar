package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.domain.model.HadithListItem
import com.example.domain.model.NarratorWithCount
import com.example.domain.model.UiState
import com.example.ui.AtharViewModel
import com.example.ui.components.NarratorBioDialog
import com.example.ui.components.NarratorChainBottomSheet
import com.example.ui.components.NarratorDirectory
import com.example.ui.screens.BooksScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.HadithsScreen
import com.example.ui.screens.NarratorsScreen
import com.example.ui.theme.AtharColors
import com.example.ui.theme.AtharTheme
import com.example.ui.theme.AtharType

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // أرضية فاتحة فقط: أيقونات شريط الحالة داكنة دائماً
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(AtharColors.Ivory.toArgb(), AtharColors.Ivory.toArgb()),
            navigationBarStyle = SystemBarStyle.light(android.graphics.Color.WHITE, android.graphics.Color.WHITE)
        )
        super.onCreate(savedInstanceState)
        setContent {
            AtharTheme {
                AtharApp()
            }
        }
    }
}

private enum class AtharTab(val label: String, val icon: ImageVector) {
    Hadiths("الآثار", Icons.Outlined.Description),
    Narrators("الرواة", Icons.Outlined.Groups),
    Books("الكتب", Icons.AutoMirrored.Outlined.MenuBook),
    Favorites("المحفوظات", Icons.Outlined.BookmarkBorder),
}

@Composable
fun AtharApp(viewModel: AtharViewModel = viewModel(factory = AtharViewModel.Factory)) {
    var tab by rememberSaveable { mutableStateOf(AtharTab.Hadiths) }
    val stateHolder = rememberSaveableStateHolder()

    val narratorsState by viewModel.narrators.collectAsStateWithLifecycle()
    val booksState by viewModel.books.collectAsStateWithLifecycle()
    val reading by viewModel.readingSettings.collectAsStateWithLifecycle()
    val narrators = remember(narratorsState) {
        (narratorsState as? UiState.Success)?.data?.let(NarratorDirectory::of) ?: NarratorDirectory.Empty
    }

    var chainItem by remember { mutableStateOf<HadithListItem?>(null) }
    var bioNarrator by remember { mutableStateOf<NarratorWithCount?>(null) }

    BackHandler(enabled = tab != AtharTab.Hadiths) { tab = AtharTab.Hadiths }

    Scaffold(
        containerColor = AtharColors.Ivory,
        bottomBar = { AtharNavigationBar(selected = tab, onSelect = { tab = it }) }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            // كل تبويب يحفظ موضع تمريره عند التنقل
            stateHolder.SaveableStateProvider(tab.name) {
                when (tab) {
                    AtharTab.Hadiths -> HadithsScreen(
                        viewModel = viewModel,
                        narrators = narrators,
                        onOpenChain = { chainItem = it }
                    )
                    AtharTab.Narrators -> NarratorsScreen(
                        state = narratorsState,
                        onOpenNarrator = { bioNarrator = it }
                    )
                    AtharTab.Books -> BooksScreen(
                        state = booksState,
                        onBrowseBook = {
                            viewModel.showBookHadiths(it.book.id)
                            tab = AtharTab.Hadiths
                        }
                    )
                    AtharTab.Favorites -> FavoritesScreen(
                        viewModel = viewModel,
                        narrators = narrators,
                        onOpenChain = { chainItem = it }
                    )
                }
            }
        }
    }

    chainItem?.let { item ->
        NarratorChainBottomSheet(
            item = item,
            narrators = narrators,
            showTashkeel = reading.showTashkeel,
            onDismiss = { chainItem = null },
            onOpenNarrator = { bioNarrator = it }
        )
    }

    bioNarrator?.let { narrator ->
        NarratorBioDialog(
            narrator = narrator,
            onDismiss = { bioNarrator = null },
            onShowHadiths = {
                viewModel.showNarratorHadiths(it.narrator.id)
                bioNarrator = null
                chainItem = null
                tab = AtharTab.Hadiths
            }
        )
    }
}

@Composable
private fun AtharNavigationBar(selected: AtharTab, onSelect: (AtharTab) -> Unit) {
    NavigationBar(
        containerColor = AtharColors.Surface,
        tonalElevation = 0.dp,
        modifier = Modifier.drawBehind {
            drawLine(AtharColors.OutlineVariant, Offset(0f, 0f), Offset(size.width, 0f), strokeWidth = 1.dp.toPx())
        }
    ) {
        AtharTab.entries.forEach { tab ->
            val active = tab == selected
            NavigationBarItem(
                selected = active,
                onClick = { onSelect(tab) },
                icon = { Icon(tab.icon, contentDescription = null, modifier = Modifier.size(22.dp)) },
                label = { Text(tab.label, style = AtharType.navLabel(active)) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AtharColors.Cypress,
                    selectedTextColor = AtharColors.Cypress,
                    indicatorColor = AtharColors.NavIndicator,
                    unselectedIconColor = AtharColors.Muted,
                    unselectedTextColor = AtharColors.Muted
                )
            )
        }
    }
}
