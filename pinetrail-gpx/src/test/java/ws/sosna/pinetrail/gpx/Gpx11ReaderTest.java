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
package ws.sosna.pinetrail.gpx;

import java.nio.file.FileSystems;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.prefs.Preferences;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import ws.sosna.pinetrail.api.io.Reader;
import ws.sosna.pinetrail.api.io.ReaderSettingsBuilder;
import ws.sosna.pinetrail.model.Trail;
import ws.sosna.pinetrail.model.Waypoint;
import ws.sosna.pinetrail.utils.error.ExecutionError;

/**
 * @author Xavier Sosnovsky
 */
public class Gpx11ReaderTest {

    private static Boolean keepOutliers;

    @BeforeClass
    public static void setup() {
        keepOutliers = Boolean.valueOf(Preferences.userRoot().node(
            "ws.sosna.pinetrail.model.Trail").get("keepOutliers", "false"));
        Preferences.userRoot().node(
            "ws.sosna.pinetrail.model.Trail").put("keepOutliers", "true");
    }

    @AfterClass
    public static void cleanup() {
        Preferences.userRoot().node(
            "ws.sosna.pinetrail.model.Trail").put("keepOutliers",
                keepOutliers.toString());
    }

    @Test
    public void noPoints() throws InterruptedException {
        final Reader reader = new Gpx11Reader();
        final Set<Trail> trails = reader.apply(FileSystems.
            getDefault().getPath(".",
                "src/test/resources/2014-05-18_Wispertal_NoPoint.gpx"));
        assertTrue(trails.isEmpty());
    }

    @Test
    public void noSegment() throws InterruptedException {
        final Reader reader = new Gpx11Reader();
        final Set<Trail> trails = reader.apply(FileSystems.
            getDefault().getPath(".",
                "src/test/resources/2014-05-18_Wispertal_NoSegment.gpx"));
        assertTrue(trails.isEmpty());
    }

    @Test
    public void noTrack() throws InterruptedException {
        final Reader reader = new Gpx11Reader();
        final Set<Trail> trails = reader.apply(FileSystems.
            getDefault().getPath(".",
                "src/test/resources/2014-05-18_Wispertal_NoTrack.gpx"));
        assertTrue(trails.isEmpty());
    }

    @Test
    public void twoSegments() throws InterruptedException {;
        final Reader reader = new Gpx11Reader();
        final Set<Trail> trails = reader.apply(FileSystems.
            getDefault().getPath(".",
                "src/test/resources/2014-05-18_Wispertal_TwoSegments.gpx"));
        assertEquals(2, trails.size());
        for (final Trail trail : trails) {
            if (null != trail.getName()) {
                switch (trail.getName()) {
                    case "Wispertal [1]": {
                        assertEquals(779, trail.getWaypoints().size());
                        final List<Waypoint> list = new ArrayList<>(trail.
                            getWaypoints());
                        final Waypoint first = list.get(0);
                        final Waypoint last = list.get(list.size() - 1);
                        assertEquals(Instant.parse("2014-05-18T08:25:32Z"),
                            first.getTime());
                        assertEquals(Double.parseDouble("50.1181208342"), first.
                            getCoordinates().getLatitude(), 0);
                        assertEquals(Double.parseDouble("7.9630853701"), first.
                            getCoordinates().getLongitude(), 0);
                        assertEquals(Instant.parse("2014-05-18T12:06:55Z"),
                            last.getTime());
                        assertEquals(Double.parseDouble("50.1181969419"), last.
                            getCoordinates().getLatitude(), 0);
                        assertEquals(Double.parseDouble("7.9630940873"), last.
                            getCoordinates().getLongitude(), 0);
                        break;
                    }
                    case "Wispertal [2]": {
                        assertEquals(1, trail.getWaypoints().size());
                        final List<Waypoint> list = new ArrayList<>(trail.
                            getWaypoints());
                        final Waypoint first = list.get(0);
                        assertEquals(Instant.parse("2014-05-18T08:25:40Z"),
                            first.getTime());
                        assertEquals(Double.parseDouble("50.1181208342"), first.
                            getCoordinates().getLatitude(), 0);
                        assertEquals(Double.parseDouble("7.9630853701"), first.
                            getCoordinates().getLongitude(), 0);
                        assertEquals(1, first.getLinks().size());
                        first.getLinks().stream().
                            map((link) -> {
                                assertEquals("Some test", link.getLabel());
                                return link;
                            }).
                            forEach((link) -> {
                                assertEquals("http://test.com", link.
                                    getLocation().toString());
                            });
                        break;
                    }
                    default:
                        fail("Unexpected trail: " + trail.getName());
                        break;
                }
            }
        }
    }

