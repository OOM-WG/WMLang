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

@Suppress("UNCHECKED_CAST") val versionDetails = extra["versionDetails"] as Closure<VersionDetails>
group = "ren.shiror.fyl.fytxt"
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
	website = "https://shirosu.gal.tf/fyl"
	vcsUrl = "https://github.com/OOM-WG/ShiroSU-FYL.git"
	plugins {
		create("FYTxt") {
			id = "ren.shiror.fyl.fytxt"
			implementationClass = "tf.gal.shirosu.fyl.fytxt.FYTxtPlugin"
			displayName = "ShiroSU FYL - FYTxt Gradle Plugin"
			description = "Kotlin Multi Language Framework"
		}
	}
}

publishing { repositories { mavenLocal() } }