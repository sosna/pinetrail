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
package ws.sosna.pinetrail.api.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.sosna.pinetrail.utils.logging.Actions;
import ws.sosna.pinetrail.utils.logging.Markers;
import ws.sosna.pinetrail.utils.logging.StatusCodes;

/**
 * Builds immutable instances of the {@code ReaderWork} interface.
 *
 * @author Xavier Sosnovsky
 *
 * @see ReaderSettings
 */
public final class ReaderSettingsBuilder {

    private boolean groupSubTrails;
    private static final Logger LOGGER
        = LoggerFactory.getLogger(ReaderSettings.class);

    /**
     * Instantiates a new ReaderWorkBuilder.
     */
    public ReaderSettingsBuilder() {
        super();
    }

    /**
     * Whether subtrails should be grouped into one {@code Trail}.
     *
     * <p>
     * Certain formats allow intermediary groupings between the {@code Trail}
     * and the {@code Waypoints}. Gpx for example has an additional level,
     * called segments between tracks and waypoints.
     *
     * <p>
     * If true, these intermediary levels will be merged into one {@code Trail}.
     * If false, each of these levels will appear as a separate {@code Trail}.
     *
     * <p>
     * Defauls to false.
     *
     * @param groupSubTrails whether subtrails should be grouped
     *
     * @return the builder with an updated value for groupSubTrails.
     */
    public ReaderSettingsBuilder groupSubTrails(final boolean groupSubTrails) {
        this.groupSubTrails = groupSubTrails;
        return this;
    }

    /**
     * Builds a new immutable instance of the {@code ReaderWork} interface.
     *
     * @return a new immutable instance of the ReaderWork interface
     */
    public ReaderSettings build() {
        return new ReaderWorkImpl(groupSubTrails);
    }

    private static final class ReaderWorkImpl implements ReaderSettings {

        private final boolean groupSubTrails;

        ReaderWorkImpl(final boolean groupSubTrails) {
            super();
            this.groupSubTrails = groupSubTrails;
            LOGGER.debug(Markers.IO.getMarker(), "{} | {} | Built a new "
                + "ReaderWork. Group subtrails: {}",
                Actions.CREATE, StatusCodes.OK.getCode(), groupSubTrails);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean groupSubTrails() {
            return groupSubTrails;
        }

        @Override
        public String toString() {
            return "ReaderWorkImpl{groupSubTrails=" + groupSubTrails + '}';
        }
    }
}
