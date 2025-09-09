import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	kotlin("jvm")
	`java-gradle-plugin`
	`maven-publish`
}

group = "dev.oom-wg.purejoy.mlang"
version = "-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_11
	targetCompatibility = JavaVersion.VERSION_11
}

kotlin.compilerOptions.jvmTarget = JvmTarget.JVM_11

sourceSets {
	named("main") {
		java.srcDirs("src/main/kotlin", "../deps/fvv/kotlin")
	}
}

// noinspection GradleDynamicVersion
dependencies {
	compileOnly(gradleApi())
	compileOnly(localGroovy())

	compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:+")
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