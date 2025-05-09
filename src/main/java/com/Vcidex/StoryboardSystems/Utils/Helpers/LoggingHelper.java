package com.Vcidex.StoryboardSystems.Utils.Helpers;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class LoggingHelper {

    // Create a Logger instance for logging
    private static final Logger logger = LogManager.getLogger(LoggingHelper.class);

    // Method to log messages with emoji for log level and action type
    public static void logWithEmoji(String level, String message, String actionName) {
        String levelEmoji;
        switch (level.toUpperCase()) {
            case "ERROR":
                levelEmoji = "❌";
                break;
            case "WARN":
                levelEmoji = "⚠️";
                break;
            case "INFO":
                levelEmoji = "ℹ️";
                break;
            default:
                levelEmoji = "";
                break;
        }

        String actionEmoji = getEmojiForAction(actionName);
        String formattedMessage = levelEmoji + " " + actionEmoji + " " + message + " | Action: " + actionName;

        log(level, formattedMessage);
    }

    // Internal method to log the message using Log4j
    private static void log(String level, String message) {
        switch (level.toUpperCase()) {
            case "INFO":
                logger.info(message);
                break;
            case "ERROR":
                logger.error(message);
                break;
            case "WARN":
                logger.warn(message);
                break;
            default:
                logger.debug(message);
                break;
        }
    }

    // Get emoji based on action type
    private static String getEmojiForAction(String action) {
        switch (action.toUpperCase()) {
            case "CLICK":
                return "🖱️";
            case "SENDKEYS":
                return "📤";
            case "NAVIGATION":
                return "🧭";
            case "ASSERTION":
                return "🧪";
            case "SCREENSHOT":
                return "📸";
            case "FIND_ELEMENT":
                return "🔍";
            case "TIMER":
                return "⏱️";
            case "START":
            case "DONE":
                return "🚀";
            case "PASS":
                return "✅";
            case "FAIL":
                return "❌";
            default:
                return "";
        }
    }

    // Log a custom message with "CustomAction" as the default action
    public static void logCustomMessage(String level, String message) {
        logWithEmoji(level, message, "CustomAction");
    }

    // Log an error message with a throwable/exception
    public static void logErrorWithException(String message, Throwable exception) {
        logger.error(message, exception);
    }
}