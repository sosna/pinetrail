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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import ws.sosna.pinetrail.api.io.Formats;
import ws.sosna.pinetrail.api.io.Reader;
import ws.sosna.pinetrail.api.io.Writer;

/** @author Xavier Sosnovsky */
public class Gpx11ProviderTest {

  @Test
  public void wrongReaderFormat() {
    final Reader reader = new Gpx11Provider().newReader(Formats.KML_2_1_0);
    assertNull(reader);
  }

  @Test
  public void gpx11Reader() {
    final Reader reader = new Gpx11Provider().newReader(Formats.GPX_1_1);
    assertNotNull(reader);
  }

  @Test
  public void wrongWriterFormat() {
    final Writer writer = new Gpx11Provider().newWriter(Formats.KML_2_1_0);
    assertNull(writer);
  }

  @Test
  public void gpx11Writer() {
    final Writer writer = new Gpx11Provider().newWriter(Formats.GPX_1_1);
    assertNotNull(writer);
  }
}
