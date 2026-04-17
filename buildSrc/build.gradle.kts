plugins { `kotlin-dsl` }

repositories {
	mavenLocal()
	mavenCentral()
	google()
	gradlePluginPortal()
}

dependencies { implementation(libs.gitVersion) }