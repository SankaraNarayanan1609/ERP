package com.Vcidex.StoryboardSystems.Common.Base;

import com.Vcidex.StoryboardSystems.Utils.Config.ConfigManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
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
    protected Actions actions;
    protected JavascriptExecutor jsExecutor;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(getTimeoutFromConfig()));
        this.actions = new Actions(driver);
        this.jsExecutor = (JavascriptExecutor) driver;
    }

    private static int getTimeoutFromConfig() {
        try {
            return Integer.parseInt(ConfigManager.getProperty("WebDriver.timeout", "10"));
        } catch (NumberFormatException e) {
            logger.warn("⚠️ Invalid timeout value in config. Using default value of 10 seconds.");
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
        try {
            WebElement element = findElement(locator);
            String text = element.getText();
            logger.info("📄 Retrieved text: '{}' from element: {}", text, locator);
            return text;
        } catch (Exception e) {
            logger.error("❌ Failed to retrieve text from element: {}", locator, e);
            throw new RuntimeException("Error retrieving text from element: " + locator, e);
        }
    }

    public void enterText(By locator, String text) {
        try {
            WebElement element = findElement(locator);
            element.clear();
            element.sendKeys(text);
            logger.info("⌨️ Entered text '{}' into element: {}", text, locator);
        } catch (Exception e) {
            logger.error("❌ Failed to enter text '{}' into element: {}", text, locator, e);
            throw new RuntimeException("Error entering text in element: " + locator, e);
        }
    }

    public void click(By locator) {
        try {
            WebElement element = findElement(locator);
            element.click();
            logger.info("🖱️ Clicked on element: {}", locator);
        } catch (Exception e) {
            logger.error("❌ Failed to click on element: {}", locator, e);
            throw new RuntimeException("Error clicking on element: " + locator, e);
        }
    }

    public void selectDropdownUsingVisibleText(By locator, String value) {
        try {
            WebElement element = findElement(locator);
            new org.openqa.selenium.support.ui.Select(element).selectByVisibleText(value);
            logger.info("📂 Selected '{}' from dropdown: {}", value, locator);
        } catch (Exception e) {
            logger.error("❌ Failed to select '{}' from dropdown: {}", value, locator, e);
            throw new RuntimeException("Error selecting value in dropdown: " + locator, e);
        }
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
        try {
            WebDriverWait dynamicWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            dynamicWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            logger.info("⏳ Waited for element: {} for {} seconds", locator, timeoutSeconds);
        } catch (TimeoutException e) {
            logger.error("❌ Timeout while waiting for element: {}", locator, e);
            throw new TimeoutException("Element not visible within timeout: " + locator, e);
        }
    }

    public void scrollIntoView(By locator) {
        try {
            WebElement element = findElement(locator);
            jsExecutor.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
            logger.info("🔽 Scrolled into view: {}", locator);
        } catch (Exception e) {
            logger.error("❌ Failed to scroll into view: {}", locator, e);
            throw new RuntimeException("Error scrolling into view: " + locator, e);
        }
    }

    public void scrollToBottom() {
        try {
            jsExecutor.executeScript("window.scrollTo(0, document.body.scrollHeight);");
            logger.info("🔽 Scrolled to bottom of the page");
        } catch (Exception e) {
            logger.error("❌ Failed to scroll to bottom", e);
            throw new RuntimeException("Error scrolling to bottom of the page", e);
        }
    }

    public void moveToElement(By locator) {
        try {
            WebElement element = findElement(locator);
            actions.moveToElement(element).perform();
            logger.info("👆 Moved to element: {}", locator);
        } catch (Exception e) {
            logger.error("❌ Failed to move to element: {}", locator, e);
            throw new RuntimeException("Error moving to element: " + locator, e);
        }
    }

    public void doubleClick(By locator) {
        try {
            WebElement element = findElement(locator);
            actions.doubleClick(element).perform();
            logger.info("🖱️ Double-clicked on element: {}", locator);
        } catch (Exception e) {
            logger.error("❌ Failed to double-click on element: {}", locator, e);
            throw new RuntimeException("Error double-clicking on element: " + locator, e);
        }
    }

    public void rightClick(By locator) {
        try {
            WebElement element = findElement(locator);
            actions.contextClick(element).perform();
            logger.info("🖱️ Right-clicked on element: {}", locator);
        } catch (Exception e) {
            logger.error("❌ Failed to right-click on element: {}", locator, e);
            throw new RuntimeException("Error right-clicking on element: " + locator, e);
        }
    }

    public void executeJavaScript(String script, Object... args) {
        try {
            jsExecutor.executeScript(script, args);
            logger.info("📝 Executed JavaScript: {}", script);
        } catch (Exception e) {
            logger.error("❌ JavaScript execution failed: {}", script, e);
            throw new RuntimeException("Error executing JavaScript: " + script, e);
        }
    }
}
