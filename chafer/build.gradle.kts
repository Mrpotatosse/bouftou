plugins {
    kotlin("jvm")
}

group = "io.github.mrpotatosse"
version = ""

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("io.insert-koin:koin-bom:4.2.0"))
    implementation("io.insert-koin:koin-core")
    implementation("com.github.ajalt.clikt:clikt:5.1.0")

    // ui
    implementation("com.formdev:flatlaf:3.7.1")

    // Project "app" depends on project "utils". (Project paths are separated with ":", so ":utils" refers to the top-level "utils" project.)
    implementation(project(":utils"))
    implementation(project(":blop"))
    implementation(project(":atouin"))
    implementation(project(":hiboukin"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.10.2")
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}