@file:Suppress("PackageDirectoryMismatch")

package dev.oom_wg.purejoy.fyl.fytxt.compose

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import dev.oom_wg.purejoy.fyl.fytxt.FYTxtConfig

@Composable
internal actual fun LocaleObserver() {
	val locked by FYTxtConfig.lock.collectAsState()
	LaunchedEffect(LocalConfiguration.current.locales, locked) { if (!locked) FYTxtConfig.updateTags() }
}