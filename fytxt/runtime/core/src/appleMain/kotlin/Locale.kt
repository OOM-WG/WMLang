@file:Suppress("PackageDirectoryMismatch")

package dev.oom_wg.purejoy.fyl.fytxt

import platform.Foundation.NSLocale
import platform.Foundation.preferredLanguages

internal actual val platformLocaleProvider = object : LocaleProvider {
	override fun getLocales() = NSLocale.preferredLanguages.map { "$it".replace('-', '_').uppercase() }
	override val localeUpdates = null
}