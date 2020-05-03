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

package com.github.nwillc.vplugin

import org.apache.maven.artifact.versioning.ComparableVersion
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.RepositoryHandler

private const val COLUMN1_WIDTH = 80
private const val COLUMN1_PAD = 2
private const val COLUMN_DEFAULT_PAD = 15

fun versions(
    configurationContainer: ConfigurationContainer,
    repositoryHandler: RepositoryHandler,
    vararg defaultRepos: String
) {
    @SuppressWarnings("SpreadOperator")
    val urls = repositoryHandler.urls() + setOf(*defaultRepos)
    val versions = configurationContainer
        .asSequence()
        .map { configuration ->
            configuration.allDependencies
                .filter { it.version != null }
                .filterNot { it.version!!.contains("SNAPSHOT") }
        }
        .flatten()
        .distinctBy { it.id() }
        .map { dependency ->
            val maxBy = urls
                .mapNotNull { latest(it, dependency.group ?: "", dependency.name) }
                .maxBy { ComparableVersion(it) } ?: ""

            val update = if (maxBy != dependency.version) maxBy else ""
            VersionInfo(dependency.id(), dependency.version ?: "", update)
        }
        .toSet()
    report(urls, versions)
}

internal fun report(urls: Set<String>, versions: Set<VersionInfo>) {
    println("Repositories")
    println("------------")
    urls.forEach {
        println("\t$it")
    }
    println()

    val pad1 = (versions.map { it.artifact.length }.max() ?: COLUMN1_WIDTH) + COLUMN1_PAD
    val pad2 = (versions.map { it.using.length }.max() ?: 0) + COLUMN_DEFAULT_PAD
    val pad3 = (versions.map { it.update.length }.max() ?: 0) + COLUMN_DEFAULT_PAD

    println("Artifact".padEnd(pad1) + "Using".padStart(pad2) + "Update".padStart(pad3))
    println("--------".padEnd(pad1) + "-----".padStart(pad2) + "------".padStart(pad3))
    versions
        .sortedBy { it.artifact }
        .forEach {
            println(it.artifact.padEnd(pad1) + it.using.padStart(pad2) + it.update.padStart(pad3))
        }
}

internal fun Dependency.id(): String = this.group + ":" + this.name
