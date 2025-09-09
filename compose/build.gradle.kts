import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	id("com.android.library")
	kotlin("android")
	kotlin("plugin.compose")
	`maven-publish`
}

kotlin.compilerOptions.jvmTarget = JvmTarget.JVM_1_8

android {
	namespace = "dev.oom_wg.purejoy.mlang.compose"
	compileSdk = 36

	defaultConfig {
		minSdk = 16
		consumerProguardFiles("consumer-rules.pro")
	}
	sourceSets.getByName("main").java.srcDir("src/main/kotlin")
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

	publishing {
		singleVariant("release") {
			withSourcesJar()
			withJavadocJar()
		}
	}
}

// noinspection GradleDynamicVersion
dependencies {
	implementation(project(":base"))

	implementation(platform("androidx.compose:compose-bom-alpha:+"))
	implementation("androidx.compose.ui:ui")
	implementation("androidx.compose.runtime:runtime")

	implementation("com.highcapable.pangutext:pangutext-android:+")
}

afterEvaluate {
	publishing {
		publications {
			create<MavenPublication>(
				"release", configurePublishConfig("compose", "for Jetpack Compose")
			)
		}
		repositories {
			mavenLocal()
		}
	}
}