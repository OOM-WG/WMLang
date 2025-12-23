import com.palantir.gradle.gitversion.VersionDetails
import groovy.lang.Closure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	id("com.android.library")
	kotlin("android")
	kotlin("plugin.compose")
	`maven-publish`
	id("com.palantir.git-version")
}

kotlin.compilerOptions.jvmTarget = JvmTarget.JVM_1_8

android {
	namespace = "dev.oom_wg.purejoy.mlang.systxt"
	compileSdk = 36
	buildToolsVersion = "36.1.0"

	defaultConfig {
		minSdk = 16
		consumerProguardFiles("consumer-rules.pro")
	}
	buildTypes {
		release {
			isMinifyEnabled = false
			proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_1_8
		targetCompatibility = JavaVersion.VERSION_1_8
	}
	buildFeatures {
		buildConfig = true
		compose = true
	}

	publishing {
		singleVariant("release") {
			withSourcesJar()
			// withJavadocJar()
		}
	}
}

// noinspection GradleDynamicVersion
dependencies {
	implementation(platform("androidx.compose:compose-bom-alpha:+"))
	implementation("androidx.compose.ui:ui")
	implementation("androidx.compose.runtime:runtime")
}

afterEvaluate {
	publishing {
		publications {
			val versionDetails: Closure<VersionDetails> by extra
			create<MavenPublication>(
				"release", configurePublishConfig(versionDetails().lastTag, "systxt", "for System Text")
			)
		}
		repositories {
			mavenLocal()
		}
	}
}