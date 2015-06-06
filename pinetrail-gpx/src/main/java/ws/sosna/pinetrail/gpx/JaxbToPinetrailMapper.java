/*
 * Copyright (c) 2015, Xavier Sosnovsky <xso@sosna.ws>
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
package ws.sosna.pinetrail.gpx;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import javax.validation.ValidationException;
import org.slf4j.LoggerFactory;
import ws.sosna.pinetrail.model.CoordinatesBuilder;
import ws.sosna.pinetrail.model.Link;
import ws.sosna.pinetrail.model.Trail;
import ws.sosna.pinetrail.model.TrailBuilder;
import ws.sosna.pinetrail.model.Waypoint;
import ws.sosna.pinetrail.model.WaypointBuilder;
import ws.sosna.pinetrail.utils.logging.Actions;
import ws.sosna.pinetrail.utils.logging.Markers;
import ws.sosna.pinetrail.utils.logging.StatusCodes;

/**
 * Performs mapping to the Pinetrail model.
 *
 * @author Xavier Sosnovsky
 */
abstract class JaxbToPinetrailMapper<T extends Object> {

    protected final ResourceBundle logMessages;
    protected final boolean groupSubTrails;
    protected static final org.slf4j.Logger LOGGER
        = LoggerFactory.getLogger(JaxbToPinetrailMapper.class);

    JaxbToPinetrailMapper(final boolean groupSubTrails) {
        super();
        logMessages = ResourceBundle.getBundle("GpxLogMessages",
            Locale.getDefault());
        this.groupSubTrails = groupSubTrails;
    }

    protected Trail getTrail(final String trailName, final Set<Waypoint> points,
        final String description, final Set<Link> links) {
        Trail trail;
        if (points.isEmpty()) {
            LOGGER.warn(Markers.IO.getMarker(), "{} | {} | {}.",
                Actions.PARSE, StatusCodes.NOT_FOUND.getCode(),
                logMessages.getString("Error.NoPoint"));
            trail = null;
        } else {
            try {
                trail = new TrailBuilder(trailName, points).description(
                    description).links(links).build();
                LOGGER.info(Markers.IO.getMarker(), "{} | {} | {}.",
                    Actions.PARSE, StatusCodes.OK.getCode(),
                    "Mapped one trail," + " containing " + points.size()
                    + " point(s)");
            } catch (final ValidationException e) {
                final String msg = "Bean validation failed. Trail will be "
                    + "ignored. Problem was: " + e.getLocalizedMessage();
                LOGGER.warn(Markers.IO.getMarker(), "{} | {} | {}",
                    Actions.CREATE, StatusCodes.SYNTAX_ERROR.getCode(),
                    msg);
                trail = null;
            }
        }
        return trail;
    }

    protected Waypoint buildPoint(final Instant time, final double lon,
        final double lat, final BigDecimal ele, final String name,
        final String desc, final Set<Link> links) {
        final CoordinatesBuilder cbld = new CoordinatesBuilder(lon, lat);
        if (null != ele) {
            cbld.elevation(ele.doubleValue());
        }
        try {
            return new WaypointBuilder(time, cbld.build()).name(name).
                description(desc).links(links).build();
        } catch (final ValidationException e) {
            final String msg = "Bean validation failed. Waypoint will be "
                + "ignored. Problem was: " + e.getLocalizedMessage();
            LOGGER.warn(Markers.IO.getMarker(), "{} | {} | {}",
                Actions.CREATE, StatusCodes.SYNTAX_ERROR.getCode(), msg);
            return null;
        }
    }

    protected Set<Waypoint> handleAdditionalWaypoints(
        final boolean multipleSegments, final Set<Waypoint> waypoints) {
        final Set<Waypoint> trkPoints = new LinkedHashSet<>();
        if (multipleSegments && !groupSubTrails && !waypoints.isEmpty()) {
            LOGGER.warn(Markers.IO.getMarker(), "{} | {} | {}.",
                Actions.PARSE, StatusCodes.NOT_ACCEPTABLE.getCode(),
                logMessages.getString("Error.WaypointsForManySegs"));
        } else {
            trkPoints.addAll(waypoints);
        }
        return trkPoints;
    }

    abstract Set<Trail> mapToTrails(final T gpx);
}
