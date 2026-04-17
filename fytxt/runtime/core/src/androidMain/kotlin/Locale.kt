@file:Suppress("PackageDirectoryMismatch", "unused")

package dev.oom_wg.purejoy.fyl.fytxt

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.LocaleList
import java.util.*

internal actual val platformLocaleProvider = object : LocaleProvider {
	override fun getLocales() =
		(if (VERSION.SDK_INT >= VERSION_CODES.N) LocaleList.getDefault().run { List(size()) { this[it] } }
		else listOf(Locale.getDefault())).map { loc ->
			when {
				VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP -> loc.toLanguageTag().replace('-', '_')
				loc.country.isNullOrBlank()               -> loc.language
				else                                      -> "${loc.language}_${loc.country}"
			}.uppercase()
		}

	override val localeUpdates = null
}