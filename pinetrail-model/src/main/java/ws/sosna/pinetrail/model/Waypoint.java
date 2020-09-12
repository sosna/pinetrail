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
import javax.validation.Valid;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

/**
 * An individual point of a {@code Trail}, typically recorded by a GPS device.
 *
 * <p>A point is identified by its coordinates, as measured at a certain point in time.
 *
 * <p>Additional information can be given about the point, such as a {@code name}, a {@code
 * description} or {@code link}s to online resources. This is useful to indicate, for example, the
 * location of a famous landmark, a nice restaurant, or an impressive viewpoint.
 *
 * <p>A new immutable instance can be obtained using a {@code WaypointBuilder}:<br>
 * <code>
 * Waypoint pt = new WaypointBuilder(time, coordinates).build();
 * </code>
 *
 * @see Trail
 * @see WaypointBuilder
 * @author Xavier Sosnovsky
 */
public interface Waypoint extends Describable, Comparable<Waypoint> {

  /**
   * The coordinates (longitude, latitude, elevation) of the waypoint.
   *
   * <p>The coordinates are mandatory and cannot be null.
   *
   * @return the coordinates (longitude, latitude, elevation) of the waypoint
   */
  @NotNull(message = "{Model.Waypoint.Coordinates.NotNull}")
  @Valid
  Coordinates getCoordinates();

  /**
   * The point in time when the coordinates were measured.
   *
   * <p>The timestamp cannot be null and must be in the past (no kidding).
   *
   * @return the point in time when the coordinates were measured
   */
  @Past(message = "{Model.Waypoint.Time.Past}")
  @NotNull(message = "{Model.Waypoint.Time.NotNull}")
  Instant getTime();

  /**
   * The type of point of interest such as a castle, a religious building, a viewpoint, etc.
   *
   * <p>Many waypoints simply represent a GPS recording at a certain point in time. Others however
   * represent a location worth documenting or visiting again, such as a museum, a castle, a point
   * offering a nice panoramic view, etc.
   *
   * @return the type of point of interest
   */
  WaypointType getType();

  /**
   * The elapsed time in seconds since the previous waypoint.
   *
   * @return the elapsed time since the previous waypoint
   */
  @Min(value = 0, message = "{Model.Waypoint.TimeDiff.Positive}")
  long getTimeDifference();

  /**
   * The distance in meters between this point and the previous point in the trail.
   *
   * @return the distance in meters between this point and the previous point in the trail
   */
  @DecimalMin(value = "0", message = "{Model.Waypoint.Distance.Positive}")
  Double getDistance();

  /**
   * The difference in elevation (in meters) between this point and the previous point in the trail.
   *
   * @return the difference in elevation (in meters) between this point and the previous point in
   *     the trail
   */
  @DecimalMin(value = "-9450", message = "{Model.Waypoint.EleDiff.Positive}")
  @DecimalMax(value = "9450", message = "{Model.Waypoint.EleDiff.Max}")
  Double getElevationDifference();

  /**
   * The speed, in km per hours, with which the distance between this point and the previous point
   * was covered.
   *
   * @return the speed, in km per hours, with which the distance between this point and the previous
   *     point was covered
   */
  @DecimalMin(value = "0", message = "{Model.Waypoint.Speed.Positive}")
  Double getSpeed();

  /**
   * Whether the waypoint is sufficiently distant from the previous one to consider that the person
   * recording the trail was in movement.
   *
   * <p>This is useful to exclude points recorded during periods of inactivity (like lunch breaks)
   * in the statistics about the trail.
   *
   * <p>Defaults to true.
   *
   * @return whether the waypoint is sufficiently distant from the previous one to consider that the
   *     person recording the trail was in movement.
   */
  boolean isActive();

  /**
   * The inclination between this waypoint and the previous one, as an angle of inclination to the
   * horizontal.
   *
   * @return the inclination between this waypoint and the previous one
   */
  @DecimalMin(value = "-90", message = "{Model.Waypoint.Grade.MinValue}")
  @DecimalMax(value = "90.0", message = "{Model.Waypoint.Grade.MaxValue}")
  Double getGrade();
}
