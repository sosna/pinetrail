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
package ws.sosna.pinetrail.utils.error;

import org.junit.Test;
import static org.junit.Assert.*;
import org.slf4j.Marker;
import ws.sosna.pinetrail.utils.logging.Actions;
import ws.sosna.pinetrail.utils.logging.Markers;
import ws.sosna.pinetrail.utils.logging.StatusCodes;

/**
 * @author Xavier Sosnovsky
 */
public class ExecutionErrorTest {

    @Test
    public void getLoggingInfo() {
        final String message = "Sorry, my mistake.";
        final Marker marker = Markers.DB.getMarker();
        final Actions action = Actions.ANALYSE;
        final StatusCodes errorCode = StatusCodes.INTERNAL_ERROR;
        final IllegalArgumentException cause = new IllegalArgumentException();
        final ExecutionError error = new ExecutionError(message, cause,
            marker, action, errorCode);
        assertEquals(marker, error.getMarker());
        assertEquals(action, error.getAction());
        assertEquals(errorCode, error.getErrorCode());
        assertEquals(cause, error.getCause());
        assertEquals(message, error.getMessage());
    }
}
