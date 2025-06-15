// File: src/main/java/com/Vcidex/StoryboardSystems/Common/BasePage.java
package com.Vcidex.StoryboardSystems.Common;

import com.Vcidex.StoryboardSystems.Utils.Config.ConfigManager;
import com.Vcidex.StoryboardSystems.Utils.Logger.DiagnosticsLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.ErrorLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.ReportManager;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BasePage provides all fundamental UI actions (click, type, submit,
 * ng-select, scroll, waits, etc.) with built-in logging and diagnostics.
 */
public abstract class BasePage {
    private static final Logger logger = LoggerFactory.getLogger(BasePage.class);

    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final Actions actions;
    protected final JavascriptExecutor jsExecutor;
    protected final ExtentTest test;

    private static final int    TIMEOUT     = Integer.parseInt(
            ConfigManager.getProperty("timeout", "10")
    );
    private static final Duration DEFAULT_WAIT = Duration.ofSeconds(TIMEOUT);
    private static final Duration SHORT_WAIT   = Duration.ofSeconds(3);
    private static final By    ALL_OVERLAYS = By.cssSelector(
            ".spinner-overlay, .modal-backdrop, .blockUI, .blockOverlay"
    );

    public BasePage(WebDriver driver) {
        this.driver     = Objects.requireNonNull(driver, "Driver must not be null");
        this.test       = ReportManager.getTest();
        this.wait       = new WebDriverWait(driver, DEFAULT_WAIT);
        this.actions    = new Actions(driver);
        this.jsExecutor = (JavascriptExecutor) driver;
    }

    /** SLF4J debug. */
    protected void debug(String message) {
        logger.debug(message);
    }

