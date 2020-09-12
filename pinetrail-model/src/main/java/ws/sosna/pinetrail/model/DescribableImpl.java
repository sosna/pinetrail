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

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Default implementation of the {@link Describable} interface.
 *
 * @author Xavier Sosnovsky
 */
class DescribableImpl implements Describable {

  private final String name;
  private final String description;
  private final Set<Link> links;
  private final int hashCode;

  DescribableImpl(final String name, final String description, final Set<Link> links) {
    this.name = name;
    this.description = description;
    this.links =
        null == links
            ? Collections.emptySet()
            : Collections.unmodifiableSet(new LinkedHashSet<>(links));
    hashCode = Objects.hash(name, description, links);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public Set<Link> getLinks() {
    return links;
  }

  @SuppressWarnings("PMD.LawOfDemeter")
  @Override
  public boolean equals(final Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Describable other = (Describable) obj;
    return Objects.equals(getName(), other.getName())
        && Objects.equals(getDescription(), other.getDescription())
        && Objects.equals(getLinks(), other.getLinks());
  }

  @Override
  public int hashCode() {
    return hashCode;
  }
}
