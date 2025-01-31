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
    public WebDriver driver;
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

    // ✅ Select dropdown by visible text
    public void selectDropdownUsingVisibleText(By locator, String visibleText) {
        try {
            WebElement element = findElement(locator);
            Select dropdown = new Select(element);
            dropdown.selectByVisibleText(visibleText);
            logger.info("✅ Selected '{}' in dropdown: {}", visibleText, locator);
        } catch (Exception e) {
            logger.error("❌ Failed to select '{}' in dropdown: {}", visibleText, locator, e);
            captureScreenshot("Dropdown_Selection_Failure_" + System.currentTimeMillis(),
                    "Failed to select option '" + visibleText + "' from dropdown: " + locator);
        }
    }

    // ✅ Enter text into a field
    public void enterTextUsingFollowingSibling(By locator, String text) {
        try {
            WebElement element = findElement(locator);
            element.clear();
            element.sendKeys(text);
            logger.info("✅ Entered text '{}' into: {}", text, locator);
        } catch (Exception e) {
            logger.error("❌ Failed to enter text into: {}", locator, e);
            captureScreenshot("TextEntry_Failure_" + System.currentTimeMillis(),
                    "Failed to enter text into: " + locator);
        }
    }

    // ✅ Get text from an element
    public String getText(By locator) {
        try {
            WebElement element = findElement(locator);
            String text = element.getText().trim();
            logger.info("✅ Extracted text from {}: {}", locator, text);
            return text;
        } catch (Exception e) {
            logger.error("❌ Failed to extract text from: {}", locator, e);
            captureScreenshot("TextExtraction_Failure_" + System.currentTimeMillis(),
                    "Failed to extract text from: " + locator);
            return "";
        }
    }

    // ✅ Find element
    public WebElement findElement(By locator) {
        try {
            logger.info("🔍 Finding element: {}", locator);
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            logger.error("❌ Element timeout: {}", locator, e);
            captureScreenshot("Element_Timeout_" + System.currentTimeMillis(),
                    "Element timeout: " + locator);
            throw new NoSuchElementException("Element not found within timeout: " + locator, e);
        } catch (Exception e) {
            logger.error("❌ Element not found: {}", locator, e);
            captureScreenshot("Element_Not_Found_" + System.currentTimeMillis(),
                    "Element not found: " + locator);
            throw new NoSuchElementException("Element not found: " + locator, e);
        }
    }

    // ✅ Click method for WebElements
    public void click(WebElement element, boolean handleAlert) {
        try {
            if (handleAlert) handleUnexpectedAlert(); // Handle alerts before clicking
            element.click();
            logger.info("✅ Clicked on WebElement.");
        } catch (Exception e) {
            logger.error("❌ Failed to click on WebElement.", e);
            captureScreenshot("Click_Failure_" + System.currentTimeMillis(),
                    "Failed to click on WebElement.");
        }
    }

    // ✅ Capture screenshot
    public void captureScreenshot(String fileName, String message) {
        try {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destination = new File("./screenshots/" + fileName + ".png");
            FileUtils.copyFile(screenshot, destination);
            logger.info("📸 Screenshot saved: {}", destination.getAbsolutePath());

            ExtentTest test = ExtentTestManager.getTest();
            if (test != null) {
                test.fail(message, MediaEntityBuilder.createScreenCaptureFromPath(destination.getAbsolutePath()).build());
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
            captureScreenshot("Unexpected_Alert_" + System.currentTimeMillis(),
                    "Unexpected alert detected!");
            alert.accept();
        } catch (TimeoutException e) {
            logger.info("No alert present.");
        }
    }

    // ✅ Scroll to element
    public void scrollToElement(By locator) {
        try {
            logger.info("🎯 Scrolling to element: {}", locator);
            WebElement element = findElement(locator);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
            if (testLogger != null) testLogger.logConsoleLogs();
        } catch (Exception e) {
            logger.error("❌ Failed to scroll to element: {}", locator, e);
            captureScreenshot("Scroll_Failure_" + System.currentTimeMillis(),
                    "Failed to scroll to element: " + locator);
        }
    }

    // ✅ Check if main UI elements are loaded
    public boolean isElementPresent(By locator) {
        try {
            return findElement(locator).isDisplayed();
        } catch (Exception e) {
            logger.error("❌ UI Component Missing: {}", locator, e);
            captureScreenshot("UI_Failure_" + System.currentTimeMillis(),
                    "UI Component Missing: " + locator);
            return false;
        }
    }

    // ✅ Wait for Page Load
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
            captureScreenshot("PageLoad_Failure_" + System.currentTimeMillis(),
                    "Page did not load completely.");
            throw e;
        }

        if (!this.isElementPresent(By.id("mainContent"))) {
            logger.error("❌ UI did not load correctly!");
            captureScreenshot("UI_Load_Failure_" + System.currentTimeMillis(),
                    "UI did not load correctly.");
        }
    }
}
