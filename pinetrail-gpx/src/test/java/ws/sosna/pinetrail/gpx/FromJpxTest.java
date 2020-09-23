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

import io.jenetics.jpx.GPX;
import java.time.Instant;
import java.util.Set;
import org.junit.Test;
import ws.sosna.pinetrail.model.GpsRecord;

/** @author Xavier Sosnovsky */
public class FromJpxTest {

  @Test
  public void map() {
    final Instant t1 = Instant.parse("2014-05-18T08:25:32Z");
    final Instant t2 = Instant.parse("2014-05-18T12:06:55Z");
    final GPX gpx =
        GPX.builder()
            .addTrack(
                track ->
                    track.addSegment(
                        segment ->
                            segment
                                .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(160).time(t1))
                                .addPoint(p -> p.lat(48.2081742).lon(16.373819).ele(161).time(t2))))
            .build();
    final FromJpx mapper = new FromJpx();
    final Set<GpsRecord> records = mapper.map(gpx);
    assertEquals(2, records.size());
    for (final GpsRecord rec : records) {
      if (rec.getTime().equals(t1)) {
        assertEquals(Double.parseDouble("48.2081743"), rec.getLatitude(), 0);
        assertEquals(Double.parseDouble("16.3738189"), rec.getLongitude(), 0);
        assertEquals(Double.parseDouble("160"), rec.getElevation(), 0);
      } else {
        assertEquals(t2, rec.getTime());
        assertEquals(Double.parseDouble("48.2081742"), rec.getLatitude(), 0);
        assertEquals(Double.parseDouble("16.373819"), rec.getLongitude(), 0);
        assertEquals(Double.parseDouble("161"), rec.getElevation(), 0);
      }
    }
  }
}
