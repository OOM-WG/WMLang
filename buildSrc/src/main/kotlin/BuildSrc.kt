import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.get

fun Project.configurePublishConfig(
	moduleVersion: String?, moduleId: String, nameExt: String = ""
): MavenPublication.() -> Unit = {
	from(components["release"])
	groupId = "dev.oom-wg.purejoy.mlang"
	artifactId = moduleId
	version = moduleVersion ?: "0.1"

	pom {
		name.set("PureJoy MultiLang${if (nameExt.isNotEmpty()) " $nameExt" else ""}")
		description.set("Android Multi Language Framework${if (nameExt.isNotEmpty()) " $nameExt" else ""}")
		url.set("https://github.com/OOM-WG/PureJoy-MultiLang")

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
				name.set("O.O.M. W.G.")
				email.set("oom@200ok.work")
				url.set("https://oom-wg.dev")
			}
		}

		organization {
			name.set("O.O.M. W.G.")
			url.set("https://oom-wg.dev")
		}

		scm {
			connection.set("scm:git:https://github.com/OOM-WG/PureJoy-MultiLang.git")
			developerConnection.set("scm:git:https://github.com/OOM-WG/PureJoy-MultiLang.git")
			url.set("https://github.com/OOM-WG/PureJoy-MultiLang.git")
		}
	}
}