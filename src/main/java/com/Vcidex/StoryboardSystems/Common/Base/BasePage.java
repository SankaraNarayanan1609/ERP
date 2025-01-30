package com.Vcidex.StoryboardSystems.Common.Base;

import com.Vcidex.StoryboardSystems.Utils.Config.ConfigManager;
import com.Vcidex.StoryboardSystems.Utils.Reporting.TestLogger;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.time.Duration;
import java.util.List;

public class BasePage {
    protected static final Logger logger = LoggerFactory.getLogger(BasePage.class);
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected TestLogger testLogger;

    static {
        ConfigManager.load("config.properties");  // Load config once
    }

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(getTimeoutFromConfig()));

        // ✅ Initialize TestLogger only for Chrome
        if (driver instanceof ChromeDriver) {
            this.testLogger = new TestLogger((ChromeDriver) driver);
        }
    }

    private static int getTimeoutFromConfig() {
        String timeout = ConfigManager.getProperty("WebDriver.timeout");
        try {
            return timeout != null ? Integer.parseInt(timeout) : 10;
        } catch (NumberFormatException e) {
            logger.warn("Invalid timeout value in config. Using default value.");
            return 10;
        }
    }

    // ✅ Log API Requests (if available)
    private void logTestAction(String action, By locator, boolean logNetwork, boolean logResponse) {
        logger.info("🔹 {} - {}", action, locator);
        if (testLogger == null) return;

        if (logNetwork) testLogger.logNetworkRequest();
        if (logResponse) testLogger.logApiResponse();
    }


    // ✅ Find element (NO API logging, just a UI action)
    public WebElement findElement(By locator) {
        try {
            logger.info("🔍 Finding element: {}", locator);
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (StaleElementReferenceException stale) {
            logger.warn("🔄 Element became stale. Retrying...");
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)); // Retry once
        } catch (Exception e) {
            logger.error("❌ Element not found: {}", locator, e);
            throw new NoSuchElementException("Element not found: " + locator, e);
        }
    }

    // ✅ Find multiple elements (NO API logging)
    public List<WebElement> findElements(By locator) {
        logger.info("🔍 Finding multiple elements: {}", locator);
        return driver.findElements(locator);
    }

    // ✅ Click element (Triggers API call, logs response)
    public void click(By locator) {
        try {
            logTestAction("Click", locator, true, true);
            WebElement element = findElement(locator);
            element.click();
        } catch (ElementClickInterceptedException e) {
            logger.warn("⚠️ Click intercepted, retrying...");
            findElement(locator).click(); // Retry once
        } catch (Exception e) {
            logger.warn("⚠️ Regular click failed, using JavaScript click for {}", locator);
            WebElement element = findElement(locator);
            if (!element.isEnabled()) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
            }
        }
    }

    // ✅ Send keys (Triggers API call but no response needed)
    public void sendKeys(By locator, String text) {
        try {
            logTestAction("Send Keys", locator, true, false);
            WebElement element = findElement(locator);
            element.clear();
            element.sendKeys(text);
        } catch (Exception e) {
            logger.error("❌ Text entry failed for element: {}", locator, e);
            throw new IllegalStateException("Failed to enter text for element: " + locator, e);
        }
    }

    // ✅ Get text (NO API logging, just a UI action)
    public String getText(By locator) {
        try {
            logger.info("🔤 Retrieving text from element: {}", locator);
            return findElement(locator).getText().trim();
        } catch (Exception e) {
            logger.error("❌ Failed to retrieve text from element: {}", locator, e);
            throw new NoSuchElementException("Failed to get text from element: " + locator, e);
        }
    }

    // ✅ Select dropdown (Triggers API call, logs response)
    public void selectDropdownUsingVisibleText(By locator, String visibleText) {
        try {
            logTestAction("Select Dropdown", locator, true, true);
            Select select = new Select(findElement(locator));
            select.selectByVisibleText(visibleText);
        } catch (Exception e) {
            logger.error("❌ Dropdown selection failed for {}", locator, e);
            throw new IllegalStateException("Dropdown selection failed: " + locator, e);
        }
    }

    // ✅ Capture screenshot (NO API logging)
    public void captureScreenshot(String fileName) {
        try {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destination = new File("./screenshots/" + fileName + ".png");
            Files.copy(screenshot.toPath(), destination.toPath());
            logger.info("📸 Screenshot saved at: {}", destination.getAbsolutePath());
        } catch (Exception e) {
            logger.error("❌ Screenshot capture failed", e);
        }
    }

    // ✅ Scroll to element (Triggers console log capture)
    public void scrollToElement(By locator) {
        try {
            logger.info("🎯 Scrolling to element: {}", locator);
            WebElement element = findElement(locator);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
            if (testLogger != null) testLogger.logConsoleLogs();  // ✅ Capture console logs for errors
        } catch (Exception e) {
            logger.error("❌ Failed to scroll to element: {}", locator, e);
        }
    }

    // ✅ Wait for Page Load (Triggers API call)
    public void waitForPageLoad() {
        logger.info("⏳ Waiting for page load...");
        if (testLogger != null) testLogger.logNetworkRequest();
        new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
        logger.info("✅ Page fully loaded.");
    }

    // ✅ Execute JavaScript (Triggers console log capture)
    public void executeJavaScript(String script, Object... args) {
        try {
            logger.info("📜 Executing JavaScript: {}", script);
            JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
            Object result = jsExecutor.executeScript(script, args);
            if (testLogger != null) testLogger.logConsoleLogs();
            logger.info("✅ JS Execution Result: {}", result);
        } catch (Exception e) {
            logger.error("❌ JavaScript execution failed: {}", script, e);
            throw new IllegalStateException("JavaScript execution failed", e);
        }
    }
}