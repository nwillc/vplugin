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

import com.gradle.publish.PublishTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val jvmTargetVersion = JavaVersion.VERSION_1_8.toString()
val assertJVersion = "3.15.0"
val jupiterVersion = "5.6.1"
val mavenArtifactVersion = "3.6.3"

buildscript {
    repositories {
        jcenter()
        mavenLocal()
    }
}

plugins {
    kotlin("jvm") version "1.3.72"
    `java-gradle-plugin`
    `maven-publish`
    id("com.gradle.plugin-publish") version "0.11.0"
    id("org.jetbrains.dokka") version "0.10.1"
    id("com.github.nwillc.vplugin") version "3.0.3"
}

group = "com.github.nwillc"
version = "3.0.4"

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
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

val javadocJar by tasks.registering(Jar::class) {
    duplicatesStrategy = DuplicatesStrategy.FAIL
    dependsOn("dokka")
    archiveClassifier.set("javadoc")
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
        useJUnitPlatform()
        testLogging {
            showStandardStreams = true
            events("passed", "failed", "skipped")
        }
    }
    withType<PublishTask> {
        val key = System.getenv("GRADLE_PUBLISH_KEY")
          val secret = System.getenv("GRADLE_PUBLISH_SECRET")
           onlyIf {
               if (project.version.toString().contains('-')) {
                   logger.lifecycle("Version ${project.version} is not a release version - skipping upload.")
                   false
               } else {
                   true
               }
           }
       }
}
