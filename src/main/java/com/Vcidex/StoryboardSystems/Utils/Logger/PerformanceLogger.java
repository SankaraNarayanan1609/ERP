
package com.Vcidex.StoryboardSystems.Utils.Logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PerformanceLogger {
    private static final Logger logger = LoggerFactory.getLogger(PerformanceLogger.class);
    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final ConcurrentHashMap<String,Long> timers = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String,Long> summary = new ConcurrentHashMap<>();

    private static String ts() {
        return LocalDateTime.now().format(fmt);
    }

    public static void start(String key) {
        timers.put(key, System.currentTimeMillis());
        logger.debug("PERF START → {}", key);
    }

    public static void end(String key) {
        Long start = timers.remove(key);
        if (start != null) {
            long delta = System.currentTimeMillis() - start;
            logger.debug("PERF END   → {} took {}ms", key, delta);
            summary.merge(key, delta, Long::sum);
        }
    }

    /** Call from TestContextLogger.logTestEnd to print cumulative times. */
    public static void printSummary() {
        for (Map.Entry<String,Long> e : summary.entrySet()) {
            logger.info("PERF SUMMARY → {} total {}ms", e.getKey(), e.getValue());
        }
    }
}