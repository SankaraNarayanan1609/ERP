package com.Vcidex.StoryboardSystems.Utils.Logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;

public class PerformanceLogger {
    private static final Logger logger = LoggerFactory.getLogger(PerformanceLogger.class);
    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final ConcurrentHashMap<String, Long> timers = new ConcurrentHashMap<>();

    private static String ts() {
        return LocalDateTime.now().format(fmt);
    }

    /**
     * Mark start of a time‐sensitive block (e.g. page load)
     */
    public static void start(String key) {
        timers.put(key, System.currentTimeMillis());
        logger.debug("[{}] PERF START → {}", ts(), key);
    }

    /**
     * Mark end & log duration
     */
    public static void end(String key) {
        Long start = timers.remove(key);
        if (start != null) {
            long delta = System.currentTimeMillis() - start;
            logger.debug("PERF {} → {}ms", key, delta);
            summary.merge(key, delta, Long::sum);
        }
    }

    // In TestContextLogger.logTestEnd:
    PerformanceLogger.summary().

    forEach((k, v) ->
            logger.info("Total {} time: {}ms",k,v)
            );
}
}