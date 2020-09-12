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

import java.nio.file.Path;
import java.util.function.BiConsumer;
import ws.sosna.pinetrail.model.Trail;

/**
 * Contract for services that write information about trails to a file.
 *
 * <p>A writer service typically outputs information about a trail to a file in a particular format
 * (GPX, KML, GeoJSON, etc.).
 *
 * <p>Implementers of this interface are expected to report any issue preventing their process to
 * complete successfully using an {@code ExecutionError}.
 *
 * @author Xavier Sosnovsky
 * @see Trail
 * @see ws.sosna.pinetrail.utils.error.ExecutionError
 */
public interface Writer extends BiConsumer<Trail, Path> {

  /**
   * Configure the writer with the supplied settings.
   *
   * @param settings the settings used to configure the writer
   * @return the configured writer
   */
  Writer configure(final WriterSettings settings);

  /**
   * Writes the supplied trail to the supplied location.
   *
   * @param trail the trail to be written
   * @param location the location where the file will be written
   * @throws ws.sosna.pinetrail.utils.error.ExecutionError issue preventing the writing process to
   *     finish successfully.
   */
  @Override
  void accept(final Trail trail, final Path location);
}
