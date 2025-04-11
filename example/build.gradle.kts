plugins {
    id("application")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

application {
    mainClass.set("Main")
    try {
        val libPath = property("libPath")
        if (libPath != null) {
            applicationDefaultJvmArgs = listOf("-Dimgui.library.path=$libPath")
        }
    } catch (_: Throwable) {
        // ignore
    }
}

dependencies {
    implementation(project(":imgui-app"))
}

tasks.getByName("run", JavaExec::class) {
    val jvmArgs = mutableListOf<String>()
    if (org.gradle.internal.os.OperatingSystem.current().isMacOsX()) {
        jvmArgs.addAll(listOf("-XstartOnFirstThread", "-Djava.awt.headless=true"))
    }

    jvmArgs.add("-Xmx4096M")

    this.jvmArgs(jvmArgs)
}
