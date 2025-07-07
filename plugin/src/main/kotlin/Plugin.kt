package dev.oom_wg.wm.wmlang

import com.android.build.gradle.AppExtension
import in_.sakit.fvv.FVVV
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import java.io.File

open class WMLangExtension {
    var configDir: String? = null
    var baseLang: String? = null
    var base: Boolean? = null
    var compose: Boolean? = null
}

@Suppress("unused")
class WMLangPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create("WMLang", WMLangExtension::class.java)
        project.pluginManager.withPlugin("com.android.application") {
            project.dependencies.add(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME, "dev.oom-wg.WMLang:base:wm-SNAPSHOT")
            val android = project.extensions.getByName("android") as AppExtension
            project.afterEvaluate {
                val ext = project.extensions.getByType(WMLangExtension::class.java)
                listOf(WMLangExtension::configDir, WMLangExtension::baseLang).forEach {
                    if (it.get(ext).isNullOrBlank()) throw GradleException("No WMLang.${it.name} found.")
                }
                listOf(WMLangExtension::base, WMLangExtension::compose).forEach {
                    if (it.get(ext) == null) throw GradleException("No WMLang.${it.name} found.")
                }
                if (ext.base!!.not() && ext.compose!!.not()) throw GradleException("Nothing to do.")

                if (ext.compose!!) project.dependencies.add(
                    JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME, "dev.oom-wg.WMLang:compose:wm-SNAPSHOT"
                )

                val generatedDir = project.layout.buildDirectory.dir("generated/wmlang/kotlin").get().asFile
                android.sourceSets.getByName("main").java.srcDir(generatedDir)
                generatedDir.apply {
                    if (exists()) deleteRecursively()
                    mkdirs()
                }
                val configDir = project.file(ext.configDir!!)
                configDir.listFiles { file -> file.isDirectory }
                    .apply { if (!map { it.name }.contains(ext.baseLang!!)) throw GradleException("No baseLang found.") }
                    .associate { dir ->
                        dir.name to FVVV().apply {
                            dir.walkTopDown().filter { it.isFile && it.extension == "fvv" }
                                .forEach { addFromString(it.readText()) }
                        }
                    }.also { tag2FVV ->
                        buildString {
                            appendLine("package dev.oom_wg.wm.wmlang\n")
                            if (ext.compose!!) {
                                appendLine("import androidx.compose.runtime.Composable")
                                appendLine("import com.highcapable.pangutext.android.PanguText\n")
                            }
                            appendLine("object WMLang {")
                            fun runWrite(
                                target: MutableMap<String, FVVV>,
                                path: List<String> = emptyList(),
                                indentLevel: Int = 1,
                            ): Unit = with(target) {
                                forEach { (k, v) ->
                                    val indentStr = " ".repeat(indentLevel * 4)
                                    if (v.sub.isEmpty()) {
                                        val na = mutableListOf<String>()
                                        appendLine(
                                            "${indentStr}private val _$k by lazy { WMLangBase(\"$v\", mapOf(${
                                                tag2FVV.mapNotNull { (tag, fvv) ->
                                                    path.fold(fvv) { current, key -> current[key] }[k].value?.let { "\"$tag\" to \"$it\"" }
                                                        .also { if (it == null) na += tag }
                                                }.joinToString(", ")
                                            })) }"
                                        )
                                        val tip = v.string.replace("(?<!^)\\\\n(?!$)".toRegex()) {
                                            "\n$indentStr *\n$indentStr * "
                                        }
                                        if (ext.base!!) {
                                            appendLine("$indentStr/** $tip")
                                            appendLine("$indentStr * @suppress compose")
                                            if (na.isNotEmpty()) appendLine(
                                                "$indentStr * NA: ${na.joinToString(", ")}"
                                            )
                                            appendLine("$indentStr **/")
                                            appendLine($$"$${indentStr}val $$k get() = \"$_$$k\"")
                                        }
                                        if (ext.compose!!) {
                                            appendLine("$indentStr/** $tip")
                                            appendLine("$indentStr * @suppress non-compose")
                                            if (na.isNotEmpty()) appendLine(
                                                "$indentStr * NA: ${na.joinToString(", ")}"
                                            )
                                            appendLine("$indentStr **/")
                                            appendLine("$indentStr@Composable")
                                            appendLine($$"$${indentStr}fun $$k(vararg args: Any?) = \"${PanguText.format(_$$k.get().format(*args))}\"")
                                        }
                                    } else {
                                        appendLine("${indentStr}object $k {")
                                        runWrite(v.sub, path + k, indentLevel + 1)
                                        appendLine("$indentStr}")
                                    }
                                }
                            }
                            runWrite(tag2FVV[ext.baseLang!!]!!.sub)
                            appendLine("}")
                        }.also { File(generatedDir, "wmlang.kt").writeText(it) }
                    }
            }
        }
    }
}