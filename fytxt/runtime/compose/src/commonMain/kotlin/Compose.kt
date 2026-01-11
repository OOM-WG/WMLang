@file:Suppress("PackageDirectoryMismatch", "unused")

package dev.oom_wg.purejoy.fyl.fytxt.compose

import androidx.compose.runtime.*
import dev.oom_wg.purejoy.fyl.fytxt.*

@Stable
class FYTxtState(val tags: State<List<FYTxtTag>>, val group: State<FYTxtGroup?>)

val LocalFYTxtState = staticCompositionLocalOf<FYTxtState> { TODO() }

@Composable
fun FYTxtProvider(content: @Composable () -> Unit) {
	LocaleObserver()

	val tagsState = FYTxtConfig.activeTags.collectAsState()
	val groupState = FYTxtConfig.activeGroup.collectAsState()
	val fytxtState = remember(tagsState, groupState) { FYTxtState(tagsState, groupState) }

	CompositionLocalProvider(LocalFYTxtState provides fytxtState) { content() }
}