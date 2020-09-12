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

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * Markers used in Pinetrail.
 *
 * <p>Markers can be used to enrich log statements and typically address two different needs:
 *
 * <ul>
 *   <li>Triggering specific actions, such as, for example, sending an email in case the database is
 *       down;
 *   <li>Filtering the logs, for example to store all the performance-related log entries in a
 *       separate log file.
 * </ul>
 *
 * <p>For additional information regarding markers, please refer to the <a
 * href="http://www.slf4j.org/docs.html">slf4j documentation</a>.
 *
 * @author Xavier Sosnovsky
 */
public enum Markers {
  /**
   * Marker for log entries related to configuration issues, instantiation of key resources when a
   * system starts up, etc.
   */
  CONFIG(MarkerFactory.getMarker("conf")),
  /** Marker for log entries related to interactions with the persistent storage. */
  DB(MarkerFactory.getMarker("db")),
  /**
   * Marker for log entries related to input/output activities such as writing and reading files.
   */
  IO(MarkerFactory.getMarker("ws.sosna.pinetrail.api.io")),
  /**
   * Marker for log entries related to the management of model objects, such as bean instantiation.
   */
  MODEL(MarkerFactory.getMarker("ws/sosna/pinetrail/model")),
  /**
   * Marker for log entries related to network activities such as retrieving information from 3rd
   * party web services, etc.
   */
  NETWORK(MarkerFactory.getMarker("net")),
  /**
   * Marker for log entries related to performance issues such as the time it took to retrieve data
   * from the database.
   */
  PERFORMANCE(MarkerFactory.getMarker("perf"));

  private final Marker marker;

  Markers(final Marker marker) {
    this.marker = marker;
  }

  /**
   * Gets the marker defined for the selected context.
   *
   * @return the marker for the selected context
   */
  public Marker getMarker() {
    return this.marker;
  }
}
