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
                val objs = configDir.listFiles { _, name -> name.endsWith(".fvv") }?.mapNotNull { fvvFile ->
                    fvvFile.name.removeSuffix(".fvv").run { replace("_", "").uppercase() }
                } ?: throw GradleException("No fvv file found.")
                configDir.listFiles { _, name -> name.endsWith(".fvv") }?.forEach { fvvFile ->
                    val tag = fvvFile.name.removeSuffix(".fvv")
                    if (tag == ext.baseLang) WMLangGenerator.generateMain(
                        fvvFile, File(generatedDir, "base.kt"), objs, ext.base!!, ext.compose!!
                    )
                    WMLangGenerator.generateMulti(fvvFile, File(generatedDir, "$tag.kt"))
                }
            }
        }
    }
}

object WMLangGenerator {
    fun generateMain(input: File, output: File, objects: List<String>, base: Boolean, compose: Boolean) {
        FVVV(null).apply {
            addFromString(input.readText())
            buildString {
                appendLine("package dev.oom_wg.wm.wmlang\n")
                if (compose) {
                    appendLine("import androidx.compose.runtime.Composable")
                    appendLine("import com.highcapable.pangutext.android.PanguText\n")
                }
                appendLine("object WMLang {")
                fun runWrite(target: MutableMap<String, FVVV>, indentLevel: Int): Unit = with(target) {
                    forEach { (k, v) ->
                        val indentStr = " ".repeat(indentLevel * 4)
                        if (v.sub.isEmpty()) {
                            appendLine("${indentStr}internal val _$k = WMLangBase(\"$v\")")
                            val tip = v.string.replace(Regex("(?<!^)\\\\n(?!$)")) {
                                "\n$indentStr *\n$indentStr * "
                            }
                            if (base) {
                                appendLine("$indentStr/** $tip")
                                appendLine("$indentStr * @suppress compose */")
                                appendLine($$"$${indentStr}val $$k get() = \"$_$$k\"")
                            }
                            if (compose) {
                                appendLine("$indentStr/** $tip")
                                appendLine("$indentStr * @suppress non-compose */")
                                appendLine("$indentStr@Composable")
                                appendLine($$"$${indentStr}fun $$k(vararg args: Any?) = \"${PanguText.format(_$$k.get().format(*args))}\"")
                            }
                        } else {
                            appendLine("${indentStr}object $k {")
                            runWrite(v.sub, indentLevel + 1)
                            appendLine("$indentStr}")
                        }
                    }
                }
                appendLine("    fun init() {")
                objects.forEach {
                    appendLine("        WMLang$it()")
                }
                appendLine("    }\n")
                runWrite(sub, 1)
                appendLine("}")
            }.also { output.writeText(it) }
        }
    }

    fun generateMulti(input: File, output: File) {
        FVVV(null).apply {
            addFromString(input.readText())
            buildString {
                val lang = input.name.removeSuffix(".fvv")
                appendLine("package dev.oom_wg.wm.wmlang\n")
                appendLine("class WMLang${lang.replace("_", "").uppercase()} {")
                appendLine("    init {")
                fun runWrite(target: MutableMap<String, FVVV>, parent: String): Unit = with(target) {
                    forEach { (k, v) ->
                        if (v.sub.isEmpty()) appendLine("        WMLang.${parent}_$k[\"$lang\"] = \"$v\"")
                        else runWrite(v.sub, "$parent$k.")
                    }
                }
                runWrite(sub, "")
                appendLine("    }")
                appendLine("}")
            }.also { output.writeText(it) }
        }
    }
}