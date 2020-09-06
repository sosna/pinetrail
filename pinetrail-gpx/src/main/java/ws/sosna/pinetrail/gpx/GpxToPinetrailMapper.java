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
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ValidationException;
import org.slf4j.LoggerFactory;
import ws.sosna.pinetrail.model.CoordinatesBuilder;
import ws.sosna.pinetrail.model.Link;
import ws.sosna.pinetrail.model.LinkBuilder;
import ws.sosna.pinetrail.model.Trail;
import ws.sosna.pinetrail.model.TrailBuilder;
import ws.sosna.pinetrail.model.Waypoint;
import ws.sosna.pinetrail.model.WaypointBuilder;
import ws.sosna.pinetrail.utils.logging.Actions;
import ws.sosna.pinetrail.utils.logging.Markers;
import ws.sosna.pinetrail.utils.logging.StatusCodes;

/**
 * Performs mapping to the Pinetrail model.
 *
 * @author Xavier Sosnovsky
 */
final class GpxToPinetrailMapper {

  private final ResourceBundle logMessages;
  private final boolean groupSubTrails;
  private static final org.slf4j.Logger LOGGER =
      LoggerFactory.getLogger(GpxToPinetrailMapper.class);

  GpxToPinetrailMapper(final boolean groupSubTrails) {
    super();
    logMessages = ResourceBundle.getBundle("GpxLogMessages", Locale.getDefault());
    this.groupSubTrails = groupSubTrails;
  }

  Set<Trail> mapToTrails(final GPX gpx) {
    final Set<Trail> trails = new LinkedHashSet<>();
    final Set<Waypoint> waypoints = new LinkedHashSet<>();
    if (!gpx.getWayPoints().isEmpty() && 1 < gpx.getTracks().size()) {
      LOGGER.warn(
          Markers.IO.getMarker(),
          "{} | {} | {}.",
          Actions.PARSE,
          StatusCodes.NOT_ACCEPTABLE.getCode(),
          logMessages.getString("Error.WaypointsForManyTracks"));
    } else {
      waypoints.addAll(
          gpx.getWayPoints().stream()
              .map(this::handlePoint)
              .filter(Objects::nonNull)
              .collect(Collectors.toSet()));
    }
    if (gpx.getTracks().isEmpty()) {
      LOGGER.warn(
          Markers.IO.getMarker(),
          "{} | {} | {}.",
          Actions.PARSE,
          StatusCodes.NOT_FOUND.getCode(),
          logMessages.getString("Error.NoTrack"));
    } else {
      for (final Track trk : gpx.getTracks()) {
        trails.addAll(handleTrack(trk, waypoints));
      }
    }
    if (!(gpx.getRoutes().isEmpty())) {
      LOGGER.warn(
          Markers.IO.getMarker(),
          "{} | {} | {}.",
          Actions.PARSE,
          StatusCodes.NOT_ACCEPTABLE.getCode(),
          logMessages.getString("Error.Route"));
    }
    return trails;
  }

  private Trail getTrail(
      final String trailName,
      final Set<Waypoint> points,
      final String description,
      final Set<Link> links) {
    Trail trail;
    if (points.isEmpty()) {
      LOGGER.warn(
          Markers.IO.getMarker(),
          "{} | {} | {}.",
          Actions.PARSE,
          StatusCodes.NOT_FOUND.getCode(),
          logMessages.getString("Error.NoPoint"));
      trail = null;
    } else {
      try {
        trail = new TrailBuilder(trailName, points).description(description).links(links).build();
        LOGGER.info(
            Markers.IO.getMarker(),
            "{} | {} | {}.",
            Actions.PARSE,
            StatusCodes.OK.getCode(),
            "Mapped one trail," + " containing " + points.size() + " point(s)");
      } catch (final ValidationException e) {
        final String msg =
            "Bean validation failed. Trail will be "
                + "ignored. Problem was: "
                + e.getLocalizedMessage();
        LOGGER.warn(
            Markers.IO.getMarker(),
            "{} | {} | {}",
            Actions.CREATE,
            StatusCodes.SYNTAX_ERROR.getCode(),
            msg);
        trail = null;
      }
    }
    return trail;
  }

