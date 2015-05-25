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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URISyntaxException;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 * @author Xavier Sosnovsky
 */
public class CoordinatesTest {

    private static Validator validator;

    @BeforeClass
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void getLatitude() {
        final double latitude = 47.5913904235;
        final Coordinates instance = 
            newCoordinates(12.9946215637, latitude, 630.28);
        assertEquals(latitude, instance.getLatitude(), 0.0);
    }

    @Test
    public void getLongitude() {
        final double longitude = 12.9946215637;
        final Coordinates instance = 
            newCoordinates(longitude, 47.5913904235, 630.28);
        assertEquals(longitude, instance.getLongitude(), 0.0);
    }

    @Test
    public void getElevation() {
        final double elevation = 630.28;
        final Coordinates instance = 
            newCoordinates(12.9946215637, 47.5913904235, elevation);
        assertEquals(elevation, instance.getElevation(), 0.0);
    }

    @Test
    public void createInstance() {
        final double latitude = 47.5913904235;
        final double longitude = 12.9946215637;
        final double elevation = 630.28;
        final Coordinates instance = 
            newCoordinates(longitude, latitude, elevation);
        assertEquals(longitude, instance.getLongitude(), 0.0);
        assertEquals(latitude, instance.getLatitude(), 0.0);
        assertEquals(elevation, instance.getElevation(), 0.0);
    }

    @Test
    public void equalityNull() {
        final double latitude = 47.5913904235;
        final double longitude = 12.9946215637;
        final double elevation = 630.28;
        final Coordinates instance = 
            newCoordinates(longitude, latitude, elevation);
        assertFalse(instance.equals(null));
    }

    @Test
    public void equalityType() {
        final double latitude = 47.5913904235;
        final double longitude = 12.9946215637;
        final double elevation = 630.28;
        final Coordinates instance = 
            newCoordinates(longitude, latitude, elevation);
        assertFalse(instance.equals("test"));
    }

    @Test
    public void equalityLongitude() {
        final double latitude = 47.5913904235;
        final double longitude = 12.9946215637;
        final double elevation = 630.28;
        final Coordinates instance1 = 
            newCoordinates(longitude, latitude, elevation);
        final Coordinates instance2 = 
            newCoordinates(0.0, latitude, elevation);
        assertFalse(instance1.equals(instance2));
    }

    @Test
    public void equalityLatitude() {
        final double latitude = 47.5913904235;
        final double longitude = 12.9946215637;
        final double elevation = 630.28;
        final Coordinates instance1 = 
            newCoordinates(longitude, latitude, elevation);
        final Coordinates instance2 = 
            newCoordinates(longitude, 0.0, elevation);
        assertFalse(instance1.equals(instance2));
    }

    @Test
    public void equalityElevation() {
        final double latitude = 47.5913904235;
        final double longitude = 12.9946215637;
        final double elevation = 630.28;
        final Coordinates instance1 = 
            newCoordinates(longitude, latitude, elevation);
        final Coordinates instance2 = 
            newCoordinates(longitude, latitude, 0.0);
        assertFalse(instance1.equals(instance2));
    }

    @Test
    public void equalityOK() {
        final double latitude = 47.5913904235;
        final double longitude = 12.9946215637;
        final double elevation = 630.28;
        final Coordinates instance1 = 
            newCoordinates(longitude, latitude, elevation);
        final Coordinates instance2 = 
            newCoordinates(longitude, latitude, elevation);
        assertTrue(instance1.equals(instance2));
    }

    @Test
    public void hashCodeOK() {
        final double latitude = 47.5913904235;
        final double longitude = 12.9946215637;
        final double elevation = 630.28;
        final Coordinates instance1 = 
            newCoordinates(longitude, latitude, elevation);
        final Coordinates instance2 = 
            newCoordinates(longitude, latitude, elevation);
        assertEquals(instance1.hashCode(), instance2.hashCode());
    }

    @Test
    public void hashCodeNOK() {
        final double latitude = 47.5913904235;
        final double longitude = 12.9946215637;
        final double elevation = 630.28;
        final Coordinates instance1 = 
            newCoordinates(longitude, latitude, elevation);
        final Coordinates instance2 = 
            newCoordinates(longitude, latitude, 0.0);
        assertFalse(instance1.hashCode() == instance2.hashCode());
    }

