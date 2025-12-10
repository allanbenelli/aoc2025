import org.jetbrains.kotlin.com.intellij.util.text.VersionComparatorUtil.VersionTokenType.SNAPSHOT

plugins {
    kotlin("jvm") version "2.1.10"
}

group = "dev.benelli"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    //implementation("com.github.holgerbrandl:kortools:master-SNAPSHOT")
    implementation("com.google.ortools:ortools-java:9.9.3963")
    
    //implementation("com.github.holgerbrandl:kortools:master-SNAPSHOT")
    
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}