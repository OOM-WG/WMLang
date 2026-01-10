@file:Suppress("UnstableApiUsage")

import com.palantir.gradle.gitversion.VersionDetails
import groovy.lang.Closure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	kotlin("jvm")
	`java-gradle-plugin`
	`maven-publish`
	id("com.palantir.git-version")
	id("com.github.gmazzo.buildconfig") version "+"
}

group = "dev.oom-wg.purejoy.fyl.fytxt"
val versionDetails: Closure<VersionDetails> by extra
version = versionDetails().lastTag ?: "0.0"

buildConfig {
	buildConfigField("VERSION", provider { "${project.version}" })
}

java {
	sourceCompatibility = JavaVersion.VERSION_11
	targetCompatibility = JavaVersion.VERSION_11
}

kotlin.compilerOptions.jvmTarget = JvmTarget.JVM_11

// noinspection GradleDynamicVersion
dependencies {
	implementation("ren.shiror.fvv:core:2.+")

	compileOnly(gradleApi())
	compileOnly(localGroovy())

	compileOnly("com.android.tools.build:gradle:+")
	compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:+")
}

gradlePlugin {
	website = "https://github.com/OOM-WG/PureJoy-FYL"
	vcsUrl = "https://github.com/OOM-WG/PureJoy-FYL"
	plugins {
		create("MLang") {
			id = "dev.oom-wg.purejoy.fyl.fytxt"
			implementationClass = "dev.oom_wg.purejoy.fyl.fytxt.FYTxtPlugin"
			displayName = "PureJoy FYTxt Gradle Plugin"
			description = "Kotlin Multi Language Framework"
		}
	}
}

publishing {
	repositories { mavenLocal() }
}