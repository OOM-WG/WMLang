package dev.oom_wg.wm.wmlang

import android.content.Context
import androidx.startup.Initializer

@Suppress("unused")
class Startup : Initializer<Unit> {
    override fun create(context: Context) = WMLang.init()
    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}