@file:SuppressLint("ComposableNaming") @file:Suppress("unused")

package dev.oom_wg.purejoy.mlang.systxt

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext

@Composable
fun Unknown() = LocalContext.current.run {
  remember(LocalConfiguration.current) { getString(android.R.string.unknownName) }
}

@Composable
fun Okay() =
  LocalContext.current.run { remember(LocalConfiguration.current) { getString(android.R.string.ok) } }

@Composable
fun Cancel() =
  LocalContext.current.run { remember(LocalConfiguration.current) { getString(android.R.string.cancel) } }