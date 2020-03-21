/*
 * Copyright (c) 2019, nwillc@gmail.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *
 */

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val jvmTargetVersion = JavaVersion.VERSION_1_8.toString()
val assertJVersion = "3.15.0"
val jupiterVersion = "5.6.0"
val mavenArtifactVersion = "3.6.3"

buildscript {
    repositories {
        jcenter()
        mavenLocal()
    }
}

plugins {
    kotlin("jvm") version "1.3.70"
    `java-gradle-plugin`
    `maven-publish`
    id("com.gradle.plugin-publish") version "0.10.1"
    id("org.jetbrains.dokka") version "0.10.1"
    id("com.github.nwillc.vplugin") version "3.0.1"
}

group = "com.github.nwillc"
version = "3.0.2"

logger.lifecycle("${project.name} $version")

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.apache.maven:maven-artifact:$mavenArtifactVersion")

    testImplementation("org.junit.jupiter:junit-jupiter:$jupiterVersion")
    testImplementation("org.assertj:assertj-core:$assertJVersion")
}

val sourcesJar by tasks.registering(Jar::class) {
    duplicatesStrategy = DuplicatesStrategy.FAIL
    classifier = "sources"
    from(sourceSets["main"].allSource)
}

val javadocJar by tasks.registering(Jar::class) {
    duplicatesStrategy = DuplicatesStrategy.FAIL
    dependsOn("dokka")
    classifier = "javadoc"
    from("$buildDir/javadoc")
}

pluginBundle {
    website = "https://github.com/nwillc/vplugin/"
    vcsUrl = "https://github.com/nwillc/vplugin"
    tags = listOf("dependencies", "versions")
}

gradlePlugin {
    plugins {
        create("versionsPlugin") {
            id = "${group}.${project.name}"
            displayName = "Gradle versions plugin"
            description = """Gradle plugin to report newer versions of dependencies. Traverses your buildscript,
compile and runtime dependencies. For each dependency, all of your declared repositories are
checked, and the highest version is found. A text report is generated showing the dependencies,
their current version, and higher ones if available.
"""
            implementationClass = "com.github.nwillc.vplugin.VersionsPlugin"
        }
    }
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = jvmTargetVersion
    }
    withType<Test> {
        useJUnitPlatform {
            includeEngines = setOf("junit-jupiter")
        }
    }
}
