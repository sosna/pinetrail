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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import io.jenetics.jpx.GPX.Version;
import java.nio.file.FileSystems;
import java.time.Instant;
import java.util.Set;
import java.util.prefs.Preferences;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ws.sosna.pinetrail.api.io.Reader;
import ws.sosna.pinetrail.api.io.ReaderSettingsBuilder;
import ws.sosna.pinetrail.model.Trail;
import ws.sosna.pinetrail.model.Waypoint;
import ws.sosna.pinetrail.utils.error.ExecutionError;

/** @author Xavier Sosnovsky */
public class Gpx11ReaderTest {

  private static Boolean keepOutliers;
  private static Boolean keepIdlePoints;

  @BeforeClass
  public static void setup() {
    keepOutliers =
        Boolean.valueOf(
            Preferences.userRoot()
                .node("ws.sosna.pinetrail.model.Trail")
                .get("keepOutliers", "false"));
    Preferences.userRoot().node("ws.sosna.pinetrail.model.Trail").put("keepOutliers", "true");
    keepIdlePoints =
        Boolean.valueOf(
            Preferences.userRoot()
                .node("ws.sosna.pinetrail.model.Trail")
                .get("keepIdlePoints", "false"));
    Preferences.userRoot().node("ws.sosna.pinetrail.model.Trail").put("keepIdlePoints", "true");
  }

  @AfterClass
  public static void cleanup() {
    Preferences.userRoot()
        .node("ws.sosna.pinetrail.model.Trail")
        .put("keepOutliers", keepOutliers.toString());
    Preferences.userRoot()
        .node("ws.sosna.pinetrail.model.Trail")
        .put("keepIdlePoints", keepIdlePoints.toString());
  }

  @Test
  public void noPoints() {
    final Reader reader = new GpxReader(Version.V11);
    final Set<Trail> trails =
        reader.apply(
            FileSystems.getDefault()
                .getPath(".", "src/test/resources/2014-05-18_Wispertal_NoPoint.gpx"));
    assertTrue(trails.isEmpty());
  }

  @Test
  public void noSegment() {
    final Reader reader = new GpxReader(Version.V11);
    final Set<Trail> trails =
        reader.apply(
            FileSystems.getDefault()
                .getPath(".", "src/test/resources/2014-05-18_Wispertal_NoSegment.gpx"));
    assertTrue(trails.isEmpty());
  }

  @Test
  public void noTrack() {
    final Reader reader = new GpxReader(Version.V11);
    final Set<Trail> trails =
        reader.apply(
            FileSystems.getDefault()
                .getPath(".", "src/test/resources/2014-05-18_Wispertal_NoTrack.gpx"));
    assertTrue(trails.isEmpty());
  }

  @Test
  public void twoSegments() {
    final Reader reader = new GpxReader(Version.V11);
    final Set<Trail> trails =
        reader.apply(
            FileSystems.getDefault()
                .getPath(".", "src/test/resources/2014-05-18_Wispertal_TwoSegments.gpx"));
    assertEquals(2, trails.size());
    for (final Trail trail : trails) {
      switch (trail.getWaypoints().size()) {
        case 779:
          {
            final Waypoint first = trail.getWaypoints().first();
            final Waypoint last = trail.getWaypoints().last();
            assertEquals(Instant.parse("2014-05-18T08:25:32Z"), first.getTime());
            assertEquals(
                Double.parseDouble("50.1181208342"), first.getCoordinates().getLatitude(), 0);
            assertEquals(
                Double.parseDouble("7.9630853701"), first.getCoordinates().getLongitude(), 0);
            assertEquals(Instant.parse("2014-05-18T12:06:55Z"), last.getTime());
            assertEquals(
                Double.parseDouble("50.1181969419"), last.getCoordinates().getLatitude(), 0);
            assertEquals(
                Double.parseDouble("7.9630940873"), last.getCoordinates().getLongitude(), 0);
            break;
          }
        case 1:
          {
            final Waypoint first = trail.getWaypoints().first();
            assertEquals(Instant.parse("2014-05-18T08:25:40Z"), first.getTime());
            assertEquals(
                Double.parseDouble("50.1181208342"), first.getCoordinates().getLatitude(), 0);
            assertEquals(
                Double.parseDouble("7.9630853701"), first.getCoordinates().getLongitude(), 0);
            break;
          }
        default:
          fail("Unexpected number of waypoints: " + trail.getWaypoints().size());
          break;
      }
    }
  }

