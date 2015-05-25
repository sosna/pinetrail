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

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

/**
 * Compiles statistics about a trail.
 *
 * @author Xavier Sosnovsky
 */
enum StatisticsProvider implements Function<Set<Waypoint>, TrailStatistics> {

    /**
     * Singleton that returns an instance of a StatisticsProvider.
     */
    INSTANCE;

    private static final int OUTLIERS_BOUNDARY = 4;
    /**
     * The angle of the slope that acts as boundary between flat, uphill and
     * downhill segments.
     *
     * An angle of 2.9 degree is considered the maximum for wheelchair navigable
     * trails and is therefore considered as a good boundary value to describe
     * flat segments.
     */
    private static final double SLOPE_ANGLE = 2.9;

    /**
     * Compiles statistics about a trail.
     *
     * @param points the waypoints that make up the trail
     * @return the statistics about the trail
     */
    @Override
    public TrailStatistics apply(final Set<Waypoint> points) {
        if (points.isEmpty()) {
            return null;
        }
        final Set<Waypoint> activePoints = points.stream().
                filter(Waypoint::isActive).
                collect(Collectors.toCollection(LinkedHashSet::new));
        final Set<Waypoint> activeUp = activePoints.stream().
                filter(p -> p.getGrade() > SLOPE_ANGLE).
                collect(Collectors.toCollection(LinkedHashSet::new));
        final Set<Waypoint> activeEleUp = activePoints.stream().
                filter(p -> p.getElevationDifference() >= 0.0).
                collect(Collectors.toCollection(LinkedHashSet::new));
        final Set<Waypoint> activeDown = activePoints.stream().
                filter(p -> p.getGrade() < -SLOPE_ANGLE).
                collect(Collectors.toCollection(LinkedHashSet::new));
        final Set<Waypoint> activeEleDown = activePoints.stream().
                filter(p -> p.getElevationDifference() < 0.0).
                collect(Collectors.toCollection(LinkedHashSet::new));
        final Set<Waypoint> activeFlat = activePoints.stream().
                filter(p -> p.getGrade() <= SLOPE_ANGLE
                    && p.getGrade() >= -SLOPE_ANGLE).
                collect(Collectors.toCollection(LinkedHashSet::new));
        final Statistics eleDiffStats = computeStatistics(points, activePoints,
            activeEleUp, activeEleDown, Collections.emptySet(),
            this::getElevationDifference);
        final Statistics eleStats = computeStatistics(points, activePoints,
            activeEleUp, activeEleDown, Collections.emptySet(),
            this::getElevation);
        final Statistics distStats = computeStatistics(points, activePoints,
            activeUp, activeDown, activeFlat, this::getDistance);
        final Statistics speedStats = computeStatistics(points, activePoints,
            activeUp, activeDown, activeFlat, this::getSpeed);
        final Statistics timeStats = computeStatistics(points, activePoints,
            activeUp, activeDown, activeFlat, this::getTimeDifference);
        final Statistics gradeStats = computeStatistics(points, activePoints,
            activeUp, activeDown, activeFlat, this::getGrade);
        return new TrailStatisticsImpl(timeStats, distStats, eleStats,
            eleDiffStats, speedStats, gradeStats);
    }

    private Statistics computeStatistics(final Set<Waypoint> all,
        final Set<Waypoint> active, final Set<Waypoint> up,
        final Set<Waypoint> down, final Set<Waypoint> flat,
        final Function<Waypoint, Double> func) {
        final SummaryStatistics allStats = getSummary(all, func);
        final SummaryStatistics activeStats = getSummary(active, func);
        final SummaryStatistics upStats = getSummary(up, func);
        final SummaryStatistics downStats = getSummary(down, func);
        final SummaryStatistics flatStats = getSummary(flat, func);
        final Set<Waypoint> outliers = getOutliers(active, activeStats, func);
        return new StatisticsImpl(allStats, activeStats, upStats, downStats,
                flatStats, outliers);
    }

    private SummaryStatistics getSummary(final Set<Waypoint> points,
            final Function<Waypoint, Double> func) {
        final SummaryStatistics stats = new SummaryStatistics();
        points.stream().map(func).filter(i -> i != null).
            forEach(stats::addValue);
        return stats;
    }

    private double getElevationDifference(final Waypoint point) {
        return point.getElevationDifference();
    }

    private Double getElevation(final Waypoint point) {
        return point.getCoordinates().getElevation();
    }

    private double getGrade(final Waypoint point) {
        return point.getGrade();
    }

    private double getSpeed(final Waypoint point) {
        return point.getSpeed();
    }

    private double getDistance(final Waypoint point) {
        return point.getDistance();
    }

    private double getTimeDifference(final Waypoint point) {
        return point.getTimeDifference();
    }

    private Set<Waypoint> getOutliers(final Set<Waypoint> points,
            final SummaryStatistics stats,
            final Function<Waypoint, Double> func) {
        return points.stream().filter(p -> func.apply(p) != null).filter(
            p -> Math.abs((func.apply(p) - stats.getMean()) / stats.
                getStandardDeviation())
            > OUTLIERS_BOUNDARY).collect(Collectors.toSet());
    }
}
