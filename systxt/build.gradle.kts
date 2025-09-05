import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	id("com.android.library")
	kotlin("android")
	kotlin("plugin.compose")
	id("maven-publish")
}

kotlin.compilerOptions.jvmTarget = JvmTarget.JVM_24

android {
	namespace = "dev.oom_wg.purejoy.mlang.systxt"
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
		sourceCompatibility = JavaVersion.VERSION_24
		targetCompatibility = JavaVersion.VERSION_24
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
			create<MavenPublication>("release") {
				from(components["release"])
				groupId = "dev.oom-wg.purejoy.mlang"
				artifactId = "systxt"
				version = "main"

				pom {
					name.set("PureJoy MultiLang for System Text")
					description.set("Android Multi Language Framework for System Text")
					url.set("https://github.com/OOM-WG/PureJoy-MultiLang")

					licenses {
						license {
							name.set("F2DLPRL")
							url.set("https://license.fileto.download/LICENSE.txt")
							distribution.set("repo")
						}
					}

					developers {
						developer {
							id.set("oom-wg")
							name.set("O.O.M. W.G.")
							email.set("oom@200ok.work")
							url.set("https://oom-wg.dev")
						}
					}

					organization {
						name.set("O.O.M. W.G.")
						url.set("https://oom-wg.dev")
					}

					scm {
						connection.set("scm:git:https://github.com/OOM-WG/PureJoy-MultiLang.git")
						developerConnection.set("scm:git:https://github.com/OOM-WG/PureJoy-MultiLang.git")
						url.set("https://github.com/OOM-WG/PureJoy-MultiLang.git")
					}
				}
			}
		}
		repositories {
			mavenLocal()
		}
	}
}