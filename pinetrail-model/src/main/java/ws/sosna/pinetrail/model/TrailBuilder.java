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
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.sosna.pinetrail.utils.logging.Actions;
import ws.sosna.pinetrail.utils.logging.Markers;
import ws.sosna.pinetrail.utils.logging.StatusCodes;

/**
 * Builds immutable instances of the {@code Trail} interface.
 *
 * <p>A new instance can be built as follows:<br>
 * <code>
 * Trail trail = new TrailBuilder(name, points).build();
 * </code>
 *
 * <p>Optional parameters can be set using the appropriate method. For example, to set the
 * description:<br>
 * <code>
 * Trail trail = new TrailBuilder(name, points).description(desc).build();
 * </code>
 *
 * <p>Instances are immutable. In case it is needed to update the values of some fields, the of()
 * method can be used:<br>
 * <code>Trail newTrail = TrailBuilder.of(trail).name(newName).build();</code>
 *
 * @see Trail
 * @author Xavier Sosnovsky
 */
public final class TrailBuilder implements Builder<Trail> {

  private Set<Waypoint> points;
  private Set<String> countries;
  private static final Logger LOGGER = LoggerFactory.getLogger(TrailBuilder.class);

  /**
   * Instantiates a new TrailBuilder, with all mandatory fields.
   *
   * <p>The collection of {@code waypoints} cannot be null or empty.
   *
   * @param points the ordered list of points describing the trail
   */
  public TrailBuilder(final Set<Waypoint> points) {
    super();
    this.points = points;
  }

  /**
   * Sets the ordered list of points describing the trail.
   *
   * <p>The list cannot be null and must contain at least one waypoint. The points will be sorted by
   * time, in ascending order.
   *
   * @param points the ordered list of points describing the trail
   * @return the builder, with the updated collection of points
   */
  public TrailBuilder points(final Set<Waypoint> points) {
    this.points = points;
    return this;
  }

  /**
   * Sets the list of countries crossed by the trail.
   *
   * <p>Each item in the set represents an ISO 3166-1 two-letter country codes. In case no country
   * has been assigned, the method returns an empty collection.
   *
   * @param countries the list of countries crossed by the trail
   * @return the builder, with an updated country list
   */
  public TrailBuilder countries(final Set<String> countries) {
    this.countries = countries;
    return this;
  }

  /**
   * Instantiate a new TrailBuilder out of an existing {@code Trail} instance.
   *
   * <p>All objects are immutable and, therefore cannot be updated. This method is a convenience
   * method that creates a new builder with the same values as the supplied {@code Trail}. The
   * setters methods of the builder can then be used to update some fields before calling the {@code
   * build} method.
   *
   * @param trail the trail from which the values will be copied
   * @return a new TrailBuilder
   */
  public static TrailBuilder of(final Trail trail) {
    return new TrailBuilder(trail.getWaypoints()).countries(trail.getCountries());
  }

  /**
   * Builds a new immutable instance of the {@code Trail} interface.
   *
   * @return a new immutable instance of the Trail interface
   */
  @Override
  public Trail build() {
    final boolean skip =
        Boolean.valueOf(
            Preferences.userRoot()
                .node("ws.sosna.pinetrail.model.Trail")
                .get("keepOutliers", "false"));
    final int iterations =
        Integer.valueOf(
            Preferences.userRoot()
                .node("ws.sosna.pinetrail.model.Trail")
                .get("cleanupPasses", "3"));
    final boolean removeIdle =
        !(Boolean.valueOf(
            Preferences.userRoot()
                .node("ws.sosna.pinetrail.model.Trail")
                .get("keepIdlePoints", "false")));
    Trail obj;
    int i = 0;
    do {
      obj = createTrail(i, i == 0 && removeIdle);
      i++;
    } while (!skip && hasOutliers(obj) && i < iterations);
    validateTrail(obj);
    return obj;
  }

  private Trail createTrail(final int iteration, final boolean removeIdle) {
    final SortedSet<Waypoint> sortedPoints =
        null == points ? Collections.emptySortedSet() : new TreeSet(points);

    final long start = System.currentTimeMillis();
    final SortedSet<Waypoint> elePoints =
        0 == iteration ? ElevationFixer.INSTANCE.apply(sortedPoints) : sortedPoints;

    final long eleTs = System.currentTimeMillis();
    final SortedSet<Waypoint> augmentedPoints = PointsAugmenter.INSTANCE.apply(elePoints);

    if (removeIdle) {
      final SortedSet<Waypoint> activePoints =
          augmentedPoints.stream()
              .filter(Waypoint::isActive)
              .collect(Collectors.toCollection(TreeSet::new));
      LOGGER.info(
          Markers.MODEL.getMarker(),
          "{} | {} | Removed {} idle" + " points from trail",
          Actions.ANALYSE,
          StatusCodes.OK.getCode(),
          (augmentedPoints.size() - activePoints.size()));
      augmentedPoints.clear();
      augmentedPoints.addAll(PointsAugmenter.INSTANCE.apply(activePoints));
    }

    final long augmentTs = System.currentTimeMillis();
    final TrailStatistics trailStatistics = StatisticsProvider.INSTANCE.apply(augmentedPoints);

    final long statsTs = System.currentTimeMillis();
    augmentTrail(trailStatistics, augmentedPoints);
    final long guessTs = System.currentTimeMillis();

    LOGGER.info(
        Markers.PERFORMANCE.getMarker(),
        "{} | {} | Performed trail"
            + " analysis in {} ms (Elevation data: {} - Augment points: {} - "
            + "Compute stats: {} - Reverse geocoding: {})",
        Actions.ANALYSE,
        StatusCodes.OK.getCode(),
        guessTs - start,
        eleTs - start,
        augmentTs - eleTs,
        statsTs - augmentTs,
        guessTs - statsTs);
    return new TrailImpl(augmentedPoints, countries, trailStatistics);
  }

