package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.BookEntity
import com.example.data.local.entity.NarratorEntity
import com.example.data.repository.AtharRepository
import com.example.data.repository.AtharRepositoryImpl
import com.example.domain.model.HadithDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class SanadFilterType(val labelAr: String) {
    ALL("الكل"),
    MADINAH_ONLY("مسار المدينة فقط"),
    SHAM_ONLY("مسار الشام فقط"),
    EXCLUDE_KUFA_TASHAYYU("استبعاد الكوفة والتشيع")
}

data class HadithUiState(
    val allHadiths: List<HadithDetail> = emptyList(),
    val filteredHadiths: List<HadithDetail> = emptyList(),
    val narrators: List<NarratorEntity> = emptyList(),
    val books: List<BookEntity> = emptyList(),
    val favoriteHadiths: List<HadithDetail> = emptyList(),
    val searchQuery: String = "",
    val activeFilter: SanadFilterType = SanadFilterType.ALL,
    // Advanced search & filtering parameters
    val selectedRegion: String? = null,
    val selectedEra: String? = null,
    val selectedNarrator: String? = null,
    val isAdvancedSearchExpanded: Boolean = false,
    val selectedHadithForChain: HadithDetail? = null,
    val selectedNarratorForModal: NarratorEntity? = null,
    val showTashkeel: Boolean = true,
    val fontSizeScale: Float = 1.0f,
    val currentTab: Int = 0,
    val isDarkMode: Boolean = false,
    val isLoading: Boolean = false,
    val userNotice: String? = null
) {
    val activeFilterCount: Int
        get() = (if (activeFilter != SanadFilterType.ALL) 1 else 0) +
                (if (!selectedRegion.isNullOrBlank()) 1 else 0) +
                (if (!selectedEra.isNullOrBlank()) 1 else 0) +
                (if (!selectedNarrator.isNullOrBlank()) 1 else 0) +
                (if (searchQuery.isNotBlank()) 1 else 0)

    val isAnyFilterActive: Boolean
        get() = activeFilterCount > 0
}

