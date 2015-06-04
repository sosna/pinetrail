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

import com.topografix.gpx._1._1.GpxType;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import org.junit.BeforeClass;
import org.junit.Test;
import ws.sosna.pinetrail.api.io.Reader;
import ws.sosna.pinetrail.api.io.Writer;
import ws.sosna.pinetrail.api.io.WriterSettings;
import ws.sosna.pinetrail.api.io.WriterSettingsBuilder;
import ws.sosna.pinetrail.model.Trail;
import ws.sosna.pinetrail.model.Waypoint;
import ws.sosna.pinetrail.utils.error.ExecutionError;

/**
 * @author Xavier Sosnovsky
 */
public class Gpx11WriterTest {

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
    public void writeFile() throws InterruptedException {
        final Reader reader = new Gpx11Reader();
        final Set<Trail> trails = reader.apply(FileSystems.
            getDefault().getPath(".",
                "src/test/resources/2014-05-18_Wispertal_Full.gpx"));
        assertEquals(1, trails.size());
        final Trail trail = (Trail) trails.toArray()[0];
        final Writer writer = new Gpx11Writer();
        final WriterSettings settings = new WriterSettingsBuilder().
            writeIdlePoints(true).writeOutliers(true).build();
        writer.configure(settings);
        final Path path = FileSystems.
            getDefault().getPath(".",
                "src/test/resources/2014-05-18_Wispertal.out.gpx");
        writer.accept(trail, path);

        final Reader reader2 = new Gpx11Reader();
        final Set<Trail> trails2 = reader2.apply(path);
        assertEquals(1, trails2.size());
        final Trail trail2 = (Trail) trails2.toArray()[0];

        assertEquals(trail.getActivity(), trail2.getActivity());
        assertEquals(trail.getCountries(), trail2.getCountries());
        assertEquals(trail.getDescription(), trail2.getDescription());
        assertEquals(trail.getDifficultyRating(), trail2.getDifficultyRating());
        assertEquals(trail.getLinks(), trail2.getLinks());
        assertEquals(trail.getName(), trail2.getName());
        assertEquals(trail.getRating(), trail2.getRating());
        assertEquals(trail.getStatistics(), trail2.getStatistics());
        assertEquals(trail.getWaypoints(), trail2.getWaypoints());

        final WriterSettings pretty
            = new WriterSettingsBuilder().prettyPrinting(true).writeOutliers(
                true).writeIdlePoints(true).build();
        final Writer writer2 = new Gpx11Writer().configure(pretty);
        writer2.accept(trail, path);

        final Reader reader3 = new Gpx11Reader();
        final Set<Trail> trails3 = reader3.apply(path);
        assertEquals(1, trails3.size());
        final Trail trail3 = (Trail) trails3.toArray()[0];

        assertEquals(trail3.getActivity(), trail2.getActivity());
        assertEquals(trail3.getCountries(), trail2.getCountries());
        assertEquals(trail3.getDescription(), trail2.getDescription());
        assertEquals(trail3.getDifficultyRating(), trail2.getDifficultyRating());
        assertEquals(trail3.getLinks(), trail2.getLinks());
        assertEquals(trail3.getName(), trail2.getName());
        assertEquals(trail3.getRating(), trail2.getRating());
        assertEquals(trail3.getStatistics(), trail2.getStatistics());
        assertEquals(trail3.getWaypoints(), trail2.getWaypoints());
    }

