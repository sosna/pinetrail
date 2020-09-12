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

import io.jenetics.jpx.GPX.Version;
import ws.sosna.pinetrail.api.io.Formats;
import ws.sosna.pinetrail.api.io.Reader;
import ws.sosna.pinetrail.api.io.ReaderProvider;

/**
 * Provider of services reading/writing from/to GPX 1.0 files.
 *
 * <p>This implementation is immutable, and so are the readers returned by this provider.
 *
 * @author Xavier Sosnovsky
 */
public final class Gpx10Provider implements ReaderProvider {

  /** Creates a new provider of GPX 1.0 readers. */
  public Gpx10Provider() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  public Reader newReader(final Formats format) {
    return Formats.GPX_1_0 == format ? new GpxReader(Version.V10) : null;
  }
}