  private void validateTrail(final Trail trail) {
    final Set<ConstraintViolation<Trail>> violations =
        ValidationService.INSTANCE.getValidator().validate(trail);
    if (violations.isEmpty()) {
      LOGGER.debug(
          Markers.MODEL.getMarker(),
          "{} | {} | Built {}",
          Actions.CREATE,
          StatusCodes.OK.getCode(),
          trail);
    } else {
      final StringBuilder msg = new StringBuilder();
      for (final ConstraintViolation<Trail> violation : violations) {
        msg.append(violation.getMessage());
        msg.append(" ");
      }
      final String errorMsg = msg.toString();
      LOGGER.warn(
          Markers.MODEL.getMarker(),
          "{} | {} | Error" + " validating trail:  {}",
          Actions.CREATE,
          StatusCodes.SYNTAX_ERROR.getCode(),
          errorMsg);
      throw new ValidationException(errorMsg);
    }
  }

  private void augmentTrail(final TrailStatistics stats, final SortedSet<Waypoint> points) {
    if (null == countries || countries.isEmpty()) {
      countries = CountryGuesser.INSTANCE.apply(points);
    }
  }

  private boolean hasOutliers(final Trail trail) {
    final Set<Waypoint> outliers = new LinkedHashSet<>();
    if (null != trail.getStatistics()) {
      outliers.addAll(trail.getStatistics().getSpeedSummary().getOutliers());
      outliers.addAll(trail.getStatistics().getGradeSummary().getOutliers());
      if (outliers.size() > 0) {
        final List<Waypoint> cleanPoints = new ArrayList<>(trail.getWaypoints());
        final Set<Waypoint> remove = new LinkedHashSet<>();
        for (final Waypoint outlier : outliers) {
          final int index = cleanPoints.indexOf(outlier);
          if (0 < index) {
            remove.add(cleanPoints.get(index - 1));
          }
          if (cleanPoints.size() - 1 > index) {
            remove.add(cleanPoints.get(index + 1));
          }
        }
        outliers.addAll(remove);
        cleanPoints.removeAll(outliers);
        LOGGER.info(
            Markers.MODEL.getMarker(),
            "{} | {} | {}",
            Actions.ANALYSE,
            StatusCodes.NOT_ACCEPTABLE.getCode(),
            outliers.size()
                + " points (outliers and their neighbours) "
                + "have been removed. The analysis will be performed "
                + "again.");
        points = new LinkedHashSet<>(cleanPoints);
      }
    }
    return outliers.size() > 0;
  }

  private static final class TrailImpl implements Trail, Serializable {

    private static final long serialVersionUID = -5323040838868491171L;
    private final SortedSet<Waypoint> points;
    private final Set<String> countries;
    private final transient int hashCode;
    private final TrailStatistics stats;

    TrailImpl(
        final SortedSet<Waypoint> points,
        final Set<String> countries,
        final TrailStatistics stats) {
      super();
      this.points = Collections.unmodifiableSortedSet(points);
      this.countries = Collections.unmodifiableSet(new LinkedHashSet<>(countries));
      this.stats = stats;
      hashCode = Objects.hash(this.points, this.countries);
    }

    @Override
    public SortedSet<Waypoint> getWaypoints() {
      return points;
    }

    @Override
    public Set<String> getCountries() {
      return countries;
    }

    @Override
    public TrailStatistics getStatistics() {
      return stats;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      TrailImpl trail = (TrailImpl) o;
      return Objects.equals(points, trail.points) &&
          Objects.equals(countries, trail.countries) &&
          Objects.equals(stats, trail.stats);
    }

    @Override
    public int hashCode() {
      return hashCode;
    }

    @Override
    public String toString() {
      return "Trail{points=" + points + ", countries=" + countries + ", statistics=" + stats + '}';
    }

    private Object writeReplace() {
      return new SerializationProxy(this);
    }

    private void readObject(final ObjectInputStream stream) throws InvalidObjectException {
      throw new InvalidObjectException("Proxy required");
    }

    private static final class SerializationProxy implements Serializable {

      private static final long serialVersionUID = -5323040838868491171L;
      private final SortedSet<Waypoint> points;
      private final Set<String> countries;
      private final TrailStatistics stats;

      SerializationProxy(final Trail trail) {
        super();
        points = trail.getWaypoints();
        countries = trail.getCountries();
        stats = trail.getStatistics();
      }

      private Object readResolve() {
        return new TrailImpl(points, countries, stats);
      }
    }
  }
}
