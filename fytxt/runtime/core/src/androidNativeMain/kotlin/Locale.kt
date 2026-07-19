@file:Suppress("PackageDirectoryMismatch")

package cn.xz.tar.shirosu.fyl.fytxt

import kotlinx.cinterop.*
import platform.posix.*

internal actual val platformLocaleProvider = object : LocaleProvider {
	@OptIn(ExperimentalForeignApi::class)
	override fun getLocales() = memScoped {
		popen("getprop persist.sys.locale", "r")?.let { pipe ->
			fgets(allocArray<ByteVar>(93), 93, pipe)?.toKString()?.takeIf { it.isNotBlank() }?.trim()?.replace('-', '_')
				?.uppercase().also { pclose(pipe) }
		}?.let { listOf(it) } ?: emptyList()
	}

	override val localeUpdates = null
}