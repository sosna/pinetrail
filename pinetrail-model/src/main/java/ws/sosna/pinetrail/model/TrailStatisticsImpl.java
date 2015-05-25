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
package ws.sosna.pinetrail.model;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Objects;

/**
 * Base implementation of the TrailStatistics interface.
 *
 * @author Xavier Sosnovsky
 */
final class TrailStatisticsImpl implements TrailStatistics {

    private static final long serialVersionUID = -2633126430781017968L;
    private final Statistics time;
    private final Statistics dist;
    private final Statistics ele;
    private final Statistics eleDiff;
    private final Statistics speed;
    private final Statistics grade;
    private final transient int hash;

    TrailStatisticsImpl(final Statistics time, final Statistics dist,
            final Statistics ele, final Statistics eleDiff,
            final Statistics speed, final Statistics grade) {
        this.time = time;
        this.dist = dist;
        this.eleDiff = eleDiff;
        this.speed = speed;
        this.grade = grade;
        this.ele = ele;
        this.hash = Objects.hash(time, dist, eleDiff, speed, grade, ele);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Statistics getTimeDifferenceSummary() {
        return time;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Statistics getDistanceSummary() {
        return dist;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Statistics getElevationDifferenceSummary() {
        return eleDiff;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Statistics getSpeedSummary() {
        return speed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Statistics getGradeSummary() {
        return grade;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Statistics getElevationSummary() {
        return ele;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "TrailStatistics{" + "timeDiff=" + time.toString()
            + ", distance=" + dist.toString() + ", elevation=" + ele.toString()
            + ", elevationDiff=" + eleDiff.toString() + ", speed="
            + speed.toString() + ", grade=" + grade.toString() + '}';
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
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TrailStatisticsImpl other = (TrailStatisticsImpl) obj;
        return Objects.equals(this.time, other.getTimeDifferenceSummary())
            && Objects.equals(this.dist, other.getDistanceSummary())
            && Objects.equals(this.eleDiff,
                other.getElevationDifferenceSummary())
            && Objects.equals(this.speed, other.getSpeedSummary())
            && Objects.equals(this.grade, other.getGradeSummary())
            && Objects.equals(this.ele, other.getElevationSummary());
    }

    private Object writeReplace() {
        return new SerializationProxy(this);
    }

    private void readObject(final ObjectInputStream stream)
        throws InvalidObjectException {
        throw new InvalidObjectException("Proxy required");
    }

    private static final class SerializationProxy implements Serializable {
        private static final long serialVersionUID = -2633126430781017968L;
        private final Statistics time;
        private final Statistics dist;
        private final Statistics eleDiff;
        private final Statistics speed;
        private final Statistics grade;
        private final Statistics ele;

        SerializationProxy(final TrailStatistics statistics) {
            super();
            time = statistics.getTimeDifferenceSummary();
            dist = statistics.getDistanceSummary();
            eleDiff = statistics.getElevationDifferenceSummary();
            speed = statistics.getSpeedSummary();
            grade = statistics.getGradeSummary();
            ele = statistics.getElevationSummary();
        }

        private Object readResolve() {
            return new TrailStatisticsImpl(time, dist, ele, eleDiff, speed,
                grade);
        }
    }
}
