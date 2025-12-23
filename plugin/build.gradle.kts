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

group = "dev.oom-wg.purejoy.mlang"
val versionDetails: Closure<VersionDetails> by extra
version = versionDetails().lastTag ?: "0.1"

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
	implementation("dev.oom-wg.FVV:FVV:1.+")

	compileOnly(gradleApi())
	compileOnly(localGroovy())

	// compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:+")
	compileOnly("com.android.tools.build:gradle:+")
}

gradlePlugin {
	website = "https://github.com/OOM-WG/PureJoy-MultiLang"
	vcsUrl = "https://github.com/OOM-WG/PureJoy-MultiLang"
	plugins {
		create("MLang") {
			id = "dev.oom-wg.purejoy.mlang"
			implementationClass = "dev.oom_wg.purejoy.mlang.MLangPlugin"
			displayName = "PureJoy MultiLang Gradle Plugin"
			description = "Android Multi Language Framework"
		}
	}
}

publishing {
	repositories {
		mavenLocal()
	}
}