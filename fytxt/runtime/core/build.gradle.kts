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

	android {
		namespace = "dev.oom_wg.purejoy.fyl.fytxt.core"
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

	jvm { compilerOptions.jvmTarget = JvmTarget.JVM_1_8 }

	androidNativeArm64()
	androidNativeArm32()
	androidNativeX64()
	androidNativeX86()

	iosArm64()
	iosSimulatorArm64()
	macosArm64()
	tvosArm64()
	tvosSimulatorArm64()
	watchosArm64()
	watchosArm32()
	watchosDeviceArm64()
	watchosSimulatorArm64()

	linuxArm64()
	linuxX64()

	mingwX64()

	js(IR) { browser() }
	wasmJs { browser() }

	sourceSets {
		commonMain.dependencies {
			implementation(libs.kotlinxCoroutines)
		}
		webMain.dependencies {
			implementation(libs.kotlinxBrowser)
		}
	}
}

afterEvaluate {
	publishing {
		publications { withType<MavenPublication>(configurePublishConfig()) }
		repositories { mavenLocal() }
	}
}