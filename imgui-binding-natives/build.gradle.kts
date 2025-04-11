plugins {
    id("java")
    id("maven-publish")
    id("signing")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

var packageName = "imgui-java-natives-linux"
var packageDesc = "Native binaries for imgui-java binding for Linux"
var moduleName = "imgui.natives.linux"
val fromDir = "../bin"
var libName = "libimgui-java64.so"

when (findProperty("deployType")) {
    "windows" -> {
        packageName = "imgui-java-natives-windows"
        packageDesc = "Native binaries for imgui-java binding for Windows"
        moduleName = "imgui.natives.windows"
        libName = "imgui-java64.dll"
    }
    "linux" -> {
        packageName = "imgui-java-natives-linux"
        packageDesc = "Native binaries for imgui-java binding for Linux"
        moduleName = "imgui.natives.linux"
        libName = "libimgui-java64.so"
    }
    "macos" -> {
        packageName = "imgui-java-natives-macos"
        packageDesc = "Native binaries for imgui-java binding for macOS"
        moduleName = "imgui.natives.macos"
        libName = "libimgui-java64.dylib"
    }
}

tasks.jar {
    from(fromDir) {
        include("$libName") // this is fine
        into("io/imgui/java/native-bin/")
    }
    manifest {
        attributes(mapOf("Automatic-Module-Name" to moduleName))
    }
}

configurePublishing(packageName, packageDesc, project.version as String)
