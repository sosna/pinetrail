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

import java.util.Set;

/**
 * A helper class for all builders of {@code Describable} artefacts.
 *
 * @author Xavier Sosnovsky
 *
 * @param <T> the type of describable builder
 */
class DescribableBuilder<T extends Builder<?>> {

    private String name;
    private String description;
    private Set<Link> links;

    DescribableBuilder(final String name, final String description,
            final Set<Link> links) {
        this.name = name;
        this.description = description;
        this.links = links;
    }

    String getName() {
        return name;
    }

    /**
     * Sets a short title for the object.
     *
     * @param name a short title for the object.
     * @return the builder, with an updated name
     */
    public T name(final String name) {
        this.name = name;
        return (T) this;
    }

    String getDescription() {
        return description;
    }

    /**
     * Sets the detailed description about the object.
     *
     * @param description a detailed description about the object
     * @return the builder, with an updated description
     */
    public T description(final String description) {
        this.description = description;
        return (T) this;
    }

    Set<Link> getLinks() {
        return links;
    }

    /**
     * Sets a collection of online resources about the object.
     *
     * @param links a collection of online resources about the object
     * @return the builder, with an updated collection of links
     */
    public T links(final Set<Link> links) {
        this.links = links;
        return (T) this;
    }
}
