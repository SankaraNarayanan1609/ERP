// File: src/main/java/com/Vcidex/StoryboardSystems/Utils/Logger/DiagnosticsLogger.java
package com.Vcidex.StoryboardSystems.Utils.Logger;

import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

import java.nio.file.Path;

public class DiagnosticsLogger {
    /** Screenshot + SLF4J + embed in Extent + console & token logs. */
    public static void onFailure(WebDriver driver, String context) {
        Path img = ScreenshotHelper.capture(driver, context);
        org.slf4j.LoggerFactory.getLogger(DiagnosticsLogger.class)
                .error("Screenshot saved: {}", img);

        ExtentTest node = ReportManager.getTest();
        node.addScreenCaptureFromPath(img.toString(), "Failure @ " + context);

        if (Boolean.getBoolean("console.logging.enabled")) {
            driver.manage().logs().get(LogType.BROWSER).forEach((LogEntry entry) ->
                    org.slf4j.LoggerFactory.getLogger(DiagnosticsLogger.class)
                            .error("BROWSER-CONSOLE {} | {}", entry.getLevel(), entry.getMessage())
            );
        }

        // also log session token if desired
        try {
            Object token = ((JavascriptExecutor) driver)
                    .executeScript("return window.localStorage.getItem('token') || window.sessionStorage.getItem('token');");
            org.slf4j.LoggerFactory.getLogger(DiagnosticsLogger.class)
                    .error("Session token @ {}: {}", context, token);
        } catch (Exception ignored) {}
    }
}