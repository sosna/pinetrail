package ws.sosna.pinetrail.analysis;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.junit.Test;
import ws.sosna.pinetrail.model.GpsRecord;
import ws.sosna.pinetrail.model.Waypoint;

/** @author Xavier Sosnovsky */
public class PointsAugmenterTest {

  @Test
  public void calculateDistance() {
    final GpsRecord point1 = GpsRecord.of(Instant.MIN, 50.1181330718, 7.9631000385);
    final GpsRecord point2 = GpsRecord.of(Instant.MIN, 50.1181969419, 7.9630940873);
    final SortedSet<GpsRecord> points = new TreeSet<>();
    points.add(point1);
    points.add(point2);
    final Set<Waypoint> augmentedPoints = PointsAugmenter.valueOf("INSTANCE").apply(points);
    for (final Waypoint point : augmentedPoints) {
      assertTrue(7.064232421078554 == point.getDistance() || 0 == point.getDistance());
    }
  }

  @Test
  public void calculateSpeed() {
    final GpsRecord point1 = GpsRecord.of(Instant.parse("2013-10-03T08:02:28Z"), 50.212403331, 8.2487693802, 344.77);
    final GpsRecord point2 = GpsRecord.of(Instant.parse("2013-10-03T08:02:45Z"), 50.2122325078, 8.2485254668, 344.29);
    final SortedSet<GpsRecord> points = new TreeSet<>();
    points.add(point1);
    points.add(point2);
    final Set<Waypoint> augmentedPoints = PointsAugmenter.INSTANCE.apply(points);
    for (final Waypoint point : augmentedPoints) {
      assertTrue(6.9881402785419 == point.getSpeed() || 0 == point.getSpeed());
    }
  }

  @Test
  public void calculateSpeedNoDistance() {
    final GpsRecord point1 = GpsRecord.of(Instant.parse("2013-10-03T08:02:28Z"), 50.2122325078, 8.2485254668, 344.29);
    final GpsRecord point2 = GpsRecord.of(Instant.parse("2013-10-03T08:02:45Z"), 50.2122325078, 8.2485254668, 0.0);
    final SortedSet<GpsRecord> points = new TreeSet<>();
    points.add(point1);
    points.add(point2);
    final Set<Waypoint> augmentedPoints = PointsAugmenter.INSTANCE.apply(points);
    for (final Waypoint point : augmentedPoints) {
      assertEquals(0, point.getSpeed(), 0.0);
    }
  }

  @Test
  public void calculateDistanceHigher() {
    final GpsRecord point1 = GpsRecord.of(Instant.MIN, 50.1181330718, 7.9631000385, 10.0);
    final GpsRecord point2 = GpsRecord.of(Instant.MIN, 50.1181969419, 7.9630940873, 12.0);
    final SortedSet<GpsRecord> points = new TreeSet<>();
    points.add(point1);
    points.add(point2);
    final Set<Waypoint> augmentedPoints = PointsAugmenter.INSTANCE.apply(points);
    for (final Waypoint point : augmentedPoints) {
      assertTrue(0 == point.getElevationDifference() || 2 == point.getElevationDifference());
    }
  }

  @Test
  public void calculateDistanceLower() {
    final GpsRecord point1 = GpsRecord.of(Instant.MIN, 50.1181330718, 7.9631000385, 10.0);
    final GpsRecord point2 = GpsRecord.of(Instant.MIN, 50.1181969419, 7.9630940873, 8.0);
    final SortedSet<GpsRecord> points = new TreeSet<>();
    points.add(point1);
    points.add(point2);
    final Set<Waypoint> augmentedPoints = PointsAugmenter.INSTANCE.apply(points);
    for (final Waypoint point : augmentedPoints) {
      assertTrue(0 == point.getElevationDifference() || -2 == point.getElevationDifference());
    }
  }

  @Test
  public void calculateDistanceBelowZero() {
    final GpsRecord point1 = GpsRecord.of(Instant.MIN, 50.1181330718, 7.9631000385, 10.0);
    final GpsRecord point2 = GpsRecord.of(Instant.MIN, 50.1181969419, 7.9630940873, -8.0);
    final SortedSet<GpsRecord> points = new TreeSet<>();
    points.add(point1);
    points.add(point2);
    final Set<Waypoint> augmentedPoints = PointsAugmenter.INSTANCE.apply(points);
    for (final Waypoint point : augmentedPoints) {
      assertTrue(0 == point.getElevationDifference() || -18 == point.getElevationDifference());
    }
  }

  @Test
  public void calculateNullDistance() {
    final GpsRecord point1 = GpsRecord.of(Instant.MIN, 50.1181330718, 7.9631000385);
    final GpsRecord point2 = GpsRecord.of(Instant.MIN, 50.1181969419, 7.9630940873);
    final SortedSet<GpsRecord> points = new TreeSet<>();
    points.add(point1);
    points.add(point2);
    final Set<Waypoint> augmentedPoints = PointsAugmenter.INSTANCE.apply(points);
    for (final Waypoint point : augmentedPoints) {
      assertEquals(0, point.getElevationDifference(), 0.0);
    }
  }

  @Test
  public void secondDistanceNull() {
    final GpsRecord point1 = GpsRecord.of(Instant.MIN, 50.1181330718, 7.9631000385);
    final GpsRecord point2 = GpsRecord.of(Instant.MIN, 50.1181969419, 7.9630940873, 123.4);
    final SortedSet<GpsRecord> points = new TreeSet<>();
    points.add(point1);
    points.add(point2);
    final Set<Waypoint> augmentedPoints = PointsAugmenter.INSTANCE.apply(points);
    for (final Waypoint point : augmentedPoints) {
      assertEquals(0, point.getElevationDifference(), 0.0);
    }

    final SortedSet<GpsRecord> points2 = new TreeSet<>();
    points2.add(point2);
    points2.add(point1);
    final Set<Waypoint> augmentedPoints2 = PointsAugmenter.INSTANCE.apply(points2);
    for (final Waypoint point : augmentedPoints2) {
      assertEquals(0, point.getElevationDifference(), 0.0);
    }
  }
}
