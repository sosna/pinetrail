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

import io.jenetics.jpx.GPX;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.sosna.pinetrail.api.io.Reader;
import ws.sosna.pinetrail.model.GpsRecord;
import ws.sosna.pinetrail.utils.error.ExecutionError;
import ws.sosna.pinetrail.utils.logging.Actions;
import ws.sosna.pinetrail.utils.logging.Markers;
import ws.sosna.pinetrail.utils.logging.StatusCodes;

/**
 * Reader of GPX files that map the extracted information to the Pinetrail model.
 *
 * @author Xavier Sosnovsky
 */
class GpxReader implements Reader {

  private static final Logger LOGGER = LoggerFactory.getLogger(GpxReader.class);
  private final GPX.Version version;

  GpxReader(final GPX.Version version) {
    super();
    this.version = version;
  }

  /** {@inheritDoc} */
  @Override
  public Set<GpsRecord> apply(final Path fileLocation) {
    try {
      LOGGER.info(
          Markers.IO.getMarker(),
          "{} | {} | {}.",
          Actions.PARSE,
          StatusCodes.OK.getCode(),
          "Started parsing GPX file " + fileLocation.toAbsolutePath().normalize().toString());
      final long start = System.currentTimeMillis();
      final GPX gpx = GPX.reader(version).read(fileLocation);
      final long parsingTime = System.currentTimeMillis() - start;
      final long startMapping = System.currentTimeMillis();
      final Set<GpsRecord> records = new FromJpx().map(gpx);
      final long end = System.currentTimeMillis();
      LOGGER.info(
          Markers.PERFORMANCE.getMarker(),
          "{} | {} | {}",
          Actions.PARSE,
          StatusCodes.OK.getCode(),
          "Processed "
              + fileLocation.toAbsolutePath().normalize().toString()
              + " in "
              + (end - start)
              + "ms (parsing: "
              + parsingTime
              + " - mapping: "
              + (end - startMapping)
              + ")");
      return records;
    } catch (IOException e) {
      throw new ExecutionError(
          "Could not read file", e, Markers.IO.getMarker(), Actions.GET, StatusCodes.SYNTAX_ERROR);
    } catch (final ExecutionError e) {
      LOGGER.error(
          e.getMarker(),
          "{} | {} | {}.",
          e.getAction(),
          e.getErrorCode().getCode(),
          e.getMessage() + (null == e.getCause() ? "" : ": " + e.getCause().getMessage()));
      throw e;
    }
  }
}
