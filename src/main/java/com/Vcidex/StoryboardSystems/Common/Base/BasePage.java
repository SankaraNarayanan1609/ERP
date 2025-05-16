package com.Vcidex.StoryboardSystems.Common.Base;

import com.Vcidex.StoryboardSystems.Utils.Helpers.LoggingHelper;
import com.Vcidex.StoryboardSystems.Utils.Config.ConfigManager;
import com.Vcidex.StoryboardSystems.Utils.Logger.ErrorHandler;
import com.Vcidex.StoryboardSystems.Utils.Logger.ExtentTestManager;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.internal.Require;
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
        this.driver = Require.nonNull("Driver must be set", driver); // This is where your error occurred
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(getTimeoutFromConfig()));
        this.actions = new Actions(driver);
        this.jsExecutor = (JavascriptExecutor) driver;
    }

    private static int getTimeoutFromConfig() {
        return Integer.parseInt(ConfigManager.getProperty("WebDriver.timeout", "10"));
    }

    // -------------------------- Element Interaction Methods ---------------------------

    public WebElement findElement(By locator) {
        LoggingHelper.logWithEmoji("INFO", "Finding element: " + locator, "FIND_ELEMENT");
        return ErrorHandler.executeSafely(driver, () -> driver.findElement(locator), "findElement");
    }

    public WebElement findElement(By locator, String elementName) {
        LoggingHelper.logWithEmoji("INFO", "Finding element: " + elementName, "FIND_ELEMENT");
        return ErrorHandler.executeSafely(driver, () -> driver.findElement(locator), "findElement - " + elementName);
    }

    public void sendKeys(By locator, String text) {
        LoggingHelper.logWithEmoji("INFO", "Entering text into element: " + locator, "SENDKEYS");
        ErrorHandler.executeSafely(driver, () -> {
            WebElement element = findElement(locator);
            element.clear();
            element.sendKeys(text);
            return null;
        }, "sendKeys");
    }

    public void sendKeys(By locator, String value, String elementName) {
        LoggingHelper.logWithEmoji("INFO", "Entering '" + value + "' into: " + elementName, "SENDKEYS");
        ErrorHandler.executeSafely(driver, () -> {
            WebElement element = findElement(locator, elementName);
            element.clear();
            element.sendKeys(value);
            return null;
        }, "SendKeys");
    }

    public void click(By locator) {
        LoggingHelper.logWithEmoji("INFO", "Clicking on: " + locator, "CLICK");
        ErrorHandler.executeSafely(driver, () -> {
            findElement(locator).click();
            return null;
        }, "click");
    }

    public String getAttribute(By locator, String attributeName) {
        return ErrorHandler.executeSafely(driver, () -> driver.findElement(locator).getAttribute(attributeName), "getAttribute");
    }

    public void click(By locator, String elementName) {
        LoggingHelper.logWithEmoji("INFO", "Clicking on: " + elementName, "CLICK");
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

    // -------------------------- Action Methods ---------------------------

    public void performAction(Runnable action, String actionDescription) {
        try {
            action.run();
            LoggingHelper.logWithEmoji("INFO", actionDescription + " succeeded", "START");
            ExtentTestManager.getTest().info("✅ Action succeeded: " + actionDescription);
        } catch (Exception e) {
            LoggingHelper.logWithEmoji("ERROR", actionDescription + " failed: " + e.getMessage(), "FAIL");
            ExtentTestManager.getTest().fail("❌ Action failed: " + actionDescription + " | Error: " + e.getMessage());
            throw new RuntimeException("Error in action: " + actionDescription, e);
        }
    }

    public String getElementAttribute(By locator, String attribute) {
        return ErrorHandler.executeSafely(driver, () -> findElement(locator).getDomAttribute(attribute), "getElementAttribute");
    }

    public String getText(By locator) {
        return ErrorHandler.executeSafely(driver, () -> findElement(locator).getText(), "getText");
    }

    // -------------------------- Element Actions ---------------------------

    public void selectDropdownUsingVisibleText(By locator, String value) {
        LoggingHelper.logWithEmoji("INFO", "Selecting dropdown value: " + value, "SENDKEYS");
        ErrorHandler.executeSafely(driver, () -> {
            new Select(findElement(locator)).selectByVisibleText(value);
            return null;
        }, "selectDropdownUsingVisibleText");
    }

    public void scrollIntoView(By locator) {
        LoggingHelper.logWithEmoji("INFO", "Scrolling into view: " + locator, "TIMER");
        ErrorHandler.executeSafely(driver, () -> {
            WebElement element = findElement(locator);
            jsExecutor.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
            return null;
        }, "scrollIntoView");
    }

    public void moveToElement(By locator) {
        LoggingHelper.logWithEmoji("INFO", "Moving to element: " + locator, "TIMER");
        ErrorHandler.executeSafely(driver, () -> {
            actions.moveToElement(findElement(locator)).perform();
            return null;
        }, "moveToElement");
    }

    public void doubleClick(By locator) {
        LoggingHelper.logWithEmoji("INFO", "Double clicking on: " + locator, "CLICK");
        ErrorHandler.executeSafely(driver, () -> {
            actions.doubleClick(findElement(locator)).perform();
            return null;
        }, "doubleClick");
    }

    public void rightClick(By locator) {
        LoggingHelper.logWithEmoji("INFO", "Right clicking on: " + locator, "CLICK");
        ErrorHandler.executeSafely(driver, () -> {
            actions.contextClick(findElement(locator)).perform();
            return null;
        }, "rightClick");
    }

    public void executeJavaScript(String script, Object... args) {
        LoggingHelper.logWithEmoji("INFO", "Executing JavaScript: " + script, "TIMER");
        ErrorHandler.executeSafely(driver, () -> {
            jsExecutor.executeScript(script, args);
            return null;
        }, "executeJavaScript");
    }

    public void clearText(By locator) {
        LoggingHelper.logWithEmoji("INFO", "Clearing text from element: " + locator, "SENDKEYS");
        ErrorHandler.executeSafely(driver, () -> {
            findElement(locator).clear();
            return null;
        }, "clearText");
    }

    public String getCSSValue(By locator, String property) {
        LoggingHelper.logWithEmoji("INFO", "Getting CSS value from: " + locator, "TIMER");
        return ErrorHandler.executeSafely(driver, () -> findElement(locator).getCssValue(property), "getCSSValue");
    }

    // -------------------------- Error Handling and Logging ---------------------------

    protected void performAction(String actionName, Runnable action, boolean isSubmit, String elementDesc) {
        try {
            LoggingHelper.logWithEmoji("INFO", actionName + ": " + elementDesc, "START");
            ExtentTestManager.getTest().info("🚀 " + actionName + ": " + elementDesc);
            ErrorHandler.executeSafely(driver, () -> {
                action.run();
                return null;
            }, actionName);

            LoggingHelper.logWithEmoji("INFO", actionName + " succeeded", "DONE");
            ExtentTestManager.getTest().pass("✅ " + actionName + " successful");
        } catch (Exception e) {
            LoggingHelper.logWithEmoji("ERROR", actionName + " failed: " + e.getMessage(), "FAIL");
            logger.error("❌ Action failed: {}", actionName, e);
            ExtentTestManager.getTest().fail("❌ " + actionName + " failed: " + e.getMessage());
            throw new RuntimeException("Failed to perform: " + actionName, e);
        }
    }
}