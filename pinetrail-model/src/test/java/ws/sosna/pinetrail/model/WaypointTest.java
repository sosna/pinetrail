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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.BeforeClass;
import org.junit.Test;

/** @author Xavier Sosnovsky */
public class WaypointTest {

  private static Validator validator;

  @BeforeClass
  public static void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  public void createInstance() {
    final Waypoint point = newWaypoint(Instant.MIN, newCoordinates(8.789654, 40.6784356, 127.8));
    assertNotNull(point);
  }

  @Test
  public void equalityCoordinates() {
    final Waypoint point1 = newWaypoint(Instant.MIN, newCoordinates(8.789654, 40.6784356, 127.8));
    final Waypoint point2 = newWaypoint(Instant.MIN, newCoordinates(8.789654, 40.6784356, 127.18));
    assertNotEquals(point1, point2);
    assertNotEquals(point1.hashCode(), point2.hashCode());
  }

  @Test
  public void equalityTime() {
    final Waypoint point1 = newWaypoint(Instant.MIN, newCoordinates(8.789654, 40.6784356, 127.8));
    final Waypoint point2 = newWaypoint(Instant.EPOCH, newCoordinates(8.789654, 40.6784356, 127.8));
    assertNotEquals(point1, point2);
    assertNotEquals(point1.hashCode(), point2.hashCode());
  }

  @Test
  public void equalityOK() {
    final Waypoint point1 = newWaypoint(Instant.MIN, newCoordinates(8.789654, 40.6784356, 127.8));
    final Waypoint point2 = newWaypoint(Instant.MIN, newCoordinates(8.789654, 40.6784356, 127.8));
    assertEquals(point1, point2);
  }

  @Test
  public void getCoordinates() {
    final Coordinates coordinates = newCoordinates(8.789654, 40.6784356, 127.8);
    final Waypoint point = newWaypoint(Instant.MIN, coordinates);
    assertEquals(coordinates, point.getCoordinates());
  }

  @Test
  public void getTime() {
    final Instant time = Instant.EPOCH;
    final Waypoint point = newWaypoint(time, newCoordinates(8.789654, 40.6784356, 127.8));
    assertEquals(time, point.getTime());
  }

  @Test
  public void hashCodeOK() {
    final Waypoint point1 = newWaypoint(Instant.MIN, newCoordinates(8.789654, 40.6784356, 127.8));
    final Waypoint point2 = newWaypoint(Instant.MIN, newCoordinates(8.789654, 40.6784356, 127.8));
    assertEquals(point1.hashCode(), point2.hashCode());
  }

  @Test
  public void hashCodeNOK() {
    final Waypoint point1 = newWaypoint(Instant.MIN, newCoordinates(8.789654, 40.6784356, 127.8));
    final Waypoint point2 = newWaypoint(Instant.EPOCH, newCoordinates(8.789654, 40.6784356, 127.8));
    assertNotEquals(point1.hashCode(), point2.hashCode());
  }

  @Test
  public void toStringOutput() {
    final Coordinates coordinates = newCoordinates(8.789654, 40.6784356, 127.8);
    final Waypoint point = newWaypoint(Instant.EPOCH, coordinates);
    assertEquals(
        "Waypoint{time="
            + Instant.EPOCH
            + ", coordinates="
            + coordinates.toString()
            + ", distance=null, elevationDifference=null, "
            + "grade=null, speed=null, isActive=true, timeDifference=0}",
        point.toString());
  }

  @Test(expected = ValidationException.class)
  public void valTimeNull() {
    newWaypoint(null, newCoordinates(8.789654, 40.6784356, 127.8));
  }

  @Test(expected = ValidationException.class)
  public void valTimeFuture() {
    newWaypoint(Instant.MAX, newCoordinates(8.789654, 40.6784356, 127.8));
  }

  @Test(expected = ValidationException.class)
  public void valCoordinatesNull() {
    newWaypoint(Instant.MIN, null);
  }

  @Test(expected = ValidationException.class)
  public void valCoordinatesInvalid() {
    newWaypoint(Instant.MIN, newCoordinates(8.789654, null, 127.8));
  }

  @Test
  public void valOK() {
    final Waypoint point = newWaypoint(Instant.MIN, newCoordinates(8.789654, 45.987234, 127.8));
    final Set<ConstraintViolation<Waypoint>> constraintViolations = validator.validate(point);
    assertEquals(0, constraintViolations.size());
  }

  @Test
  public void updateTime() {
    final Waypoint original = newWaypoint(Instant.MIN, newCoordinates(8.789654, 45.987234, 127.8));
    final Waypoint copy = WaypointBuilder.of(original).time(Instant.EPOCH).build();
    assertNotEquals(original, copy);
    assertEquals(Instant.EPOCH, copy.getTime());
  }

  @Test
  public void updateCoordinates() {
    final Waypoint original = newWaypoint(Instant.MIN, newCoordinates(8.789654, 45.987234, 127.8));
    final Coordinates newCoordinates = newCoordinates(9.876, 42.567, 163.0);
    final Waypoint copy = WaypointBuilder.of(original).coordinates(newCoordinates).build();
    assertNotEquals(original, copy);
    assertEquals(newCoordinates, copy.getCoordinates());
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

  private Waypoint newWaypoint(final Instant time, final Coordinates coordinates) {
    return new WaypointBuilder(time, coordinates).build();
  }
}
