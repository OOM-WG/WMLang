import com.palantir.gradle.gitversion.VersionDetails
import groovy.lang.Closure
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.*

fun Project.configurePublishConfig(nameExt: String = ""): MavenPublication.() -> Unit = {
	groupId = "dev.oom-wg.purejoy.fyl.fytxt"
	val versionDetails: Closure<VersionDetails> by extra
	version = versionDetails().lastTag ?: "0.0"

	pom {
		name = "PureJoy FYTxt${if (nameExt.isNotEmpty()) " $nameExt" else ""}"
		description = "Kotlin Multi Language Framework${if (nameExt.isNotEmpty()) " $nameExt" else ""}"
		url = "https://app.niggergo.work/purejoy/fytxt/"

		licenses {
			license {
				name = "File-to-Downloader"
				url = "https://license.fileto.download/LICENSE.txt"
				distribution = "repo"
			}
		}

		developers {
			developer {
				id = "oom-wg"
				name = "OOM WG"
				email = "oom@200ok.work"
				url = "https://oom-wg.dev"
			}
		}

		organization {
			name = "OOM WG"
			url = "https://oom-wg.dev"
		}

		scm {
			connection = "scm:git:https://github.com/OOM-WG/PureJoy-FYL.git"
			developerConnection = "scm:git:https://github.com/OOM-WG/PureJoy-FYL.git"
			url = "https://github.com/OOM-WG/PureJoy-FYL.git"
		}
	}

	when (name) {
		"androidRelease" -> "releaseRuntimeClasspath"
		else             -> listOf(
			"${name}RuntimeClasspath", "${name}CompileKlibraries"
		).firstOrNull { project.configurations.findByName(it) != null }
	}?.let { versionMapping { allVariants { fromResolutionOf(it) } } }
}