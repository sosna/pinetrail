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

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.sosna.pinetrail.utils.logging.Actions;
import ws.sosna.pinetrail.utils.logging.Markers;
import ws.sosna.pinetrail.utils.logging.StatusCodes;

/**
 * Determines the activity out of the provided speed statistics.
 *
 * @author Xavier Sosnovsky
 */
enum ActivityGuesser implements BiFunction<Statistics, Statistics, Activity> {

  /** Singleton that returns an instance of a ActivityGuesser. */
  INSTANCE;

  private static final Logger LOGGER = LoggerFactory.getLogger(ActivityGuesser.class);
  private static final double MARATHON = 42195;

  /**
   * Determines the activity (hike, run, etc.) out of the provided speed statistics.
   *
   * <p>The average value of the provided speed statistics will be compared with the expected
   * average speed for known activities and the activity with the smallest difference will be
   * returned.
   *
   * @param speedStats the speed statistics for the trail
   * @return the trail activity
   */
  @Override
  public Activity apply(final Statistics speedStats, final Statistics distanceStats) {
    final double distance = distanceStats.getActive().getSum();
    final double speed = speedStats.getActive().getMean();
    if (distance > MARATHON) {
      return Activity.BIKING;
    } else if (distance > 20000 & speed > 10) {
      return Activity.BIKING;
    } else {
      final Map<Double, Activity> speeds = new HashMap<>(Activity.values().length);
      for (final Activity activity : Activity.values()) {
        speeds.put(Math.abs(speed - activity.getAverageSpeed()), activity);
      }
      final Double minValue = speeds.keySet().stream().min(Double::compare).get();
      final Activity activity = speeds.get(minValue);
      LOGGER.debug(
          Markers.MODEL.getMarker(),
          "{} | {} | Guessed activity: {}" + " (difference with average: {})",
          Actions.ANALYSE,
          StatusCodes.OK.getCode(),
          activity,
          new DecimalFormat("#0.0").format(minValue));
      return activity;
    }
  }
}
