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

import com.topografix.gpx._1._0.Gpx;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import ws.sosna.pinetrail.model.Trail;
import ws.sosna.pinetrail.model.Waypoint;
import ws.sosna.pinetrail.utils.logging.Actions;
import ws.sosna.pinetrail.utils.logging.Markers;
import ws.sosna.pinetrail.utils.logging.StatusCodes;

/**
 * Performs the mapping of the GPX 1.0 information to the Pinetrail model.
 *
 * @author Xavier Sosnovsky
 */
final class Gpx10ToPinetrailMapper extends JaxbToPinetrailMapper<Gpx> {

    Gpx10ToPinetrailMapper(final boolean groupSubTrails) {
        super(groupSubTrails);
    }

    @SuppressWarnings("PMD.LawOfDemeter")
    @Override
    Set<Trail> mapToTrails(final Gpx gpx) {
        final Set<Trail> trails = new LinkedHashSet<>();
        final Set<Waypoint> waypoints = new LinkedHashSet<>();
        if (!gpx.getWpt().isEmpty() && 1 < gpx.getTrk().size()) {
            LOGGER.warn(Markers.IO.getMarker(), "{} | {} | {}.",
                Actions.PARSE, StatusCodes.NOT_ACCEPTABLE.getCode(),
                logMessages.getString("Error.WaypointsForManyTracks"));
        } else {
            waypoints.addAll(gpx.getWpt().stream().map(
                wpt -> handlePoint(wpt)).filter(wpt -> wpt != null).
                collect(Collectors.toSet()));
        }
        if (gpx.getTrk().isEmpty()) {
            LOGGER.warn(Markers.IO.getMarker(), "{} | {} | {}.",
                Actions.PARSE, StatusCodes.NOT_FOUND.getCode(),
                logMessages.getString("Error.NoTrack"));
        } else {
            for (final Gpx.Trk trk : gpx.getTrk()) {
                trails.addAll(handleTrack(trk, waypoints));
            }
        }
        if (!(gpx.getRte().isEmpty())) {
            LOGGER.warn(Markers.IO.getMarker(), "{} | {} | {}.",
                Actions.PARSE, StatusCodes.NOT_ACCEPTABLE.getCode(),
                logMessages.getString("Error.Route"));
        }
        return trails;
    }

    @SuppressWarnings("PMD.LawOfDemeter")
    private Set<Trail> handleTrack(final Gpx.Trk trk,
        final Set<Waypoint> waypoints) {
        if (trk.getTrkseg().isEmpty()) {
            LOGGER.warn(Markers.IO.getMarker(), "{} | {} | {}.",
                Actions.PARSE, StatusCodes.NOT_FOUND.getCode(),
                logMessages.getString("Error.NoSegment"));
        }
        final Set<Waypoint> points
            = handleAdditionalWaypoints(trk.getTrkseg().size() > 1, waypoints);
        return handleSegments(trk, points);
    }

    private Set<Trail> handleSegments(final Gpx.Trk trk,
        final Set<Waypoint> points) {
        int count = 1;
        final String trailName = trk.getName();
        final Set<Trail> trails = new LinkedHashSet<>();
        for (final Gpx.Trk.Trkseg seg : trk.getTrkseg()) {
            final Set<Waypoint> segPoints = seg.getTrkpt().stream().map(
                wpt -> handlePoint(wpt)).filter(wpt -> wpt != null).
                collect(Collectors.toSet());
            if (!groupSubTrails && 1 < trk.getTrkseg().size()) {
                points.clear();
            }
            points.addAll(segPoints);
            if (!groupSubTrails) {
                final String segName = trk.getTrkseg().size() > 1
                    ? trailName + " [" + count++ + "]" : trailName;
                final Trail trail
                    = getTrail(segName, points, trk.getDesc(), null);
                if (null != trail) {
                    trails.add(trail);
                }
            }
        }
        if (groupSubTrails) {
            final Trail trail
                = getTrail(trailName, points, trk.getDesc(), null);
            if (null != trail) {
                trails.add(trail);
            }
        }
        return trails;
    }

    @SuppressWarnings("PMD.LawOfDemeter")
    private Waypoint handlePoint(final Gpx.Wpt wpt) {
        return buildPoint(wpt.getTime().toGregorianCalendar().
            toInstant(), wpt.getLon().doubleValue(), wpt.getLat().doubleValue(),
            wpt.getEle(), wpt.getName(), wpt.getDesc(), null);
    }

    @SuppressWarnings("PMD.LawOfDemeter")
    private Waypoint handlePoint(final Gpx.Trk.Trkseg.Trkpt wpt) {
        return buildPoint(wpt.getTime().toGregorianCalendar().
            toInstant(), wpt.getLon().doubleValue(), wpt.getLat().doubleValue(),
            wpt.getEle(), wpt.getName(), wpt.getDesc(), null);
    }
}
