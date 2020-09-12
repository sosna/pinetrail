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

import org.junit.Test;
import static org.junit.Assert.*;

/** @author Xavier Sosnovsky */
public class WriterSettingsBuilderTest {

  @Test
  public void overwriteIfExistsDefault() {
    final WriterSettingsBuilder instance = new WriterSettingsBuilder();
    assertTrue(instance.build().overwriteIfExists());
  }

  @Test
  public void overwriteIfExists() {
    final WriterSettingsBuilder instance = new WriterSettingsBuilder();
    instance.overwriteIfExists(false);
    assertFalse(instance.build().overwriteIfExists());
  }

  @Test
  public void prettyPrintingDefault() {
    final WriterSettingsBuilder instance = new WriterSettingsBuilder();
    assertFalse(instance.build().prettyPrinting());
  }

  @Test
  public void prettyPrinting() {
    final WriterSettingsBuilder instance = new WriterSettingsBuilder();
    instance.prettyPrinting(true);
    assertTrue(instance.build().prettyPrinting());
  }

  @Test
  public void writeOutliersDefault() {
    final WriterSettingsBuilder instance = new WriterSettingsBuilder();
    assertFalse(instance.build().writeOutliers());
  }

  @Test
  public void writeOutliers() {
    final WriterSettingsBuilder instance = new WriterSettingsBuilder();
    instance.writeOutliers(true);
    assertTrue(instance.build().writeOutliers());
  }

  @Test
  public void writeIdlePointsDefault() {
    final WriterSettingsBuilder instance = new WriterSettingsBuilder();
    assertFalse(instance.build().writeIdlePoints());
  }

  @Test
  public void writeIdlePoints() {
    final WriterSettingsBuilder instance = new WriterSettingsBuilder();
    instance.writeIdlePoints(true);
    assertTrue(instance.build().writeIdlePoints());
  }

  @Test
  public void writeRouteDefault() {
    final WriterSettingsBuilder instance = new WriterSettingsBuilder();
    assertFalse(instance.build().writeRoute());
  }

  @Test
  public void writeRoute() {
    final WriterSettingsBuilder instance = new WriterSettingsBuilder();
    instance.writeRoute(true);
    assertTrue(instance.build().writeRoute());
  }
}