    @Test
    public void writeFileNoOutliers() throws InterruptedException {
        final Reader reader = new Gpx11Reader();
        final Set<Trail> trails = reader.apply(FileSystems.
            getDefault().getPath(".",
                "src/test/resources/2014-05-18_Wispertal_Full.gpx"));
        assertEquals(1, trails.size());
        final Trail trail = (Trail) trails.toArray()[0];
        final Set<Waypoint> outliers = new LinkedHashSet<>();
        outliers.addAll(trail.getStatistics().getDistanceSummary().
            getOutliers());
        outliers.addAll(trail.getStatistics().
            getElevationDifferenceSummary().getOutliers());
        outliers.addAll(trail.getStatistics().getElevationSummary().
            getOutliers());
        outliers.addAll(trail.getStatistics().getGradeSummary().
            getOutliers());
        outliers.addAll(trail.getStatistics().getSpeedSummary().
            getOutliers());
        assertFalse(outliers.isEmpty());

        final Writer writer = new Gpx11Writer();
        final WriterSettings settings = new WriterSettingsBuilder().
            writeIdlePoints(true).writeOutliers(false).prettyPrinting(true).
            build();
        writer.configure(settings);
        final Path path = FileSystems.
            getDefault().getPath(".",
                "src/test/resources/2014-05-18_Wispertal.out.gpx");
        writer.accept(trail, path);

        final Reader reader2 = new Gpx11Reader();
        final Set<Trail> trails2 = reader2.apply(FileSystems.
            getDefault().getPath(".",
                "src/test/resources/2014-05-18_Wispertal.out.gpx"));
        assertEquals(1, trails2.size());
        final Trail trail2 = (Trail) trails2.toArray()[0];

        assertEquals(trail.getActivity(), trail2.getActivity());
        assertEquals(trail.getCountries(), trail2.getCountries());
        assertEquals(trail.getDescription(), trail2.getDescription());
        assertEquals(trail.getDifficultyRating(), trail2.getDifficultyRating());
        assertEquals(trail.getLinks(), trail2.getLinks());
        assertEquals(trail.getName(), trail2.getName());
        assertEquals(trail.getRating(), trail2.getRating());
        assertEquals(trail.getWaypoints().size() - outliers.size(), trail2.
            getWaypoints().size());
    }

    @Test
    public void writeFileNoIdlePoints() throws InterruptedException {
        final Reader reader = new Gpx11Reader();
        final Set<Trail> trails = reader.apply(FileSystems.
            getDefault().getPath(".",
                "src/test/resources/2014-05-18_Wispertal_Full.gpx"));
        assertEquals(1, trails.size());
        final Trail trail = (Trail) trails.toArray()[0];

        final Set<Waypoint> activePoints = new LinkedHashSet<>();
        activePoints.addAll(trail.getWaypoints().stream().filter(
            Waypoint::isActive).collect(Collectors.toSet()));
        assertFalse(activePoints.isEmpty());

        final Writer writer = new Gpx11Writer();
        final WriterSettings settings = new WriterSettingsBuilder().
            writeIdlePoints(false).writeOutliers(true).prettyPrinting(true).
            build();
        writer.configure(settings);
        final Path path = FileSystems.
            getDefault().getPath(".",
                "src/test/resources/2014-05-18_Wispertal.out.gpx");
        writer.accept(trail, path);

        final Reader reader2 = new Gpx11Reader();
        final Set<Trail> trails2 = reader2.apply(path);
        assertEquals(1, trails2.size());
        final Trail trail2 = (Trail) trails2.toArray()[0];

        assertEquals(trail.getActivity(), trail2.getActivity());
        assertEquals(trail.getCountries(), trail2.getCountries());
        assertEquals(trail.getDescription(), trail2.getDescription());
        assertEquals(trail.getDifficultyRating(), trail2.getDifficultyRating());
        assertEquals(trail.getLinks(), trail2.getLinks());
        assertEquals(trail.getName(), trail2.getName());
        assertEquals(trail.getRating(), trail2.getRating());
        assertEquals(activePoints.size(), trail2.getWaypoints().size());
    }

