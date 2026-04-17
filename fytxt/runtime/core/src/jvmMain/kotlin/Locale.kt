@file:Suppress("PackageDirectoryMismatch", "unused")

package dev.oom_wg.purejoy.fyl.fytxt

import java.util.*

internal actual val platformLocaleProvider = object : LocaleProvider {
	override fun getLocales() = listOf(Locale.getDefault().toLanguageTag().replace('-', '_').uppercase())
	override val localeUpdates = null
}