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
package ws.sosna.pinetrail.api.io;

import java.util.EnumMap;
import java.util.Map;
import java.util.ServiceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.sosna.pinetrail.utils.logging.Actions;
import ws.sosna.pinetrail.utils.logging.Markers;
import ws.sosna.pinetrail.utils.logging.StatusCodes;

/**
 * Utility class that instantiates writers for the supported formats.
 *
 * <p>In the background, this class uses a ServiceLoader to register the {@code WriterProviders}
 * that will be used to instantiate the {@code Writers} returned to the client.
 *
 * @author Xavier Sosnovsky
 */
public enum Writers {

  /** Singleton instance of Pinetrail writers. */
  INSTANCE;

  private final Map<Formats, WriterProvider> providers;
  private final Logger LOGGER = LoggerFactory.getLogger(Writers.class);
  private final ServiceLoader<WriterProvider> loader;

  Writers() {
    LOGGER.info(
        Markers.CONFIG.getMarker(),
        "{} | {} | Created a registry " + "for accessing writers services.",
        Actions.CREATE,
        StatusCodes.OK.getCode());
    this.providers = new EnumMap<>(Formats.class);
    loader = ServiceLoader.load(WriterProvider.class);
  }

  /**
   * Returns a writer that will process the work item.
   *
   * @param format the output format
   * @return a writer that will perform the supplied work
   * @throws UnsupportedOperationException if there is no provider of writers for the supplied
   *     format.
   */
  public Writer newWriter(final Formats format) {
    if (!(providers.containsKey(format))) {
      for (final WriterProvider tmpProvider : loader) {
        if (null != tmpProvider.newWriter(format)) {
          registerProvider(format, tmpProvider);
          break;
        }
      }
    }
    final WriterProvider provider = providers.get(format);
    if (null == provider) {
      LOGGER.warn(
          Markers.IO.getMarker(),
          "{} | {} | Could not find a writer for {}.",
          Actions.GET,
          StatusCodes.NOT_FOUND.getCode(),
          format);
      throw new UnsupportedOperationException("Could not find a writer for " + format);
    } else {
      LOGGER.debug(
          Markers.IO.getMarker(),
          "{} | {} | Returning a writer for {}.",
          Actions.GET,
          StatusCodes.OK.getCode(),
          format);
      return provider.newWriter(format);
    }
  }

  private void registerProvider(final Formats format, final WriterProvider provider) {
    providers.putIfAbsent(format, provider);
    LOGGER.info(
        Markers.CONFIG.getMarker(),
        "{} | {} | Registered a " + "provider of writers for {} ({}).",
        Actions.REGISTER,
        StatusCodes.OK.getCode(),
        format,
        provider.getClass().getCanonicalName());
  }
}
