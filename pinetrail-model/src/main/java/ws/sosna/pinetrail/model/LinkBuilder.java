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

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.URI;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.sosna.pinetrail.utils.logging.Actions;
import ws.sosna.pinetrail.utils.logging.Markers;
import ws.sosna.pinetrail.utils.logging.StatusCodes;

/**
 * Builds immutable instances of the {@code Link} interface.
 *
 * <p>A new instance can be built as follows:<br>
 * <code>Link link = new LinkBuilder(label, location).build();</code>
 *
 * <p>Instances are immutable. In case it is needed to update the values of some fields (like
 * changing the label of the link via a GUI), the of() method can be used:<br>
 * <code>Link newLink = LinkBuilder.of(link).label(newLabel).build();</code>
 *
 * @see Link
 * @author Xavier Sosnovsky
 */
public final class LinkBuilder implements Builder<Link> {

  private String label;
  private URI location;
  private static final Logger LOGGER = LoggerFactory.getLogger(LinkBuilder.class);

  /**
   * Instantiates a new LinkBuilder, with all mandatory fields.
   *
   * <p>Neither the {@code label} nor the {@code location} can be null or empty. The {@code
   * location} must be a valid URL according to RFC2396.
   *
   * @param label the link label
   * @param location the location of the external resource
   */
  public LinkBuilder(final String label, final URI location) {
    this.label = label;
    this.location = location;
  }

  /**
   * Sets the link label.
   *
   * <p>The label cannot be null or empty.
   *
   * @param label the link label
   * @return the builder, with the updated label
   */
  public LinkBuilder label(final String label) {
    this.label = label;
    return this;
  }

  /**
   * Sets the link location.
   *
   * <p>It cannot be null and it must be a valid URL according to RFC2396.
   *
   * @param location the location of the external resource
   * @return the builder, with the updated location
   */
  public LinkBuilder location(final URI location) {
    this.location = location;
    return this;
  }

  /**
   * Instantiate a new LinkBuilder out of an existing {@code Link} instance.
   *
   * <p>All objects are immutable and, therefore cannot be updated. This method is a convenience
   * method that creates a new builder with the same values as the supplied {@code Link}. The
   * setters methods of the builder can then be used to update some fields before calling the {@code
   * build} method.
   *
   * @param link the link from which the values will be copied
   * @return a new LinkBuilder
   */
  public static LinkBuilder of(final Link link) {
    return new LinkBuilder(link.getLabel(), link.getLocation());
  }

  /**
   * Builds a new immutable instance of the {@code Link} interface.
   *
   * @return a new immutable instance of the Link interface
   */
  @Override
  public Link build() {
    final Link obj = new LinkImpl(label, location);
    LOGGER.debug(
        Markers.MODEL.getMarker(),
        "{} | {} | Built {}",
        Actions.CREATE,
        StatusCodes.OK.getCode(),
        obj);
    return obj;
  }

  private static final class LinkImpl implements Link {

    private static final long serialVersionUID = 3936634038165277263L;
    private final String label;
    private final URI location;
    private final transient int hashcode;

    LinkImpl(final String label, final URI location) {
      super();
      this.label = label;
      this.location = location;
      hashcode = Objects.hash(label, location);
    }

    @Override
    public String getLabel() {
      return label;
    }

    @Override
    public URI getLocation() {
      return location;
    }

    @SuppressWarnings("PMD.LawOfDemeter")
    @Override
    public boolean equals(final Object obj) {
      if (obj == null) {
        return false;
      }
      if (!(obj instanceof Link)) {
        return false;
      }
      final Link other = (Link) obj;
      return Objects.equals(this.label, other.getLabel())
          && Objects.equals(this.location, other.getLocation());
    }

    @Override
    public int hashCode() {
      return hashcode;
    }

    @Override
    public String toString() {
      return "Link{" + "label=" + label + ", " + "location=" + location + '}';
    }

    private Object writeReplace() {
      return new SerializationProxy(this);
    }

    private void readObject(final ObjectInputStream stream) throws InvalidObjectException {
      throw new InvalidObjectException("Proxy required");
    }

    private static final class SerializationProxy implements Serializable {
      private static final long serialVersionUID = 3936634038165277263L;
      private final String label;
      private final URI location;

      SerializationProxy(final Link link) {
        super();
        this.label = link.getLabel();
        this.location = link.getLocation();
      }

      private Object readResolve() {
        return new LinkImpl(label, location);
      }
    }
  }
}
