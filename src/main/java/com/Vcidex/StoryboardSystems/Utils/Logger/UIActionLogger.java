package com.Vcidex.StoryboardSystems.Utils.Logger;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import org.openqa.selenium.*;
import org.openqa.selenium.logging.LogType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static com.Vcidex.StoryboardSystems.Utils.Logger.ExtentTestManager.getTest;

public class UIActionLogger {
    private static final Logger logger = LoggerFactory.getLogger(UIActionLogger.class);

    public static void debug(String message) {
        logger.debug(message);
    }

    // ───── Click ─────────────────────────────────────────
    public static void click(WebDriver driver, By locator, String name) {
        click(driver, locator, name, getTest());
    }

    public static void click(WebDriver driver, By locator, String name, ExtentTest node) {
        logger.debug("Click → {} | {}", name, locator);
        node.info("🖱 Click ▶ " + name);
        driver.findElement(locator).click();
        node.pass("✅ Clicked " + name);
    }

    // ───── Type ─────────────────────────────────────────
    public static void type(WebDriver driver, By locator, String value, String name) {
        type(driver, locator, value, name, getTest());
    }

    public static void type(WebDriver driver, By locator, String value, String name, ExtentTest node) {
        logger.debug("Type → {} | '{}' | {}", name, value, locator);
        node.info("⌨️ Type ▶ " + name + " : '" + value + "'");
        WebElement el = driver.findElement(locator);
        el.clear();
        el.sendKeys(value);
        node.pass("✅ Typed '" + value + "' into " + name);
    }

    // ───── Submit ────────────────────────────────────────
    public static void submit(WebDriver driver, By locator, String label) {
        submit(driver, locator, label, getTest());
    }

    public static void submit(WebDriver driver, By locator, String label, ExtentTest node) {
        logger.debug("Submit → {} | {}", label, locator);
        node.info("📤 Submit ▶ " + label);
        driver.findElement(locator).submit();
        node.pass("✅ Submitted " + label);
    }

    // ───── Snapshot & Failure ────────────────────────────
    public static void snapshot(WebDriver driver, String context) {
        try {
            String html = driver.getPageSource();
            Markup domBlock = MarkupHelper.createCodeBlock("<!-- DOM snapshot: " + context + " -->\n" + html, "html");
            getTest().info(domBlock);
        } catch (Exception e) {
            logger.warn("Could not capture DOM snapshot: {}", e.getMessage());
        }
    }

    public static void failure(WebDriver driver, String context) {
        logger.error("UI Failure context: {}", context);
        captureScreenshot(driver, context);
        try {
            driver.manage().logs().get(LogType.BROWSER).forEach(entry ->
                    logger.error("BROWSER-CONSOLE {} | {}", entry.getLevel(), entry.getMessage())
            );
        } catch (Exception e) {
            logger.error("Could not fetch console logs: {}", e.getMessage());
        }
    }

    private static void captureScreenshot(WebDriver driver, String context) {
        try {
            Path targetDir = Path.of("logs", "screenshots");
            Files.createDirectories(targetDir);
            String fileName = context.replaceAll("\\W+", "_") + "_" + System.currentTimeMillis() + ".png";
            Path target = targetDir.resolve(fileName);
            Path temp = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE).toPath();
            Files.copy(temp, target, StandardCopyOption.REPLACE_EXISTING);
            logger.error("Screenshot saved: {}", target);
        } catch (Exception e) {
            logger.error("Screenshot capture failed: {}", e.getMessage());
        }
    }
}