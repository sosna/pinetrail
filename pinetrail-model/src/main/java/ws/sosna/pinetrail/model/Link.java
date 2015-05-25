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
import java.net.URI;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotBlank;

/**
 * A link to a resource with additional information.
 *
 * <p>
 * This can be used to point to additional information (for example a Wikipedia
 * entry) about a {@code Trail} or a {@code Waypoint}.
 *
 * <p>
 * A new immutable instance can be obtained using a {@link LinkBuilder}:<br>
 * <code>Link link = new LinkBuilder(label, location).build();</code>
 *
 * @see LinkBuilder
 *
 * @author Xavier Sosnovsky
 */
public interface Link extends Serializable {

    /**
     * A label for the link.
     *
     * <p>
     * The link cannot be null or empty.
     *
     * @return the link label
     */
    @NotBlank(message = "{Model.Link.Label.NotBlank}")
    String getLabel();

    /**
     * The location of the online resource.
     *
     * <p>
     * The {@code URI} cannot be null and it must be a valid URL according to
     * RFC2396.
     *
     * @return the location of the external resource
     */
    @NotNull(message = "{Model.Link.Location.NotNull}")
    URI getLocation();
}
