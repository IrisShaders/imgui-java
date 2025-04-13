package tool.generator

import com.badlogic.gdx.jnigen.BuildConfig
import com.badlogic.gdx.jnigen.BuildTarget
import com.badlogic.gdx.jnigen.FileDescriptor
import com.badlogic.gdx.jnigen.NativeCodeGenerator
import com.badlogic.gdx.jnigen.build.PlatformBuilder
import com.badlogic.gdx.jnigen.commons.Architecture
import com.badlogic.gdx.jnigen.commons.Os
import org.gradle.api.DefaultTask
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

open class GenerateLibs : DefaultTask() {
    companion object {
        @JvmStatic
        val INCLUDES = listOf(
            "include/imgui",
            "include/imnodes",
            "include/imgui-node-editor",
            "include/imguizmo",
            "include/implot",
//        "include/ImGuiColorTextEdit",
//        "include/ImGuiFileDialog",
            "include/imgui_club/imgui_memory_editor",
            "include/imgui-knobs"
        )
    }

    init {
        group = "build"
        description = "Generate imgui-java native binaries."
    }

    private val buildEnvs = System.getProperty("envs")?.split(",")
    private val forWindows = buildEnvs?.contains("windows") == true
    private val forLinux = buildEnvs?.contains("linux") == true
    private val forMac = buildEnvs?.contains("macos") == true
    private val buildArm = System.getProperty("arm64", "false") == "true"
    // TODO: Figure out how to cross compile arm64 on Linux and Windows. This only works on macOS right now.

    private val withFreeType = System.getProperty("freetype", "false") == "true"

    private val sourceDir = project.file("src/generated/java")
    private val classpath = project.file("build/classes/java/main")
    private val rootDir = project.layout.buildDirectory.get().asFile.resolve("imgui").absolutePath
    private val jniDir = "$rootDir/jni"
    private val tmpDir = "$rootDir/tmp"
    private val libsDirName = "libsNative"

    fun runCommands(workDir: File, vararg commands: String): Int {
        val pb = ProcessBuilder()
        pb.directory(workDir)
        pb.command(*commands)
        return pb.start().apply {
            this.inputStream.use {
                it.bufferedReader().use { r ->
                    var line = r.readLine()
                    while (line != null) {
                        logger.info("[STDOUT] $line")
                        line = r.readLine()
                    }
                }
            }

            this.errorStream.use {
                it.bufferedReader().use { r ->
                    var line = r.readLine()
                    while (line != null) {
                        logger.error("[STDERR] $line")
                        line = r.readLine()
                    }
                }
            }
        }.waitFor()
    }

