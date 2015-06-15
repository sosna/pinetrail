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
import java.util.Set;
import java.util.prefs.Preferences;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import ws.sosna.pinetrail.api.io.Reader;
import ws.sosna.pinetrail.model.Trail;
import ws.sosna.pinetrail.model.Waypoint;
import ws.sosna.pinetrail.utils.error.ExecutionError;

/**
 * @author Xavier Sosnovsky
 */
public class Gpx10ReaderTest {

    private static Boolean keepOutliers;
    private static Boolean keepIdlePoints;

    @BeforeClass
    public static void setup() {
        keepOutliers = Boolean.valueOf(Preferences.userRoot().node(
            "ws.sosna.pinetrail.model.Trail").get("keepOutliers", "false"));
        Preferences.userRoot().node(
            "ws.sosna.pinetrail.model.Trail").put("keepOutliers", "true");
        keepIdlePoints = Boolean.valueOf(Preferences.userRoot().node(
            "ws.sosna.pinetrail.model.Trail").get("keepIdlePoints", "false"));
        Preferences.userRoot().node(
            "ws.sosna.pinetrail.model.Trail").put("keepIdlePoints", "true");
    }

    @AfterClass
    public static void cleanup() {
        Preferences.userRoot().node(
            "ws.sosna.pinetrail.model.Trail").put("keepOutliers",
                keepOutliers.toString());
        Preferences.userRoot().node(
            "ws.sosna.pinetrail.model.Trail").put("keepIdlePoints",
                keepIdlePoints.toString());
    }

    @Test
    public void readGpx10File() throws InterruptedException {
        final Reader reader = new Gpx10Reader();
        final Set<Trail> trails = reader.apply(FileSystems.
            getDefault().getPath(".", "src/test/resources/test_bike.gpx"));
        assertEquals(1, trails.size());
        for (final Trail trail : trails) {
            assertNotNull(trail.getName());
            assertEquals("Track", trail.getName());
            assertNull(trail.getDescription());
            assertTrue(trail.getLinks().isEmpty());
            assertEquals(4000, trail.getWaypoints().size());
            final Waypoint first = trail.getWaypoints().first();
            final Waypoint last = trail.getWaypoints().last();
            assertEquals(Instant.parse("2015-05-14T10:53:08.985Z"),
                first.getTime());
            assertEquals(Double.parseDouble("49.993740000"), first.
                getCoordinates().getLatitude(), 0);
            assertEquals(Double.parseDouble("8.277416000"), first.
                getCoordinates().getLongitude(), 0);
            assertEquals(Instant.parse("2015-05-14T16:42:47.692Z"),
                last.getTime());
            assertEquals(Double.parseDouble("50.001615000"), last.
                getCoordinates().getLatitude(), 0);
            assertEquals(Double.parseDouble("8.259343000"), last.
                getCoordinates().getLongitude(), 0);
            break;
        }
    }

    @Test(expected = ExecutionError.class)
    public void catchException() {
        final Reader reader = new Gpx11Reader();
        reader.apply(FileSystems.
            getDefault().getPath(".",
                "src/test/resources/2014-05-18_Wispertal_NotWellFormed.gpx"));
    }
}
