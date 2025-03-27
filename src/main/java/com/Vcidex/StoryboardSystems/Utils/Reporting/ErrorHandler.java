package com.Vcidex.StoryboardSystems.Utils.Reporting;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.testng.ITestResult;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.function.Supplier;

public class ErrorHandler {

    private static final Logger logger = LogManager.getLogger(ErrorHandler.class);

    // ✅ Universal Logging
    public static void logInfo(WebDriver driver, String message) {
        logger.info("ℹ️ {}", message);
    }

    // ✅ API Exception Class
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

    // ✅ Unified Exception Handling
    public static void handleException(WebDriver driver, Throwable throwable, String testName) {
        logger.error("❗ Error in Test: {}", testName, throwable);

        if (throwable instanceof ApiException) {
            ApiException apiException = (ApiException) throwable;
            logAPIErrorWithBody(apiException, testName);
        } else {
            if (driver != null) {
                captureScreenshot(driver, testName, "Error");
                captureBrowserLogs(driver, testName);
            }
        }
        throw new RuntimeException(throwable); // Rethrow the exception
    }

    // ✅ Capture API Errors with Body
    public static void logAPIErrorWithBody(ApiException apiException, String testName) {
        try (FileWriter writer = new FileWriter("./api-errors/" + testName + "_api_error.txt", true)) {
            writer.write("Test: " + testName + "\n");
            writer.write("Error: " + apiException.getMessage() + "\n");
            writer.write("API Request: " + apiException.getRequestBody() + "\n");
            writer.write("API Response: " + apiException.getResponseBody() + "\n");
            logger.info("✅ API error logged with request/response for: {}", testName);
        } catch (IOException e) {
            logger.error("⚠️ Failed to log API error with body: {}", e.getMessage());
        }
    }

    // ✅ Capture Browser Logs
    public static void captureBrowserLogs(WebDriver driver, String testName) {
        try {
            LogEntries logs = driver.manage().logs().get(LogType.BROWSER);
            try (FileWriter logFile = new FileWriter("./browser-logs/" + testName + "_logs.txt")) {
                for (LogEntry logEntry : logs) {
                    logFile.write(new Date(logEntry.getTimestamp()) + " " + logEntry.getLevel() + " " + logEntry.getMessage() + "\n");
                }
                logger.info("✅ Browser logs captured for: {}", testName);
            }
        } catch (Exception e) {
            logger.error("⚠️ Failed to capture browser logs: {}", e.getMessage());
        }
    }

    // ✅ Safe Execution Wrapper for Actions with Return Values
    public static <T> T safeExecute(WebDriver driver, Supplier<T> action, String actionName, boolean isSubmit, String locator) {
        try {
            if (isSubmit) {
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

    // ✅ Safe Execution Wrapper for Actions without Return Values
    public static void safeExecute(WebDriver driver, Runnable task, String actionName, boolean isSubmit, String locator) {
        try {
            if (isSubmit) {
                captureScreenshot(driver, actionName, "BeforeSubmit");
            }
            task.run();
            captureScreenshot(driver, actionName, isSubmit ? "AfterSubmit_Pass" : "After_Action");
        } catch (Exception e) {
            captureScreenshot(driver, actionName, "AfterSubmit_Fail");
            handleException(driver, e, actionName);
        }
    }

    // ✅ On Test Finish - Central Point
    public static void onTestFinish(ITestResult result, WebDriver driver) {
        if (result.getStatus() == ITestResult.FAILURE) {
            handleException(driver, result.getThrowable(), result.getName());
            result.setStatus(ITestResult.SKIP);
            logger.info("🔄 Skipping to the next test.");
        }
        ExtentTestManager.flushReports();
    }

    // ✅ Capture Screenshot with Status (Pass/Fail/Before Submit)
    public static void captureScreenshot(WebDriver driver, String fileName, String status) {
        if (driver == null) {
            logger.warn("⚠️ Screenshot not taken - WebDriver is null.");
            return;
        }

        try {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String screenshotPath = "./screenshots/" + status + "_" + fileName + "_" + System.nanoTime() + ".png";
            FileUtils.copyFile(screenshot, new File(screenshotPath));
            logger.info("📸 Screenshot captured: {}", screenshotPath);
        } catch (IOException e) {
            logger.error("❌ Failed to capture screenshot", e);
        }
    }
}