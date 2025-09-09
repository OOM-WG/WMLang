package dev.oom_wg.purejoy.mlang

import com.android.build.gradle.BaseExtension
import org.gradle.api.*
import org.gradle.api.plugins.JavaPlugin
import ren.shiror.fvv.FVVV
import java.io.File

open class MLangExtension {
	var name: String? = null
	var configDir: String? = null
	var baseLang: String? = null
	var base: Boolean? = null
	var compose: Boolean? = null
}

@Suppress("unused")
class MLangPlugin : Plugin<Project> {
	fun setupMLang(project: Project) {
		project.dependencies.add(
			JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME, "dev.oom-wg.PureJoy-MultiLang:base:-SNAPSHOT"
		)
		val android = project.extensions.getByName("android") as BaseExtension
		project.afterEvaluate {
			val ext = project.extensions.getByType(MLangExtension::class.java)

			listOf(MLangExtension::configDir, MLangExtension::baseLang).forEach {
				if (it.get(ext).isNullOrBlank()) throw GradleException("No MLang.${it.name} found.")
			}
			listOf(MLangExtension::base, MLangExtension::compose).forEach {
				if (it.get(ext) == null) throw GradleException("No MLang.${it.name} found.")
			}
			if (ext.base!!.not() && ext.compose!!.not()) throw GradleException("Nothing to do.")
			if (ext.compose!!) project.dependencies.add(
				JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME,
				"dev.oom-wg.PureJoy-MultiLang:compose:-SNAPSHOT"
			)

			val packageName =
				"dev.oom_wg.purejoy${ext.name?.takeIf { it.isNotEmpty() }?.let { ".`$it`" } ?: ""}.mlang"

			val generatedDir = project.layout.buildDirectory.dir("generated/mlang/kotlin").get().asFile
			android.sourceSets.getByName("main").java.srcDir(generatedDir)
			generatedDir.apply { if (exists()) deleteRecursively() }.mkdirs()

			val configDir = project.file(ext.configDir!!)
			configDir.listFiles { file -> file.isDirectory }.apply {
				if (!map { it.name }.contains(ext.baseLang!!)) throw GradleException("No baseLang found.")
			}.associate { dir ->
				dir.name to FVVV().apply {
					dir.walkTopDown().filter { it.isFile && it.extension == "fvv" }
						.forEach { addFromString(it.readText()) }
				}
			}.also { tag2FVV ->
				buildString {
					appendLine("package $packageName\n")
					if (ext.compose!!) {
						appendLine("import androidx.compose.runtime.Composable")
						appendLine("import com.highcapable.pangutext.android.PanguText")
					}
					appendLine("import dev.oom_wg.purejoy.mlang.MLangBase\n")
					appendLine("object MLang {")
					fun runWrite(
						target: MutableMap<String, FVVV>,
						path: List<String> = emptyList(),
						indentLevel: Int = 1,
					): Unit = target.forEach { (k, v) ->
						val indentStr = " ".repeat(indentLevel * 4)
						if (v.sub.isEmpty()) {
							val na = buildList {
								"${indentStr}private val `_$k` by lazy { MLangBase(\"$v\", mapOf(${
									tag2FVV.mapNotNull { (tag, fvv) ->
										path.fold(
											fvv
										) { current, key -> current[key] }[k].value?.let { "\"$tag\" to \"$it\"" }
											.also { if (it == null) add(tag) }
									}.joinToString(", ")
								})) }".let { appendLine(it) }
							}.joinToString(", ")
							if (na.isNotEmpty()) println(path.joinToString(".") + ".$k NA: $na")
							val tip = v.string.replace("(?<!^)\\\\n(?!$)".toRegex()) {
								"\n$indentStr *\n$indentStr * "
							}
							if (ext.base!!) {
								appendLine("$indentStr/** $tip")
								appendLine("$indentStr * @suppress compose")
								if (na.isNotEmpty()) appendLine("$indentStr * NA: $na")
								appendLine("$indentStr **/")
								appendLine($$"$${indentStr}val `$$k` get() = \"$_$$k\"")
							}
							if (ext.compose!!) {
								appendLine("$indentStr/** $tip")
								appendLine("$indentStr * @suppress non-compose")
								if (na.isNotEmpty()) appendLine("$indentStr * NA: $na")
								appendLine("$indentStr **/")
								appendLine("$indentStr@Composable")
								appendLine(
									$$"$${indentStr}fun `$$k`(vararg args: Any?) = \"${PanguText.format(_$$k.get().run { takeIf { args.isEmpty() } ?: format(*args) })}\""
								)
							}
						} else {
							appendLine("${indentStr}object `$k` {")
							runWrite(v.sub, path + k, indentLevel + 1)
							appendLine("$indentStr}")
						}
					}
					runWrite(tag2FVV[ext.baseLang!!]!!.sub)
					appendLine("}")
				}.also { File(generatedDir, "mlang.kt").writeText(it) }
			}
		}
	}

	override fun apply(project: Project) {
		project.extensions.create("MLang", MLangExtension::class.java)
		project.pluginManager.withPlugin("com.android.application") { setupMLang(project) }
		project.pluginManager.withPlugin("com.android.library") { setupMLang(project) }
		// project.pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") { setupMLang(project) }
	}
}