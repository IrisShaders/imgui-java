import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.kotlin.dsl.maven

fun Project.configurePublishing(packageName: String, packageDesc: String, packageVersion: String) {
    val sourceSets = this.extensions.getByName("sourceSets") as SourceSetContainer

    tasks.register("sourcesJar", Jar::class.java) {
        dependsOn("classes")
        archiveClassifier.set("sources")
        from(sourceSets.getByName("main").allSource)
    }
    tasks.register("javadocJar", Jar::class.java) {
        dependsOn(tasks.named("javadoc"))
        archiveClassifier.set("javadoc")
        from((tasks.named("javadoc").get() as Javadoc).destinationDir)
    }
    this.extensions.configure<PublishingExtension>("publishing") {
        repositories {
            val releasesRepoUrl = "https://mvn.devos.one/releases"
            val snapshotsRepoUrl = "https://mvn.devos.one/snapshots"
            maven(if (packageVersion.endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl) {
                name = "devOS"
                credentials {
                    username = System.getenv("MAVEN_USER")?.trim() ?: ""
                    password = System.getenv("MAVEN_PASS")?.trim() ?: ""
                }
            }
        }
        publications {
            create("imgui", MavenPublication::class.java) {
                groupId = "io.github.spair"
                artifactId = packageName
                version = packageVersion

                from(components.getByName("java"))
                artifact(tasks.named("sourcesJar").get())
                artifact(tasks.named("javadocJar").get())

                pom {
                    name.set(packageName)
                    description.set(packageDesc)
                    url.set("https://github.com/IrisShaders/imgui-java")
                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("https://opensource.org/license/mit/")
                        }
                    }
                    developers {
                        developer {
                            id.set("SpaiR")
                            name.set("Ilya Prymshyts")
                            email.set("despsolver@gmail.com")
                        }
                    }
//                    scm {
//                        connection = "scm:git:https://github.com/SpaiR/imgui-java.git"
//                        developerConnection = "scm:git:https://github.com/SpaiR/imgui-java.git"
//                        url = "https://github.com/SpaiR/imgui-java.git"
//                    }
                }
            }
        }
    }
//    if (System.getenv("SIGNING_KEY_ID") != null) {
//        signing {
//            def signingKeyId = System.getenv("SIGNING_KEY_ID")?.trim() ?: ""
//            def signingKey = System.getenv("SIGNING_KEY")?.trim() ?: ""
//            def signingKeyPass = System.getenv("SIGNING_KEY_PASS")?.trim() ?: ""
//            useInMemoryPgpKeys(signingKeyId, signingKey, signingKeyPass)
//            sign publishing.publications.imgui
//        }
//    }
}
