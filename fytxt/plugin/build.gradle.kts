import com.palantir.gradle.gitversion.VersionDetails
import groovy.lang.Closure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	kotlin("jvm")
	`java-gradle-plugin`
	`maven-publish`
	id("com.palantir.git-version")
	alias(libs.plugins.buildconfig)
}

val versionDetails: Closure<VersionDetails> by extra
group = "dev.oom_wg.purejoy.fyl.fytxt"
version = versionDetails().lastTag

buildConfig {
	buildConfigField("VERSION", provider { "${project.version}" })
}

java {
	sourceCompatibility = JavaVersion.VERSION_11
	targetCompatibility = JavaVersion.VERSION_11
}

kotlin.compilerOptions.jvmTarget = JvmTarget.JVM_11

dependencies {
	implementation(libs.fvv)
	implementation(libs.kotlinpoet)

	compileOnly(gradleApi())
	compileOnly(localGroovy())

	compileOnly(libs.androidGradle)
	compileOnly(libs.kotlinGradle)
}

gradlePlugin {
	website = "https://github.com/OOM-WG/PureJoy-FYL"
	vcsUrl = "https://github.com/OOM-WG/PureJoy-FYL"
	plugins {
		create("FYTxt") {
			id = "dev.oom-wg.purejoy.fyl.fytxt"
			implementationClass = "dev.oom_wg.purejoy.fyl.fytxt.FYTxtPlugin"
			displayName = "PureJoy FYTxt Gradle Plugin"
			description = "Kotlin Multi Language Framework"
		}
	}
}

publishing { repositories { mavenLocal() } }