package com.Vcidex.StoryboardSystems.Utils.Logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class PerformanceLogger {
    private static final Logger logger = LoggerFactory.getLogger(PerformanceLogger.class);

    // start timestamps
    private static final ConcurrentMap<String, Long> starts = new ConcurrentHashMap<>();
    // accumulated durations
    private static final ConcurrentMap<String, AtomicLong> totals = new ConcurrentHashMap<>();

    public static void start(String key) {
        starts.put(key, System.currentTimeMillis());
    }

    public static void end(String key) {
        Long start = starts.remove(key);
        if (start != null) {
            long elapsed = System.currentTimeMillis() - start;
            totals.computeIfAbsent(key, k -> new AtomicLong()).addAndGet(elapsed);
        }
    }

    /**
     * Print a summary of all durations logged so far,
     * handling keys that have no "_" or "." gracefully.
     */
    public static void printSummary() {
        if (totals.isEmpty()) {
            logger.info("⏱ No performance data to report.");
            return;
        }

        logger.info("⏱ Performance summary:");
        totals.forEach((key, atomicMs) -> {
            long ms = atomicMs.get();

            // Derive a more friendly label:
            String label;
            if (key.contains("_")) {
                label = key.substring(key.indexOf("_") + 1);
            } else if (key.contains(".")) {
                label = key.substring(key.lastIndexOf(".") + 1);
            } else {
                label = key;
            }

            logger.info("    {} → {} ms", label, ms);
        });

        // Clear out collected data so next test starts fresh:
        totals.clear();
    }
}