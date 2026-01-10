import com.palantir.gradle.gitversion.VersionDetails
import groovy.lang.Closure
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.*

fun Project.configurePublishConfig(nameExt: String = ""): MavenPublication.() -> Unit =
	{
		groupId = "dev.oom-wg.purejoy.fyl.fytxt"
		val versionDetails: Closure<VersionDetails> by extra
		version = versionDetails().lastTag ?: "0.0"

		pom {
			name.set("PureJoy FYTxt${if (nameExt.isNotEmpty()) " $nameExt" else ""}")
			description.set("Kotlin Multi Language Framework${if (nameExt.isNotEmpty()) " $nameExt" else ""}")
			url.set("https://github.com/OOM-WG/PureJoy-FYL")

			licenses {
				license {
					name.set("F2DLPRL")
					url.set("https://license.fileto.download/LICENSE.txt")
					distribution.set("repo")
				}
			}

			developers {
				developer {
					id.set("oom-wg")
					name.set("OOM WG")
					email.set("oom@200ok.work")
					url.set("https://oom-wg.dev")
				}
			}

			organization {
				name.set("OOM WG")
				url.set("https://oom-wg.dev")
			}

			scm {
				connection.set("scm:git:https://github.com/OOM-WG/PureJoy-FYL.git")
				developerConnection.set("scm:git:https://github.com/OOM-WG/PureJoy-FYL.git")
				url.set("https://github.com/OOM-WG/PureJoy-FYL.git")
			}
		}

		when (name) {
			"androidRelease" -> "releaseRuntimeClasspath"
			else             -> listOf(
				"${name}RuntimeClasspath", "${name}CompileKlibraries"
			).firstOrNull { project.configurations.findByName(it) != null }
		}?.let {
			versionMapping { allVariants { fromResolutionOf(it) } }
		}
	}