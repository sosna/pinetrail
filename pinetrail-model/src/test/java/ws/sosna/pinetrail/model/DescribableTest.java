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
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.BeforeClass;
import org.junit.Test;

/** @author Xavier Sosnovsky */
public abstract class DescribableTest {

  private static Validator validator;

  @BeforeClass
  public static void setUpClass() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  abstract Describable createDescribable(
      final String name, final String description, final Set<Link> links);

  @Test
  public void equalityOK() {
    final String name = "name";
    final String desc = "desc";
    final Describable item1 = createDescribable(name, desc, null);
    final Describable item2 = createDescribable(name, desc, null);
    assertEquals(item1, item2);
  }

  @Test
  public void equalityNull() {
    final String name = "name";
    final String desc = "desc";
    final Describable item = createDescribable(name, desc, null);
    assertFalse(item.equals(null));
  }

  @Test
  public void equalityType() {
    final String name = "name";
    final String desc = "desc";
    final Describable item = createDescribable(name, desc, null);
    assertFalse(item.equals(name));
  }

  @Test
  public void equalityName() {
    final String name1 = "name1";
    final String name2 = "name2";
    final Describable item1 = createDescribable(name1, null, null);
    final Describable item2 = createDescribable(name2, null, null);
    final Describable item3 = createDescribable(name2, null, null);
    assertFalse(item1.equals(item2));
    assertFalse(item1.hashCode() == item2.hashCode());
    assertTrue(item3.equals(item2));
    assertEquals(item3.hashCode(), item2.hashCode());
  }

  @Test
  public void equalityDescription() {
    final String desc1 = "name1";
    final String desc2 = "name2";
    final Describable item1 = createDescribable("name", desc1, null);
    final Describable item2 = createDescribable("name", desc2, null);
    final Describable item3 = createDescribable("name", desc2, null);
    assertFalse(item1.equals(item2));
    assertFalse(item1.hashCode() == item2.hashCode());
    assertTrue(item3.equals(item2));
    assertEquals(item3.hashCode(), item2.hashCode());
  }

  @Test
  public void equalityLinks() throws URISyntaxException {
    final String name = "name";
    final Set<Link> links = new HashSet<>();
    links.add(newLinkInstance("label", new URI("http://www.test.com")));
    final Describable item1 = createDescribable(name, null, links);
    final Describable item2 = createDescribable(name, null, null);
    final Describable item3 = createDescribable(name, null, links);
    final Describable item4 = createDescribable(name, null, null);
    assertFalse(item1.equals(item2));
    assertFalse(item4.equals(item3));
    assertEquals(item1, item3);
    assertEquals(item2, item4);
    assertFalse(item1.hashCode() == item2.hashCode());
    assertEquals(item3.hashCode(), item1.hashCode());
  }

  @Test
  public void getName() {
    final String name = "name";
    final Describable item = createDescribable(name, null, null);
    assertEquals(name, item.getName());
  }

  @Test
  public void getDescription() {
    final String desc = "desc";
    final Describable item = createDescribable("name", desc, null);
    assertEquals(desc, item.getDescription());
  }

  @Test
  public void getLinks() throws URISyntaxException {
    final Set<Link> links = new HashSet<>();
    links.add(newLinkInstance("label", new URI("http://www.test.com")));
    final Describable item = createDescribable("name", null, links);
    assertEquals(links, item.getLinks());
  }

  @Test
  public void hashCodeOK() throws URISyntaxException {
    final String name = "name";
    final Set<Link> links = new HashSet<>();
    links.add(newLinkInstance("label", new URI("http://www.test.com")));
    final Describable item1 = createDescribable(name, null, links);
    final Describable item2 = createDescribable(name, null, links);
    assertEquals(item1.hashCode(), item2.hashCode());
  }

  @Test
  public void hashCodeNOK() throws URISyntaxException {
    final String name = "name";
    final Set<Link> links = new HashSet<>();
    links.add(newLinkInstance("label", new URI("http://www.test.com")));
    final Describable item1 = createDescribable(name, null, links);
    final Describable item2 = createDescribable(name, null, null);
    assertFalse(item1.hashCode() == item2.hashCode());
  }

  @Test
  public void valOK() throws URISyntaxException {
    final String name = "name";
    final Set<Link> links = new HashSet<>();
    links.add(newLinkInstance("label", new URI("http://www.test.com")));
    final Describable item = createDescribable(name, null, links);
    final Set<ConstraintViolation<Describable>> constraintViolations = validator.validate(item);
    assertEquals(0, constraintViolations.size());
  }

  @Test(expected = ValidationException.class)
  public void valNOK() {
    final String name = "name";
    final Set<Link> links = new HashSet<>();
    links.add(newLinkInstance(" ", null));
    createDescribable(name, null, links);
  }

  @Test
  public void linksImmutable() throws URISyntaxException {
    final String name = "name";
    final Set<Link> links = new HashSet<>();
    links.add(newLinkInstance("label", new URI("http://www.test.com")));
    final Describable item = createDescribable(name, null, links);
    final Link link2 = newLinkInstance("label2", new URI("http://www.test.org"));
    links.add(link2);
    assertEquals(1, item.getLinks().size());
    assertFalse(item.getLinks().contains(link2));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void linksImmutableFromGetter() throws URISyntaxException {
    final String name = "name";
    final Set<Link> links = new HashSet<>();
    links.add(newLinkInstance("label", new URI("http://www.test.com")));
    final Describable item = createDescribable(name, null, links);
    item.getLinks().clear();
  }

  private Link newLinkInstance(final String label, final URI location) {
    return new LinkBuilder(label, location).build();
  }
}
