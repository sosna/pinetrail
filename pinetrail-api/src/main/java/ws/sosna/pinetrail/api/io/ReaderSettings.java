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
 * Describes the work to be done by a reader.
 *
 * @author Xavier Sosnovsky
 */
public interface ReaderSettings {

    /**
     * Whether subtrails should be grouped into one {@code Trail}.
     *
     * <p>
     * Certain formats allow intermediary groupings between the {@code Trail}
     * and the {@code Waypoints}. Gpx for example has an additional level,
     * called segments between tracks and waypoints.
     *
     * <p>
     * If true, these intermediary levels will be merged into one {@code Trail}.
     * If false, each of these levels will appear as a separate {@code Trail}.
     *
     * <p>
     * Defauls to false.
     *
     * @return whether subtrails should be grouped
     */
    boolean groupSubTrails();
}
