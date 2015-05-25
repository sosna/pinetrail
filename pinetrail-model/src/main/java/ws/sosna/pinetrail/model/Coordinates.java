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

import java.io.Serializable;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * The coordinates of a point in a 3-dimensional plan: longitude (x), latitude
 * (y) and elevation (z).
 *
 * <p>
 * A new immutable instance can be obtained using a
 * {@link CoordinatesBuilder}:<br>
 * <code>
 * Coordinates point =
 *     new CoordinatesBuilder(12.9946215637, 47.5913904235).build();</code>
 *
 * @see CoordinatesBuilder
 * @see Waypoint
 *
 * @author Xavier Sosnovsky
 */
public interface Coordinates extends Serializable {

    /**
     * The latitude of the point, in decimal degrees (WGS84 datum).
     *
     * <p>
     * The latitude of the point, in decimal degrees. The value cannot be null
     * and must be between or equal to -90.0 and 90.0 degrees.
     *
     * @return the latitude of the point
     */
    @NotNull(message = "{Model.Coordinates.Latitude.NotNull}")
    @DecimalMax(value = "90", message = "{Model.Coordinates.Latitude.MaxValue}")
    @DecimalMin(value = "-90",
            message = "{Model.Coordinates.Latitude.MinValue}")
    Double getLatitude();

    /**
     * The longitude of the point, in decimal degrees (WGS84 datum).
     *
     * <p>
     * The value cannot be null, must be superior or equal to -180.0 and
     * inferior to 180.0 degrees.
     *
     * @return the longitude of the point
     */
    @NotNull(message = "{Model.Coordinates.Longitude.NotNull}")
    @DecimalMax(value = "180", inclusive = false,
            message = "{Model.Coordinates.Longitude.MaxValue}")
    @DecimalMin(value = "-180",
            message = "{Model.Coordinates.Longitude.MinValue}")
    Double getLongitude();

    /**
     * The elevation of the point, in meters.
     *
     * <p>
     * On earth, the elevation should be between -450 and 9000 meters (Mt.
     * Everest culminates at 8850m, while the lowest point on dry land is at
     * -418m). If you happen to be on another planet, validation will fail.
     *
     * @return the elevation of the point, in meters
     */
    @Max(value = 9000, message = "{Model.Coordinates.Elevation.MaxValue}")
    @Min(value = -450, message = "{Model.Coordinates.Elevation.MinValue}")
    Double getElevation();
}
