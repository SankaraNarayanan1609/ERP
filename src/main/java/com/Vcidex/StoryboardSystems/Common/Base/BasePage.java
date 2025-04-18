package com.Vcidex.StoryboardSystems.Common.Base;

import com.Vcidex.StoryboardSystems.Utils.Config.ConfigManager;
import com.Vcidex.StoryboardSystems.Utils.Reporting.ErrorHandler;
import com.Vcidex.StoryboardSystems.Utils.Reporting.ExtentTestManager;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

import static com.Vcidex.StoryboardSystems.Common.Base.TestBase.logger;

public class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected Actions actions;
    protected JavascriptExecutor jsExecutor;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        int timeout = getTimeoutFromConfig();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        this.actions = new Actions(driver);
        this.jsExecutor = (JavascriptExecutor) driver;
    }

    private static int getTimeoutFromConfig() {
        return Integer.parseInt(ConfigManager.getProperty("WebDriver.timeout", "10"));
    }

    public WebElement findElement(By locator) {
        return ErrorHandler.executeSafely(driver, () -> driver.findElement(locator), "findElement");
    }

    public WebElement findElement(By locator, String elementName) {
        ExtentTestManager.getTest().info("🔍 Finding element: " + elementName);
        logger.info("🔍 Finding element: {}", elementName);
        return ErrorHandler.executeSafely(driver, () -> driver.findElement(locator), "findElement - " + elementName);
    }

    public void sendKeys(By locator, String text) {
        ErrorHandler.executeSafely(driver, () -> {
            WebElement element = findElement(locator);
            element.clear();
            element.sendKeys(text);
            return null;
        }, "sendKeys");
    }

    public void sendKeys(By locator, String value, String elementName) {
        ExtentTestManager.getTest().info("⌨️ Entering '" + value + "' into: " + elementName);
        logger.info("⌨️ Entering '{}' into: {}", value, elementName);
        WebElement element = findElement(locator, elementName);
        element.clear();
        element.sendKeys(value);
    }

    public void click(By locator) {
        ErrorHandler.executeSafely(driver, () -> {
            findElement(locator).click();
            return null;
        }, "click");
    }

    public void click(By locator, String elementName) {
        ExtentTestManager.getTest().info("🖱️ Clicking on: " + elementName);
        logger.info("🖱️ Clicking on: {}", elementName);
        WebElement element = findElement(locator, elementName);
        element.click();
    }

    public String getTitle() {
        return ErrorHandler.executeSafely(driver, driver::getTitle, "getTitle");
    }

    public String getCurrentUrl() {
        return ErrorHandler.executeSafely(driver, driver::getCurrentUrl, "getCurrentUrl");
    }

    public void waitUntilVisible(By locator) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public boolean isElementPresent(By locator) {
        return ErrorHandler.executeSafely(driver, () -> driver.findElements(locator).size() > 0, "isElementPresent");
    }
    public void performAction(Runnable action, String actionDescription) {
        try {
            action.run();
            ExtentTestManager.getTest().info("✅ Action succeeded: " + actionDescription);
            logger.info("✅ Action succeeded: {}", actionDescription);
        } catch (Exception e) {
            ExtentTestManager.getTest().fail("❌ Action failed: " + actionDescription + " | Error: " + e.getMessage());
            logger.error("❌ Action failed: {} | Error: {}", actionDescription, e.getMessage(), e);
            throw new RuntimeException("Error in action: " + actionDescription, e);
        }
    }

    public String getElementAttribute(By locator, String attribute) {
        return ErrorHandler.executeSafely(driver, () -> findElement(locator).getDomAttribute(attribute), "getElementAttribute");
    }

    public String getText(By locator) {
        return ErrorHandler.executeSafely(driver, () -> findElement(locator).getText(), "getText");
    }

    public void waitForElement(By locator, int seconds) {
        ErrorHandler.executeSafely(driver, () -> {
            new WebDriverWait(driver, Duration.ofSeconds(seconds)).until(ExpectedConditions.visibilityOfElementLocated(locator));
            return null;
        }, "waitForElement");
    }

    public void selectDropdownUsingVisibleText(By locator, String value) {
        ErrorHandler.executeSafely(driver, () -> {
            new Select(findElement(locator)).selectByVisibleText(value);
            return null;
        }, "selectDropdownUsingVisibleText");
    }

    public void scrollIntoView(By locator) {
        ErrorHandler.executeSafely(driver, () -> {
            WebElement element = findElement(locator);
            jsExecutor.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
            return null;
        }, "scrollIntoView");
    }

    public void moveToElement(By locator) {
        ErrorHandler.executeSafely(driver, () -> {
            actions.moveToElement(findElement(locator)).perform();
            return null;
        }, "moveToElement");
    }

    public void doubleClick(By locator) {
        ErrorHandler.executeSafely(driver, () -> {
            actions.doubleClick(findElement(locator)).perform();
            return null;
        }, "doubleClick");
    }

    public void rightClick(By locator) {
        ErrorHandler.executeSafely(driver, () -> {
            actions.contextClick(findElement(locator)).perform();
            return null;
        }, "rightClick");
    }

    public void executeJavaScript(String script, Object... args) {
        ErrorHandler.executeSafely(driver, () -> {
            jsExecutor.executeScript(script, args);
            return null;
        }, "executeJavaScript");
    }

    public boolean isElementClickable(By locator) {
        return ErrorHandler.executeSafely(driver, () -> {
            wait.until(ExpectedConditions.elementToBeClickable(locator));
            return true;
        }, "isElementClickable");
    }

    public void clearText(By locator) {
        ErrorHandler.executeSafely(driver, () -> {
            findElement(locator).clear();
            return null;
        }, "clearText");
    }

    public void waitForLoaderToDisappear() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.invisibilityOfElementLocated(
                    By.cssSelector(".ngx-spinner-overlay, .loader, .overlay-loader"))); // adjust if needed
        } catch (TimeoutException e) {
            logger.warn("Loader did not disappear in expected time.");
        }
    }

    public String getCSSValue(By locator, String property) {
        return ErrorHandler.executeSafely(driver, () -> findElement(locator).getCssValue(property), "getCSSValue");
    }

    // ✨ Optional: Common utility to wrap any action with logging and extent reporting
    protected void performAction(String actionName, Runnable action, boolean isSubmit, String elementDesc) {
        try {
            ExtentTestManager.getTest().info("🚀 " + actionName + ": " + elementDesc);
            logger.info("🔄 Performing action: {}", actionName);

            ErrorHandler.executeSafely(driver, () -> {
                action.run();
                return null;
            }, actionName);

            logger.info("✅ Action succeeded: {}", actionName);
            ExtentTestManager.getTest().pass("✅ " + actionName + " successful");
        } catch (Exception e) {
            logger.error("❌ Action failed: {}", actionName, e);
            ExtentTestManager.getTest().fail("❌ " + actionName + " failed: " + e.getMessage());
            throw new RuntimeException("Failed to perform: " + actionName, e);
        }
    }
}