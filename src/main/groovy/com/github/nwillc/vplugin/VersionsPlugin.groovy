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

import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency

import java.util.regex.Matcher
import java.util.regex.Pattern

class VersionsPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.task('versions') << {
            def checked = [:]
            println sprintf('\n%-40s%20s%20s', 'Dependency', 'Using', 'Update')
            println sprintf('%-40s%20s%20s', '----------', '-----', '------')
            project.configurations.each { Configuration configuration ->
                configuration.allDependencies.each { Dependency dependency ->
                    def version = dependency.version
                    if (!version.contains('SNAPSHOT') && !checked[dependency]) {
                        def group = dependency.group
                        def path = group.replace('.', '/')
                        def name = dependency.name
                        def url = "http://repo1.maven.org/maven2/$path/$name/maven-metadata.xml"
                        try {
                            def metadata = new XmlSlurper().parseText(url.toURL().text)
                            def versions = metadata.versioning.versions.version.collect { it.text() }
                            versions.removeAll { it.toLowerCase().contains('alpha') }
                            versions.removeAll { it.toLowerCase().contains('beta') }
                            versions.removeAll { it.toLowerCase().contains('rc') }
                            def newest = versions.last()
                            if (match(version,newest.toString())){
                               println sprintf('%-40s%20s',"$group:$name", newest)
                            } else {
                                println sprintf('%-40s%20s ->%17s',"$group:$name",version, newest)
                            }
                        } catch (FileNotFoundException e) {
                            project.logger.debug "Unable to download $url: $e.message"
                        } catch (org.xml.sax.SAXParseException e) {
                            project.logger.debug "Unable to parse $url: $e.message"
                        }
                        checked[dependency] = true
                    }
                }
            }

        }
    }

    public static boolean match(String patternString, String matcherString) {
        final Pattern pattern = Pattern.compile(patternString);
        final Matcher matcher = pattern.matcher(matcherString);
        return matcher.find();
    }
}
