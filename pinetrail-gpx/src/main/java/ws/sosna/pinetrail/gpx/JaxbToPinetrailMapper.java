/*
 * Copyright (c) 2015, Xavier Sosnovsky <xso@sosna.ws>
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
package ws.sosna.pinetrail.gpx;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import ws.sosna.pinetrail.model.Trail;

/**
 * Performs mapping to the Pinetrail model.
 *
 * @author Xavier Sosnovsky
 */
abstract class JaxbToPinetrailMapper<T extends Object> {

    protected final ResourceBundle logMessages;
    protected final boolean groupSubTrails;

    JaxbToPinetrailMapper(final boolean groupSubTrails) {
        super();
        logMessages = ResourceBundle.getBundle("GpxLogMessages",
            Locale.getDefault());
        this.groupSubTrails = groupSubTrails;
    }

    abstract Set<Trail> mapToTrails(final T gpx);
}
