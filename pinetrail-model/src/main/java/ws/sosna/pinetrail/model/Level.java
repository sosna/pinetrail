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
 *
 */
package ws.sosna.pinetrail.model;

/**
 * The difficulty level of the trail.
 *
 * @author Xavier Sosnovsky
 */
public enum Level {

    /**
     * An easy trail, suitable for beginners and families.
     */
    BEGINNER,
    /**
     * A trail suitable for people with average physical condition.
     */
    INTERMEDIATE,
    /**
     * A physically demanding trail.
     */
    ADVANCED;

    private static final int LOWER_BOUNDARY = 6;
    private static final int UPPER_BOUNDARY = 10;

    /**
     * Get the level from the trail difficulty rating.
     *
     * @param rating the trail difficulty rating
     *
     * @return the trail level
     */
    public static Level getLevelFromRating(final Integer rating) {
        final Level level;
        if (rating < LOWER_BOUNDARY) {
            level = Level.BEGINNER;
        } else if (rating >= UPPER_BOUNDARY) {
            level = Level.ADVANCED;
        } else {
            level = Level.INTERMEDIATE;
        }
        return level;
    }
}
