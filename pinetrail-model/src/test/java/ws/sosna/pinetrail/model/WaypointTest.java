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
 *
 */
package ws.sosna.pinetrail.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.BeforeClass;
import org.junit.Test;

/** @author Xavier Sosnovsky */
public class WaypointTest extends DescribableTest {

  private static Validator validator;

  @BeforeClass
  public static void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Override
  Describable createDescribable(
      final String name, final String description, final Set<Link> links) {
    return newWaypoint(
        Instant.EPOCH, newCoordinates(8.789654, 40.6784356, 127.8), name, description, links);
  }

  @Test
  public void createInstance() {
    final Waypoint point =
        newWaypoint(Instant.MIN, newCoordinates(8.789654, 40.6784356, 127.8), null, null, null);
    assertNotNull(point);
  }

  @Test
  public void equalityCoordinates() {
    final Waypoint point1 =
        newWaypoint(Instant.MIN, newCoordinates(8.789654, 40.6784356, 127.8), null, null, null);
    final Waypoint point2 =
        newWaypoint(Instant.MIN, newCoordinates(8.789654, 40.6784356, 127.18), null, null, null);
    assertFalse(point1.equals(point2));
    assertFalse(point1.hashCode() == point2.hashCode());
  }

  @Test
  public void equalityTime() {
    final Waypoint point1 =
        newWaypoint(Instant.MIN, newCoordinates(8.789654, 40.6784356, 127.8), null, null, null);
    final Waypoint point2 =
        newWaypoint(Instant.EPOCH, newCoordinates(8.789654, 40.6784356, 127.8), null, null, null);
    assertFalse(point1.equals(point2));
    assertFalse(point1.hashCode() == point2.hashCode());
  }

  @Test
  @Override
  public void equalityOK() {
    super.equalityOK();
    final Waypoint point1 =
        newWaypoint(Instant.MIN, newCoordinates(8.789654, 40.6784356, 127.8), null, null, null);
    final Waypoint point2 =
        newWaypoint(Instant.MIN, newCoordinates(8.789654, 40.6784356, 127.8), null, null, null);
    assertTrue(point1.equals(point2));
  }

  @Test
  public void getCoordinates() {
    final Coordinates coordinates = newCoordinates(8.789654, 40.6784356, 127.8);
    final Waypoint point = newWaypoint(Instant.MIN, coordinates, null, null, null);
    assertEquals(coordinates, point.getCoordinates());
  }

  @Test
  public void getTime() {
    final Instant time = Instant.EPOCH;
    final Waypoint point =
        newWaypoint(time, newCoordinates(8.789654, 40.6784356, 127.8), null, null, null);
    assertEquals(time, point.getTime());
  }

  @Test
  @Override
  public void hashCodeOK() throws URISyntaxException {
    super.hashCodeOK();
    final Waypoint point1 =
        newWaypoint(Instant.MIN, newCoordinates(8.789654, 40.6784356, 127.8), null, null, null);
    final Waypoint point2 =
        newWaypoint(Instant.MIN, newCoordinates(8.789654, 40.6784356, 127.8), null, null, null);
    assertEquals(point1.hashCode(), point2.hashCode());
  }

  @Test
  @Override
  public void hashCodeNOK() throws URISyntaxException {
    super.hashCodeNOK();
    final Waypoint point1 =
        newWaypoint(Instant.MIN, newCoordinates(8.789654, 40.6784356, 127.8), null, null, null);
    final Waypoint point2 =
        newWaypoint(Instant.EPOCH, newCoordinates(8.789654, 40.6784356, 127.8), null, null, null);
    assertFalse(point1.hashCode() == point2.hashCode());
  }

  @Test
  public void toStringOutput() {
    final Coordinates coordinates = newCoordinates(8.789654, 40.6784356, 127.8);
    final Waypoint point = newWaypoint(Instant.EPOCH, coordinates, null, null, null);
    assertEquals(
        "Waypoint{time="
            + Instant.EPOCH
            + ", coordinates="
            + coordinates.toString()
            + ", name=null, description=null, links=[], "
            + "type=null, distance=null, elevationDifference=null, "
            + "grade=null, speed=null, isActive=true, timeDifference=0}",
        point.toString());
  }

  @Test(expected = ValidationException.class)
  public void valTimeNull() {
    newWaypoint(null, newCoordinates(8.789654, 40.6784356, 127.8), null, null, null);
  }

  @Test(expected = ValidationException.class)
  public void valTimeFuture() {
    newWaypoint(Instant.MAX, newCoordinates(8.789654, 40.6784356, 127.8), null, null, null);
  }

  @Test(expected = ValidationException.class)
  public void valCoordinatesNull() {
    newWaypoint(Instant.MIN, null, null, null, null);
  }

  @Test(expected = ValidationException.class)
  public void valCoordinatesInvalid() {
    newWaypoint(Instant.MIN, newCoordinates(8.789654, null, 127.8), null, null, null);
  }

