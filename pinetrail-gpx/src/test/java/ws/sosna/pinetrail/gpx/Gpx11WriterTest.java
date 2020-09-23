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

import io.jenetics.jpx.GPX.Version;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.After;
import org.junit.Test;
import ws.sosna.pinetrail.api.io.Reader;
import ws.sosna.pinetrail.api.io.Writer;
import ws.sosna.pinetrail.model.GpsRecord;

/** @author Xavier Sosnovsky */
public class Gpx11WriterTest {

  final String file = "src/test/resources/test_write.gpx";

  @After
  public void clean() throws IOException {
    Files.deleteIfExists(Path.of(file));
  }

  @Test
  public void writeFile() {
    final Instant t1 = Instant.parse("2014-05-18T08:25:32Z");
    final Instant t2 = Instant.parse("2014-05-18T12:06:55Z");
    final GpsRecord r1 = GpsRecord.of(t1, 19.678, 45.89, 120.9);
    final GpsRecord r2 = GpsRecord.of(t2, 20.678, 45.9, 127.7);
    final Set<GpsRecord> expected = new LinkedHashSet<>();
    expected.add(r1);
    expected.add(r2);
    final Writer writer = new Gpx11Writer();
    final Path path = FileSystems.getDefault().getPath(".", file);
    writer.accept(expected, path);

    final Reader reader = new GpxReader(Version.V11);
    final Set<GpsRecord> received = reader.apply(path);
    assertEquals(expected, received);
  }
}
