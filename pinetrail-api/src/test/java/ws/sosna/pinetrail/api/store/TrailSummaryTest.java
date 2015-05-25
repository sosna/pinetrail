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
package ws.sosna.pinetrail.api.store;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;
import ws.sosna.pinetrail.model.Activity;
import ws.sosna.pinetrail.model.Coordinates;
import ws.sosna.pinetrail.model.CoordinatesBuilder;
import ws.sosna.pinetrail.model.Link;
import ws.sosna.pinetrail.model.Trail;
import ws.sosna.pinetrail.model.TrailBuilder;
import ws.sosna.pinetrail.model.Waypoint;
import ws.sosna.pinetrail.model.WaypointBuilder;

/**
 *
 * @author Xavier Sosnovsky
 */
public class TrailSummaryTest {

    /**
     * Test of of method, of class TrailSummaryBuilder.
     */
    @Test
    public void testOf() {
        final Waypoint point1 = newWaypoint(Instant.
            parse("2014-05-18T08:27:00Z"),
            newCoordinates(7.9631012119, 50.1183273643, 216.43), null, null,
            null);
        final Waypoint point2 = newWaypoint(Instant.
            parse("2014-05-18T08:27:02Z"),
            newCoordinates(7.9631012119, 50.1183273643, 216.43), null, null,
            null);
        final Waypoint point3 = newWaypoint(Instant.
            parse("2014-05-18T08:27:09Z"),
            newCoordinates(7.9631571192, 50.1184399333, 215.47), null, null,
            null);
        final Set<Waypoint> points = new LinkedHashSet<>();
        points.add(point1);
        points.add(point2);
        points.add(point3);
        final Set<String> countries = new LinkedHashSet<>();
        countries.add("DE");
        final Trail trail = newTrail(points, "trail", "trail desc", null, null,
            Activity.JOGGING, countries);
        final TrailSummary summary = new TrailSummaryBuilder(trail).build();
        assertNotNull(summary);
        assertNotNull(summary.getTrailId());
        assertEquals(Activity.JOGGING, summary.getActivity());
        assertEquals(countries, summary.getCountries());
        assertEquals(Instant.parse("2014-05-18T08:27:00Z"), summary.getDate());
        assertEquals(13.136472601561202, summary.getDistance(), 0.0);
        assertEquals(0.0, summary.getElevationDifference(), 0.0);
        assertTrue(0 == summary.getDifficultyRating());
        assertEquals(6.755900195088619, summary.getMovingSpeed(), 0.0);
        assertEquals("trail", summary.getName());
        assertEquals(0, summary.getRating());
    }
    
    @Test
    public void checkToString() {
        final Waypoint point1 = newWaypoint(Instant.
            parse("2014-05-18T08:27:00Z"),
            newCoordinates(7.9631012119, 50.1183273643, 216.43), null, null,
            null);
        final Waypoint point2 = newWaypoint(Instant.
            parse("2014-05-18T08:27:02Z"),
            newCoordinates(7.9631012119, 50.1183273643, 216.43), null, null,
            null);
        final Waypoint point3 = newWaypoint(Instant.
            parse("2014-05-18T08:27:09Z"),
            newCoordinates(7.9631571192, 50.1184399333, 215.47), null, null,
            null);
        final Set<Waypoint> points = new LinkedHashSet<>();
        points.add(point1);
        points.add(point2);
        points.add(point3);
        final Set<String> countries = new LinkedHashSet<>();
        countries.add("DE");
        final Trail trail = newTrail(points, "trail", "trail desc", null, null,
            Activity.JOGGING, countries);
        final TrailSummary summary = new TrailSummaryBuilder(trail).build();
        assertEquals("TrailSummary{" + "trailId=" + trail.getId() + ", "
            + "name=trail, date=2014-05-18T08:27:00Z, activity=JOGGING, "
            + "countries=" + countries +  ", distance=13.1m, "
            + "elevationDifference=0.0, difficulty rating=0, movingSpeed=6.8km/h, "
            + "rating=0}", summary.toString());
    }
    
