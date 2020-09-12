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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.prefs.Preferences;
import javax.validation.ValidationException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/** @author Xavier Sosnovsky */
public class TrailTest {

  private static Boolean keepIdlePoints;

  @BeforeClass
  public static void init() {
    keepIdlePoints =
        Boolean.valueOf(
            Preferences.userRoot()
                .node("ws.sosna.pinetrail.model.Trail")
                .get("keepIdlePoints", "false"));
    Preferences.userRoot().node("ws.sosna.pinetrail.model.Trail").put("keepIdlePoints", "true");
  }

  @AfterClass
  public static void cleanup() {
    Preferences.userRoot()
        .node("ws.sosna.pinetrail.model.Trail")
        .put("keepIdlePoints", keepIdlePoints.toString());
  }

  private Trail trail;

  @Before
  public void setup() {
    Preferences.userRoot()
        .node("ws.sosna.pinetrail.model.Trail")
        .put("crossBorder", Boolean.toString(false));
    final Waypoint point = newWaypoint(Instant.EPOCH, newCoordinates(8.789654, 40.6784356, 393.31));
    final Set<Waypoint> points = new LinkedHashSet<>();
    points.add(point);
    trail = newTrail(points);
  }

  @Test
  public void createInstance() {
    final Waypoint point = newWaypoint(Instant.EPOCH, newCoordinates(8.789654, 40.6784356, 393.31));
    final Set<Waypoint> points = new LinkedHashSet<>();
    points.add(point);
    final Trail trail = newTrail(points);
    assertNotNull(trail);
  }

  @Test
  public void getPoints() {
    final Waypoint point = newWaypoint(Instant.EPOCH, newCoordinates(8.789654, 40.6784356, 393.31));
    final Set<Waypoint> points = new LinkedHashSet<>();
    points.add(point);
    final Trail trail = newTrail(points);
    assertEquals(points, trail.getWaypoints());
  }

  @Test
  public void equalityPoints() {
    final Waypoint point1 =
        newWaypoint(Instant.EPOCH, newCoordinates(18.789654, 40.6784356, 0.0));
    final Set<Waypoint> points1 = new LinkedHashSet<>();
    points1.add(point1);
    final Waypoint point2 = newWaypoint(Instant.EPOCH, newCoordinates(8.789654, 40.6784356, 0.0));
    final Set<Waypoint> points2 = new LinkedHashSet<>();
    points2.add(point2);
    final Trail trail1 = newTrail(points1);
    final Trail trail2 = newTrail(points2);
    assertNotEquals(point1, point2);
    assertNotEquals(trail1, trail2);
  }

  @Test
  public void hashCodeNOK() {
    final Waypoint point1 =
        newWaypoint(Instant.EPOCH, newCoordinates(8.789654, 40.6784356, 0.0));
    final Set<Waypoint> points1 = new LinkedHashSet<>();
    points1.add(point1);
    final Waypoint point2 = newWaypoint(Instant.EPOCH, newCoordinates(18.789654, 40.6784356, 0.0));
    final Set<Waypoint> points2 = new LinkedHashSet<>();
    points2.add(point2);
    final Trail trail1 = newTrail(points1);
    final Trail trail2 = newTrail(points2);
    assertNotEquals(trail1.hashCode(), trail2.hashCode());
  }

  @Test
  public void hashCodeOK() {
    final Trail trail1 = TrailBuilder.of(trail).build();
    final Trail trail2 = TrailBuilder.of(trail).build();
    assertEquals(trail1.hashCode(), trail2.hashCode());
  }

  @Test
  public void toStringOutput() {
    final Coordinates coordinates = newCoordinates(8.789654, 40.6784356, 393.31);
    final Waypoint point = newWaypoint(Instant.EPOCH, coordinates);
    final Set<Waypoint> points = new LinkedHashSet<>();
    points.add(point);
    final Set<String> countries = new LinkedHashSet<>();
    countries.add("DE");
    final Trail trail = newTrail(points, countries);
    System.out.println(trail);
    assertEquals(
        "Trail{points=[Waypoint{time="
            + Instant.EPOCH
            + ", coordinates="
            + coordinates.toString()
            + ", distance=0.0, elevationDifference=0.0, "
            + "grade=0.0, speed=0.0, isActive=false, timeDifference=0}], "
            + "countries=[DE],"
            + " statistics="
            + trail.getStatistics()
            + '}',
        trail.toString());
  }

