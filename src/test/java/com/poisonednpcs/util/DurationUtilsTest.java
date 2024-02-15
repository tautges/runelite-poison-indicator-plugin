package com.poisonednpcs.util;

import junit.framework.TestCase;

import java.time.Duration;

/** Tests for {@link DurationUtils}. */
public class DurationUtilsTest extends TestCase {

    public void testToSecondsString() {
        Duration duration1 = Duration.ofSeconds(5);
        assertEquals("5.0s", DurationUtils.durationToSecondsString(duration1));

        Duration duration2 = Duration.ofMillis(6497);
        assertEquals("6.4s", DurationUtils.durationToSecondsString(duration2));
    }

    public void testToSecondsStringZeroDuration() {
        assertEquals("", DurationUtils.durationToSecondsString(Duration.ZERO));
    }

}