package dev.oom_wg.wm.wmlang

import android.os.LocaleList
import com.highcapable.betterandroid.system.extension.tool.SystemVersion
import java.util.*

object WMLangProvider {
    fun getTags(): List<String> = mutableListOf<String>().also {
        if (SystemVersion.isHighOrEqualsTo(SystemVersion.N)) {
            val list = LocaleList.getDefault()
            for (i in 0 until list.size()) {
                val loc = list[i]
                it += loc.toLanguageTag().replace('-', '_')
                if (loc.language !in it) it += loc.language
            }
        } else {
            val loc = Locale.getDefault()
            it += if (loc.country.isNullOrBlank()) loc.language else "${loc.language}_${loc.country}"
            if (loc.language !in it) it += loc.language
        }
    }

    @Volatile
    private var _locTags: List<String>? = null
    internal val locTags: List<String> get() = _locTags ?: getTags().also { _locTags = it }

    @Suppress("unused")
    fun clearCache() {
        _locTags = null
    }
}

class WMLangBase(val dflt: String) {
    val values: MutableMap<String, String> = mutableMapOf()
    operator fun set(tag: String, value: String) {
        values[tag] = value
    }

    override fun toString(): String = WMLangProvider.locTags.firstNotNullOfOrNull { values[it] } ?: dflt
}