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
package ws.sosna.pinetrail.api.io;

/**
 * A list of formats supplying information about {@code Trail}s
 * and {@code Waypoint}s.
 *
 * @author Xavier Sosnovsky
 */
public enum Formats {
    /**
     * Version 1.0 of the GPS Exchange Format standard, maintained by
     * TopoGrafix.
     */
    GPX_1_0,
    /**
     * Version 1.1 of the GPS Exchange Format standard, maintained by
     * TopoGrafix.
     */
    GPX_1_1,
    /**
     * A geospatial data interchange format based on JavaScript Object Notation.
     */
    GEOJSON_1_0,
    /**
     * Version 2.1.0 of the Keyhole Markup Language, an international standard
     * of the Open Geospatial Consortium, developed by Keyhole Inc and Google.
     */
    KML_2_1_0,
    /**
     * Version 2.2.0 of the Keyhole Markup Language, an international standard
     * of the Open Geospatial Consortium, developed by Keyhole Inc and Google.
     */
    KML_2_2_0
}
