@file:Suppress("PackageDirectoryMismatch")

package tf.gal.shirosu.fyl.fytxt.strfmt

@Suppress("unused")
actual fun String.fmt(args: Array<out Any?>) = args.fold(this) { str, arg -> str.replaceFirst("%s", "$arg") }