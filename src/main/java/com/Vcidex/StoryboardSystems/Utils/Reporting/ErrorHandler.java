package com.Vcidex.StoryboardSystems.Utils.Reporting;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.text.StringEscapeUtils;
import org.openqa.selenium.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Supplier;

public class ErrorHandler {

    private static final String basePath = System.getProperty("user.dir") + "/test-output/";
    private static final Logger logger = LogManager.getLogger(ErrorHandler.class);

    private static final Set<String> loggedMessages = new HashSet<>();
    private static final Map<String, Integer> logFrequency = new HashMap<>();
    private static final boolean captureScreenshotsOnFailureOnly = Boolean.parseBoolean(
            System.getProperty("capture.screenshots.on.failure.only", "true")
    );

    private static final int MAX_INFO_LOGS = 10;
    private static int infoLogCount = 0;

    // ===========================
    //          LOGGING
    // ===========================

    public static void log(String level, String message, boolean toExtent, String actionName) {
        if (shouldSkipLog(level, message)) return;

        message = normalizeLogMessage(message);
        loggedMessages.add(message);  // Avoid duplicate logs

        switch (level.toUpperCase()) {
            case "INFO":
                logger.info("ℹ️ " + message);
                break;
            case "ERROR":
                logger.error("❗ " + message);
                break;
            case "WARN":
                logger.warn("⚠️ " + message);
                break;
            case "DEBUG":
                logger.debug("🐞 " + message);
                break;
            default:
                logger.info("Unknown log level: " + level + " - " + message);
        }

        if (toExtent) logToExtent(message, level.equalsIgnoreCase("ERROR"), actionName);
    }

    private static boolean shouldSkipLog(String level, String message) {
        if (message.matches(".*(Clicking on element|Element found).*")) return true;

        if (level.equalsIgnoreCase("INFO")) {
            if (infoLogCount >= MAX_INFO_LOGS) return true;
            infoLogCount++;
        }

        return loggedMessages.contains(normalizeLogMessage(message));
    }

    private static String normalizeLogMessage(String message) {
        return message.replaceAll("\\d{13,}", "{TIMESTAMP}")
                .replaceAll("(?i)session id: .*", "")
                .replaceAll("(?i)driver info: .*", "");
    }

    private static void logToExtent(String message, boolean isFailure, String actionName) {
        try {
            String callerMethod = (actionName != null && !actionName.isEmpty()) ? actionName : getCallerMethodName();
            String logMessage = "<details><summary><b>"
                    + (isFailure ? "❗ Error in " : "ℹ️ Info in ")
                    + callerMethod + "</b></summary>"
                    + "<pre>" + StringEscapeUtils.escapeHtml4(message) + "</pre></details>";

            if (isFailure) {
                ExtentTestManager.getTest().fail(logMessage);
            } else {
                ExtentTestManager.getTest().info(logMessage);
            }
        } catch (Exception e) {
            logger.error("Failed to log to Extent: " + e.getMessage());
        }
    }

    private static String getCallerMethodName() {
        for (StackTraceElement element : new Throwable().getStackTrace()) {
            if (!element.getClassName().contains("ErrorHandler")) {
                return element.getMethodName();
            }
        }
        return "UnknownMethod";
    }

    // ===========================
    //         SAFE EXECUTE
    // ===========================

    public static <T> T executeSafely(WebDriver driver, Supplier<T> action, String actionName) {
        try {
            return action.get();
        } catch (Exception e) {
            captureScreenshot(driver, actionName, ScreenshotStatus.AFTER_SUBMIT_FAIL);
            handleException(driver, e, actionName);
            throw new RuntimeException("❌ Failed to execute: " + actionName, e);
        }
    }

    private static void handleException(WebDriver driver, Exception e, String actionName) {
        String errorMessage = "❌ Action Failed: " + actionName + " | " + extractRootCause(e);
        log("ERROR", errorMessage, true, actionName);
    }

    private static String extractRootCause(Throwable throwable) {
        Throwable root = throwable;
        while (root.getCause() != null) {
            root = root.getCause();
        }
        return root.getMessage().split("\\R")[0];
    }

    // ===========================
    //        SCREENSHOTS
    // ===========================

    public static void captureScreenshot(WebDriver driver, String fileName, ScreenshotStatus status) {
        if (!(driver instanceof TakesScreenshot)) {
            log("ERROR", "WebDriver does not support screenshots.", true, "");
            return;
        }

        if (status == ScreenshotStatus.AFTER_SUBMIT_PASS && captureScreenshotsOnFailureOnly) return;

        ensureDirectoryExists(basePath + "screenshots/");

        try {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
            String screenshotPath = basePath + "screenshots/" + status.name() + "_" + fileName + "_" + timestamp + ".png";
            FileUtils.copyFile(screenshot, new File(screenshotPath));

            log("INFO", "📸 Screenshot captured: " + screenshotPath, true, fileName);
        } catch (Exception e) {
            log("ERROR", "Failed to capture screenshot: " + e.getMessage(), true, fileName);
        }
    }

    public static void ensureDirectoryExists(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists() && !dir.mkdirs()) {
            log("ERROR", "Failed to create directory: " + dirPath, true, "");
        }
    }

    public enum ScreenshotStatus {
        PASS, FAIL, AFTER_SUBMIT_FAIL, AFTER_SUBMIT_PASS;
    }
}