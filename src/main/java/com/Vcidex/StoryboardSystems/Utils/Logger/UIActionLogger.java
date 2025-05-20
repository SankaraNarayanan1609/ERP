// UIActionLogger.java
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

import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.CodeLanguage;


public class UIActionLogger {
    private static final Logger logger = LoggerFactory.getLogger(UIActionLogger.class);

    public static void click(WebDriver driver, By locator, String name) {
        boolean visible = false;
        try { visible = driver.findElement(locator).isDisplayed(); } catch (Exception ignore) {}
        logger.debug("UI Click → {} | {} | visible={}", name, locator, visible);

        ExtentTestManager.getTest()
                .createNode("Click: " + name)
                .info("Locator: " + locator);

        driver.findElement(locator).click();
    }

    public static void type(WebDriver driver, By locator, String value, String name) {
        boolean visible = false;
        try { visible = driver.findElement(locator).isDisplayed(); } catch (Exception ignore) {}
        logger.debug("UI Type → {} | '{}' | {} | visible={}", name, value, locator, visible);

        ExtentTestManager.getTest()
                .createNode("Type: " + name)
                .info("Value: '" + value + "' into " + locator);

        driver.findElement(locator).sendKeys(value);
    }

    public static void submit(WebDriver driver, By locator, String pageName) {
        logger.debug("UI Submit → Page: {} | {}", pageName, locator);

        ExtentTestManager.getTest()
                .createNode("Submit on: " + pageName)
                .info("Locator: " + locator);

        driver.findElement(locator).submit();
    }

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

    public static void debug(String message) {
        logger.debug(message);
    }

    public static void info(String message) {
        logger.info(message);
    }

    public static void snapshot(WebDriver driver, String context) {
        try {
            String html = driver.getPageSource();
            Markup domBlock = MarkupHelper.createCodeBlock(
                    "<!-- DOM snapshot: " + context + " -->\n" + html,
                    "html"
            );
            ExtentTestManager.getTest().info(domBlock);
        } catch (Exception e) {
            logger.warn("Could not capture DOM snapshot: {}", e.getMessage());
        }
    }

    private static void captureScreenshot(WebDriver driver, String name) {
        try {
            Path targetDir = Path.of("logs", "screenshots");
            Files.createDirectories(targetDir);
            String fileName = name.replaceAll("\\W+","_")
                    + "_" + System.currentTimeMillis() + ".png";
            Path target = targetDir.resolve(fileName);
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