  @Test
  @Override
  public void valOK() {
    final Waypoint point =
        newWaypoint(Instant.MIN, newCoordinates(8.789654, 45.987234, 127.8), null, null, null);
    final Set<ConstraintViolation<Waypoint>> constraintViolations = validator.validate(point);
    assertEquals(0, constraintViolations.size());
  }

  @Test
  public void of() throws URISyntaxException {
    final String label = "Wikipedia";
    final URI location = new URI("https://www.wikipedia.org/");
    final Link link = new LinkBuilder(label, location).build();
    final Set<Link> links = new LinkedHashSet<>();
    links.add(link);
    final Waypoint original =
        new WaypointBuilder(Instant.EPOCH, newCoordinates(8.789654, 45.987234, 127.8))
            .name("name")
            .description("desc")
            .type(WaypointType.VIEWPOINT)
            .links(links)
            .build();
    final Waypoint copy = WaypointBuilder.of(original).build();
    assertNotSame(original, copy);
    assertEquals(original, copy);
  }

  @Test
  public void updateTime() {
    final Waypoint original =
        newWaypoint(Instant.MIN, newCoordinates(8.789654, 45.987234, 127.8), null, null, null);
    final Waypoint copy = WaypointBuilder.of(original).time(Instant.EPOCH).build();
    assertFalse(original.equals(copy));
    assertEquals(Instant.EPOCH, copy.getTime());
  }

  @Test
  public void updateCoordinates() {
    final Waypoint original =
        newWaypoint(Instant.MIN, newCoordinates(8.789654, 45.987234, 127.8), null, null, null);
    final Coordinates newCoordinates = newCoordinates(9.876, 42.567, 163.0);
    final Waypoint copy = WaypointBuilder.of(original).coordinates(newCoordinates).build();
    assertFalse(original.equals(copy));
    assertEquals(newCoordinates, copy.getCoordinates());
  }

  @Test
  public void typeDefault() {
    final Waypoint original =
        newWaypoint(Instant.MIN, newCoordinates(8.789654, 45.987234, 127.8), null, null, null);
    assertNull(original.getType());
  }

  @Test
  public void getType() {
    final Waypoint point =
        new WaypointBuilder(Instant.MIN, newCoordinates(8.789654, 45.987234, 127.8))
            .type(WaypointType.VIEWPOINT)
            .build();
    assertEquals(WaypointType.valueOf("VIEWPOINT"), point.getType());
  }

  @Test
  public void typeAffectEquality() {
    final Waypoint point1 =
        new WaypointBuilder(Instant.MIN, newCoordinates(8.789654, 45.987234, 127.8))
            .type(WaypointType.VIEWPOINT)
            .build();
    final Waypoint point2 = WaypointBuilder.of(point1).build();
    final Waypoint point3 = WaypointBuilder.of(point1).type(WaypointType.FOOD_AND_DRINK).build();
    assertEquals(point1, point2);
    assertFalse(point1.equals(point3));
  }

  @Test
  public void typeAffectHashCode() {
    final Waypoint point1 =
        new WaypointBuilder(Instant.MIN, newCoordinates(8.789654, 45.987234, 127.8))
            .type(WaypointType.VIEWPOINT)
            .build();
    final Waypoint point2 = WaypointBuilder.of(point1).build();
    final Waypoint point3 = WaypointBuilder.of(point1).type(WaypointType.FOOD_AND_DRINK).build();
    assertTrue(point1.hashCode() == point2.hashCode());
    assertFalse(point1.hashCode() == point3.hashCode());
  }

  @Test
  public void distanceDefaultsToNull() {
    final Waypoint point =
        new WaypointBuilder(Instant.MIN, newCoordinates(8.789654, 45.987234, 127.8)).build();
    assertNull(point.getDistance());
  }

  @Test
  public void getDistance() {
    final Waypoint point =
        new WaypointBuilder(Instant.MIN, newCoordinates(8.789654, 45.987234, 127.8))
            .distance(10.5)
            .build();
    assertEquals(10.5, point.getDistance(), 0.0);
  }

  @Test(expected = ValidationException.class)
  public void valDistancePositive() {
    new WaypointBuilder(Instant.MIN, newCoordinates(8.789654, 45.987234, 127.8))
        .distance(-10.0)
        .build();
  }

  @Test
  public void eleDiffDefaultsToNull() {
    final Waypoint point =
        new WaypointBuilder(Instant.MIN, newCoordinates(8.789654, 45.987234, 127.8)).build();
    assertNull(point.getElevationDifference());
  }

  @Test
  public void getEleDiff() {
    final Waypoint point =
        new WaypointBuilder(Instant.MIN, newCoordinates(8.789654, 45.987234, 127.8))
            .elevationDifference(10.5)
            .build();
    assertEquals(10.5, point.getElevationDifference(), 0.0);
  }

