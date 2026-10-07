package com.example.util

/**
 * أدوات النص العربي.
 *
 * [normalizeForSearch] يجب أن يطابق normalize_for_search في tools/build_athar_db.py حرفياً،
 * لأن فهرس hadith_fts مبني بها مسبقاً؛ أي اختلاف يعني نتائج بحث ناقصة.
 */
object ArabicText {

    /** الحركات والعلامات القرآنية والتطويل — نفس _STRIP في سكربت البناء */
    private val STRIP = Regex("[\\u0610-\\u061A\\u064B-\\u065F\\u0670\\u06D6-\\u06ED\\u0640]")

    /** نفس _NON_WORD: كل ما ليس حرفاً عربياً أساسياً أو رقماً لاتينياً يصير فاصلاً */
    private val NON_WORD = Regex("[^\\u0621-\\u064A0-9]+")

    /** للعرض: الحركات فقط، مع إبقاء الحروف كما هي */
    private val TASHKEEL = Regex("[\\u0610-\\u061A\\u064B-\\u065F\\u0670\\u06D6-\\u06ED]")

    private val LETTER_MAP = mapOf(
        'أ' to 'ا', 'إ' to 'ا', 'آ' to 'ا', 'ٱ' to 'ا',
        'ؤ' to 'و', 'ئ' to 'ي', 'ة' to 'ه', 'ى' to 'ي'
    )

    fun normalizeForSearch(text: String): String {
        val stripped = STRIP.replace(text, "")
        val mapped = buildString(stripped.length) {
            for (c in stripped) append(LETTER_MAP[c] ?: c)
        }
        return NON_WORD.replace(mapped, " ").trim().split(' ').filter { it.isNotEmpty() }.joinToString(" ")
    }

    /**
     * تعبير MATCH لـ FTS4: كل كلمة موحّدة مع «*» للبادئة، والكلمات مجتمعة (AND ضمني).
     * الكلمات لا تحوي إلا حروفاً عربية وأرقاماً بعد التوحيد، فلا خطر من صياغة FTS.
     * يعيد null إذا لم يبقَ شيء يُبحث عنه.
     */
    fun buildMatchQuery(raw: String): String? {
        val words = normalizeForSearch(raw).split(' ').filter { it.isNotEmpty() }
        if (words.isEmpty()) return null
        return words.joinToString(" ") { "$it*" }
    }

    /** حذف التشكيل للعرض المجرّد */
    fun stripTashkeel(text: String): String = TASHKEEL.replace(text, "")

    private const val ARABIC_INDIC_ZERO = '٠'

    /** أرقام هندية: 123 ← ١٢٣ */
    fun toArabicDigits(text: String): String = buildString(text.length) {
        for (c in text) append(if (c in '0'..'9') ARABIC_INDIC_ZERO + (c - '0') else c)
    }

    fun toArabicDigits(number: Int): String = toArabicDigits(number.toString())
}

/** اختصار للعرض */
fun Int.arabicDigits(): String = ArabicText.toArabicDigits(this)
