@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	id("com.android.kotlin.multiplatform.library")
	kotlin("multiplatform")
	kotlin("plugin.compose")
	`maven-publish`
	id("com.palantir.git-version")
}

kotlin {
	applyDefaultHierarchyTemplate()
	withSourcesJar()

	androidLibrary {
		namespace = "dev.oom_wg.purejoy.fyl.fytxt.systxt"
		compileSdk = 36
		minSdk = 1
		buildToolsVersion = "36.1.0"

		compilerOptions.jvmTarget = JvmTarget.JVM_1_8

		optimization {
			consumerKeepRules.publish = true
			consumerKeepRules.files("consumer-rules.pro")
			minify = false
		}
	}

	// noinspection GradleDynamicVersion
	sourceSets.androidMain.dependencies {
		implementation(project.dependencies.platform("androidx.compose:compose-bom:+"))
		implementation("androidx.compose.ui:ui")
		implementation("androidx.compose.runtime:runtime")
	}
}

afterEvaluate {
	publishing {
		publications { withType<MavenPublication>(configurePublishConfig("for Android System Text")) }
		repositories { mavenLocal() }
	}
}