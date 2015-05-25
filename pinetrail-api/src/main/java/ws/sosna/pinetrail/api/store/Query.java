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

import java.time.Instant;
import javax.validation.constraints.Pattern;
import ws.sosna.pinetrail.model.Activity;
import ws.sosna.pinetrail.model.Level;

/**
 * Represents the criteria for filtering the trails to be returned from the
 * persistence storage.
 *
 * <p>
 * For example, to return all the hiking trails in Germany that have at least 4
 * stars and that are suitable for beginners:<br>
 * <code>
 * final Query query = new QueryBuilder().activity(Activity.HIKING).
 * country("DE").level(Level.BEGINNER).minimumRating(4).build();<br>
 * final StoreProvider store = Stores.INSTANCE.getStore();<br>
 * final Set&lt;TrailSummary&gt; trails = store.findMatching(query);
 * </code>
 *
 * @author Xavier Sosnovsky
 */
public interface Query {

    /**
     * Returns an ISO 3166-1 two-letter country code representing the country
     * crossed by the trails to be returned.
     *
     * @return the country where the activity occurred
     */
    @Pattern(regexp = "[A-Z]{2}", message = "{Api.Query.Country.Pattern}")
    String getCountry();

    /**
     * The activity performed on the trails to be returned.
     *
     * @return the activity performed on the trail
     */
    Activity getActivity();

    /**
     * The minimum rating for the trails to be returned.
     *
     * @return the minimum rating for the trails
     */
    int getMinimalRating();

    /**
     * The difficulty level for the trails to be returned.
     *
     * @return the difficulty level
     */
    Level getLevel();

    /**
     * The date after which the activity represented by the trail occurred.
     *
     * @return the date after which the activity represented by the trail
     * occurred
     */
    Instant getAfterDate();

    /**
     * The date before which the activity represented by the trail occurred.
     *
     * @return the date before which the activity represented by the trail
     * occurred
     */
    Instant getBeforeDate();
}
