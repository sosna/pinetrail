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
import io.jenetics.jpx.Link;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.sosna.pinetrail.api.io.Writer;
import ws.sosna.pinetrail.api.io.WriterSettings;
import ws.sosna.pinetrail.api.io.WriterSettingsBuilder;
import ws.sosna.pinetrail.model.Trail;
import ws.sosna.pinetrail.model.Waypoint;
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
  private WriterSettings settings;

  Gpx11Writer() {
    super();
    this.settings = new WriterSettingsBuilder().build();
  }

  @Override
  public Writer configure(final WriterSettings settings) {
    this.settings = settings;
    return this;
  }

  @Override
  public void accept(final Trail trail, final Path location) {
    try {
      LOGGER.info(
          Markers.IO.getMarker(),
          "{} | {} | {}.",
          Actions.CREATE,
          StatusCodes.OK.getCode(),
          "Started writing " + "GPX 1.1 file " + location.toAbsolutePath().normalize().toString());
      if (!settings.overwriteIfExists() && Files.exists(location)) {
        final String msg =
            location.toAbsolutePath().normalize().toString()
                + " already exists and writer is not allowed"
                + " to overwrite existing files";
        throw new ExecutionError(
            msg, null, Markers.IO.getMarker(), Actions.CREATE, StatusCodes.SYNTAX_ERROR);
      }
      final long start = System.currentTimeMillis();
      final List<WayPoint> pts = getPoints(trail);
      final TrackSegment seg = TrackSegment.builder().points(pts).build();
      final List<Link> links =
          trail.getLinks().stream().map(this::getLink).collect(Collectors.toList());
      final Track track =
          Track.builder()
              .addSegment(seg)
              .name(trail.getName())
              .desc(trail.getDescription())
              .links(links)
              .build();
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

  private List<WayPoint> getPoints(final Trail trail) {
    Set<Waypoint> points = handleOutliers(trail);
    points = handleIdlePoints(points);
    return points.stream().map(this::getPoint).collect(Collectors.toList());
  }

  private SortedSet<Waypoint> handleIdlePoints(final Set<Waypoint> input) {
    final SortedSet<Waypoint> points = new TreeSet<>();
    if (settings.writeIdlePoints()) {
      points.addAll(input);
    } else {
      points.addAll(input.stream().filter(Waypoint::isActive).collect(Collectors.toSet()));
    }
    return points;
  }

  private Set<Waypoint> getOutliers(final Trail trail) {
    final Set<Waypoint> outliers = new LinkedHashSet<>();
    outliers.addAll(trail.getStatistics().getDistanceSummary().getOutliers());
    outliers.addAll(trail.getStatistics().getElevationDifferenceSummary().getOutliers());
    outliers.addAll(trail.getStatistics().getElevationSummary().getOutliers());
    outliers.addAll(trail.getStatistics().getGradeSummary().getOutliers());
    outliers.addAll(trail.getStatistics().getSpeedSummary().getOutliers());
    return outliers;
  }

  private Set<Waypoint> handleOutliers(final Trail trail) {
    final Set<Waypoint> outliers = getOutliers(trail);
    final SortedSet<Waypoint> points = new TreeSet<>();
    if (settings.writeOutliers()) {
      points.addAll(trail.getWaypoints());
    } else {
      points.addAll(
          trail.getWaypoints().stream()
              .filter(item -> !(outliers.contains(item)))
              .collect(Collectors.toSet()));
    }
    return points;
  }

  private WayPoint getPoint(final Waypoint pt) {
    return WayPoint.builder()
        .lat(pt.getCoordinates().getLatitude())
        .lon(pt.getCoordinates().getLongitude())
        .ele(pt.getCoordinates().getElevation())
        .time(pt.getTime())
        .build();
  }

  private Link getLink(final ws.sosna.pinetrail.model.Link ln) {
    return Link.of(ln.getLocation(), ln.getLabel(), "text/plain");
  }
}
