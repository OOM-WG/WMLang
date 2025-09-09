@file:SuppressLint("ComposableNaming") @file:Suppress("unused")

package dev.oom_wg.purejoy.mlang.systxt

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource

@Composable
fun Unknown() = stringResource(remember(LocalConfiguration.current) { android.R.string.unknownName })

@Composable
fun Okay() = stringResource(remember(LocalConfiguration.current) { android.R.string.ok })

@Composable
fun Cancel() = stringResource(remember(LocalConfiguration.current) { android.R.string.cancel })