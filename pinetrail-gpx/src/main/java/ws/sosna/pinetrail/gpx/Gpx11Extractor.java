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
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import org.slf4j.LoggerFactory;
import ws.sosna.pinetrail.utils.error.ExecutionError;
import ws.sosna.pinetrail.utils.logging.Actions;
import ws.sosna.pinetrail.utils.logging.Markers;
import ws.sosna.pinetrail.utils.logging.StatusCodes;

/**
 * Performs the extraction of the GPX 1.1 information using JAXB.
 *
 * @author Xavier Sosnovsky
 */
final class Gpx11Extractor {

    private static final org.slf4j.Logger LOGGER
        = LoggerFactory.getLogger(Gpx11Reader.class);
    private final ResourceBundle logMessages;

    Gpx11Extractor() {
        super();
        logMessages = ResourceBundle.getBundle("GpxLogMessages",
            Locale.getDefault());
        LOGGER.debug(Markers.IO.getMarker(), "{} | {} | {}.",
            Actions.CREATE, StatusCodes.OK.getCode(), logMessages.getString(
                "Beans.CreatedGpx11Extractor"));
    }

    @SuppressWarnings("PMD.LawOfDemeter")
    GpxType parseXml(final Path fileLocation) {
        try (final BufferedReader reader
            = Files.newBufferedReader(fileLocation)) {
            final Unmarshaller u = GpxJaxbUtils.INSTANCE.getGpx11Context().
                createUnmarshaller();
            u.setSchema(GpxJaxbUtils.INSTANCE.getGpx11Schema());
            final Set<String> validationIssues = new LinkedHashSet<>();
            u.setEventHandler(event -> validationIssues.add(event.toString()));
            final JAXBElement<GpxType> root = u.unmarshal(new StreamSource(
                reader), GpxType.class);
            if (!validationIssues.isEmpty()) {
                LOGGER.warn(Markers.IO.getMarker(), "{} | {} | {}.",
                    Actions.VALIDATE, StatusCodes.SYNTAX_ERROR.getCode(),
                    validationIssues);
            }
            return root.getValue();
        } catch (final JAXBException e) {
            throw new ExecutionError(logMessages.getString("Error.Parsing")
                + "(" + fileLocation.toAbsolutePath().normalize() + ")",
                e.getLinkedException(), Markers.IO.getMarker(), Actions.PARSE,
                StatusCodes.SYNTAX_ERROR);
        } catch (final NoSuchFileException e) {
            throw new ExecutionError(logMessages.getString("Error.FileNotFound")
                + " " + fileLocation.toAbsolutePath().normalize(), null,
                Markers.IO.getMarker(), Actions.OPEN, StatusCodes.NOT_FOUND);
        } catch (final IOException e) {
            throw new ExecutionError(logMessages.getString("Error.Opening")
                + "(" + fileLocation.toAbsolutePath().normalize() + ")", e,
                Markers.IO.getMarker(), Actions.OPEN,
                StatusCodes.INTERNAL_ERROR);
        }
    }
}
