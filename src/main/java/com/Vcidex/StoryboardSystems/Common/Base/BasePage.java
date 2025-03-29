package com.Vcidex.StoryboardSystems.Common.Base;

import com.Vcidex.StoryboardSystems.Utils.Config.ConfigManager;
import com.Vcidex.StoryboardSystems.Utils.Reporting.ErrorHandler;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
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

    // ✅ Unified Method for Finding and Waiting for Elements
    public WebElement findElement(By locator) {
        return ErrorHandler.executeSafely(driver, () -> wait.until(ExpectedConditions.visibilityOfElementLocated(locator)), "findElement", false, locator.toString());
    }

    public boolean isElementPresent(By locator) {
        return ErrorHandler.executeSafely(driver, () -> driver.findElement(locator) != null, "isElementPresent", false, locator.toString());
    }

    public String getElementAttribute(By locator, String attribute) {
        return ErrorHandler.executeSafely(driver, () -> findElement(locator).getDomAttribute(attribute), "getElementAttribute", false, locator.toString());
    }

    public String getText(By locator) {
        return ErrorHandler.executeSafely(driver, () -> findElement(locator).getText(), "getText", false, locator.toString());
    }

    public void sendKeys(By locator, String text) {
        ErrorHandler.executeSafely(driver, () -> {
            WebElement element = findElement(locator);
            element.clear();
            element.sendKeys(text);
        }, "sendKeys", false, locator.toString());
    }

    public void waitForElement(By locator, int seconds) {
        ErrorHandler.executeSafely(driver, () -> {
            WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
            customWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        }, "waitForElement", false, locator.toString());
    }

    public void click(By locator) {
        boolean isSubmit = isSubmitButton(locator);
        ErrorHandler.executeSafely(driver, () -> findElement(locator).click(), "click", isSubmit, locator.toString());
    }

    private boolean isSubmitButton(By locator) {
        WebElement element = findElement(locator);
        String accessibleName = element.getAccessibleName();
        return "submit".equalsIgnoreCase(accessibleName) || element.getText().toLowerCase().contains("submit");
    }

    public void selectDropdownUsingVisibleText(By locator, String value) {
        ErrorHandler.executeSafely(driver, () -> {
            new org.openqa.selenium.support.ui.Select(findElement(locator)).selectByVisibleText(value);
        }, "selectDropdownUsingVisibleText", false, locator.toString());
    }

//    public void captureScreenshot(String fileName) {
//        ErrorHandler.executeSafely(driver, () -> ErrorHandler.captureScreenshot(driver, fileName, "Manual"), "captureScreenshot", false, fileName);
//    }

    public void scrollIntoView(By locator) {
        ErrorHandler.executeSafely(driver, () -> {
            WebElement element = findElement(locator);
            jsExecutor.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
        }, "scrollIntoView", false, locator.toString());
    }

    public void moveToElement(By locator) {
        ErrorHandler.executeSafely(driver, () -> actions.moveToElement(findElement(locator)).perform(), "moveToElement", false, locator.toString());
    }

    public void doubleClick(By locator) {
        ErrorHandler.executeSafely(driver, () -> actions.doubleClick(findElement(locator)).perform(), "doubleClick", false, locator.toString());
    }

    public void rightClick(By locator) {
        ErrorHandler.executeSafely(driver, () -> actions.contextClick(findElement(locator)).perform(), "rightClick", false, locator.toString());
    }

    public void executeJavaScript(String script, Object... args) {
        ErrorHandler.executeSafely(driver, () -> {
            jsExecutor.executeScript(script, args);
        }, "executeJavaScript", false, "JavaScriptExecution");
    }

    public boolean isElementClickable(By locator) {
        return ErrorHandler.executeSafely(driver, () -> {
            wait.until(ExpectedConditions.elementToBeClickable(locator));
            return true;
        }, "isElementClickable", false, locator.toString());
    }

    public void clearText(By locator) {
        ErrorHandler.executeSafely(driver, () -> findElement(locator).clear(), "clearText", false, locator.toString());
    }

    public String getCSSValue(By locator, String property) {
        return ErrorHandler.executeSafely(driver, () -> findElement(locator).getCssValue(property), "getCSSValue", false, locator.toString());
    }
}
