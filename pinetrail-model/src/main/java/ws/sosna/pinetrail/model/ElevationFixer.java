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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.prefs.Preferences;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ws.sosna.pinetrail.utils.error.ExecutionError;
import ws.sosna.pinetrail.utils.logging.Actions;
import ws.sosna.pinetrail.utils.logging.Markers;
import ws.sosna.pinetrail.utils.logging.StatusCodes;

/**
 * Determines the elevation of the points in the trail.
 *
 * <p>
 * Regardless of whether the barometric pressure or the GPS signal is used to
 * measure the elevation, GPX files often contain elevation data of questionable
 * accuracy. Therefore, the elevation web service kindly offered by MapQuest is
 * used to get better elevation data.
 *
 * @author Xavier Sosnovsky
 */
enum ElevationFixer implements Function<SortedSet<Waypoint>,
    SortedSet<Waypoint>> {

    /**
     * Singleton that returns an instance of the ElevationFixer.
     */
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(
        ElevationFixer.class);
    private static final int DEFAULT_TIME_OUT = 5000;
    private static final int ASCII_QM = 63;
    private static final int HEXA_32 = 0x20;
    private static final int HEXA_31 = 0x1f;
    private static final int BITS_CHUNK_SIZE = 5;

    /**
     * Determines the elevation of the points in the trail.
     *
     * @param points the points that make up the trail
     *
     * @return the points that make up the trail, with corrected elevation data
     */
    @Override
    public SortedSet<Waypoint> apply(final SortedSet<Waypoint> points) {
        final String key = Preferences.userRoot().node(
                "ws.sosna.pinetrail.UserSettings").get("mapQuestKey", "");
        final SortedSet<Waypoint> response = new TreeSet<>();
        if (key.isEmpty()) {
            LOGGER.warn(Markers.MODEL.getMarker(), "{} | {} | {}",
                Actions.ANALYSE, StatusCodes.NOT_FOUND.getCode(),
                "MapQuest key not found: Elevation data will not be"
                    + " corrected.");
            response.addAll(points);
        } else if (points.isEmpty()) {
            LOGGER.info(Markers.MODEL.getMarker(), "{} | {} | {}",
                Actions.ANALYSE, StatusCodes.NOT_FOUND.getCode(),
                "No elevation data to be corrected.");
        } else {
            try {
                final String input = compressPoints(points);
                final List<Double> elevations
                    = processResponse(parseXml(askMapQuest(input, key)));
                response.addAll(replaceElevation(points, elevations));
                LOGGER.info(Markers.MODEL.getMarker(), "{} | {} | {}",
                Actions.ANALYSE, StatusCodes.OK.getCode(),
                "Successfully retrieved elevation data with MapQuest");
            } catch (final ExecutionError e) {
                LOGGER.warn(Markers.MODEL.getMarker(), "{} | {} | {}",
                Actions.ANALYSE, StatusCodes.INTERNAL_ERROR.getCode(),
                "There was an error getting elevation data from MapQuest. "
                    + "Initial elevation data will be used instead. The error "
                    + "was: " + e.getMessage());
                response.addAll(points);
            }
        }
        return response;
    }

    /*
     * Retrieve elevation data using the service provided by MapQuest.
     */
    private InputStream askMapQuest(final String compressedInput,
        final String key) {
        final String url
            = "http://open.mapquestapi.com/elevation/v1/profile?key=" + key;
        final String params =
            "outFormat=xml"
            + "&shapeFormat=cmp6"
            + "&useFilter=true"
            + "&outShapeFormat=none"
            + "&latLngCollection=" + compressedInput;
        final byte[] postData = params.getBytes(StandardCharsets.UTF_8);
        final int postDataLength = postData.length;

        try {
            final URL target = new URL(url);
            LOGGER.debug(Markers.NETWORK.getMarker(), "{} | {} | Created URL "
                + "for reverse elevation: {}", Actions.CREATE,
                StatusCodes.OK.getCode(), url);
            final HttpURLConnection conn
                = (HttpURLConnection) target.openConnection();
            //Abort after 5 seconds
            conn.setConnectTimeout(DEFAULT_TIME_OUT);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty( "charset", "utf-8");
            conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
            conn.setUseCaches( false );
            try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                wr.write(postData);
                wr.flush();
            }
            //conn.connect();
            return conn.getInputStream();
        } catch (final SocketTimeoutException e) {
            throw new ExecutionError("Connection to MapQuest timed out", e,
                Markers.NETWORK.getMarker(), Actions.GET, StatusCodes.TIME_OUT);
        } catch (final IOException e) {
            throw new ExecutionError(e.getMessage(), e.getCause(),
                Markers.NETWORK.getMarker(), Actions.OPEN,
                StatusCodes.INTERNAL_ERROR);
        }
    }

    private Document parseXml(final InputStream xmlInput) {
        final DocumentBuilderFactory dbFactory
            = DocumentBuilderFactory.newInstance();
        try {
            final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            final Document doc = dBuilder.parse(xmlInput);
            LOGGER.debug(Markers.MODEL.getMarker(), "{} | {} | MapQuest "
                + "response successfully read as DOM object.",
                Actions.PARSE, StatusCodes.OK.getCode());
            return doc;
        } catch (final ParserConfigurationException e) {
            throw new ExecutionError("Error creating DOM builder", e,
                Markers.MODEL.getMarker(), Actions.CREATE,
                StatusCodes.INTERNAL_ERROR);
        } catch (final SAXException e) {
            throw new ExecutionError("Error parsing XML from MapQuest (SAX)", e,
                Markers.MODEL.getMarker(), Actions.PARSE,
                StatusCodes.SYNTAX_ERROR);
        } catch (final IOException e) {
            throw new ExecutionError("Error parsing XML from MapQuest (IO)", e,
                Markers.MODEL.getMarker(), Actions.PARSE,
                StatusCodes.INTERNAL_ERROR);
        }
    }

    private List<Double> processResponse(final Document xmlDocument) {
        final List<Double> elevations = new ArrayList<>();
        final NodeList nList
            = xmlDocument.getElementsByTagName("distanceHeight");
        for (int temp = 0; temp < nList.getLength(); temp++) {
            final Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                final Element eElement = (Element) nNode;
                final Double elevation = Double.parseDouble(eElement.
                    getElementsByTagName("height").item(0).getTextContent());
                LOGGER.debug(Markers.MODEL.getMarker(), "{} | {} | MapQuest"
                    + " response contains elevation data: {}.",
                    Actions.ANALYSE, StatusCodes.OK.getCode(), elevation);
                elevations.add(elevation);
            }
        }
        if (0 == elevations.size()) {
            throw new ExecutionError("Could not find elevation data in the"
                + " response from MapQuest.", null, Markers.MODEL.
                getMarker(), Actions.GET, StatusCodes.NOT_FOUND);
        }
        return elevations;
    }

    private String compressPoints(final Set<Waypoint> points) {
        int oldLat = 0;
        int oldLng = 0;
        final StringBuilder encoded = new StringBuilder();
        final double precision = Math.pow(10, 6);
        for (final Waypoint point : points) {
            final int lat = (int) Math.round(point.getCoordinates().
                getLatitude() * precision);
            final int lng = (int) Math.round(point.getCoordinates().
                getLongitude() * precision);
            encoded.append(encodeNumber(lat - oldLat));
            encoded.append(encodeNumber(lng - oldLng));
            oldLat = lat;
            oldLng = lng;
        }
        return encoded.toString();
    }

    private String encodeNumber(final int number) {
        int num = number << 1;
        if (num < 0) {
            num = ~(num);
        }
        final StringBuilder encoded = new StringBuilder();
        while (num >= HEXA_32) {
            encoded.append(Character.toChars((HEXA_32 | (num & HEXA_31))
                + ASCII_QM));
            num >>= BITS_CHUNK_SIZE;
        }
        encoded.append(Character.toChars(num + ASCII_QM));
        return encoded.toString();
    }

    private SortedSet<Waypoint> replaceElevation(final Set<Waypoint> points,
        final List<Double> elevations) {
        if (points.size() != elevations.size()) {
            throw new ExecutionError("Elevation data is incomplete. Expected "
                + points.size() + " but found only " + elevations.size()
                + " points in the response from MapQuest.", null, Markers.MODEL.
                getMarker(), Actions.GET, StatusCodes.NOT_FOUND);
        }
        final SortedSet<Waypoint> augmentedPoints = new TreeSet<>();
        int i = 0;
        for (final Waypoint point : points) {
            final Coordinates c = CoordinatesBuilder.of(point.getCoordinates())
                .elevation(elevations.get(i)).build();
            augmentedPoints.add(
                WaypointBuilder.of(point).coordinates(c).build());
            i++;
        }
        return augmentedPoints;
    }
}
