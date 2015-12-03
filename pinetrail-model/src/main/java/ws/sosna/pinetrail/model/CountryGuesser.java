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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
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
 * Determines the countries crossed by the trail.
 *
 * <p>
 * This is possible thanks to the reverse geocoding facility kindly offered by
 * mapquest (http://open.mapquestapi.com/nominatim/).
 *
 * @author Xavier Sosnovsky
 */
enum CountryGuesser implements Function<SortedSet<Waypoint>, Set<String>> {

    /**
     * Singleton that returns an instance of a CountryGuesser.
     */
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(
        CountryGuesser.class);
    private static final int DEFAULT_TIME_OUT = 3000;
    private String mapQuestKey;

    /**
     * Determines the countries crossed by the trail, using reverse geocoding.
     *
     * @param points the points that make up the trail
     * @return the countries crossed by the trail
     */
    @Override
    public Set<String> apply(final SortedSet<Waypoint> points) {
        final Set<String> countries = new LinkedHashSet<>();
        mapQuestKey = Preferences.userRoot().node(
            "ws.sosna.pinetrail.UserSettings").get("mapQuestKey", "");
        if (mapQuestKey.isEmpty()) {
            LOGGER.warn(Markers.MODEL.getMarker(), "{} | {} | {}",
                Actions.ANALYSE, StatusCodes.NOT_FOUND.getCode(),
                "MapQuest key not found: Country will not be guessed.");
            return countries;
        }
        
        try {
            countries.addAll(getSelectedPoints(points).parallelStream()
                .map(p -> getCountry(parseXml(askMapquest(p))))
                .map(String::toUpperCase)
                .collect(Collectors.toSet()));
            return countries;
        } catch (final ExecutionError e) {
            LOGGER.warn(e.getMarker(), "{} | {} | {}", e.getAction(),
                e.getErrorCode().getCode(), e.getMessage()
                + (null == e.getCause() ? "" : ": "
                    + e.getCause().getMessage()));
            return countries;
        }
    }

    /*
     * Returns the point selected for reverse geocoding out of the supplied
     * collection of waypoints. The default strategy is to randomely select one
     * in the collection of points.
     */
    private Set<Waypoint> getSelectedPoints(final SortedSet<Waypoint> points) {
        final Set<Waypoint> selected = new LinkedHashSet<>();
        if (Boolean.parseBoolean(Preferences.userRoot()
            .node("ws.sosna.pinetrail.model.Trail")
            .get("crossBorder", "false"))) {
            selected.addAll(getOnePointPerHour(points));
        } else {
            selected.add(getRandomPoint(points));
        }
        return selected;
    }

    /*
     * Returns the point selected randomely for reverse geocoding out of the
     * supplied collection of waypoints.
     */
    private Waypoint getRandomPoint(final SortedSet<Waypoint> points) {
        final List<Waypoint> ptsList = new ArrayList<>(points);
        return ptsList.get(new Random().nextInt(points.size()));
    }

    /*
     * Returns the points selected randomely for reverse geocoding out of the
     * supplied collection of waypoints, taking one point per hour.
     */
    private Set<Waypoint> getOnePointPerHour(final SortedSet<Waypoint> points) {
        final Set<Waypoint> selected = new LinkedHashSet<>();
        final long hours = ChronoUnit.HOURS.between(points.first().getTime(),
            points.last().getTime());
        final double step = Math.floor(points.size() / hours);
        int count = 0;
        final ArrayList<Waypoint> ptsList = new ArrayList<>(points);
        while (count < points.size()) {
            selected.add(ptsList.get(count));
            count += step;
        }
        selected.add(points.last());
        return selected;
    }

    /*
     * Performs the reverse geocoding of the supplied point, using the service
     * provided by Mapquest.
     */
    private InputStream askMapquest(final Waypoint point) {
        final String url = "http://open.mapquestapi.com/nominatim/v1/"
            + "reverse.php?format=xml"
            + "&lat=" + point.getCoordinates().getLatitude()
            + "&lon=" + point.getCoordinates().getLongitude()
            + "&key=" + mapQuestKey;
        try {
            final URL target = new URL(url);
            LOGGER.debug(Markers.NETWORK.getMarker(), "{} | {} | Created URL "
                + "for reverse geocoding: {}", Actions.CREATE,
                StatusCodes.OK.getCode(), url);
            final HttpURLConnection conn
                = (HttpURLConnection) target.openConnection();
            //Abort after 3 seconds
            conn.setConnectTimeout(DEFAULT_TIME_OUT);
            conn.connect();
            return conn.getInputStream();
        } catch (final SocketTimeoutException e) {
            throw new ExecutionError("Connection to mapquest timed out", e,
                Markers.NETWORK.getMarker(), Actions.GET, StatusCodes.TIME_OUT);
        } catch (final IOException e) {
            throw new ExecutionError("Error querying mapquest", e,
                Markers.NETWORK.getMarker(), Actions.OPEN,
                StatusCodes.INTERNAL_ERROR);
        }
    }

    /*
     * Returns an XML document out of the supplied XML string.
     */
    private Document parseXml(final InputStream xmlInput) {
        final DocumentBuilderFactory dbFactory
            = DocumentBuilderFactory.newInstance();
        try {
            final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            final Document doc = dBuilder.parse(xmlInput);
            LOGGER.debug(Markers.MODEL.getMarker(), "{} | {} | Mapquest "
                + "response successfully read as DOM object.",
                Actions.PARSE, StatusCodes.OK.getCode());
            return doc;
        } catch (final ParserConfigurationException e) {
            throw new ExecutionError("Error creating DOM builder", e,
                Markers.MODEL.getMarker(), Actions.CREATE,
                StatusCodes.INTERNAL_ERROR);
        } catch (final SAXException e) {
            throw new ExecutionError("Error parsing XML from mapquest (SAX)", e,
                Markers.MODEL.getMarker(), Actions.PARSE,
                StatusCodes.SYNTAX_ERROR);
        } catch (final IOException e) {
            throw new ExecutionError("Error parsing XML from mapquest (IO)", e,
                Markers.MODEL.getMarker(), Actions.PARSE,
                StatusCodes.INTERNAL_ERROR);
        }
    }

    /*
     * Extracts the country code out of the supplied XML document.
     */
    private String getCountry(final Document xmlDocument) {
        String country = null;
        final NodeList nList
            = xmlDocument.getElementsByTagName("addressparts");
        for (int temp = 0; temp < nList.getLength(); temp++) {
            final Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                final Element eElement = (Element) nNode;
                country = eElement.getElementsByTagName("country_code").
                    item(0).getTextContent();
                LOGGER.debug(Markers.MODEL.getMarker(), "{} | {} | Mapquest"
                    + " response contains a country code: {}.",
                    Actions.ANALYSE, StatusCodes.OK.getCode(), country);
                break;
            }
        }
        if (null == country) {
            throw new ExecutionError("Could not find a country code in the"
                + " response from Mapquest.", null, Markers.MODEL.
                getMarker(), Actions.GET, StatusCodes.NOT_FOUND);
        }
        return country;
    }
}