  private Waypoint buildPoint(
      final Instant time,
      final double lon,
      final double lat,
      final Double ele,
      final String name,
      final String desc,
      final Set<Link> links) {
    final CoordinatesBuilder cbld = new CoordinatesBuilder(lon, lat);
    if (null != ele) {
      cbld.elevation(ele);
    }
    try {
      return new WaypointBuilder(time, cbld.build())
          .name(name)
          .description(desc)
          .links(links)
          .build();
    } catch (final ValidationException e) {
      final String msg =
          "Bean validation failed. Waypoint will be "
              + "ignored. Problem was: "
              + e.getLocalizedMessage();
      LOGGER.warn(
          Markers.IO.getMarker(),
          "{} | {} | {}",
          Actions.CREATE,
          StatusCodes.SYNTAX_ERROR.getCode(),
          msg);
      return null;
    }
  }

  private Set<Waypoint> handleAdditionalWaypoints(
      final boolean multipleSegments, final Set<Waypoint> waypoints) {
    final Set<Waypoint> trkPoints = new LinkedHashSet<>();
    if (multipleSegments && !groupSubTrails && !waypoints.isEmpty()) {
      LOGGER.warn(
          Markers.IO.getMarker(),
          "{} | {} | {}.",
          Actions.PARSE,
          StatusCodes.NOT_ACCEPTABLE.getCode(),
          logMessages.getString("Error.WaypointsForManySegs"));
    } else {
      trkPoints.addAll(waypoints);
    }
    return trkPoints;
  }

  private Set<Trail> handleTrack(final Track trk, final Set<Waypoint> waypoints) {
    if (trk.getSegments().isEmpty()) {
      LOGGER.warn(
          Markers.IO.getMarker(),
          "{} | {} | {}.",
          Actions.PARSE,
          StatusCodes.NOT_FOUND.getCode(),
          logMessages.getString("Error.NoSegment"));
    }
    final Set<Link> links =
        trk.getLinks().stream()
            .map(this::handleLink)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    final Set<Waypoint> points = handleAdditionalWaypoints(trk.getSegments().size() > 1, waypoints);
    return handleSegments(trk, points, links);
  }

  private Set<Trail> handleSegments(
      final Track trk, final Set<Waypoint> points, final Set<Link> links) {
    int count = 1;
    final String trailName = trk.getName().orElse(null);
    final Set<Trail> trails = new LinkedHashSet<>();
    for (final TrackSegment seg : trk.getSegments()) {
      final Set<Waypoint> segPoints =
          seg.getPoints().stream()
              .map(this::handlePoint)
              .filter(Objects::nonNull)
              .collect(Collectors.toSet());
      if (!groupSubTrails && 1 < trk.getSegments().size()) {
        points.clear();
      }
      points.addAll(segPoints);
      if (!groupSubTrails) {
        final String segName =
            trk.getSegments().size() > 1 ? trailName + " [" + count++ + "]" : trailName;
        final Trail trail = getTrail(segName, points, trk.getDescription().orElse(null), links);
        if (null != trail) {
          trails.add(trail);
        }
      }
    }
    if (groupSubTrails) {
      final Trail trail = getTrail(trailName, points, trk.getDescription().orElse(null), links);
      if (null != trail) {
        trails.add(trail);
      }
    }
    return trails;
  }

  @SuppressWarnings("PMD.LawOfDemeter")
  private Waypoint handlePoint(final WayPoint wpt) {
    final Set<Link> links =
        wpt.getLinks().stream()
            .map(this::handleLink)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    final Instant time = wpt.getTime().isPresent() ? wpt.getTime().get().toInstant() : null;
    final Double ele =
        wpt.getElevation().isPresent() ? wpt.getElevation().get().doubleValue() : null;
    return buildPoint(
        time,
        wpt.getLongitude().doubleValue(),
        wpt.getLatitude().doubleValue(),
        ele,
        wpt.getName().orElse(null),
        wpt.getDescription().orElse(null),
        links);
  }

  @SuppressWarnings("PMD.LawOfDemeter")
  private Link handleLink(final io.jenetics.jpx.Link link) {
    try {
      return new LinkBuilder(link.getText().orElse(null), new URI(link.getHref().toString())).build();
    } catch (final URISyntaxException e) {
      final String logMsg =
          logMessages.getString("Error.LinkURI")
              + "("
              + link.getHref()
              + "). Error was: "
              + e.getMessage();
      LOGGER.warn(
          Markers.IO.getMarker(),
          "{} | {} | {}.",
          Actions.PARSE,
          StatusCodes.SYNTAX_ERROR.getCode(),
          logMsg);
      return null;
    }
  }
}
