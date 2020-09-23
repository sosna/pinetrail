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

import io.jenetics.jpx.GPX.Version;
import java.nio.file.FileSystems;
import java.util.Set;
import org.junit.Test;
import ws.sosna.pinetrail.api.io.Reader;
import ws.sosna.pinetrail.model.GpsRecord;
import ws.sosna.pinetrail.utils.error.ExecutionError;

/** @author Xavier Sosnovsky */
public class Gpx11ReaderTest {

  @Test
  public void noPoints() {
    final Reader reader = new GpxReader(Version.V11);
    final Set<GpsRecord> records =
        reader.apply(
            FileSystems.getDefault()
                .getPath(".", "src/test/resources/2014-05-18_Wispertal_NoPoint.gpx"));
    assertTrue(records.isEmpty());
  }

  @Test
  public void noSegment() {
    final Reader reader = new GpxReader(Version.V11);
    final Set<GpsRecord> records =
        reader.apply(
            FileSystems.getDefault()
                .getPath(".", "src/test/resources/2014-05-18_Wispertal_NoSegment.gpx"));
    assertTrue(records.isEmpty());
  }

  @Test
  public void noTrack() {
    final Reader reader = new GpxReader(Version.V11);
    final Set<GpsRecord> records =
        reader.apply(
            FileSystems.getDefault()
                .getPath(".", "src/test/resources/2014-05-18_Wispertal_NoTrack.gpx"));
    assertTrue(records.isEmpty());
  }

  @Test
  public void twoSegmentsMerged() {
    final Reader reader = new GpxReader(Version.V11);
    final Set<GpsRecord> records =
        reader.apply(
            FileSystems.getDefault()
                .getPath(".", "src/test/resources/2014-05-18_Wispertal_TwoSegments.gpx"));
    assertEquals(4, records.size());
  }

  @Test
  public void twoTracks() {
    final Reader reader = new GpxReader(Version.V11);
    final Set<GpsRecord> records =
        reader.apply(
            FileSystems.getDefault()
                .getPath(".", "src/test/resources/2014-05-18_Wispertal_TwoTracks.gpx"));
    assertEquals(4, records.size());
  }

  @Test(expected = ExecutionError.class)
  public void catchException() {
    final Reader reader = new GpxReader(Version.V11);
    reader.apply(
        FileSystems.getDefault()
            .getPath(".", "src/test/resources/2014-05-18_Wispertal_NotWellFormed.gpx"));
  }
}
