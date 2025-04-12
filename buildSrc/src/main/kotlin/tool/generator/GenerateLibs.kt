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
    private val forMacArm64 = buildEnvs?.contains("macosarm64") == true

    private val isLocal = System.getProperties().containsKey("local")
    private val withFreeType = System.getProperty("freetype", "false") == "true"

    private val sourceDir = project.file("src/generated/java")
    private val classpath = project.file("build/classes/java/main")
    private val rootDir = (if (isLocal) project.layout.buildDirectory.get().asFile.absolutePath else "/tmp/imgui")
    private val jniDir = "$rootDir/jni"
    private val tmpDir = "$rootDir/tmp"
    private val libsDirName = "libsNative"

    @TaskAction
    fun generate() {
        println("Generating Native Libraries...")
        println("Build targets: $buildEnvs")
        println("Local: $isLocal")
        println("FreeType: $withFreeType")
        println("Location: $rootDir")
        println("=====================================")

        if (buildEnvs == null) {
            throw IllegalStateException("No build targets")
        }

        File(jniDir).deleteRecursively()
        File(tmpDir).deleteRecursively()
        val libsDirPath = "$rootDir/$libsDirName"
        val libsDir = File("$rootDir/$libsDirName")
        libsDir.deleteRecursively()

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
        if (forWindows) {
            os = Os.Windows
            val win64 = BuildTarget.newDefaultTarget(Os.Windows, Architecture.Bitness._64)
            addFreeTypeIfEnabled(win64)
            buildTargets += win64
        }

        if (forLinux) {
            os = Os.Linux
            val linux64 = BuildTarget.newDefaultTarget(Os.Linux, Architecture.Bitness._64)
            linux64.cFlags += "-g"
            linux64.cppFlags += "-g"
            addFreeTypeIfEnabled(linux64)
            buildTargets += linux64
        }

        if (forMac) {
            os = Os.MacOsX
            buildTargets += createMacTarget(Architecture.x86)
        }

        if (forMacArm64) {
            buildTargets += createMacTarget(Architecture.ARM)
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
        if (forMac)
            checkLibExist("macosx64/libimgui-java64.dylib")
        if (forMacArm64)
            checkLibExist("macosxarm64/libimgui-java64.dylib")
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
        macTarget.cppFlags += " -std=c++14"
        // macTarget.cppFlags = macTarget.cppFlags.replace("10.7", minMacOsVersion)
        // macTarget.linkerFlags = macTarget.linkerFlags.replace("10.7", minMacOsVersion)
        addFreeTypeIfEnabled(macTarget)
        return macTarget
    }

    fun addFreeTypeIfEnabled(target: BuildTarget) {
        if (!withFreeType) {
            return
        }

        val freetypeVendorDir = project.rootProject.file("build/vendor/freetype")
        if (!freetypeVendorDir.exists()) {
            logger.error("$freetypeVendorDir doesn't exist! Run \"buildSrc/scripts/vendor_freetype.sh\" for your platform beforehand!")
            throw IllegalStateException("Unable to build library for FreeType")
        }

        target.cppFlags += " -I$freetypeVendorDir/include"
        target.linkerFlags += " -L${project.rootProject.file("$freetypeVendorDir/lib")}"
        target.libraries += " -lfreetype"
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