class HadithFilterViewModel(
    private val repository: AtharRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _activeFilter = MutableStateFlow(SanadFilterType.ALL)
    private val _selectedRegion = MutableStateFlow<String?>(null)
    private val _selectedEra = MutableStateFlow<String?>(null)
    private val _selectedNarrator = MutableStateFlow<String?>(null)
    private val _isAdvancedSearchExpanded = MutableStateFlow(false)

    private val _selectedHadithForChain = MutableStateFlow<HadithDetail?>(null)
    private val _selectedNarratorForModal = MutableStateFlow<NarratorEntity?>(null)
    private val _showTashkeel = MutableStateFlow(true)
    private val _fontSizeScale = MutableStateFlow(1.0f)
    private val _currentTab = MutableStateFlow(0)
    private val _isDarkMode = MutableStateFlow(false)
    private val _userNotice = MutableStateFlow<String?>(null)

    private val _searchFiltersFlow = combine(
        _searchQuery,
        _activeFilter,
        _selectedRegion,
        _selectedEra,
        _selectedNarrator
    ) { search, filter, region, era, narrator ->
        SearchFilterParams(search, filter, region, era, narrator)
    }

    private val _displayPrefsFlow = combine(
        _isAdvancedSearchExpanded,
        _showTashkeel,
        _fontSizeScale,
        _isDarkMode
    ) { isExpanded, tashkeel, fontScale, isDark ->
        DisplayPrefsParams(isExpanded, tashkeel, fontScale, isDark)
    }

    private val _modalFlow = combine(
        _currentTab,
        _selectedHadithForChain,
        _selectedNarratorForModal,
        _userNotice
    ) { tab, chainHadith, narrator, notice ->
        ModalParams(tab, chainHadith, narrator, notice)
    }

    val uiState: StateFlow<HadithUiState> = combine(
        repository.getAllHadithDetails(),
        repository.getAllNarrators(),
        repository.getAllBooks(),
        combine(_searchFiltersFlow, _displayPrefsFlow) { sf, dp -> Pair(sf, dp) },
        _modalFlow
    ) { allHadiths, narrators, books, (filters, prefs), modal ->
        val queryNorm = AtharRepositoryImpl.normalizeArabic(filters.search.trim())
        val selectedNarratorNorm = filters.narrator?.let { AtharRepositoryImpl.normalizeArabic(it.trim()) }

        val filtered = allHadiths.filter { detail ->
            // 1. Sanad Path Filter
            val matchesPathFilter = when (filters.filter) {
                SanadFilterType.ALL -> true
                SanadFilterType.MADINAH_ONLY -> {
                    detail.hadith.pathBadge.contains("مدني") || detail.chain.all { it.region.contains("المدينة") || it.era == "صحابي" }
                }
                SanadFilterType.SHAM_ONLY -> {
                    detail.hadith.pathBadge.contains("شام") || detail.chain.any { it.region.contains("الشام") }
                }
                SanadFilterType.EXCLUDE_KUFA_TASHAYYU -> {
                    detail.chain.none { it.region.contains("الكوفة") || it.sectAffiliation?.contains("شيع") == true } &&
                            detail.chain.all { it.isTrusted }
                }
            }

            // 2. Region Filter (المدينة، الشام، الأندلس)
            val matchesRegion = if (filters.region.isNullOrBlank() || filters.region == "الكل") {
                true
            } else {
                detail.hadith.pathBadge.contains(filters.region) ||
                        detail.chain.any { it.region.contains(filters.region) }
            }

            // 3. Era Filter (صحابي، أموي، أندلسي، عباسي)
            val matchesEra = if (filters.era.isNullOrBlank() || filters.era == "الكل") {
                true
            } else {
                detail.chain.any { it.era.contains(filters.era) }
            }

            // 4. Narrator Filter
            val matchesNarrator = if (selectedNarratorNorm.isNullOrBlank()) {
                true
            } else {
                detail.chain.any {
                    AtharRepositoryImpl.normalizeArabic(it.name).contains(selectedNarratorNorm) ||
                            (it.popularName != null && AtharRepositoryImpl.normalizeArabic(it.popularName).contains(selectedNarratorNorm))
                } || AtharRepositoryImpl.normalizeArabic(detail.hadith.rawSanad).contains(selectedNarratorNorm)
            }

            // 5. Free-text Search Query
            val matchesSearch = if (queryNorm.isEmpty()) {
                true
            } else {
                val matnNorm = AtharRepositoryImpl.normalizeArabic(detail.hadith.matn)
                val sanadNorm = AtharRepositoryImpl.normalizeArabic(detail.hadith.rawSanad)
                val chapterNorm = AtharRepositoryImpl.normalizeArabic(detail.hadith.chapter)
                val badgeNorm = AtharRepositoryImpl.normalizeArabic(detail.hadith.pathBadge)
                val bookNorm = AtharRepositoryImpl.normalizeArabic(detail.book.title)
                val chainNorm = detail.chain.any {
                    AtharRepositoryImpl.normalizeArabic(it.name).contains(queryNorm) ||
                            (it.popularName != null && AtharRepositoryImpl.normalizeArabic(it.popularName).contains(queryNorm)) ||
                            AtharRepositoryImpl.normalizeArabic(it.region).contains(queryNorm) ||
                            AtharRepositoryImpl.normalizeArabic(it.era).contains(queryNorm)
                }

                matnNorm.contains(queryNorm) ||
                        sanadNorm.contains(queryNorm) ||
                        chapterNorm.contains(queryNorm) ||
                        badgeNorm.contains(queryNorm) ||
                        bookNorm.contains(queryNorm) ||
                        chainNorm
            }

            matchesPathFilter && matchesRegion && matchesEra && matchesNarrator && matchesSearch
        }

        val favorites = allHadiths.filter { it.isFavorite }

        HadithUiState(
            allHadiths = allHadiths,
            filteredHadiths = filtered,
            narrators = narrators,
            books = books,
            favoriteHadiths = favorites,
            searchQuery = filters.search,
            activeFilter = filters.filter,
            selectedRegion = filters.region,
            selectedEra = filters.era,
            selectedNarrator = filters.narrator,
            isAdvancedSearchExpanded = prefs.isExpanded,
            selectedHadithForChain = modal.chainHadith,
            selectedNarratorForModal = modal.narrator,
            showTashkeel = prefs.tashkeel,
            fontSizeScale = prefs.fontScale,
            currentTab = modal.tab,
            isDarkMode = prefs.isDark,
            userNotice = modal.notice,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HadithUiState(isLoading = true, isDarkMode = false)
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }

    fun setFilterType(filterType: SanadFilterType) {
        _activeFilter.value = filterType
    }

    fun setSelectedRegion(region: String?) {
        _selectedRegion.value = if (_selectedRegion.value == region) null else region
    }

    fun setSelectedEra(era: String?) {
        _selectedEra.value = if (_selectedEra.value == era) null else era
    }

    fun setSelectedNarrator(narratorName: String?) {
        _selectedNarrator.value = if (_selectedNarrator.value == narratorName) null else narratorName
    }

    fun toggleAdvancedSearch() {
        _isAdvancedSearchExpanded.update { !it }
    }

    fun resetAllFilters() {
        _searchQuery.value = ""
        _activeFilter.value = SanadFilterType.ALL
        _selectedRegion.value = null
        _selectedEra.value = null
        _selectedNarrator.value = null
    }

    fun setTab(tab: Int) {
        _currentTab.value = tab
    }

    fun toggleTashkeel() {
        _showTashkeel.update { !it }
    }

    fun toggleTheme() {
        _isDarkMode.update { !it }
    }

    fun setLightMode() {
        _isDarkMode.value = false
    }

    fun adjustFontSize(delta: Float) {
        _fontSizeScale.update { current ->
            (current + delta).coerceIn(0.85f, 1.4f)
        }
    }

    fun toggleFavorite(hadithId: Int, currentFav: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(hadithId, currentFav)
            _selectedHadithForChain.value?.let { current ->
                if (current.hadith.id == hadithId) {
                    _selectedHadithForChain.value = current.copy(isFavorite = !currentFav)
                }
            }
        }
    }

    fun openNarratorChain(hadith: HadithDetail) {
        _selectedHadithForChain.value = hadith
    }

    fun closeNarratorChain() {
        _selectedHadithForChain.value = null
    }

    fun openNarratorBio(narratorId: Int) {
        val narrator = uiState.value.narrators.find { it.id == narratorId }
        _selectedNarratorForModal.value = narrator
    }

    fun closeNarratorBio() {
        _selectedNarratorForModal.value = null
    }

    fun clearNotice() {
        _userNotice.value = null
    }

    private data class SearchFilterParams(
        val search: String,
        val filter: SanadFilterType,
        val region: String?,
        val era: String?,
        val narrator: String?
    )

    private data class DisplayPrefsParams(
        val isExpanded: Boolean,
        val tashkeel: Boolean,
        val fontScale: Float,
        val isDark: Boolean
    )

    private data class ModalParams(
        val tab: Int,
        val chainHadith: HadithDetail?,
        val narrator: NarratorEntity?,
        val notice: String?
    )

    class Factory(private val repository: AtharRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HadithFilterViewModel::class.java)) {
                return HadithFilterViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
