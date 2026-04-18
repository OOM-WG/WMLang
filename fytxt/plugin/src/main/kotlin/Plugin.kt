@file:Suppress("UnstableApiUsage")

package dev.oom_wg.purejoy.fyl.fytxt

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.AndroidComponentsExtension
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import dev.oom_wg.purejoy.fyl.fytxt.plugin.BuildConfig
import org.gradle.api.*
import org.gradle.api.file.*
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import ren.shiror.fvv.FVVV
import java.io.File

const val fytxtGrp = "dev.oom-wg.purejoy.fyl.fytxt"
const val fytxtPkg = "dev.oom_wg.purejoy.fyl.fytxt"

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
		composeGen.convention(false)
		internalClass.convention(true)
	}
}

@Suppress("unused")
class FYTxtPlugin : Plugin<Project> {
	override fun apply(project: Project) {
		project.extensions.create("fytxt", FYTxtExtension::class.java)
		project.pluginManager.withPlugin("com.android.application") { setup(project) }
		project.pluginManager.withPlugin("com.android.library") { setup(project) }
		project.pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") { setup(project) }
	}

	private fun setup(project: Project) {
		val ext = project.extensions.getByType(FYTxtExtension::class.java)

		val kotlin = runCatching { project.extensions.findByType(KotlinMultiplatformExtension::class.java) }.getOrNull()
		val android =
			runCatching { project.extensions.findByType(CommonExtension::class.java) }.getOrNull()?.takeIf { kotlin == null }

		val bom = project.dependencies.platform("$fytxtGrp:bom:${BuildConfig.VERSION}")
		val core = "$fytxtGrp:core"
		val compose = "$fytxtGrp:compose"

		kotlin?.apply {
			sourceSets.commonMain.get().dependencies {
				implementation(bom)
				implementation(core)
			}
		} ?: project.dependencies.apply {
			add(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME, bom)
			add(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME, core)
		}

		val genTask = project.tasks.register("generateFYTxt", GenerateFYTxtTask::class.java) { task ->
			task.packageName.set(ext.packageName)
			task.objectName.set(ext.objectName)
			task.langSrcRoots.set(ext.langSrcs.map { map -> map.mapValues { (_, dir) -> dir.asFile.absolutePath } })
			task.langSrcFiles.from(ext.langSrcs.map { map ->
				map.values.map { dir ->
					project.fileTree(dir) { tree -> tree.include("**/*.fvv", "**/*.fw", "**/*.fyl") }
				}
			})
			task.langAliases.set(ext.langAliases)
			task.defaultLang.set(ext.defaultLang.map { it.uppercase() })
			task.composeGen.set(ext.composeGen)
			task.internalClass.set(ext.internalClass)

			task.outputDir.set(project.layout.buildDirectory.dir("generated/fytxt/kotlin"))
		}

		kotlin?.apply { sourceSets.commonMain.get().kotlin.srcDir(genTask) }
		if (android != null) project.extensions.configure(AndroidComponentsExtension::class.java) { ext ->
			ext.onVariants { variant ->
				variant.sources.java?.addGeneratedSourceDirectory(genTask, GenerateFYTxtTask::outputDir)
			}
		}

		project.tasks.matching { it.name == "prepareKotlinIdeaImport" }.configureEach { task ->
			task.dependsOn(genTask)
		}
		project.afterEvaluate {
			if (ext.composeGen.getOrElse(false)) kotlin?.apply {
				sourceSets.commonMain.get().dependencies { implementation(compose) }
			} ?: project.dependencies.add(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME, compose)
		}
	}
}

@CacheableTask
abstract class GenerateFYTxtTask : DefaultTask() {
	@get:Input abstract val packageName: Property<String>
	@get:Input abstract val objectName: Property<String>

	@get:Input abstract val langSrcRoots: MapProperty<String, String>
	@get:InputFiles @get:PathSensitive(PathSensitivity.RELATIVE) abstract val langSrcFiles: ConfigurableFileCollection

	@get:Input abstract val langAliases: MapProperty<String, String>
	@get:Input abstract val defaultLang: Property<String>

	@get:Input abstract val composeGen: Property<Boolean>

	@get:Input abstract val internalClass: Property<Boolean>

	@get:OutputDirectory abstract val outputDir: DirectoryProperty

