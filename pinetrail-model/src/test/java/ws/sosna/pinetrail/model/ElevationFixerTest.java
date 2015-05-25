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

import java.time.Instant;
import java.util.SortedSet;
import java.util.TreeSet;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Xavier Sosnovsky
 */
public class ElevationFixerTest {

    @Test
    public void testApply() {
        final SortedSet<Waypoint> points = getWaypoints();
        final ElevationFixer instance = ElevationFixer.INSTANCE;
        final SortedSet<Waypoint> augmentedPoints = instance.apply(points);
        for (final Waypoint point : augmentedPoints) {
            if (null != point.getCoordinates().getElevation()) {
                assertTrue(110.61 <= point.getCoordinates().getElevation() &&
                    114.16 >= point.getCoordinates().getElevation());
            }
        }
    }

    private Waypoint createWaypoint(final double latitude,
        final double longitude) {
        final Coordinates coordinates = new CoordinatesBuilder(longitude,
            latitude).build();
        return new WaypointBuilder(Instant.EPOCH, coordinates).build();
    }

    private SortedSet<Waypoint> getWaypoints() {
        final SortedSet<Waypoint> points = new TreeSet<>();
        points.add(createWaypoint(50.1834285166, 8.7450412475));
        points.add(createWaypoint(50.1834630501, 8.7450394034));
        points.add(createWaypoint(50.1833731122, 8.7450366374));
        points.add(createWaypoint(50.1833943184, 8.7448001839));
        points.add(createWaypoint(50.1834155247, 8.7446464598));
        points.add(createWaypoint(50.1835140958, 8.7444315478));
        points.add(createWaypoint(50.1835442707, 8.7443784066));
        points.add(createWaypoint(50.1836881042, 8.7443323899));
        points.add(createWaypoint(50.1838302612, 8.7442308012));
        points.add(createWaypoint(50.1838716678, 8.7442001235));
        points.add(createWaypoint(50.1839823090, 8.7444794085));
        return points;
    }
}
