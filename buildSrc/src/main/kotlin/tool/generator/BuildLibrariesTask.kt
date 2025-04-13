package tool.generator

import org.apache.maven.shared.utils.Os
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.PosixFilePermissions

open class BuildLibrariesTask : DefaultTask() {
    companion object {
        const val LIBDIR = "build/vendor/freetype"
        const val VERSION = "2.13.3"
        const val MACOS_VERSION = "10.15"

        const val COMMON_FLAGS = "--enable-static --disable-shared --without-zlib --without-bzip2 --without-png --without-harfbuzz --without-brotli"
    }

    init {
        this.group = "build"
        this.description = "Builds ImGui libraries for the current platform (or specified platforms if run with \"-Pplatforms=\")"
        this.dependsOn(":imgui-binding:assemble")
    }

    @TaskAction
    fun build() {
        val platforms = this.getPlatforms()

        for (platform in platforms) {
            // vendor_freetype.sh
            prepareVendorFreetype(platform)

            // build.sh
            logger.info("Creating destination directory for ImGui libraries...")
            val buildDir = project.layout.buildDirectory.get().asFile
            val imGuiLibDir = buildDir.resolve("tmp/imgui/dst")
            val generatedDir = project.rootDir.resolve("imgui-binding/build/imgui/libsNative")

            if (!imGuiLibDir.exists())
                imGuiLibDir.mkdirs()

            logger.info("Directory $imGuiLibDir created successfully")

            val isWindows = Os.OS_FAMILY == Os.FAMILY_WINDOWS

            logger.info("Running Gradle task for platform $platform...")
            if (runCommands(project.rootDir, if (isWindows) "gradlew" else "./gradlew", "imgui-binding:generateLibs", "-Denvs=$platform", "-Dfreetype=true") != 0) {
                throw RuntimeException("Gradle task for platform $platform failed!")
            }

            logger.info("Checking if the generated library exists...")

            val platformGenFiles = if (platform == "windows")
                listOf(generatedDir.resolve("windows64/imgui-java64.dll"))
            else if (platform == "macos")
                listOf(generatedDir.resolve("macosx64/libimgui-java64.dylib"), generatedDir.resolve("macosxarm64/libimgui-java64.dylib"))
            else if (platform == "linux")
                listOf(generatedDir.resolve("linux64/libimgui-java64.so"))
            else throw IllegalArgumentException("Invalid platform $platform!")

            for (file in platformGenFiles) {
                if (!file.exists())
                    throw IllegalStateException("File $file not found!")
            }

            if (platform == "macos") {
                logger.info("Creating a universal library using lipo...")

                runCommands(project.rootDir, "lipo", "-create", "-output", imGuiLibDir.resolve("libimgui-java64.dylib").absolutePath, *platformGenFiles.map { it.absolutePath }.toTypedArray())

                logger.info("Universal library created successfully.")
            } else {
                logger.info("Copying the generated library file to the destination directory...")

                for (file in platformGenFiles) {
                    file.copyTo(imGuiLibDir.resolve(file.name), true)
                }
            }
        }
    }

    fun prepareVendorFreetype(platform: String) {
        val rootDir = project.rootDir

        var libDir = rootDir.resolve(LIBDIR)
        logger.info("Cleaning and creating library directory, then extracting FreeType source...")

        libDir.resolve(platform).deleteRecursively()
        libDir.mkdirs()

        // Extract FreeType source
        try {
            project.tarTree("vendor/freetype-$VERSION.tar.gz").visit {
                val file = libDir.resolve(this.path)

                if (!file.parentFile.exists())
                    file.parentFile.mkdirs()

                if (!file.exists())
                    if (!this.isDirectory)
                        file.createNewFile()
                    else
                        file.mkdirs()

                if (!this.isDirectory) {
                    this.open().use { input ->
                        file.outputStream().use { out ->
                            input.transferTo(out)
                        }
                    }

                    Files.setPosixFilePermissions(file.toPath(), PosixFilePermissions.fromString("rwxr-xr-x"))
                }
            }
        } catch (e: Throwable) {
            throw RuntimeException("Failed to extract FreeType source!", e)
        }

        libDir = libDir.resolve("freetype-$VERSION")
        libDir.resolve("configure").setExecutable(true)

        logger.info("FreeType unzipped to $LIBDIR")

        // Ensure necessary directories exist
        logger.info("Ensuring necessary directories exist...")

        libDir.resolve("lib").mkdirs()
        libDir.resolve("tmp").mkdirs()

        prepareBuildFreetype(rootDir, libDir, platform)
    }

