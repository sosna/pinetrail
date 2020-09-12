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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import ws.sosna.pinetrail.utils.error.ExecutionError;
import ws.sosna.pinetrail.utils.logging.Actions;
import ws.sosna.pinetrail.utils.logging.Markers;
import ws.sosna.pinetrail.utils.logging.StatusCodes;

/**
 * A list of formats supplying information about {@code Trail}s and {@code Waypoint}s.
 *
 * @author Xavier Sosnovsky
 */
public enum Formats {

  /** Version 1.0 of the GPS Exchange Format standard, maintained by TopoGrafix. */
  GPX_1_0,
  /** Version 1.1 of the GPS Exchange Format standard, maintained by TopoGrafix. */
  GPX_1_1,
  /** A geospatial data interchange format based on JavaScript Object Notation. */
  GEOJSON_1_0,
  /**
   * Version 2.1.0 of the Keyhole Markup Language, an international standard of the Open Geospatial
   * Consortium, developed by Keyhole Inc and Google.
   */
  KML_2_1_0,
  /**
   * Version 2.2.0 of the Keyhole Markup Language, an international standard of the Open Geospatial
   * Consortium, developed by Keyhole Inc and Google.
   */
  KML_2_2_0;

  /**
   * Guess the format of the file stored at the supplied location.
   *
   * <p>This is a helper method so as to be able to call the {@link
   * Readers#newReader(Formats)} method when the format is unknown.
   *
   * @param fileLocation the file whose format needs to be guessed
   * @return the format of the file stored at the supplied location
   * @throws ExecutionError if the format cannot be guessed or the file is not found.
   */
  public static Formats of(final Path fileLocation) {
    try {
      final String content = new String(Files.readAllBytes(fileLocation));
      if (content.contains("http://www.topografix.com/GPX/1/1")) {
        return GPX_1_1;
      } else if (content.contains("http://www.topografix.com/GPX/1/0")) {
        return GPX_1_0;
      } else {
        throw new ExecutionError(
            "The file does '"
                + fileLocation.toAbsolutePath().normalize()
                + " does not seem to be in "
                + "one of the supported formats.",
            null,
            Markers.IO.getMarker(),
            Actions.OPEN,
            StatusCodes.NOT_ACCEPTABLE);
      }
    } catch (IOException ex) {
      throw new ExecutionError(
          "Could not find file " + fileLocation.toAbsolutePath().normalize(),
          null,
          Markers.IO.getMarker(),
          Actions.OPEN,
          StatusCodes.NOT_FOUND);
    }
  }
}
