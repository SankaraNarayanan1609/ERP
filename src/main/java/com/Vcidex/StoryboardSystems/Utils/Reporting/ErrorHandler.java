package com.Vcidex.StoryboardSystems.Utils.Reporting;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import com.aventstack.extentreports.ExtentTest;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.function.Supplier;

public class ErrorHandler {

    private static final Logger logger = LogManager.getLogger(ErrorHandler.class);

    // ===========================
    //         EXTENT HELPERS
    // ===========================
    private static void logToExtentInfo(String message) {
        try {
            ExtentTestManager.getTest().info(message);
        } catch (Exception e) {
            logger.error("Failed to log info to Extent: {}", e.getMessage());
        }
    }

    private static void logToExtentFail(String message) {
        try {
            ExtentTestManager.getTest().fail("<b style='color:red;'>❗ " + message + "</b>");
        } catch (Exception e) {
            logger.error("Failed to log fail to Extent: {}", e.getMessage());
        }
    }

    private static void attachScreenshotToExtent(String screenshotPath) {
        try {
            ExtentTestManager.getTest().addScreenCaptureFromPath(screenshotPath);
        } catch (Exception e) {
            logger.error("Failed to attach screenshot to Extent: {}", e.getMessage());
            logToExtentInfo("❌ Failed to attach screenshot: " + e.getMessage());
        }
    }

    // ===========================
    //         PUBLIC LOG INFO
    // ===========================
    public static void logInfo(WebDriver driver, String message) {
        logger.info("ℹ️ " + message);
        logToExtentInfo("ℹ️ " + message);
    }

    // ===========================
    //         SAFE EXECUTE
    // ===========================
    // Safe Execution Wrapper (Return Values)
    public static <T> T safeExecute(WebDriver driver, Supplier<T> action, String actionName, boolean isSubmit, String locator) {
        try {
            if (isSubmit) {
                logger.info("Performing submit action: {}", actionName); // Added here
                captureScreenshot(driver, actionName, "BeforeSubmit");
            }
            T result = action.get();
            captureScreenshot(driver, actionName, isSubmit ? "AfterSubmit_Pass" : "After_Action");
            return result;
        } catch (Exception e) {
            captureScreenshot(driver, actionName, "AfterSubmit_Fail");
            handleException(driver, e, actionName);
            throw e;
        }
    }

    // Safe Execution Wrapper (No Return Values)
    public static void safeExecute(WebDriver driver, Runnable task, String actionName, boolean isSubmit, String locator) {
        try {
            if (isSubmit) {
                logger.info("Performing submit action: {}", actionName); // Added here
                captureScreenshot(driver, actionName, "BeforeSubmit");
            }
            task.run();
            captureScreenshot(driver, actionName, isSubmit ? "AfterSubmit_Pass" : "After_Action");
        } catch (Exception e) {
            captureScreenshot(driver, actionName, "AfterSubmit_Fail");
            handleException(driver, e, actionName);
        }
    }

    public static void captureScreenshot(WebDriver driver, String fileName, String status) {
        if (driver == null) {
            logger.warn("⚠️ Screenshot not taken - WebDriver is null.");
            return;
        }
        if (!(driver instanceof TakesScreenshot)) {
            logger.error("❌ The provided WebDriver does not support screenshots. Driver type: {}", driver.getClass().getName());
            return;
        }
        String screenshotsDir = System.getProperty("user.dir") + "/test-output/screenshots/";
        ensureDirectoryExists(screenshotsDir);

        try {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String screenshotPath = screenshotsDir + status + "_" + fileName + "_" + System.nanoTime() + ".png";
            File destination = new File(screenshotPath);
            FileUtils.copyFile(screenshot, destination);
            logger.info("📸 Screenshot captured: {}", destination.getAbsolutePath());
            attachScreenshotToExtent(screenshotPath);
        } catch (IOException e) {
            logger.error("❌ Failed to capture screenshot", e);
            logToExtentInfo("❌ Failed to capture screenshot: " + e.getMessage());
        }
    }

