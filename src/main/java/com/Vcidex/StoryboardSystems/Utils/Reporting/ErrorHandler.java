package com.Vcidex.StoryboardSystems.Utils.Reporting;

import org.apache.commons.io.FileUtils;
import com.aventstack.extentreports.ExtentTest;
import com.Vcidex.StoryboardSystems.Utils.Reporting.ExtentTestManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.apache.commons.text.StringEscapeUtils;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.function.Supplier;
import java.util.HashSet;
import java.util.Set;

public class ErrorHandler {

    private static final String basePath = System.getProperty("user.dir") + "/test-output/";
    private static final Logger logger = LogManager.getLogger(ErrorHandler.class);

    // Track logged messages to prevent duplicates
    private static final Set<String> loggedMessages = new HashSet<>();

    // ===========================
    //          LOG WRAPPER
    // ===========================
    public static void log(String level, String message, boolean toExtent) {
        log(level, message, toExtent, ""); // Calls the overloaded method with a default action name
    }

    public static void log(String level, String message, boolean toExtent, String actionName) {
        // Skip logging if the message has already been logged
        if (loggedMessages.contains(message)) {
            return; // Skip duplicate
        }

        // Mark the message as logged
        loggedMessages.add(message);

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
                break;
        }
        if (toExtent) logToExtent(message, level.equalsIgnoreCase("ERROR"), actionName);
    }

    // ===========================
    //         EXTENT HELPERS
    // ===========================
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
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        for (StackTraceElement element : stackTrace) {
            if (!element.getClassName().contains("ErrorHandler") &&
                    !element.getClassName().contains("ExtentTestManager") &&
                    !element.getClassName().contains("JavascriptExecutor")) {  // Exclude only JS executor calls
                return element.getMethodName();
            }
        }
        return "UnknownMethod";
    }


    private static void attachScreenshotToExtent(String screenshotPath) {
        try {
            if (ExtentTestManager.getTest() != null) {
                ExtentTestManager.getTest().addScreenCaptureFromPath(screenshotPath);
            } else {
                log("ERROR", "Failed to attach screenshot: ExtentTestManager.getTest() is null", true);
            }
        } catch (Exception e) {
            log("ERROR", "Failed to attach screenshot to Extent: " + e.getMessage(), true);
        }
    }

    // ===========================
    //         SAFE EXECUTE
    // ===========================
    public static void executeSafely(WebDriver driver, Runnable task, String actionName, boolean isSubmit, String locator) {
        executeCommon(driver, () -> {
            task.run();
            return null;
        }, actionName, isSubmit, locator);
    }

    public static <T> T executeSafely(WebDriver driver, Supplier<T> action, String actionName, boolean isSubmit, String locator) {
        return executeCommon(driver, action, actionName, isSubmit, locator);
    }

    private static <T> T executeCommon(WebDriver driver, Supplier<T> action, String actionName, boolean isSubmit, String locator) {
        try {
            if (isSubmit) {
                log("INFO", "Performing submit action: " + actionName + " | Locator: " + locator, true);
                captureScreenshot(driver, actionName, ScreenshotStatus.BEFORE_SUBMIT);
            }

            T result = action.get(); // Perform the action

            // Capture screenshot only on successful submit if enabled
            if (isSubmit && captureScreenshotsOnSuccess) {
                captureScreenshot(driver, actionName, ScreenshotStatus.AFTER_SUBMIT_PASS);
            }

            // Log successful execution (Removed "Executing action" log)
            logToExtent("Successfully executed action: " + actionName + " | Locator: " + locator, false, actionName);

            return result; // Return result if no exception occurs

        } catch (Exception e) {
            // Capture failure screenshot and handle the exception
            captureScreenshot(driver, actionName, ScreenshotStatus.AFTER_SUBMIT_FAIL);
            handleException(driver, e, actionName);

            // Log failure in Extent Reports with function name
            logToExtent("❗ Exception occurred: " + e.getMessage(), true, actionName);

            throw e; // Rethrow the exception to ensure test failure
        }
    }

    public enum ScreenshotStatus {
        PASS, FAIL, ERROR, SKIP, BEFORE_SUBMIT, AFTER_SUBMIT_PASS, AFTER_SUBMIT_FAIL, AFTER_ACTION;
    }

    // ===========================
    //        SCREENSHOTS
    // ===========================

    private static final boolean captureScreenshotsOnSuccess = Boolean.parseBoolean(
            System.getProperty("capture.screenshots.on.success", "true")
    );

    public static void captureScreenshot(WebDriver driver, String fileName, ScreenshotStatus status) {
        if (!(driver instanceof TakesScreenshot)) {
            log("ERROR", "WebDriver does not support screenshots. Driver type: " + driver.getClass().getName(), true);
            return;
        }

        String basePath = System.getProperty("user.dir") + "/test-output/";
        ensureDirectoryExists(basePath + "network-logs/");
        ensureDirectoryExists(basePath + "browser-logs/");
        String screenshotsDir = basePath + "screenshots/";
        ensureDirectoryExists(screenshotsDir);

        try {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
            String screenshotPath = screenshotsDir + status.name() + "_" + fileName + "_" + timestamp + ".png";
            FileUtils.copyFile(screenshot, new File(screenshotPath));
            log("INFO", "📸 Screenshot captured: " + screenshotPath, true);
            attachScreenshotToExtent(screenshotPath);
        } catch (Exception e) {
            log("ERROR", "Failed to capture screenshot: " + e.getMessage(), true);
        }
    }

    public static void ensureDirectoryExists(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                log("ERROR", "Failed to create directory: " + dirPath, true);
            }
        }
    }

    public static void logAPIErrorWithBody(ApiException apiException, String testName) {
        ensureDirectoryExists("./api-errors/");
        String requestBody = apiException.getRequestBody() != null ? apiException.getRequestBody() : "N/A";
        String responseBody = apiException.getResponseBody() != null ? apiException.getResponseBody() : "N/A";
        String apiErrorFile = basePath + "api-errors/" + testName + "_api_error.txt";

        try (FileWriter writer = new FileWriter(apiErrorFile, true)) {
            writer.write(String.format(
                    "Timestamp: %s%nTest: %s%nError: %s%nAPI Request: %s%nAPI Response: %s%n%n",
                    new Date(), testName, apiException.getMessage(), requestBody, responseBody
            ));
        } catch (IOException e) {
            log("ERROR", "Failed to write API error log: " + e.getMessage(), true);
        }
    }

    public static void handleException(WebDriver driver, Throwable throwable, String testName) {
        String errorMessage = "Exception occurred in Test: " + testName + " | " + throwable.getMessage();

        // Check if this message has already been logged
        if (!loggedMessages.contains(errorMessage)) {
            log("ERROR", errorMessage, true);
            loggedMessages.add(errorMessage);
        }

        if (throwable instanceof ApiException) {
            logAPIErrorWithBody((ApiException) throwable, testName);
        } else {
            if (driver != null) {
                if (throwable instanceof ApiException apiException) {
                    logAPIErrorWithBody(apiException, testName);
                } else {
                    if (driver != null) {
                        captureScreenshot(driver, testName, ScreenshotStatus.ERROR);
                        captureBrowserLogs(driver, testName);
                        captureNetworkLogs(driver, testName);
                    }
                }
            }
        }
    }

    public static void captureNetworkLogs(WebDriver driver, String testName) {
        if (driver == null) {
            String warnMessage = "Network logs not captured - WebDriver is null.";
            if (!loggedMessages.contains(warnMessage)) {
                log("WARN", warnMessage, false);
                loggedMessages.add(warnMessage);
            }
            return;
        }

        ensureDirectoryExists(basePath + "network-logs/");
        String networkLogFile = "./network-logs/" + testName + "_network_logs.txt";

        try {
            LogEntries networkLogs = driver.manage().logs().get(LogType.PERFORMANCE);
            StringBuilder sb = new StringBuilder();
            for (LogEntry logEntry : networkLogs) {
                sb.append(new Date(logEntry.getTimestamp())).append(" ")
                        .append(logEntry.getLevel()).append(" ")
                        .append(logEntry.getMessage()).append("\n");
            }
            try (FileWriter logFile = new FileWriter(networkLogFile)) {
                logFile.write(sb.toString());
            }
            log("INFO", "✅ Network logs captured for: " + testName, false);

            // Wrap network logs in a collapsible block
            if (sb.isEmpty()) {
                String warnMessage = "No network logs available for test: " + testName;
                if (!loggedMessages.contains(warnMessage)) {
                    log("WARN", warnMessage, false);
                    loggedMessages.add(warnMessage);
                }
            } else {
                String expandableLog = "<details><summary><b>📡 Network Logs for " + testName + ":</b> Click to expand</summary>"
                        + "<pre>" + StringEscapeUtils.escapeHtml4(sb.toString()) + "</pre></details>";
                logToExtent(expandableLog, false, testName);
            }
        } catch (Exception e) {
            String errorMessage = "⚠️ Failed to capture network logs: " + e.getMessage();
            if (!loggedMessages.contains(errorMessage)) {
                log("ERROR", errorMessage, true);
                loggedMessages.add(errorMessage);
            }
        }
    }

    public static void captureBrowserLogs(WebDriver driver, String testName) {
        if (driver == null) {
            String warnMessage = "Browser logs not captured - WebDriver is null.";
            if (!loggedMessages.contains(warnMessage)) {
                log("WARN", warnMessage, false);
                loggedMessages.add(warnMessage);
            }
            return;
        }

        ensureDirectoryExists(basePath + "browser-logs/");
        String logFilePath = "./browser-logs/" + testName + "_logs.txt";

        try {
            LogEntries logs = driver.manage().logs().get(LogType.BROWSER);
            if (logs.getAll().isEmpty()) {
                String warnMessage = "No browser logs available for test: " + testName;
                if (!loggedMessages.contains(warnMessage)) {
                    log("WARN", warnMessage, false);
                    loggedMessages.add(warnMessage);
                }
                return;
            }

            StringBuilder sb = new StringBuilder();
            for (LogEntry logEntry : logs) {
                sb.append(String.format("[%s] %s - %s%n",
                        new Date(logEntry.getTimestamp()),
                        logEntry.getLevel(),
                        logEntry.getMessage()));
            }

            try (FileWriter logFile = new FileWriter(logFilePath)) {
                logFile.write(sb.toString());
            }
            log("INFO", "✅ Browser logs captured for: " + testName, false);
        } catch (Exception e) {
            String errorMessage = "⚠️ Failed to capture browser logs: " + e.getMessage();
            if (!loggedMessages.contains(errorMessage)) {
                log("ERROR", errorMessage, true);
                loggedMessages.add(errorMessage);
            }
        }
    }

    // ===========================
    //          API EXCEPTION
    // ===========================
    public static class ApiException extends Exception {
        private final String requestBody;
        private final String responseBody;

        public ApiException(String message, String requestBody, String responseBody) {
            super(message);
            this.requestBody = requestBody;
            this.responseBody = responseBody;
        }

        public String getRequestBody() {
            return requestBody;
        }

        public String getResponseBody() {
            return responseBody;
        }
    }
}


