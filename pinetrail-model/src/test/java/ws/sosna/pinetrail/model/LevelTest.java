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
package ws.sosna.pinetrail.model;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Xavier Sosnovsky
 */
public class LevelTest {
    
  @Test
  public void levelFromLowRating() {
      assertEquals(Level.BEGINNER, Level.getLevelFromRating(1));
  }
 
  @Test
  public void levelFromMidRating() {
      assertEquals(Level.INTERMEDIATE, Level.getLevelFromRating(7));
  } 
  
  @Test
  public void levelFromHighRating() {
      assertEquals(Level.ADVANCED, Level.getLevelFromRating(10));
  }
}
