package com.example.data.prefs

import android.content.Context
import com.example.domain.model.ReadingSettings
import com.example.ui.theme.AtharMatnScale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** إعدادات القراءة (حجم خط المتن، التشكيل) محفوظة محلياً على الجهاز. */
class ReadingPrefs(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(
        ReadingSettings(
            matnSize = AtharMatnScale.clamp(prefs.getInt(KEY_SIZE, AtharMatnScale.Default)),
            showTashkeel = prefs.getBoolean(KEY_TASHKEEL, true)
        )
    )
    val settings: StateFlow<ReadingSettings> = _settings.asStateFlow()

    fun changeMatnSize(delta: Int) = save { it.copy(matnSize = AtharMatnScale.clamp(it.matnSize + delta)) }

    fun toggleTashkeel() = save { it.copy(showTashkeel = !it.showTashkeel) }

    private fun save(transform: (ReadingSettings) -> ReadingSettings) {
        _settings.update(transform)
        val s = _settings.value
        prefs.edit().putInt(KEY_SIZE, s.matnSize).putBoolean(KEY_TASHKEEL, s.showTashkeel).apply()
    }

    private companion object {
        const val FILE = "athar_reading"
        const val KEY_SIZE = "matn_size"
        const val KEY_TASHKEEL = "show_tashkeel"
    }
}
