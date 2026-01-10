@file:Suppress("PackageDirectoryMismatch", "unused")

package dev.oom_wg.purejoy.fyl.fytxt

import kotlinx.browser.window
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import org.w3c.dom.events.Event

internal actual val platformLocaleProvider = object : LocaleProvider {
	@OptIn(ExperimentalWasmJsInterop::class)
	override fun getLocales() =
		window.navigator.languages.toArray().ifEmpty { arrayOf(window.navigator.language) }
			.map { "$it".replace('-', '_').uppercase() }

	override val localeUpdates = callbackFlow {
		trySend(getLocales())
		({ _: Event -> Unit.also { trySend(getLocales()) } }).let { listener ->
			window.addEventListener("languagechange", listener)
			awaitClose { window.removeEventListener("languagechange", listener) }
		}
	}
}