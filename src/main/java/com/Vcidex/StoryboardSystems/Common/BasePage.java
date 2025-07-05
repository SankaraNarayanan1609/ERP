package com.Vcidex.StoryboardSystems.Common;

import com.Vcidex.StoryboardSystems.Utils.Logger.ScreenshotHelper;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Vcidex.StoryboardSystems.Utils.Config.ConfigManager;
import com.Vcidex.StoryboardSystems.Utils.Logger.DiagnosticsLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.ErrorLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.ReportManager;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class BasePage {
    public static final Logger log = LoggerFactory.getLogger(BasePage.class);

    protected final WebDriver       driver;
    protected final WebDriverWait   wait;
    protected final Actions         actions;
    protected final JavascriptExecutor jsExecutor;
    protected final ExtentTest      test;

    // tightened default wait from 10s → 3s
    private static final int    TIMEOUT      = Integer.parseInt(
            ConfigManager.getProperty("timeout", "3")
    );
    private static final Duration DEFAULT_WAIT = Duration.ofSeconds(TIMEOUT);

    // overlays often clear in < 500ms
    private static final Duration OVERLAY_WAIT = Duration.ofMillis(500);
    private static final By      ALL_OVERLAYS = By.cssSelector(
            ".spinner-overlay, .modal-backdrop, .blockUI, .blockOverlay"
    );

    public BasePage(WebDriver driver) {
        this.driver      = Objects.requireNonNull(driver);
        this.test        = ReportManager.getTest();
        this.wait        = new WebDriverWait(driver, DEFAULT_WAIT);
        this.actions     = new Actions(driver);
        this.jsExecutor  = (JavascriptExecutor) driver;
    }

    /** Wait up to 500 ms for any loading overlays to disappear. */
    public void waitForOverlayClear() {
        try {
            List<WebElement> found = driver.findElements(ALL_OVERLAYS);
            log.debug("→ waitForOverlayClear start, matching overlays: " + found.size()
                    + " classes: " + found.stream()
                    .map(e->e.getAttribute("class"))
                    .collect(Collectors.joining(",")));
            new WebDriverWait(driver, OVERLAY_WAIT)
                    .until(ExpectedConditions.invisibilityOfElementLocated(ALL_OVERLAYS));
            found = driver.findElements(ALL_OVERLAYS);
            log.debug("→ waitForOverlayClear end, " +
                    " overlays: " + found.size());
        } catch (TimeoutException ignored) {
            log.debug("Overlay still present after wait, proceeding anyway");
        }
    }

    /** Finds an element after clearing overlays and presence-checking. */
    protected WebElement findElement(By locator) {
        try {
            waitForOverlayClear();
            return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (Exception e) {
            ErrorLogger.logException(e, "findElement: " + locator, driver);
            throw e;
        }
    }

    /**
     * Scrolls, logs, then clicks—with one 3 s wait, plus a simple JS fallback if blocked.
     */
    public void click(By locator, String name) {
        try {
            waitForOverlayClear();
            WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
            jsExecutor.executeScript(
                    "arguments[0].scrollIntoView({block:'center',inline:'center'});",
                    el
            );
            test.info("🔱 Click ▶ " + name);
            safeClick(el);
            test.pass("✅ Clicked " + name);
        } catch (Exception e) {
            DiagnosticsLogger.onFailure(driver, "Click: " + name);
            throw e;
        }
    }

    /**
     * Types into a field, with a single 3 s wait for visibility,
     * plus JS fallback if sendKeys fails.
     */
    public void type(By locator, String value, String name) {
        try {
            waitForOverlayClear();
            test.info("⌨️ Type ▶ " + name + " : '" + value + "'");
            WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
            jsExecutor.executeScript("arguments[0].scrollIntoView({block:'center'});", el);
            safeClick(el);
            el.clear();
            try {
                el.sendKeys(Keys.chord(Keys.CONTROL, "a"), value);
            } catch (InvalidElementStateException ie) {
                jsExecutor.executeScript(
                        "arguments[0].value=arguments[1];" +
                                "arguments[0].dispatchEvent(new Event('input',{bubbles:true}));",
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

    /**
     * Simple click: try WebElement.click(), fallback to JS if intercepted.
     * Assumes caller has already waited for clickability.
     */
    public void safeClick(WebElement element) {
        try {
            element.click();
        } catch (ElementClickInterceptedException ie) {
            jsExecutor.executeScript("arguments[0].click();", element);
        }
    }

    /** Waits up to 3 s for visibility, after overlay-clear. */
    public WebElement waitUntilVisible(By locator) {
        waitForOverlayClear();
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /** Waits up to 3 s for clickability, after overlay-clear. */
    public WebElement waitUntilClickable(By locator) {
        waitForOverlayClear();
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * @deprecated in favor of waitUntilVisible(locator)
     */
    @Deprecated
    public WebElement waitUntilVisible(By locator, String alias) {
        test.info("🔎 Wait for ▶ " + alias);
        return waitUntilVisible(locator);
    }

    /** Helper to clear overlays then wait for Angular stability (<3 s). */
    public void waitForAngularRequestsToFinish() {
        try {
            new WebDriverWait(driver, DEFAULT_WAIT)
                    .until(d -> Boolean.TRUE.equals(jsExecutor
                            .executeScript("return (window.getAllAngularTestabilities||" +
                                    "window.getAllAngularTestability) ?" +
                                    " window.getAllAngularTestabilities().every(t=>t.isStable()) : true;")
                    ));
        } catch (Exception e) {
            log.debug("Skipping Angular wait: {}", e.getMessage());
        }
    }

    /** Scroll helper (unchanged). */
    public void scrollIntoView(By locator) {
        WebElement el = findElement(locator);
        jsExecutor.executeScript(
                "arguments[0].scrollIntoView({behavior:'smooth',block:'center'});",
                el
        );
    }

    protected void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Selects a value from any ng-select on the page (by formControlName).
     */
    protected void selectFromNgSelect(String formControlName, String value) {
        waitForOverlayClear();
        log.info("🔍 Attempting to select option '{}' from ng-select '{}'", value, formControlName);

        By container = By.xpath(String.format(
                "//ng-select[@formcontrolname='%s']//div[contains(@class,'ng-select-container')]",
                formControlName));
        WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(container));
        jsExecutor.executeScript("arguments[0].scrollIntoView({block:'center'});", dropdown);
        try { dropdown.click(); }
        catch(Exception e) { jsExecutor.executeScript("arguments[0].click();", dropdown); }

        By panel = By.xpath("//ng-dropdown-panel");
        wait.until(ExpectedConditions.visibilityOfElementLocated(panel));

        By filterInput = By.xpath(String.format(
                "//ng-select[@formcontrolname='%s']//input", formControlName));
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(filterInput));
        input.clear();
        input.sendKeys(value);

        By option = By.xpath(String.format(
                "//ng-dropdown-panel//div[contains(@class,'ng-option') and normalize-space()='%s']",
                value));
        try {
            wait.until(ExpectedConditions.elementToBeClickable(option)).click();
        } catch(Exception e) {
            input.sendKeys(Keys.ARROW_DOWN, Keys.ENTER);
        }

        waitForOverlayClear();
        log.info("✅ Successfully selected '{}' in '{}'", value, formControlName);
    }

    /**
     * Selects an ng-select inside the given row by formControlName + filter text.
     * Works for appendTo="body" panels.
     */
    protected void selectFromNgSelectInRow(WebElement row,
                                           String formControlName,
                                           String value) {
        // 1) open the dropdown
        WebElement container = row.findElement(By.cssSelector(
                "ng-select[formcontrolname='" + formControlName + "'] .ng-select-container"
        ));
        jsExecutor.executeScript(
                "arguments[0].scrollIntoView({block:'center'});", container
        );
        safeClick(container);

        // 2) wait for the actual filter input (role=combobox) to appear & be visible
        WebElement filter = wait.until(driver ->
                driver.findElements(By.cssSelector("input[role='combobox']"))
                        .stream()
                        .filter(WebElement::isDisplayed)
                        .findFirst()
                        .orElse(null)
        );
        filter.clear();
        filter.sendKeys(value);

        // 3) pick the matching option that's visible
        WebElement option = wait.until(driver ->
                driver.findElements(By.cssSelector(".ng-option"))
                        .stream()
                        .filter(el -> el.isDisplayed() && el.getText().trim().equals(value))
                        .findFirst()
                        .orElse(null)
        );
        safeClick(option);

        waitForOverlayClear();
    }

    protected void waitForRowToIncrease(By locator, int before, String context) {
        try {
            wait.withTimeout(Duration.ofSeconds(15))
                    .pollingEvery(Duration.ofMillis(300))
                    .until(d -> {
                        int count = driver.findElements(locator).size();
                        log.info("📊 [{}] Current count: {}", context, count);
                        return count == before + 1;
                    });
        } catch (Exception e) {
            ScreenshotHelper.capture(driver, context + "_RowIncreaseFailed");
            log.error("❌ [{}] Row did not increase after action.", context, e);
            throw new RuntimeException("Row increase failed: " + context, e);
        }
    }
}