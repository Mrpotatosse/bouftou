import org.lwjgl.Lwjgl
import org.lwjgl.Release
import org.lwjgl.lwjgl
import org.lwjgl.sonatype

plugins {
    kotlin("jvm")

    id("org.lwjgl.plugin") version "0.0.35"
}

group = "io.github.mrpotatosse"
version = ""

repositories {
    mavenCentral()
    sonatype()
}

dependencies {
    implementation(platform("io.insert-koin:koin-bom:4.2.0"))
    implementation("io.insert-koin:koin-core")
    implementation("com.github.ajalt.clikt:clikt:5.1.0")

    // internal modules
    implementation(project(":utils"))
    implementation(project(":blop"))
    implementation(project(":atouin"))
    implementation(project(":hiboukin"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

    lwjgl {
        version = Release.latest
        implementation(Lwjgl.Preset.everything)
        implementation(Lwjgl.Module.nanovg, Lwjgl.Module.stb)
    }
}

kotlin {
    jvmToolchain(25)
}

tasks.test {
    useJUnitPlatform()
}