  @Test(expected = ValidationException.class)
  public void valEleDiffAboveMin() {
    new WaypointBuilder(Instant.MIN, newCoordinates(8.789654, 45.987234, 127.8))
        .elevationDifference(-9450.1)
        .build();
  }

  @Test(expected = ValidationException.class)
  public void valEleDiffBelowMax() {
    new WaypointBuilder(Instant.MIN, newCoordinates(8.789654, 45.987234, 127.8))
        .elevationDifference(9450.1)
        .build();
  }

  @Test
  public void speedDefaultsToNull() {
    final Waypoint point =
        new WaypointBuilder(Instant.MIN, newCoordinates(8.789654, 45.987234, 127.8)).build();
    assertNull(point.getSpeed());
  }

  @Test
  public void getSpeed() {
    final Waypoint point =
        new WaypointBuilder(Instant.MIN, newCoordinates(8.789654, 45.987234, 127.8))
            .speed(4.75)
            .build();
    assertEquals(4.75, point.getSpeed(), 0.0);
  }

  @Test(expected = ValidationException.class)
  public void valSpeedPositive() {
    new WaypointBuilder(Instant.MIN, newCoordinates(8.789654, 45.987234, 127.8))
        .speed(-10.0)
        .build();
  }

  @Test
  public void isActiveDefaultsToTrue() {
    final Waypoint point =
        new WaypointBuilder(Instant.MIN, newCoordinates(8.789654, 45.987234, 127.8)).build();
    assertTrue(point.isActive());
  }

  @Test
  public void getIsActive() {
    final Waypoint point =
        new WaypointBuilder(Instant.MIN, newCoordinates(8.789654, 45.987234, 127.8))
            .isActive(false)
            .build();
    assertFalse(point.isActive());
  }

  @Test
  public void getGrade() {
    final Waypoint point =
        new WaypointBuilder(Instant.MIN, newCoordinates(8.789654, 45.987234, 127.8))
            .grade(3.5)
            .build();
    assertEquals(3.5, point.getGrade(), 0.0);
  }

  @Test
  public void gradeDefaultsToNull() {
    final Waypoint point =
        new WaypointBuilder(Instant.MIN, newCoordinates(8.789654, 45.987234, 127.8)).build();
    assertNull(point.getGrade());
  }

  @Test(expected = ValidationException.class)
  public void valMinGrade() {
    new WaypointBuilder(Instant.MIN, newCoordinates(8.789654, 45.987234, 127.8))
        .grade(-90.1)
        .build();
  }

  @Test(expected = ValidationException.class)
  public void valMaxGrade() {
    new WaypointBuilder(Instant.MIN, newCoordinates(8.789654, 45.987234, 127.8))
        .grade(90.1)
        .build();
  }

  @Test
  public void valOKGrade() {
    final Waypoint point =
        new WaypointBuilder(Instant.MIN, newCoordinates(8.789654, 45.987234, 127.8))
            .grade(45.0)
            .build();
    final Set<ConstraintViolation<Waypoint>> constraintViolations = validator.validate(point);
    assertEquals(0, constraintViolations.size());
  }

  @Test
  public void getTimeDifference() {
    final Waypoint point =
        new WaypointBuilder(Instant.MIN, newCoordinates(8.789654, 45.987234, 127.8))
            .timeDifference(10)
            .build();
    assertEquals(10, point.getTimeDifference(), 0.0);
  }

  @Test(expected = ValidationException.class)
  public void valMinTimeDifference() {
    new WaypointBuilder(Instant.MIN, newCoordinates(8.789654, 45.987234, 127.8))
        .timeDifference(-1)
        .build();
  }

  @Test
  public void serialize() throws IOException, ClassNotFoundException {
    final double latitude = 90.0;
    final double longitude = 179.9;
    final double elevation = 37.0;
    final Waypoint point =
        new WaypointBuilder(Instant.MIN, newCoordinates(8.789654, 45.987234, 127.8))
            .timeDifference(10)
            .grade(45.0)
            .isActive(false)
            .speed(10.0)
            .elevationDifference(10.5)
            .distance(10.5)
            .build();

    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    final ObjectOutputStream oos = new ObjectOutputStream(out);
    oos.writeObject(point);
    oos.close();

    final byte[] recovered = out.toByteArray();
    final InputStream in = new ByteArrayInputStream(recovered);
    final ObjectInputStream ois = new ObjectInputStream(in);
    final Waypoint recoveredPoint = (Waypoint) ois.readObject();

    assertEquals(point, recoveredPoint);
  }

  private Coordinates newCoordinates(
      final Double longitude, final Double latitude, final Double elevation) {
    return new CoordinatesBuilder(longitude, latitude).elevation(elevation).build();
  }

  private Waypoint newWaypoint(
      final Instant time,
      final Coordinates coordinates,
      final String name,
      final String description,
      final Set<Link> links) {
    return new WaypointBuilder(time, coordinates)
        .name(name)
        .description(description)
        .links(links)
        .build();
  }
}
