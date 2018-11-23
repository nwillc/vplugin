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
========
Repositories
------------
        https://plugins.gradle.org/m2/

Artifact                                                         Using               Update
--------                                                         -----               ------
com.gradle.plugin-publish:com.gradle.plugin-publish.gradle.plugin 0.9.6
com.github.nwillc.vplugin:com.github.nwillc.vplugin.gradle.plugin 2.1.1              2.2.2

Dependencies
=============
Repositories
------------
        https://jcenter.bintray.com/

Artifact                                                         Using               Update
--------                                                         -----               ------
org.apache.maven:maven-artifact                                  3.6.0
org.junit.jupiter:junit-jupiter-api                              5.3.1
org.assertj:assertj-core                                         3.11.1
org.junit.jupiter:junit-jupiter-engine                           5.3.1

```

-----

[![license](https://img.shields.io/github/license/nwillc/vplugin.svg)](https://tldrlegal.com/license/-isc-license)
[![Travis](https://img.shields.io/travis/nwillc/vplugin.svg)](https://travis-ci.org/nwillc/vplugin)
[![Gradle Plugins](https://img.shields.io/badge/Gradle-Plugin-green.svg)](https://plugins.gradle.org/plugin/com.github.nwillc.vplugin)

