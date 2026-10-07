package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.AtharDao
import com.example.data.local.entity.BookEntity
import com.example.data.local.entity.FavoriteHadithEntity
import com.example.data.local.entity.HadithEntity
import com.example.data.local.entity.HadithNarratorChainEntity
import com.example.data.local.entity.NarratorEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        BookEntity::class,
        NarratorEntity::class,
        HadithEntity::class,
        HadithNarratorChainEntity::class,
        FavoriteHadithEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AtharDatabase : RoomDatabase() {

    abstract fun atharDao(): AtharDao

    companion object {
        private const val DATABASE_NAME = "athar_master.db"

        @Volatile
        private var INSTANCE: AtharDatabase? = null

        fun getInstance(context: Context): AtharDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AtharDatabase::class.java,
                    DATABASE_NAME
                )
                    .addCallback(AtharDatabaseCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AtharDatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateInitialData(database.atharDao())
                }
            }
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    val dao = database.atharDao()
                    populateInitialData(dao)
                }
            }
        }

        private suspend fun populateInitialData(dao: AtharDao) {
            // Books
            dao.insertBook(BookEntity(1, "مُوَطَّأُ الإِمَامِ مَالِكٍ", "الإِمَامُ مَالِكُ بْنُ أَنَسٍ", "أموي/أوائل العباسي"))
            dao.insertBook(BookEntity(2, "سُنَنُ وَمَسَائِلُ الإِمَامِ الأَوْزَاعِيِّ", "عَبْدُ الرَّحْمَنِ بْنُ عَمْرٍو الأَوْزَاعِيُّ", "أموي/شامي"))
            dao.insertBook(BookEntity(3, "المُدَوَّنَةُ الكُبْرَى فِي فِقْهِ أَهْلِ المَدِينَةِ", "سَحْنُونٌ عَنِ ابْنِ القَاسِمِ عَنْ مَالِكٍ", "المدرسة المالكية المغربية والأندلسية"))
            dao.insertBook(BookEntity(4, "مَرْوِيَّاتُ وَآثَارُ أَهْلِ الشَّامِ وَالأَنْدَلُسِ", "مَجْمُوعَةٌ مِنْ أَئِمَّةِ الشَّامِ وَالأَنْدَلُسِ", "الحقبة الأموية"))

            // Narrators with Full Tashkeel
            dao.insertNarrator(NarratorEntity(1, "مَالِكُ بْنُ أَنَسٍ الأَصْبَحِيُّ", "الإِمَامُ مَالِكٌ", "المدينة", "أموي/أوائل العباسي", true, "سني مدني", "إِمَامُ دَارِ الهِجْرَةِ وَمُؤَسِّسُ فِقْهِ عَمَلِ أَهْلِ المَدِينَةِ"))
            dao.insertNarrator(NarratorEntity(2, "نَافِعٌ مَوْلَى عَبْدِ اللَّهِ بْنِ عُمَرَ", "نَافِعٌ", "المدينة", "أموي", true, "سني مدني", "سِلْسِلَةُ الذَّهَبِ وَمُفْتِي المَدِينَةِ المُنَوَّرَةِ"))
            dao.insertNarrator(NarratorEntity(3, "عَبْدُ اللَّهِ بْنُ عُمَرَ بْنِ الخَطَّابِ", "ابْنُ عُمَرَ", "المدينة", "صحابي", true, "صحابي", "صَحَابِيٌّ جَلِيلٌ مِنْ أَشَدِّ الصَّحَابَةِ اتِّبَاعًا لِآثَارِ النَّبِيِّ ﷺ"))
            dao.insertNarrator(NarratorEntity(4, "مُحَمَّدُ بْنُ مُسْلِمِ بْنِ شِهَابٍ الزُّهْرِيُّ", "ابْنُ شِهَابٍ الزُّهْرِيُّ", "المدينة/الشام", "أموي", true, "سني أموي", "مُحَدِّثُ الشَّامِ وَالحِجَازِ وَأَوَّلُ مَنْ دَوَّنَ الحَدِيثَ بِأَمْرِ عُمَرَ بْنِ عَبْدِ العَزِيزِ"))
            dao.insertNarrator(NarratorEntity(5, "سَالِمُ بْنُ عَبْدِ اللَّهِ بْنِ عُمَرَ", "سَالِمُ بْنُ عَبْدِ اللَّهِ", "المدينة", "أموي", true, "سني مدني", "مِنْ فُقَهَاءِ المَدِينَةِ السَّبْعَةِ وَزَاهِدِ التَّابِعِينَ"))
            dao.insertNarrator(NarratorEntity(6, "عَبْدُ الرَّحْمَنِ بْنُ عَمْرٍو الأَوْزَاعِيُّ", "الإِمَامُ الأَوْزَاعِيُّ", "الشام", "أموي", true, "سني شامي", "إِمَامُ أَهْلِ الشَّامِ وَثَغْرِ بَيْرُوتَ فِي الحَدِيثِ وَالفِقْهِ وَالمَغَازِي"))
            dao.insertNarrator(NarratorEntity(7, "حَرِيزُ بْنُ عُثْمَانَ الرَّحَبِيُّ الحِمْصِيُّ", "حَرِيزُ بْنُ عُثْمَانَ", "الشام", "أموي", true, "سني شامي", "ثَبْتٌ مُتْقِنٌ، مُوَالٍ لِلْأُمَوِيِّينَ وَمُبَرَّأٌ مِنَ التَّشَيُّعِ"))
            dao.insertNarrator(NarratorEntity(8, "يَحْيَى بْنُ يَحْيَى اللَّيْثِيُّ المَصْمُودِيُّ", "يَحْيَى بْنُ يَحْيَى اللَّيْثِيُّ", "الأندلس", "أموي أندلسي", true, "سني أندلسي", "رَاوِي المُوَطَّأِ المُعْتَمَدُ فِي الأَنْدَلُسِ وَالمَغْرِبِ، تِلْمِيذُ الإِمَامِ مَالِكٍ"))
            dao.insertNarrator(NarratorEntity(9, "مُعَاوِيَةُ بْنُ أَبِي سُفْيَانَ", "مُعَاوِيَةُ بْنُ أَبِي سُفْيَانَ", "الشام", "صحابي", true, "صحابي", "كَاتِبُ الوَحْيِ وَخَلِيفَةُ المُسْلِمِينَ وَمُؤَسِّسُ الدَّوْلَةِ الأُمَوِيَّةِ بِالشَّامِ"))
            dao.insertNarrator(NarratorEntity(10, "سَعِيدُ بْنُ المُسَيَّبِ القُرَشِيُّ", "سَعِيدُ بْنُ المُسَيَّبِ", "المدينة", "أموي", true, "سني مدني", "سَيِّدُ التَّابِعِينَ وَكَبِيرُ فُقَهَاءِ المَدِينَةِ السَّبْعَةِ"))
            dao.insertNarrator(NarratorEntity(11, "عُرْوَةُ بْنُ الزُّبَيْرِ بْنِ العَوَّامِ", "عُرْوَةُ بْنُ الزُّبَيْرِ", "المدينة", "أموي", true, "سني مدني", "أَحَدُ فُقَهَاءِ المَدِينَةِ السَّبْعَةِ وَإِمَامُ المَغَازِي وَالآثَارِ"))
            dao.insertNarrator(NarratorEntity(12, "أَبُو الدَّرْدَاءِ الأَنْصَارِيُّ", "أَبُو الدَّرْدَاءِ", "الشام", "صحابي", true, "صحابي", "قَاضِي دِمَشْقَ وَحَكِيمُ الأُمَّةِ وَمُعَلِّمُ أَهْلِ الشَّامِ"))
            dao.insertNarrator(NarratorEntity(13, "رَجَاءُ بْنُ حَيْوَةَ الكِنْدِيُّ", "رَجَاءُ بْنُ حَيْوَةَ", "الشام", "أموي", true, "سني شامي", "فَقِيهٌ وَمُسْتَشَارُ خُلَفَاءِ بَنِي أُمَيَّةَ (سُلَيْمَانَ وَعُمَرَ بْنِ عَبْدِ العَزِيزِ)"))
            dao.insertNarrator(NarratorEntity(14, "عُبَادَةُ بْنُ الصَّامِتِ الأَنْصَارِيُّ", "عُبَادَةُ بْنُ الصَّامِتِ", "الشام", "صحابي", true, "صحابي", "صَحَابِيٌّ بَدْرِيٌّ، قَاضِي الشَّامِ وَمُعَلِّمُ أَهْلِهَا بِالمَسْجِدِ الأَقْصَى"))
            dao.insertNarrator(NarratorEntity(15, "قَبِيصَةُ بْنُ ذُؤَيْبٍ الخُزَاعِيُّ", "قَبِيصَةُ بْنُ ذُؤَيْبٍ", "الشام/المدينة", "أموي", true, "سني شامي/مدني", "فَقِيهُ المَدِينَةِ وَالشَّامِ، صَاحِبُ خَاتَمِ عَبْدِ المَلِكِ بْنِ مَرْوَانَ"))

            // Fully Vocalized Prophetic Athar with Full Diacritics (حركات وتشكيل كامل)
            dao.insertHadith(HadithEntity(1, 1, "كِتَابُ وُقُوتِ الصَّلَاةِ", "سَنَدٌ مَدَنِيٌّ خَالِصٌ", "مَالِكٌ عَنْ نَافِعٍ عَنْ عَبْدِ اللَّهِ بْنِ عُمَرَ", "أَنَّ رَسُولَ اللَّهِ ﷺ قَالَ: «الَّذِي تَفُوتُهُ صَلَاةُ العَصْرِ كَأَنَّمَا وُتِرَ أَهْلَهُ وَمَالَهُ»."))
            dao.insertHadith(HadithEntity(2, 1, "كِتَابُ الإِيمَانِ", "سَنَدٌ شَامِيٌّ مَدَنِيٌّ", "الأَوْزَاعِيُّ عَنِ الزُّهْرِيِّ عَنْ سَالِمِ بْنِ عَبْدِ اللَّهِ عَنْ أَبِيهِ", "أَنَّ رَسُولَ اللَّهِ ﷺ سَمِعَ رَجُلًا يَعِظُ أَخَاهُ فِي الحَيَاءِ، فَقَالَ رَسُولُ اللَّهِ ﷺ: «دَعْهُ فَإِنَّ الحَيَاءَ مِنَ الإِيمَانِ»."))
            dao.insertHadith(HadithEntity(3, 1, "كِتَابُ الجَامِعِ وَالأَدَبِ", "سَنَدٌ مَدَنِيٌّ (سِلْسِلَةُ الذَّهَبِ)", "مَالِكٌ عَنْ نَافِعٍ عَنْ عَبْدِ اللَّهِ بْنِ عُمَرَ", "أَنَّ رَسُولَ اللَّهِ ﷺ قَالَ: «لَا يُقِيمُ الرَّجُلُ الرَّجُلَ مِنْ مَقْعَدِهِ ثُمَّ يَجْلِسُ فِيهِ، وَلَكِنْ تَفَسَّحُوا وَتَوَسَّعُوا»."))
            dao.insertHadith(HadithEntity(4, 2, "كِتَابُ الجِهَادِ وَالسِّيَرِ", "سَنَدٌ شَامِيٌّ خَالِصٌ", "الأَوْزَاعِيُّ عَنْ حَرِيزِ بْنِ عُثْمَانَ عَنْ مُعَاوِيَةَ بْنِ أَبِي سُفْيَانَ", "سَمِعْتُ رَسُولَ اللَّهِ ﷺ يَقُولُ: «لَا تَنْقَطِعُ الهِجْرَةُ حَتَّى تَنْقَطِعَ التَّوْبَةُ، وَلَا تَنْقَطِعُ التَّوْبَةُ حَتَّى تَطْلُعَ الشَّمْسُ مِنْ مَغْرِبِهَا»."))
            dao.insertHadith(HadithEntity(5, 1, "كِتَابُ الصَّلَاةِ وَالجُمُعَةِ", "سَنَدٌ مَدَنِيٌّ خَالِصٌ", "مَالِكٌ عَنِ الزُّهْرِيِّ عَنْ سَالِمٍ عَنْ عَبْدِ اللَّهِ بْنِ عُمَرَ", "أَنَّ رَسُولَ اللَّهِ ﷺ قَالَ: «إِذَا جَاءَ أَحَدُكُمُ الجُمُعَةَ فَلْيَغْتَسِلْ»."))
            dao.insertHadith(HadithEntity(6, 2, "كِتَابُ العِلْمِ وَالحِكْمَةِ", "سَنَدٌ شَامِيٌّ أُمَوِيٌّ", "الأَوْزَاعِيُّ عَنِ الزُّهْرِيِّ عَنْ قَبِيصَةَ بْنِ ذُؤَيْبٍ الخُزَاعِيِّ", "عَنْ أَبِي الدَّرْدَاءِ رَضِيَ اللَّهُ عَنْهُ قَالَ: «تَعَلَّمُوا قَبْلَ أَنْ يُرْفَعَ العِلْمُ، وَإِنَّ رَفْعَ العِلْمِ ذَهَابُ العُلَمَاءِ، وَالعَالِمُ وَالمُتَعَلِّمُ فِي الأَجْرِ سَوَاءٌ وَلَا خَيْرَ فِي سَائِرِ النَّاسِ بَعْدَهُمَا»."))
            dao.insertHadith(HadithEntity(7, 3, "كِتَابُ البُيُوعِ وَالأَحْكَامِ", "سَنَدٌ أَنْدَلُسِيٌّ مَدَنِيٌّ", "يَحْيَى بْنُ يَحْيَى اللَّيْثِيُّ عَنْ مَالِكٍ عَنْ نَافِعٍ عَنِ ابْنِ عُمَرَ", "أَنَّ رَسُولَ اللَّهِ ﷺ قَالَ: «المُتَبَايِعَانِ كُلُّ وَاحِدٍ مِنْهُمَا بِالخِيَارِ عَلَى صَاحِبِهِ مَا لَمْ يَتَفَرَّقَا إِلَّا بَيْعَ الخِيَارِ»."))
            dao.insertHadith(HadithEntity(8, 1, "كِتَابُ الحَجِّ وَالمَنَاسِكِ", "سَنَدٌ مَدَنِيٌّ (الفُقَهَاءُ السَّبْعَةُ)", "مَالِكٌ عَنِ ابْنِ شِهَابٍ الزُّهْرِيِّ عَنْ سَعِيدِ بْنِ المُسَيَّبِ", "أَنَّ عُمَرَ بْنَ الخَطَّابِ وَعُثْمَانَ بْنَ عَفَّانَ رَضِيَ اللَّهُ عَنْهُمَا كَانَا يَقْضِيَانِ فِي السُّنَّةِ وَيَتَّبِعَانِ عَمَلَ أَهْلِ المَدِينَةِ وَآثَارَ رَسُولِ اللَّهِ ﷺ."))
            dao.insertHadith(HadithEntity(9, 4, "كِتَابُ السِّيَرِ وَالآثَارِ الأُمَوِيَّةِ", "سَنَدٌ شَامِيٌّ أُمَوِيٌّ مُوَثَّقٌ", "رَجَاءُ بْنُ حَيْوَةَ عَنْ عُبَادَةَ بْنِ الصَّامِتِ", "أَنَّ النَّبِيَّ ﷺ قَالَ: «خُذُوا عَنِّي، قَدْ جَعَلَ اللَّهُ لَهُنَّ سَبِيلًا؛ البِكْرُ بِالبِكْرِ جَلْدُ مِائَةٍ وَتَغْرِيبُ عَامٍ»."))
            dao.insertHadith(HadithEntity(10, 1, "كِتَابُ الأَقْضِيَةِ وَالأَحْكَامِ", "سَنَدٌ مَدَنِيٌّ خَالِصٌ", "مَالِكٌ عَنْ نَافِعٍ عَنِ ابْنِ عُمَرَ", "أَنَّ رَسُولَ اللَّهِ ﷺ قَضَى بِاليَمِينِ مَعَ الشَّاهِدِ."))

            // Chains
            dao.insertChainLink(HadithNarratorChainEntity(1, 1, 1))
            dao.insertChainLink(HadithNarratorChainEntity(1, 2, 2))
            dao.insertChainLink(HadithNarratorChainEntity(1, 3, 3))

            dao.insertChainLink(HadithNarratorChainEntity(2, 6, 1))
            dao.insertChainLink(HadithNarratorChainEntity(2, 4, 2))
            dao.insertChainLink(HadithNarratorChainEntity(2, 5, 3))
            dao.insertChainLink(HadithNarratorChainEntity(2, 3, 4))

            dao.insertChainLink(HadithNarratorChainEntity(3, 1, 1))
            dao.insertChainLink(HadithNarratorChainEntity(3, 2, 2))
            dao.insertChainLink(HadithNarratorChainEntity(3, 3, 3))

            dao.insertChainLink(HadithNarratorChainEntity(4, 6, 1))
            dao.insertChainLink(HadithNarratorChainEntity(4, 7, 2))
            dao.insertChainLink(HadithNarratorChainEntity(4, 9, 3))

            dao.insertChainLink(HadithNarratorChainEntity(5, 1, 1))
            dao.insertChainLink(HadithNarratorChainEntity(5, 4, 2))
            dao.insertChainLink(HadithNarratorChainEntity(5, 5, 3))
            dao.insertChainLink(HadithNarratorChainEntity(5, 3, 4))

            dao.insertChainLink(HadithNarratorChainEntity(6, 6, 1))
            dao.insertChainLink(HadithNarratorChainEntity(6, 4, 2))
            dao.insertChainLink(HadithNarratorChainEntity(6, 15, 3))
            dao.insertChainLink(HadithNarratorChainEntity(6, 12, 4))

            dao.insertChainLink(HadithNarratorChainEntity(7, 8, 1))
            dao.insertChainLink(HadithNarratorChainEntity(7, 1, 2))
            dao.insertChainLink(HadithNarratorChainEntity(7, 2, 3))
            dao.insertChainLink(HadithNarratorChainEntity(7, 3, 4))

            dao.insertChainLink(HadithNarratorChainEntity(8, 1, 1))
            dao.insertChainLink(HadithNarratorChainEntity(8, 4, 2))
            dao.insertChainLink(HadithNarratorChainEntity(8, 10, 3))

            dao.insertChainLink(HadithNarratorChainEntity(9, 13, 1))
            dao.insertChainLink(HadithNarratorChainEntity(9, 14, 2))

            dao.insertChainLink(HadithNarratorChainEntity(10, 1, 1))
            dao.insertChainLink(HadithNarratorChainEntity(10, 2, 2))
            dao.insertChainLink(HadithNarratorChainEntity(10, 3, 3))

            dao.insertFavorite(FavoriteHadithEntity(1, 1700000000))
        }
    }
}
