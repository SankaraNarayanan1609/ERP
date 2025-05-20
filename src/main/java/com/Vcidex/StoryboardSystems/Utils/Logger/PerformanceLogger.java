package com.Vcidex.StoryboardSystems.Utils.Logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;

public class PerformanceLogger {
    private static final Logger logger = LoggerFactory.getLogger(PerformanceLogger.class);
    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final ConcurrentHashMap<String,Long> timers  = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String,Long> summary = new ConcurrentHashMap<>();

    private static String ts() {
        return LocalDateTime.now().format(fmt);
    }

    private static String bucketKey(String key) {
        return Thread.currentThread().getId() + ":" + key;
    }

    public static void start(String key) {
        timers.put(bucketKey(key), System.currentTimeMillis());
    }

    public static void end(String key) {
        String b = bucketKey(key);
        Long start = timers.remove(b);
        if (start != null) {
            long delta = System.currentTimeMillis() - start;
            // only log debug when >100ms
            if (delta > 100) {
                logger.debug("PERF → {} took {}ms [{}]", key, delta, ts());
            }
            summary.merge(key, delta, Long::sum);
        }
    }

    public static void printSummary() {
        summary.forEach((k, total) -> {
            String[] parts = k.split(":",2);
            logger.info("PERF SUMMARY → {} total {}ms (thread {})",
                    parts[1], total, parts[0]);
        });
    }
}