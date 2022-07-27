group = "dev.librecybernetics"
version = "0.1.0"

repositories { mavenCentral() }

plugins {
	kotlin("multiplatform") version "1.7.10"
	id("org.jetbrains.kotlinx.kover") version "0.5.1"
	id("java-library")
}

rootProject.plugins.withType<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin> {
    rootProject.the<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension>().download = false
}

kotlin {
	js(IR) {
		browser {
			testTask {
				useKarma {
					useFirefox()
				}
			}
		}
		nodejs()
	}

	jvm()

	sourceSets {
		val commonMain by getting
		val commonTest by getting {
			dependencies {
				implementation("org.jetbrains.kotlin:kotlin-test")
			}
		}

		val jsMain by getting

		val nonJvmMain by creating {
			dependsOn(commonMain)

			jsMain.dependsOn(this)
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
