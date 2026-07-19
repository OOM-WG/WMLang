import com.palantir.gradle.gitversion.VersionDetails
import groovy.lang.Closure
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.*

fun Project.configurePublishConfig(
	groupName: String,
	projectName: String,
	desc: String,
	nameExt: String = ""
): MavenPublication.() -> Unit = {
	groupId = "ren.shiror.fyl.$groupName"
	@Suppress("UNCHECKED_CAST") val versionDetails = extra["versionDetails"] as Closure<VersionDetails>
	version = versionDetails().lastTag ?: "0.0"

	pom {
		name = "ShiroSU FYL - $projectName${if (nameExt.isNotEmpty()) " $nameExt" else ""}"
		description = desc
		url = "https://shirosu.gal.tf/fyl"

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
			connection = "scm:git:https://github.com/OOM-WG/ShiroSU-FYL.git"
			developerConnection = "scm:git:https://github.com/OOM-WG/ShiroSU-FYL.git"
			url = "https://github.com/OOM-WG/ShiroSU-FYL.git"
		}
	}

	when (name) {
		"androidRelease" -> "releaseRuntimeClasspath"
		else             -> listOf(
			"${name}RuntimeClasspath", "${name}CompileKlibraries"
		).firstOrNull { project.configurations.findByName(it) != null }
	}?.let { versionMapping { allVariants { fromResolutionOf(it) } } }
}

fun Project.configureFYTxtPublishConfig(nameExt: String = "") = configurePublishConfig(
	"fytxt",
	"FYTxt",
	"Kotlin Multi Language Framework${if (nameExt.isNotEmpty()) " $nameExt" else ""}",
	nameExt
)