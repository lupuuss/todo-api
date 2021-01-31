plugins {
    kotlin("multiplatform") version "1.4.21"
}

group = "com.github.lupuuss.todo"
version = "1.0-SNAPSHOT"

kotlin {

    jvm()

    js {
        browser()
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
