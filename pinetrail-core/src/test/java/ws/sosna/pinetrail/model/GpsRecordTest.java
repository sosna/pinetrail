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
import static org.junit.Assert.assertNotEquals;

import java.time.Instant;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/** @author Xavier Sosnovsky */
public class GpsRecordTest {

  private static Validator validator;

  @BeforeClass
  public static void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  public void getTime() {
    final Instant time = Instant.EPOCH;
    final GpsRecord instance = newGpsRecord(time, 12.9946215637, 47.5913904235, 630.28);
    assertEquals(time, instance.getTime());
  }

  @Test
  public void getLatitude() {
    final double latitude = 47.5913904235;
    final GpsRecord instance = newGpsRecord(Instant.EPOCH, 12.9946215637, latitude, 630.28);
    assertEquals(latitude, instance.getLatitude(), 0.0);
  }

  @Test
  public void getLongitude() {
    final double longitude = 12.9946215637;
    final GpsRecord instance = newGpsRecord(Instant.EPOCH, longitude, 47.5913904235, 630.28);
    assertEquals(longitude, instance.getLongitude(), 0.0);
  }

  @Test
  public void getElevation() {
    final double elevation = 630.28;
    final GpsRecord instance = newGpsRecord(Instant.EPOCH, 12.9946215637, 47.5913904235, elevation);
    assertEquals(elevation, instance.getElevation(), 0.0);
  }

  @Test
  public void elevationDefaultTo0() {
    final GpsRecord instance = GpsRecord.of(Instant.EPOCH, 12.9946215637, 47.5913904235);
    assertEquals(Double.NaN, instance.getElevation(), 0.0);
  }

  @Test
  public void nullElevationDefaultTo0() {
    final GpsRecord instance = newGpsRecord(Instant.EPOCH, 12.9946215637, 47.5913904235, null);
    assertEquals(Double.NaN, instance.getElevation(), 0.0);
  }

  @Test
  public void nanElevationDefaultTo0() {
    final GpsRecord instance = newGpsRecord(Instant.EPOCH, 12.9946215637, 47.5913904235, Double.NaN);
    assertEquals(Double.NaN, instance.getElevation(), 0.0);
  }

  @Test
  public void createInstance() {
    final Instant time = Instant.EPOCH;
    final double latitude = 47.5913904235;
    final double longitude = 12.9946215637;
    final double elevation = 630.28;
    final GpsRecord instance = newGpsRecord(time, longitude, latitude, elevation);
    assertEquals(time, instance.getTime());
    assertEquals(longitude, instance.getLongitude(), 0.0);
    assertEquals(latitude, instance.getLatitude(), 0.0);
    assertEquals(elevation, instance.getElevation(), 0.0);
  }

  @Test
  public void equalityNull() {
    final Instant time = Instant.EPOCH;
    final double latitude = 47.5913904235;
    final double longitude = 12.9946215637;
    final double elevation = 630.28;
    final GpsRecord instance = newGpsRecord(time, longitude, latitude, elevation);
    assertNotEquals(instance, null);
  }

  @Test
  public void equalityTime() {
    final Instant time = Instant.EPOCH;
    final double latitude = 47.5913904235;
    final double longitude = 12.9946215637;
    final double elevation = 630.28;
    final GpsRecord instance1 = newGpsRecord(time, longitude, latitude, elevation);
    final GpsRecord instance2 = newGpsRecord(Instant.MAX, longitude, latitude, elevation);
    assertNotEquals(instance1, instance2);
  }

  @Test
  public void equalityLongitude() {
    final Instant time = Instant.EPOCH;
    final double latitude = 47.5913904235;
    final double longitude = 12.9946215637;
    final double elevation = 630.28;
    final GpsRecord instance1 = newGpsRecord(time, longitude, latitude, elevation);
    final GpsRecord instance2 = newGpsRecord(time, 0.0, latitude, elevation);
    assertNotEquals(instance1, instance2);
  }

