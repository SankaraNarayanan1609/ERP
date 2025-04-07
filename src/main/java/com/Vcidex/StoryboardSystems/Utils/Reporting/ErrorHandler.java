package com.Vcidex.StoryboardSystems.Utils.Reporting;

import org.apache.commons.io.FileUtils;
import com.Vcidex.StoryboardSystems.Utils.WebDriverFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.text.StringEscapeUtils;
import org.openqa.selenium.*;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.model.Media;
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

            // Check if there’s a screenshot captured just before this call
            String screenshotPath = captureScreenshotWithCallerName();
            if (screenshotPath != null) {
                Media media = MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build();
                if (isFailure) {
                    ExtentTestManager.getTest().fail(logMessage, media);
                } else {
                    ExtentTestManager.getTest().info(logMessage, media);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to log to Extent: " + e.getMessage());
        }
    }

    public class TestExtent {
        public static void main(String[] args) throws Exception {
            Media media = MediaEntityBuilder.createScreenCaptureFromPath("screenshot.png").build();
            System.out.println("Media entity created: " + media);
        }
    }
    private static String getCallerMethodName() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        for (int i = 2; i < stackTrace.length; i++) {
            String className = stackTrace[i].getClassName();

            if (!className.contains("ErrorHandler") && !className.contains("Thread")) {
                return stackTrace[i].getMethodName();
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

    private static String getLatestScreenshotPathForAction(String actionName) {
        File screenshotsDir = new File(basePath + "screenshots/");
        if (!screenshotsDir.exists() || !screenshotsDir.isDirectory()) return null;

        File[] matchingFiles = screenshotsDir.listFiles((dir, name) -> name.contains(actionName));
        if (matchingFiles == null || matchingFiles.length == 0) return null;

        Arrays.sort(matchingFiles, Comparator.comparingLong(File::lastModified).reversed());

        // Only return the latest file path relative to the report
        return "screenshots/" + matchingFiles[0].getName(); // For HTML compatibility
    }

    private static String captureScreenshotWithCallerName() {
        try {
            WebDriver driver = WebDriverFactory.getDriver();//Expected 2 arguments but found 0
            if (!(driver instanceof TakesScreenshot)) {
                log("ERROR", "WebDriver does not support screenshots.", true, "");
                return null;
            }

            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String methodName = getCallerMethodName();
            String screenshotName = "screenshot_" + methodName + "_" + timestamp + ".png";

            String screenshotDir = basePath + "screenshots/";
            ensureDirectoryExists(screenshotDir);

            String screenshotPath = screenshotDir + screenshotName;
            File dest = new File(screenshotPath);
            FileUtils.copyFile(src, dest);

            return "screenshots/" + screenshotName; // Relative path for HTML compatibility
        } catch (Exception e) {
            logger.error("⚠️ Failed to capture screenshot: {}", e.getMessage(), e);
            return null;
        }
    }

    public enum ScreenshotStatus {
        PASS, FAIL, AFTER_SUBMIT_FAIL, AFTER_SUBMIT_PASS;
    }
}