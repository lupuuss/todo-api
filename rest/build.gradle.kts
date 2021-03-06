import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.30"
    application
}

group = "com.github.lupuuss.todo"
version = "1.0.4"

repositories {
    jcenter()
    maven { url = uri("https://dl.bintray.com/kotlin/kotlinx") }
    maven { url = uri("https://dl.bintray.com/kotlin/ktor") }
}

val vKtor = "1.5.1"
val vKodein = "7.2.0"
val vSlf4j = "2.0.0-alpha1"
val vKMongo = "4.2.4"

dependencies {

    implementation(project(":core"))

    // utils

    implementation("org.slf4j:slf4j-simple:$vSlf4j")
    implementation("org.slf4j:slf4j-api:$vSlf4j")

    implementation("org.mindrot:jbcrypt:0.4")

    // Ktor

    implementation("io.ktor:ktor-server-netty:$vKtor")
    implementation("io.ktor:ktor-serialization:$vKtor")
    implementation("io.ktor:ktor-auth:$vKtor")
    implementation("io.ktor:ktor-auth-jwt:$vKtor")
    implementation("io.ktor:ktor-websockets:$vKtor")

    // Kodein

    implementation("org.kodein.di:kodein-di-framework-ktor-server-jvm:$vKodein")
    implementation("org.kodein.di:kodein-di-framework-ktor-server-controller-jvm:$vKodein")

    // mongo
    implementation("org.litote.kmongo:kmongo:$vKMongo")

    testImplementation(kotlin("test-junit"))
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

fun JavaExec.envOrProperty(name: String) {
    val local = properties[name]
    environment(name, requireNotNull(System.getenv(name) ?: local))
}

tasks.withType<JavaExec>() {
    envOrProperty("CONNECT_STR")
    envOrProperty("DB_NAME")
    envOrProperty("JWT_SECRET")
    envOrProperty("TOKEN_REFRESH")
    envOrProperty("TOKEN_EXPIRE")
    envOrProperty("PORT")
}

val mainClassKt = "com.github.lupuuss.todo.api.rest.ServerKt"

application {
    mainClass.set(mainClassKt)
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = mainClassKt
    }

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}