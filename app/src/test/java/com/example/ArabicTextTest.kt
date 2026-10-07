package com.example

import com.example.util.ArabicText
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** القيم المتوقعة مولّدة من normalize_for_search في tools/build_athar_db.py */
class ArabicTextTest {

    @Test
    fun normalizationMatchesBuildScript() {
        assertEquals("الصلاه والزكاه", ArabicText.normalizeForSearch("الصَّلاةُ، وَالزَّكَاةُ"))
        assertEquals(
            "قال رسول الله صلي الله عليه وسلم انما",
            ArabicText.normalizeForSearch("قَالَ رَسُولُ اللَّهِ ـ صلى الله عليه وسلم ـ «إِنَّمَا»")
        )
        assertEquals("اومن بموسي وعيسي 12", ArabicText.normalizeForSearch("أُؤْمِنُ بِمُوسَى وَعِيسَى 12 ٣"))
    }

    @Test
    fun matchQueryPrefixesEveryWord() {
        assertEquals("مالك* نافع*", ArabicText.buildMatchQuery("  مَالِكٍ، نَافِعٍ "))
        assertNull(ArabicText.buildMatchQuery(" «» ، "))
        // لا يمرّ شيء من صياغة FTS
        assertEquals("ابن* عمر*", ArabicText.buildMatchQuery("ابن\" OR عمر*"))
    }

    @Test
    fun displayHelpers() {
        assertEquals("قال رسول الله", ArabicText.stripTashkeel("قَالَ رَسُولُ اللَّهِ"))
        assertEquals("٤٧١٥", ArabicText.toArabicDigits(4715))
    }
}
