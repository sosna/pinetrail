package ws.sosna.pinetrail.model;

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

import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.junit.Test;

/** @author Xavier Sosnovsky */
public class PointsAugmenterTest {

  @Test
  public void calculateDistance() {
    final Waypoint point1 =
        new WaypointBuilder(
                Instant.MIN, new CoordinatesBuilder(50.1181330718, 7.9631000385).build())
            .build();
    final Waypoint point2 =
        new WaypointBuilder(
                Instant.MIN, new CoordinatesBuilder(50.1181969419, 7.9630940873).build())
            .build();
    final SortedSet<Waypoint> points = new TreeSet<>();
    points.add(point1);
    points.add(point2);
    final Set<Waypoint> augmentedPoints = PointsAugmenter.valueOf("INSTANCE").apply(points);
    for (final Waypoint point : augmentedPoints) {
      assertTrue(7.064232421078554 == point.getDistance() || 0 == point.getDistance());
    }
  }

  @Test
  public void calculateSpeed() {
    final Waypoint point1 =
        new WaypointBuilder(
                Instant.parse("2013-10-03T08:02:28Z"),
                new CoordinatesBuilder(50.212403331, 8.2487693802).elevation(344.77).build())
            .build();
    final Waypoint point2 =
        new WaypointBuilder(
                Instant.parse("2013-10-03T08:02:45Z"),
                new CoordinatesBuilder(50.2122325078, 8.2485254668).elevation(344.29).build())
            .build();
    final SortedSet<Waypoint> points = new TreeSet<>();
    points.add(point1);
    points.add(point2);
    final Set<Waypoint> augmentedPoints = PointsAugmenter.INSTANCE.apply(points);
    for (final Waypoint point : augmentedPoints) {
      assertTrue(6.9881402785419 == point.getSpeed() || 0 == point.getSpeed());
    }
  }

  @Test
  public void calculateSpeedNoDistance() {
    final Waypoint point1 =
        new WaypointBuilder(
                Instant.parse("2013-10-03T08:02:28Z"),
                new CoordinatesBuilder(50.2122325078, 8.2485254668).elevation(344.29).build())
            .build();
    final Waypoint point2 =
        new WaypointBuilder(
                Instant.parse("2013-10-03T08:02:45Z"),
                new CoordinatesBuilder(50.2122325078, 8.2485254668).elevation(344.29).build())
            .distance(0.0)
            .build();
    final SortedSet<Waypoint> points = new TreeSet<>();
    points.add(point1);
    points.add(point2);
    final Set<Waypoint> augmentedPoints = PointsAugmenter.INSTANCE.apply(points);
    for (final Waypoint point : augmentedPoints) {
      assertTrue(0 == point.getSpeed());
    }
  }

  @Test
  public void calculateDistanceHigher() {
    final Waypoint point1 =
        new WaypointBuilder(
                Instant.MIN,
                new CoordinatesBuilder(50.1181330718, 7.9631000385).elevation(10.0).build())
            .build();
    final Waypoint point2 =
        new WaypointBuilder(
                Instant.MIN,
                new CoordinatesBuilder(50.1181969419, 7.9630940873).elevation(12.0).build())
            .build();
    final SortedSet<Waypoint> points = new TreeSet<>();
    points.add(point1);
    points.add(point2);
    final Set<Waypoint> augmentedPoints = PointsAugmenter.INSTANCE.apply(points);
    for (final Waypoint point : augmentedPoints) {
      assertTrue(0 == point.getElevationDifference() || 2 == point.getElevationDifference());
    }
  }

  @Test
  public void calculateDistanceLower() {
    final Waypoint point1 =
        new WaypointBuilder(
                Instant.MIN,
                new CoordinatesBuilder(50.1181330718, 7.9631000385).elevation(10.0).build())
            .build();
    final Waypoint point2 =
        new WaypointBuilder(
                Instant.MIN,
                new CoordinatesBuilder(50.1181969419, 7.9630940873).elevation(8.0).build())
            .build();
    final SortedSet<Waypoint> points = new TreeSet<>();
    points.add(point1);
    points.add(point2);
    final Set<Waypoint> augmentedPoints = PointsAugmenter.INSTANCE.apply(points);
    for (final Waypoint point : augmentedPoints) {
      assertTrue(0 == point.getElevationDifference() || -2 == point.getElevationDifference());
    }
  }

  @Test
  public void calculateDistanceBelowZero() {
    final Waypoint point1 =
        new WaypointBuilder(
                Instant.MIN,
                new CoordinatesBuilder(50.1181330718, 7.9631000385).elevation(10.0).build())
            .build();
    final Waypoint point2 =
        new WaypointBuilder(
                Instant.MIN,
                new CoordinatesBuilder(50.1181969419, 7.9630940873).elevation(-8.0).build())
            .build();
    final SortedSet<Waypoint> points = new TreeSet<>();
    points.add(point1);
    points.add(point2);
    final Set<Waypoint> augmentedPoints = PointsAugmenter.INSTANCE.apply(points);
    for (final Waypoint point : augmentedPoints) {
      assertTrue(0 == point.getElevationDifference() || -18 == point.getElevationDifference());
    }
  }

  @Test
  public void calculateNullDistance() {
    final Waypoint point1 =
        new WaypointBuilder(
                Instant.MIN, new CoordinatesBuilder(50.1181330718, 7.9631000385).build())
            .build();
    final Waypoint point2 =
        new WaypointBuilder(
                Instant.MIN, new CoordinatesBuilder(50.1181969419, 7.9630940873).build())
            .build();
    final SortedSet<Waypoint> points = new TreeSet<>();
    points.add(point1);
    points.add(point2);
    final Set<Waypoint> augmentedPoints = PointsAugmenter.INSTANCE.apply(points);
    for (final Waypoint point : augmentedPoints) {
      assertTrue(0 == point.getElevationDifference());
    }
  }

  @Test
  public void secondDistanceNull() {
    final Waypoint point1 =
        new WaypointBuilder(
                Instant.MIN, new CoordinatesBuilder(50.1181330718, 7.9631000385).build())
            .build();
    final Waypoint point2 =
        new WaypointBuilder(
                Instant.MIN,
                new CoordinatesBuilder(50.1181969419, 7.9630940873).elevation(123.4).build())
            .build();
    final SortedSet<Waypoint> points = new TreeSet<>();
    points.add(point1);
    points.add(point2);
    final Set<Waypoint> augmentedPoints = PointsAugmenter.INSTANCE.apply(points);
    for (final Waypoint point : augmentedPoints) {
      assertTrue(0 == point.getElevationDifference());
    }

    final SortedSet<Waypoint> points2 = new TreeSet<>();
    points2.add(point2);
    points2.add(point1);
    final Set<Waypoint> augmentedPoints2 = PointsAugmenter.INSTANCE.apply(points2);
    for (final Waypoint point : augmentedPoints2) {
      assertTrue(0 == point.getElevationDifference());
    }
  }
}
