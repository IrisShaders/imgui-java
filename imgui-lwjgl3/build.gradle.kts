plugins {
    id("java")
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
    implementation(platform("org.lwjgl:lwjgl-bom:${property("lwjglVersion")}"))

    implementation("org.lwjgl:lwjgl")
    implementation("org.lwjgl:lwjgl-glfw")
    implementation("org.lwjgl:lwjgl-opengl")

    implementation(project(":imgui-binding"))
}

configurePublishing("imgui-java-lwjgl3", "Backend LWJGL3 implementation for imgui-java", project.version as String)

tasks.jar {
    manifest {
        attributes(mapOf("Automatic-Module-Name" to "imgui.lwjgl3"))
    }
}
