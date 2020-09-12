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
package ws.sosna.pinetrail.api.io;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/** @author Xavier Sosnovsky */
public class WritersTest {

  @Test
  public void registerProviderAndGetWriter() {
    final Writer writer = Writers.INSTANCE.newWriter(Formats.GPX_1_1);
    assertNotNull(writer);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void unsupportedFormat() {
    Writers.valueOf("INSTANCE").newWriter(Formats.valueOf("KML_2_2_0"));
  }

  @Test
  public void getAndConfigureWriter() {
    final WriterSettings settings = new WriterSettingsBuilder().overwriteIfExists(false).build();
    final Writer writer = Writers.INSTANCE.newWriter(Formats.GPX_1_1).configure(settings);
    assertNotNull(writer);
  }
}
