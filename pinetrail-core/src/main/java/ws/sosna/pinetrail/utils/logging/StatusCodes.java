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

/**
 * The result of an action.
 *
 * <p>This list is a subset of the HTTP status codes.
 *
 * @author Xavier Sosnovsky
 */
public enum StatusCodes {

  /** The action finished successfully. */
  OK(200),
  /**
   * The action did not result into any change. This can be used for example when trying to register
   * something that has already been registered.
   */
  NOT_MODIFIED(304),
  /**
   * The action cannot be performed because of a syntax error. This can be used, for example, when
   * parsing a non-well-formed XML file.
   */
  SYNTAX_ERROR(400),
  /** No results were found that matched the query. */
  NOT_FOUND(404),
  /**
   * Indicates that the system cannot accept something supplied/requested by the client. This can be
   * used, for example, if the format supplied or requested by the client is not supported.
   */
  NOT_ACCEPTABLE(406),
  /** The action failed, because of an application-related error. */
  INTERNAL_ERROR(500),
  /** The action was aborted because it took too long. */
  TIME_OUT(408);

  private final int code;

  StatusCodes(final int code) {
    this.code = code;
  }

  /**
   * Returns the code to be used in the log entry to summarise the result of the action.
   *
   * @return the code that summarises the result of an action.
   */
  public int getCode() {
    return code;
  }
}
