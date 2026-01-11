@file:OptIn(ExperimentalWasmDsl::class) @file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	id("com.android.kotlin.multiplatform.library")
	kotlin("multiplatform")
	kotlin("plugin.compose")
	id("org.jetbrains.compose")
	`maven-publish`
	id("com.palantir.git-version")
}

kotlin {
	applyDefaultHierarchyTemplate()
	withSourcesJar()

	androidLibrary {
		namespace = "dev.oom_wg.purejoy.fyl.fytxt.compose"
		compileSdk = gropify.config.compileSdk
		minSdk = gropify.config.minSdk
		buildToolsVersion = gropify.config.buildToolsVersion

		compilerOptions.jvmTarget = JvmTarget.JVM_1_8

		optimization {
			consumerKeepRules.publish = true
			consumerKeepRules.files("consumer-rules.pro")
			minify = false
		}
	}

	jvm { compilerOptions.jvmTarget = JvmTarget.JVM_1_8 }

	iosArm64()
	iosX64()
	iosSimulatorArm64()

	js(IR) { browser() }
	wasmJs { browser() }

	// noinspection GradleDynamicVersion
	sourceSets {
		commonMain.dependencies {
			implementation(project(":fytxt:runtime:common"))
			implementation("org.jetbrains.compose.ui:ui:+")
			implementation("org.jetbrains.compose.runtime:runtime:+")
		}
		androidMain.dependencies {
			implementation("com.highcapable.pangutext:pangutext-android:+")
		}
	}
}

afterEvaluate {
	publishing {
		publications { withType<MavenPublication>(configurePublishConfig("for Compose")) }
		repositories { mavenLocal() }
	}
}