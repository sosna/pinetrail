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

import java.time.Instant;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Xavier Sosnovsky
 */
public class ActivityTest {

    @Test
    public void defaultAverageSpeed() {
        final Activity hike = Activity.valueOf("HIKING");
        assertEquals(5.0, hike.getAverageSpeed(), 0.0);
    }

    @Test
    public void customAverageSpeed() {
        final Activity bike = Activity.BIKING;
        assertEquals(15.0, bike.getAverageSpeed(), 0.0);
        bike.setAverageSpeed(20.0);
        assertEquals(20.0, bike.getAverageSpeed(), 0.0);
    }

    @Test
    public void guessHikingLevel() {
        final Activity activity = Activity.HIKING;
        final Integer rating = activity.getDifficultyRating(
            Level.INTERMEDIATE, getStats().getDistanceSummary(), getStats().
            getElevationDifferenceSummary(), Instant.
            parse("2014-05-18T08:25:32Z"), Instant.parse(
                "2014-05-18T08:27:26Z"));
        assertTrue(8 == rating);
    }

    @Test
    public void guessHikingLevelExpert() {
        final Activity activity = Activity.HIKING;
        final Integer rating = activity.getDifficultyRating(
            Level.ADVANCED, getStats().getDistanceSummary(), getStats().
            getElevationDifferenceSummary(), Instant.
            parse("2014-05-18T08:25:32Z"), Instant.parse(
                "2014-05-18T08:27:26Z"));
        assertTrue(6 == rating);
    }

    @Test
    public void guessHikingLevelNovice() {
        final Activity activity = Activity.HIKING;
        final Integer rating = activity.getDifficultyRating(
            Level.BEGINNER, getStats().getDistanceSummary(), getStats().
            getElevationDifferenceSummary(), Instant.
            parse("2014-05-18T08:25:32Z"), Instant.parse(
                "2014-05-18T08:27:26Z"));
        assertTrue(12 == rating);
    }

    @Test
    public void guessLevelSeveralDays() {
        final Activity activity = Activity.HIKING;
        final Integer rating = activity.getDifficultyRating(
            Level.BEGINNER, getStats().getDistanceSummary(), getStats().
            getElevationDifferenceSummary(), Instant.
            parse("2014-05-18T08:25:32Z"), Instant.parse(
                "2014-05-19T08:27:26Z"));
        assertTrue(6 == rating);
    }

    private TrailStatistics getStats() {
        final Set<Waypoint> points
            = PointsAugmenter.INSTANCE.apply(getTestPoints());
        return StatisticsProvider.valueOf("INSTANCE").apply(points);
    }

    private SortedSet<Waypoint> getTestPoints() {
        final SortedSet<Waypoint> points = new TreeSet<>();
        points.add(getPoint(8.739481, 50.182336, 214.03,
            "2014-05-18T08:25:32Z"));
        points.add(getPoint(8.773227, 50.230132, 215.47,
            "2014-05-18T09:46:14Z"));
        points.add(getPoint(8.939867, 50.288352, 216.43,
            "2014-05-18T14:27:02Z"));
        return points;
    }

    private Waypoint getPoint(final double x, final double y, final double z,
        final String time) {
        return new WaypointBuilder(Instant.parse(time),
            new CoordinatesBuilder(x, y).elevation(z).build()).build();
    }
}
