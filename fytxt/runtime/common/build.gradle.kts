@file:OptIn(ExperimentalWasmDsl::class) @file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	id("com.android.kotlin.multiplatform.library")
	kotlin("multiplatform")
	`maven-publish`
	id("com.palantir.git-version")
}

kotlin {
	applyDefaultHierarchyTemplate()
	withSourcesJar()

	androidLibrary {
		namespace = "dev.oom_wg.purejoy.fyl.fytxt.common"
		compileSdk = 36
		minSdk = 16
		buildToolsVersion = "36.1.0"

		compilerOptions.jvmTarget = JvmTarget.JVM_1_8

		optimization {
			consumerKeepRules.publish = true
			consumerKeepRules.files("consumer-rules.pro")
			minify = false
		}
	}

	jvm { compilerOptions.jvmTarget = JvmTarget.JVM_1_8 }

	androidNativeArm64()
	androidNativeArm32()
	androidNativeX64()
	androidNativeX86()

	iosArm64()
	iosX64()
	iosSimulatorArm64()
	macosArm64()
	macosX64()
	tvosArm64()
	tvosX64()
	tvosSimulatorArm64()
	watchosArm64()
	watchosArm32()
	watchosX64()
	watchosDeviceArm64()
	watchosSimulatorArm64()

	linuxArm64()
	linuxX64()

	mingwX64()

	js(IR) { browser() }
	wasmJs { browser() }

	// noinspection GradleDynamicVersion
	sourceSets {
		commonMain.dependencies {
			implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:+")
		}
		webMain.dependencies {
			implementation("org.jetbrains.kotlinx:kotlinx-browser:+")
		}
	}
}

afterEvaluate {
	publishing {
		publications { withType<MavenPublication>(configurePublishConfig()) }
		repositories { mavenLocal() }
	}
}