import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.10"
    kotlin("plugin.serialization") version ("1.6.0")
    application
    `java-library`
    `maven-publish`
}

group = "com.zenasoft"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    gradlePluginPortal() // required by the GraalVM plugin
}

dependencies {
    val kotlinVersion = "1.9.10"
    val kotlinxCoroutinesVersion = "1.7.3"
    val kotlinxSerializationVersion = "1.6.0"

    val jasyptVersion = "1.9.3"
    val kamlVersion = "0.55.0"
    val koinVersion = "3.4.3"
    val ktorVersion = "2.3.4"
    val yamlVersion = "2.15.2"
    val okHttpVersion = "4.11.0"

    testImplementation(kotlin("test"))

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${kotlinxCoroutinesVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${kotlinxCoroutinesVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${kotlinxSerializationVersion}")

    // Logging
    // Don't use janino because it will cause the "Error: Could not find or load main class MainKt" error in the fatJar.
    implementation("io.github.microutils:kotlin-logging:3.0.5")
    implementation("ch.qos.logback:logback-classic:1.4.11")

    // Dependency injection
    implementation("io.insert-koin:koin-core:${koinVersion}")

    // Jasypt
    implementation("org.jasypt:jasypt:${jasyptVersion}")

    // Kaml (Yaml extension for KSerializer)
    implementation("com.charleskorn.kaml:kaml:${kamlVersion}")

    // Ktor
    implementation("io.ktor:ktor-server-core:${ktorVersion}")
//    implementation("io.ktor:ktor-server-netty:${ktorVersion}")
    implementation("io.ktor:ktor-server-cio:${ktorVersion}")
    implementation("io.ktor:ktor-server-content-negotiation:${ktorVersion}")
    implementation("io.ktor:ktor-serialization-kotlinx-json:${ktorVersion}")
    implementation("io.netty:netty-all:4.1.98.Final")

    // OkHttp
    implementation("com.squareup.okhttp3:okhttp:${okHttpVersion}")

    // Serde
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:${yamlVersion}")
    implementation("com.fasterxml.jackson.core:jackson-databind:${yamlVersion}")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${yamlVersion}")

    // Selenium
    implementation("org.seleniumhq.selenium:selenium-java:4.5.0")
    implementation("org.seleniumhq.selenium:selenium-edge-driver:4.5.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

tasks {
    val fatJar = register<Jar>("fatJar") {
        dependsOn.addAll(
            listOf(
                "compileJava",
                "compileKotlin",
                "processResources"
            )
        ) // We need this for Gradle optimization to work
        archiveClassifier.set("standalone") // Naming the jar
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest {
            attributes(
                mapOf(
                    "Main-Class" to application.mainClass,
                )
            )
        } // Provided we set it up in the application plugin configuration
        val sourcesMain = sourceSets.main.get()
        val contents = configurations.runtimeClasspath.get()
            .map { if (it.isDirectory) it else zipTree(it) } +
                sourcesMain.output
        from(contents)
    }
    build {
        dependsOn(fatJar)
    }
}

application {
    mainClass.set("MainKt")
}

java {
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "auto-music-info-server"
            from(components["java"])
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom {
                name.set("Auto Music Info Server")
                description.set("A Kotlin server that makes use of both rules and AI power to collect information about music from the Internet.")
                url.set("https://www.github.com/czyczk/auto-music-info-server")
//                properties.set(mapOf(
//                    "myProp" to "value",
//                    "prop.with.dots" to "anotherValue"
//                ))
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("czyczk")
                        name.set("Zenas Chen")
                        email.set("czyczk@qq.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://www.github.com/auto-music-info-server.git")
                    developerConnection.set("scm:git:ssh://www.github.com/auto-music-info-server.git")
                    url.set("https://www.github.com/czyczk/auto-music-info-server/")
                }
            }
        }
    }
    repositories {
        maven {
            val releasesRepoUrl = uri(layout.buildDirectory.dir("repositories/releases"))
            val snapshotsRepoUrl = uri(layout.buildDirectory.dir("repositories/snapshots"))
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
        }
    }
}