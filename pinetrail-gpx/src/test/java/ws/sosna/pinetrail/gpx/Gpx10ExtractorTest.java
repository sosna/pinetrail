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
package ws.sosna.pinetrail.gpx;

import com.topografix.gpx._1._0.Gpx;
import java.math.BigDecimal;
import java.nio.file.FileSystems;
import javax.xml.bind.JAXBException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.xml.sax.SAXParseException;
import ws.sosna.pinetrail.utils.error.ExecutionError;

/**
 * @author Xavier Sosnovsky
 */
public class Gpx10ExtractorTest {

    @Test
    public void parseXml() {
        final Gpx10Extractor extractor = new Gpx10Extractor();
        final Gpx extracted = extractor.parseXml(
            FileSystems.getDefault().getPath(".",
                "src/test/resources/test_bike.gpx"));
        assertNotNull(extracted);
        assertEquals(0, extracted.getRte().size());
        assertEquals(0, extracted.getWpt().size());
        assertEquals(1, extracted.getTrk().size());
        final Gpx.Trk trail = extracted.getTrk().get(0);
        assertEquals(1, trail.getTrkseg().size());
        final Gpx.Trk.Trkseg seg = trail.getTrkseg().get(0);
        assertEquals(4000, seg.getTrkpt().size());
        final Gpx.Trk.Trkseg.Trkpt point = seg.getTrkpt().get(3999);
        assertEquals(new BigDecimal("50.001615000"), point.getLat());
        assertEquals(new BigDecimal("8.259343000"), point.getLon());
        assertEquals(new BigDecimal("166.000000"), point.getEle());
    }

    @Test
    public void parseNotWellFormedXml() {
        try {
            final Gpx11Extractor extractor = new Gpx11Extractor();
            extractor.parseXml(FileSystems.
                getDefault().getPath(".", "src/test/resources/2014-05-18_"
                    + "Wispertal_NotWellFormed.gpx"));
        } catch (final ExecutionError error) {
            if (!(error.getCause() instanceof SAXParseException)) {
                fail("Expected another exception type");
            }
        }
    }

    @Test
    public void parseUnmappableXml() {
        try {
            final Gpx11Extractor extractor = new Gpx11Extractor();
            extractor.parseXml(FileSystems.
                getDefault().getPath(".",
                    "src/test/resources/test_bike_not_valid.gpx"));
        } catch (final ExecutionError error) {
            if (!(error.getCause() instanceof JAXBException)) {
                fail("Expected another exception type");
            }
        }
    }

    @Test
    public void fileDoesNotExist() {
        try {
            final Gpx11Extractor extractor = new Gpx11Extractor();
            extractor.parseXml(FileSystems.
                getDefault().getPath(".", "Not_There"));
        } catch (final ExecutionError error) {
            if (null != error.getCause()) {
                fail("Expected another exception type");
            }
        }
    }
}