    @TaskAction
    fun generate() {
        println("Generating Native Libraries...")
        println("Build targets: $buildEnvs")
        println("FreeType: $withFreeType")
        println("Location: $rootDir")
        println("=====================================")

        if (buildEnvs == null) {
            throw IllegalStateException("No build targets")
        }

        File(jniDir).deleteRecursively()
        File(tmpDir).deleteRecursively()
        val libsDirPath = "$rootDir/$libsDirName"


        // Generate h/cpp files for JNI
        NativeCodeGenerator().generate(sourceDir.absolutePath, classpath.absolutePath, jniDir)

        // Copy ImGui h/cpp files
        project.copy {
            INCLUDES.forEach {
                from(project.rootProject.file(it)) { include("*.h", "*.cpp", "*.inl") }
            }
            from(project.rootProject.file("imgui-binding/src/main/native"))
            into(jniDir)
            duplicatesStrategy = DuplicatesStrategy.INCLUDE // Allows for duplicate imconfig.h, we ensure the correct one is copied below
        }

        // Ensure we overwrite imconfig.h with our own
        project.copy {
            from(project.rootProject.file("imgui-binding/src/main/native/imconfig.h"))
            into(jniDir)
        }

        if (withFreeType) {
            project.copy {
                from(project.rootProject.file("include/imgui/misc/freetype")) { include("*.h", "*.cpp") }
                into("$jniDir/misc/freetype")
            }
            
            // Since we give a possibility to build library without enabled freetype - define should be set like that.
            replaceSourceFileContent("imconfig.h", "//#define IMGUI_ENABLE_FREETYPE", "#define IMGUI_ENABLE_FREETYPE")

            // Binding specific behavior to handle FreeType.
            // By defining IMGUI_ENABLE_FREETYPE, Dear ImGui will default to using the FreeType font renderer.
            // However, we modify the source code to ensure that, even with this, the STB_TrueType renderer is used instead.
            // To use the FreeType font renderer, it must be explicitly forced on the atlas manually.
            replaceSourceFileContent("imgui_draw.cpp", "ImGuiFreeType::GetBuilderForFreeType()", "ImFontAtlasGetBuilderForStbTruetype()")
        }

        // Copy dirent for ImGuiFileDialog
        project.copy {
            from(project.rootProject.file("include/ImGuiFileDialog/dirent")) { include("*.h", "*.cpp", "*.inl") }
            into("$jniDir/dirent")
        }

        // Generate platform dependant ant configs and header files
        val buildConfig = BuildConfig("imgui-java", tmpDir, libsDirPath, jniDir, FileDescriptor(rootDir))
        val buildTargets = mutableListOf<BuildTarget>()

        buildConfig.multiThreadedCompile = true
        var os: Os? = null
        val arch = if (buildArm) Architecture.ARM else Architecture.x86
        if (forWindows) {
            os = Os.Windows
            val libsDir = File("$rootDir/$libsDirName/win64")
            libsDir.deleteRecursively()
            val win64 = BuildTarget.newDefaultTarget(Os.Windows, Architecture.Bitness._64, arch)
            addFreeTypeIfEnabled(win64, "windows")
            buildTargets += win64
        }

        if (forLinux) {
            os = Os.Linux
            val libsDir = File("$rootDir/$libsDirName/linux64")
            libsDir.deleteRecursively()
            val linux64 = BuildTarget.newDefaultTarget(Os.Linux, Architecture.Bitness._64, arch)
            linux64.cFlags += "-Os"
            linux64.cppFlags += "-Os"
            linux64.linkerFlags += "-Os"
            addFreeTypeIfEnabled(linux64, "linux")
            buildTargets += linux64
        }

        if (forMac) {
            os = Os.MacOsX
            val target = createMacTarget(Architecture.x86)
            target.cFlags += "-Oz"
            target.cppFlags += "-Oz"
            target.linkerFlags += "-Oz -s"
            buildTargets += target
            val target2 = createMacTarget(Architecture.ARM)
            target2.cFlags += "-Oz"
            target2.cppFlags += "-Oz"
            target2.linkerFlags += "-Oz -s"
            buildTargets += target2
        }


        // Generate native libraries
        // Comment/uncomment lines with OS you need.

        val commonParams = listOf("-v", "-Dhas-compiler=true", "-Drelease=true", "clean", "postcompile")
        println("Build dir: " + buildConfig.buildDir.toString())
        PlatformBuilder.copyHeaders(FileDescriptor(jniDir))

        PlatformBuilder().build(os, buildConfig, buildTargets)


        if (forWindows)
            checkLibExist("windows64/imgui-java64.dll")
        if (forLinux)
            checkLibExist("linux64/libimgui-java64.so")
        if (forMac) {
            checkLibExist("macosx64/libimgui-java64.dylib")
            checkLibExist("macosxarm64/libimgui-java64.dylib")
            Files.createDirectories(Path.of("$rootDir/$libsDirName/macos"))

            logger.info("Creating universal library using lipo...")

            if (runCommands(File("$rootDir/$libsDirName/"), "lipo", "-create", "-output", "macos/libimgui-java64.dylib", "macosx64/libimgui-java64.dylib", "macosxarm64/libimgui-java64.dylib") != 0) {
                throw RuntimeException("Failed to create universal library with lipo")
            }

            File("$rootDir/$libsDirName/macosx64").deleteRecursively()
            File("$rootDir/$libsDirName/macosxarm64").deleteRecursively()

            if (runCommands(File("$rootDir/$libsDirName/"), "strip", "-S", "-x", "macos/libimgui-java64.dylib") != 0) {
                throw RuntimeException("Failed to create universal library with lipo")
            }
        }
    }

    fun checkLibExist(libName: String) {
        val path = File("$rootDir/$libsDirName/$libName")
        if (!path.exists()) {
            logger.error("Failed to build $libName!")
            throw IllegalStateException("$path does not exist")
        }
    }

    fun createMacTarget(arch: Architecture): BuildTarget {
        val minMacOsVersion = "10.15"
        val macTarget = BuildTarget.newDefaultTarget(Os.MacOsX, Architecture.Bitness._64, arch)
        macTarget.libName = "libimgui-java64.dylib" // Lib for arm64 will be named the same for consistency.
        macTarget.cppFlags += "-std=c++14"
        // macTarget.cppFlags = macTarget.cppFlags.replace("10.7", minMacOsVersion)
        // macTarget.linkerFlags = macTarget.linkerFlags.replace("10.7", minMacOsVersion)
        addFreeTypeIfEnabled(macTarget, "macos")
        return macTarget
    }

    fun addFreeTypeIfEnabled(target: BuildTarget, os : String) {
        if (!withFreeType) {
            return
        }

        val freetypeVendorDir = project.rootProject.file("build/vendor/freetype/freetype-2.13.3")
        if (!freetypeVendorDir.exists()) {
            logger.error("$freetypeVendorDir doesn't exist! Run \"buildSrc/scripts/vendor_freetype.sh\" for your platform beforehand!")
            throw IllegalStateException("Unable to build library for FreeType")
        }

        target.headerDirs += "$freetypeVendorDir/include"
        target.headerDirs += "${jniDir}/misc/freetype"
        println("Going to link with $freetypeVendorDir/lib/$os")
        target.linkerFlags += "-L${project.rootProject.file("$freetypeVendorDir/lib/$os")}"
        target.libraries += "-lfreetype"
    }

    fun replaceSourceFileContent(fileName: String, replaceWhat: String, replaceWith: String) {
        val sourceFile = File("$jniDir/$fileName")
        val sourceTxt = sourceFile.readText()
        val sourceTxtModified = sourceTxt.replace(replaceWhat, replaceWith)
        if (sourceTxt == sourceTxtModified) {
            throw IllegalStateException("Unable to replace [$fileName] with content [$replaceWith]!")
        }
        sourceFile.writeText(sourceTxtModified)
    }
}
