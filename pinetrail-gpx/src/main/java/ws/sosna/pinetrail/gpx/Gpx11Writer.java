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
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.sosna.pinetrail.api.io.Writer;
import ws.sosna.pinetrail.model.GpsRecord;
import ws.sosna.pinetrail.utils.error.ExecutionError;
import ws.sosna.pinetrail.utils.logging.Actions;
import ws.sosna.pinetrail.utils.logging.Markers;
import ws.sosna.pinetrail.utils.logging.StatusCodes;

/**
 * Writes a trail in GPX 1.1 format.
 *
 * @author Xavier Sosnovsky
 */
final class Gpx11Writer implements Writer {

  private static final Logger LOGGER = LoggerFactory.getLogger(Gpx11Writer.class);

  Gpx11Writer() {
    super();
  }

  @Override
  public void accept(final Set<GpsRecord> records, final Path location) {
    try {
      LOGGER.info(
          Markers.IO.getMarker(),
          "{} | {} | {}.",
          Actions.CREATE,
          StatusCodes.OK.getCode(),
          "Started writing " + "GPX 1.1 file " + location.toAbsolutePath().normalize().toString());
      final long start = System.currentTimeMillis();
      final List<WayPoint> pts = records.stream().map(this::getPoint).collect(Collectors.toList());
      final TrackSegment seg = TrackSegment.builder().points(pts).build();
      final Track track = Track.builder().addSegment(seg).build();
      final GPX gpx = GPX.builder().addTrack(track).build();
      GPX.write(gpx, location);
      final long end = System.currentTimeMillis();
      LOGGER.info(
          Markers.PERFORMANCE.getMarker(),
          "{} | {} | {}.",
          Actions.CREATE,
          StatusCodes.OK.getCode(),
          "Written "
              + location.toAbsolutePath().normalize().toString()
              + " in "
              + (end - start)
              + "ms");
    } catch (final IOException e) {
      LOGGER.error(
          Markers.IO.getMarker(),
          "{} | {} | {}.",
          Actions.CREATE,
          StatusCodes.INTERNAL_ERROR.getCode(),
          e.getMessage());
      throw new ExecutionError(
          e.getMessage(), e, Markers.IO.getMarker(), Actions.CREATE, StatusCodes.SYNTAX_ERROR);
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

  private WayPoint getPoint(final GpsRecord pt) {
    return WayPoint.builder()
        .lat(pt.getLatitude())
        .lon(pt.getLongitude())
        .ele(pt.getElevation())
        .time(pt.getTime())
        .build();
  }
}
