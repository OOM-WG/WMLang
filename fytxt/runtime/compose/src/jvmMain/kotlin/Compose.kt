@file:Suppress("PackageDirectoryMismatch", "unused")

package dev.oom_wg.purejoy.fyl.fytxt

import androidx.compose.runtime.Composable

@Composable
actual inline fun <T> FYTxtConfig.obsLoc(block: @Composable () -> T) = block()