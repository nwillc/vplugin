/*
 * Copyright (c) 2014, nwillc@gmail.com
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

package com.github.nwillc.vplugin

import com.google.common.annotations.VisibleForTesting
import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.repositories.ArtifactRepository
import org.xml.sax.SAXParseException

import java.util.regex.Matcher
import java.util.regex.Pattern

class VersionsPlugin implements Plugin<Project> {
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone()
    }

    @Override
    int hashCode() {
        return super.hashCode()
    }

    void apply(Project project) {
        project.task('versions') << {
            def urls = repoUrls(project)
            def checked = [:]
            println sprintf('%-40s%20s%20s', 'Dependency', 'Using', 'Update')
            println sprintf('%-40s%20s%20s', '----------', '-----', '------')
            project.configurations.each { Configuration configuration ->
                configuration.allDependencies.each { Dependency dependency ->
                    if (!dependency.version.contains('SNAPSHOT') && !checked[dependency]) {
                        for (String url : urls) {
                            def newest = latest(url, dependency.group, dependency.name)
                            if (newest != null) {
                                if (match(dependency.version, newest.toString())){
                                    println sprintf('%-40s%20s',"$dependency.group:$dependency.name", newest)
                                } else {
                                    println sprintf('%-40s%20s ->%17s',"$dependency.group:$dependency.name",dependency.version, newest)
                                }
                                break
                            }
                        }

                        checked[dependency] = true
                    }
                }
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize()
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
        def path = group.replace('.','/')
        def fullUrl = "$url/$path/$name/maven-metadata.xml"
        try {
            def metadata = new XmlSlurper().parseText(fullUrl.toURL().text)
            def latest = metadata.versioning.latest.text()
            if (latest.length() == 0) {
                def versions = metadata.versioning.versions.version.collect { it.text() }
                latest = versions.last();
            }
            return latest
        } catch (FileNotFoundException ignored) {
        } catch (SAXParseException e) {
            println "Unable to parse $url: $e.message"
        }

        return null;
    }

    @VisibleForTesting
    public static boolean match(String patternString, String matcherString) {
        final Pattern pattern = Pattern.compile(patternString);
        final Matcher matcher = pattern.matcher(matcherString);
        return matcher.find();
    }
}
