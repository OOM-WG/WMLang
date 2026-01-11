@file:Suppress("PackageDirectoryMismatch", "unused")

package dev.oom_wg.purejoy.fyl.fytxt

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration

@Composable
internal actual fun LocaleObserver() {
	val locked by FYTxtConfig.lock.collectAsState()
	LaunchedEffect(LocalConfiguration.current.locales, locked) { if (!locked) FYTxtConfig.updateTags() }
}