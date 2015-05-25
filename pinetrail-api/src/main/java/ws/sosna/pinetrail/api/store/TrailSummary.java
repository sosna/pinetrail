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
package ws.sosna.pinetrail.api.store;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotBlank;
import ws.sosna.pinetrail.model.Activity;

/**
 * Key information about a trail.
 *
 * <p>
 * It represents a "management summary" about a trail and contains information
 * such as the trail title, the difficulty level, the countries crossed by the
 * trail, the distance, the elevation difference, etc.
 *
 * @author Xavier Sosnovsky
 */
public interface TrailSummary {

    /**
     * Returns the activity performed on the trail.
     *
     * <p>
     * The value cannot be null.
     *
     * @return the activity performed on the trail
     */
    @NotNull(message = "{Api.TrailSummary.Activity.NotNull}")
    Activity getActivity();

    /**
     * Returns the list of countries crossed by the trail.
     *
     * <p>
     * Each item in the set represents an ISO 3166-1 two-letter country codes.
     * In case no country has been assigned, the method returns an empty
     * collection.
     *
     * @return the list of countries crossed by the trail
     */
    Set<String> getCountries();

    /**
     * The date when the activity took place.
     *
     * <p>
     * The value cannot be null.
     *
     * @return the date when the activity took place
     */
    @NotNull(message = "{Api.TrailSummary.Date.NotNull}")
    Instant getDate();

    /**
     * The distance, in meters, covered by the trail.
     *
     * <p>
     * The value cannot be null.
     *
     * @return the distance in meters covered by the trail
     */
    @NotNull(message = "{Api.TrailSummary.Distance.NotNull}")
    Double getDistance();

    /**
     * The difference, in meters, between the lowest and the highest points on
     * the trail.
     *
     * <p>
     * The value cannot be null.
     *
     * @return the net elevation
     */
    @NotNull(message = "{Api.TrailSummary.ElvationDifference.NotNull}")
    Double getElevationDifference();

    /**
     * Returns the difficulty rating of the trail.
     *
     * <p>
     * The value cannot be null.
     *
     * @return the difficulty rating of the trail
     */
    @NotNull(message = "{Api.TrailSummary.Level.NotNull}")
    Integer getDifficultyRating();

    /**
     * The average moving speed, in kilometres per hour, for the trail.
     *
     * <p>
     * The value cannot be null.
     *
     * @return the average moving speed for the trail
     */
    @NotNull(message = "{Api.TrailSummary.MovingSpeed.NotNull}")
    Double getMovingSpeed();

    /**
     * Returns a short title describing the trail.
     *
     * <p>
     * The value cannot be null or empty.
     *
     * @return a short title for the trail
     */
    @NotBlank(message = "{Api.TrailSummary.Name.NotBlank}")
    String getName();

    /**
     * Returns the star rating for the trail.
     *
     * <p>
     * The value is a number from 0 (the default) to 5 (the best ranking).
     *
     * @return the star rating for the trail
     */
    @Min(value = 0, message = "{Api.TrailSummary.Rating.MinValue}")
    @Max(value = 5, message = "{Api.TrailSummary.Rating.MaxValue}")
    int getRating();

    /**
     * Returns the identifier of the trail for which this summary is provided.
     *
     * <p>
     * The value cannot be null.
     *
     * @return the identifier of the trail for which this summary is provided
     */
    @NotNull(message = "{Api.TrailSummary.Id.NotNull}")
    UUID getTrailId();
}