    @Test
    public void checkEquals() {
        final Waypoint point1 = newWaypoint(Instant.
            parse("2014-05-18T08:27:00Z"),
            newCoordinates(7.9631012119, 50.1183273643, 216.43), null, null,
            null);
        final Waypoint point2 = newWaypoint(Instant.
            parse("2014-05-18T08:27:02Z"),
            newCoordinates(7.9631012119, 50.1183273643, 216.43), null, null,
            null);
        final Waypoint point3 = newWaypoint(Instant.
            parse("2014-05-18T08:27:09Z"),
            newCoordinates(7.9631571192, 50.1184399333, 215.47), null, null,
            null);
        final Set<Waypoint> points = new LinkedHashSet<>();
        points.add(point1);
        points.add(point2);
        points.add(point3);
        final Set<String> countries = new LinkedHashSet<>();
        countries.add("DE");
        final Trail trail1 = newTrail(points, "trail", "trail desc", null, null,
            Activity.JOGGING, countries);
        final Trail trail2 = TrailBuilder.of(trail1).build();
        final Trail trail3 = newTrail(points, "trail", "trail desc", null, null,
            Activity.BIKING, countries);
        final TrailSummary summary1 = new TrailSummaryBuilder(trail1).build();
        final TrailSummary summary2 = new TrailSummaryBuilder(trail2).build();
        final TrailSummary summary3 = new TrailSummaryBuilder(trail3).build();
        assertEquals(summary1, summary2);
        assertFalse(summary1.equals(summary3));
        assertFalse(summary1.equals("type"));
        assertFalse(summary1.equals(null));
    }
    
    @Test
    public void checkhashCode() {
        final Waypoint point1 = newWaypoint(Instant.
            parse("2014-05-18T08:27:00Z"),
            newCoordinates(7.9631012119, 50.1183273643, 216.43), null, null,
            null);
        final Waypoint point2 = newWaypoint(Instant.
            parse("2014-05-18T08:27:02Z"),
            newCoordinates(7.9631012119, 50.1183273643, 216.43), null, null,
            null);
        final Waypoint point3 = newWaypoint(Instant.
            parse("2014-05-18T08:27:09Z"),
            newCoordinates(7.9631571192, 50.1184399333, 215.47), null, null,
            null);
        final Set<Waypoint> points = new LinkedHashSet<>();
        points.add(point1);
        points.add(point2);
        points.add(point3);
        final Set<String> countries = new LinkedHashSet<>();
        countries.add("DE");
        final Trail trail1 = newTrail(points, "trail", "trail desc", null, null,
            Activity.JOGGING, countries);
        final Trail trail2 = TrailBuilder.of(trail1).build();
        final Trail trail3 = newTrail(points, "trail", "trail desc", null, null,
            Activity.BIKING, countries);
        final TrailSummary summary1 = new TrailSummaryBuilder(trail1).build();
        final TrailSummary summary2 = new TrailSummaryBuilder(trail2).build();
        final TrailSummary summary3 = new TrailSummaryBuilder(trail3).build();
        assertEquals(summary1.hashCode(), summary2.hashCode());
        assertFalse(summary1.hashCode() == summary3.hashCode());
    }

    private Coordinates newCoordinates(final Double longitude,
        final Double latitude, final Double elevation) {
        return new CoordinatesBuilder(longitude, latitude).elevation(elevation).
            build();
    }

    private Waypoint newWaypoint(final Instant time,
        final Coordinates coordinates, final String name,
        final String description, final Set<Link> links) {
        return new WaypointBuilder(time, coordinates).name(name).description(
            description).links(links).build();

    }

    private Trail newTrail(final Set<Waypoint> points, final String name,
        final String description, final Set<Link> links,
        final Integer difficultyRating,
        final Activity activity, final Set<String> countries) {
        return new TrailBuilder(name, points).description(description).
            links(links).difficultyRating(difficultyRating).activity(activity).countries(countries).
            build();
    }
}
