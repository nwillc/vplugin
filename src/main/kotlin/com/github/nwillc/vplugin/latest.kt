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

package com.github.nwillc.vplugin

import java.net.URL

fun latest(url: String, group: String, artifact: String): String? =
    try {
        val groupPath = group.replace('.', '/')
        val properUrl = if (url.endsWith("/")) url else {
            "$url/"
        }
        val metaDataUrl = "$properUrl$groupPath/$artifact/maven-metadata.xml"
        val text = URL(metaDataUrl).readText()
        val xmlDoc = text.toXmlDoc()
        val latest = xmlDoc.xPathValue("/metadata/versioning/latest")
        if (latest != null)
            latest
        else {
            val versions = xmlDoc.xPathNodeList("/metadata/versioning/versions/version")
            versions?.item(versions.length - 1)?.textContent
        }
    } catch (e: Exception) {
        null
    }
