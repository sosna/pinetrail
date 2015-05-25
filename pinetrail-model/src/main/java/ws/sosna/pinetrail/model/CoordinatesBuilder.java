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

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.sosna.pinetrail.utils.logging.Actions;
import ws.sosna.pinetrail.utils.logging.Markers;
import ws.sosna.pinetrail.utils.logging.StatusCodes;

/**
 * Builds immutable instances of the {@code Coordinates} interface.
 *
 * <p>
 * A new instance can be built as follows:<br>
 * <code>
 * Coordinates point =
 *     new CoordinatesBuilder(12.9946215637, 47.5913904235).build();
 * </code>
 *
 * <p>
 * Optional parameters can be set using the appropriate method. For example, to
 * set the elevation:<br>
 * <code>
 * Coordinates point =
 *     new CoordinatesBuilder(12.99462156, 47.59139042).elevation(63).build();
 * </code>
 *
 * <p>
 * Instances are immutable. In case it is needed to update the values of some
 * fields (like changing the label of the link via a GUI), the of() method can
 * be used:<br>
 * <code>Coordinates point =
 *      CoordinatesBuilder.of(point).elevation(123).build();</code>
 *
 * @see Coordinates
 *
 * @author Xavier Sosnovsky
 */
public final class CoordinatesBuilder implements Builder<Coordinates> {

    private Double latitude;
    private Double longitude;
    private Double elevation;
    private static final Logger LOGGER = LoggerFactory.getLogger(
            CoordinatesBuilder.class);

    /**
     * Instantiates a new CoordinatesBuilder, with all mandatory fields.
     *
     * <p>
     * The {@code longitude} cannot be null, must be superior or equal to -180.0
     * and inferior to 180.0 degrees.
     *
     * <p>
     * The {@code latitude} cannot be null and must be between or equal to -90.0
     * and 90.0 degrees.
     *
     * @param longitude the longitude of the point, in decimal degrees
     * @param latitude the latitude of the point, in decimal degrees
     */
    public CoordinatesBuilder(final Double longitude, final Double latitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Sets the latitude of the point, in decimal degrees.
     *
     * <p>
     * The value cannot be null and must be between or equal to -90.0 and 90.0
     * degrees.
     *
     * @param latitude the latitude of the point, in decimal degrees
     * @return the builder, with the updated latitude
     */
    public CoordinatesBuilder latitude(final Double latitude) {
        this.latitude = latitude;
        return this;
    }

    /**
     * Sets the longitude of the point, in decimal degrees.
     *
     * <p>
     * The value cannot be null, must be superior or equal to -180.0 and
     * inferior to 180.0 degrees.
     *
     * @param longitude the longitude of the point, in decimal degrees
     * @return the builder, with the updated longitude
     */
    public CoordinatesBuilder longitude(final Double longitude) {
        this.longitude = longitude;
        return this;
    }

    /**
     * Sets the elevation of the point, in meters (optional).
     *
     * <p>
     * On earth, the elevation should be between -450 and 9000 meters (Mt.
     * Everest culminates at 8850m, while the lowest point on dry land is at
     * -418m). If you happen to be on another planet, validation will fail.
     *
     * @param elevation the elevation of the point, in meters
     * @return the builder, with the updated elevation
     */
    public CoordinatesBuilder elevation(final Double elevation) {
        this.elevation = elevation;
        return this;
    }

    /**
     * Instantiate a new CoordinatesBuilder out of an existing
     * {@code Coordinates} instance.
     *
     * <p>
     * All objects are immutable and, therefore cannot be updated. This method
     * is a convenience method that creates a new builder with the same values
     * as the supplied {@code Coordinates}. The setters methods of the builder
     * can then be used to update some fields before calling the {@code build}
     * method.
     *
     * @param coordinates the coordinates from which the values will be copied
     * @return a new CoordinatesBuilder
     */
    public static CoordinatesBuilder of(final Coordinates coordinates) {
        final CoordinatesBuilder bld = new CoordinatesBuilder(coordinates.
                getLongitude(), coordinates.getLatitude());
        bld.elevation(coordinates.getElevation());
        return bld;
    }

    /**
     * Builds a new immutable instance of the {@code Coordinates} interface.
     *
     * @return a new immutable instance of the Coordinates interface
     */
    @Override
    public Coordinates build() {
        final Coordinates obj = new CoordinatesImpl(longitude, latitude,
                elevation);
        LOGGER.debug(Markers.MODEL.getMarker(), "{} | {} | Built {}",
            Actions.CREATE, StatusCodes.OK.getCode(), obj);
        return obj;
    }

    private static final class CoordinatesImpl implements Coordinates {

        private static final long serialVersionUID = 2880454943555301352L;
        private final Double latitude;
        private final Double longitude;
        private final Double elevation;
        private final transient int hashcode;

        CoordinatesImpl(final Double longitude, final Double latitude,
                final Double elevation) {
            super();
            this.latitude = latitude;
            this.longitude = longitude;
            this.elevation = elevation;
            this.hashcode = Objects.hash(longitude, latitude, elevation);
        }

        @Override
        public Double getLatitude() {
            return latitude;
        }

        @Override
        public Double getLongitude() {
            return longitude;
        }

        @Override
        public Double getElevation() {
            return elevation;
        }

        @Override
        public int hashCode() {
            return hashcode;
        }

        @SuppressWarnings("PMD.LawOfDemeter")
        @Override
        public boolean equals(final Object obj) {
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof Coordinates)) {
                return false;
            }
            final Coordinates other = (Coordinates) obj;
            return Objects.equals(this.latitude, other.getLatitude())
                    && Objects.equals(this.longitude, other.getLongitude())
                    && Objects.equals(this.elevation, other.getElevation());
        }

        @Override
        public String toString() {
            return "Coordinates{" + "latitude=" + latitude + ", "
                    + "longitude=" + longitude + ", elevation=" + elevation
                    + '}';
        }

        private Object writeReplace() {
            return new SerializationProxy(this);
        }

        private void readObject(final ObjectInputStream stream)
            throws InvalidObjectException {
            throw new InvalidObjectException("Proxy required");
        }

        private static final class SerializationProxy implements Serializable {
            private static final long serialVersionUID = 2880454943555301352L;
            private final Double latitude;
            private final Double longitude;
            private final Double elevation;

            SerializationProxy(final Coordinates c) {
                super();
                this.latitude = c.getLatitude();
                this.longitude = c.getLongitude();
                this.elevation = c.getElevation();
            }

            private Object readResolve() {
                return new CoordinatesImpl(longitude, latitude, elevation);
            }
        }
    }
}
