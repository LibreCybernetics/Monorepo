group = "dev.librecybernetics"
version = "0.1.0"

repositories { mavenCentral() }

plugins {
	kotlin("multiplatform") version "1.7.10"
	id("org.jetbrains.kotlinx.kover") version "0.5.1"
	id("java-library")
}

kotlin {
	jvm()

	sourceSets {
		val commonTest by getting {
			dependencies {
				implementation("org.jetbrains.kotlin:kotlin-test")
			}
		}
	}
}

tasks.withType<AbstractTestTask> {
	testLogging {
		showStandardStreams = true
	}
}

tasks.test {
	extensions.configure(kotlinx.kover.api.KoverTaskExtension::class) {}
}
