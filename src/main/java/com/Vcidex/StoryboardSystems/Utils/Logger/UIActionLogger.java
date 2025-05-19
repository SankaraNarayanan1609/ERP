package com.Vcidex.StoryboardSystems.Utils.Logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import com.Vcidex.StoryboardSystems.Utils.ExtentLogUtil;
import com.Vcidex.StoryboardSystems.Utils.Logger.ExtentTestManager;

public class UIActionLogger {
    private static final Logger logger = LoggerFactory.getLogger(UIActionLogger.class);

    /** DEBUG-level click with visibility check. */
    public static void click(WebDriver driver, By locator, String name) {
        boolean visible = false;
        try { visible = driver.findElement(locator).isDisplayed(); } catch (Exception ignore) {}
        logger.debug("UI Click → {} | {} | visible={}", name, locator, visible);
        driver.findElement(locator).click();
    }

    /** DEBUG-level type with visibility check. */
    public static void type(WebDriver driver, By locator, String value, String name) {
        boolean visible = false;
        try { visible = driver.findElement(locator).isDisplayed(); } catch (Exception ignore) {}
        logger.debug("UI Type → {} | '{}' | {} | visible={}", name, value, locator, visible);
        driver.findElement(locator).sendKeys(value);
    }

    /** DEBUG-level form submit. */
    public static void submit(WebDriver driver, By locator, String pageName) {
        logger.debug("UI Submit → Page: {} | {}", pageName, locator);
        driver.findElement(locator).submit();
    }

    /** ERROR-level failure hook: screenshot + console logs. */
    public static void failure(WebDriver driver, String context) {
        logger.error("UI Failure context: {}", context);
        captureScreenshot(driver, context);

        try {
            LogEntries entries = driver.manage().logs().get(LogType.BROWSER);
            for (LogEntry e : entries) {
                logger.error("BROWSER-CONSOLE {} | {}", e.getLevel(), e.getMessage());
            }
        } catch (Exception ex) {
            logger.error("Could not fetch console logs: {}", ex.getMessage());
        }
    }

    /** for arbitrary UI-layer debug messages */
    public static void debug(String message) {
        logger.debug(message);
    }

    /** if you need generic info: */
    public static void info(String message) {
        logger.info(message);
    }

    /** Helper to save DOM snapshot into the Extent report. */
    public static void snapshot(WebDriver driver, String context) {
        try {
            String html = driver.getPageSource();
            String block = ExtentLogUtil.wrapLog("DOM snapshot: " + context, html);
            ExtentTestManager.getTest().info(block);
        } catch (Exception e) {
            logger.warn("Could not capture DOM snapshot: {}", e.getMessage());
        }
    }

    private static void captureScreenshot(WebDriver driver, String name) {
        try {
            Path target = Path.of("logs/screenshots", name.replaceAll("\\W+","_") + ".png");
            Files.createDirectories(target.getParent());
            Files.copy(
                    ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE).toPath(),
                    target,
                    StandardCopyOption.REPLACE_EXISTING
            );
            logger.error("Screenshot saved: {}", target);
        } catch (Exception e) {
            logger.error("Screenshot capture failed: {}", e.getMessage());
        }
    }
}