@file:Suppress("PackageDirectoryMismatch")

package tf.gal.shirosu.fyl.fytxt.compose

import androidx.compose.runtime.*
import cn.xz.tar.shirosu.fyl.fytxt.compose.LocaleObserver
import tf.gal.shirosu.fyl.fytxt.*

@Stable
class FYTxtState(val tags: State<List<FYTxtTag>>, val group: State<FYTxtGroup?>)

val LocalFYTxtState = staticCompositionLocalOf<FYTxtState> { TODO() }

@Suppress("unused")
@Composable
fun FYTxtProvider(content: @Composable () -> Unit) {
	LocaleObserver()

	val tagsState = FYTxtConfig.activeTags.collectAsState()
	val groupState = FYTxtConfig.activeGroup.collectAsState()
	val fytxtState = remember(tagsState, groupState) { FYTxtState(tagsState, groupState) }

	CompositionLocalProvider(LocalFYTxtState provides fytxtState) { content() }
}

@Suppress("unused", "UnusedReceiverParameter")
@Composable
inline fun <T> FYTxtConfig.observe(crossinline block: () -> T): T {
	val state = LocalFYTxtState.current
	val tags by state.tags
	val group by state.group
	return remember(tags, group) { block() }
}