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

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File

class XmlExtKTTest {
    @Test
    fun `should read metadata`() {
        val xmlFile = File("src/test/resources/maven-metadata.xml")
        val doc = xmlFile.readText().toXmlDoc()
        val evaluate = doc.xPathValue("/metadata/versioning/latest")
        assertThat(evaluate).isNotNull()
        println(evaluate)
        val evaluate1 = doc.xPathNodeList("/metadata/versioning/versions/version")
        assertThat(evaluate1).isNotNull()
        if (evaluate1 != null) {
            for (i in 0 until evaluate1.length) {
                val node = evaluate1.item(i)
                println(node?.textContent)
            }
        }
    }
}
