// DiagnosticsLogger.java
package com.Vcidex.StoryboardSystems.Utils.Logger;

import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

import java.nio.file.Path;

/**
 * Captures screenshots and optionally console logs on failure.
 */
public class DiagnosticsLogger {
    /**
     * Capture screenshot + embed in Extent and SLF4J.
     */
    public static void onFailure(WebDriver driver, String context) {
        Path img = ScreenshotHelper.capture(driver, context);
        org.slf4j.LoggerFactory.getLogger(DiagnosticsLogger.class)
                .error("Screenshot saved: {}", img);

        ExtentTest node = ReportManager.getTest();
        node.addScreenCaptureFromPath(img.toString(), "Failure @ " + context);

        if (Boolean.getBoolean("console.logging.enabled")) {
            try {
                for (LogEntry entry : driver.manage().logs().get(LogType.BROWSER)) {
                    org.slf4j.LoggerFactory.getLogger(DiagnosticsLogger.class)
                            .error("BROWSER-CONSOLE {} | {}", entry.getLevel(), entry.getMessage());
                }
            } catch (Exception ignored) {}
        }
    }
}