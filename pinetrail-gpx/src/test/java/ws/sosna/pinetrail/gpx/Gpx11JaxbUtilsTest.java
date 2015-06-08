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

import java.util.Locale;
import javax.xml.bind.JAXBContext;
import javax.xml.validation.Schema;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 * @author Xavier Sosnovsky
 */
public class Gpx11JaxbUtilsTest {
    
    @BeforeClass
    public static void init() {
        Locale.setDefault(Locale.ENGLISH);
    }
   
    @Test
    public void getGpx11Context() {
        final JAXBContext context1 =
            Gpx11JaxbUtils.valueOf("INSTANCE").getGpx11Context();
        final JAXBContext context2 = Gpx11JaxbUtils.INSTANCE.getGpx11Context();
        assertNotNull(context1);
        assertSame(context1, context2);
    }

    @Test
    public void testGetGpx11Schema() {
        final Schema schema1 = Gpx11JaxbUtils.INSTANCE.getGpx11Schema();
        final Schema schema2 = Gpx11JaxbUtils.INSTANCE.getGpx11Schema();
        assertNotNull(schema1);
        assertSame(schema1, schema2);
    }
    
}
