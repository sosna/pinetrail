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

/**
 * Contract for providers of {@code Reader}s.
 *
 * <p>This is the service provider interface (SPI), introduced with Java SE 6. Implementations
 * should register themselves, by placing a text file called
 * ws.sosna.pinetrail.api.io.ReaderProvider below META-INF/services. See Java Service Provider
 * implementations (SPI) for additional information.
 *
 * @author Xavier Sosnovsky
 */
public interface ReaderProvider {

  /**
   * Returns a new instance of a {@code Reader} implementation if the format is supported, null
   * otherwise.
   *
   * @param format the format of the file to be processed
   * @return a new instance of a {@code Reader} implementation, or null if the format is not
   *     supported by the provider
   */
  Reader newReader(final Formats format);
}
