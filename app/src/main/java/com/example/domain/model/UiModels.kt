package com.example.domain.model

import androidx.compose.runtime.Immutable

/**
 * الإقليم كما في hadiths.region_tag ([tag]).
 * «مشترك»: في السند راوٍ مدني وراوٍ شامي معاً.
 */
enum class Region(val tag: String, val label: String, val shortLabel: String) {
    Madinah("المدينة", "أهل المدينة", "مدني"),
    Sham("الشام", "أهل الشام", "شامي"),
    Shared("مشترك", "مسار مشترك", "مشترك");

    companion object {
        fun fromTag(tag: String?): Region? = entries.firstOrNull { it.tag == tag }

        /**
         * إقليم الراوي من حقل narrators.region النصي («المدينة المنورة»، «الشام / دمشق»،
         * «المدينة المنورة / الشام»…): من جمع البلدين فهو مشترك.
         */
        fun ofNarrator(region: String): Region {
            val madani = "المدينة" in region
            val shami = "الشام" in region
            return when {
                madani && shami -> Shared
                shami -> Sham
                else -> Madinah
            }
        }
    }
}

/** فلاتر شاشة الآثار؛ null يعني «الكل». */
@Immutable
data class HadithFilters(
    val region: Region? = null,
    val bookId: Int? = null,
    val narratorId: Int? = null,
    val connectedOnly: Boolean = false
) {
    val isActive: Boolean
        get() = region != null || bookId != null || narratorId != null || connectedOnly
}

@Immutable
data class ReadingSettings(
    val matnSize: Int,
    val showTashkeel: Boolean
)

/** حالة بيانات غير مقسّمة إلى صفحات (الكتب، الرواة). المقسّمة تتبع LoadState في Paging. */
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data object Empty : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}
