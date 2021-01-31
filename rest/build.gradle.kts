import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.21"
    application
}

group = "com.github.lupuuss.todo"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
    maven { url = uri("https://dl.bintray.com/kotlin/kotlinx") }
    maven { url = uri("https://dl.bintray.com/kotlin/ktor") }
}

val vKtor = "1.5.1"
val vKodein = "7.2.0"

dependencies {
    implementation(project(":core"))

    // utils

    implementation("org.mindrot:jbcrypt:0.4")

    // Ktor

    implementation("io.ktor:ktor-server-netty:$vKtor")
    implementation("io.ktor:ktor-html-builder:$vKtor")
    implementation("io.ktor:ktor-auth:$vKtor")
    implementation("io.ktor:ktor-auth-jwt:$vKtor")

    // Kodein

    implementation("org.kodein.di:kodein-di-framework-ktor-server-jvm:$vKodein")
    implementation("org.kodein.di:kodein-di-framework-ktor-server-controller-jvm:$vKodein")

    testImplementation(kotlin("test-junit"))
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("com.github.lupuuss.todo.api.rest.ServerKt")
}