import com.palantir.gradle.gitversion.VersionDetails
import groovy.lang.Closure

plugins {
	`java-platform`
	`maven-publish`
	id("com.palantir.git-version")
}

val versionDetails: Closure<VersionDetails> by extra
group = "dev.oom_wg.purejoy.fyl.fytxt"
version = versionDetails().lastTag

javaPlatform { allowDependencies() }

dependencies {
	constraints {
		api(project(":fytxt:runtime:core"))
		api(project(":fytxt:runtime:compose"))
		api(project(":fytxt:runtime:systxt"))
	}
}

afterEvaluate {
	publishing {
		publications {
			create<MavenPublication>("maven") {
				from(components["javaPlatform"])
				configurePublishConfig("BOM")()
			}
		}
		repositories { mavenLocal() }
	}
}