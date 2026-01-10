@file:Suppress("PackageDirectoryMismatch", "unused")

package dev.oom_wg.purejoy.fyl.fytxt

import kotlinx.browser.window
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import org.w3c.dom.events.EventListener

internal actual val platformLocaleProvider = object : LocaleProvider {
	override fun getLocales() = window.navigator.languages.ifEmpty { arrayOf(window.navigator.language) }
		.map { it.replace('-', '_').uppercase() }

	override val localeUpdates = callbackFlow {
		trySend(getLocales())
		EventListener {
			trySend(getLocales())
		}.let { listener ->
			window.addEventListener("languagechange", listener)
			awaitClose { window.removeEventListener("languagechange", listener) }
		}
	}
}