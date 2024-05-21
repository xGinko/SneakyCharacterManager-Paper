plugins {
    id("scm.project-conventions")
    java
    alias(libs.plugins.userdev)
    alias(libs.plugins.shadow)
}

repositories {
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
	mavenCentral()
}

dependencies {
    paperweight.paperDevBundle(libs.versions.paperdevbundle.get())
    compileOnly(libs.paper)
    implementation(libs.httpclient)
    compileOnly(libs.papi)
    compileOnly(libs.luckperms)
}

tasks {
    build.configure {
        dependsOn("shadowJar")
    }

    shadowJar {
        archiveFileName = "${rootProject.name}-${project.version}-paper.jar"
        relocate("com.github.benmanes.caffeine", "net.sneakymouse.sneakycharactermanager.libs.caffeine")
        relocate("io.github.thatsmusic99.configurationmaster", "net.sneakymouse.sneakycharactermanager.libs.configmaster")
    }
}