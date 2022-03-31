group = "dev.librecybernetics"
version = "0.1.0"

plugins {
    kotlin("jvm") version "1.6.20-RC2"
    `java-library`
    jacoco
}

repositories { mavenCentral() }

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

// Show STDOUT in test run
tasks.withType<AbstractTestTask> { testLogging { showStandardStreams = true } }

tasks.test { finalizedBy(tasks.jacocoTestReport) }

tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        xml.required.set(true)
    }
}

jacoco {
    toolVersion = "0.8.7"
}
