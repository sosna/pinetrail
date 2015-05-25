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
import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.sosna.pinetrail.utils.logging.Actions;
import ws.sosna.pinetrail.utils.logging.Markers;
import ws.sosna.pinetrail.utils.logging.StatusCodes;

/**
 * Builds immutable instances of the {@code Waypoint} interface.
 *
 * <p>
 * A new instance can be built as follows:<br>
 * <code>
 * Waypoint pt = new WaypointBuilder(time, coordinates).build();
 * </code>
 *
 * <p>
 * Optional parameters can be set using the appropriate method. For example, to
 * set the name:<br>
 * <code>
 * Waypoint pt = new WaypointBuilder(time, coordinates).name(name).build();
 * </code>
 *
 * <p>
 * Instances are immutable. In case it is needed to update the values of some
 * fields, the of() method can be used:<br>
 * <code>Waypoint npt = WaypointBuilder.of(pt).name(name).build();</code>
 *
 * @see Waypoint
 *
 * @author Xavier Sosnovsky
 */
public final class WaypointBuilder extends DescribableBuilder<WaypointBuilder>
        implements Builder<Waypoint> {

    private static final Logger LOGGER = LoggerFactory.getLogger(
            WaypointBuilder.class);
    private Instant time;
    private Coordinates coordinates;
    private WaypointType type;
    private Double distance;
    private Double elevationDiff;
    private Double speed;
    private boolean isActive;
    private Double grade;
    private long timeDiff;

    /**
     * Instantiates a new WaypointBuilder, with all mandatory fields.
     *
     * <p>
     * Neither the {@code time} nor the {@code coordinates} can be null. Also,
     * the {@code time} must be in the past.
     *
     * @param time the moment in time when the coordinates were measured
     * @param coordinates the coordinates of the waypoint
     */
    public WaypointBuilder(final Instant time, final Coordinates coordinates) {
        super(null, null, null);
        this.time = time;
        this.coordinates = coordinates;
        isActive = true;
    }

    /**
     * Sets the moment in time when the coordinates were measured.
     *
     * <p>
     * It must be in the past (no kidding) and it cannot be null.
     *
     * @param time the moment in time when the coordinates were measured
     * @return the builder, with an updated timestamp
     */
    public WaypointBuilder time(final Instant time) {
        this.time = time;
        return this;
    }

    /**
     * Sets the coordinates (longitude, latitude, elevation) of the waypoint.
     *
     * <p>
     * It cannot be null.
     *
     * @param coordinates the coordinates of the waypoint
     * @return the builder, with updated coordinates
     */
    public WaypointBuilder coordinates(final Coordinates coordinates) {
        this.coordinates = coordinates;
        return this;
    }

    /**
     * Sets the type of point of interest such as a castle, a religious
     * building, a viewpoint, etc.
     *
     * @param type the type of point of interest
     * @return the builder, with an updated type for this waypoint
     */
    public WaypointBuilder type(final WaypointType type) {
        this.type = type;
        return this;
    }

     /**
     * Sets the elapsed time in seconds since the previous waypoint.
     *
     * @param elapsed the time in seconds since the previous waypoint
     *
     * @return the builder, with an updated time difference for this waypoint
     */
    public WaypointBuilder timeDifference(final long elapsed) {
        this.timeDiff = elapsed;
        return this;
    }

    /**
     * Sets the distance, in meters, between this waypoint and the previous
     * waypoint in the trail.
     *
     * @param distance the distance, in meters, between this waypoint and the
     * previous waypoint in the trail
     * @return the builder, with an updated distance for this waypoint
     */
    public WaypointBuilder distance(final Double distance) {
        this.distance = distance;
        return this;
    }

    /**
     * Sets the difference in elevation (in meters) between this point and the
     * previous point in the trail.
     *
     * @param elevationDifference the difference in elevation (in meters)
     * between this point and the previous point in the trail
     * @return the builder, with an updated difference in elevation for this
     * waypoint
     */
    public WaypointBuilder elevationDifference(
            final Double elevationDifference) {
        this.elevationDiff = elevationDifference;
        return this;
    }

    /**
     * Sets the speed, in km per hours, with which the distance between this
     * point and the previous point was covered.
     *
     * @param speed the speed, in km per hours, with which the distance between
     * this point and the previous point was covered
     * @return the builder, with an updated speed for this waypoint
     */
    public WaypointBuilder speed(
            final Double speed) {
        this.speed = speed;
        return this;
    }

    /**
     * Sets whether the waypoint is sufficiently distant from the previous one
     * to consider that the person recording the trail was in movement.
     *
     * <p>
     * This is useful to exclude points recorded during periods of inactivity
     * (like lunch breaks) in the statistics about the trail.
     *
     * <p>
     * Defaults to true.
     *
     * @param flag whether the waypoint is sufficiently distant from the
     * previous one to consider that the person recording the trail was in
     * movement
     *
     * @return the builder, with an updated isActive flag for this waypoint
     */
    public WaypointBuilder isActive(final boolean flag) {
        this.isActive = flag;
        return this;
    }

    /**
     * Sets the grade for the waypoint, that is, the inclination between this
     * waypoint and the previous one, expressed as ratio of the rise (the
     * difference in elevation) against the run (the horizontal distance), in
     * percent.
     *
     * @param grade the grade for the waypoint
     * @return the builder, with an updated grade for this waypoint
     */
    public WaypointBuilder grade(final Double grade) {
        this.grade = grade;
        return this;
    }

    /**
     * Instantiate a new WaypointBuilder out of an existing {@code Waypoint}
     * instance.
     *
     * <p>
     * All objects are immutable and, therefore cannot be updated. This method
     * is a convenience method that creates a new builder with the same values
     * as the supplied {@code Waypoint}. The setters methods of the builder can
     * then be used to update some fields before calling the {@code build}
     * method.
     *
     * @param point the waypoint from which the values will be copied
     * @return a new WaypointBuilder
     */
    public static WaypointBuilder of(final Waypoint point) {
        return new WaypointBuilder(point.getTime(), point.getCoordinates()).
                name(point.getName()).description(point.getDescription()).links(
                        point.getLinks()).type(point.getType()).distance(point.
                        getDistance()).elevationDifference(point.
                        getElevationDifference()).speed(point.getSpeed()).
                isActive(point.isActive()).grade(point.getGrade()).
                timeDifference(point.getTimeDifference());
    }

    /**
     * Builds a new immutable instance of the {@code Waypoint} interface.
     *
     * @return a new immutable instance of the Waypoint interface
     *
     * @throws ValidationException if validation fails
     */
    @Override
    public Waypoint build() {
        final Waypoint obj = new WaypointImpl(getName(), getDescription(),
            getLinks(), time, coordinates, type, distance, elevationDiff, speed,
            isActive, grade, timeDiff);
        final Set<ConstraintViolation<Waypoint>> violations
                = ValidationService.INSTANCE.getValidator().validate(obj);
        if (violations.isEmpty()) {
            LOGGER.debug(Markers.MODEL.getMarker(), "{} | {} | Built {}",
                Actions.CREATE, StatusCodes.OK.getCode(), obj);
            return obj;
        } else {
            final StringBuilder msg = new StringBuilder();
            for (final ConstraintViolation<Waypoint> violation : violations) {
                msg.append(violation.getMessage());
                msg.append(" Waypoint: ");
                msg.append(violation.getRootBean());
            }
            final String errorMsg = msg.toString();
            LOGGER.warn(Markers.MODEL.getMarker(), "{} | {} | Error"
                    + " validating waypoint {}:  {}", Actions.CREATE,
                    StatusCodes.SYNTAX_ERROR.getCode(), time, errorMsg);
            throw new ValidationException(errorMsg);
        }
    }

    private static final class WaypointImpl extends DescribableImpl
            implements Waypoint {

        private static final long serialVersionUID = -6931107933134694682L;
        private final Instant time;
        private final Coordinates coordinates;
        private final WaypointType type;
        private final transient int hashCode;
        private final Double distance;
        private final Double elevationDiff;
        private final Double speed;
        private final boolean isActive;
        private final Double grade;
        private final long timeDiff;

        WaypointImpl(final String name, final String description,
            final Set<Link> links, final Instant time,
            final Coordinates coordinates, final WaypointType type,
            final Double distance, final Double elevationDiff,
            final Double speed, final boolean isActive, final Double grade,
            final long timeDiff) {
            super(name, description, links);
            this.time = time;
            this.coordinates = coordinates;
            this.type = type;
            this.distance = distance;
            this.elevationDiff = elevationDiff;
            this.speed = speed;
            this.isActive = isActive;
            this.grade = grade;
            this.timeDiff = timeDiff;
            hashCode = Objects.hash(time, coordinates, type, super.hashCode());
        }

        @Override
        public Coordinates getCoordinates() {
            return coordinates;
        }

        @Override
        public Instant getTime() {
            return time;
        }

        @Override
        public WaypointType getType() {
            return type;
        }

        @Override
        public Double getDistance() {
            return distance;
        }

        @Override
        public Double getElevationDifference() {
            return elevationDiff;
        }

        @Override
        public Double getSpeed() {
            return speed;
        }

        @Override
        public boolean isActive() {
            return isActive;
        }

        @Override
        public Double getGrade() {
            return grade;
        }

        @Override
        public long getTimeDifference() {
            return timeDiff;
        }

        @SuppressWarnings("PMD.LawOfDemeter")
        @Override
        public boolean equals(final Object obj) {
            if (!super.equals(obj)) {
                return false;
            }
            final Waypoint other = (Waypoint) obj;
            return Objects.equals(this.time, other.getTime())
                    && Objects.equals(this.coordinates, other.getCoordinates())
                    && this.type == other.getType();
        }

        @Override
        public int hashCode() {
            return hashCode;
        }

        @Override
        public String toString() {
            return "Waypoint{time=" + time + ", coordinates=" + coordinates
                    + ", name=" + getName() + ", description="
                    + getDescription() + ", links=" + getLinks()
                    + ", type=" + type + ", distance=" + distance + ", "
                    + "elevationDifference=" + elevationDiff + ", grade="
                    + grade + ", speed=" + speed + ", isActive=" + isActive
                    + ", timeDifference=" + timeDiff + "}";
        }

        @Override
        public int compareTo(final Waypoint point) {
            return this.time.compareTo(point.getTime());
        }

        private Object writeReplace() {
            return new SerializationProxy(this);
        }

        private void readObject(final ObjectInputStream stream)
            throws InvalidObjectException {
            throw new InvalidObjectException("Proxy required");
        }

        private static final class SerializationProxy implements Serializable {
            private static final long serialVersionUID = -6931107933134694682L;
            private final String name;
            private final String description;
            private final Set<Link> links;
            private final Instant time;
            private final Coordinates coordinates;
            private final WaypointType type;
            private final Double distance;
            private final Double elevationDiff;
            private final Double speed;
            private final boolean isActive;
            private final Double grade;
            private final long timeDiff;

            SerializationProxy(final Waypoint pt) {
                super();
                name = pt.getName();
                description = pt.getDescription();
                links = pt.getLinks();
                time = pt.getTime();
                coordinates = pt.getCoordinates();
                type = pt.getType();
                distance = pt.getDistance();
                elevationDiff = pt.getElevationDifference();
                speed = pt.getSpeed();
                isActive = pt.isActive();
                grade = pt.getGrade();
                timeDiff = pt.getTimeDifference();
            }

            private Object readResolve() {
                return new WaypointImpl(name, description, links, time,
                    coordinates, type, distance, elevationDiff, speed, isActive,
                    grade, timeDiff);
            }
        }
    }
}
