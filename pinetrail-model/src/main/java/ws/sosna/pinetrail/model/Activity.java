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
 *
 */
package ws.sosna.pinetrail.model;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.sosna.pinetrail.utils.logging.Actions;
import ws.sosna.pinetrail.utils.logging.Markers;
import ws.sosna.pinetrail.utils.logging.StatusCodes;

/**
 * The type of outdoor activity for the trail.
 *
 * @author Xavier Sosnovsky
 */
public enum Activity {

    /**
     * "Running" activities like cross-country training, marathons, etc.
     */
    JOGGING(9.0),
    /**
     * "Walking" activities like hiking, trekking, orienteering, etc.
     */
    HIKING(5.0) {
            /**
             * Determines the difficulty level out of the provided statistics
             * about distance and elevation differences.
             *
             * The level the user considers to have will also be taken into
             * account, as the trail difficulty will be perceived differently by
             * novices and experts.
             *
             * <p>
             * For hiking, the formula was kindly provided by
             * http://www.hikingincolorado.org/hikecalc.html
             *
             * @param userLevel the level the user considers to have
             * @param distanceStats statistics about the trail distance
             * @param elevationStats statistics about the elevation gains
             * @param startPeriod when the activity started
             * @param endPeriod when the activity ended
             *
             * @return the difficulty level for the trail
             */
            @Override
            public Integer getDifficultyRating(final Level userLevel,
                final Statistics distanceStats,
                final Statistics elevationStats, final Instant startPeriod,
                final Instant endPeriod) {
                final Double rating = getWeight(userLevel)
                * elevationStats.getActiveUp().getSum() * METERS2FEET
                + distanceStats.getActive().getSum() * KM2MILES
                / (METERS2KM * getDivider(userLevel));
                final long days = ChronoUnit.DAYS.between(
                    startPeriod, endPeriod) + 1; // At least one day
                final Double ratingByDay = rating / days;
                return ratingByDay.intValue();
            }

            private double getWeight(final Level userLevel) {
                final Double weight;
                switch (userLevel) {
                    case ADVANCED:
                        weight = 0.0005;
                        break;
                    case BEGINNER:
                        weight = 0.002;
                        break;
                    default:
                        weight = 0.001;
                        break;
                }
                return weight;
            }

            private double getDivider(final Level userLevel) {
                final Double divider;
                switch (userLevel) {
                    case ADVANCED:
                        divider = 2.0;
                        break;
                    case BEGINNER:
                        divider = 1.0;
                        break;
                    default:
                        divider = 1.5;
                        break;
                }
                return divider;
            }
        },
    /**
     * "Cycling" activities like mountain biking, etc.
     */
    BIKING(15.0);

    private double averageSpeed;
    private static final Logger LOGGER = LoggerFactory.getLogger(
        Activity.class);
    private static final double METERS2FEET = 3.280840;
    private static final int METERS2KM = 1000;
    private static final double KM2MILES = 0.621371;

    private Activity(final double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    /**
     * Gets the expected average speed for the activity.
     *
     * @return the expected average speed for the activity
     */
    public double getAverageSpeed() {
        return averageSpeed;
    }

    /**
     * Sets the expected average speed for the activity.
     *
     * <p>
     * This allows to override the default expected average speed, based on
     * previous activities.
     *
     * @param averageSpeed the expected average speed for the activity
     */
    public void setAverageSpeed(final double averageSpeed) {
        LOGGER.info(Markers.MODEL.getMarker(), "{} | {} | Updated average speed"
            + ": {} (was {})",
            Actions.UPDATE, StatusCodes.OK.getCode(), averageSpeed,
            this.averageSpeed);
        this.averageSpeed = averageSpeed;
    }

    /**
     * Determines the difficulty rating out of the provided statistics about
     * distance and elevation differences.
     *
     * The level this user considers to have will also be taken into account, as
     * the trail difficulty will be perceived differently by novices and
     * experts.
     *
     * <p>
     * Currently this works only for hiking activities. For the other
     * activities, 0 will be returned,
     *
     * @param userLevel the level the user considers to have
     * @param distanceStats statistics about the trail distance
     * @param elevationStats statistics about the elevation gains
     * @param startPeriod when the activity started
     * @param endPeriod when the activity ended
     *
     * @return the difficulty rating for the trail
     */
    public Integer getDifficultyRating(final Level userLevel,
        final Statistics distanceStats, final Statistics elevationStats,
        final Instant startPeriod, final Instant endPeriod) {
        return 0;
    }
}
