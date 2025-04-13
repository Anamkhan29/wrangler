package io.cdap.wrangler.parser;

import io.cdap.wrangler.api.parser.ByteSize;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ByteSizeTest {
    @Test
    public void testByteSizeParsing() {
        assertEquals(1000, new ByteSize("1KB").getBytes());
        assertEquals(1500000, new ByteSize("1.5MB").getBytes());
        assertEquals(1024, new ByteSize("1KiB").getBytes());
        assertEquals(1073741824, new ByteSize("1GiB").getBytes());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidByteSize() {
        new ByteSize("1XB");
    }
}
