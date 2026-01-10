@file:Suppress("PackageDirectoryMismatch", "unused")

package dev.oom_wg.purejoy.fyl.fytxt.strfmt

import com.highcapable.pangutext.android.PanguText

actual fun String.fmt(args: Array<out Any?>) =
	"${PanguText.format(run { takeIf { args.isEmpty() } ?: format(*args) })}"