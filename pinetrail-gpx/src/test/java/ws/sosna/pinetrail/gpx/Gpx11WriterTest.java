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
package ws.sosna.pinetrail.gpx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import io.jenetics.jpx.GPX.Version;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ws.sosna.pinetrail.api.io.Reader;
import ws.sosna.pinetrail.api.io.Writer;
import ws.sosna.pinetrail.api.io.WriterSettings;
import ws.sosna.pinetrail.api.io.WriterSettingsBuilder;
import ws.sosna.pinetrail.model.Trail;
import ws.sosna.pinetrail.model.Waypoint;
import ws.sosna.pinetrail.utils.error.ExecutionError;

/** @author Xavier Sosnovsky */
public class Gpx11WriterTest {

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
  public void writeFile() {
    final Reader reader = new GpxReader(Version.V11);
    final Set<Trail> trails =
        reader.apply(
            FileSystems.getDefault()
                .getPath(".", "src/test/resources/2014-05-18_Wispertal_Full.gpx"));
    assertEquals(1, trails.size());
    final Trail trail = (Trail) trails.toArray()[0];
    final Writer writer = new Gpx11Writer();
    final WriterSettings settings =
        new WriterSettingsBuilder().writeIdlePoints(true).writeOutliers(true).build();
    writer.configure(settings);
    final Path path =
        FileSystems.getDefault().getPath(".", "src/test/resources/2014-05-18_Wispertal.out.gpx");
    writer.accept(trail, path);

    final Reader reader2 = new GpxReader(Version.V11);
    final Set<Trail> trails2 = reader2.apply(path);
    assertEquals(1, trails2.size());
    final Trail trail2 = (Trail) trails2.toArray()[0];

    assertEquals(trail.getCountries(), trail2.getCountries());
    assertEquals(trail.getStatistics(), trail2.getStatistics());
    assertEquals(trail.getWaypoints(), trail2.getWaypoints());

    final WriterSettings pretty =
        new WriterSettingsBuilder()
            .prettyPrinting(true)
            .writeOutliers(true)
            .writeIdlePoints(true)
            .build();
    final Writer writer2 = new Gpx11Writer().configure(pretty);
    writer2.accept(trail, path);

    final Reader reader3 = new GpxReader(Version.V11);
    final Set<Trail> trails3 = reader3.apply(path);
    assertEquals(1, trails3.size());
    final Trail trail3 = (Trail) trails3.toArray()[0];

    assertEquals(trail3.getCountries(), trail2.getCountries());
    assertEquals(trail3.getStatistics(), trail2.getStatistics());
    assertEquals(trail3.getWaypoints(), trail2.getWaypoints());
  }

  @Test
  public void writeFileNoOutliers() {
    final Reader reader = new GpxReader(Version.V11);
    final Set<Trail> trails =
        reader.apply(
            FileSystems.getDefault()
                .getPath(".", "src/test/resources/2014-05-18_Wispertal_Full.gpx"));
    assertEquals(1, trails.size());
    final Trail trail = (Trail) trails.toArray()[0];
    final Set<Waypoint> outliers = new LinkedHashSet<>();
    outliers.addAll(trail.getStatistics().getDistanceSummary().getOutliers());
    outliers.addAll(trail.getStatistics().getElevationDifferenceSummary().getOutliers());
    outliers.addAll(trail.getStatistics().getElevationSummary().getOutliers());
    outliers.addAll(trail.getStatistics().getGradeSummary().getOutliers());
    outliers.addAll(trail.getStatistics().getSpeedSummary().getOutliers());
    assertFalse(outliers.isEmpty());

    final Writer writer = new Gpx11Writer();
    final WriterSettings settings =
        new WriterSettingsBuilder()
            .writeIdlePoints(true)
            .writeOutliers(false)
            .prettyPrinting(true)
            .build();
    writer.configure(settings);
    final Path path =
        FileSystems.getDefault().getPath(".", "src/test/resources/2014-05-18_Wispertal.out.gpx");
    writer.accept(trail, path);

    final Reader reader2 = new GpxReader(Version.V11);
    final Set<Trail> trails2 =
        reader2.apply(
            FileSystems.getDefault()
                .getPath(".", "src/test/resources/2014-05-18_Wispertal.out.gpx"));
    assertEquals(1, trails2.size());
    final Trail trail2 = (Trail) trails2.toArray()[0];

    assertEquals(trail.getCountries(), trail2.getCountries());
    assertEquals(trail.getWaypoints().size() - outliers.size(), trail2.getWaypoints().size());
  }

  @Test
  public void writeFileNoIdlePoints() {
    final Reader reader = new GpxReader(Version.V11);
    final Set<Trail> trails =
        reader.apply(
            FileSystems.getDefault()
                .getPath(".", "src/test/resources/2014-05-18_Wispertal_Full.gpx"));
    assertEquals(1, trails.size());
    final Trail trail = (Trail) trails.toArray()[0];

    final Set<Waypoint> activePoints =
        trail.getWaypoints().stream()
            .filter(Waypoint::isActive)
            .collect(Collectors.toCollection(LinkedHashSet::new));
    assertFalse(activePoints.isEmpty());

    final Writer writer = new Gpx11Writer();
    final WriterSettings settings =
        new WriterSettingsBuilder()
            .writeIdlePoints(false)
            .writeOutliers(true)
            .prettyPrinting(true)
            .build();
    writer.configure(settings);
    final Path path =
        FileSystems.getDefault().getPath(".", "src/test/resources/2014-05-18_Wispertal.out.gpx");
    writer.accept(trail, path);

    final Reader reader2 = new GpxReader(Version.V11);
    final Set<Trail> trails2 = reader2.apply(path);
    assertEquals(1, trails2.size());
    final Trail trail2 = (Trail) trails2.toArray()[0];

    assertEquals(trail.getCountries(), trail2.getCountries());
    assertEquals(activePoints.size(), trail2.getWaypoints().size());
  }

