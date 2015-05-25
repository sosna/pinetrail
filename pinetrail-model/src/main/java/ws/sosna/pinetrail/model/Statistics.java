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
import java.util.Set;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

/**
 * Provides statistics about the trail for a particular aspect, such as
 * distance, elevation or speed.
 *
 * @author Xavier Sosnovsky
 */
public interface Statistics extends Serializable  {

    /**
     * Get the statistics for all the points in the trail.
     *
     * @return the statistics for all the points in the trail
     */
    SummaryStatistics getAll();

    /**
     * Get the statistics for all the points in the trail where the person
     * recording the trail was considered to be in movement.
     *
     * @return the statistics for all the active points in the trail
     */
    SummaryStatistics getActive();

    /**
     * Get the statistics for all the points in the trail where the person
     * recording the trail was considered to be going up.
     *
     * @return the statistics for all the active points going up in the trail
     */
    SummaryStatistics getActiveUp();

    /**
     * Get the statistics for all the points in the trail where the person
     * recording the trail was considered to be going down.
     *
     * @return the statistics for all the active points going down in the trail
     */
    SummaryStatistics getActiveDown();

    /**
     * Get the statistics for all the points in the trail where the person
     * recording the trail was considered to be going neither up nor down.
     *
     * @return the statistics for all the active in the trail that are neither
     * up nor down
     */
    SummaryStatistics getActiveFlat();

    /**
     * Get the points that could be considered outliers in the trail.
     *
     * @return the points that could be considered outliers in the trail
     */
    Set<Waypoint> getOutliers();
}