  @Test
  public void twoSegmentsMerged() {
    final Reader reader =
        new GpxReader(Version.V11)
            .configure(new ReaderSettingsBuilder().groupSubTrails(true).build());
    final Set<Trail> trails =
        reader.apply(
            FileSystems.getDefault()
                .getPath(".", "src/test/resources/2014-05-18_Wispertal_TwoSegments.gpx"));
    assertEquals(1, trails.size());
    for (final Trail trail : trails) {
      if (trail.getWaypoints().size() == 784) {
        assertEquals(784, trail.getWaypoints().size());
        final Waypoint first = trail.getWaypoints().first();
        final Waypoint last = trail.getWaypoints().last();
        assertEquals(Instant.parse("2013-10-03T09:06:37Z"), first.getTime());
        assertEquals(Double.parseDouble("50.205804"), first.getCoordinates().getLatitude(), 0);
        assertEquals(Double.parseDouble("8.191569"), first.getCoordinates().getLongitude(), 0);
        assertEquals(Instant.parse("2014-05-18T12:06:55Z"), last.getTime());
        assertEquals(Double.parseDouble("50.1181969419"), last.getCoordinates().getLatitude(), 0);
        assertEquals(Double.parseDouble("7.9630940873"), last.getCoordinates().getLongitude(), 0);
      } else {
        fail("Unexpected number of waypoints: " + trail.getWaypoints().size());
      }
    }
  }

  @Test
  public void twoTracks() {
    final Reader reader =
        new GpxReader(Version.V11)
            .configure(new ReaderSettingsBuilder().groupSubTrails(true).build());
    final Set<Trail> trails =
        reader.apply(
            FileSystems.getDefault()
                .getPath(".", "src/test/resources/2014-05-18_Wispertal_TwoTracks.gpx"));
    assertEquals(2, trails.size());
    for (final Trail trail : trails) {
      switch (trail.getWaypoints().size()) {
        case 779:
          {
            final Waypoint first = trail.getWaypoints().first();
            final Waypoint last = trail.getWaypoints().last();
            assertEquals(Instant.parse("2014-05-18T08:25:32Z"), first.getTime());
            assertEquals(
                Double.parseDouble("50.1181208342"), first.getCoordinates().getLatitude(), 0);
            assertEquals(
                Double.parseDouble("7.9630853701"), first.getCoordinates().getLongitude(), 0);
            assertEquals(Instant.parse("2014-05-18T12:06:55Z"), last.getTime());
            assertEquals(
                Double.parseDouble("50.1181969419"), last.getCoordinates().getLatitude(), 0);
            assertEquals(
                Double.parseDouble("7.9630940873"), last.getCoordinates().getLongitude(), 0);
            break;
          }
        case 1:
          {
            final Waypoint first = trail.getWaypoints().first();
            assertEquals(Instant.parse("2014-05-18T08:25:32Z"), first.getTime());
            assertEquals(
                Double.parseDouble("50.1181208342"), first.getCoordinates().getLatitude(), 0);
            assertEquals(
                Double.parseDouble("7.9630853701"), first.getCoordinates().getLongitude(), 0);
            break;
          }
        default:
          fail("Unexpected trail size: " + trail.getWaypoints().size());
          break;
      }
    }
  }

  @Test
  public void withRoute() {
    final Reader reader = new GpxReader(Version.V11);
    final Set<Trail> trails =
        reader.apply(
            FileSystems.getDefault()
                .getPath(".", "src/test/resources/2014-05-18_Wispertal_WithRoute.gpx"));
    assertTrue(trails.isEmpty());
  }

  @Test
  public void mergeWaypoints() {
    final Reader reader = new GpxReader(Version.V11);
    final Set<Trail> trails =
        reader.apply(
            FileSystems.getDefault().getPath(".", "src/test/resources/2013-03-10_Wiesbaden.gpx"));
    assertEquals(1, trails.size());
    trails.stream()
        .forEach(
            (trail) -> {
              assertEquals(917, trail.getWaypoints().size()); // 913 trkpt + 4 wpt - outliers
            });
  }

  @Test(expected = ExecutionError.class)
  public void catchException() {
    final Reader reader = new GpxReader(Version.V11);
    reader.apply(
        FileSystems.getDefault()
            .getPath(".", "src/test/resources/2014-05-18_Wispertal_NotWellFormed.gpx"));
  }
}
