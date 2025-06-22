/**
 * BasePage is the foundational class for all page objects.
 * It provides reusable WebDriver interactions with built-in logging,
 * diagnostic handling, waits, scrolling, and JavaScript execution support.
 *
 * This class should be extended by all specific page object classes.
 *
 * Dependencies:
 * - SLF4J for internal debug logs
 * - ExtentReports for step-based reporting
 * - Selenium Actions/JSExecutor/WebDriverWait for UI operations
 */
package com.Vcidex.StoryboardSystems.Common;

// ─── External Libraries ───────────────────────────────────────────
import com.aventstack.extentreports.ExtentTest; // Used for structured test reporting
import org.openqa.selenium.*; // Core Selenium WebDriver and element interaction classes
import org.openqa.selenium.interactions.Actions; // Needed for hover, drag-drop, etc.
import org.openqa.selenium.support.ui.*; // Fluent waits and ExpectedConditions
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// ─── Internal Utilities ───────────────────────────────────────────
import com.Vcidex.StoryboardSystems.Utils.Config.ConfigManager; // Reads timeout from config
import com.Vcidex.StoryboardSystems.Utils.Logger.DiagnosticsLogger; // Centralized screenshot logging
import com.Vcidex.StoryboardSystems.Utils.Logger.ErrorLogger; // For consistent error stack logging
import com.Vcidex.StoryboardSystems.Utils.Logger.ReportManager; // Fetches ExtentTest node

import java.time.Duration;
import java.util.Objects;

public abstract class BasePage {
    private static final Logger logger = LoggerFactory.getLogger(BasePage.class);

    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final Actions actions;
    protected final JavascriptExecutor jsExecutor;
    protected final ExtentTest test;

    private static final int TIMEOUT = Integer.parseInt(
            ConfigManager.getProperty("timeout", "10")
    );
    private static final Duration DEFAULT_WAIT = Duration.ofSeconds(TIMEOUT);

    // Overlay selectors used to detect loading spinners, modals, and blockUI overlays.
    private static final By ALL_OVERLAYS = By.cssSelector(
            ".spinner-overlay, .modal-backdrop, .blockUI, .blockOverlay"
    );

    /**
     * Constructor for all child page classes.
     *
     * @param driver The active WebDriver instance.
     */
    public BasePage(WebDriver driver) {
        this.driver = Objects.requireNonNull(driver, "Driver must not be null");
        this.test = ReportManager.getTest();
        this.wait = new WebDriverWait(driver, DEFAULT_WAIT);
        this.actions = new Actions(driver);
        this.jsExecutor = (JavascriptExecutor) driver;
    }

    /** Logs a debug message using SLF4J. */
    protected void debug(String message) {
        logger.debug(message);
    }

    /**
     * Returns the element after waiting for its presence.
     * Logs and throws on failure.
     */
    protected WebElement findElement(By locator) {
        try {
            return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (Exception e) {
            ErrorLogger.logException(e, "findElement: " + locator, driver);
            throw e;
        }
    }

    /**
     * Clicks an element with scroll into view and logging.
     * Retries on stale element exception.
     *
     * @param locator Locator of the element to click.
     * @param name    Friendly name used for logging and reporting.
     */
    public void click(By locator, String name) {
        try {
            WebElement el = waitUntilClickable(locator);
            // Scroll the element into view before clicking
            jsExecutor.executeScript("arguments[0].scrollIntoView({block:'center', inline:'center'});", el);
            logger.debug("Click → {} | {}", name, locator);
            test.info("🔱 Click ▶ " + name);
            safeClick(el);
            test.pass("✅ Clicked " + name);
        } catch (StaleElementReferenceException staleEx) {
            logger.warn("⚠️ StaleElementReferenceException on '{}' — retrying once...", name);
            try {
                WebElement retryEl = waitUntilClickable(locator);
                // Retry after handling stale reference
                jsExecutor.executeScript("arguments[0].scrollIntoView({block:'center', inline:'center'});", retryEl);
                safeClick(retryEl);
                test.pass("✅ Clicked " + name + " after stale element retry");
            } catch (Exception retryFail) {
                DiagnosticsLogger.onFailure(driver, "Click (after retry): " + name);
                throw new RuntimeException("Failed to click '" + name + "' even after retry", retryFail);
            }
        } catch (Exception e) {
            DiagnosticsLogger.onFailure(driver, "Click: " + name);
            throw e;
        }
    }

    /**
     * Types into input fields using both normal and JS fallback strategy.
     *
     * @param locator Target input field.
     * @param value   Value to be entered.
     * @param name    Field name for logging.
     */
    public void type(By locator, String value, String name) {
        try {
            logger.debug("Type → {} | '{}' | {}", name, value, locator);
            test.info("⌨️ Type ▶ " + name + " : '" + value + "'");
            WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            scrollIntoView(locator);
            el.click();
            el.clear();
            try {
                el.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
                el.sendKeys(value);
            } catch (InvalidElementStateException e) {
                // If normal sendKeys fails (e.g. read-only or blocked), fallback to JS typing
                logger.warn("Fallback to JS: sendKeys failed on " + name, e);
                jsExecutor.executeScript(
                        "arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input', { bubbles: true }))",
                        el, value);
                test.info("⚠️ JS fallback used for typing into '" + name + "'");
            }
            test.pass("✅ Typed '" + value + "' into " + name);
        } catch (Exception e) {
            DiagnosticsLogger.onFailure(driver, "Type: " + name);
            throw e;
        }
    }

    /**
     * Selects a value from an Angular ng-select component using typing + enter.
     * Includes scroll and fallback to JS click.
     */
    protected void selectFromNgSelect(String formControlName, String value) {
        By container = By.xpath(String.format("//ng-select[@formcontrolname='%s']//div[contains(@class,'ng-select-container')]", formControlName));
        WebElement dropdown = waitUntilClickable(container);
        // Scroll ng-select into view (helps with fixed footer modals)
        jsExecutor.executeScript("arguments[0].scrollIntoView({block:'center'})", dropdown);
        try {
            dropdown.click();
        } catch (ElementClickInterceptedException e) {
            // Fallback if element is obscured
            jsExecutor.executeScript("arguments[0].click();", dropdown);
        }
        By filterInput = By.xpath(String.format("//ng-select[@formcontrolname='%s']//input", formControlName));
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(filterInput));
        input.sendKeys(value);
        input.sendKeys(Keys.ENTER);
        waitForOverlayClear();
    }

