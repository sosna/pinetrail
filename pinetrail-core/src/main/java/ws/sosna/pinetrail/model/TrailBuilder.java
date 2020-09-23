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
import ws.sosna.pinetrail.analysis.CountryGuesser;
import ws.sosna.pinetrail.analysis.ElevationFixer;
import ws.sosna.pinetrail.analysis.PointsAugmenter;
import ws.sosna.pinetrail.analysis.StatisticsProvider;
import ws.sosna.pinetrail.analysis.TrailStatistics;
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
public final class TrailBuilder {

  private Set<GpsRecord> records;
  private Set<Waypoint> points;
  private Set<String> countries;
  private static final Logger LOGGER = LoggerFactory.getLogger(TrailBuilder.class);

  /**
   * Instantiates a new TrailBuilder, with all mandatory fields.
   *
   * <p>The collection of {@code waypoints} cannot be null or empty.
   *
   * @param records the ordered list of points describing the trail
   */
  public TrailBuilder(final Set<GpsRecord> records) {
    super();
    this.records = records;
    this.points = new LinkedHashSet<>();
  }

  /**
   * Builds a new immutable instance of the {@code Trail} interface.
   *
   * @return a new immutable instance of the Trail interface
   */
  public Trail build() {
    final boolean skip =
        Boolean.parseBoolean(
            Preferences.userRoot()
                .node("ws.sosna.pinetrail.model.Trail")
                .get("keepOutliers", "false"));
    final int iterations =
        Integer.parseInt(
            Preferences.userRoot()
                .node("ws.sosna.pinetrail.model.Trail")
                .get("cleanupPasses", "3"));
    final boolean removeIdle =
        !(Boolean.parseBoolean(
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
    final SortedSet<GpsRecord> sortedPoints =
        records.stream().sorted().collect(Collectors.toCollection(TreeSet::new));

    final long start = System.currentTimeMillis();
    final SortedSet<GpsRecord> elePoints =
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
      final SortedSet<GpsRecord> activeRecords =
          augmentedPoints.stream()
              .map(Waypoint::getRecord)
              .collect(Collectors.toCollection(TreeSet::new));
      augmentedPoints.clear();
      augmentedPoints.addAll(PointsAugmenter.INSTANCE.apply(activeRecords));
    }

    final long augmentTs = System.currentTimeMillis();
    final TrailStatistics trailStatistics = StatisticsProvider.INSTANCE.apply(augmentedPoints);

    final long statsTs = System.currentTimeMillis();
    final SortedSet<GpsRecord> records =
        augmentedPoints.stream()
            .map(Waypoint::getRecord)
            .collect(Collectors.toCollection(TreeSet::new));
    augmentTrail(records);
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

  private void augmentTrail(final SortedSet<GpsRecord> points) {
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

  private static final class TrailImpl implements Trail {

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
      return Objects.equals(points, trail.points)
          && Objects.equals(countries, trail.countries)
          && Objects.equals(stats, trail.stats);
    }

    @Override
    public int hashCode() {
      return hashCode;
    }

    @Override
    public String toString() {
      return "Trail{points=" + points + ", countries=" + countries + ", statistics=" + stats + '}';
    }
  }
}
