import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	id("com.android.library")
	kotlin("android")
	`maven-publish`
}

kotlin.compilerOptions.jvmTarget = JvmTarget.JVM_1_8

android {
	namespace = "dev.oom_wg.purejoy.mlang.base"
	compileSdk = 36

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
	}

	publishing {
		singleVariant("release") {
			withSourcesJar()
			withJavadocJar()
		}
	}
}
afterEvaluate {
	publishing {
		publications {
			create<MavenPublication>("release", configurePublishConfig("base"))
		}
		repositories {
			mavenLocal()
		}
	}
}