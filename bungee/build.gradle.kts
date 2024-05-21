plugins {
    id("scm.project-conventions")
    alias(libs.plugins.shadow)
}

dependencies {
    compileOnly(libs.bungee)

    implementation(projects.sneakycharactermanagerShared)

    implementation(libs.configmaster)
}

tasks {
    build.configure {
        dependsOn("shadowJar")
    }

    shadowJar {
        archiveFileName = "${rootProject.name}-${project.version}-bungee.jar"
    }
}