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

import java.util.Set;
import java.util.UUID;
import ws.sosna.pinetrail.model.Trail;

/**
 * Contract for services that persist information about trails.
 *
 * <p>
 * Three methods are offered for retrieving information about trails. One
 * returns the matching trail itself, while the two others return
 * {@code TrailSummary} objects, i.e. key information about a trail but without
 * the trail itself. This is useful, for example, to get an overview of matching
 * trails (like on an index page).
 *
 * @author Xavier Sosnovsky
 */
public interface StoreProvider {

    /**
     * Retrieves the summary of all the trails available in the persistence
     * storage.
     *
     * @return the summary of all the trails available in the persistence
     * storage
     *
     * @throws ws.sosna.pinetrail.utils.error.ExecutionError in case an error
     * occurs during the retrieval process.
     */
    Set<TrailSummary> findAll();

    /**
     * Dispatches the summary of the trails matching the supplied filter.
     *
     * @param filter the filter defining the criteria for finding trails
     *
     * @return the summary of the trails matching the supplied filter
     *
     * @throws ws.sosna.pinetrail.utils.error.ExecutionError in case an error
     * occurs during the retrieval process.
     */
    Set<TrailSummary> findMatching(Query filter);

    /**
     * Dispatches the trail identified by the supplied id.
     *
     * @param id the id of the trail to be returned
     *
     * @return the trail identified by the supplied id.
     *
     * @throws ws.sosna.pinetrail.utils.error.ExecutionError in case an error
     * occurs during the retrieval process.
     */
    Trail findById(UUID id);

    /**
     * Save the supplied trail to the persistence storage.
     *
     * <p>
     * A {@code ws.sosna.pinetrail.api.event.JobCompletedEvent} should be
     * dispatched, once the trail has been successfully persisted. In case an
     * error occurs, a {@code ws.sosna.pinetrail.utils.error.ExecutionError}
     * should be thrown.
     *
     * <p>
     * This method can be used both for persisting new trails and for updating
     * persisted ones.
     *
     * @param trail the trail to be persisted
     *
     * @throws ws.sosna.pinetrail.utils.error.ExecutionError in case an error
     * occurs during the insertion process.
     */
    void persist(Trail trail);

    /**
     * Removes the trail identified with the supplied id from the store.
     *
     * <p>
     * A {@code ws.sosna.pinetrail.api.event.JobCompletedEvent} should be
     * dispatched, once the deletion has successfully finished. In case an
     * error occurs, a {@code ws.sosna.pinetrail.utils.error.ExecutionError}
     * should be thrown.
     *
     * @param trailId the id of the trail to be deleted
     *
     * @throws ws.sosna.pinetrail.utils.error.ExecutionError in case an error
     * occurs during the deletion process.
     */
    void delete(UUID trailId);
}
