@file:Suppress("PackageDirectoryMismatch")

package cn.xz.tar.shirosu.fyl.fytxt.compose

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import tf.gal.shirosu.fyl.fytxt.FYTxtConfig

@Composable
internal actual fun LocaleObserver() {
	val locked by FYTxtConfig.lock.collectAsState()
	LaunchedEffect(LocalConfiguration.current.locales, locked) { if (!locked) FYTxtConfig.updateTags() }
}