    @Test
    public void twoSegmentsMerged() throws InterruptedException {
        final Reader reader = new Gpx11Reader().configure(
            new ReaderSettingsBuilder().groupSubTrails(true).build());
        final Set<Trail> trails = reader.apply(FileSystems.
            getDefault().getPath(".",
                "src/test/resources/2014-05-18_Wispertal_TwoSegments.gpx"));
        assertEquals(1, trails.size());
        for (final Trail trail : trails) {
            if (null != trail.getName()) {
                switch (trail.getName()) {
                    case "Wispertal": {
                        assertEquals(784, trail.getWaypoints().size());
                        final List<Waypoint> list = new ArrayList<>(trail.
                            getWaypoints());
                        final Waypoint first = list.get(0);
                        final Waypoint last = list.get(list.size() - 1);
                        assertEquals(Instant.parse("2013-10-03T09:06:37Z"),
                            first.getTime());
                        assertEquals(Double.parseDouble("50.205804"), first.
                            getCoordinates().getLatitude(), 0);
                        assertEquals(Double.parseDouble("8.191569"), first.
                            getCoordinates().getLongitude(), 0);
                        assertEquals(Instant.parse("2014-05-18T12:06:55Z"),
                            last.getTime());
                        assertEquals(Double.parseDouble("50.1181969419"), last.
                            getCoordinates().getLatitude(), 0);
                        assertEquals(Double.parseDouble("7.9630940873"), last.
                            getCoordinates().getLongitude(), 0);
                        break;
                    }
                    default:
                        fail("Unexpected trail: " + trail.getName());
                        break;
                }
            } else {
                fail("Trail has missing name");
            }
        }
    }

    @Test
    public void twoTracks() throws InterruptedException {
        final Reader reader = new Gpx11Reader().configure(
            new ReaderSettingsBuilder().groupSubTrails(true).build());
        final Set<Trail> trails = reader.apply(FileSystems.
            getDefault().getPath(".",
                "src/test/resources/2014-05-18_Wispertal_TwoTracks.gpx"));
        assertEquals(2, trails.size());
        for (final Trail trail : trails) {
            if (null != trail.getName()) {
                switch (trail.getName()) {
                    case "Wispertal [1]": {
                        assertEquals(779, trail.getWaypoints().size());
                        final List<Waypoint> list = new ArrayList<>(trail.
                            getWaypoints());
                        final Waypoint first = list.get(0);
                        final Waypoint last = list.get(list.size() - 1);
                        assertEquals(Instant.parse("2014-05-18T08:25:32Z"),
                            first.getTime());
                        assertEquals(Double.parseDouble("50.1181208342"), first.
                            getCoordinates().getLatitude(), 0);
                        assertEquals(Double.parseDouble("7.9630853701"), first.
                            getCoordinates().getLongitude(), 0);
                        assertEquals(Instant.parse("2014-05-18T12:06:55Z"),
                            last.getTime());
                        assertEquals(Double.parseDouble("50.1181969419"), last.
                            getCoordinates().getLatitude(), 0);
                        assertEquals(Double.parseDouble("7.9630940873"), last.
                            getCoordinates().getLongitude(), 0);
                        break;
                    }
                    case "Wispertal [2]": {
                        assertEquals(1, trail.getWaypoints().size());
                        assertEquals(1, trail.getLinks().size());
                        trail.getLinks().stream().
                            map((link) -> {
                                assertEquals("Some test", link.getLabel());
                                return link;
                            }).
                            forEach((link) -> {
                                assertEquals("http://test.com", link.
                                    getLocation().
                                    toString());
                            });
                        final List<Waypoint> list = new ArrayList<>(trail.
                            getWaypoints());
                        final Waypoint first = list.get(0);
                        assertEquals(Instant.parse("2014-05-18T08:25:32Z"),
                            first.
                            getTime());
                        assertEquals(Double.parseDouble("50.1181208342"), first.
                            getCoordinates().getLatitude(), 0);
                        assertEquals(Double.parseDouble("7.9630853701"), first.
                            getCoordinates().getLongitude(), 0);
                        break;
                    }
                    default:
                        fail("Unexpected trail: " + trail.getName());
                        break;
                }
            }
        }
    }

    @Test
    public void withRoute() throws InterruptedException {
        final Reader reader = new Gpx11Reader();
        final Set<Trail> trails = reader.apply(FileSystems.
            getDefault().getPath(".",
                "src/test/resources/2014-05-18_Wispertal_WithRoute.gpx"));
        assertTrue(trails.isEmpty());
    }

    @Test
    public void mergeWaypoints() throws InterruptedException {
        final Reader reader = new Gpx11Reader();
        final Set<Trail> trails = reader.apply(FileSystems.
            getDefault().getPath(".",
                "src/test/resources/2013-03-10_Wiesbaden.gpx"));
        assertEquals(1, trails.size());
        trails.stream().forEach((trail) -> {
            assertEquals(917, trail.getWaypoints().size()); //913 trkpt + 4 wpt - outliers
        });
    }

    @Test(expected=ExecutionError.class)
    public void catchException() {
        final Reader reader = new Gpx11Reader();
        reader.apply(FileSystems.
            getDefault().getPath(".",
                "src/test/resources/2014-05-18_Wispertal_NotWellFormed.gpx"));
    }
}
