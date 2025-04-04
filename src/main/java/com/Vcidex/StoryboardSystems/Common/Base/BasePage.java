package com.Vcidex.StoryboardSystems.Common.Base;

import com.Vcidex.StoryboardSystems.Utils.Config.ConfigManager;
import com.Vcidex.StoryboardSystems.Utils.Reporting.ErrorHandler;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

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

    // ✅ Unified Element Handling
    public WebElement findElement(By locator) {
        return ErrorHandler.executeSafely(driver, () -> {
            WebElement element = driver.findElement(locator);
            return element;
            }, "findElement");
    }


    public boolean isElementPresent(By locator) {
        return ErrorHandler.executeSafely(driver,
                () -> driver.findElements(locator).size() > 0,
                "isElementPresent"); // ✅ Only three arguments

    }

    public String getElementAttribute(By locator, String attribute) {
        return ErrorHandler.executeSafely(driver,
                () -> findElement(locator).getDomAttribute(attribute),
                "getElementAttribute"); // ✅ Fix
    }

    public String getText(By locator) {
        return ErrorHandler.executeSafely(driver,
                () -> findElement(locator).getText(),
                "getText"); // ✅ Fix
    }

    public void sendKeys(By locator, String text) {
        ErrorHandler.executeSafely(driver, () -> {
            WebElement element = findElement(locator);
            element.clear();
            element.sendKeys(text);
            return null;
        }, "sendKeys"); // ✅ Fix
    }

    public void waitForElement(By locator, int seconds) {
        ErrorHandler.executeSafely(driver, () -> {
            waitForElement(locator, seconds);
            return null;
        }, "waitForElement"); // ✅ Fix
    }

    public void click(By locator) {
        boolean isSubmit = isSubmitButton(locator);
        ErrorHandler.executeSafely(driver, () -> {
            findElement(locator).click();
            return null;
        }, "click"); // ✅ Fix
    }

    private boolean isSubmitButton(By locator) {
        WebElement element = findElement(locator);
        String accessibleName = element.getAccessibleName();
        return "submit".equalsIgnoreCase(accessibleName) || element.getText().toLowerCase().contains("submit");
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

    public String getCSSValue(By locator, String property) {
        return ErrorHandler.executeSafely(driver,
                () -> findElement(locator).getCssValue(property),
                "getCSSValue");
    }

}