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
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.LoggerFactory;
import ws.sosna.pinetrail.model.GpsRecord;
import ws.sosna.pinetrail.utils.logging.Actions;
import ws.sosna.pinetrail.utils.logging.Markers;
import ws.sosna.pinetrail.utils.logging.StatusCodes;

/**
 * Performs mapping to the Pinetrail model.
 *
 * @author Xavier Sosnovsky
 */
final class FromJpx {

  private final ResourceBundle logMessages;
  private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(FromJpx.class);

  FromJpx() {
    super();
    logMessages = ResourceBundle.getBundle("GpxLogMessages", Locale.getDefault());
  }

  Set<GpsRecord> map(final GPX gpx) {
    final Set<GpsRecord> records = new LinkedHashSet<>();
    if (gpx.getTracks().isEmpty()) {
      LOGGER.warn(
          Markers.IO.getMarker(),
          "{} | {} | {}.",
          Actions.PARSE,
          StatusCodes.NOT_FOUND.getCode(),
          logMessages.getString("Error.NoTrack"));
    } else {
      for (final Track trk : gpx.getTracks()) {
        records.addAll(handleTrack(trk));
      }
    }
    return records;
  }

  private Set<GpsRecord> handleTrack(final Track trk) {
    if (trk.getSegments().isEmpty()) {
      LOGGER.warn(
          Markers.IO.getMarker(),
          "{} | {} | {}.",
          Actions.PARSE,
          StatusCodes.NOT_FOUND.getCode(),
          logMessages.getString("Error.NoSegment"));
    }
    return handleSegments(trk);
  }

  private Set<GpsRecord> handleSegments(final Track trk) {
    final Set<GpsRecord> records = new LinkedHashSet<>();
    for (final TrackSegment seg : trk.getSegments()) {
      final Set<GpsRecord> segPoints =
          seg.getPoints().stream().map(this::handlePoint).collect(Collectors.toSet());
      records.addAll(segPoints);
    }
    return records;
  }

  private GpsRecord handlePoint(final WayPoint wpt) {
    final Instant time = wpt.getTime().isPresent() ? wpt.getTime().get().toInstant() : null;
    final Double ele =
        wpt.getElevation().isPresent() ? wpt.getElevation().get().doubleValue() : null;
    return GpsRecord.of(
        time, wpt.getLongitude().doubleValue(), wpt.getLatitude().doubleValue(), ele);
  }
}
