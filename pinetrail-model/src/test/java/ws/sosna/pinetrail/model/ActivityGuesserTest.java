package ws.sosna.pinetrail.model;

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


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Xavier Sosnovsky
 */
public class ActivityGuesserTest {

    @Test
    public void guessActivityHiking() {
        final List<Double> list
                = Arrays.asList(4.0, 4.57, 4.63, 5.0, 5.07, 4.88);
        final SummaryStatistics summary = new SummaryStatistics();
        list.stream().forEach(summary::addValue);
        final Statistics stats
                = new StatisticsImpl(summary, summary, summary, summary, 
                        summary, Collections.EMPTY_SET);
        assertSame(Activity.HIKING, 
            ActivityGuesser.valueOf("INSTANCE").apply(stats));
    }
    
    @Test
    public void guessActivityJogging() {
        final List<Double> list
                = Arrays.asList(4.0, 8.57, 9.63, 9.0, 10.07, 7.88);
        final SummaryStatistics summary = new SummaryStatistics();
        list.stream().forEach(summary::addValue);
        final Statistics stats
                = new StatisticsImpl(summary, summary, summary, summary, 
                        summary, Collections.EMPTY_SET);
        assertSame(Activity.JOGGING, ActivityGuesser.INSTANCE.apply(stats));
    }
}
