package com.Vcidex.StoryboardSystems.Utils.Logger;

import com.Vcidex.StoryboardSystems.Utils.Config.ConfigManager;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

import java.nio.file.Path;

public class DiagnosticsLogger {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DiagnosticsLogger.class);

    public static void onFailure(WebDriver driver, String context) {
        Path img = ScreenshotHelper.capture(driver, context);
        log.error("Screenshot saved: {}", img);

        ExtentTest node = ReportManager.getTest();
        node.addScreenCaptureFromPath(img.toString(), "Failure @ " + context);

        if (Boolean.parseBoolean(ConfigManager.getProperty("console.logging.enabled", "false"))) {
            try {
                driver.manage().logs().get(LogType.BROWSER).forEach((LogEntry entry) ->
                        log.error("BROWSER-CONSOLE {} | {}", entry.getLevel(), entry.getMessage())
                );
            } catch (Exception e) {
                log.warn("Could not fetch browser console logs: {}", e.getMessage());
            }
        }

        try {
            Object token = ((JavascriptExecutor) driver)
                    .executeScript("return window.localStorage.getItem('token') || window.sessionStorage.getItem('token');");
            log.error("Session token @ {}: {}", context, token);
        } catch (Exception ignored) {
        }
    }
}