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

import io.jenetics.jpx.GPX.Version;
import java.nio.file.FileSystems;
import java.time.Instant;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import ws.sosna.pinetrail.api.io.Reader;
import ws.sosna.pinetrail.model.GpsRecord;
import ws.sosna.pinetrail.utils.error.ExecutionError;

/** @author Xavier Sosnovsky */
public class Gpx10ReaderTest {

  @Test
  public void readGpx10File() {
    final Reader reader = new GpxReader(Version.V10);
    final Set<GpsRecord> records =
        reader.apply(FileSystems.getDefault().getPath(".", "src/test/resources/test_bike.gpx"));
    Assert.assertEquals(2, records.size());
    for (final GpsRecord rec : records) {
      if (rec.getTime().equals(Instant.parse("2015-05-14T10:53:08.985Z"))) {
        Assert.assertEquals(49.993740000, rec.getLatitude(), 0.0);
        Assert.assertEquals(8.277416000, rec.getLongitude(), 0.0);
        Assert.assertEquals(149.000000, rec.getElevation(), 0.0);
      } else {
        Assert.assertEquals(49.993668000, rec.getLatitude(), 0.0);
        Assert.assertEquals(8.277310000, rec.getLongitude(), 0.0);
        Assert.assertEquals(173.000000, rec.getElevation(), 0.0);
      }
    }
  }

  @Test(expected = ExecutionError.class)
  public void catchException() {
    final Reader reader = new GpxReader(Version.V10);
    reader.apply(
        FileSystems.getDefault()
            .getPath(".", "src/test/resources/2014-05-18_Wispertal_NotWellFormed.gpx"));
  }
}
