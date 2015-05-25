/*
 * Copyright (c) 2015, Xavier Sosnovsky <xso@sosna.ws>
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
 * Interface describing the customisation options of writers.
 *
 * @author Xavier Sosnovsky
 */
public interface WriterSettings {

    /**
     * Whether to allow overwriting existing files. Defaults to true.
     *
     * @return whether to allow overwriting existing files
     */
    boolean overwriteIfExists();

    /**
     * Whether the output should be nicely formatted. Defaults to false.
     *
     * @return whether the output should be nicely formatted
     */
    boolean prettyPrinting();

    /**
     * Whether abnormal values (aka outliers) should be written to the output
     * file. Defaults to false.
     *
     * @return whether outliers will be written to the output file
     */
    boolean writeOutliers();

    /**
     * Whether points labelled as inactive should be written to the output file.
     * Defaults to false.
     *
     * Inactive points are points where the subject was most likely not moving,
     * for example when interrupting the hike for a lunch break.
     *
     * @return whether inactive points will be written to the output file
     */
    boolean writeIdlePoints();
}
