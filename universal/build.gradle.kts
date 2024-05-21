plugins {
    id("scm.project-conventions")
}

val platforms = setOf(
    rootProject.projects.sneakycharactermanagerBungee,
    rootProject.projects.sneakycharactermanagerPaper
).map { it.dependencyProject }

tasks {
    jar {
        archiveClassifier.set("")
        archiveFileName.set("SneakyCharacterManager-" + rootProject.version + ".jar")
        destinationDirectory.set(rootProject.projectDir.resolve("build/libs"))
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        platforms.forEach { platform ->
            val shadowJarTask = platform.tasks.named<Jar>("shadowJar").get()
            dependsOn(shadowJarTask)
            dependsOn(platform.tasks.withType<Jar>())
            from(zipTree(shadowJarTask.archiveFile))
        }
    }
}