    /**
     * Clicks an element using WebDriver and falls back to JS click if blocked.
     */
    public void safeClick(WebElement element) {
        try {
            waitForOverlayToDisappear();
            scrollToElement(element);
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .ignoring(ElementClickInterceptedException.class)
                    .until(ExpectedConditions.elementToBeClickable(element));
            element.click();
        } catch (ElementClickInterceptedException e) {
            // Use JS click if WebDriver click is intercepted (e.g. overlay)
            jsExecutor.executeScript("arguments[0].click();", element);
        }
    }

    public void waitForAngular() {
        new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(wd -> {
                    Object out = ((JavascriptExecutor)wd).executeAsyncScript(
                            "var cb = arguments[arguments.length-1];" +
                                    "if (window.getAllAngularTestabilities) {" +
                                    "  var testabilities = window.getAllAngularTestabilities();" +
                                    "  function check() {" +
                                    "    if (testabilities.every(t=>t.isStable())) cb(true);" +
                                    "    else setTimeout(check,100);" +
                                    "  } check();" +
                                    "} else { cb(true); }"
                    );
                    return Boolean.TRUE.equals(out);
                });
    }

    /**
     * Waits for Angular apps to finish pending tasks using testability APIs.
     */
    public void waitForAngularRequestsToFinish() {
        try {
            new WebDriverWait(driver, DEFAULT_WAIT)
                    .until(d -> {
                        Object ready = ((JavascriptExecutor) d).executeScript(
                                "return (window.getAllAngularTestabilities||" +
                                        "window.getAllAngularTestability) ? window.getAllAngularTestabilities().every(t=>t.isStable()) : true;"
                        );
                        return Boolean.TRUE.equals(ready);
                    });
        } catch (TimeoutException | JavascriptException e) {
            // Angular might not be loaded on all pages, so continue safely
            logger.debug("Skipping Angular wait: " + e.getMessage());
        }
    }

    // Remaining methods unchanged — they are clear enough without line comments
    public WebElement waitUntilVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public WebElement waitUntilVisible(By locator, String alias) {
        return waitUntilVisible(locator);
    }

    public WebElement waitUntilClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public void scrollToElement(WebElement element) {
        jsExecutor.executeScript("arguments[0].scrollIntoView({block:'center'});", element);
    }

    public void scrollIntoView(By locator) {
        jsExecutor.executeScript("arguments[0].scrollIntoView({behavior:'smooth', block:'center'});", findElement(locator));
    }

    public void scrollAboveFooter(By locator, int footerHeightPx) {
        WebElement el = findElement(locator);
        jsExecutor.executeScript(
                "const el=arguments[0],f=arguments[1],r=el.getBoundingClientRect()," +
                        "v=window.innerHeight||document.documentElement.clientHeight," +
                        "d=r.top-((v-f)/2);window.scrollBy(0,d);",
                el, footerHeightPx
        );
    }

    public void waitForOverlayToDisappear() {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(ALL_OVERLAYS));
        } catch (TimeoutException ignored) {
            logger.debug("Overlay may still be present — proceeding with caution.");
        }
    }

    public void waitForOverlayClear() {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(ALL_OVERLAYS));
        } catch (TimeoutException ignored) {
        }
    }

    /**
     * Triggers DiagnosticsLogger manually with a custom failure context.
     */
    protected void failure(String context) {
        DiagnosticsLogger.onFailure(driver, context);
    }
}