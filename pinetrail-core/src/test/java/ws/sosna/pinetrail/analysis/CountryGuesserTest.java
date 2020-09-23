package ws.sosna.pinetrail.analysis;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.prefs.Preferences;
import org.junit.Test;
import ws.sosna.pinetrail.model.GpsRecord;

/** @author Xavier Sosnovsky */
public class CountryGuesserTest {

  /** Test of apply method, of class CountryGuesser. */
  @Test
  public void guessCountries() {
    Preferences.userRoot()
        .node("ws.sosna.pinetrail.model.Trail")
        .put("crossBorder", Boolean.toString(false));
    final SortedSet<GpsRecord> points = new TreeSet<>();
    points.add(getPoint(7.9630853701, 50.1181208342, 214.03, "2014-05-18T08:25:32Z"));
    points.add(getPoint(7.9629951809, 50.1181007177, 215.47, "2014-05-18T08:26:14Z"));
    points.add(getPoint(7.9631012119, 50.1183273643, 216.43, "2014-05-18T08:27:02Z"));
    points.add(getPoint(7.9631571192, 50.1184399333, 215.47, "2014-05-18T08:27:09Z"));
    points.add(getPoint(7.9631261062, 50.1186041348, 215.95, "2014-05-18T08:27:26Z"));

    final Set<String> countries = CountryGuesser.valueOf("INSTANCE").apply(points);
    // 0 if offline
    assertTrue(1 == countries.size() || 0 == countries.size());
    if (1 == countries.size()) {
      for (final String country : countries) {
        assertEquals("DE", country);
      }
    }
  }

  private GpsRecord getPoint(final double x, final double y, final double z, final String time) {
    return GpsRecord.of(Instant.parse(time), x, y, z);
  }
}
