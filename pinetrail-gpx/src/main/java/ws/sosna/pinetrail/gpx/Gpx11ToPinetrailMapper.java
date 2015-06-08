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
package ws.sosna.pinetrail.gpx;

import com.topografix.gpx._1._1.GpxType;
import com.topografix.gpx._1._1.LinkType;
import com.topografix.gpx._1._1.TrkType;
import com.topografix.gpx._1._1.TrksegType;
import com.topografix.gpx._1._1.WptType;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import ws.sosna.pinetrail.model.Link;
import ws.sosna.pinetrail.model.LinkBuilder;
import ws.sosna.pinetrail.model.Trail;
import ws.sosna.pinetrail.model.Waypoint;
import ws.sosna.pinetrail.utils.logging.Actions;
import ws.sosna.pinetrail.utils.logging.Markers;
import ws.sosna.pinetrail.utils.logging.StatusCodes;

/**
 * Performs the mapping of the GPX 1.1 information to the Pinetrail model.
 *
 * @author Xavier Sosnovsky
 */
final class Gpx11ToPinetrailMapper extends JaxbToPinetrailMapper<GpxType> {

    Gpx11ToPinetrailMapper(final boolean groupSubTrails) {
        super(groupSubTrails);
    }

    @SuppressWarnings("PMD.LawOfDemeter")
    @Override
    Set<Trail> mapToTrails(final GpxType gpx) {
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
            for (final TrkType trk : gpx.getTrk()) {
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
    private Set<Trail> handleTrack(final TrkType trk,
        final Set<Waypoint> waypoints) {
        if (trk.getTrkseg().isEmpty()) {
            LOGGER.warn(Markers.IO.getMarker(), "{} | {} | {}.",
                Actions.PARSE, StatusCodes.NOT_FOUND.getCode(),
                logMessages.getString("Error.NoSegment"));
        }
        final Set<Link> links = trk.getLink().stream().map(
            lnk -> handleLink(lnk)).filter(lnk -> lnk != null).collect(
                Collectors.toSet());
        final Set<Waypoint> points
            = handleAdditionalWaypoints(trk.getTrkseg().size() > 1, waypoints);
        return handleSegments(trk, points, links);
    }

    private Set<Trail> handleSegments(final TrkType trk,
        final Set<Waypoint> points, final Set<Link> links) {
        int count = 1;
        final String trailName = trk.getName();
        final Set<Trail> trails = new LinkedHashSet<>();
        for (final TrksegType seg : trk.getTrkseg()) {
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
                    = getTrail(segName, points, trk.getDesc(), links);
                if (null != trail) {
                    trails.add(trail);
                }
            }
        }
        if (groupSubTrails) {
            final Trail trail
                = getTrail(trailName, points, trk.getDesc(), links);
            if (null != trail) {
                trails.add(trail);
            }
        }
        return trails;
    }

    @SuppressWarnings("PMD.LawOfDemeter")
    private Waypoint handlePoint(final WptType wpt) {
        final Set<Link> links = wpt.getLink().stream().map(lnk -> handleLink(
            lnk)).filter(lnk -> lnk != null).collect(Collectors.toSet());
        return buildPoint(wpt.getTime().toGregorianCalendar().
            toInstant(), wpt.getLon().doubleValue(), wpt.getLat().doubleValue(),
            wpt.getEle(), wpt.getName(), wpt.getDesc(), links);
    }

    @SuppressWarnings("PMD.LawOfDemeter")
    private Link handleLink(final LinkType linkType) {
        try {
            return new LinkBuilder(linkType.getText(), new URI(linkType.
                getHref())).build();
        } catch (final URISyntaxException e) {
            final String logMsg = logMessages.getString("Error.LinkURI")
                + "(" + linkType.getHref() + "). Error was: " + e.getMessage();
            LOGGER.warn(Markers.IO.getMarker(), "{} | {} | {}.",
                Actions.PARSE, StatusCodes.SYNTAX_ERROR.getCode(), logMsg);
            return null;
        }
    }
}
