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
package ws.sosna.pinetrail.api.io;

/**
 * Builds immutable instances of the {@code WriterSettings} interface.
 *
 * @author Xavier Sosnovsky
 */
public final class WriterSettingsBuilder {

    private boolean prettyPrinting;
    private boolean overwriteIfExists;
    private boolean writeOutliers;
    private boolean writeIdlePoints;
    private boolean writeRoute;

    /**
     * Instantiates a new WriterSettingsBuilder.
     */
    public WriterSettingsBuilder() {
        super();
        prettyPrinting = false;
        overwriteIfExists = true;
        writeOutliers = false;
        writeIdlePoints = false;
        writeRoute = false;
    }

    /**
     * Returns the builder, with the overwriteIfExists flag set.
     *
     * @param flag whether to allow overwriting existing files
     *
     * @return the builder with the updated overwriteIfExists flag
     */
    public WriterSettingsBuilder overwriteIfExists(final boolean flag) {
        overwriteIfExists = flag;
        return this;
    }

    /**
     * Returns the builder, with the prettyPrinting flag set.
     *
     * @param flag whether the output should be nicely formatted
     *
     * @return the builder with the updated prettyPrinting flag
     */
    public WriterSettingsBuilder prettyPrinting(final boolean flag) {
        prettyPrinting = flag;
        return this;
    }

    /**
     * Returns the builder, with the writeOutliers flag set.
     *
     * @param flag whether outliers should be written to the output file
     *
     * @return the builder with the updated writeOutliers flag
     */
    public WriterSettingsBuilder writeOutliers(final boolean flag) {
        writeOutliers = flag;
        return this;
    }

    /**
     * Returns the builder, with the writeIdlePoints flag set.
     *
     * @param flag whether inactive points should be written to the output file
     *
     * @return the builder with the updated writeIdlePoints flag
     */
    public WriterSettingsBuilder writeIdlePoints(final boolean flag) {
        writeIdlePoints = flag;
        return this;
    }

    /**
     * Returns the builder, with the writeRoute flag set.
     *
     * @param flag whether the trail should be written as an ordered collection
     * of points with time information or as a route, i.e. an ordered
     * collection of points leading to a destination.
     *
     * @return the builder with the updated writeRoute flag
     */
    public WriterSettingsBuilder writeRoute(final boolean flag) {
        writeRoute = flag;
        return this;
    }

    /**
     * Builds an immutable implementation of the WriterSettings interface.
     *
     * @return a WriterSettings instance.
     */
    public WriterSettings build() {
        return new WriterSettingsImpl(prettyPrinting, overwriteIfExists,
            writeOutliers, writeIdlePoints, writeRoute);
    }

    private static final class WriterSettingsImpl implements WriterSettings {

        private final boolean prettyPrinting;
        private final boolean overwriteIfExists;
        private final boolean writeOutliers;
        private final boolean writeIdlePoints;
        private final boolean writeRoute;

        WriterSettingsImpl(final boolean pretty, final boolean overwrite,
            final boolean withOutliers, final boolean withInactive,
            final boolean asRoute) {
            super();
            prettyPrinting = pretty;
            overwriteIfExists = overwrite;
            writeIdlePoints = withInactive;
            writeOutliers = withOutliers;
            writeRoute = asRoute;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean overwriteIfExists() {
            return overwriteIfExists;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean prettyPrinting() {
            return prettyPrinting;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean writeOutliers() {
            return writeOutliers;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean writeIdlePoints() {
            return writeIdlePoints;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean writeRoute() {
            return writeRoute;
        }
    }
}
