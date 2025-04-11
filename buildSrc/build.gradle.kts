plugins {
    groovy
    `kotlin-dsl`
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        mavenContent {
            snapshotsOnly()
        }
    }
}

dependencies {
    implementation(gradleApi())
    implementation(localGroovy())

    implementation("com.badlogicgames.jnigen:jnigen-core:3.0.1-SNAPSHOT")
    implementation("com.badlogicgames.jnigen:jnigen-commons:3.0.1-SNAPSHOT")
    implementation("com.badlogicgames.jnigen:jnigen-generator:3.0.1-SNAPSHOT")
    implementation("com.badlogicgames.jnigen:jnigen-loader:3.0.1-SNAPSHOT")
    implementation("com.badlogicgames.jnigen:jnigen-runtime:3.0.1-SNAPSHOT")
    implementation("com.badlogicgames.jnigen:jnigen-runtime-platform:3.0.1-SNAPSHOT:natives-desktop")
    implementation("com.badlogicgames.jnigen:jnigen-runtime-platform:3.0.1-SNAPSHOT:natives-x86_64")
    implementation("org.reflections:reflections:0.10.2")
    implementation("com.lordcodes.turtle:turtle:0.6.0")
    implementation("fr.inria.gforge.spoon:spoon-core:10.3.0")

    implementation("com.fasterxml.jackson.core:jackson-core:2.18.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.0")
}