  @Test(expected = ValidationException.class)
  public void valPointsNull() {
    newTrail(null);
  }

  @Test(expected = ValidationException.class)
  public void valPointsEmpty() {
    newTrail(new LinkedHashSet<>());
  }

  @Test
  public void pointsImmutable() {
    final Waypoint point = newWaypoint(Instant.EPOCH, newCoordinates(8.789654, 40.6784356, 393.31));
    final Set<Waypoint> points = new LinkedHashSet<>();
    points.add(point);
    final Trail trail = newTrail(points);
    final Waypoint point2 =
        newWaypoint(
            Instant.parse("2014-12-23T14:56:32Z"), newCoordinates(8.789654, 40.6784356, 137.8));
    points.add(point2);
    assertEquals(1, trail.getWaypoints().size());
    assertFalse(trail.getWaypoints().contains(point2));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void pointsImmutableFromGetter() {
    final Waypoint point = newWaypoint(Instant.EPOCH, newCoordinates(8.789654, 40.6784356, 393.31));
    final Set<Waypoint> points = new LinkedHashSet<>();
    points.add(point);
    final Trail trail = newTrail(points);
    trail.getWaypoints().clear();
  }

  @Test
  public void pointsSorted() {
    final Waypoint point1 =
        newWaypoint(Instant.EPOCH, newCoordinates(8.789654, 40.6784356, 393.31));
    final Waypoint point2 = newWaypoint(Instant.MIN, newCoordinates(8.789654, 40.6784356, 393.31));
    final Set<Waypoint> points = new LinkedHashSet<>();
    points.add(point1);
    points.add(point2);
    final Trail trail = newTrail(points);
    assertEquals(2, trail.getWaypoints().size());
    int count = 0;
    for (final Waypoint point : trail.getWaypoints()) {
      assertSame(point.getTime(), (0 == count ? Instant.MIN : Instant.EPOCH));
      count++;
    }
  }

  @Test
  public void updatePoints() {
    final Waypoint point = newWaypoint(Instant.MIN, newCoordinates(8.789654, 40.6784356, 393.31));
    final Set<Waypoint> points = new LinkedHashSet<>();
    points.add(point);
    final Trail original = newTrail(points);
    final Waypoint point2 =
        newWaypoint(Instant.EPOCH, newCoordinates(8.789654, 40.6784356, 393.31));
    points.add(point2);
    final Trail copy = TrailBuilder.of(original).points(points).build();
    assertNotSame(original, copy);
    assertNotEquals(original, copy);
    assertEquals(points, copy.getWaypoints());
  }

  @Test
  public void getCountries() {
    final Waypoint point = newWaypoint(Instant.MIN, newCoordinates(8.789654, 40.6784356, 393.31));
    final Set<Waypoint> points = new LinkedHashSet<>();
    points.add(point);
    final Set<String> countries = new LinkedHashSet<>();
    countries.add("DE");
    final Trail trail = new TrailBuilder(points).countries(countries).build();
    assertEquals(countries, trail.getCountries());
  }

  @Test
  public void countriesImmutable() {
    final Waypoint point = newWaypoint(Instant.MIN, newCoordinates(8.789654, 40.6784356, 393.31));
    final Set<Waypoint> points = new LinkedHashSet<>();
    points.add(point);
    final Set<String> countries = new LinkedHashSet<>();
    countries.add("DE");
    final Trail trail = new TrailBuilder(points).countries(countries).build();
    countries.add("BE");
    assertEquals(1, trail.getCountries().size());
    assertTrue(trail.getCountries().contains("DE"));
  }

  @Test
  public void tryGuessingCountryIfEmpty() {
    final Waypoint point = newWaypoint(Instant.MIN, newCoordinates(8.789654, 40.6784356, 393.31));
    final Set<Waypoint> points = new LinkedHashSet<>();
    points.add(point);
    final Set<String> countries = new LinkedHashSet<>();
    final Trail trail = new TrailBuilder(points).countries(countries).build();
    // 1 if country could be guessed, 0 otherwise
    assertTrue(1 == trail.getCountries().size() || 0 == trail.getCountries().size());
  }

  @Test(expected = UnsupportedOperationException.class)
  public void countriesImmutableFromGetter() {
    final Waypoint point = newWaypoint(Instant.MIN, newCoordinates(8.789654, 40.6784356, 393.31));
    final Set<Waypoint> points = new LinkedHashSet<>();
    points.add(point);
    final Set<String> countries = new LinkedHashSet<>();
    countries.add("DE");
    final Trail trail = new TrailBuilder(points).countries(countries).build();
    trail.getCountries().clear();
  }

  @Test
  public void updateCountries() {
    final Waypoint point = newWaypoint(Instant.MIN, newCoordinates(8.789654, 40.6784356, 393.31));
    final Set<Waypoint> points = new LinkedHashSet<>();
    points.add(point);
    final Set<String> countries = new LinkedHashSet<>();
    countries.add("DE");
    final Trail original = new TrailBuilder(points).build();
    final Trail copy = TrailBuilder.of(original).countries(countries).build();
    assertNotEquals(original, copy);
    assertEquals(countries, copy.getCountries());
  }

  @Test
  public void countriesAffectHashcode() {
    final Waypoint point = newWaypoint(Instant.MIN, newCoordinates(8.789654, 40.6784356, 393.31));
    final Set<Waypoint> points = new LinkedHashSet<>();
    points.add(point);
    final Set<String> countries = new LinkedHashSet<>();
    countries.add("DE");
    final Trail original = new TrailBuilder(points).build();
    final Trail copy = new TrailBuilder(points).countries(countries).build();
    assertNotEquals(original.hashCode(), copy.hashCode());
  }

  @Test
  public void countriesAffectEquality() {
    final Waypoint point = newWaypoint(Instant.MIN, newCoordinates(8.789654, 40.6784356, 393.31));
    final Set<Waypoint> points = new LinkedHashSet<>();
    points.add(point);
    final Set<String> countries = new LinkedHashSet<>();
    countries.add("DE");
    final Trail original = new TrailBuilder(points).build();
    final Trail copy = new TrailBuilder(points).countries(countries).build();
    assertNotEquals(original, copy);
  }

  @Test
  public void statsComputed() {
    final Waypoint point = newWaypoint(Instant.MIN, newCoordinates(8.789654, 40.6784356, 393.31));
    final Set<Waypoint> points = new LinkedHashSet<>();
    points.add(point);
    final Trail trail = new TrailBuilder(points).build();
    assertNotNull(trail.getStatistics());
  }

  @Test
  public void serialize() throws IOException, ClassNotFoundException {
    final Waypoint point1 =
        newWaypoint(
            Instant.parse("2014-05-18T08:27:02Z"),
            newCoordinates(7.9631012119, 50.1183273643, 216.43));
    final Waypoint point2 =
        newWaypoint(
            Instant.parse("2014-05-18T08:27:09Z"),
            newCoordinates(7.9631571192, 50.1184399333, 215.47));
    final Set<Waypoint> points = new LinkedHashSet<>();
    points.add(point1);
    points.add(point2);
    final Trail trail = newTrail(points);

    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    final ObjectOutputStream oos = new ObjectOutputStream(out);
    oos.writeObject(trail);
    oos.close();

    final byte[] recovered = out.toByteArray();
    final InputStream in = new ByteArrayInputStream(recovered);
    final ObjectInputStream ois = new ObjectInputStream(in);
    final Trail recoveredTrail = (Trail) ois.readObject();

    assertEquals(trail, recoveredTrail);
  }

  private Coordinates newCoordinates(
      final Double longitude, final Double latitude, final Double elevation) {
    return new CoordinatesBuilder(longitude, latitude).elevation(elevation).build();
  }

  private Waypoint newWaypoint(final Instant time, final Coordinates coordinates) {
    return new WaypointBuilder(time, coordinates).build();
  }

  private Trail newTrail(final Set<Waypoint> points) {
    final Set<String> countries = new LinkedHashSet<>();
    countries.add("DE");
    return newTrail(points, countries);
  }

  private Trail newTrail(final Set<Waypoint> points, final Set<String> countries) {
    return new TrailBuilder(points).countries(countries).build();
  }
}
