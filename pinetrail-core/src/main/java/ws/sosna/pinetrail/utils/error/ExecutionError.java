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

import org.slf4j.Marker;
import ws.sosna.pinetrail.utils.logging.Actions;
import ws.sosna.pinetrail.utils.logging.StatusCodes;

/**
 * Error thrown when one of the Pinetrail processes fails to complete successfully.
 *
 * <p>The error should contain enough information so as to be useful to the upper layers (e.g.:
 * GUI).
 *
 * @author Xavier Sosnovsky
 */
public final class ExecutionError extends RuntimeException {

  private final Marker marker;
  private final Actions action;
  private final StatusCodes errorCode;

  /**
   * Instantiates a new execution error with the specified message and cause.
   *
   * @param message the error message
   * @param cause the error cause
   * @param marker the module where the error happened
   * @param action the action performed when the error happened
   * @param errorCode the errorCode
   */
  public ExecutionError(
      final String message,
      final Throwable cause,
      final Marker marker,
      final Actions action,
      final StatusCodes errorCode) {
    super(message, cause);
    this.marker = marker;
    this.action = action;
    this.errorCode = errorCode;
  }

  /**
   * Returns the module where the error occurred.
   *
   * @return the module where the error occurred
   */
  public Marker getMarker() {
    return marker;
  }

  /**
   * Returns the action performed when the error occurred.
   *
   * @return the action performed when the error occurred
   */
  public Actions getAction() {
    return action;
  }

  /**
   * Returns the code describing the error.
   *
   * @return the code describing the error
   */
  public StatusCodes getErrorCode() {
    return errorCode;
  }
}
