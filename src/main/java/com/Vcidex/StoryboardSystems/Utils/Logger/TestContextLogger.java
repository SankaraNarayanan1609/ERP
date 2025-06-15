// TestContextLogger.java
package com.Vcidex.StoryboardSystems.Utils.Logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.UUID;

/**
 * Solely responsible for text-based logging of test events.
 */
public class TestContextLogger {
    private static final Logger logger = LoggerFactory.getLogger(TestContextLogger.class);
    private static final String TEST_RUN_ID = UUID.randomUUID().toString();

    public static void logTestStart(String testName) {
        logger.info("[{}] 🔄 Starting test: {}", TEST_RUN_ID, testName);
    }

    public static void logTestSuccess(String testName) {
        logger.info("[{}] ✅ Test succeeded: {}", TEST_RUN_ID, testName);
    }

    public static void logTestFailure(String testName, Throwable t) {
        logger.info("[{}] ❌ Test failed: {} → {}", TEST_RUN_ID, testName, t.toString());
    }

    public static void logTestSkipped(String testName) {
        logger.info("[{}] ⏭️ Test skipped: {}", TEST_RUN_ID, testName);
    }

    /** General-purpose info, if you need it elsewhere. */
    public static void info(String message) {
        logger.info("[{}] {}", TEST_RUN_ID, message);
    }
}
