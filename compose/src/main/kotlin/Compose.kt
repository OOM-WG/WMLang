package dev.oom_wg.wm.wmlang

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration

@Composable
private fun WMLangProvider.locTags() = remember(LocalConfiguration.current) { getTags() }

@Composable
fun WMLangBase.get() = WMLangProvider.locTags().firstNotNullOfOrNull { vals[it] } ?: dflt