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

import com.topografix.gpx._1._1.GpxType;
import java.nio.file.FileSystems;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.prefs.Preferences;
import org.junit.AfterClass;
import org.junit.Test;
import ws.sosna.pinetrail.model.Trail;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import ws.sosna.pinetrail.model.Activity;
import ws.sosna.pinetrail.model.Waypoint;

/**
 * @author Xavier Sosnovsky
 */
public class Gpx11ToPinetrailMapperTest {

    private static Boolean keepOutliers;
    private static Boolean keepIdlePoints;
    private static String userLevel;

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
        userLevel = Preferences.userRoot().node(
            "ws.sosna.pinetrail.UserSettings").get("level", "INTERMEDIATE");
    }

    @AfterClass
    public static void cleanup() {
        Preferences.userRoot().node(
            "ws.sosna.pinetrail.model.Trail").put("keepOutliers",
                keepOutliers.toString());
        Preferences.userRoot().node(
            "ws.sosna.pinetrail.model.Trail").put("keepIdlePoints",
                keepIdlePoints.toString());
        Preferences.userRoot().node(
            "ws.sosna.pinetrail.UserSettings").put("level", userLevel);
    }

    @Test
    public void mapGpxType() throws InterruptedException {
        Preferences.userRoot().node(
            "ws.sosna.pinetrail.UserSettings").put("level", "INTERMEDIATE");
        final GpxType gpx = new Gpx11Extractor().parseXml(
            FileSystems.getDefault().getPath(".",
                "src/test/resources/2014-05-18_Wispertal.gpx"));
        final Gpx11ToPinetrailMapper mapper = new Gpx11ToPinetrailMapper(false);
        final Set<Trail> trails = mapper.mapToTrails(gpx);
        assertEquals(1, trails.size());
        for (final Trail trail : trails) {
            assertEquals(779, trail.getWaypoints().size());
            final List<Waypoint> list = new ArrayList<>(trail.getWaypoints());
            final Waypoint first = list.get(0);
            final Waypoint last = list.get(list.size() - 1);
            assertEquals(Instant.parse("2014-05-18T08:25:32Z"), first.getTime());
            assertEquals(Double.parseDouble("50.1181208342"), first.
                getCoordinates().getLatitude(), 0);
            assertEquals(Double.parseDouble("7.9630853701"), first.
                getCoordinates().getLongitude(), 0);
            assertEquals(Instant.parse("2014-05-18T12:06:55Z"), last.getTime());
            assertEquals(Double.parseDouble("50.1181969419"), last.
                getCoordinates().getLatitude(), 0);
            assertEquals(Double.parseDouble("7.9630940873"), last.
                getCoordinates().getLongitude(), 0);
            assertSame(4, trail.getDifficultyRating());
            assertSame(Activity.HIKING, trail.getActivity());
        }
    }

    @Test
    public void ignoreInvalidPoint() throws InterruptedException {
        final GpxType gpx = new Gpx11Extractor().parseXml(
            FileSystems.getDefault().getPath(".",
                "src/test/resources/2014-05-18_Wispertal_NotValidBusinessRules.gpx"));
        final Gpx11ToPinetrailMapper mapper = new Gpx11ToPinetrailMapper(false);
        final Set<Trail> trails = mapper.mapToTrails(gpx);
        assertEquals(1, trails.size());
        for (final Trail trail : trails) {
            assertEquals(778, trail.getWaypoints().size());
            final List<Waypoint> list = new ArrayList<>(trail.getWaypoints());
            final Waypoint first = list.get(0);
            final Waypoint last = list.get(list.size() - 1);
            assertEquals(Instant.parse("2014-05-18T08:25:32Z"), first.getTime());
            assertEquals(Double.parseDouble("50.1181208342"), first.
                getCoordinates().getLatitude(), 0);
            assertEquals(Double.parseDouble("7.9630853701"), first.
                getCoordinates().getLongitude(), 0);
            assertEquals(Instant.parse("2014-05-18T12:06:55Z"), last.getTime());
            assertEquals(Double.parseDouble("50.1181969419"), last.
                getCoordinates().getLatitude(), 0);
            assertEquals(Double.parseDouble("7.9630940873"), last.
                getCoordinates().getLongitude(), 0);
        }
    }

     @Test
    public void ignoreMissingElevation() throws InterruptedException {
        final GpxType gpx = new Gpx11Extractor().parseXml(
            FileSystems.getDefault().getPath(".",
                "src/test/resources/2014-05-18_Wispertal_MissingElevation.gpx"));
        final Gpx11ToPinetrailMapper mapper = new Gpx11ToPinetrailMapper(false);
        final Set<Trail> trails = mapper.mapToTrails(gpx);
        assertEquals(1, trails.size());
        for (final Trail trail : trails) {
            assertEquals(779, trail.getWaypoints().size());
            final List<Waypoint> list = new ArrayList<>(trail.getWaypoints());
            final Waypoint first = list.get(0);
            final Waypoint last = list.get(list.size() - 1);
            assertEquals(Instant.parse("2014-05-18T08:25:32Z"), first.getTime());
            assertEquals(Double.parseDouble("50.1181208342"), first.
                getCoordinates().getLatitude(), 0);
            assertEquals(Double.parseDouble("7.9630853701"), first.
                getCoordinates().getLongitude(), 0);
            assertEquals(Instant.parse("2014-05-18T12:06:55Z"), last.getTime());
            assertEquals(Double.parseDouble("50.1181969419"), last.
                getCoordinates().getLatitude(), 0);
            assertEquals(Double.parseDouble("7.9630940873"), last.
                getCoordinates().getLongitude(), 0);
        }
    }

    @Test
    public void ignoreInvalidTrail() throws InterruptedException {
        final GpxType gpx = new Gpx11Extractor().parseXml(
            FileSystems.getDefault().getPath(".",
                "src/test/resources/2014-05-18_Wispertal_NotValidTrail.gpx"));
        final Gpx11ToPinetrailMapper mapper = new Gpx11ToPinetrailMapper(false);
        final Set<Trail> trails = mapper.mapToTrails(gpx);
        assertTrue(trails.isEmpty());
    }
}
