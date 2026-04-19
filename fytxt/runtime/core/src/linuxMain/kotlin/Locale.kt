@file:Suppress("PackageDirectoryMismatch")

package dev.oom_wg.purejoy.fyl.fytxt

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import platform.posix.getenv

internal actual val platformLocaleProvider = object : LocaleProvider {
	@OptIn(ExperimentalForeignApi::class)
	override fun getLocales() =
		getenv("LANG")?.toKString()?.substringBefore('.')?.substringBefore('@')?.uppercase()?.let { listOf(it) } ?: emptyList()

	override val localeUpdates = null
}