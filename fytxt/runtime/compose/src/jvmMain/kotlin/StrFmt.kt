@file:Suppress("PackageDirectoryMismatch")

package dev.oom_wg.purejoy.fyl.fytxt.strfmt

@Suppress("unused")
actual fun String.fmt(args: Array<out Any?>) = run { takeIf { args.isEmpty() } ?: format(*args) }