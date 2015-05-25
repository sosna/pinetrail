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
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
import org.junit.AfterClass;
import org.junit.Test;
import ws.sosna.pinetrail.api.io.Formats;
import ws.sosna.pinetrail.api.io.ReaderProvider;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import ws.sosna.pinetrail.api.io.Reader;
import ws.sosna.pinetrail.model.Trail;

/**
 * @author Xavier Sosnovsky
 */
public class Gpx11ThreadedReaderTest {

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
    public void multithreadedReading() throws InterruptedException {
        final Set<String> files = new LinkedHashSet<>();
        files.add("src/test/resources/2013-03-10_Wiesbaden.gpx");
        files.add("src/test/resources/2014-02-02_Niedernhausen.gpx");
        files.add("src/test/resources/2014-05-18_Wispertal.gpx");

        final ReaderProvider provider = new Gpx11Provider();
        final Set<Set<Trail>> trails = files.parallelStream()
                .map(item -> createPath(item))
                .map(path -> provider.newReader(Formats.GPX_1_1).apply(path))
                .collect(Collectors.toSet());

        checkResults(trails);
    }

    private CompletableFuture<Set<Trail>> getJob(final Path path) {
        final ReaderProvider provider = new Gpx11Provider();
        final Reader reader = provider.newReader(Formats.GPX_1_1);
        return CompletableFuture.supplyAsync(() -> reader.apply(path));
    }

    private Path createPath(final String file) {
        return FileSystems.getDefault().getPath(".", file);
    }

    private void checkResults(final Set<Set<Trail>> all) {
        for (final Set<Trail> trails : all) {
            for (final Trail trail : trails) {
                switch (trail.getName()) {
                    case "Wiesbaden":
                        assertEquals(917, trail.getWaypoints().size());
                        break;
                    case "Niedernhausen":
                        assertEquals(1024, trail.getWaypoints().size());
                        break;
                    case "Wispertal":
                        assertEquals(779, trail.getWaypoints().size());
                        break;
                    default:
                        fail("Unexpected track:" + trail.getName());
                }
            }
        }
    }
}
