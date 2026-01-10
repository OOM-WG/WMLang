plugins {
	`kotlin-dsl`
}

repositories {
	mavenLocal()
	mavenCentral()
	google()
	gradlePluginPortal()
}

//noinspection GradleDynamicVersion
dependencies {
	implementation("com.palantir.gradle.gitversion:gradle-git-version:+")
}