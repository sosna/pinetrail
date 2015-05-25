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
package ws.sosna.pinetrail.api.io;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;
import ws.sosna.pinetrail.model.Trail;

/**
 * @author Xavier Sosnovsky
 */
public class TestReaderProvider implements ReaderProvider {

    public TestReaderProvider() {
        super();
    }

    @Override
    public Reader newReader(final Formats format) {
        return Formats.GPX_1_1 == format ? new TestReader() : null;
    }

    private static final class TestReader implements Reader {

        public TestReader() {
            super();
        }

        @Override
        public Reader configure(final ReaderSettings settings) {
            return this;
        }

        @Override
        public Set<Trail> apply(final Path fileLocation) {
            return Collections.EMPTY_SET;
        }
    }
}