	private val suppress = arrayOf(
		"PackageName",
		"ClassName",
		"ObjectPropertyName",
		"PropertyName",
		"FunctionName",
		"NonAsciiCharacters",
		"RemoveRedundantBackticks",
		"RemoveRedundantQualifierName",
		"REDUNDANT_ELSE_IN_WHEN",
		"RedundantNullableReturnType",
		"RedundantVisibilityModifier",
		"UnusedExpression",
		"unused"
	)

	private val fytxtGroup = ClassName(fytxtPkg, "FYTxtGroup")
	private val fytxtTag = ClassName(fytxtPkg, "FYTxtTag")
	private val fytxtConfig = ClassName(fytxtPkg, "FYTxtConfig")
	private val fytxtObserve = MemberName(
		"$fytxtPkg.compose",
		"observe",
		isExtension = true,
	)
	private val fytxtFmt = MemberName(
		"$fytxtPkg.strfmt",
		"fmt",
		isExtension = true,
	)
	private val composeComposable = ClassName("androidx.compose.runtime", "Composable")

	@TaskAction
	fun generate() {
		val pkgName = packageName.get()
		val objName = objectName.get()
		val langAliases = langAliases.orNull ?: emptyMap()
		val dfltLang = defaultLang.get()
		val composeGen = composeGen.getOrElse(false)
		val internalCls = internalClass.get()

		val langs = langSrcRoots.get().map { (name, path) ->
			name to File(path).listFiles { file -> file.isDirectory }.orEmpty().sortedBy { it.name }.associate { dir ->
				dir.name.uppercase() to FVVV().apply {
					dir.walkTopDown().filter { it.isFile && it.extension in setOf("fvv", "fw", "fyl") }
						.sortedBy { it.relativeTo(dir).invariantSeparatorsPath }.forEach { parse(it.readText()) }
				}
			}
		}
		if (langs.isEmpty()) throw GradleException("No 'langSrcs' found.")

		val commonLang = langs.first()
		val variants = langs.drop(1)
		val langTags = commonLang.second.keys
		if (!commonLang.second.containsKey(dfltLang)) throw GradleException("'defaultLang' not found.")
		val defaultRoot = commonLang.second[dfltLang]!!

		val groupsType = ClassName(pkgName, "${objName}Groups")
		val tagsType = ClassName(pkgName, "${objName}Tags")

		val builder = FileSpec.builder(pkgName, "fytxt").indent("\t")

		run {
			AnnotationSpec.builder(Suppress::class).useSiteTarget(AnnotationSpec.UseSiteTarget.FILE)
				.addMember(suppress.joinToString(", ") { "%S" }, *suppress).build()
		}.apply { builder.addAnnotation(this) }

		run {
			val totalTexts = countTexts(defaultRoot).toDouble()
			val statsType = Map::class.asClassName().parameterizedBy(fytxtTag, DOUBLE)

			TypeSpec.enumBuilder(groupsType.simpleName).apply { if (internalCls) addModifiers(KModifier.INTERNAL) }
				.addSuperinterface(fytxtGroup).apply {
					langs.forEach { (groupName, langMap) ->
						val mapCode = CodeBlock.builder().add("mapOf(\n").indent()
						langMap.forEach { (tag, node) ->
							mapCode.add("%T.%L to %L,\n", tagsType, tag, countTexts(node) / totalTexts)
						}
						mapCode.unindent().add(")")
						addEnumConstant(
							groupName, TypeSpec.anonymousClassBuilder().addProperty(
								PropertySpec.builder("stats", statsType, KModifier.OVERRIDE).initializer(mapCode.build())
									.build()
							).build()
						)
					}
				}.addType(
					TypeSpec.companionObjectBuilder().addInitializerBlock(
						CodeBlock.builder()
							.addStatement("%T.init(%T.%N, %T.entries)", fytxtConfig, groupsType, commonLang.first, tagsType)
							.build()
					).build()
				).build()
		}.apply { builder.addType(this) }

		run {
			val regexType = Regex::class.asClassName().copy(nullable = true)

			TypeSpec.enumBuilder(tagsType.simpleName).apply { if (internalCls) addModifiers(KModifier.INTERNAL) }
				.addSuperinterface(fytxtTag).apply {
					langTags.toList().forEach { tag ->
						addEnumConstant(
							tag,
							TypeSpec.anonymousClassBuilder().addProperty(
								PropertySpec.builder("pattern", regexType, KModifier.OVERRIDE)
									.initializer(langAliases[tag]?.let { CodeBlock.of("%S.toRegex()", it) }
										?: CodeBlock.of("null")).build()).build())
					}
				}.build()
		}.apply { builder.addType(this) }

		run {
			fun buildGetterExpr(
				commonTexts: Map<String, String?>,
				variantTexts: Map<String, Map<String, String?>>,
				defaultText: String,
			) = CodeBlock.builder().apply {
				add("%T.activeTags.value.firstNotNullOfOrNull {\n", fytxtConfig)
				indent()
				addStatement("it as %T", tagsType)
				if (variantTexts.isNotEmpty()) {
					beginControlFlow("when (%T.activeGroup.value as %T)", fytxtConfig, groupsType)
					variantTexts.forEach { (variantName, texts) ->
						beginControlFlow("%T.%N -> when (it)", groupsType, variantName)
						texts.forEach { (tag, value) ->
							if (value != null) addStatement("%T.%L -> %S", tagsType, tag, value)
						}
						addStatement("else -> null")
						endControlFlow()
					}
					add("else -> null\n")
					endControlFlow()
					add("?: ")
				}
				beginControlFlow("when (it)")
				commonTexts.forEach { (tag, value) ->
					if (value != null) addStatement("%T.%L -> %S", tagsType, tag, value)
				}
				addStatement("else -> null")
				endControlFlow()
				unindent()
				add("} ?: %S", defaultText)
			}.build()

			fun TypeSpec.Builder.addNodes(
				node: Map<String, FVVV>,
				path: List<String> = emptyList(),
			): Unit = node.forEach { (key, node) ->
				if (node.nodes.isEmpty()) {
					val idxVariants = langs.associate { (groupName, langMap) ->
						groupName to langMap.mapValues { (_, rootFVVV) -> path.fold(rootFVVV) { tgt, idx -> tgt[idx] }[key].`as`<String>() }
					}
					val commonTexts = idxVariants[commonLang.first]!!
					val naTags = idxVariants.mapNotNull { (groupName, texts) ->
						langTags.filter { tag -> texts[tag] == null }.joinToString(", ").takeIf { it.isNotEmpty() }
							?.let { "$groupName: $it" }
					}.joinToString(" | ")
					if (naTags.isNotEmpty()) logger.warn("${(path + key).joinToString(".")} NA: $naTags")

					val defaultText = commonTexts[dfltLang]!!
					val tip = buildString {
						append(buildKdoc(defaultText))
						if (naTags.isNotEmpty()) append("\n@suppress $naTags")
					}

					val getter = PropertySpec.builder(key, STRING).addKdoc("%L", tip).getter(
						FunSpec.getterBuilder().addStatement(
							"return %L", buildGetterExpr(
								commonTexts, variants.associate { it.first to idxVariants[it.first]!! }, defaultText
							)
						).build()
					).build()
					addProperty(getter)
					if (composeGen) addFunction(
						FunSpec.builder(key).addKdoc("%L", tip).addAnnotation(composeComposable).addParameter(
							ParameterSpec.builder("args", ANY.copy(nullable = true)).addModifiers(KModifier.VARARG).build()
						).returns(STRING)
							.addStatement("return %T.%M { %N.%M(args) }", fytxtConfig, fytxtObserve, getter, fytxtFmt).build()
					)
				} else {
					addType(
						TypeSpec.objectBuilder(key).addInitializerBlock(
							CodeBlock.builder().addStatement("%T", groupsType).build()
						).apply { addNodes(node.nodes, path + key) }.build()
					)
				}
			}

			TypeSpec.objectBuilder(objName).apply { if (internalCls) addModifiers(KModifier.INTERNAL) }.addInitializerBlock(
				CodeBlock.builder().addStatement("%T", groupsType).build()
			).apply { addNodes(defaultRoot.nodes) }.build()
		}.apply { builder.addType(this) }

		outputDir.get().asFile.apply {
			if (exists()) deleteRecursively()
			mkdirs()
			builder.build().writeTo(this)
		}
	}

	private fun countTexts(node: FVVV): Int = if (node.`is`<String>()) 1 else node.nodes.values.sumOf { countTexts(it) }
	private fun buildKdoc(txt: String) = txt.replace("*/", "* /")
}