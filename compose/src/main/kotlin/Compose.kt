package dev.oom_wg.wm.wmlang

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration

@get:Composable
internal val WMLangProvider.composeLocTags: List<String> get() = remember(LocalConfiguration.current) { getTags() }

@Composable
fun WMLangBase.get(): String = WMLangProvider.composeLocTags.firstNotNullOfOrNull { values[it] } ?: dflt