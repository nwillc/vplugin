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

import com.google.common.annotations.VisibleForTesting
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.repositories.ArtifactRepository
import org.xml.sax.SAXParseException

import java.util.regex.Matcher
import java.util.regex.Pattern

class VersionsPlugin implements Plugin<Project> {
    private static final String LONG_PAD = "                                                       "
    private static final String SHORT_PAD = "                    "

    void apply(Project project) {
        project.task('versions') << {
            def urls = repoUrls(project)
            def checked = [:]
            println "Dependency" + LONG_PAD.substring("Dependency".length()) + SHORT_PAD.substring("Using".length()) + "Using" + SHORT_PAD.substring("Update".length()) + "Update"
            println "----------" + LONG_PAD.substring("----------".length()) + SHORT_PAD.substring("-----".length()) + "-----" + SHORT_PAD.substring("------".length()) + "------"
            project.configurations.each { Configuration configuration ->
                configuration.allDependencies.each { Dependency dependency ->
                    if (dependency.version != null && !dependency.version.contains('SNAPSHOT') && !checked[dependency]) {
                        def maxVersion = dependency.version
                        for (String url : urls) {
                            def newest = latest(url, dependency.group, dependency.name)
                            if (newest != null && newest > maxVersion) {
                                maxVersion = newest
                            }
                        }
                        if (maxVersion != null) {
                            def name = "$dependency.group:$dependency.name"
                            print name + LONG_PAD.substring(name.length()) + SHORT_PAD.substring(dependency.version.length()) + dependency.version
                            if (!match(dependency.version, maxVersion)) {
                                print " ->" + SHORT_PAD.substring(maxVersion.length() + 3) + maxVersion
                            }
                            println ""
                        }
                        checked[dependency] = true
                    }
                }
            }
        }
    }

    private static String[] repoUrls(Project project) {
        def urls = []
        println 'Searching repositories:'
        for (ArtifactRepository repo : project.repositories) {
            if (repo.hasProperty('url') && repo.url) {
                def url = repo.url.toString()
                if (url.startsWith('http') && !urls.contains(url)) {
                    println '\t' + repo.name + ' at ' + url
                    urls.add(url)
                }
            }
        }
        for (ArtifactRepository repo : project.buildscript.repositories) {
            if (repo.hasProperty('url') && repo.url) {
                def url = repo.url.toString()
                if (url.startsWith('http') && !urls.contains(url)) {
                    println '\t' + repo.name + ' at ' + url
                    urls.add(url)
                }
            }
        }
        println ''
        return urls
    }

    private static String latest(String url, String group, String name) {
        def path = group.replace('.', '/')
        def fullUrl = (url.endsWith("/") ? url : url + '/') + "$path/$name/maven-metadata.xml"
        try {
            def text = fullUrl.toURL().text
            if (!text.contains("<?xml")) {
                return null;
            }
            def metadata = new XmlSlurper().parseText(text)
            def latest = metadata.versioning.latest.text()
            if (latest.length() == 0) {
                def versions = metadata.versioning.versions.version.collect { it.text() }
                latest = versions.last()
            }
            return latest
        } catch (Exception ignored) {
        }

        return null
    }

    @VisibleForTesting
    static boolean match(String patternString, String matcherString) {
        final Pattern pattern = Pattern.compile(patternString)
        final Matcher matcher = pattern.matcher(matcherString)
        return matcher.find()
    }
}
