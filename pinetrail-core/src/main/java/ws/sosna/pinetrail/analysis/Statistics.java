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

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import ws.sosna.pinetrail.model.Waypoint;

/**
 * Provides statistics about the trail for a particular aspect, such as distance, elevation or
 * speed.
 *
 * @author Xavier Sosnovsky
 */
public final class Statistics {

  private final SummaryStatistics all;
  private final SummaryStatistics active;
  private final SummaryStatistics up;
  private final SummaryStatistics down;
  private final SummaryStatistics flat;
  private final Set<Waypoint> outliers;

  public Statistics(
      final SummaryStatistics all,
      final SummaryStatistics active,
      final SummaryStatistics up,
      final SummaryStatistics down,
      final SummaryStatistics flat,
      final Set<Waypoint> outliers) {
    this.all = all;
    this.active = active;
    this.up = up;
    this.down = down;
    this.flat = flat;
    this.outliers = Collections.unmodifiableSet(new LinkedHashSet<>(outliers));
  }

  /** Get the statistics for all the points in the trail. */
  public SummaryStatistics getAll() {
    return all;
  }

  /**
   * Get the statistics for all the points in the trail where the person recording the trail was
   * considered to be in movement.
   */
  public SummaryStatistics getActive() {
    return active;
  }

  /**
   * Get the statistics for all the points in the trail where the person recording the trail was
   * considered to be going up.
   */
  public SummaryStatistics getActiveUp() {
    return up;
  }

  /**
   * Get the statistics for all the points in the trail where the person recording the trail was
   * considered to be going down.
   */
  public SummaryStatistics getActiveDown() {
    return down;
  }

  /**
   * Get the statistics for all the points in the trail where the person recording the trail was
   * considered to be going neither up nor down.
   */
  public SummaryStatistics getActiveFlat() {
    return flat;
  }

  /** Get the points that could be considered outliers in the trail. */
  public Set<Waypoint> getOutliers() {
    return outliers;
  }

  @Override
  public String toString() {
    return "Statistics{"
        + "all="
        + all
        + ", active="
        + active
        + ", up="
        + up
        + ", down="
        + down
        + ", flat="
        + flat
        + ", outliers="
        + outliers
        + '}';
  }
}
