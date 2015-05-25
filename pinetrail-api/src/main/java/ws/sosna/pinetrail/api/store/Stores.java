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
package ws.sosna.pinetrail.api.store;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.ServiceLoader;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.sosna.pinetrail.api.io.Readers;
import ws.sosna.pinetrail.utils.error.ExecutionError;
import ws.sosna.pinetrail.utils.logging.Actions;
import ws.sosna.pinetrail.utils.logging.Markers;
import ws.sosna.pinetrail.utils.logging.StatusCodes;

/**
 * Utility class that instantiates the service to be used for persistence
 * storage.
 *
 * <p>
 * In the background, this class uses a ServiceLoader to register the
 * {@code StoreProvider} that will be used by the client.
 *
 * @author Xavier Sosnovsky
 */
public enum Stores {
    /**
     * Singleton instance of Pinetrail store services.
     */
    INSTANCE;

    private final Logger LOGGER = LoggerFactory.getLogger(Readers.class);
    private final ServiceLoader<StoreProvider> loader;
    private final Set<StoreProvider> stores;
    private final StoreProvider store;

    private Stores() {
        loader = ServiceLoader.load(StoreProvider.class);
        stores = new LinkedHashSet<>();
        for (final StoreProvider provider : loader) {
            stores.add(provider);
        }
        if (stores.size() > 1) {
            final String msg =
                "Found more than one service for storing trails.";
            LOGGER.warn(Markers.CONFIG.getMarker(), "{} | {} | " + msg,
                Actions.REGISTER, StatusCodes.NOT_ACCEPTABLE);
            throw new ExecutionError(msg, null, Markers.CONFIG.getMarker(),
                Actions.REGISTER, StatusCodes.NOT_ACCEPTABLE);
        }
        if (stores.isEmpty()) {
            final String msg = "Could not find a service for storing trails.";
            LOGGER.warn(Markers.CONFIG.getMarker(), "{} | {} | " + msg,
                Actions.REGISTER, StatusCodes.NOT_FOUND);
            throw new ExecutionError(msg, null, Markers.CONFIG.getMarker(),
                Actions.REGISTER, StatusCodes.NOT_FOUND);
        }
        store = new ArrayList<>(stores).get(0);
        LOGGER.info(Markers.CONFIG.getMarker(), "{} | {} | Registered a "
            + "store provider ({}).", Actions.REGISTER,
            StatusCodes.OK.getCode(), store.getClass().getCanonicalName());
    }

    /**
     * Returns the service to be used for persistence storage.
     *
     * @return the service to be used for persistence storage
     */
    public StoreProvider getStore() {
        return store;
    }
}
