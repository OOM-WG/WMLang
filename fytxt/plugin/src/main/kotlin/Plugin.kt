@file:Suppress("PackageDirectoryMismatch", "unused")

package dev.oom_wg.purejoy.fyl.fytxt

import com.android.build.api.dsl.CommonExtension
import dev.oom_wg.purejoy.fyl.fytxt.plugin.BuildConfig
import org.gradle.api.*
import org.gradle.api.file.Directory
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import ren.shiror.fvv.FVVV
import java.io.File

abstract class FYTxtExtension {
	abstract val packageName: Property<String>
	abstract val objectName: Property<String>

	abstract val langSrcs: MapProperty<String, Directory>

	abstract val langAliases: MapProperty<String, String>
	abstract val defaultLang: Property<String>

	abstract val composeGen: Property<Boolean>

	abstract val internalClass: Property<Boolean>

	init {
		packageName.convention("dev.oom_wg.purejoy.fyl.fytxt")
		objectName.convention("FYTxt")
		internalClass.convention(true)
	}
}

class FYTxtPlugin : Plugin<Project> {
	override fun apply(project: Project) {
		project.extensions.create("fytxt", FYTxtExtension::class.java)
		project.pluginManager.withPlugin("com.android.application") { setup(project) }
		project.pluginManager.withPlugin("com.android.library") { setup(project) }
		project.pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") { setup(project) }
	}
}

const val suppress =
	"@file:Suppress(\"PackageDirectoryMismatch\", \"PackageName\", \"ClassName\", \"ObjectPropertyName\", \"PropertyName\", \"FunctionName\", \"NonAsciiCharacters\", \"RemoveRedundantBackticks\", \"REDUNDANT_ELSE_IN_WHEN\", \"UnusedExpression\", \"unused\")\n"

