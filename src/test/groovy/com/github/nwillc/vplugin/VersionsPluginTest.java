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

package com.github.nwillc.vplugin;

import org.apache.maven.artifact.versioning.ComparableVersion;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static com.github.nwillc.vplugin.VersionsPlugin.match;

class VersionsPluginTest {

    @Test
    void testMatcherTrue() throws Exception {
        assertThat(match("4.1.1", "4.1.1")).isTrue();
        assertThat(match("4.+", "4.11")).isTrue();
    }

    @Test
    void testMatcherFalse() throws Exception {
        assertThat(VersionsPlugin.match("4.+", "3.9")).isFalse();
    }

    @Test
    void testMax() throws Exception {
        ComparableVersion a, b;

        a = new ComparableVersion("1.0.1");
        b = new ComparableVersion("1.0.0");
        assertThat(VersionsPlugin.max(a,b)).isEqualTo(a);

        a = new ComparableVersion("1.0.0");
        b = new ComparableVersion("1.1.0");
        assertThat(VersionsPlugin.max(a,b)).isEqualTo(b);

        a = new ComparableVersion("1.0.1-rc1");
        b = new ComparableVersion("1.0.0");
        assertThat(VersionsPlugin.max(a,b)).isEqualTo(a);

        a = new ComparableVersion("1.0.1");
        b = new ComparableVersion("1.0.10");
        assertThat(VersionsPlugin.max(a,b)).isEqualTo(b);
    }
}
