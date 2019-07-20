[![license](https://img.shields.io/github/license/nwillc/vplugin.svg)](https://tldrlegal.com/license/-isc-license)
[![Travis](https://img.shields.io/travis/nwillc/vplugin.svg)](https://travis-ci.org/nwillc/vplugin)
[![Gradle Plugins](https://img.shields.io/badge/Gradle-Plugin-green.svg)](https://plugins.gradle.org/plugin/com.github.nwillc.vplugin)
------

# vplugin

A Gradle plugin to report newer versions of a builds dependencies. Traverses your plugin,
compile and runtime dependencies. For each dependency, all of your declared repositories are
checked, and the highest version is found. A text report is generated showing the dependencies, 
their current version, and higher ones if available.

## Use

See [Gradle Plugins](https://plugins.gradle.org/plugin/com.github.nwillc.vplugin).

## Configuring the Plugin

The plugin requires no configuration it relies on the existing repos and dependencies.

## Running

```bash
./gradlew versions

Plugins
=======
Repositories
------------
        https://plugins.gradle.org/m2/

Artifact                                                                            Using          Update
--------                                                                            -----          ------
com.github.ngyewch.git-version:com.github.ngyewch.git-version.gradle.plugin         0.2
com.github.nwillc.vplugin:com.github.nwillc.vplugin.gradle.plugin                   2.3.0
com.jfrog.bintray:com.jfrog.bintray.gradle.plugin                                   1.8.4
io.gitlab.arturbosch.detekt:io.gitlab.arturbosch.detekt.gradle.plugin               1.0.0.RC9.2
org.jetbrains.dokka:org.jetbrains.dokka.gradle.plugin                               0.9.17
org.jetbrains.kotlin.jvm:org.jetbrains.kotlin.jvm.gradle.plugin                     1.3.11
org.jmailen.kotlinter:org.jmailen.kotlinter.gradle.plugin                           1.20.1

Dependencies
============
Repositories
------------
        https://jcenter.bintray.com/

Artifact                                                                            Using              Update
--------                                                                            -----              ------
io.mockk:mockk                                                                      1.8.13.kotlin13
org.assertj:assertj-core                                                            3.11.1
org.jetbrains.kotlin:kotlin-scripting-compiler-embeddable                           1.3.11
org.junit.jupiter:junit-jupiter-api                                                 5.3.1              5.3.2
org.junit.jupiter:junit-jupiter-engine                                              5.3.1              5.3.2
```

## Implementation Note

Since 3.0.0+ implemented in Kotlin.

