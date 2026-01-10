@file:Suppress("PackageDirectoryMismatch", "unused")

package dev.oom_wg.purejoy.fyl.fytxt

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration

@Composable
actual inline fun <T> FYTxtConfig.obsLoc(block: @Composable () -> T): T {
	val locked by lock.collectAsState()
	LaunchedEffect(LocalConfiguration.current, locked) { if (!locked) updateTags() }
	return block()
}