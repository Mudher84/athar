package com.example.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.data.local.AtharDatabase
import com.example.data.prefs.ReadingPrefs
import com.example.data.repository.AtharRepository
import com.example.data.repository.AtharRepositoryImpl
import com.example.domain.model.BookWithCount
import com.example.domain.model.HadithFilters
import com.example.domain.model.HadithListItem
import com.example.domain.model.NarratorWithCount
import com.example.domain.model.ReadingSettings
import com.example.domain.model.Region
import com.example.domain.model.UiState
import com.example.ui.theme.AtharMatnScale
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel واحد للتبويبات الأربعة: الانتقال بينها يحمل حالة (مثلاً «عرض آثار الراوي»
 * يضبط فلتر الراوي ثم يفتح تبويب الآثار)، فالمشاركة أبسط من تمرير الوسائط.
 */
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class AtharViewModel(
    private val repository: AtharRepository,
    private val readingPrefs: ReadingPrefs
) : ViewModel() {

    // ------------------------------------------------------------ الآثار

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _filters = MutableStateFlow(HadithFilters())
    val filters: StateFlow<HadithFilters> = _filters.asStateFlow()

    /** البحث بعد توقف الكتابة 300ms؛ المسح يُطبَّق فوراً */
    private val debouncedQuery: Flow<String> = _query
        .debounce { if (it.isBlank()) 0L else SEARCH_DEBOUNCE_MS }
        .map { it.trim() }
        .distinctUntilChanged()

    private val hadithRequest = combine(debouncedQuery, _filters, ::Pair).distinctUntilChanged()

    val hadiths: Flow<PagingData<HadithListItem>> = hadithRequest
        .flatMapLatest { (q, f) -> repository.hadiths(q, f) }
        .cachedIn(viewModelScope)

    /** null أثناء العدّ */
    val resultCount: StateFlow<Int?> = hadithRequest
        .flatMapLatest { (q, f) -> repository.countHadiths(q, f) }
        .map<Int, Int?> { it }
        .catch { emit(null) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun onQueryChange(value: String) {
        _query.value = value
    }

    fun setRegion(region: Region?) = _filters.update { it.copy(region = region) }
    fun setBook(bookId: Int?) = _filters.update { it.copy(bookId = bookId) }
    fun setNarrator(narratorId: Int?) = _filters.update { it.copy(narratorId = narratorId) }
    fun setConnectedOnly(value: Boolean) = _filters.update { it.copy(connectedOnly = value) }
    fun resetFilters() {
        _filters.value = HadithFilters()
    }

    /** من الترجمة أو الكتب: فلتر واحد نظيف بدل تراكم فلاتر سابقة غير مرئية */
    fun showNarratorHadiths(narratorId: Int) {
        _query.value = ""
        _filters.value = HadithFilters(narratorId = narratorId)
    }

    fun showBookHadiths(bookId: Int) {
        _query.value = ""
        _filters.value = HadithFilters(bookId = bookId)
    }

    // ------------------------------------------------------------ المحفوظات

    private val _favoritesQuery = MutableStateFlow("")
    val favoritesQuery: StateFlow<String> = _favoritesQuery.asStateFlow()

    private val debouncedFavoritesQuery = _favoritesQuery
        .debounce { if (it.isBlank()) 0L else SEARCH_DEBOUNCE_MS }
        .map { it.trim() }
        .distinctUntilChanged()

    val favorites: Flow<PagingData<HadithListItem>> = debouncedFavoritesQuery
        .flatMapLatest { repository.favorites(it) }
        .cachedIn(viewModelScope)

    /** عدد المحفوظ كله (للترويسة)، بصرف النظر عن البحث */
    val favoritesTotal: StateFlow<Int?> = repository.countFavorites("")
        .map<Int, Int?> { it }
        .catch { emit(null) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val favoritesResultCount: StateFlow<Int?> = debouncedFavoritesQuery
        .flatMapLatest { repository.countFavorites(it) }
        .map<Int, Int?> { it }
        .catch { emit(null) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun onFavoritesQueryChange(value: String) {
        _favoritesQuery.value = value
    }

    fun setFavorite(hadithId: Int, favorite: Boolean) {
        viewModelScope.launch { repository.setFavorite(hadithId, favorite) }
    }

    // ------------------------------------------------------------ الكتب والرواة

    val books: StateFlow<UiState<List<BookWithCount>>> = repository.books()
        .asUiState()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)

    val narrators: StateFlow<UiState<List<NarratorWithCount>>> = repository.narrators()
        .asUiState()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)

    // ------------------------------------------------------------ القراءة

    val readingSettings: StateFlow<ReadingSettings> = readingPrefs.settings

    fun increaseMatnSize() = readingPrefs.changeMatnSize(+AtharMatnScale.Step)
    fun decreaseMatnSize() = readingPrefs.changeMatnSize(-AtharMatnScale.Step)
    fun toggleTashkeel() = readingPrefs.toggleTashkeel()

    private fun <T> Flow<List<T>>.asUiState(): Flow<UiState<List<T>>> =
        map<List<T>, UiState<List<T>>> { if (it.isEmpty()) UiState.Empty else UiState.Success(it) }
            .catch { emit(UiState.Error(it.message ?: "تعذّر فتح القاعدة المحلية")) }

    companion object {
        private const val SEARCH_DEBOUNCE_MS = 300L

        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as Application
                AtharViewModel(
                    repository = AtharRepositoryImpl(AtharDatabase.getInstance(app).atharDao()),
                    readingPrefs = ReadingPrefs(app)
                )
            }
        }
    }
}
