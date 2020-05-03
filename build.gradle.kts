/*
 * Copyright (c) 2020, nwillc@gmail.com
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
 */

import com.gradle.publish.PublishTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        jcenter()
    }
}

plugins {
    `java-gradle-plugin`
    `maven-publish`
    Dependencies.plugins.forEach { (n, v) -> id(n) version v }
}

group = Constants.group
version = Constants.version

logger.lifecycle("${project.name} $version")

repositories {
    jcenter()
    mavenLocal()
}

dependencies {
    Dependencies.artifacts(
        "org.apache.maven:maven-artifact",
        "org.jetbrains.kotlin:kotlin-stdlib-jdk8"
    ) { implementation(it) }

    Dependencies.artifacts(
        "org.assertj:assertj-core",
        "org.junit.jupiter:junit-jupiter"
    ) { testImplementation(it) }
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
            id = "$group.${project.name}"
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
        kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
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

ktlint {
    version.set(ToolVersions.ktlint)
    disabledRules.set(setOf("import-ordering"))
}