  @Test
  public void equalityLatitude() {
    final Instant time = Instant.EPOCH;
    final double latitude = 47.5913904235;
    final double longitude = 12.9946215637;
    final double elevation = 630.28;
    final GpsRecord instance1 = newGpsRecord(time, longitude, latitude, elevation);
    final GpsRecord instance2 = newGpsRecord(time, longitude, 0.0, elevation);
    assertNotEquals(instance1, instance2);
  }

  @Test
  public void equalityElevation() {
    final Instant time = Instant.EPOCH;
    final double latitude = 47.5913904235;
    final double longitude = 12.9946215637;
    final double elevation = 630.28;
    final GpsRecord instance1 = newGpsRecord(time, longitude, latitude, elevation);
    final GpsRecord instance2 = newGpsRecord(time, longitude, latitude, 0.0);
    assertNotEquals(instance1, instance2);
  }

  @Test
  public void equalityOK() {
    final Instant time = Instant.EPOCH;
    final double latitude = 47.5913904235;
    final double longitude = 12.9946215637;
    final double elevation = 630.28;
    final GpsRecord instance1 = newGpsRecord(time, longitude, latitude, elevation);
    final GpsRecord instance2 = newGpsRecord(time, longitude, latitude, elevation);
    assertEquals(instance1, instance2);
  }

  @Test
  public void hashCodeOK() {
    final Instant time = Instant.EPOCH;
    final double latitude = 47.5913904235;
    final double longitude = 12.9946215637;
    final double elevation = 630.28;
    final GpsRecord instance1 = newGpsRecord(time, longitude, latitude, elevation);
    final GpsRecord instance2 = newGpsRecord(time, longitude, latitude, elevation);
    assertEquals(instance1.hashCode(), instance2.hashCode());
  }

  @Test
  public void hashCodeNOK() {
    final Instant time = Instant.EPOCH;
    final double latitude = 47.5913904235;
    final double longitude = 12.9946215637;
    final double elevation = 630.28;
    final GpsRecord instance1 = newGpsRecord(time, longitude, latitude, elevation);
    final GpsRecord instance2 = newGpsRecord(time, longitude, latitude, 0.0);
    assertNotEquals(instance1.hashCode(), instance2.hashCode());
  }

  @Test
  public void toStringOutput() {
    final Instant time = Instant.EPOCH;
    final double latitude = 47.5913904235;
    final double longitude = 12.9946215637;
    final double elevation = 630.28;
    final GpsRecord instance = newGpsRecord(time, longitude, latitude, elevation);
    assertEquals(
        "GpsRecord{time="
            + time
            + ", latitude="
            + latitude
            + ", "
            + "longitude="
            + longitude
            + ", elevation="
            + elevation
            + '}',
        instance.toString());
  }

  @Test
  public void valOK() {
    final Instant time = Instant.EPOCH;
    final double latitude = 47.5913904235;
    final double longitude = 12.9946215637;
    final double elevation = 630.28;
    final GpsRecord instance = newGpsRecord(time, longitude, latitude, elevation);
    Set<ConstraintViolation<GpsRecord>> constraintViolations = validator.validate(instance);
    assertEquals(0, constraintViolations.size());
  }

  @Test
  public void valTimeFuture() {
    final Instant time = Instant.MAX;
    final double latitude = -89;
    final double longitude = 12.9946215637;
    final double elevation = 630.28;
    final GpsRecord instance = newGpsRecord(time, longitude, latitude, elevation);
    Set<ConstraintViolation<GpsRecord>> constraintViolations = validator.validate(instance);
    assertEquals(1, constraintViolations.size());
    constraintViolations
        .forEach(
            (prob) -> {
              Assert.assertEquals(
                  "The time the waypoint was recorded must be in the past. Got " + time + ".",
                  prob.getMessage());
            });
  }

  @Test
  public void valLatitudeMin() {
    final Instant time = Instant.EPOCH;
    final double latitude = -90.1;
    final double longitude = 12.9946215637;
    final double elevation = 630.28;
    final GpsRecord instance = newGpsRecord(time, longitude, latitude, elevation);
    Set<ConstraintViolation<GpsRecord>> constraintViolations = validator.validate(instance);
    assertEquals(1, constraintViolations.size());
    constraintViolations
        .forEach(
            (prob) -> {
              Assert.assertEquals(
                  "Latitude must be superior or equal to -90 degrees. Got -90.1.",
                  prob.getMessage());
            });
  }

