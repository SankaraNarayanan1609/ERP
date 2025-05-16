package com.Vcidex.StoryboardSystems.Utils.Logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class ValidationLogger {
    private static final Logger logger = LoggerFactory.getLogger(ValidationLogger.class);
    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final AtomicInteger counter = new AtomicInteger(0);

    private static String ts() {
        return LocalDateTime.now().format(fmt);
    }

    /** Reset at test setup (@BeforeMethod) */
    public static void reset() {
        counter.set(0);
    }

    public static void assertEquals(String label, String expected, String actual, boolean passed, WebDriver driver) {
        int step = counter.incrementAndGet();
        if (passed) {
            logger.debug("Step#{} {} PASS", step, label);
        } else {
            logger.warn("Step#{} {} FAIL: expected='{}' actual='{}'", step, label, expected, actual);
            ErrorLogger.logException(new AssertionError(label), "Validation:"+label, driver);
        }
    }

    public static void assertTrue(String label, boolean condition) {
        logger.info("[{}] Step#{} | {} → {}", ts(), counter.incrementAndGet(), label,
                condition ? "PASS ✅" : "FAIL ❌");
    }
}