    /** Locate an element or log & rethrow on failure. */
    protected WebElement findElement(By locator) {
        try {
            return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (Exception e) {
            ErrorLogger.logException(e, "findElement: " + locator, driver);
            throw e;
        }
    }

    /** Click with info + pass logs. */
    public void click(By locator, String name) {
        try {
            WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));

            // bring it above any sticky footer
            jsExecutor.executeScript(
                    "arguments[0].scrollIntoView({block:'center', inline:'center'});",
                    el
            );

            logger.debug("Click → {} | {}", name, locator);
            test.info("🖱 Click ▶ " + name);

            // use your safeClick helper
            safeClick(el);

            test.pass("✅ Clicked " + name);
        } catch (Exception e) {
            DiagnosticsLogger.onFailure(driver, "Click: " + name);
            throw e;
        }
    }

    /** Type with info + pass logs. */
    /** Type with info + fallback for readonly or blocked fields. */
    public void type(By locator, String value, String name) {
        try {
            logger.debug("Type → {} | '{}' | {}", name, value, locator);
            test.info("⌨️ Type ▶ " + name + " : '" + value + "'");

            WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            scrollIntoView(locator); // ensure visibility
            el.click();
            el.clear();

            // attempt normal input first
            try {
                el.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
                el.sendKeys(value);
            } catch (InvalidElementStateException e) { // ✅ catch only base
                logger.warn("Fallback to JS: sendKeys failed on " + name, e);
                jsExecutor.executeScript(
                        "arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input', { bubbles: true }))",
                        el, value
                );
                test.info("⚠️ JS fallback used for typing into '" + name + "'");
            }

            test.pass("✅ Typed '" + value + "' into " + name);
        } catch (Exception e) {
            DiagnosticsLogger.onFailure(driver, "Type: " + name);
            throw e;
        }
    }

    /** Submit with info + pass logs. */
    public void submit(By locator, String name) {
        try {
            logger.debug("Submit → {} | {}", name, locator);
            test.info("📤 Submit ▶ " + name);
            WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
            el.submit();
            test.pass("✅ Submitted " + name);
        } catch (Exception e) {
            DiagnosticsLogger.onFailure(driver, "Submit: " + name);
            throw e;
        }
    }

    /** Ng-select with options logging & retry attempts. */
    /**
     * Improved version of selectFromNgSelect that scrolls dropdown into view
     * and falls back to JS click if normal click is intercepted.
     */
    protected void selectFromNgSelect(String formControlName, String value) {
        // 1) Locate the dropdown container
        By container = By.xpath(
                String.format("//ng-select[@formcontrolname='%s']//div[contains(@class,'ng-select-container')]",
                        formControlName)
        );

        // 2) Wait for it to be clickable
        WebElement dropdown = waitUntilClickable(container);

        // 3) Scroll into view so it sits above any fixed footer
        jsExecutor.executeScript("arguments[0].scrollIntoView({block:'center'})", dropdown);


        // 4) Try a normal click, fallback to JS if intercepted
        try {
            dropdown.click();
        } catch (ElementClickInterceptedException e) {
            jsExecutor.executeScript("arguments[0].click();", dropdown);
        }

        // 5) Type into the filter box and hit ENTER
        By filterInput = By.xpath(
                String.format("//ng-select[@formcontrolname='%s']//input", formControlName)
        );
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(filterInput));
        input.sendKeys(value);
        input.sendKeys(Keys.ENTER);

        // 6) Wait for the options panel / overlays to clear
        waitForOverlayClear();
    }

    /** Safe JS click fallback if native click fails. */
    protected void safeClick(WebElement el) {
        try {
            new WebDriverWait(driver, SHORT_WAIT)
                    .until(d -> {
                        Point p = el.getLocation();
                        return el.isDisplayed() && el.isEnabled() &&
                                jsExecutor.executeScript(
                                        "return document.elementFromPoint(arguments[0],arguments[1])===arguments[2];",
                                        p.getX() + el.getSize().getWidth()/2,
                                        p.getY() + el.getSize().getHeight()/2,
                                        el
                                ).equals(true);
                    });
            el.click();
        } catch (Exception e) {
            jsExecutor.executeScript("arguments[0].click();", el);
        }
    }

    /** Delegate failure diagnostics to DiagnosticsLogger. */
    protected void failure(String context) {
        DiagnosticsLogger.onFailure(driver, context);
    }

    /** Scroll into view with no logging. */
    public void scrollIntoView(By locator) {
        jsExecutor.executeScript(
                "arguments[0].scrollIntoView({behavior:'smooth', block:'center'});",
                findElement(locator)
        );
    }

    /** Scroll element above a footer height with no logging. */
    public void scrollAboveFooter(By locator, int footerHeightPx) {
        WebElement el = findElement(locator);
        jsExecutor.executeScript(
                "const el=arguments[0],f=arguments[1],r=el.getBoundingClientRect()," +
                        "v=window.innerHeight||document.documentElement.clientHeight," +
                        "d=r.top-((v-f)/2);window.scrollBy(0,d);",
                el, footerHeightPx
        );
    }

    /** Wait for overlays to clear, no logging. */
    public void waitForOverlayClear() {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(ALL_OVERLAYS));
        } catch (TimeoutException ignored) {}
    }

    /**
     * Waits for Angular testabilities to report “stable”.
     * If they’re not present or the script times out, we swallow the error.
     */
    public void waitForAngularRequestsToFinish() {
        try {
            new WebDriverWait(driver, DEFAULT_WAIT)
                    .until(d -> {
                        Object ready = ((JavascriptExecutor)d).executeScript(
                                "return (window.getAllAngularTestabilities||" +
                                        "window.getAllAngularTestability) " +
                                        "? window.getAllAngularTestabilities().every(t=>t.isStable()) " +
                                        ": true;"
                        );
                        return Boolean.TRUE.equals(ready);
                    });
        } catch (TimeoutException | JavascriptException e) {
            // Angular either didn’t stabilize in time or isn’t present.
            // We ignore and proceed—overlay waits will handle the rest.
            logger.debug("Skipping Angular wait: " + e.getMessage());
        }
    } 

    /** Wait until visible, no logging. */
    public WebElement waitUntilVisible(By locator, String branchDropdown) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /** Wait until clickable, no logging. */
    public WebElement waitUntilClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }
}