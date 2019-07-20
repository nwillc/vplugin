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
val assertjVersion = "3.12.2"
val jupiterVersion = "5.5.1"

buildscript {
    repositories {
        jcenter()
        mavenLocal()
    }
    dependencies {
//        classpath("com.github.nwillc:vplugin:3.0.0-SNAPSHOT")
    }
}

plugins {
    kotlin("jvm") version "1.3.41"
    `java-gradle-plugin`
    maven
    id("com.gradle.plugin-publish") version "0.10.1"
    id("org.jetbrains.dokka") version "0.9.18"
}

//apply(plugin = "com.github.nwillc.vplugin")

group = "com.github.nwillc"
version = "3.0.1-SNAPSHOT"

logger.lifecycle("${project.name} $version")

repositories {
    jcenter()
    mavenLocal()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.apache.maven:maven-artifact:3.6.1")

    testImplementation("org.junit.jupiter:junit-jupiter:$jupiterVersion")
    testImplementation("org.assertj:assertj-core:$assertjVersion")
}

val sourcesJar by tasks.registering(Jar::class) {
    classifier = "sources"
    from(sourceSets["main"].allSource)
}

val javadocJar by tasks.registering(Jar::class) {
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
