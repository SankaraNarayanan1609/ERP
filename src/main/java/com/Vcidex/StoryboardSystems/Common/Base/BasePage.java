package com.Vcidex.StoryboardSystems.Common.Base;

import com.Vcidex.StoryboardSystems.Utils.Config.ConfigManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;
import java.io.IOException;
import java.time.Duration;

public class BasePage {
    protected static final Logger logger = LogManager.getLogger(BasePage.class);
    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(getTimeoutFromConfig()));
    }

    private static int getTimeoutFromConfig() {
        String timeout = ConfigManager.getProperty("WebDriver.timeout", "10");
        try {
            return Integer.parseInt(timeout);
        } catch (NumberFormatException e) {
            logger.warn("Invalid timeout value in config. Using default value of 10 seconds.");
            return 10;
        }
    }

    public WebElement findElement(By locator) {
        try {
            logger.info("🔍 Finding element: {}", locator);
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            logger.error("❌ Timeout while finding element: {}", locator, e);
            throw new TimeoutException("Element not found within timeout: " + locator, e);
        }
    }

    public String getText(By locator) {
        return findElement(locator).getText();
    }

    public void enterText(By locator, String text) {
        WebElement element = findElement(locator);
        element.clear();
        element.sendKeys(text);
    }

    public void selectDropdownUsingVisibleText(By locator, String value) {
        WebElement element = findElement(locator);
        new org.openqa.selenium.support.ui.Select(element).selectByVisibleText(value);
    }

    public void captureScreenshot(String fileName) {
        try {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destination = new File("./screenshots/" + fileName + ".png");
            FileUtils.copyFile(screenshot, destination);
            logger.info("📸 Screenshot saved at: {}", destination.getAbsolutePath());
        } catch (IOException e) {
            logger.error("❌ Screenshot capture failed", e);
        }
    }

    public void waitForElement(By locator, int timeoutSeconds) {
        wait.withTimeout(Duration.ofSeconds(timeoutSeconds))
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
}