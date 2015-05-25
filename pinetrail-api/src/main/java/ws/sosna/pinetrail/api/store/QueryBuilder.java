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
import ws.sosna.pinetrail.model.Activity;
import ws.sosna.pinetrail.model.Level;

/**
 * Builds immutable instances of the {@code Query} interface.
 *
 * @see Query
 *
 * @author Xavier Sosnovsky
 */
public final class QueryBuilder {

    private Instant after;
    private Instant before;
    private int rating;
    private Activity activity;
    private String country;
    private Level level;

    /**
     * Instantiates a new QueryBuilder.
     */
    public QueryBuilder() {
        super();
    }

    /**
     * The time after which the matching trails must have been recorded.
     *
     * @param timestamp the time after which the matching trails must have been
     * recorded
     *
     * @return the query builder with an updated 'after' filter
     */
    public QueryBuilder after(final Instant timestamp) {
        after = timestamp;
        return this;
    }

    /**
     * The time before which the matching trails must have been recorded.
     *
     * @param timestamp the time before which the matching trails must have been
     * recorded
     *
     * @return the query builder with an updated 'before' filter
     */
    public QueryBuilder before(final Instant timestamp) {
        before = timestamp;
        return this;
    }

    /**
     * The difficulty level of the trails to be returned.
     *
     * @param level the difficulty level of the trails to be returned
     *
     * @return the query builder with an updated 'difficulty level' filter
     */
    public QueryBuilder level(final Level level) {
        this.level = level;
        return this;
    }

    /**
     * The country crossed by the trails to be returned.
     *
     * @param country an ISO 3166-1 two-letter country codes representing the
     * country crossed by the trails to be returned
     *
     * @return the query builder with an updated 'country' filter
     */
    public QueryBuilder country(final String country) {
        this.country = country;
        return this;
    }

    /**
     * The minimum rating of the trails to be returned.
     *
     * @param minimumRating the minimum rating of the trails to be returned
     *
     * @return the query builder with an updated 'rating' filter
     */
    public QueryBuilder minimumRating(final int minimumRating) {
        this.rating = minimumRating;
        return this;
    }

    /**
     * The type of activity performed on the trails to be returned.
     *
     * @param activity the type of activity performed on the trails to be
     * returned
     *
     * @return the query builder with an updated 'activity' filter
     */
    public QueryBuilder activity(final Activity activity) {
        this.activity = activity;
        return this;
    }

    /**
     * Builds an immutable instance of the {@code Query} interface.
     *
     * @return an immutable instance of a query
     */
    public Query build() {
        return new QueryImpl(after, before, rating, activity, country, level);
    }

    private static final class QueryImpl implements Query {

        private final Instant after;
        private final Instant before;
        private final int rating;
        private final Activity activity;
        private final String country;
        private final Level level;

        QueryImpl(final Instant after, final Instant before, final int rating,
            final Activity activity, final String country, final Level level) {
            super();
            this.after = after;
            this.before = before;
            this.rating = rating;
            this.activity = activity;
            this.country = country;
            this.level = level;
        }

        @Override
        public String getCountry() {
            return country;
        }

        @Override
        public Activity getActivity() {
            return activity;
        }

        @Override
        public int getMinimalRating() {
            return rating;
        }

        @Override
        public Level getLevel() {
            return level;
        }

        @Override
        public Instant getAfterDate() {
            return after;
        }

        @Override
        public Instant getBeforeDate() {
            return before;
        }

        @Override
        public String toString() {
            return "Query{" + "after=" + after + ", before=" + before
                + ", rating=" + rating + ", activity=" + activity
                + ", country=" + country + ", level=" + level + '}';
        }
    }
}
