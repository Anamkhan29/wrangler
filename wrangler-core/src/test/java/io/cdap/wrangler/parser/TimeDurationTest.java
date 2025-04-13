package io.cdap.wrangler.parser;

import io.cdap.wrangler.api.parser.TimeDuration;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TimeDurationTest {
    @Test
    public void testTimeDurationParsing() {
        assertEquals(1_000_000L, new TimeDuration("1ms").getNanoseconds());
        assertEquals(1_500_000_000L, new TimeDuration("1.5s").getNanoseconds());
        assertEquals(3_600_000_000_000L, new TimeDuration("1h").getNanoseconds());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidTimeDuration() {
        new TimeDuration("1xs");
    }
}
