plugins {
    kotlin("multiplatform") version "1.4.30"
    id("maven-publish")
}

group = "com.github.lupuuss.todo"
version = "1.0-SNAPSHOT"

publishing {

    repositories {
        maven {
            url = uri("https://mymavenrepo.com/repo/BjwNRjndUUDYeEf55vUU/")
        }
    }
}

kotlin {

    jvm()

    js {
        browser()
    }

    val publicationsFromMainHost =
        listOf(jvm(), js()).map { it.name } + "kotlinMultiplatform"
    publishing {

        publications {
            create<MavenPublication>("todo-api-core") {
            }

            matching { it.name in publicationsFromMainHost }.all {
                val targetPublication = this@all
                tasks.withType<AbstractPublishToMaven>()
                    .matching { it.publication == targetPublication }
                    .configureEach { onlyIf { findProperty("isMainHost") == "true" } }
            }
        }
    }

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
    }
}
