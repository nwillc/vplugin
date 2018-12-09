/*
 * Copyright (c) 2016, nwillc@gmail.com
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

package com.github.nwillc.vplugin

import org.apache.maven.artifact.versioning.ComparableVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.ArtifactRepository

import java.util.logging.Level
import java.util.logging.Logger
import java.util.regex.Matcher
import java.util.regex.Pattern

class VersionsPlugin implements Plugin<Project> {
    private static final Logger LOGGER = Logger.getLogger(VersionsPlugin.class.getName())

    void apply(Project project) {
        project.task('versions').doLast() {
            try {
                println "\nPlugins"
                println "======="
                versions(project.buildscript.configurations, project.buildscript.repositories,
                        "https://plugins.gradle.org/m2/")
                println "\nDependencies"
                println "============"
                versions(project.configurations, project.repositories)
                print "\n"
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error: " + e, e)
            }
        }
    }

    private static void versions(
            ConfigurationContainer configurationContainer,
            RepositoryHandler repositoryHandler,
            String... defaultRepos) {
        def urls = repoUrls(repositoryHandler) + defaultRepos
        println 'Repositories'
        println '------------'
        for (def url : urls) {
            println "\t$url"
        }
        println ''
        def checked = [:]
        List<VersionInfo> versionInfoList = []

        def artifactPad = 80
        def versionPad = 10

        configurationContainer.each { Configuration configuration ->
            configuration.allDependencies.each { Dependency dependency ->
                if (dependency.version != null && !dependency.version.contains('SNAPSHOT') && !checked[dependency]) {
                    def maxVersion = new ComparableVersion(dependency.version)
                    for (String url : urls) {
                        def newest = latest(url, dependency.group, dependency.name)
                        if (newest != null) {
                            maxVersion = max(maxVersion, new ComparableVersion(newest))
                        }
                    }
                    if (maxVersion != null) {
                        def versionInfo = new VersionInfo()
                        versionInfo.artifact = "${dependency.group}:${dependency.name}"
                        versionInfo.version = dependency.version
                        versionInfo.available = match(dependency.version, maxVersion.toString()) ? "" : maxVersion
                        versionInfoList.add(versionInfo)
                        artifactPad = Math.max(artifactPad, versionInfo.artifact.length())
                        versionPad = Math.max(versionPad, versionInfo.version.length())
                    }
                    checked[dependency] = true
                }
            }
        }

        artifactPad += 4
        versionPad += 4

        println String.format("%-${artifactPad}s%-${versionPad}s%s", "Artifact", "Using", "Update")
        println String.format("%-${artifactPad}s%-${versionPad}s%s", "--------", "-----", "------")

        versionInfoList.sort { it.artifact }.each {
            println String.format("%-${artifactPad}s%-${versionPad}s%s",
                    it.artifact, it.version, it.available)
        }
    }

    private static String[] repoUrls(RepositoryHandler repositoryHandler) {
        def urls = []
        for (ArtifactRepository repo : repositoryHandler) {
            if (repo.hasProperty('url') && repo.url) {
                def url = repo.url.toString()
                if (url.startsWith('http') && !urls.contains(url)) {
                    urls.add(url)
                }
            }
        }
        return urls
    }

    private static String latest(String url, String group, String name) {
        def path = group.replace('.', '/')
        def fullUrl = (url.endsWith("/") ? url : url + '/') + "$path/$name/maven-metadata.xml"
        try {
            def text = fullUrl.toURL().text
            if (!text.contains("<?xml")) {
                return null
            }
            def metadata = new XmlSlurper().parseText(text)
            def latest = metadata.versioning.latest.text()
            if (latest.length() == 0) {
                def versions = metadata.versioning.versions.version.collect { it.text() }
                latest = versions.last()
            }
            return latest
        } catch (Exception ignored) {
            LOGGER.fine("Exception: " + ignored)
        }

        return null
    }

    // Visible for testing
    static ComparableVersion max(final ComparableVersion a, final ComparableVersion b) {
        return a > b ? a : b
    }

    // Visible for testing
    static boolean match(String patternString, String matcherString) {
        final Pattern pattern = Pattern.compile(patternString)
        final Matcher matcher = pattern.matcher(matcherString)
        return matcher.find()
    }
}
