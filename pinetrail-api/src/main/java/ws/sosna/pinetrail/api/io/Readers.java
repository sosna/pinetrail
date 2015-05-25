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

import java.util.EnumMap;
import java.util.Map;
import java.util.ServiceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.sosna.pinetrail.utils.logging.Actions;
import ws.sosna.pinetrail.utils.logging.Markers;
import ws.sosna.pinetrail.utils.logging.StatusCodes;

/**
 * Utility class that instantiates readers for one of the supported formats.
 *
 * <p>
 * In the background, this class uses a ServiceLoader to register the
 * {@code ReaderProviders} that will be used to instantiate the {@code Readers}
 * returned to the client.
 *
 * @author Xavier Sosnovsky
 */
public enum Readers {

    /**
     * Singleton instance of Pinetrail readers.
     */
    INSTANCE;

    private final Map<Formats, ReaderProvider> providers;
    private final Logger LOGGER = LoggerFactory.getLogger(Readers.class);
    private final ServiceLoader<ReaderProvider> loader;

    private Readers() {
        LOGGER.info(Markers.CONFIG.getMarker(), "{} | {} | Created a registry "
            + "for accessing readers services.",
            Actions.CREATE, StatusCodes.OK.getCode());
        this.providers = new EnumMap<>(Formats.class);
        loader = ServiceLoader.load(ReaderProvider.class);
    }

    /**
     * Returns a reader that will process the supplied file.
     *
     * @param format the format the file is in
     *
     * @return a reader that will process the supplied file
     *
     * @throws UnsupportedOperationException if there is no provider of readers
     * for the supplied format.
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    public Reader newReader(final Formats format) {
        if (!(providers.containsKey(format))) {
            for (final ReaderProvider tmpProvider : loader) {
                if (null != tmpProvider.newReader(format)) {
                    registerProvider(format, tmpProvider);
                    break;
                }
            }
        }
        final ReaderProvider provider = providers.get(format);
        if (null == provider) {
            LOGGER.warn(Markers.IO.getMarker(),
                "{} | {} | Could not find a reader for {}.", Actions.GET,
                StatusCodes.NOT_FOUND.getCode(), format);
            throw new UnsupportedOperationException(
                "Could not find a reader for " + format);
        } else {
            LOGGER.debug(Markers.IO.getMarker(),
                "{} | {} | Returning a reader for {}.", Actions.GET,
                StatusCodes.OK.getCode(), format);
            return provider.newReader(format);
        }
    }

    @SuppressWarnings("PMD.LawOfDemeter")
    private void registerProvider(final Formats format,
        final ReaderProvider provider) {
        providers.putIfAbsent(format, provider);
        LOGGER.info(Markers.CONFIG.getMarker(), "{} | {} | Registered a "
            + "provider of readers for {} ({}).", Actions.REGISTER,
            StatusCodes.OK.getCode(), format, provider.getClass().
            getCanonicalName());
    }
}
