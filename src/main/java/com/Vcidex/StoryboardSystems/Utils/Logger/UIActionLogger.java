package com.Vcidex.StoryboardSystems.Utils.Logger;

import com.Vcidex.StoryboardSystems.Utils.ExtentLogUtil;
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

public class UIActionLogger {
    private static final Logger logger = LoggerFactory.getLogger(UIActionLogger.class);

    /** Interaction logs at DEBUG level */
    public static void click(By locator, String name) {
        logger.debug("UI Click → {} | {}", name, locator);
    }

    public static void type(By locator, String value, String name) {
        logger.debug("UI Type → {} | '{}' | {}", name, value, locator);
    }

    public static void submit(By locator, String pageName) {
        logger.debug("UI Submit → Page: {} | {}", pageName, locator);
    }

    /**
     * On failure: capture screenshot + browser console, at ERROR level.
     */
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

    public static void click(WebDriver driver, By locator, String name) {
        boolean visible = false;
        try { visible = driver.findElement(locator).isDisplayed(); }
        catch (Exception ignore) {}
        logger.debug("UI Click → {} | {} | visible={}", name, locator, visible);
        driver.findElement(locator).click();
    }

    // New helper—call in catch blocks when you need full page source:
    public static void snapshot(WebDriver driver, String context) {
        String html = driver.getPageSource();
        String block = ExtentLogUtil.wrapLog("DOM snapshot: " + context, html);
        ExtentTestManager.getTest().info(block);
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