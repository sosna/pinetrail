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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.sosna.pinetrail.api.io.Writer;
import ws.sosna.pinetrail.api.io.WriterSettings;
import ws.sosna.pinetrail.api.io.WriterSettingsBuilder;
import ws.sosna.pinetrail.model.Trail;
import ws.sosna.pinetrail.utils.error.ExecutionError;
import ws.sosna.pinetrail.utils.logging.Actions;
import ws.sosna.pinetrail.utils.logging.Markers;
import ws.sosna.pinetrail.utils.logging.StatusCodes;

/**
 * Writes a trail in GPX 1.1 format.
 *
 * @author Xavier Sosnovsky
 */
final class Gpx11Writer implements Writer {

    private static final Logger LOGGER
        = LoggerFactory.getLogger(Gpx11Writer.class);
    private WriterSettings settings;

    Gpx11Writer() {
        super();
        this.settings = new WriterSettingsBuilder().build();
    }

    @Override
    public Writer configure(final WriterSettings settings) {
        this.settings = settings;
        return this;
    }

    @Override
    public void accept(final Trail trail, final Path location) {
        try {
            LOGGER.info(Markers.IO.getMarker(), "{} | {} | {}.",
                Actions.CREATE, StatusCodes.OK.getCode(), "Started writing "
                + "GPX 1.1 file "
                + location.toAbsolutePath().normalize().toString());
            if (!settings.overwriteIfExists() && Files.exists(location)) {
                final String msg = location.toAbsolutePath().normalize().
                    toString() + " already exists and writer is not allowed"
                    + " to overwrite existing files";
                throw new ExecutionError(msg, null, Markers.IO.getMarker(),
                    Actions.CREATE, StatusCodes.SYNTAX_ERROR);
            }
            final long start = System.currentTimeMillis();
            final GpxType gpx = new PinetrailToGpx11Mapper().getGpxInstance(
                trail, settings);
            final Marshaller marshaller
                = Gpx11JaxbUtils.INSTANCE.getGpx11Context().createMarshaller();
            if (settings.prettyPrinting()) {
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            }
            marshaller.marshal(new JAXBElement<>(
                new QName("http://www.topografix.com/GPX/1/1", "gpx"),
                GpxType.class, gpx), Files.newBufferedWriter(location));
            final long end = System.currentTimeMillis();
            LOGGER.info(Markers.PERFORMANCE.getMarker(), "{} | {} | {}.",
                Actions.CREATE, StatusCodes.OK.getCode(), "Written "
                + location.toAbsolutePath().normalize().toString() + " in "
                + (end - start) + "ms");
        } catch (final JAXBException | IOException e) {
            LOGGER.error(Markers.IO.getMarker(), "{} | {} | {}.",
                Actions.CREATE, StatusCodes.INTERNAL_ERROR.getCode(),
                e.getMessage());
            throw new ExecutionError(e.getMessage(), e, Markers.IO.getMarker(),
                Actions.CREATE, StatusCodes.SYNTAX_ERROR);
        } catch (final ExecutionError e) {
            LOGGER.error(e.getMarker(), "{} | {} | {}.", e.getAction(), e.
                getErrorCode().getCode(), e.getMessage() + (null == e.getCause()
                    ? "" : ": " + e.getCause().getMessage()));
            throw e;
        }
    }
}
