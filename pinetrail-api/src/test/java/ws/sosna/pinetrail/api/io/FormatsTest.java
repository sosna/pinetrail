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

import java.nio.file.FileSystems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import ws.sosna.pinetrail.utils.error.ExecutionError;
import ws.sosna.pinetrail.utils.logging.StatusCodes;

/** @author Xavier Sosnovsky */
public class FormatsTest {

  @Test
  public void getGpx1_0() {
    assertEquals(
        Formats.GPX_1_0,
        Formats.of(FileSystems.getDefault().getPath(".", "src/test/resources/gpx1_0.gpx")));
  }

  @Test
  public void getGpx1_1() {
    assertEquals(
        Formats.GPX_1_1,
        Formats.of(FileSystems.getDefault().getPath(".", "src/test/resources/gpx1_1.gpx")));
  }

  @Test
  public void throwNotAcceptable() {
    try {
      Formats.of(FileSystems.getDefault().getPath(".", "src/test/resources/logback-test.xml"));
      fail("Expected 406");
    } catch (final ExecutionError e) {
      if (StatusCodes.NOT_ACCEPTABLE != e.getErrorCode()) {
        fail("Expected 406 but got " + e.getErrorCode());
      }
    }
  }

  @Test
  public void throwNotFound() {
    try {
      Formats.of(FileSystems.getDefault().getPath(".", "src/test/resources/nothere.why"));
      fail("Expected 404");
    } catch (final ExecutionError e) {
      if (StatusCodes.NOT_FOUND != e.getErrorCode()) {
        fail("Expected 404 but got " + e.getErrorCode());
      }
    }
  }
}
