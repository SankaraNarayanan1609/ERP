package com.Vcidex.StoryboardSystems.Common.Base;

import com.Vcidex.StoryboardSystems.Utils.Config.ConfigManager;
import com.Vcidex.StoryboardSystems.Utils.Logger.ErrorLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.PerformanceLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.UIActionLogger;
import com.aventstack.extentreports.ExtentTest;
import com.Vcidex.StoryboardSystems.Utils.Logger.ExtentTestManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Objects;

/**
 * BasePage providing common WebDriver interactions
 * with unified logging, performance timing, and error handling.
 */
public class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected Actions actions;
    protected JavascriptExecutor jsExecutor;
    private static final int TIMEOUT = Integer.parseInt(
            ConfigManager.getProperty("timeout", "10")
    );
    private static final Logger logger = LoggerFactory.getLogger(BasePage.class);
    protected ExtentTest test = ExtentTestManager.getTest();

    public BasePage(WebDriver driver) {
        this.driver = Objects.requireNonNull(driver, "Driver must be set");
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT));
        this.actions = new Actions(driver);
        this.jsExecutor = (JavascriptExecutor) driver;
    }

    // ---------------- Element Interaction ----------------


     /**
      * Find element with presence check and debug-only logging
      */
    public WebElement findElement(By locator) {
        try {
            WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            UIActionLogger.debug("FindElement → " + locator);
            return el;
        } catch (Exception e) {
            ErrorLogger.logException(e, "findElement:" + locator, driver);
            throw e;
        }
    }

    /**
     * Click action wrapped with logging and error handling.
     */
    public void click(By locator, String name) {
        performAction(
                () -> UIActionLogger.click(driver, locator, name),
                "Click: " + name
        );
    }

    public void click(By locator) {
        click(locator, locator.toString());
    }

    /**
     * Type action wrapped with logging and error handling.
     */
    public void type(By locator, String value, String name) {
        performAction(
                () -> UIActionLogger.type(driver, locator, value, name),
                "Type '" + value + "' into " + name
        );
    }

    public void type(By locator, String value) {
        type(locator, value, locator.toString());
    }

    /**
     * Submit form wrapped with logging and error handling.
     */
    public void submit(By locator, String pageName) {
        performAction(
                () -> UIActionLogger.submit(driver, locator, pageName),
                "Submit: " + pageName
        );
    }

    // ---------------- Waits & Reads ----------------

    /**
     * Wait until visible, with performance timing and error capture.
     */
    public WebElement waitUntilVisible(By locator) {
        String key = "waitVisible:" + locator;
        PerformanceLogger.start(key);
        try {
            WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            return el;
        } catch (Exception e) {
            ErrorLogger.logException(e, key, driver);
            throw e;
        } finally {
            PerformanceLogger.end(key);
        }
    }

    public String getText(By locator, String name) {
        return performGet(
                () -> findElement(locator).getText(),
                "GetText: " + name
        );
    }

    public String getText(By locator) {
        return getText(locator, locator.toString());
    }

    public String getAttribute(By locator, String attribute) {
        return performGet(
                () -> findElement(locator).getAttribute(attribute),
                "GetAttribute: " + attribute + " @ " + locator
        );
    }

    // ---------------- Dropdowns & Misc ----------------

    public void selectByText(By locator, String value) {
        performAction(
                () -> {
                    UIActionLogger.debug("Select → " + value + " @ " + locator);
                    new Select(findElement(locator)).selectByVisibleText(value);
                },
                "Select: " + value
        );
    }

    public void scrollIntoView(By locator) {
        performAction(
                () -> jsExecutor.executeScript(
                        "arguments[0].scrollIntoView({behavior:'smooth', block:'center'});",
                        findElement(locator)
                ),
                "ScrollIntoView: " + locator
        );
    }

    public void moveToElement(By locator) {
        performAction(
                () -> actions.moveToElement(findElement(locator)).perform(),
                "Hover: " + locator
        );
    }

    public void executeJavaScript(String script, Object... args) {
        performAction(
                () -> jsExecutor.executeScript(script, args),
                "ExecuteJS: " + script
        );
    }

    // ---------------- Utilities ----------------

    private <T> T performGet(SupplierWithException<T> supplier, String description) {
        try {
            test.info("🔍 " + description);
            T result = supplier.get();
            test.pass("✅ " + description + " → " + result);
            return result;
        } catch (Exception e) {
            test.fail("❌ " + description + " | " + e.getMessage());
            ErrorLogger.logException(e, description, driver);
            throw new RuntimeException(description, e);
        }
    }

    private void performAction(Runnable action, String description) {
        try {
            test.info("🚀 " + description);
            action.run();
            test.pass("✅ " + description);
        } catch (Exception e) {
            test.fail("❌ " + description + " | " + e.getMessage());
            ErrorLogger.logException(e, description, driver);
            throw e;
        }
    }

    @FunctionalInterface
    private interface SupplierWithException<T> {
        T get() throws Exception;
    }
}
