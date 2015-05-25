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

/**
 * The type of point of interest.
 *
 * <p>
 * Many waypoints simply represent a GPS recording at a certain point in time.
 * Others however represent a location worth documenting or visiting again, such
 * as a museum, a castle, a point offering a nice panoramic view, etc. A
 * {@code WaypointType} allows classifying different such points of interest by
 * type.
 *
 * @author Xavier Sosnovsky
 */
public enum WaypointType {

    /**
     * Hotel, camping, etc.
     */
    ACCOMMODATION,
    /**
     * School, university, theatre, museum, Art gallery, etc.
     */
    EDUCATIONAL_BUILDING,
    /**
     * Restaurant, bar, etc.
     */
    FOOD_AND_DRINK,
    /**
     * Town hall, parliament, palace, etc.
     */
    GOVERNMENT_BUILDING,
    /**
     * Church, monastery, cathedral, etc.
     */
    RELIGIOUS_BUILDING,
    /**
     * Airport, bus or train stations, etc.
     */
    TRANSPORT_BUILDING,
    /**
     * Castle, watchtower, citadel, fortifications, etc.
     */
    MILITARY_BUILDING,
    /**
     * A point offering a nice panoramic view.
     */
    VIEWPOINT;
}