    // ===========================
    //       ERROR HANDLING
    // ===========================
    public static void handleException(WebDriver driver, Throwable throwable, String testName) {
        logger.error("❗ Error in Test: {}", testName, throwable);
        logToExtentFail("❗ Error in Test: " + testName + "\n" + throwable.getMessage());
        logToExtentInfo("[UI ERROR] " + throwable.getMessage()); // Added for UI Error Logs

        if (throwable instanceof ApiException) {
            logAPIErrorWithBody((ApiException) throwable, testName);
        } else {
            if (driver != null) {
                captureScreenshot(driver, testName, "Error");
                captureBrowserLogs(driver, testName);
            }
        }
    }

    // ===========================
    //           API LOGS
    // ===========================
    public static void logAPIErrorWithBody(ApiException apiException, String testName) {
        ensureDirectoryExists("./api-errors/");
        String requestBody = apiException.getRequestBody() != null ? apiException.getRequestBody() : "N/A";
        String responseBody = apiException.getResponseBody() != null ? apiException.getResponseBody() : "N/A";
        String apiErrorFile = "./api-errors/" + testName + "_api_error.txt";

        try (FileWriter writer = new FileWriter(apiErrorFile, true)) {
            writer.write("Timestamp: " + new Date() + "\n");
            writer.write("Test: " + testName + "\n");
            writer.write("Error: " + apiException.getMessage() + "\n");
            writer.write("API Request: " + requestBody + "\n");
            writer.write("API Response: " + responseBody + "\n");
            writer.write("====================================\n");
            logger.info("✅ API error logged for: {}", testName);
            logToExtentInfo("[API LOG] API Error Details Captured"); // Added API Log
        } catch (IOException e) {
            logger.error("⚠️ Failed to log API error: {}", e.getMessage());
        }
    }

    // ===========================
//       BROWSER LOGS
// ===========================
    public static void captureBrowserLogs(WebDriver driver, String testName) {
        ensureDirectoryExists("./browser-logs/");
        String logFilePath = "./browser-logs/" + testName + "_logs.txt";

        try {
            LogEntries logs = driver.manage().logs().get(LogType.BROWSER);
            StringBuilder sb = new StringBuilder();
            for (LogEntry logEntry : logs) {
                sb.append(new Date(logEntry.getTimestamp())).append(" ")
                        .append(logEntry.getLevel()).append(" ")
                        .append(logEntry.getMessage()).append("\n");
            }
            try (FileWriter logFile = new FileWriter(logFilePath)) {
                logFile.write(sb.toString());
            }
            logger.info("✅ Browser logs captured for: {}", testName);
            logToExtentInfo("[UI ERROR] Browser Logs Captured for " + testName);
        } catch (Exception e) {
            logger.error("⚠️ Failed to capture browser logs: {}", e.getMessage());
            logToExtentInfo("⚠️ Failed to capture browser logs: " + e.getMessage());
        }
    }

    // ===========================
    //       NETWORK LOGS
    // ===========================
    public static void captureNetworkLogs(WebDriver driver, String testName) {
        ensureDirectoryExists("./network-logs/");
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
            logger.info("✅ Network logs captured for: {}", testName);
            logToExtentInfo("[NETWORK LOG] Network Logs Captured"); // Added Network Log
        } catch (Exception e) {
            logger.error("⚠️ Failed to capture network logs: {}", e.getMessage());
            logToExtentInfo("⚠️ Failed to capture network logs: " + e.getMessage());
        }
    }

    // ===========================
    //   DIRECTORY CHECK HELPER
    // ===========================
    private static void ensureDirectoryExists(String dirPath) {
        File directory = new File(dirPath);
        if (!directory.exists() && !directory.mkdirs()) {
            logger.warn("⚠️ Failed to create directory: {}", dirPath);
        } else {
            logger.info("📂 Directory exists: {}", dirPath);
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
        public String getRequestBody() { return requestBody; }
        public String getResponseBody() { return responseBody; }
    }
}