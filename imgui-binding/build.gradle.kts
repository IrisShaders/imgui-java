import org.apache.tools.ant.filters.ReplaceTokens
import tool.generator.GenerateLibs
import tool.generator.api.task.GenerateApi

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

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.3")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.11.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

tasks.compileJava {
    // By default we do not generate API, since we believe this should be done on purpose.
    if (System.getProperty("build.debug.generate.api.enable") != null) {
        dependsOn("generateApi")
    }
}

sourceSets {
    getByName("main").java.srcDir("src/generated/java")
    create("raw").java.srcDir("src/main/java")
}

tasks.register("generateApi", GenerateApi::class.java)

tasks.register("generateLibs", GenerateLibs::class.java) {
    dependsOn("assemble")
}

configurePublishing("imgui-java-binding", "JNI based binding for Dear ImGui", project.version as String)

tasks.jar {
    manifest {
        attributes(mapOf("Automatic-Module-Name" to "imgui.binding"))
    }
}

tasks.processResources {
    filesMatching("**/imgui-java.properties") {
        filter(mapOf(
            "tokens" to mapOf(
                "version" to version
            )
        ), ReplaceTokens::class.java)
    }
}