    @Test
    public void toStringOutput() {
        final double latitude = 47.5913904235;
        final double longitude = 12.9946215637;
        final double elevation = 630.28;
        final Coordinates instance = 
            newCoordinates(longitude, latitude, elevation);
        assertEquals("Coordinates{" + "latitude=" + latitude + ", "
            + "longitude=" + longitude + ", elevation=" + elevation + '}',
            instance.toString());
    }

    @Test
    public void valOK() {
        final double latitude = 47.5913904235;
        final double longitude = 12.9946215637;
        final double elevation = 630.28;
        final Coordinates instance = 
            newCoordinates(longitude, latitude, elevation);
        Set<ConstraintViolation<Coordinates>> constraintViolations
            = validator.validate(instance);
        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void valLatitudeMin() {
        final double latitude = -90.1;
        final double longitude = 12.9946215637;
        final double elevation = 630.28;
        final Coordinates instance = 
            newCoordinates(longitude, latitude, elevation);
        Set<ConstraintViolation<Coordinates>> constraintViolations
            = validator.validate(instance);
        assertEquals(1, constraintViolations.size());
        constraintViolations.stream().forEach((prob) -> {
            assertEquals("Latitude must be superior or equal to -90 degrees. Got -90.1.", prob.getMessage());
        });
    }

    @Test
    public void valLatitudeMax() {
        final double latitude = 90.1;
        final double longitude = 12.9946215637;
        final double elevation = 630.28;
        final Coordinates instance = 
            newCoordinates(longitude, latitude, elevation);
        Set<ConstraintViolation<Coordinates>> constraintViolations
            = validator.validate(instance);
        assertEquals(1, constraintViolations.size());
        constraintViolations.stream().forEach((prob) -> {
            assertEquals("Latitude must be inferior or equal to 90 degrees. Got 90.1.", prob.getMessage());
        });
    }

    @Test
    public void valLatitudeNull() {
        final double longitude = 12.9946215637;
        final double elevation = 630.28;
        final Coordinates instance = 
            newCoordinates(longitude, null, elevation);
        Set<ConstraintViolation<Coordinates>> constraintViolations
            = validator.validate(instance);
        assertEquals(1, constraintViolations.size());
        constraintViolations.stream().forEach((prob) -> {
            assertEquals("Latitude must be supplied.", prob.getMessage());
        });
    }

    @Test
    public void valLatitudeNaN() {
        final double longitude = 12.9946215637;
        final double elevation = 630.28;
        final Coordinates instance = 
            newCoordinates(longitude, Double.NaN, elevation);
        Set<ConstraintViolation<Coordinates>> constraintViolations
            = validator.validate(instance);
        assertEquals(2, constraintViolations.size());
    }

    @Test
    public void valLongitudeMin() {
        final double latitude = -90.0;
        final double longitude = -180.1;
        final double elevation = 630.28;
        final Coordinates instance = 
            newCoordinates(longitude, latitude, elevation);
        Set<ConstraintViolation<Coordinates>> constraintViolations
            = validator.validate(instance);
        assertEquals(1, constraintViolations.size());
        constraintViolations.stream().forEach((prob) -> {
            assertEquals("Longitude must be superior or equal to -180 degrees. Got -180.1.", prob.getMessage());
        });
    }

    @Test
    public void valLongitudeMax() {
        final double latitude = 90.0;
        final double longitude = 180.0;
        final double elevation = 630.28;
        final Coordinates instance = 
            newCoordinates(longitude, latitude, elevation);
        Set<ConstraintViolation<Coordinates>> constraintViolations
            = validator.validate(instance);
        assertEquals(1, constraintViolations.size());
        constraintViolations.stream().forEach((prob) -> {
            assertEquals("Longitude must be inferior to 180 degrees. Got 180.0.", prob.getMessage());
        });
    }

    @Test
    public void valLongitudeNull() {
        final double latitude = 90.0;
        final double elevation = 630.28;
        final Coordinates instance = 
            newCoordinates(null, latitude, elevation);
        Set<ConstraintViolation<Coordinates>> constraintViolations
            = validator.validate(instance);
        assertEquals(1, constraintViolations.size());
        constraintViolations.stream().forEach((prob) -> {
            assertEquals("Longitude must be supplied.", prob.getMessage());
        });
    }

    @Test
    public void valLongitudeNaN() {
        final double latitude = 90.0;
        final double elevation = 630.28;
        final Coordinates instance = newCoordinates(Double.NaN, latitude,
            elevation);
        Set<ConstraintViolation<Coordinates>> constraintViolations
            = validator.validate(instance);
        assertEquals(2, constraintViolations.size());
    }

    @Test
    public void valElevationMin() {
        final double latitude = -90.0;
        final double longitude = -180.0;
        final double elevation = -451;
        final Coordinates instance = newCoordinates(longitude, latitude,
            elevation);
        Set<ConstraintViolation<Coordinates>> constraintViolations
            = validator.validate(instance);
        assertEquals(1, constraintViolations.size());
        constraintViolations.stream().forEach((prob) -> {
            assertEquals("Elevation must be superior or equal to -450 meters. Got -451.0.", prob.getMessage());
        });
        
    }

    @Test
    public void valElevationMax() {
        final double latitude = 90.0;
        final double longitude = 179.9;
        final double elevation = 10000;
        final Coordinates instance = newCoordinates(longitude, latitude,
            elevation);
        Set<ConstraintViolation<Coordinates>> constraintViolations
            = validator.validate(instance);
        assertEquals(1, constraintViolations.size());
        constraintViolations.stream().forEach((prob) -> {
            assertEquals("Elevation must be inferior or equal to 9000 meters. Got 10000.0.", prob.getMessage());
        });
    }

    @Test
    public void valElevationNull() {
        final double latitude = 90.0;
        final double longitude = 179.9;
        final Coordinates instance = newCoordinates(longitude, latitude, null);
        Set<ConstraintViolation<Coordinates>> constraintViolations
            = validator.validate(instance);
        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void valElevationNaN() {
        final double latitude = 90.0;
        final double longitude = 179.9;
        final Coordinates instance
            = newCoordinates(longitude, latitude, Double.NaN);
        Set<ConstraintViolation<Coordinates>> constraintViolations
            = validator.validate(instance);
        assertEquals(2, constraintViolations.size());
    }
    
    @Test
    public void of() {
        final double latitude = 90.0;
        final double longitude = 179.9;
        final double elevation = 37.0;
        final Coordinates original
            = newCoordinates(longitude, latitude, elevation);
        final Coordinates copy = CoordinatesBuilder.of(original).build();
        assertEquals(original, copy);
    }
    
    @Test
    public void updateLatitude() {
        final double latitude = 90.0;
        final double newLatitude = 79.0;
        final double longitude = 179.9;
        final double elevation = 37.0;
        final Coordinates original
            = newCoordinates(longitude, latitude, elevation);
        final Coordinates copy = CoordinatesBuilder.of(original).latitude(
            newLatitude).build();
        assertFalse(original.equals(copy));
        assertEquals(newLatitude, copy.getLatitude(), 0.0);
    }
    
    @Test
    public void updateLongitude() {
        final double latitude = 90.0;
        final double longitude = 179.9;
        final double newLongitude = 69.9;
        final double elevation = 37.0;
        final Coordinates original
            = newCoordinates(longitude, latitude, elevation);
        final Coordinates copy = CoordinatesBuilder.of(original).longitude(
            newLongitude).build();
        assertFalse(original.equals(copy));
        assertEquals(newLongitude, copy.getLongitude(), 0.0);
    }
    
    @Test
    public void updateElevation() {
        final double latitude = 90.0;
        final double longitude = 179.9;
        final double elevation = 37.0;
        final double newElevation = 47.0;
        final Coordinates original
            = newCoordinates(longitude, latitude, elevation);
        final Coordinates copy = CoordinatesBuilder.of(original).elevation(
            newElevation).build();
        assertFalse(original.equals(copy));
        assertEquals(newElevation, copy.getElevation(), 0.0);
    }
    
    @Test
    public void serialize() throws IOException, ClassNotFoundException {
        final double latitude = 90.0;
        final double longitude = 179.9;
        final double elevation = 37.0;
        final Coordinates c = newCoordinates(longitude, latitude, elevation);
        
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(c);
        oos.close();
        
        final byte[] recovered = out.toByteArray();
        final InputStream in = new ByteArrayInputStream(recovered);
        final ObjectInputStream ois = new ObjectInputStream(in);
        final Coordinates recoveredC = (Coordinates) ois.readObject();
        
        assertEquals(c, recoveredC);
    }

    private Coordinates newCoordinates(final Double longitude,
        final Double latitude, final Double elevation) {
        return new CoordinatesBuilder(longitude, latitude).elevation(elevation).
            build();
    }
}
