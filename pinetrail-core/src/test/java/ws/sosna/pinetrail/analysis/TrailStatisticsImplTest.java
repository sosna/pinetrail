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
import java.util.SortedSet;
import java.util.TreeSet;
import org.junit.Test;
import ws.sosna.pinetrail.model.GpsRecord;
import ws.sosna.pinetrail.model.Waypoint;

/** @author Xavier Sosnovsky */
public class TrailStatisticsImplTest {

  @Test
  public void getSpeedSummary() {
    final Statistics speed = getStats().getSpeedSummary();
    assertTrue(speed.getOutliers().isEmpty());
    assertEquals(5, speed.getAll().getN());
    assertEquals(3, speed.getActive().getN());
    assertEquals(1, speed.getActiveDown().getN());
    assertEquals(0, speed.getActiveUp().getN());
    assertEquals(2, speed.getActiveFlat().getN());
  }

  @Test
  public void getDistanceSummary() {
    final Statistics dist = getStats().getDistanceSummary();
    assertTrue(dist.getOutliers().isEmpty());
    assertEquals(5, dist.getAll().getN());
    assertEquals(3, dist.getActive().getN());
    assertEquals(1, dist.getActiveDown().getN());
    assertEquals(0, dist.getActiveUp().getN());
    assertEquals(2, dist.getActiveFlat().getN());
  }

  @Test
  public void getTimeSummary() {
    final Statistics time = getStats().getTimeDifferenceSummary();
    assertTrue(time.getOutliers().isEmpty());
    assertEquals(5, time.getAll().getN());
    assertEquals(3, time.getActive().getN());
    assertEquals(1, time.getActiveDown().getN());
    assertEquals(0, time.getActiveUp().getN());
    assertEquals(2, time.getActiveFlat().getN());
  }

  @Test
  public void getElevationDiffSummary() {
    final Statistics ele = getStats().getElevationDifferenceSummary();
    assertTrue(ele.getOutliers().isEmpty());
    assertEquals(5, ele.getAll().getN());
    assertEquals(3, ele.getActive().getN());
    assertEquals(1, ele.getActiveDown().getN());
    assertEquals(2, ele.getActiveUp().getN());
    assertEquals(0, ele.getActiveFlat().getN());
  }

  @Test
  public void getElevationSummary() {
    final Statistics ele = getStats().getElevationSummary();
    assertTrue(ele.getOutliers().isEmpty());
    assertEquals(5, ele.getAll().getN());
    assertEquals(3, ele.getActive().getN());
    assertEquals(1, ele.getActiveDown().getN());
    assertEquals(2, ele.getActiveUp().getN());
    assertEquals(0, ele.getActiveFlat().getN());
  }

  @Test
  public void getGradeSummary() {
    final Statistics grade = getStats().getGradeSummary();
    assertTrue(grade.getOutliers().isEmpty());
    assertEquals(5, grade.getAll().getN());
    assertEquals(3, grade.getActive().getN());
    assertEquals(1, grade.getActiveDown().getN());
    assertEquals(0, grade.getActiveUp().getN());
    assertEquals(2, grade.getActiveFlat().getN());
  }

  private TrailStatistics getStats() {
    final SortedSet<Waypoint> points = PointsAugmenter.INSTANCE.apply(getTestPoints());
    return StatisticsProvider.valueOf("INSTANCE").apply(points);
  }

  private SortedSet<GpsRecord> getTestPoints() {
    final SortedSet<GpsRecord> points = new TreeSet<>();
    points.add(getPoint(7.9630853701, 50.1181208342, 214.03, "2014-05-18T08:25:32Z"));
    points.add(getPoint(7.9629951809, 50.1181007177, 215.47, "2014-05-18T08:26:14Z"));
    points.add(getPoint(7.9631012119, 50.1183273643, 216.43, "2014-05-18T08:27:02Z"));
    points.add(getPoint(7.9631571192, 50.1184399333, 215.47, "2014-05-18T08:27:09Z"));
    points.add(getPoint(7.9631261062, 50.1186041348, 215.95, "2014-05-18T08:27:26Z"));
    return points;
  }

  private GpsRecord getPoint(final double x, final double y, final double z, final String time) {
    return GpsRecord.of(Instant.parse(time), x, y, z);
  }
}
