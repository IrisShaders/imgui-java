plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.9.0")
}

buildscript {
    repositories {
        mavenCentral()
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
            mavenContent {
                snapshotsOnly()
            }
        }
    }
}

rootProject.name = "imgui-java"
include("imgui-binding")
include("imgui-lwjgl3")
include("imgui-binding-natives")
include("imgui-app")
include("example")