    @Test
    public void defaultToValidActivePoints() throws InterruptedException {
        final Reader reader = new Gpx11Reader();
        final Set<Trail> trails = reader.apply(FileSystems.
            getDefault().getPath(".",
                "src/test/resources/2014-05-18_Wispertal_Full.gpx"));
        assertEquals(1, trails.size());
        final Trail trail = (Trail) trails.toArray()[0];

        final Set<Waypoint> activePoints = new LinkedHashSet<>();
        activePoints.addAll(trail.getWaypoints().stream().filter(
            Waypoint::isActive).collect(Collectors.toSet()));
        assertFalse(activePoints.isEmpty());

        final Set<Waypoint> outliers = new LinkedHashSet<>();
        outliers.addAll(trail.getStatistics().getDistanceSummary().
            getOutliers());
        outliers.addAll(trail.getStatistics().
            getElevationDifferenceSummary().getOutliers());
        outliers.addAll(trail.getStatistics().getElevationSummary().
            getOutliers());
        outliers.addAll(trail.getStatistics().getGradeSummary().
            getOutliers());
        outliers.addAll(trail.getStatistics().getSpeedSummary().
            getOutliers());
        assertFalse(outliers.isEmpty());

        final Set<Waypoint> validActivePoints = new LinkedHashSet<>();
        validActivePoints.addAll(activePoints.stream().filter(
            i -> !(outliers.contains(i))).collect(Collectors.toSet()));
        assertFalse(validActivePoints.isEmpty());

        final Writer writer = new Gpx11Writer();
        final Path path = FileSystems.
            getDefault().getPath(".",
                "src/test/resources/2014-05-18_Wispertal.out.gpx");
        writer.accept(trail, path);

        final Reader reader2 = new Gpx11Reader();
        final Set<Trail> trails2 = reader2.apply(path);
        assertEquals(1, trails2.size());
        final Trail trail2 = (Trail) trails2.toArray()[0];

        assertEquals(trail.getActivity(), trail2.getActivity());
        assertEquals(trail.getCountries(), trail2.getCountries());
        assertEquals(trail.getDescription(), trail2.getDescription());
        assertEquals(trail.getDifficultyRating(), trail2.getDifficultyRating());
        assertEquals(trail.getLinks(), trail2.getLinks());
        assertEquals(trail.getName(), trail2.getName());
        assertEquals(trail.getRating(), trail2.getRating());
        assertEquals(validActivePoints.size(), trail2.getWaypoints().size());
    }

    @Test(expected = ExecutionError.class)
    public void attemptToOverwriteFile() throws InterruptedException {
        final Reader reader = new Gpx11Reader();
        final Set<Trail> trails = reader.apply(FileSystems.
            getDefault().getPath(".",
                "src/test/resources/2014-05-18_Wispertal_Full.gpx"));
        assertEquals(1, trails.size());
        final Trail trail = (Trail) trails.toArray()[0];
        final WriterSettings settings
            = new WriterSettingsBuilder().overwriteIfExists(false).build();
        final Writer writer = new Gpx11Writer().configure(settings);
        final Path path = FileSystems.
            getDefault().getPath(".",
                "src/test/resources/2014-05-18_Wispertal.out.gpx");
        writer.accept(trail, path);
    }

    @Test
    public void writeRoute() {
        final Reader reader = new Gpx11Reader();
        final Set<Trail> trails = reader.apply(FileSystems.
            getDefault().getPath(".",
                "src/test/resources/2014-05-18_Wispertal_Full.gpx"));
        assertEquals(1, trails.size());
        final Trail trail = (Trail) trails.toArray()[0];

        final Writer writer = new Gpx11Writer();
        final Path path = FileSystems.
            getDefault().getPath(".",
                "src/test/resources/2014-05-18_Wispertal.route.gpx");
        final WriterSettings settings = new WriterSettingsBuilder().writeRoute(
            true).build();
        writer.configure(settings).accept(trail, path);

        try (final BufferedReader routeReader
            = Files.newBufferedReader(path)) {
            final Unmarshaller u = GpxJaxbUtils.INSTANCE.getGpx11Context().
                createUnmarshaller();
            u.setSchema(GpxJaxbUtils.INSTANCE.getGpx11Schema());
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
        }
    }

    @Test
    public void elevationForLongRoute() {
        final String key = Preferences.userRoot().node(
            "ws.sosna.pinetrail.UserSettings").get("mapQuestKey", null);
        if (null != key) {
            final Reader reader = new Gpx11Reader();
            final Set<Trail> trails = reader.apply(FileSystems.
                getDefault().getPath(".",
                    "src/test/resources/long_route.gpx"));
            assertEquals(1, trails.size());
            final Trail trail = (Trail) trails.toArray()[0];
            for (final Waypoint pt : trail.getWaypoints()) {
                assertNotNull(pt.getCoordinates().getElevation());
            }
        }
    }
}
