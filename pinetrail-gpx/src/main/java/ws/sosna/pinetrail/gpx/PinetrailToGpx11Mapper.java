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

import com.topografix.gpx._1._1.GpxType;
import com.topografix.gpx._1._1.LinkType;
import com.topografix.gpx._1._1.TrkType;
import com.topografix.gpx._1._1.TrksegType;
import com.topografix.gpx._1._1.WptType;
import java.math.BigDecimal;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.sosna.pinetrail.api.io.WriterSettings;
import ws.sosna.pinetrail.model.Link;
import ws.sosna.pinetrail.model.Trail;
import ws.sosna.pinetrail.model.Waypoint;
import ws.sosna.pinetrail.utils.logging.Actions;
import ws.sosna.pinetrail.utils.logging.Markers;
import ws.sosna.pinetrail.utils.logging.StatusCodes;

/**
 *
 * @author Xavier Sosnovsky
 */
final class PinetrailToGpx11Mapper {

    private static final Logger LOGGER
        = LoggerFactory.getLogger(PinetrailToGpx11Mapper.class);
    private WriterSettings settings;

    PinetrailToGpx11Mapper() {
        super();
    }

    GpxType getGpxInstance(final Trail trail, final WriterSettings settings) {
        this.settings = settings;
        final GpxType gpx = new GpxType();
        gpx.setCreator("Pinetrail");
        gpx.getTrk().add(getTrack(trail));
        gpx.setVersion("1.1");
        return gpx;
    }

    private TrkType getTrack(final Trail trail) {
        final TrkType track = new TrkType();
        track.setName(trail.getName());
        track.setDesc(trail.getDescription());
        for (final Link link : trail.getLinks()) {
            track.getLink().add(getLink(link));
        }
        final TrksegType segment = new TrksegType();

        Set<Waypoint> points = handleOutliers(trail);
        points = handleIdlePoints(points);
        for (final Waypoint point : points) {
            try {
                segment.getTrkpt().add(getWaypoint(point));
            } catch (final DatatypeConfigurationException e) {
                LOGGER.warn(Markers.IO.getMarker(), "{} | {} | {}.",
                    Actions.CREATE, StatusCodes.INTERNAL_ERROR.getCode(),
                    "Problem marshalling waypoint " + point.getTime()
                    + ". Point will be ignored.");
            }
        }
        track.getTrkseg().add(segment);
        return track;
    }

    private LinkType getLink(final Link link) {
        final LinkType linkType = new LinkType();
        linkType.setHref(link.getLocation().toString());
        linkType.setText(link.getLabel());
        return linkType;
    }

    private WptType getWaypoint(final Waypoint pt)
        throws DatatypeConfigurationException {
        final WptType waypoint = new WptType();
        waypoint.setLat(BigDecimal.valueOf(pt.getCoordinates().getLatitude()));
        waypoint.setLon(BigDecimal.valueOf(pt.getCoordinates().getLongitude()));
        waypoint.setEle(BigDecimal.valueOf(pt.getCoordinates().getElevation()));
        waypoint.setName(pt.getName());
        waypoint.setDesc(pt.getDescription());
        final GregorianCalendar gCalendar = new GregorianCalendar();
        gCalendar.setTime(new Date(pt.getTime().toEpochMilli()));
        final XMLGregorianCalendar xmlCalendar = DatatypeFactory.newInstance().
            newXMLGregorianCalendar(gCalendar);
        waypoint.setTime(xmlCalendar);
        if (null != pt.getType()) {
            waypoint.setType(pt.getType().toString());
        }
        pt.getLinks().stream().
            forEach((link) -> {
                waypoint.getLink().add(getLink(link));
            });
        return waypoint;
    }

    private Set<Waypoint> getOutliers(final Trail trail) {
        final Set<Waypoint> outliers = new LinkedHashSet<>();
        outliers.addAll(trail.getStatistics().getDistanceSummary().
            getOutliers());
        outliers.addAll(trail.getStatistics().
            getElevationDifferenceSummary().getOutliers());
        outliers.addAll(trail.getStatistics().getElevationSummary().
            getOutliers());
        outliers.addAll(trail.getStatistics().getGradeSummary().
            getOutliers());
        outliers.addAll(trail.getStatistics().getSpeedSummary().
            getOutliers());
        return outliers;
    }

    private Set<Waypoint> handleOutliers(final Trail trail) {
        final Set<Waypoint> outliers = getOutliers(trail);
        final Set<Waypoint> points = new LinkedHashSet<>();
        if (settings.writeOutliers()) {
            points.addAll(trail.getWaypoints());
        } else {
            points.addAll(trail.getWaypoints().stream().filter(
                item -> !(outliers.contains(item))).collect(Collectors.toSet()));
        }
        return points;
    }

    private Set<Waypoint> handleIdlePoints(final Set<Waypoint> input) {
        final Set<Waypoint> points = new LinkedHashSet<>();
        if (settings.writeIdlePoints()) {
            points.addAll(input);
        } else {
            points.addAll(input.stream().filter(Waypoint::isActive).collect(
                Collectors.toSet()));
        }
        return points;
    }
}
