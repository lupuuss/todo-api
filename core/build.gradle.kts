plugins {
    kotlin("multiplatform") version "1.4.30"
    kotlin("plugin.serialization") version "1.4.30"
    id("maven-publish")
}

group = "com.github.lupuuss.todo"
version = "1.0.1"

val publishPassword: String by project
val publishUrlWrite: String by project

publishing {

    repositories {
        maven {
            url = uri(publishUrlWrite)
            credentials {
                username = "myMavenRepo"
                password = publishPassword
            }
        }
    }
}

kotlin {

    jvm()

    js {
        browser()
    }

    publishing {

        publications {
            create<MavenPublication>("todo-api-core") {
                pom.packaging = "jar"
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0-RC")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
    }
}
