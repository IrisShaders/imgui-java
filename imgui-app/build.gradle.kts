plugins {
    id("java-library")
    id("com.gradleup.shadow") version "8.3.3"
    id("checkstyle")
    id("maven-publish")
    id("signing")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

dependencies {
    api(platform("org.lwjgl:lwjgl-bom:${property("lwjglVersion")}"))

    api("org.lwjgl:lwjgl")
    api("org.lwjgl:lwjgl-glfw")
    api("org.lwjgl:lwjgl-opengl")

    listOf("natives-linux", "natives-windows", "natives-macos", "natives-macos-arm64").forEach {
        api("org.lwjgl:lwjgl::$it")
        api("org.lwjgl:lwjgl-glfw::$it")
        api("org.lwjgl:lwjgl-opengl::$it")
    }

    api(project(":imgui-binding"))
    api(project(":imgui-lwjgl3"))
}

tasks {
    jar {
        from("../bin") {
            include("imgui-java64.dll")
            include("libimgui-java64.so")
            include("libimgui-java64.dylib")
            into("io/imgui/java/native-bin/")
        }

        manifest {
            attributes(mapOf("Automatic-Module-Name" to "imgui.app"))
        }
    }

    shadowJar {
        dependsOn(jar)
    }
}

configurePublishing("imgui-java-app", "Application wrapper for Dear ImGui", project.version as String)
