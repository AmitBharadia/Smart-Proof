package com.kit.verifier.zeroknowledge.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class TimerUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimerUtil.class);

    private TimerUtil() {
    }

    public static <T> T timeAndLog(final String phase, Supplier<T> supplier) {
        final long startTime = System.nanoTime();
        try {
            return supplier.get();
        } finally {
            final long endTime = System.nanoTime();
            final long millis = (endTime - startTime) / 1000000L;
            logTime(phase, millis);
        }
    }

    private static void logTime(String label, long millisElapsed) {
        LOGGER.info(label + " took " + millisElapsed + " milliseconds");
    }
}
