package com.Vcidex.StoryboardSystems.Common.Base;

import com.Vcidex.StoryboardSystems.Utils.Config.ConfigManager;
import com.Vcidex.StoryboardSystems.Utils.Logger.ErrorLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.PerformanceLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.UIActionLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.ExtentTestManager;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Objects;

public class BasePage {
    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final Actions actions;
    protected final JavascriptExecutor jsExecutor;
    protected final ExtentTest test;

    private static final Logger logger = LoggerFactory.getLogger(BasePage.class);
    private static final int TIMEOUT = Integer.parseInt(
            ConfigManager.getProperty("timeout", "10")
    );

    public BasePage(WebDriver driver) {
        this(driver, ExtentTestManager.getTest());
    }

    public BasePage(WebDriver driver, ExtentTest node) {
        this.driver = Objects.requireNonNull(driver, "Driver must not be null");
        this.test   = Objects.requireNonNull(node,   "ExtentTest node must not be null");
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT));
        this.actions    = new Actions(driver);
        this.jsExecutor = (JavascriptExecutor) driver;
    }

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

    // ───────────── Element Interaction ──────────────────────────────────────────

    /** Directly delegate to your UIActionLogger so you get only its two lines. */
    public void click(By locator, String name) {
        UIActionLogger.click(driver, locator, name, test);
    }

    /** Ditto for typing text. */
    protected void type(By locator, String value, String name) {
        UIActionLogger.type(driver, locator, value, name, test);
    }

    /** Ditto for form submits. */
    public void submit(By locator, String pageName) {
        UIActionLogger.submit(driver, locator, pageName, test);
    }

    // ───────────── Waits & Reads ────────────────────────────────────────────────

    public WebElement waitUntilVisible(By locator) {
        String key = "waitVisible:" + locator;
        PerformanceLogger.start(key);
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
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

    public String getAttribute(By locator, String attribute) {
        return performGet(
                () -> findElement(locator).getAttribute(attribute),
                "GetAttribute: " + attribute + " @ " + locator
        );
    }

    // ───────────── Dropdowns & Misc ────────────────────────────────────────────

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

    // ───────────── Utilities ───────────────────────────────────────────────────

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

    protected void performAction(Runnable action, String description) {
        test.info("🚀 " + description);
        action.run();
        test.pass("✅ " + description);
    }

    @FunctionalInterface
    private interface SupplierWithException<T> {
        T get() throws Exception;
    }
}