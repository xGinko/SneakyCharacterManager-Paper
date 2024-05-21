enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    plugins {
        id("io.papermc.paperweight.userdev") version "1.7.0"
    }
}

plugins {
    id("com.gradle.enterprise") version "3.16.1"
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
}

dependencyResolutionManagement {
    @Suppress("UnstableAPIUsage")
    repositories {
        maven("https://repo.papermc.io/repository/maven-public/") {
            name = "papermc-repo"
        }
        maven("https://ci.pluginwiki.us/plugin/repository/everything/") {
            name = "configmaster-repo"
        }
        maven("https://repo.aikar.co/content/groups/aikar/") {
            name = "aikar-repo"
        }
        maven("https://jitpack.io") {
            name = "jitpack"
        }
        mavenCentral()
    }
}

rootProject.name = "SneakyCharacterManager"

gradleEnterprise {
    buildScan {
        if (!System.getenv("CI").isNullOrEmpty()) {
            termsOfServiceUrl = "https://gradle.com/terms-of-service"
            termsOfServiceAgree = "yes"
        }
    }
}

setupSneakySubproject("shared")
setupSneakySubproject("paper")
setupSneakySubproject("bungee")

setupSubproject("sneakycharactermanager") {
    projectDir = file("universal")
}

fun setupSneakySubproject(name: String) {
    setupSubproject("sneakycharactermanager-$name") {
        projectDir = file(name)
    }
}

inline fun setupSubproject(name: String, block: ProjectDescriptor.() -> Unit) {
    include(name)
    project(":$name").apply(block)
}