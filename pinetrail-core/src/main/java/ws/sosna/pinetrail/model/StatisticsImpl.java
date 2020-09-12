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
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

/**
 * Base implementation of the Statistics interface.
 *
 * @author Xavier Sosnovsky
 */
final class StatisticsImpl implements Statistics {

  private static final long serialVersionUID = 3272647220016375249L;
  private final SummaryStatistics all;
  private final SummaryStatistics active;
  private final SummaryStatistics up;
  private final SummaryStatistics down;
  private final SummaryStatistics flat;
  private final Set<Waypoint> outliers;
  private final transient int hash;

  StatisticsImpl(
      final SummaryStatistics all,
      final SummaryStatistics active,
      final SummaryStatistics up,
      final SummaryStatistics down,
      final SummaryStatistics flat,
      final Set<Waypoint> outliers) {
    this.all = all;
    this.active = active;
    this.up = up;
    this.down = down;
    this.flat = flat;
    this.outliers = Collections.unmodifiableSet(new LinkedHashSet<>(outliers));
    this.hash = Objects.hash(all, active, up, down, flat, outliers);
  }

  /** {@inheritDoc} */
  @Override
  public SummaryStatistics getAll() {
    return all;
  }

  /** {@inheritDoc} */
  @Override
  public SummaryStatistics getActive() {
    return active;
  }

  /** {@inheritDoc} */
  @Override
  public SummaryStatistics getActiveUp() {
    return up;
  }

  /** {@inheritDoc} */
  @Override
  public SummaryStatistics getActiveDown() {
    return down;
  }

  /** {@inheritDoc} */
  @Override
  public SummaryStatistics getActiveFlat() {
    return flat;
  }

  /** {@inheritDoc} */
  @Override
  public Set<Waypoint> getOutliers() {
    return outliers;
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
    final StatisticsImpl other = (StatisticsImpl) obj;
    return Objects.equals(this.outliers, other.outliers)
        && Objects.equals(this.flat, other.flat)
        && Objects.equals(this.down, other.down)
        && Objects.equals(this.up, other.up)
        && Objects.equals(this.active, other.active)
        && Objects.equals(this.all, other.all);
  }

  @Override
  public String toString() {
    return "Statistics{"
        + "all="
        + all
        + ", active="
        + active
        + ", up="
        + up
        + ", down="
        + down
        + ", flat="
        + flat
        + ", outliers="
        + outliers
        + '}';
  }

  private Object writeReplace() {
    return new SerializationProxy(this);
  }

  private void readObject(final ObjectInputStream stream) throws InvalidObjectException {
    throw new InvalidObjectException("Proxy required");
  }

  private static final class SerializationProxy implements Serializable {
    private static final long serialVersionUID = 3272647220016375249L;
    private final SummaryStatistics all;
    private final SummaryStatistics active;
    private final SummaryStatistics up;
    private final SummaryStatistics down;
    private final SummaryStatistics flat;
    private final Set<Waypoint> outliers;

    SerializationProxy(final Statistics statistics) {
      super();
      all = statistics.getAll();
      active = statistics.getActive();
      up = statistics.getActiveUp();
      down = statistics.getActiveDown();
      flat = statistics.getActiveFlat();
      outliers = statistics.getOutliers();
    }

    private Object readResolve() {
      return new StatisticsImpl(all, active, up, down, flat, outliers);
    }
  }
}
