plugins {
    id("scm.project-conventions")
    alias(libs.plugins.userdev)
    alias(libs.plugins.shadow)
    alias(libs.plugins.runpaper)
}

repositories {
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/") {
        name = "placeholderapi-repo"
    }
    maven("https://jitpack.io") {
        name = "jitpack"
    }
}

dependencies {
    implementation(projects.sneakycharactermanagerShared)

    compileOnly(libs.paper)
    paperweight.paperDevBundle(libs.versions.paperdevbundle.get())
    compileOnly(libs.gsit)
    compileOnly(libs.papi)
    compileOnly(libs.luckperms)

    implementation(libs.httpclient)
}

tasks {
    runServer {
        minecraftVersion(libs.versions.minecraftTargetVer.get())
    }

    build.configure {
        dependsOn("shadowJar")
    }

    shadowJar {
        archiveFileName = "${rootProject.name}-${project.version}-paper.jar"
        relocate("com.github.benmanes.caffeine", "net.sneakymouse.sneakycharactermanager.libs.caffeine")
        relocate("io.github.thatsmusic99.configurationmaster", "net.sneakymouse.sneakycharactermanager.libs.configmaster")
    }
}