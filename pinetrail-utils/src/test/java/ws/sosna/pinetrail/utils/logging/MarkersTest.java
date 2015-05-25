/*
 * Copyright (c) 2014, Xavier Sosnovsky <xso@sosna.ws>
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH
 * REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT,
 * INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM
 * LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR
 * OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 * PERFORMANCE OF THIS SOFTWARE.
 */
package ws.sosna.pinetrail.utils.logging;

import org.junit.Test;
import static org.junit.Assert.*;
import org.slf4j.Marker;

/**
 * @author Xavier Sosnovsky
 */
public class MarkersTest {

    @Test
    public void getMarker() {
        final Markers audit =  Markers.valueOf("DB");
        final Marker marker = audit.getMarker();
        assertEquals("db", marker.getName());
    }

    @Test
    public void sameInstance() {
        final Marker marker1 = Markers.MODEL.getMarker();
        final Marker marker2 = Markers.MODEL.getMarker();
        assertSame(marker1, marker2);
    }
}
