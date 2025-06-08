// RetryLogger.java
package com.Vcidex.StoryboardSystems.Utils.Logger;

/**
 * Handles retry and recovery logging separately.
 */
public class RetryLogger {
    /**
     * Logs a retry attempt.
     */
    public static void retry(String action, int attempt, boolean willRetry) {
        org.slf4j.LoggerFactory.getLogger(RetryLogger.class)
                .warn("Retry #{} for: {} → {}", attempt, action, willRetry ? "will retry" : "giving up");
    }

    /**
     * Logs the outcome of a recovery attempt.
     */
    public static void recovery(String action, boolean success) {
        org.slf4j.LoggerFactory.getLogger(RetryLogger.class)
                .info("{}: {}", success ? "Recovered" : "Recovery failed", action);
    }
}