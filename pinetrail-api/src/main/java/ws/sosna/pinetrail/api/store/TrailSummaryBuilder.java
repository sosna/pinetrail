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

import java.text.DecimalFormat;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import ws.sosna.pinetrail.model.Activity;
import ws.sosna.pinetrail.model.Trail;

/**
 * Builds immutable instances of the {@code TrailSummary} interface.
 *
 * @author Xavier Sosnovsky
 */
public final class TrailSummaryBuilder {

    private final Trail trail;

    /**
     * Instantiates a new TrailSummaryBuilder.
     *
     * @param trail the trail from which the summary will be extracted.
     */
    public TrailSummaryBuilder(final Trail trail) {
        super();
        this.trail = trail;
    }

    /**
     * Builds an immutable instance of the {@code TrailSummary} interface.
     *
     * @return an immutable instance of a trail summary
     */
    public TrailSummary build() {
        return new TrailSummaryImpl(trail);
    }

    private static final class TrailSummaryImpl implements TrailSummary {

        private final UUID trailId;
        private final String name;
        private final Activity activity;
        private final Set<String> countries;
        private final Instant date;
        private final double distance;
        private final double elevationDifference;
        private final int difficultyRating;
        private final double movingSpeed;
        private final int rating;
        private final transient int hash;

        public TrailSummaryImpl(final Trail trail) {
            super();
            this.trailId = trail.getId();
            this.name = trail.getName();
            this.activity = trail.getActivity();
            this.countries = trail.getCountries();
            this.date = trail.getWaypoints().first().getTime();
            this.distance
                = trail.getStatistics().getDistanceSummary().getActive().
                getSum();
            this.elevationDifference = trail.getStatistics().
                getElevationSummary().getActive().getMax() - trail.
                getStatistics().
                getElevationSummary().getActive().getMin();
            this.difficultyRating = trail.getDifficultyRating();
            this.movingSpeed
                = trail.getStatistics().getSpeedSummary().getActive().getMean();
            this.rating = trail.getRating();
            this.hash = Objects.hash(trailId, name, activity, countries, date,
                distance, elevationDifference, difficultyRating, movingSpeed,
                rating);
        }

        @Override
        public UUID getTrailId() {
            return trailId;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Integer getDifficultyRating() {
            return difficultyRating;
        }

        @Override
        public Activity getActivity() {
            return activity;
        }

        @Override
        public Set<String> getCountries() {
            return countries;
        }

        @Override
        public int getRating() {
            return rating;
        }

        @Override
        public Instant getDate() {
            return date;
        }

        @Override
        public Double getDistance() {
            return distance;
        }

        @Override
        public Double getElevationDifference() {
            return elevationDifference;
        }

        @Override
        public Double getMovingSpeed() {
            return movingSpeed;
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof TrailSummary)) {
                return false;
            }
            final TrailSummary other = (TrailSummary) obj;
            return Objects.equals(this.trailId, other.getTrailId())
                && Objects.equals(this.name, other.getName())
                && this.activity == other.getActivity()
                && Objects.equals(this.countries, other.getCountries())
                && Objects.equals(this.date, other.getDate())
                && Double.doubleToLongBits(this.distance) == Double.
                doubleToLongBits(other.getDistance())
                && Double.doubleToLongBits(this.elevationDifference) == Double.
                doubleToLongBits(other.getElevationDifference())
                && this.difficultyRating == other.getDifficultyRating()
                && Double.doubleToLongBits(this.movingSpeed) == Double.
                doubleToLongBits(other.getMovingSpeed())
                && this.rating == other.getRating();
        }

        @Override
        public String toString() {
            return "TrailSummary{" + "trailId=" + trailId + ", name=" + name
                + ", date=" + date + ", activity=" + activity + ", countries="
                + countries + ", distance=" + new DecimalFormat("#.0").format(
                    distance) + "m, elevationDifference="
                + elevationDifference + ", difficulty rating="
                + difficultyRating + ", movingSpeed="
                + new DecimalFormat("#.0").format(movingSpeed) + "km/h, rating="
                + rating + '}';
        }
    }
}
