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
package ws.sosna.pinetrail.model;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.sosna.pinetrail.utils.logging.Actions;
import ws.sosna.pinetrail.utils.logging.Markers;
import ws.sosna.pinetrail.utils.logging.StatusCodes;

/**
 * Adds information to waypoints, such as speed, distance, grade and elevation difference.
 *
 * @author Xavier Sosnovsky
 */
enum PointsAugmenter implements Function<SortedSet<Waypoint>, SortedSet<Waypoint>> {

  /** Singleton that returns an instance of a PointsAugmenter. */
  INSTANCE;

  private static final Logger LOGGER = LoggerFactory.getLogger(PointsAugmenter.class);
  private static final int KM2M = 1000;
  private static final int EARTH_RADIUS = 6371;
  /** From meters per second to km per hours. */
  private static final double MS2KMH = 3.6;
  /** Default minimum speed, in km/h, to be considered in movement. */
  private static final double ACTIVE_SPEED = 1.5;

  /**
   * Add distance, speed, grade, time difference and elevation difference to the points contained in
   * the supplied collection.
   *
   * @param points the collection of points that will be augmented
   * @return the collection of augmented points
   */
  @Override
  public SortedSet<Waypoint> apply(final SortedSet<Waypoint> points) {
    final SortedSet<Waypoint> output = new TreeSet<>();
    if (!points.isEmpty()) {
      final List<Waypoint> received = new ArrayList<>(points);
      output.add(handleFirstPoint(received.get(0)));
      for (int i = 1; i < received.size(); i++) {
        output.add(augmentPoint(received.get(i), received.get(i - 1)));
      }
    }
    return output;
  }

  private Waypoint handleFirstPoint(final Waypoint current) {
    return WaypointBuilder.of(current)
        .distance(0.0)
        .elevationDifference(0.0)
        .grade(0.0)
        .speed(0.0)
        .isActive(false)
        .timeDifference(0)
        .build();
  }

  private Waypoint augmentPoint(final Waypoint current, final Waypoint previous) {
    final Double distance = calculateDistance(current, previous);
    final Double eleDiff = calculateEleDiff(current, previous);
    final Double grade = calculateGrade(distance, eleDiff);
    final long elapsed = Duration.between(previous.getTime(), current.getTime()).getSeconds();
    final Double speed = calculateSpeed(distance, elapsed);
    final boolean isActive = isActive(speed);
    final WaypointBuilder bld = WaypointBuilder.of(current);
    return bld.distance(distance)
        .elevationDifference(eleDiff)
        .grade(grade)
        .speed(speed)
        .isActive(isActive)
        .timeDifference(elapsed)
        .build();
  }

  /**
   * Java implementation of the JavaScript formula kindly published on movable type:
   * http://www.movable-type.co.uk/scripts/latlong.html.
   */
  private Double calculateDistance(final Waypoint p1, final Waypoint p2) {
    final double dLat =
        Math.toRadians(p2.getCoordinates().getLatitude() - p1.getCoordinates().getLatitude());
    final double dLon =
        Math.toRadians(p2.getCoordinates().getLongitude() - p1.getCoordinates().getLongitude());
    final double lat1 = Math.toRadians(p1.getCoordinates().getLatitude());
    final double lat2 = Math.toRadians(p2.getCoordinates().getLatitude());

    final double a =
        Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + Math.cos(lat1) * Math.cos(lat2) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
    final double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return EARTH_RADIUS * KM2M * c;
  }

  private Double calculateEleDiff(final Waypoint current, final Waypoint previous) {
    if (null == current.getCoordinates().getElevation()
        || null == previous.getCoordinates().getElevation()) {
      LOGGER.warn(
          Markers.MODEL.getMarker(),
          "{} | {} | Missing "
              + "elevation information for this and/or the previous "
              + "point(s). Elevation difference set to 0 for point {}",
          Actions.ANALYSE,
          StatusCodes.NOT_FOUND.getCode(),
          current.getTime());
      return 0.0;
    } else {
      return current.getCoordinates().getElevation() - previous.getCoordinates().getElevation();
    }
  }

  private Double calculateGrade(final Double distance, final Double elevationDifference) {
    if (0 == distance) {
      return 0.0;
    } else {
      return Math.toDegrees(Math.atan(elevationDifference / distance));
    }
  }

  private Double calculateSpeed(final Double distance, final long elapsed) {
    // Distance in m and duration in sec converted to km/h.
    return elapsed > 0 ? (distance / elapsed) * MS2KMH : 0;
  }

  private boolean isActive(final Double speed) {
    return speed >= ACTIVE_SPEED;
  }
}
