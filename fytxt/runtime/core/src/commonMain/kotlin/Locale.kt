@file:Suppress("PackageDirectoryMismatch")

package dev.oom_wg.purejoy.fyl.fytxt

import kotlinx.coroutines.flow.Flow

internal interface LocaleProvider {
	fun getLocales(): List<String>
	val localeUpdates: Flow<List<String>>?
}

internal expect val platformLocaleProvider: LocaleProvider