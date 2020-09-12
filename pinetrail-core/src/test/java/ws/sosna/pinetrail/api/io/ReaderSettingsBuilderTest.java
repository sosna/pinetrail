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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/** @author Xavier Sosnovsky */
public class ReaderSettingsBuilderTest {

  @Test
  public void getGroupSubTrails() {
    final ReaderSettings task = new ReaderSettingsBuilder().groupSubTrails(true).build();
    assertTrue(task.groupSubTrails());
  }

  @Test
  public void groupSubTrailsDefaultToFalse() {
    final ReaderSettings task = new ReaderSettingsBuilder().build();
    assertFalse(task.groupSubTrails());
  }

  @Test
  public void getCrossBorder() {
    final ReaderSettings task = new ReaderSettingsBuilder().crossBorder(true).build();
    assertTrue(task.crossBorder());
  }

  @Test
  public void crossBorderDefaultToFalse() {
    final ReaderSettings task = new ReaderSettingsBuilder().build();
    assertFalse(task.crossBorder());
  }
}
