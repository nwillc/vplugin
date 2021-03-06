
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
 *
 */

object Constants {
    const val group = "com.github.nwillc"
    const val version = "3.0.6-SNAPSOT"
}

object PluginVersions {
    const val detekt = "1.9.1"
    const val dokka = "0.10.1"
    const val kotlin = "1.3.72"
    const val ktlint = "9.2.1"
    const val pluginPublish = "0.11.0"
    const val vplugin = "3.0.5"
}

object ToolVersions {
    const val ktlint = "0.36.0"
}

object Versions {
    const val assertJ = "3.16.1"
    const val jupiter = "5.7.0-M1"
    const val mavenArtifact = "3.6.3"
}

object Dependencies {
    val plugins = mapOf(
        "com.github.nwillc.vplugin" to PluginVersions.vplugin,
        "com.gradle.plugin-publish" to PluginVersions.pluginPublish,
        "io.gitlab.arturbosch.detekt" to PluginVersions.detekt,
        "org.jetbrains.dokka" to PluginVersions.dokka,
        "org.jetbrains.kotlin.jvm" to PluginVersions.kotlin,
        "org.jlleitschuh.gradle.ktlint" to PluginVersions.ktlint
    )
    val artifacts = mapOf(
        "org.apache.maven:maven-artifact" to Versions.mavenArtifact,
        "org.assertj:assertj-core" to Versions.assertJ,
        "org.jetbrains.kotlin:kotlin-stdlib-jdk8" to PluginVersions.kotlin,
        "org.junit.jupiter:junit-jupiter" to Versions.jupiter
    )

    fun plugins(vararg keys: String, block: (Pair<String, String>) -> Unit) =
        keys
            .map { it to (plugins[it] ?: error("No plugin $it registered in Dependencies.")) }
            .forEach {
                block(it)
            }

    fun artifacts(vararg keys: String, block: (String) -> Unit) =
        keys
            .map { it to (artifacts[it] ?: error("No artifact $it registered in Dependencies.")) }
            .forEach { (n, v) ->
                block("$n:$v")
            }
}
