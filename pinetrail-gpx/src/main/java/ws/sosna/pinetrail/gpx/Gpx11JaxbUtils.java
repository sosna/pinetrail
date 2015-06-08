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
import java.util.Locale;
import java.util.ResourceBundle;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import ws.sosna.pinetrail.utils.error.ExecutionError;
import ws.sosna.pinetrail.utils.logging.Actions;
import ws.sosna.pinetrail.utils.logging.Markers;
import ws.sosna.pinetrail.utils.logging.StatusCodes;

/**
 * Offers Gpx 11 JAXB resources to be shared among threads.
 *
 * <p>
 * Some resources like a JAXB context or XML schemas are expensive to create
 * but can be reused or shared among threads. This class offers such JAXB
 * resources.
 *
 * @author Xavier Sosnovsky
 */
enum Gpx11JaxbUtils {

    /**
     * Singleton instance of Gpx11JaxbUtils readers.
     */
    INSTANCE;

    private static final JAXBContext GPX11_CONTEXT;
    private static final Schema GPX11_SCHEMA;
    private static final Logger LOGGER;
    private static final ResourceBundle LOG_MESSAGES;

    static {
        LOGGER = LoggerFactory.getLogger(Gpx11JaxbUtils.class);
        LOG_MESSAGES = ResourceBundle.getBundle("GpxLogMessages",
            Locale.getDefault());
        try {
            GPX11_CONTEXT = JAXBContext.newInstance(GpxType.class);
            final SchemaFactory sf
                = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            GPX11_SCHEMA = sf.newSchema(Gpx11JaxbUtils.class.getClassLoader().
                getResource("gpx.1_1.xsd"));
            LOGGER.info(Markers.CONFIG.getMarker(), "{} | {} | {}.",
                Actions.CREATE, StatusCodes.OK.getCode(), LOG_MESSAGES.
                getString("Config.CreatingContext"));
        } catch (final JAXBException e) {
            throw new ExecutionError(LOG_MESSAGES.getString(
                "Error.CreatingContext"), e, Markers.CONFIG.
                getMarker(), Actions.CREATE, StatusCodes.INTERNAL_ERROR);
        } catch (final SAXException e) {
            throw new ExecutionError(LOG_MESSAGES.getString(
                "Error.ParsingSchema"), e, Markers.CONFIG.getMarker(),
                Actions.PARSE, StatusCodes.SYNTAX_ERROR);
        }
    }

    /**
     * Gets the JAXBContext for GPX version 1.1.
     *
     * @return the JAXBContext for GPX version 1.1
     */
    public JAXBContext getGpx11Context() {
        return GPX11_CONTEXT;
    }

    /**
     * Gets the schema for GPX version 1.1.
     *
     * @return the schema for GPX version 1.1
     */
    public Schema getGpx11Schema() {
        return GPX11_SCHEMA;
    }
}
