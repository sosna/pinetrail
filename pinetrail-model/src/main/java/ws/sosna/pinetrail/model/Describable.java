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

import java.io.Serializable;
import java.util.Set;
import javax.validation.Valid;

/**
 * Provides artefacts with basic information such as id, name, description, etc.
 *
 * @author Xavier Sosnovsky
 */
interface Describable extends Serializable {

  /**
   * A short title describing the object.
   *
   * @return a short title for the object
   */
  String getName();

  /**
   * A detailed description about the object.
   *
   * @return a detailed description about the object
   */
  String getDescription();

  /**
   * An immutable collection of links to resources with additional information.
   *
   * <p>Links can be used to point to additional information (for example a Wikipedia entry) about
   * the object.
   *
   * <p>If no link has been attached to the object, an empty collection will be returned.
   *
   * @return a collection of online resources about the object
   */
  @Valid
  Set<Link> getLinks();
}