  @Test
  public void valLatitudeMax() {
    final Instant time = Instant.EPOCH;
    final double latitude = 90.1;
    final double longitude = 12.9946215637;
    final double elevation = 630.28;
    final GpsRecord instance = newGpsRecord(time, longitude, latitude, elevation);
    Set<ConstraintViolation<GpsRecord>> constraintViolations = validator.validate(instance);
    assertEquals(1, constraintViolations.size());
    constraintViolations
        .forEach(
            (prob) -> {
              Assert.assertEquals(
                  "Latitude must be inferior or equal to 90 degrees. Got 90.1.", prob.getMessage());
            });
  }

  @Test
  public void valLatitudeNull() {
    final Instant time = Instant.EPOCH;
    final double longitude = 12.9946215637;
    final double elevation = 630.28;
    final GpsRecord instance = newGpsRecord(time, longitude, null, elevation);
    Set<ConstraintViolation<GpsRecord>> constraintViolations = validator.validate(instance);
    assertEquals(1, constraintViolations.size());
    constraintViolations
        .forEach(
            (prob) -> {
              Assert.assertEquals("Latitude must be supplied.", prob.getMessage());
            });
  }

  @Test
  public void valLatitudeNaN() {
    final Instant time = Instant.EPOCH;
    final double longitude = 12.9946215637;
    final double elevation = 630.28;
    final GpsRecord instance = newGpsRecord(time, longitude, Double.NaN, elevation);
    Set<ConstraintViolation<GpsRecord>> constraintViolations = validator.validate(instance);
    assertEquals(2, constraintViolations.size());
  }

  @Test
  public void valLongitudeMin() {
    final Instant time = Instant.EPOCH;
    final double latitude = -90.0;
    final double longitude = -180.1;
    final double elevation = 630.28;
    final GpsRecord instance = newGpsRecord(time, longitude, latitude, elevation);
    Set<ConstraintViolation<GpsRecord>> constraintViolations = validator.validate(instance);
    assertEquals(1, constraintViolations.size());
    constraintViolations
        .forEach(
            (prob) -> {
              Assert.assertEquals(
                  "Longitude must be superior or equal to -180 degrees. Got -180.1.",
                  prob.getMessage());
            });
  }

  @Test
  public void valLongitudeMax() {
    final Instant time = Instant.EPOCH;
    final double latitude = 90.0;
    final double longitude = 180.0;
    final double elevation = 630.28;
    final GpsRecord instance = newGpsRecord(time, longitude, latitude, elevation);
    Set<ConstraintViolation<GpsRecord>> constraintViolations = validator.validate(instance);
    assertEquals(1, constraintViolations.size());
    constraintViolations
        .forEach(
            (prob) -> {
              Assert.assertEquals(
                  "Longitude must be inferior to 180 degrees. Got 180.0.", prob.getMessage());
            });
  }

  @Test
  public void valLongitudeNull() {
    final Instant time = Instant.EPOCH;
    final double latitude = 90.0;
    final double elevation = 630.28;
    final GpsRecord instance = newGpsRecord(time, null, latitude, elevation);
    Set<ConstraintViolation<GpsRecord>> constraintViolations = validator.validate(instance);
    assertEquals(1, constraintViolations.size());
    constraintViolations
        .forEach(
            (prob) -> {
              Assert.assertEquals("Longitude must be supplied.", prob.getMessage());
            });
  }

  @Test
  public void valLongitudeNaN() {
    final Instant time = Instant.EPOCH;
    final double latitude = 90.0;
    final double elevation = 630.28;
    final GpsRecord instance = newGpsRecord(time, Double.NaN, latitude, elevation);
    Set<ConstraintViolation<GpsRecord>> constraintViolations = validator.validate(instance);
    assertEquals(2, constraintViolations.size());
  }

  private GpsRecord newGpsRecord(
      final Instant time, final Double longitude, final Double latitude, final Double elevation) {
    return GpsRecord.of(time, longitude, latitude, elevation);
  }
}
