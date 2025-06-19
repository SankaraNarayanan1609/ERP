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
        try {
            Path img = ScreenshotHelper.capture(driver, context);
            log.error("🖼️ Screenshot saved: {}", img);

            ExtentTest node = ReportManager.getTest();
            node.addScreenCaptureFromPath(img.toString(), "Failure @ " + context);

            // 🔄 Backup: embed base64 for HTML-safe inline display
            try {
                String base64 = ScreenshotHelper.captureBase64(driver); // Cannot resolve method 'captureBase64' in 'ScreenshotHelper'
                node.addScreenCaptureFromBase64String(base64, "📸 Embedded Screenshot");
            } catch (Exception e) {
                log.warn("⚠️ Could not attach base64 screenshot: {}", e.getMessage());
            }

            // 🧪 Session/local storage token logging
            try {
                Object token = ((JavascriptExecutor) driver)
                        .executeScript("return window.localStorage.getItem('token') || window.sessionStorage.getItem('token');");
                log.error("🪪 Session token @ {}: {}", context, token);
            } catch (Exception ignored) {
            }

            // 🌐 Browser console logs (configurable)
            if (Boolean.parseBoolean(ConfigManager.getProperty("console.logging.enabled", "false"))) {
                try {
                    driver.manage().logs().get(LogType.BROWSER).forEach((LogEntry entry) ->
                            log.error("🌐 CONSOLE {} | {}", entry.getLevel(), entry.getMessage())
                    );
                } catch (Exception e) {
                    log.warn("⚠️ Could not fetch browser console logs: {}", e.getMessage());
                }
            }

        } catch (Exception e) {
            log.error("❌ Diagnostics failed during onFailure for '{}': {}", context, e.getMessage(), e);
        }
    }
}