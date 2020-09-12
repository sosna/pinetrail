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
package ws.sosna.pinetrail.cli;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashSet;
import java.util.LongSummaryStatistics;
import java.util.Set;
import java.util.stream.Collectors;
import ws.sosna.pinetrail.model.Trail;
import ws.sosna.pinetrail.model.Waypoint;

/**
 * Writes basic information about a trail to a file.
 *
 * @author Xavier Sosnovsky
 */
final class TrailInfoWriter {

    private static final int M2KM = 1000;
    private static final double MS2KMH = 3.6;
    private static final double MIN2H = 60;
    private static final DecimalFormat DEC_FORMAT = new DecimalFormat("#0.0");

    TrailInfoWriter() {
        super();
    }

    void write(final Trail trail) {
        final StringBuilder bld = new StringBuilder();
        bld.append("\n===");
        bld.append("\nName: ");
        bld.append(trail.getName());
        bld.append("\n");
        bld.append("\n--- Overview ---");
        bld.append("\nNumber of points: ").append(trail.getWaypoints().
            size());
        final LongSummaryStatistics timeStats = trail.getWaypoints().
            stream().collect(Collectors.summarizingLong(p -> p.getTime().
                    getEpochSecond()));
        bld.append("\nPoints per minute: ").append(Math.round(
            trail.getWaypoints().size() / ((timeStats.getMax() - timeStats.
            getMin()) / MIN2H)));
        bld.append("\nPoints per kilometer: ").append(Math.round(trail.
            getWaypoints().size() / (trail.
            getStatistics().
            getDistanceSummary().getActive().getSum() / M2KM)));
        bld.append("\nActive points: ").append(trail.getWaypoints().
            stream().filter(Waypoint::isActive).
            collect(Collectors.toSet()).size());
        final Set<Waypoint> outliers = new LinkedHashSet<>();
        outliers.addAll(trail.getStatistics().getGradeSummary().
            getOutliers());
        outliers.addAll(trail.getStatistics().getSpeedSummary().
            getOutliers());
        final double outPerc = ((double) outliers.size() * 100.0)
            / (double) trail.getWaypoints().size();
        bld.append("\nNumber of outliers: ").append(outliers.size()).append(
            " (").append(DEC_FORMAT.format(outPerc)).
            append("%)");
        bld.append("\nCountries crossed by the trail: ").
            append(trail.getCountries());

        bld.append("\n--- Time ---");
        bld.append("\nStart: ").append(trail.getWaypoints().first().getTime());
        bld.append("\nEnd: ").append(trail.getWaypoints().last().getTime());
        bld.append("\nTotal: ").append(Duration.between(
            trail.getWaypoints().first().getTime(),
            trail.getWaypoints().last().getTime()));
        bld.append("\nMoving: ").append(Duration.of(((Double) trail.
            getStatistics().getTimeDifferenceSummary().getActive().getSum()).
            longValue(), ChronoUnit.SECONDS));
        bld.append("\nUp: ").append(Duration.of(((Double) trail.getStatistics().
            getTimeDifferenceSummary().getActiveUp().getSum()).longValue(),
            ChronoUnit.SECONDS));
        bld.append("\nDown: ").append(Duration.of(((Double) trail.
            getStatistics().getTimeDifferenceSummary().getActiveDown().getSum())
            .longValue(), ChronoUnit.SECONDS));
        bld.append("\nFlat: ").append(Duration.of(((Double) trail.
            getStatistics().getTimeDifferenceSummary().getActiveFlat().getSum())
            .longValue(), ChronoUnit.SECONDS));

        bld.append("\n--- Elevation ---");
        bld.append("\nMin: ").append(Math.round(trail.getStatistics().
            getElevationSummary().getActive().getMin())).append(" meters.");
        bld.append("\nMax: ").append(Math.round(trail.getStatistics().
            getElevationSummary().getActive().getMax())).append(" meters.");
        bld.append("\nNet: ").
            append(Math.round(trail.getStatistics().getElevationSummary().
                    getActive().getMax() - trail.getStatistics().
                    getElevationSummary().getActive().getMin())).
            append(" meters.");
        bld.append("\nUp: ").
            append(Math.round(trail.getStatistics().
                    getElevationDifferenceSummary().getActiveUp().getSum())).
            append(
                " meters.");
        bld.append("\nDown: ").
            append(Math.round(trail.getStatistics().
                    getElevationDifferenceSummary().getActiveDown().getSum())).
            append(
                " meters.");
        bld.append("\nOutliers (slope): ").
            append(trail.getStatistics().
                getGradeSummary().getOutliers().size());

        bld.append("\n--- Distance ---");
        bld.append("\nTotal: ").
            append(DEC_FORMAT.format(
                    trail.getStatistics().getDistanceSummary().getActive().
                    getSum() / M2KM)).append(" km.");
        bld.append("\nUp: ").
            append(DEC_FORMAT.format(trail.
                    getStatistics().getDistanceSummary().getActiveUp().
                    getSum() / M2KM)).append(" km.");
        bld.append("\nDown: ").
            append(DEC_FORMAT.format(trail.
                    getStatistics().getDistanceSummary().getActiveDown().
                    getSum() / M2KM)).append(" km.");
        bld.append("\nFlat: ").
            append(DEC_FORMAT.format(trail.
                    getStatistics().getDistanceSummary().getActiveFlat().
                    getSum() / M2KM)).append(" km.");

        bld.append("\n--- Speed ---");
        bld.append("\nMoving: ").append(DEC_FORMAT.format(
            formatSpeed(trail.getStatistics().getDistanceSummary().getActive()
                .getSum(), trail.getStatistics().getTimeDifferenceSummary()
                    .getActive().getSum()))).append(" km/h.");
        bld.append("\nUp: ").
            append(DEC_FORMAT.format(formatSpeed(trail
                .getStatistics().getDistanceSummary().getActiveUp().getSum(),
                trail.getStatistics().getTimeDifferenceSummary().getActiveUp().
                    getSum()))).append(" km/h.");
        bld.append("\nDown: ").
            append(DEC_FORMAT.format(formatSpeed(trail
                .getStatistics().getDistanceSummary().getActiveDown().getSum(),
                trail.getStatistics().getTimeDifferenceSummary()
                    .getActiveDown().getSum()))).append(" km/h.");
        bld.append("\nFlat: ").
            append(DEC_FORMAT.format(formatSpeed(trail
                .getStatistics().getDistanceSummary().getActiveFlat().getSum(),
                trail.getStatistics().getTimeDifferenceSummary()
                    .getActiveFlat().getSum()))).append(" km/h.");
        bld.append("\nMax: ").
            append(DEC_FORMAT.format(trail.
                    getStatistics().getSpeedSummary().getActive().getMax())).
            append(" km/h.");
        bld.append("\nOutliers: ").
            append(trail.getStatistics().
                getSpeedSummary().getOutliers().size());
        System.out.println(bld.toString());
    }

    private double formatSpeed(final double distance, final double time) {
        return 0 < time ? (distance / time) * MS2KMH : 0;
    }
}
