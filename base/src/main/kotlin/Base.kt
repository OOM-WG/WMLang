package dev.oom_wg.purejoy.mlang

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.LocaleList
import java.util.*

object MLangProvider {
  fun getTags() = buildList {
    (if (VERSION.SDK_INT >= VERSION_CODES.N) LocaleList.getDefault().run { List(size()) { this[it] } }
    else listOf(Locale.getDefault())).forEach {
      with(it) {
        if (country.isNullOrBlank()) add(language)
        else add("${language}_${country}").also { if (language !in this@buildList) add(language) }
      }
    }
  }

  @Volatile private var _locTags: List<String>? = null
  val locTags get() = _locTags ?: getTags().also { _locTags = it }

  @Suppress("unused")
  fun clearCache() {
    _locTags = null
  }
}

class MLangBase(val dflt: String, val vals: Map<String, String>) {
  override fun toString() = MLangProvider.locTags.firstNotNullOfOrNull { vals[it] } ?: dflt
}