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

import java.util.Set;
import java.util.SortedSet;
import java.util.UUID;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.NotBlank;

/**
 * A collection of {@code Waypoint}s describing the trail.
 *
 * <p>This is typically a GPS recording of a path followed during some outdoor activity. It is
 * roughly equivalent to what is known as a track in the GPX format, or a linestring in GeoJSON or
 * KML.
 *
 * <p>A new immutable instance can be obtained using a {@code TrailBuilder}:<br>
 * <code>
 * Trail trail = new TrailBuilder(name, points).build();
 * </code>
 *
 * @see Waypoint
 * @see TrailBuilder
 * @author Xavier Sosnovsky
 */
public interface Trail extends Describable {

  /**
   * Returns a unique identifier for the trail.
   *
   * <p>The value cannot be null.
   *
   * @return a unique identifier for the trail
   */
  @NotNull(message = "{Model.Trail.Id.NotNull}")
  UUID getId();

  /**
   * Returns a short title describing the trail.
   *
   * <p>The value cannot be null or empty.
   *
   * @return a short title for the trail
   */
  @Override
  @NotBlank(message = "{Model.Trail.Name.NotBlank}")
  String getName();

  /**
   * Returns the ordered list of points describing the trail.
   *
   * <p>The list is immutable and is sorted by time, in ascending order. The list cannot be null and
   * must contain at least one waypoint.
   *
   * @return the ordered list of points describing the trail
   */
  @NotNull(message = "{Model.Trail.Waypoints.MinSize}")
  @Size(min = 1, message = "{Model.Trail.Waypoints.MinSize}")
  SortedSet<Waypoint> getWaypoints();

  /**
   * Returns the difficulty rating of the trail.
   *
   * <p>The rating will be guessed from the elevation gain and the distance of the trail.
   *
   * @return the difficulty rating of the trail
   */
  Integer getDifficultyRating();

  /**
   * Returns the activity performed on the trail.
   *
   * <p>The value represents the type of activity performed when following the trail, such as
   * hiking, jogging or biking.
   *
   * <p>The value can be null and the activity will be guessed from the average speed.
   *
   * @return the activity performed on the trail
   */
  Activity getActivity();

  /**
   * Returns the list of countries crossed by the trail.
   *
   * <p>Each item in the set represents an ISO 3166-1 two-letter country codes. In case no country
   * has been assigned, the method returns an empty collection.
   *
   * @return the list of countries crossed by the trail
   */
  Set<String> getCountries();

  /**
   * Returns the star rating for the trail.
   *
   * <p>The value is a number from 0 (the default) to 5 (the best ranking).
   *
   * @return the star rating for the trail
   */
  @Min(value = 0, message = "{Model.Trail.Rating.MinValue}")
  @Max(value = 5, message = "{Model.Trail.Rating.MaxValue}")
  int getRating();

  /**
   * Returns the time, distance, elevation and speed statistics for the trail.
   *
   * @return the time, distance, elevation and speed statistics for the trail
   */
  TrailStatistics getStatistics();
}
