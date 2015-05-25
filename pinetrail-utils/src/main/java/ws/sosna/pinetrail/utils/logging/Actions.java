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
 */
package ws.sosna.pinetrail.utils.logging;

import java.util.Locale;

/**
 * The type of action whose result is being logged.
 *
 * @author Xavier Sosnovsky
 */
public enum Actions {

    /**
     * The action of creating something.
     */
    CREATE,
    /**
     * The action of retrieving something.
     */
    GET,
    /**
     * The action of updating something.
     */
    UPDATE,
    /**
     * The action of deleting something.
     */
    DELETE,
    /**
     * The action of registering something.
     */
    REGISTER,
    /**
     * The action of unregistering something.
     */
    UNREGISTER,
    /**
     * The action of parsing something.
     */
    PARSE,
    /**
     * The action of performing some statistical analysis.
     */
    ANALYSE,
    /**
     * The action of validating input.
     */
    VALIDATE,
    /**
     * The action of opening something (like a connection to a service).
     */
    OPEN,
    /**
     * The action of closing something (like a connection to a service).
     */
    CLOSE,
    /**
     * The action of persisting something to a store. Can also be used when it
     * is not clear whether an action creates or updates an object.
     */
    PERSIST;

    @Override
    public String toString() {
        return String.format("%-10s", this.name().
            toLowerCase(Locale.getDefault()));
    }
}
