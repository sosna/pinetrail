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
package ws.sosna.pinetrail.model;

import java.io.Serializable;

/**
 * Provides time, distance, elevation, speed and grade statistics for the trail.
 *
 * @author Xavier Sosnovsky
 */
public interface TrailStatistics extends Serializable {

    /**
     * Get the time statistics (start time, end time, etc.) for the trail.
     *
     * @return the time statistics for the trail
     */
    Statistics getTimeDifferenceSummary();

    /**
     * Get the distance statistics (number of kilometers, etc.) for the trail.
     *
     * @return the distance statistics for the trail
     */
    Statistics getDistanceSummary();

    /**
     * Get the statistics about the differences in elevation between the points
     * of the trail.
     *
     * @return the statistics about the differences in elevation
     */
    Statistics getElevationDifferenceSummary();

    /**
     * Get the elevation statistics (highest and lowest points, total elevation,
     * etc.) for the trail.
     *
     * @return the elevation statistics for the trail
     */
    Statistics getElevationSummary();

    /**
     * Get the speed statistics (average moving speed, etc.) for the trail.
     *
     * @return the speed statistics for the trail
     */
    Statistics getSpeedSummary();

    /**
     * Get the grade statistics (steepness) for the trail.
     *
     * @return the grade statistics for the trail
     */
    Statistics getGradeSummary();
}
