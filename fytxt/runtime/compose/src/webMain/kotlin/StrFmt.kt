@file:Suppress("PackageDirectoryMismatch", "unused")

package dev.oom_wg.purejoy.fyl.fytxt.strfmt

actual fun String.fmt(args: Array<out Any?>) = args.fold(this) { str, arg -> str.replaceFirst("%s", "$arg") }