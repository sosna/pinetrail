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
import java.util.UUID;
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
public final class TrailBuilder extends DescribableBuilder<TrailBuilder> implements Builder<Trail> {

  private Set<Waypoint> points;
  private Set<String> countries;
  private Integer difficultyRating;
  private Activity activity;
  private int rating;
  private UUID id;
  private static final Logger LOGGER = LoggerFactory.getLogger(TrailBuilder.class);

  /**
   * Instantiates a new TrailBuilder, with all mandatory fields.
   *
   * <p>Neither the {@code name} nor the collection of {@code waypoints} can be null or empty.
   *
   * @param name a short title describing the trail
   * @param points the ordered list of points describing the trail
   */
  public TrailBuilder(final String name, final Set<Waypoint> points) {
    super(name, null, null);
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
   * Sets the difficulty rating for the trail.
   *
   * @param difficultyRating the difficulty rating of the trail
   * @return the builder, with the updated difficulty rating
   */
  public TrailBuilder difficultyRating(final Integer difficultyRating) {
    this.difficultyRating = difficultyRating;
    return this;
  }

  /**
   * Sets the type of outdoor activity for the trail.
   *
   * <p>The value represents the type of activity performed when following the trail, such as
   * hiking, jogging or biking.
   *
   * <p>The value can be null.
   *
   * @param activity the type of outdoor activity for the trail
   * @return the builder, with an updated activity
   */
  public TrailBuilder activity(final Activity activity) {
    this.activity = activity;
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
   * Sets the star rating for the trail.
   *
   * <p>The value is a number from 0 (the default) to 5 (the best ranking).
   *
   * @param rating the star rating for the trail
   * @return the builder, with an updated star rating
   */
  public TrailBuilder rating(final int rating) {
    this.rating = rating;
    return this;
  }

  /**
   * Sets the trail id.
   *
   * <p>Will be automatically generated if empty.
   *
   * @param id the trail id
   * @return the builder, with an updated id
   */
  public TrailBuilder id(final UUID id) {
    this.id = id;
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
    return new TrailBuilder(trail.getName(), trail.getWaypoints())
        .description(trail.getDescription())
        .links(trail.getLinks())
        .activity(trail.getActivity())
        .difficultyRating(trail.getDifficultyRating())
        .countries(trail.getCountries())
        .rating(trail.getRating())
        .id(trail.getId());
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
    if (null == id) {
      id = UUID.randomUUID();
    }
    return new TrailImpl(
        getName(),
        getDescription(),
        getLinks(),
        augmentedPoints,
        countries,
        rating,
        difficultyRating,
        activity,
        trailStatistics,
        id);
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
    if (null == activity && null != stats) {
      activity =
          ActivityGuesser.INSTANCE.apply(stats.getSpeedSummary(), stats.getDistanceSummary());
    }
    if (null == difficultyRating && null != stats) {
      final Level userLevel =
          Level.valueOf(
              Preferences.userRoot()
                  .node("ws.sosna.pinetrail.UserSettings")
                  .get("level", "INTERMEDIATE"));
      LOGGER.info(
          Markers.MODEL.getMarker(),
          "{} | {} | {}",
          Actions.ANALYSE,
          StatusCodes.OK.getCode(),
          "User level set to " + userLevel);
      difficultyRating =
          activity.getDifficultyRating(
              userLevel,
              stats.getDistanceSummary(),
              stats.getElevationDifferenceSummary(),
              points.first().getTime(),
              points.last().getTime());
    }
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

  private static final class TrailImpl extends DescribableImpl implements Trail {

    private static final long serialVersionUID = -5323040838868491171L;
    private final SortedSet<Waypoint> points;
    private final Set<String> countries;
    private final int rating;
    private final transient int hashCode;
    private final Integer difficultyRating;
    private final Activity activity;
    private final TrailStatistics stats;
    private final UUID id;

    TrailImpl(
        final String name,
        final String description,
        final Set<Link> links,
        final SortedSet<Waypoint> points,
        final Set<String> countries,
        final int rating,
        final Integer difficultyRating,
        final Activity activity,
        final TrailStatistics stats,
        final UUID id) {
      super(name, description, links);
      this.points = Collections.unmodifiableSortedSet(points);
      this.difficultyRating = difficultyRating;
      this.activity = activity;
      this.countries = Collections.unmodifiableSet(new LinkedHashSet<>(countries));
      this.rating = rating;
      this.stats = stats;
      this.id = id;
      hashCode =
          Objects.hash(
              super.hashCode(), this.points, difficultyRating, activity, countries, rating, id);
    }

    @Override
    public SortedSet<Waypoint> getWaypoints() {
      return points;
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
    public TrailStatistics getStatistics() {
      return stats;
    }

    @Override
    public UUID getId() {
      return id;
    }

    @SuppressWarnings("PMD.LawOfDemeter")
    @Override
    public boolean equals(final Object obj) {
      if (!super.equals(obj)) {
        return false;
      }
      final Trail other = (Trail) obj;
      return this.points.equals(other.getWaypoints())
          && difficultyRating.equals(other.getDifficultyRating())
          && rating == other.getRating()
          && activity == other.getActivity()
          && countries.equals(other.getCountries())
          && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
      return hashCode;
    }

    @Override
    public String toString() {
      return "Trail{id="
          + id.toString()
          + ", points="
          + points
          + ", name="
          + getName()
          + ", description="
          + getDescription()
          + ", links="
          + getLinks()
          + ", difficulty rating="
          + difficultyRating
          + ", activity="
          + activity
          + ", countries="
          + countries
          + ", rating="
          + rating
          + ", statistics="
          + stats
          + '}';
    }

    private Object writeReplace() {
      return new SerializationProxy(this);
    }

    private void readObject(final ObjectInputStream stream) throws InvalidObjectException {
      throw new InvalidObjectException("Proxy required");
    }

    private static final class SerializationProxy implements Serializable {

      private static final long serialVersionUID = -5323040838868491171L;
      private final String name;
      private final String description;
      private final Set<Link> links;
      private final SortedSet<Waypoint> points;
      private final Set<String> countries;
      private final int rating;
      private final Integer difficultyRating;
      private final Activity activity;
      private final TrailStatistics stats;
      private final UUID id;

      SerializationProxy(final Trail trail) {
        super();
        name = trail.getName();
        description = trail.getDescription();
        links = trail.getLinks();
        points = trail.getWaypoints();
        countries = trail.getCountries();
        rating = trail.getRating();
        difficultyRating = trail.getDifficultyRating();
        activity = trail.getActivity();
        stats = trail.getStatistics();
        id = trail.getId();
      }

      private Object readResolve() {
        return new TrailImpl(
            name,
            description,
            links,
            points,
            countries,
            rating,
            difficultyRating,
            activity,
            stats,
            id);
      }
    }
  }
}
