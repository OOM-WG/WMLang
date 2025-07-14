package dev.oom_wg.purejoy.mlang

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun MLangProvider.locTags() = remember(LocalConfiguration.current) { getTags() }

@Composable
fun MLangBase.get() = MLangProvider.locTags().firstNotNullOfOrNull { vals[it] } ?: dflt