    private fun getPlatforms(): List<String> {
        if (project.hasProperty("platforms")) {
            return (project.properties["platforms"] as String).split(",")
        } else {
            val platform = if (Os.OS_FAMILY == Os.FAMILY_WINDOWS)
                "windows"
            else if (Os.OS_FAMILY == Os.FAMILY_MAC)
                "macos"
            else
                Os.OS_NAME.lowercase().split(" ").first()

            return listOf(platform)
        }
    }

    private fun prepareBuildFreetype(rootDir: File, libDir: File, vendorType: String) {
        logger.info("Preparing build for vendor type $vendorType")

        val unixDef = libDir.resolve("builds/unix/unix-def.in")
        if (unixDef.exists()) {
            if (vendorType == "windows")
                // https://github.com/rdp/ffmpeg-windows-build-helpers/issues/234#issuecomment-862903347
                unixDef.writeText(unixDef.readText().replace("TOP_DIR := $(shell cd $(TOP_DIR); pwd)", "#replacedForWSLSupport"))
            else
                unixDef.writeText(unixDef.readText().replace("#replacedForWSLSupport", "TOP_DIR := $(shell cd $(TOP_DIR); pwd)"))
        }

        when (vendorType) {
            "windows" -> {
                buildFreetype(libDir, "", "--host=x86_64-w64-mingw32 --prefix=/usr/x86_64-w64-mingw32", libDir.resolve("lib/windows/libfreetype.a"))
            }

            "macos" -> {
                buildFreetype(libDir, "-arch x86_64 -mmacosx-version-min=$MACOS_VERSION", "", libDir.resolve("tmp/libfreetype-x86_64.a"))
                buildFreetype(libDir, "-arch arm64 -mmacosx-version-min=$MACOS_VERSION", "", libDir.resolve("tmp/libfreetype-arm64.a"))

                logger.info("Creating universal library using lipo...")

                if (runCommands(libDir, "lipo", "-create", "-output", "lib/libfreetype.a", "tmp/libfreetype-x86_64.a", "tmp/libfreetype-arm64.a") != 0) {
                    throw RuntimeException("Failed to create universal library with lipo")
                }

                logger.info("Universal library created at lib/libfreetype.a")
            }

            "linux" -> {
                buildFreetype(libDir, "-fPIC", "", libDir.resolve("lib/linux/libfreetype.a"))
            }

            else -> {
                throw RuntimeException("Unknown vendor type: $vendorType")
            }
        }
    }

    private fun buildFreetype(workDir: File, cFlags: String, prefix: String, outputDir: File) {
        Files.createDirectories(outputDir.toPath())
        logger.info("Cleaning previous builds...")
        runCommands(workDir, "make", "clean")

        logger.info("Autogenning FreeType")
        if (runCommands(workDir, "./autogen.sh").apply { if (this != 0) logger.info("Exited with code $this") } != 0) {
            throw RuntimeException("Failed to configure FreeType")
        }

        logger.info("Configuring FreeType with CFLAGS='$cFlags' and PREFIX='$prefix'")
        if (runCommands(workDir, "./configure", "CFLAGS=$cFlags", *COMMON_FLAGS.split(" ").toTypedArray(), *prefix.split(" ").toTypedArray()).apply { if (this != 0) logger.info("Exited with code $this") } != 0) {
            throw RuntimeException("Failed to configure FreeType")
        }

        logger.info("Building FreeType...")

        if (runCommands(workDir, "make", "-j6").apply { if (this != 0) logger.info("Exited with code $this") } != 0) {
            throw RuntimeException("Failed to build FreeType!")
        }

        logger.info("Checking if the generated library exists...")
        if (!workDir.resolve("objs/.libs/libfreetype.a").exists()) {
            throw IllegalStateException("File objs/.libs/libfreetype.a not found!")
        }

        logger.info("Copying the generated library to $outputDir...")
        logger.info("Library copied to ${workDir.resolve("objs/.libs/libfreetype.a").copyRecursively(outputDir, true)}")
    }

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
}
