@file:Suppress("PackageDirectoryMismatch")

package tf.gal.shirosu.fyl.fytxt.strfmt

import com.highcapable.pangutext.android.PanguText

@Suppress("unused")
actual fun String.fmt(args: Array<out Any?>) = "${PanguText.format(run { takeIf { args.isEmpty() } ?: format(*args) })}"