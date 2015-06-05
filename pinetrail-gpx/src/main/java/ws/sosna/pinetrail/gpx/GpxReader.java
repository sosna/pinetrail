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
import java.nio.file.Path;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.sosna.pinetrail.api.io.Reader;
import ws.sosna.pinetrail.api.io.ReaderSettings;
import ws.sosna.pinetrail.model.Trail;
import ws.sosna.pinetrail.utils.error.ExecutionError;
import ws.sosna.pinetrail.utils.logging.Actions;
import ws.sosna.pinetrail.utils.logging.Markers;
import ws.sosna.pinetrail.utils.logging.StatusCodes;

/**
 * Reads GPX 1.1 files and map the extracted information to the Pinetrail model.
 *
 * @author Xavier Sosnovsky
 */
final class Gpx11Reader implements Reader {

    private static final Logger LOGGER
        = LoggerFactory.getLogger(Gpx11Reader.class);
    private boolean groupSubTrails;

    Gpx11Reader() {
        super();
        groupSubTrails = false;
        LOGGER.debug(Markers.IO.getMarker(), "{} | {} | {}.",
            Actions.CREATE, StatusCodes.OK.getCode(),
            "Instantiated a new Gpx11Reader");
    }

    @Override
    public Reader configure(final ReaderSettings settings) {
        groupSubTrails = settings.groupSubTrails();
        return this;
    }

    @Override
    public Set<Trail> apply(final Path fileLocation) {
        try {
            LOGGER.info(Markers.IO.getMarker(), "{} | {} | {}.",
                Actions.PARSE, StatusCodes.OK.getCode(), "Started parsing "
                + "GPX 1.1 file " + fileLocation.toAbsolutePath().normalize().
                    toString());
            final long start = System.currentTimeMillis();
            final GpxType gpx = new Gpx11Extractor().parseXml(fileLocation);
            final long parsingTime = System.currentTimeMillis() - start;
            final long startMapping = System.currentTimeMillis();
            final Set<Trail> trails
                = new Gpx11ToPinetrailMapper(groupSubTrails).mapToTrails(gpx);
            final long end = System.currentTimeMillis();
            LOGGER.info(Markers.PERFORMANCE.getMarker(), "{} | {} | {}",
                Actions.PARSE, StatusCodes.OK.getCode(), "Processed "
                + fileLocation.toAbsolutePath().normalize().toString() + " in "
                    + (end - start) + "ms (parsing: "
                + parsingTime + " - mapping: " + (end - startMapping) + ")");
            return trails;
        } catch (final ExecutionError e) {
            LOGGER.error(e.getMarker(), "{} | {} | {}.", e.getAction(), e.
                getErrorCode().getCode(), e.getMessage() + (null == e.getCause()
                    ? "" : ": " + e.getCause().getMessage()));
            throw e;
        }
    }
}