  @Test
  public void defaultToValidActivePoints() {
    final Reader reader = new GpxReader(Version.V11);
    final Set<Trail> trails =
        reader.apply(
            FileSystems.getDefault()
                .getPath(".", "src/test/resources/2014-05-18_Wispertal_Full.gpx"));
    assertEquals(1, trails.size());
    final Trail trail = (Trail) trails.toArray()[0];

    final Set<Waypoint> activePoints =
        trail.getWaypoints().stream()
            .filter(Waypoint::isActive)
            .collect(Collectors.toCollection(LinkedHashSet::new));
    assertFalse(activePoints.isEmpty());

    final Set<Waypoint> outliers = new LinkedHashSet<>();
    outliers.addAll(trail.getStatistics().getDistanceSummary().getOutliers());
    outliers.addAll(trail.getStatistics().getElevationDifferenceSummary().getOutliers());
    outliers.addAll(trail.getStatistics().getElevationSummary().getOutliers());
    outliers.addAll(trail.getStatistics().getGradeSummary().getOutliers());
    outliers.addAll(trail.getStatistics().getSpeedSummary().getOutliers());
    assertFalse(outliers.isEmpty());

    final Set<Waypoint> validActivePoints =
        activePoints.stream()
            .filter(i -> !(outliers.contains(i)))
            .collect(Collectors.toCollection(LinkedHashSet::new));
    assertFalse(validActivePoints.isEmpty());

    final Writer writer = new Gpx11Writer();
    final Path path =
        FileSystems.getDefault().getPath(".", "src/test/resources/2014-05-18_Wispertal.out.gpx");
    writer.accept(trail, path);

    final Reader reader2 = new GpxReader(Version.V11);
    final Set<Trail> trails2 = reader2.apply(path);
    assertEquals(1, trails2.size());
    final Trail trail2 = (Trail) trails2.toArray()[0];

    assertEquals(trail.getCountries(), trail2.getCountries());
    assertEquals(validActivePoints.size(), trail2.getWaypoints().size());
  }

  @Test(expected = ExecutionError.class)
  public void attemptToOverwriteFile() {
    final Reader reader = new GpxReader(Version.V11);
    final Path path =
        FileSystems.getDefault().getPath(".", "src/test/resources/2014-05-18_Wispertal_Full.gpx");
    final Set<Trail> trails = reader.apply(path);
    assertEquals(1, trails.size());
    final Trail trail = (Trail) trails.toArray()[0];
    final WriterSettings settings = new WriterSettingsBuilder().overwriteIfExists(false).build();
    final Writer writer = new Gpx11Writer().configure(settings);
    writer.accept(trail, path);
  }

  @Test
  public void writeRoute() {
    final Reader reader = new GpxReader(Version.V11);
    final Set<Trail> trails =
        reader.apply(
            FileSystems.getDefault()
                .getPath(".", "src/test/resources/2014-05-18_Wispertal_Full.gpx"));
    assertEquals(1, trails.size());
    final Trail trail = (Trail) trails.toArray()[0];

    final Writer writer = new Gpx11Writer();
    final Path path =
        FileSystems.getDefault().getPath(".", "src/test/resources/2014-05-18_Wispertal.route.gpx");
    final WriterSettings settings = new WriterSettingsBuilder().writeRoute(true).build();
    writer.configure(settings).accept(trail, path);

    /*try (final BufferedReader routeReader
        = Files.newBufferedReader(path)) {
        final Unmarshaller u = Gpx11JaxbUtils.INSTANCE.getGpx11Context().
            createUnmarshaller();
        u.setSchema(Gpx11JaxbUtils.INSTANCE.getGpx11Schema());
        final Set<String> validationIssues = new LinkedHashSet<>();
        u.setEventHandler(event -> validationIssues.add(event.toString()));
        final JAXBElement<GpxType> root = u.unmarshal(new StreamSource(
            routeReader), GpxType.class);
        if (!validationIssues.isEmpty()) {
            fail("Found validation error");
        }
    } catch (final JAXBException e) {
        fail("Received unexpected JAXBException");
    } catch (final IOException e) {
        fail("Received unexpected IOException");
    }*/
  }

  @Test
  public void elevationForLongRoute() {
    final String key =
        Preferences.userRoot().node("ws.sosna.pinetrail.UserSettings").get("mapQuestKey", null);
    if (null != key) {
      final Reader reader = new GpxReader(Version.V11);
      final Set<Trail> trails =
          reader.apply(FileSystems.getDefault().getPath(".", "src/test/resources/long_route.gpx"));
      assertEquals(1, trails.size());
      final Trail trail = (Trail) trails.toArray()[0];
      boolean allNull = true;
      for (final Waypoint pt : trail.getWaypoints()) {
        if (null != pt.getCoordinates().getElevation()) {
          allNull = false;
          break;
        }
      }
      assertFalse(allNull);
    }
  }
}
