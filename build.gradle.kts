import tool.generator.BuildLibrariesTask
import tool.generator.ast.task.GenerateAst

import java.text.SimpleDateFormat

ext {
    set("lwjglVersion", "3.3.3")
}

allprojects {
    group = "imgui-java"
    version = "1.90.9"

    repositories {
        mavenCentral()
    }

    tasks.withType<Jar>().configureEach {
        from(project.rootDir) {
            include("LICENSE")
            into("META-INF")
        }

        val jdkMetadata = tasks.withType(JavaCompile::class.java).first().javaCompiler.get().metadata
        val buildJdk = "${jdkMetadata.javaRuntimeVersion} (${jdkMetadata.vendor})".toString()

        manifest {
            attributes (mapOf(
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version,
                "Build-Timestamp" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(System.currentTimeMillis()),
                "Build-Revision" to Runtime.getRuntime().exec("git rev-parse HEAD").inputStream.use { it.reader().readText() }.trim(),
                "Build-Jdk" to buildJdk,
                "Source-Compatibility" to tasks.withType(JavaCompile::class.java).first().sourceCompatibility,
                "Target-Compatibility" to tasks.withType(JavaCompile::class.java).first().targetCompatibility,
                "Created-By" to "Gradle ${gradle.gradleVersion} "
            ))
        }
    }
}

tasks.register("buildAll") {
    group = "build"
    description = "Build all project sources."

    dependsOn(":imgui-app:shadowJar")

    listOf("app", "binding", "lwjgl3").forEach { module ->
        listOf("build", "sourcesJar", "javadocJar").forEach { task ->
            dependsOn(":imgui-$module:$task")
        }
    }
}

tasks.register<BuildLibrariesTask>("buildLibraries")

tasks.register<GenerateAst>("generateAst") {
    headerFiles = listOf(
            file("include/imgui/imgui.h"),
            file("include/imgui/imgui_internal.h"),
            file("include/imgui/misc/freetype/imgui_freetype.h"),
            file("include/ImGuiFileDialog/ImGuiFileDialog.h"),
            file("include/imgui-knobs/imgui-knobs.h"),
            file("include/imguizmo/ImGuizmo.h"),
            file("include/imnodes/imnodes.h"),
            file("include/implot/implot.h"),
            file("include/imgui-node-editor/imgui_node_editor.h"),
            file("include/imgui_toggle/imgui_toggle.h"),
            file("include/ImGuiColorTextEdit/TextEditor.h"),
    )
}

tasks.register("publishAll") {
    group = "publishing"

    for (name in listOf("imgui-app", "imgui-lwjgl3", "imgui-binding")) {
        dependsOn(":$name:publishImguiPublicationToDevOSRepository")
    }

    dependsOn("publishWindowsNatives", "publishMacNatives", "publishLinuxNatives")
}

tasks.register("publishWindowsNatives") {
    group = "publishing"

    doFirst {
        project.setProperty("deployType", "windows")
    }

    finalizedBy(":imgui-binding-natives:publishImguiPublicationToDevOSRepository")
}

tasks.register("publishMacNatives") {
    group = "publishing"

    doFirst {
        project.setProperty("deployType", "macos")
    }

    finalizedBy(":imgui-binding-natives:publishImguiPublicationToDevOSRepository")
}

tasks.register("publishLinuxNatives") {
    group = "publishing"

    doFirst {
        project.setProperty("deployType", "linux")
    }

    finalizedBy(":imgui-binding-natives:publishImguiPublicationToDevOSRepository")
}
