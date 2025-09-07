plugins {
    kotlin("jvm")
    application
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(project(":spec"))
    implementation("org.xerial:sqlite-jdbc:3.46.1.0")
    runtimeOnly(project(":prvaImpl"))
    runtimeOnly(project(":drugaImpl"))
    runtimeOnly(project(":trecaImpl"))
    testImplementation(kotlin("test"))
}


application {
    mainClass.set("testApp.TestKt")
}

tasks.shadowJar {
    archiveClassifier.set("all")
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }
    mergeServiceFiles() // include meta-inf services files
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}