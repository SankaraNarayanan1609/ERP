package com.Vcidex.StoryboardSystems.Utils.Logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class PerformanceLogger {
    private static final Logger logger = LoggerFactory.getLogger(PerformanceLogger.class);

    private static final ConcurrentMap<String, Long> starts = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, AtomicLong> totals = new ConcurrentHashMap<>();

    public static void start(String key) { starts.put(key, System.currentTimeMillis()); }

    public static void end(String key) {
        Long start = starts.remove(key);
        if (start != null) {
            long elapsed = System.currentTimeMillis() - start;
            totals.computeIfAbsent(key, k -> new AtomicLong()).addAndGet(elapsed);
        }
    }

    public static void reset() {
        starts.clear();
        totals.clear();
    }

    public static void printSummary() {
        if (totals.isEmpty()) {
            logger.info("⏱ No performance data to report.");
            return;
        }
        logger.info("⏱ Performance summary:");
        totals.forEach((key, atomicMs) -> {
            long ms = atomicMs.get();
            String label = (!key.contains("_") && !key.contains(".")) ? key
                    : (key.contains("_") ? key.substring(key.indexOf("_") + 1)
                    : key.substring(key.lastIndexOf(".") + 1));
            logger.info("    {} → {} ms", label, ms);
        });
    }

    /** For rendering inside Extent as a table. */
    public static String[][] dumpAsTable(){
        if (totals.isEmpty()) return new String[][]{{"Operation","ms"},{"(none)","0"}};
        String[][] rows = new String[totals.size() + 1][2];
        rows[0] = new String[]{"Operation","ms"};
        int i = 1;
        for (Map.Entry<String, AtomicLong> e : totals.entrySet()){
            rows[i++] = new String[]{ e.getKey(), String.valueOf(e.getValue().get()) };
        }
        return rows;
    }
}
