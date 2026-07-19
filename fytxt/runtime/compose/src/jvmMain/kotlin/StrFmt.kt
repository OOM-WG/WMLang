@file:Suppress("PackageDirectoryMismatch")

package tf.gal.shirosu.fyl.fytxt.strfmt

@Suppress("unused")
actual fun String.fmt(args: Array<out Any?>) = run { takeIf { args.isEmpty() } ?: format(*args) }