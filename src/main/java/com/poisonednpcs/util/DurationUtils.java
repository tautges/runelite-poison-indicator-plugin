package com.poisonednpcs.util;

import java.time.Duration;

/**
 * Utilities which assist in certain operations affected on {@link Duration}s.
 */
public class DurationUtils {

    public static String durationToSecondsString(Duration duration) {
        if (duration.compareTo(Duration.ZERO) <= 0) {
            return "";
        }
        // TODO: there already exist ways to do this
        long seconds = duration.getSeconds();
        int millis = duration.getNano() / 100000000;
        return String.format("%d.%ds", seconds, millis);
    }

}
