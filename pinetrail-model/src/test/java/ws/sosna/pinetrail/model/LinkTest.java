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
import java.net.URI;
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
public class LinkTest {
    
    private static Validator validator;

    @BeforeClass
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
    @Test
    public void createInstance() throws URISyntaxException {
        final String label = "Wikipedia";
        final URI location = new URI("https://www.wikipedia.org/");
        final Link link = newLinkInstance(label, location);
        assertNotNull(link);
    }
    
    @Test
    public void equalityNull() throws URISyntaxException {
        final String label = "Wikipedia";
        final URI location = new URI("https://www.wikipedia.org/");
        final Link link = newLinkInstance(label, location);
        assertFalse(link.equals(null));
    }
    
    @Test
    public void equalityOK() throws URISyntaxException {
        final String label = "Wikipedia";
        final URI location = new URI("https://www.wikipedia.org/");
        final Link link1 = newLinkInstance(label, location);
        final Link link2 = newLinkInstance(label, location);
        assertEquals(link1, link2);
    }
    
    @Test
    public void equalityType() throws URISyntaxException {
        final String label = "Wikipedia";
        final URI location = new URI("https://www.wikipedia.org/");
        final Link link = newLinkInstance(label, location);
        assertFalse(link.equals(label));
    }
    
    @Test
    public void equalityLabel() throws URISyntaxException {
        final String label = "Wikipedia";
        final URI location = new URI("https://www.wikipedia.org/");
        final Link link1 = newLinkInstance(label, location);
        final Link link2 = newLinkInstance(label + "1", location);
        assertFalse(link1.equals(link2));
    }
    
    @Test
    public void equalityLocation() throws URISyntaxException {
        final String label = "Wikipedia";
        final URI location1 = new URI("https://www.wikipedia.org/");
        final URI location2 = new URI("https://de.wikipedia.org/");
        final Link link1 = newLinkInstance(label, location1);
        final Link link2 = newLinkInstance(label, location2);
        assertFalse(location1.equals(location2));
        assertFalse(link1.equals(link2));
    }
    
    @Test
    public void hashCodeOK() throws URISyntaxException {
        final String label = "Wikipedia";
        final URI location = new URI("https://www.wikipedia.org/");
        final Link link1 = newLinkInstance(label, location);
        final Link link2 = newLinkInstance(label, location);
        assertEquals(link1.hashCode(), link2.hashCode());
    }
    
    @Test
    public void hashCodeNOK() throws URISyntaxException {
        final String label = "Wikipedia";
        final URI location = new URI("https://www.wikipedia.org/");
        final Link link1 = newLinkInstance(label, location);
        final Link link2 = newLinkInstance(label, new URI(
            "https://de.wikipedia.org/"));
        assertFalse(link1.hashCode() == link2.hashCode());
    }
    
    @Test
    public void toStringOutput() throws URISyntaxException {
        final String label = "Wikipedia";
        final URI location = new URI("https://www.wikipedia.org/");
        final Link link = newLinkInstance(label, location);
        assertEquals(
            "Link{label=" + label + ", location=https://www.wikipedia.org/}",
            link.toString());
    }
    
    @Test
    public void valLabelNull() throws URISyntaxException {
        final URI location = new URI("https://www.wikipedia.org/");
        final Link link = newLinkInstance(null, location);
        final Set<ConstraintViolation<Link>> constraintViolations
            = validator.validate(link);
        assertEquals(1, constraintViolations.size());
    }
    
    @Test
    public void valLabelEmpty() throws URISyntaxException {
        final String label = "  ";
        final URI location = new URI("https://www.wikipedia.org/");
        final Link link = newLinkInstance(label, location);
        final Set<ConstraintViolation<Link>> constraintViolations
            = validator.validate(link);
        assertEquals(1, constraintViolations.size());
        constraintViolations.stream().forEach((prob) -> {
            assertEquals("A label must be supplied for the link.", prob.getMessage());
        });
    }
    
    @Test
    public void valLocationNull() {
        final String label = "Wikipedia";
        final Link link = newLinkInstance(label, null);
        final Set<ConstraintViolation<Link>> constraintViolations
            = validator.validate(link);
        assertEquals(1, constraintViolations.size());
        constraintViolations.stream().forEach((prob) -> {
            assertEquals("A location must be supplied for the link.", prob.getMessage());
        });
    }
    
    @Test
    public void valOK() throws URISyntaxException {
        final String label = "Wikipedia";
        final URI location = new URI("https://www.wikipedia.org/");
        final Link link = newLinkInstance(label, location);
        final Set<ConstraintViolation<Link>> constraintViolations
            = validator.validate(link);
        assertEquals(0, constraintViolations.size());
    }
    
    @Test
    public void of() throws URISyntaxException {
        final String label = "Wikipedia";
        final URI location = new URI("https://www.wikipedia.org/");
        final Link link = newLinkInstance(label, location);
        final Link copy = LinkBuilder.of(link).build();
        assertEquals(link.getLabel(), copy.getLabel());
        assertEquals(copy.getLocation(), copy.getLocation());
    }
    
    @Test
    public void updateLocation() throws URISyntaxException {
        final String label = "Wikipedia";
        final URI location = new URI("https://www.wikipedia.org/");
        final URI newLocation = new URI("https://ru.wikipedia.org/");
        final Link link = newLinkInstance(label, location);
        final Link copy = LinkBuilder.of(link).location(newLocation).build();
        assertEquals(link.getLabel(), copy.getLabel());
        assertFalse(link.getLocation().equals(copy.getLocation()));
        assertEquals(newLocation, copy.getLocation());
    }
    
    @Test 
    public void updateLabel() throws URISyntaxException {        
        final String label = "Wikipedia";
        final String newLabel = "Wiki";
        final URI location = new URI("https://www.wikipedia.org/");
        final Link link = newLinkInstance(label, location);
        final Link copy = LinkBuilder.of(link).label(newLabel).build();
        assertFalse(link.getLabel().equals(copy.getLabel()));
        assertEquals(newLabel, copy.getLabel());
        assertEquals(link.getLocation(), copy.getLocation());
    }
    
    @Test
    public void serialize() throws URISyntaxException, IOException,
        ClassNotFoundException {
        final String label = "Wikipedia";
        final URI location = new URI("https://www.wikipedia.org/");
        final Link link = newLinkInstance(label, location);
        
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(link);
        oos.close();
        
        final byte[] recovered = out.toByteArray();
        final InputStream in = new ByteArrayInputStream(recovered);
        final ObjectInputStream ois = new ObjectInputStream(in);
        final Link recoveredLink = (Link) ois.readObject();
        
        assertEquals(link, recoveredLink);
        
    }
    
    private Link newLinkInstance(final String label, final URI location) {
        return new LinkBuilder(label, location).build();
    }
}
