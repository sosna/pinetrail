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
import java.util.Objects;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

/**
 * An immutable instance of a GPS Log Record (time and coordinates).
 *
 * @author Xavier Sosnovsky
 */
public final class GpsRecord implements Comparable<GpsRecord> {

  private final Instant time;
  private final Double latitude;
  private final Double longitude;
  private final Double elevation;
  private final transient int hashcode;

  private GpsRecord(
      final Instant time, final Double longitude, final Double latitude, final Double elevation) {
    this.time = time;
    this.latitude = latitude;
    this.longitude = longitude;
    this.elevation = elevation;
    this.hashcode = Objects.hash(time, longitude, latitude, elevation);
  }

  /**
   * Creates an instance of GpsRecord.
   *
   * @param time the point in time when the coordinates were measured.
   * @param longitude the longitude of the point, in decimal degrees (WGS84 datum).
   * @param latitude the latitude of the point, in decimal degrees (WGS84 datum).
   * @param elevation the elevation of the point, in meters.
   * @return a new GpsRecord
   */
  public static GpsRecord of(
      final Instant time, final Double longitude, final Double latitude, final Double elevation) {
    final double ele = elevation == null ? Double.NaN : elevation;
    return new GpsRecord(time, longitude, latitude, ele);
  }

  /**
   * Creates an instance of GpsRecord.
   *
   * @param time the point in time when the coordinates were measured.
   * @param longitude the longitude of the point, in decimal degrees (WGS84 datum).
   * @param latitude the latitude of the point, in decimal degrees (WGS84 datum).
   * @return a new GpsRecord
   */
  public static GpsRecord of(final Instant time, final Double longitude, final Double latitude) {
    return new GpsRecord(time, longitude, latitude, Double.NaN);
  }

  /**
   * The point in time when the coordinates were measured.
   *
   * <p>The timestamp cannot be null and must be in the past (no kidding).
   *
   * @return the point in time when the coordinates were measured
   */
  @Past(message = "{Model.Waypoint.Time.Past}")
  @NotNull(message = "{Model.Waypoint.Time.NotNull}")
  public Instant getTime() {
    return time;
  }

  /**
   * The latitude of the point, in decimal degrees (WGS84 datum).
   *
   * <p>The latitude of the point, in decimal degrees. The value cannot be null and must be between
   * or equal to -90.0 and 90.0 degrees.
   *
   * @return the latitude of the point
   */
  @NotNull(message = "{Model.Coordinates.Latitude.NotNull}")
  @DecimalMax(value = "90", message = "{Model.Coordinates.Latitude.MaxValue}")
  @DecimalMin(value = "-90", message = "{Model.Coordinates.Latitude.MinValue}")
  public Double getLatitude() {
    return latitude;
  }

  /**
   * The longitude of the point, in decimal degrees (WGS84 datum).
   *
   * <p>The value cannot be null, must be superior or equal to -180.0 and inferior to 180.0 degrees.
   *
   * @return the longitude of the point
   */
  @NotNull(message = "{Model.Coordinates.Longitude.NotNull}")
  @DecimalMax(value = "180", inclusive = false, message = "{Model.Coordinates.Longitude.MaxValue}")
  @DecimalMin(value = "-180", message = "{Model.Coordinates.Longitude.MinValue}")
  public Double getLongitude() {
    return longitude;
  }

  /**
   * The elevation of the point, in meters.
   *
   * @return the elevation of the point, in meters
   */
  public Double getElevation() {
    return elevation;
  }

  @Override
  public int hashCode() {
    return hashcode;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof GpsRecord)) {
      return false;
    }
    final GpsRecord other = (GpsRecord) obj;
    return Objects.equals(this.time, other.getTime())
        && Objects.equals(this.latitude, other.getLatitude())
        && Objects.equals(this.longitude, other.getLongitude())
        && Objects.equals(this.elevation, other.getElevation());
  }

  @Override
  public String toString() {
    return "GpsRecord{time="
        + time
        + ", latitude="
        + latitude
        + ", "
        + "longitude="
        + longitude
        + ", elevation="
        + elevation
        + '}';
  }

  @Override
  public int compareTo(final GpsRecord o) {
    return this.time.compareTo(o.getTime());
  }
}
