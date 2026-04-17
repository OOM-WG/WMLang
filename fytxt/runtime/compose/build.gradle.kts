@file:OptIn(ExperimentalWasmDsl::class) @file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
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

	android {
		namespace = "dev.oom_wg.purejoy.fyl.fytxt.compose"
		compileSdk = libs.versions.compileSdk.get().toInt()
		minSdk = 16
		buildToolsVersion = libs.versions.buildTools.get()

		compilerOptions.jvmTarget = JvmTarget.JVM_1_8

		optimization {
			consumerKeepRules.publish = true
			consumerKeepRules.files("consumer-rules.pro")
			minify = false
		}
	}

	jvm { compilerOptions.jvmTarget = JvmTarget.JVM_11 }

	iosArm64()
	iosSimulatorArm64()

	js(IR) { browser() }
	wasmJs { browser() }

	sourceSets {
		commonMain.dependencies {
			implementation(project(":fytxt:runtime:core"))
			implementation(libs.composeUi)
			implementation(libs.composeRuntime)
		}
		androidMain.dependencies {
			implementation(libs.pangutextAndroid)
		}
	}
}

afterEvaluate {
	publishing {
		publications { withType<MavenPublication>(configurePublishConfig("for Compose")) }
		repositories { mavenLocal() }
	}
}