package com.Vcidex.StoryboardSystems.Common.Base;

import com.Vcidex.StoryboardSystems.Utils.Config.ConfigManager;
import com.Vcidex.StoryboardSystems.Utils.Reporting.ExtentTestManager;
import com.Vcidex.StoryboardSystems.Utils.Reporting.TestLogger;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
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
        if (testLogger == null) {
            logger.warn("⚠️ No API logging enabled for this action.");
            return;
        }

        if (logNetwork) testLogger.logNetworkRequest();
        if (logResponse) testLogger.logApiResponse();
    }

    // ✅ Find element (NO API logging, just a UI action)
    public WebElement findElement(By locator) {
        try {
            logger.info("🔍 Finding element: {}", locator);
            if (testLogger != null) testLogger.logNetworkRequest(); // ✅ Capture API requests on findElement()
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            logger.error("❌ Element timeout: {}", locator, e);
            captureScreenshot("Element_Timeout_" + System.currentTimeMillis());
            throw new NoSuchElementException("Element not found within timeout: " + locator, e);
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
            handleUnexpectedAlert();
            logTestAction("Click", locator, true, true);
            WebElement element = findElement(locator);
            element.click();

            if (testLogger != null) testLogger.logConsoleLogs(); // ✅ Capture JS Errors
        } catch (Exception e) {
            logger.warn("⚠️ Click failed, capturing screenshot.");
            captureScreenshot("Click_Failure_" + System.currentTimeMillis());
        }
    }

    public void sendKeys(By locator, String text) {
        try {
            logTestAction("Send Keys", locator, true, false);
            WebElement element = findElement(locator);
            element.clear();
            element.sendKeys(text);

            if (testLogger != null) testLogger.logConsoleLogs(); // ✅ Capture JS Errors
        } catch (Exception e) {
            logger.error("❌ Text entry failed for element: {}", locator, e);
            captureScreenshot("SendKeys_Failure_" + System.currentTimeMillis());
            throw new IllegalStateException("Failed to enter text for element: " + locator, e);
        }
    }

    // ✅ Capture screenshot (NO API logging)
    public void captureScreenshot(String fileName) {
        try {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String screenshotPath = "./screenshots/" + fileName + ".png";
            File destination = new File(screenshotPath);

            FileUtils.copyFile(screenshot, destination);
            logger.info("📸 Screenshot saved at: {}", destination.getAbsolutePath());

            ExtentTest test = ExtentTestManager.getTest();
            if (test != null) {
                test.fail("Screenshot on Failure", MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
            }
        } catch (IOException e) {
            logger.error("❌ Screenshot capture failed", e);
        }
    }

    // ✅ Handle Unexpected Alerts
    public void handleUnexpectedAlert() {
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            logger.info("⚠️ Unexpected Alert: {}", alert.getText());
            captureScreenshot("Unexpected_Alert_" + System.currentTimeMillis());
            alert.accept();
        } catch (TimeoutException e) {
            logger.info("No alert present.");
        }
    }

    // ✅ Scroll to element (Triggers console log capture)
    public void scrollToElement(By locator) {
        try {
            logger.info("🎯 Scrolling to element: {}", locator);
            WebElement element = findElement(locator);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
            if (testLogger != null) testLogger.logConsoleLogs();
        } catch (Exception e) {
            logger.error("❌ Failed to scroll to element: {}", locator, e);
            captureScreenshot("Scroll_Failure_" + System.currentTimeMillis());
        }
    }

    // ✅ Check if main UI elements are loaded
    public boolean isElementPresent(By locator) {
        try {
            if (testLogger != null) testLogger.logNetworkRequest(); // ✅ Log API Requests
            return findElement(locator).isDisplayed();
        } catch (Exception e) {
            logger.error("❌ UI Component Missing: {}", locator, e);
            captureScreenshot("UI_Failure_" + System.currentTimeMillis());
            return false;
        }
    }

    // ✅ Wait for Page Load (Triggers API call)
    public void waitForPageLoad() {
        logger.info("⏳ Waiting for page load...");
        if (testLogger != null) testLogger.logNetworkRequest();

        try {
            new WebDriverWait(driver, Duration.ofSeconds(30))
                    .until(webDriver -> ((JavascriptExecutor) webDriver)
                            .executeScript("return document.readyState").equals("complete"));
            logger.info("✅ Page fully loaded.");
        } catch (TimeoutException e) {
            logger.error("❌ Page load timeout!");
            captureScreenshot("PageLoad_Failure_" + System.currentTimeMillis());
            throw e;
        }

        if (!this.isElementPresent(By.id("mainContent"))) {
            logger.error("❌ UI did not load correctly!");
            captureScreenshot("UI_Load_Failure_" + System.currentTimeMillis());
        }
    }
}