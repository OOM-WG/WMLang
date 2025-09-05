plugins {
	id("org.jetbrains.kotlin.jvm")
	id("java-gradle-plugin")
	id("maven-publish")
}

group = "dev.oom-wg.purejoy.mlang"
version = "main"

sourceSets {
	named("main") {
		java.srcDirs("src/main/kotlin", "../deps/fvv/kotlin")
	}
}

// noinspection GradleDynamicVersion
dependencies {
	compileOnly(gradleApi())
	compileOnly(localGroovy())

	compileOnly("com.android.tools.build:gradle:+")
}

gradlePlugin {
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