@file:Suppress("PackageDirectoryMismatch", "unused")

package dev.oom_wg.purejoy.fyl.fytxt

import kotlinx.cinterop.*
import platform.windows.GetUserDefaultLocaleName
import platform.windows.WCHARVar

internal actual val platformLocaleProvider = object : LocaleProvider {
	@OptIn(ExperimentalForeignApi::class)
	override fun getLocales() = memScoped {
		allocArray<WCHARVar>(85).let { buffer ->
			GetUserDefaultLocaleName(buffer, 85).takeIf { ret -> ret > 0 }?.let {
				listOf(buffer.toKString().replace('-', '_').uppercase())
			}
		} ?: emptyList()
	}

	override val localeUpdates = null
}