private fun setup(project: Project) {
	val kotlin =
		runCatching { project.extensions.findByType(KotlinMultiplatformExtension::class.java) }.getOrNull()
	val android = runCatching { project.extensions.findByType(CommonExtension::class.java) }.getOrNull()
		?.takeIf { kotlin == null }
	kotlin?.apply {
		sourceSets.commonMain.get()
			.dependencies { implementation("dev.oom-wg.purejoy.fyl.fytxt:common:${BuildConfig.VERSION}") }
	} ?: project.dependencies.add(
		JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME,
		"dev.oom-wg.purejoy.fyl.fytxt:common:${BuildConfig.VERSION}"
	)
	project.afterEvaluate {
		val ext = project.extensions.getByType(FYTxtExtension::class.java)

		hashSetOf(FYTxtExtension::langSrcs, FYTxtExtension::defaultLang).forEach {
			if (!it.get(ext).isPresent) throw GradleException("No '${it.name}' found.")
		}
		ext.defaultLang.set(ext.defaultLang.get().uppercase())

		if (ext.composeGen.getOrElse(false)) kotlin?.apply {
			sourceSets.commonMain.get()
				.dependencies { implementation("dev.oom-wg.purejoy.fyl.fytxt:compose:${BuildConfig.VERSION}") }
		} ?: project.dependencies.add(
			JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME,
			"dev.oom-wg.purejoy.fyl.fytxt:compose:${BuildConfig.VERSION}"
		)

		val srcRootDir = project.layout.buildDirectory.dir("generated/fytxt/kotlin").get().asFile
		val commonMainDir = File(srcRootDir, "commonMain/kotlin")
		kotlin?.apply { sourceSets.commonMain.get().kotlin.srcDir(commonMainDir) }
		android?.apply { sourceSets.getByName("main").java.directories.add(commonMainDir.path) }
		srcRootDir.apply { if (exists()) deleteRecursively() }
		commonMainDir.mkdirs()

		val langs = ext.langSrcs.get().map { (name, dir) ->
			name to dir.asFile.listFiles { file -> file.isDirectory }.associate { dir ->
				dir.name.uppercase() to FVVV().apply {
					dir.walkTopDown().filter { it.isFile && it.extension in setOf("fvv", "fw") }
						.forEach { parse(it.readText()) }
				}
			}
		}
		if (!langs.first().second.containsKey(ext.defaultLang.get())) throw GradleException("'defaultLang' not found.")

		val commonLang = langs.first()
		val variants = langs.drop(1)
		val langTags = commonLang.second.keys

		buildString {
			appendLine(suppress)
			appendLine("package ${ext.packageName.get()}\n")
			appendLine("import dev.oom_wg.purejoy.fyl.fytxt.FYTxtGroup")
			appendLine("import dev.oom_wg.purejoy.fyl.fytxt.FYTxtTag")
			appendLine("import dev.oom_wg.purejoy.fyl.fytxt.FYTxtConfig")
			if (ext.composeGen.getOrElse(false)) {
				appendLine("import dev.oom_wg.purejoy.fyl.fytxt.strfmt.fmt")
				appendLine()
				appendLine("import androidx.compose.runtime.Composable")
				appendLine("import androidx.compose.runtime.collectAsState")
				appendLine("import androidx.compose.runtime.remember")
			}
			appendLine()
			appendLine("${if (ext.internalClass.get()) "internal " else ""}object`${ext.objectName.get()}`{init{`${ext.objectName.get()}Groups`}")
			fun countTexts(node: FVVV): Int =
				if (node.`is`<String>()) 1 else node.nodes.values.sumOf { countTexts(it) }

			val totalTexts = countTexts(commonLang.second[ext.defaultLang.get()]!!).toDouble()
			appendLine(
				"${if (ext.internalClass.get()) "internal " else ""}enum class`${ext.objectName.get()}Groups`:FYTxtGroup{${
				langs.joinToString(",") { group ->
					"`${group.first}`{override val stats=mapOf(${
						group.second.map { (tag, node) ->
							"`${ext.objectName.get()}Tags`.$tag to ${countTexts(node) / totalTexts}"
						}.joinToString(",")
					})}"
				}
			};companion object{init{FYTxtConfig.init(`${commonLang.first}`,`${ext.objectName.get()}Tags`.entries)}}}")
			appendLine(
				"${if (ext.internalClass.get()) "internal " else ""}enum class`${ext.objectName.get()}Tags`:FYTxtTag{${
				commonLang.second.keys.joinToString(",") { tag ->
					"$tag{override val pattern=${
						ext.langAliases.orNull?.get(tag)?.let { "\"\"\"$it\"\"\".toRegex()" }
					}}"
				}
			}}\n")
			fun StringBuilder.runWrite(tgtNode: Map<String, FVVV>, path: List<String> = emptyList()) {
				tgtNode.forEach { (key, node) ->
					if (node.nodes.isEmpty()) {
						val idxVariants = langs.associate { (groupName, langMap) ->
							groupName to langMap.mapValues { (_, rootFVVV) -> path.fold(rootFVVV) { tgt, idx -> tgt[idx] }[key].`as`<String>() }
						}

						val commonTexts = idxVariants[commonLang.first]!!
						val naTags = langs.mapNotNull { (groupName, _) ->
							langTags.filter { tag -> idxVariants[groupName]!![tag] == null }
								.joinToString(", ").takeIf { it.isNotEmpty() }?.let { "$groupName: $it" }
						}.joinToString(" | ")
						if (naTags.isNotEmpty()) println(path.joinToString(".") + ".$key NA:  $naTags")
						val defaultText = commonTexts[ext.defaultLang.get()]!!
						val tip = defaultText.replace("\n", "\n*")

						appendLine("/**$tip")
						if (naTags.isNotEmpty()) appendLine("*@suppress $naTags")
						appendLine("*/")
						appendLine("val`$key`get()=FYTxtConfig.activeTags.value.firstNotNullOfOrNull{it as`${ext.objectName.get()}Tags`")
						if (variants.isNotEmpty()) {
							appendLine("when(FYTxtConfig.activeGroup.value as`${ext.objectName.get()}Groups`){")
							variants.forEach { (variantName, _) ->
								appendLine("`${ext.objectName.get()}Groups`.`$variantName`->when(it){")
								idxVariants[variantName]!!.forEach { (tag, value) ->
									if (value != null) appendLine("`${ext.objectName.get()}Tags`.$tag->\"\"\"$value\"\"\"")
								}
								appendLine("else->null}")
							}
							appendLine("else->null}?:")
						}
						appendLine("when(it){")
						commonTexts.forEach { (tag, value) -> if (value != null) appendLine("`${ext.objectName.get()}Tags`.$tag->\"\"\"$value\"\"\"") }
						appendLine("else -> null}")
						appendLine("}?:\"\"\"${defaultText}\"\"\"")
						if (ext.composeGen.getOrElse(false)) {
							appendLine("/**$tip")
							if (naTags.isNotEmpty()) appendLine("*@suppress $naTags")
							appendLine("*/")
							appendLine("@Composable")
							appendLine("fun`$key`(vararg args:Any?)=FYTxtConfig.observe{`$key`.fmt(args)}")
						}
					} else {
						appendLine("object`$key`{init{`${ext.objectName.get()}Groups`}")
						runWrite(node.nodes, path + key)
						appendLine("}")
					}
				}
			}
			runWrite(commonLang.second[ext.defaultLang.get()]!!.nodes)
			append("}")
		}.let { File(commonMainDir, "fytxt.kt").writeText(it) }
	}
}