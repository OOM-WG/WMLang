@file:Suppress("PackageDirectoryMismatch")

package cn.xz.tar.shirosu.fyl.fytxt

import java.util.*

internal actual val platformLocaleProvider = object : LocaleProvider {
	override fun getLocales() = listOf(Locale.getDefault().toLanguageTag().replace('-', '_').uppercase())
	override val localeUpdates = null
}