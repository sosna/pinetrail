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
package ws.sosna.pinetrail.analysis;

import java.util.Objects;

/**
 * Base implementation of the TrailStatistics interface.
 *
 * @author Xavier Sosnovsky
 */
public final class TrailStatistics {

  private final Statistics time;
  private final Statistics dist;
  private final Statistics ele;
  private final Statistics eleDiff;
  private final Statistics speed;
  private final Statistics grade;

  TrailStatistics(
      final Statistics time,
      final Statistics dist,
      final Statistics ele,
      final Statistics eleDiff,
      final Statistics speed,
      final Statistics grade) {
    this.time = time;
    this.dist = dist;
    this.eleDiff = eleDiff;
    this.speed = speed;
    this.grade = grade;
    this.ele = ele;
  }

  /** Get the time statistics (start time, end time, etc.) for the trail. */
  public Statistics getTimeDifferenceSummary() {
    return time;
  }

  /** Get the distance statistics (number of kilometers, etc.) for the trail. */
  public Statistics getDistanceSummary() {
    return dist;
  }

  /** Get the statistics about the differences in elevation between the points of the trail. */
  public Statistics getElevationDifferenceSummary() {
    return eleDiff;
  }

  /** Get the speed statistics (average moving speed, etc.) for the trail. */
  public Statistics getSpeedSummary() {
    return speed;
  }

  /** Get the grade statistics (steepness) for the trail. */
  public Statistics getGradeSummary() {
    return grade;
  }

  /** Get the elevation statistics (highest/lowest points, total elevation, etc.) for the trail. */
  public Statistics getElevationSummary() {
    return ele;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return "TrailStatistics{"
        + "timeDiff="
        + time.toString()
        + ", distance="
        + dist.toString()
        + ", elevation="
        + ele.toString()
        + ", elevationDiff="
        + eleDiff.toString()
        + ", speed="
        + speed.toString()
        + ", grade="
        + grade.toString()
        + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TrailStatistics that = (TrailStatistics) o;
    return Objects.equals(time, that.time) &&
        Objects.equals(dist, that.dist) &&
        Objects.equals(ele, that.ele) &&
        Objects.equals(eleDiff, that.eleDiff) &&
        Objects.equals(speed, that.speed) &&
        Objects.equals(grade, that.grade);
  }

  @Override
  public int hashCode() {
    return Objects.hash(time, dist, ele, eleDiff, speed, grade);
  }
}
