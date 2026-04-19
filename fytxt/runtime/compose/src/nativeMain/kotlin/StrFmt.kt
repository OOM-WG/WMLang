@file:Suppress("PackageDirectoryMismatch")

package dev.oom_wg.purejoy.fyl.fytxt.strfmt

@Suppress("unused")
actual fun String.fmt(args: Array<out Any?>) = args.fold(this) { str, arg -> str.replaceFirst("%s", "$arg") }