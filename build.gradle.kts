import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.0"
    kotlin("plugin.serialization") version "1.4.0"
    application
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    val coroutinesVersion = "1.3.9"
    val serializationVersion = "1.0.0-RC"
    val koinVersion = "2.2.0-beta-1"
    val ktorVersion = "1.4.0"
    val cliktVersion = "3.0.1"

    // coroutines for async operations
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")

    // serialization for JSON
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$serializationVersion")

    // ktor client for networking
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-serialization:$ktorVersion")

    // CLI parser
    implementation("com.github.ajalt.clikt:clikt:$cliktVersion")

    // dependency injection for simple dependency management
    implementation ("org.koin:koin-core:$koinVersion")

    testImplementation(kotlin("test-junit5"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClassName = "MainKt"
}