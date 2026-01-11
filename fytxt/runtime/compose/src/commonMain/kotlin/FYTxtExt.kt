@file:Suppress("PackageDirectoryMismatch", "unused")

package dev.oom_wg.purejoy.fyl.fytxt

import androidx.compose.runtime.*
import dev.oom_wg.purejoy.fyl.fytxt.compose.LocalFYTxtState

@Suppress("UnusedReceiverParameter")
@Composable
inline fun <T> FYTxtConfig.observe(crossinline block: () -> T): T {
	val state = LocalFYTxtState.current
	val tags by state.tags
	val group by state.group
	return remember(tags, group) { block() }
}