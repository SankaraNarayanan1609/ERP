package com.Vcidex.StoryboardSystems.Common;

import com.Vcidex.StoryboardSystems.Utils.ButtonHelper;
import com.Vcidex.StoryboardSystems.Utils.Logger.DiagnosticsLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.ReportManager;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class BasePage {
    public static final Logger log = LoggerFactory.getLogger(BasePage.class);

    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final ButtonHelper buttons;
    protected final Actions actions;
    protected final JavascriptExecutor jsExecutor;

    private static final int TIMEOUT = Integer.parseInt(
            com.Vcidex.StoryboardSystems.Utils.Config.ConfigManager.getProperty("timeout", "3")
    );
    private static final Duration DEFAULT_WAIT = Duration.ofSeconds(TIMEOUT);
    protected static final Duration LONG_WAIT  = Duration.ofSeconds(30);

    private static final By ALL_OVERLAYS = By.cssSelector(
            ".spinner-overlay, .modal-backdrop, .blockUI, .blockOverlay, .ngx-spinner-overlay, ngx-spinner, " +
                    ".cdk-overlay-backdrop, .loader, .overlay, .spinner"
    );

    protected ExtentTest T(){ return ReportManager.getTest(); }

    public BasePage(WebDriver driver) {
        this.driver   = Objects.requireNonNull(driver);
        this.wait     = new WebDriverWait(driver, DEFAULT_WAIT);
        this.actions  = new Actions(driver);
        this.jsExecutor = (JavascriptExecutor) driver;
        this.buttons  = new ButtonHelper(driver, Duration.ofSeconds(30));
    }

    public WebDriver getDriver(){ return driver; }

    /** Wait (up to ~20s) for loading overlays to disappear. */
    public void waitForOverlayClear() {
        long deadline = System.currentTimeMillis() + 20000;
        while (System.currentTimeMillis() < deadline) {
            try {
                List<WebElement> els = driver.findElements(ALL_OVERLAYS);
                boolean anyVisible = false;
                for (WebElement el : els) {
                    try { if (el.isDisplayed()) { anyVisible = true; break; } }
                    catch (StaleElementReferenceException ignored) {}
                }
                if (!anyVisible) return;
            } catch (Exception ignored) {}
            sleep(150);
        }
    }

    protected WebElement findElement(By locator) {
        try {
            waitForOverlayClear();
            return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (Exception e) {
            DiagnosticsLogger.onFailure(driver, "findElement: " + locator, e);
            throw e;
        }
    }

    public void click(By locator, String name) {
        try {
            waitForOverlayClear();
            try { driver.switchTo().activeElement().sendKeys(Keys.ESCAPE); } catch (Exception ignore) {}
            WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
            scrollIntoView(el);
            try { el.click(); } catch (WebDriverException e) { jsClick(el); }
        } catch (Exception e) {
            DiagnosticsLogger.onFailure(driver, "Click " + name, e);
            Assert.fail("Click failed: " + name);
        }
    }

    protected void safeClick(WebElement element) {
        try { element.click(); } catch (WebDriverException e) { jsClick(element); }
    }

    protected void safeClick(By locator, String context) {
        try {
            WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
            scrollIntoView(el);
            safeClick(el);
        } catch (Exception e) {
            DiagnosticsLogger.onFailure(driver, "Click " + context, e);
            Assert.fail("Step failed: " + context);
        }
    }

    public void clickByLabel(String label) { this.buttons.click(label); }
    /** Types with visibility wait and JS fallback for non-editable inputs. */
    public void type(By locator, String value, String name) {
        try {
            waitForOverlayClear();
            if (T() != null) T().info("⌨️ Type ▶ " + name + " : '" + value + "'");
            WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
            scrollIntoView(el);
            safeClick(el);
            el.clear();
            try {
                el.sendKeys(Keys.chord(Keys.CONTROL, "a"), value);
            } catch (InvalidElementStateException ie) {
                jsExecutor.executeScript(
                        "arguments[0].value=arguments[1];" +
                                "arguments[0].dispatchEvent(new Event('input',{bubbles:true}));" +
                                "arguments[0].dispatchEvent(new Event('change',{bubbles:true}));",
                        el, value
                );
                if (T() != null) T().info("⚠️ JS fallback used for typing into '" + name + "'");
            }
            if (T() != null) T().pass("✅ Typed '" + value + "' into " + name);
        } catch (Exception e) {
            DiagnosticsLogger.onFailure(driver, "Type '" + value + "' in " + name, e);
            throw e;
        }
    }
    /** Quiet typing: single, hardened version (remove any duplicate method). */
    public void typeQuiet(By locator, String value, String name) {
        try {
            waitForOverlayClear();
            WebElement el = new WebDriverWait(driver, LONG_WAIT)
                    .until(ExpectedConditions.visibilityOfElementLocated(locator));

            scrollIntoView(el);

            // try to focus/click if possible; ok if not clickable
            try {
                new WebDriverWait(driver, Duration.ofSeconds(2))
                        .until(ExpectedConditions.elementToBeClickable(el));
                safeClick(el);
            } catch (Exception ignored) {}

            try {
                el.clear();
                el.sendKeys(Keys.chord(Keys.CONTROL, "a"), value);
            } catch (InvalidElementStateException ie) {   // <-- only catch the parent once
                jsExecutor.executeScript(
                        "arguments[0].value=arguments[1];" +
                                "arguments[0].dispatchEvent(new Event('input',{bubbles:true}));" +
                                "arguments[0].dispatchEvent(new Event('change',{bubbles:true}));",
                        el, value
                );
            }
        } catch (Exception e) {
            DiagnosticsLogger.onFailure(driver, "TypeQuiet '" + value + "' in " + name, e);
            throw e;
        }
    }

    /** Type into an input that lives inside a given row (quiet). */
    public void typeInRowQuiet(WebElement row, By inputLocator, String value, String name) {
        try {
            waitForOverlayClear();
            WebElement el = row.findElement(inputLocator);
            scrollIntoView(el);
            safeClick(el);
            el.clear();
            el.sendKeys(Keys.chord(Keys.CONTROL, "a"), value);
        } catch (Exception e) {
            DiagnosticsLogger.onFailure(driver, "TypeInRowQuiet '" + value + "' in " + name, e);
            throw e;
        }
    }

    // ---------- ng-select helpers ----------
    public void selectFromNgSelect(String formControlName, String value) {
        By container = By.xpath("//ng-select[@formcontrolname=" + xpathLiteral(formControlName) + "]//div[contains(@class,'ng-select-container')]");
        WebElement box = new WebDriverWait(driver, LONG_WAIT).until(ExpectedConditions.elementToBeClickable(container));
        scrollIntoView(box);
        try { box.click(); } catch (Exception e) { jsClick(box); }

        By panel = By.xpath("//ng-dropdown-panel[not(contains(@class,'ng-select-hidden'))]");
        waitPanelOpen(panel);

        driver.findElements(By.cssSelector("ng-dropdown-panel input[type='text'], ng-dropdown-panel input[role='combobox']"))
                .stream().findFirst().ifPresent(inp -> {
                    inp.sendKeys(Keys.chord(Keys.CONTROL, "a"));
                    inp.sendKeys(value == null ? "" : value.trim());
                    sleep(150);
                });

        String canon = value == null ? "" : value.replaceAll("\\s+"," ").trim();
        By exact = By.xpath("//ng-dropdown-panel//div[contains(@class,'ng-option') and not(contains(@class,'disabled'))][normalize-space(.)=" + xpathLiteral(canon) + "]");
        By containsCI = By.xpath("//ng-dropdown-panel//div[contains(@class,'ng-option') and not(contains(@class,'disabled'))][contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), translate(" + escapeXpathLiteral(canon) + ",'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'))]");

        WebElement opt;
        try {
            opt = new WebDriverWait(driver, LONG_WAIT).until(ExpectedConditions.elementToBeClickable(exact));
        } catch (TimeoutException te) {
            opt = new WebDriverWait(driver, LONG_WAIT).until(ExpectedConditions.elementToBeClickable(containsCI));
        }

        scrollIntoView(opt);
        try { opt.click(); } catch (Exception e) { jsClick(opt); }
        try { waitPanelClosed(panel); } catch (Exception ignored) {}
    }

    public void selectFromNgSelectInRow(String formControlName, String value) {
        By rowSel = By.xpath("//tr[.//ng-select[@formcontrolname=" + xpathLiteral(formControlName) + "]]");
        WebElement row = new WebDriverWait(driver, LONG_WAIT).until(ExpectedConditions.presenceOfElementLocated(rowSel));
        selectFromNgSelectInRow(row, formControlName, value);
    }

    public void selectFromNgSelectInRow(WebElement row, String formControlName, String value) {
        By container = By.xpath(".//ng-select[@formcontrolname=" + xpathLiteral(formControlName) + "]//div[contains(@class,'ng-select-container')]");
        WebElement box = new WebDriverWait(driver, LONG_WAIT).until(ExpectedConditions.elementToBeClickable(row.findElement(container)));
        scrollIntoView(box);
        try { box.click(); } catch (Exception e) { jsClick(box); }

        By panel = By.xpath("//ng-dropdown-panel[not(contains(@class,'ng-select-hidden'))]");
        waitPanelOpen(panel);

        driver.findElements(By.cssSelector("ng-dropdown-panel input[type='text'], ng-dropdown-panel input[role='combobox']"))
                .stream().findFirst().ifPresent(inp -> {
                    inp.sendKeys(Keys.chord(Keys.CONTROL, "a"));
                    inp.sendKeys(value == null ? "" : value.trim());
                    sleep(150);
                });

        String canon = value == null ? "" : value.replaceAll("\\s+"," ").trim();
        By exact = By.xpath("//ng-dropdown-panel//div[contains(@class,'ng-option') and not(contains(@class,'disabled'))][normalize-space(.)=" + xpathLiteral(canon) + "]");
        By containsCI = By.xpath("//ng-dropdown-panel//div[contains(@class,'ng-option') and not(contains(@class,'disabled'))][contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), translate(" + escapeXpathLiteral(canon) + ",'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'))]");

        WebElement opt;
        try {
            opt = new WebDriverWait(driver, LONG_WAIT).until(ExpectedConditions.elementToBeClickable(exact));
        } catch (TimeoutException te) {
            opt = new WebDriverWait(driver, LONG_WAIT).until(ExpectedConditions.elementToBeClickable(containsCI));
        }

        scrollIntoView(opt);
        try { opt.click(); } catch (Exception e) { jsClick(opt); }
        try { waitPanelClosed(panel); } catch (Exception ignored) {}
    }

    public void waitForRowToIncrease(By rowsLocator, int beforeCount, String label) {
        try {
            wait.until(d -> d.findElements(rowsLocator).size() > beforeCount);
        } catch (Exception e) {
            DiagnosticsLogger.onFailure(driver, "waitForRowToIncrease: " + label, e);
            throw e;
        }
    }

    public WebElement waitUntilVisible(By locator) {
        waitForOverlayClear();
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
    public WebElement waitUntilVisible(By locator, String alias) {
        if (T() != null) T().info("🔎 Wait for ▶ " + alias);
        return waitUntilVisible(locator);
    }

    public WebElement waitUntilClickable(By locator) {
        waitForOverlayClear();
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }
    public WebElement waitUntilClickable(By locator, String alias) {
        if (T() != null) T().info("🖱️ Wait clickable ▶ " + alias);
        return waitUntilClickable(locator);
    }

    public void waitForAngularRequestsToFinish() {
        try {
            new WebDriverWait(driver, DEFAULT_WAIT).until(d -> Boolean.TRUE.equals(
                    jsExecutor.executeScript(
                            "return (window.getAllAngularTestabilities||window.getAllAngularTestability) ? " +
                                    "window.getAllAngularTestabilities().every(t=>t.isStable()) : true;")));
        } catch (Exception e) {
            log.debug("Skipping Angular wait: {}", e.getMessage());
        }
    }

    public void scrollIntoView(By locator) {
        WebElement el = findElement(locator);
        jsExecutor.executeScript("arguments[0].scrollIntoView({behavior:'smooth',block:'center'});", el);
    }
    protected void sleep(long millis) {
        try { Thread.sleep(millis); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    protected static String escapeXpathLiteral(String s) {
        if (s == null) return "''";
        if (!s.contains("'"))  return "'" + s + "'";
        if (!s.contains("\"")) return "\"" + s + "\"";
        String[] parts = s.split("'");
        StringBuilder sb = new StringBuilder("concat(");
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) sb.append(", \"'\", ");
            sb.append('"').append(parts[i]).append('"');
        }
        sb.append(")");
        return sb.toString();
    }
    protected static String xpathLiteral(String s) {
        if (s == null) return "''";
        if (s.indexOf('\'') == -1) return "'" + s + "'";
        if (s.indexOf('"')  == -1) return "\"" + s + "\"";
        String[] parts = s.split("'");
        StringBuilder out = new StringBuilder("concat(");
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) out.append(",'\"',");
            out.append("'").append(parts[i]).append("'");
        }
        return out.append(")").toString();
    }

    protected void jsClick(WebElement el) { jsExecutor.executeScript("arguments[0].click();", el); }
    protected void scrollIntoView(WebElement el) { jsExecutor.executeScript("arguments[0].scrollIntoView({block:'center',inline:'center'});", el); }
    protected void waitPanelOpen(By panel) { new WebDriverWait(driver, LONG_WAIT).until(ExpectedConditions.visibilityOfElementLocated(panel)); }
    protected void waitPanelClosed(By panel) { new WebDriverWait(driver, LONG_WAIT).until(ExpectedConditions.invisibilityOfElementLocated(panel)); }

    public void waitForAngularAndIdle() {
        try {
            wait.until(driver -> ((JavascriptExecutor)driver).executeScript("return document.readyState").equals("complete"));
            long stableNeededMs = 300L, start = System.currentTimeMillis(), lastDom = -1;
            while (System.currentTimeMillis() - start < 4000) {
                Long now = (Long)((JavascriptExecutor)driver).executeScript("return (performance.timing.domComplete||Date.now())");
                if (lastDom != -1 && (now - lastDom) >= stableNeededMs) break;
                lastDom = now; Thread.sleep(100);
            }
            wait.until(d -> ((JavascriptExecutor)d).executeScript("return !!document.querySelector('.ngx-spinner,.global-overlay')") == Boolean.FALSE);
        } catch (Exception ignored) { }
    }

    /** Try common toast/alert containers first, else fall back to page body text. */
    protected String captureTextFromUi() {
        By nodes = By.xpath("//*[contains(@class,'toast') or contains(@class,'swal2') or contains(@class,'alert')]//*[self::div or self::span or self::p or self::h2 or self::h3 or self::h4]");
        try {
            List<WebElement> els = driver.findElements(nodes);
            for (WebElement el : els) {
                try { String t = el.getText(); if (t != null && !t.isBlank()) return t; }
                catch (StaleElementReferenceException ignored) {}
            }
        } catch (Exception ignored) {}
        try { return driver.findElement(By.tagName("body")).getText(); }
        catch (Exception ignored) { return ""; }
    }

    /** PUBLIC so pages can reuse it (e.g., GRN/INWARD extractors). */
    public String captureRefByRegex(String... patterns) {
        try {
            waitForAngularRequestsToFinish();
            waitForOverlayClear();
            String text = driver.findElement(By.tagName("body")).getText();
            for (String p : patterns) {
                java.util.regex.Matcher m = java.util.regex.Pattern
                        .compile(p, java.util.regex.Pattern.CASE_INSENSITIVE)
                        .matcher(text);
                if (m.find()) return m.group(0).replaceAll(".*[:#]\\s*", "").trim();
            }
        } catch (Exception ignored) {}
        return null;
    }

    /** Presence-only wait. */
    public WebElement waitUntilPresent(By locator) {
        waitForOverlayClear();
        return new WebDriverWait(driver, LONG_WAIT).until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /** Safe, reusable invalid-controls logger. */
    protected void dumpInvalidControlsIfAny() {
        String names = listInvalidControls();
        if (names != null && !names.isBlank()) {
            try {
                var t = ReportManager.getTest();
                if (t != null) t.warning("⛔ Invalid controls before submit: <b>" + names + "</b>");
            } catch (Exception ignored) {}
            log.warn("Invalid controls before submit: {}", names);
        }
    }

    /** Returns a comma-separated list of invalid control names visible on the current form. */
    /** List invalid Angular controls by formcontrolname (used by dumpInvalidControlsIfAny). */
    protected String listInvalidControls() {
        try {
            List<WebElement> invalids = driver.findElements(By.cssSelector("form .ng-invalid[formcontrolname]"));
            if (invalids.isEmpty()) return "";
            return invalids.stream()
                    .map(e -> e.getAttribute("formcontrolname"))
                    .filter(Objects::nonNull)
                    .distinct()
                    .reduce((a,b)->a+", "+b)
                    .orElse("");
        } catch (Exception e) { return ""; }
    }

    private void clickAwayToCommit() {
        try { driver.findElement(By.tagName("body")).click(); } catch (Exception ignored) {}
    }

    private boolean isNgValid(WebElement ngSelect) {
        try {
            String cls = ngSelect.getAttribute("class");
            return cls != null && cls.contains("ng-valid") && !cls.contains("ng-invalid");
        } catch (Exception e) { return false; }
    }

    private boolean isNgValid(String formControl) {
        try {
            WebElement el = driver.findElement(By.cssSelector("ng-select[formcontrolname='"+formControl+"']"));
            return isNgValid(el);
        } catch (Exception e) { return false; }
    }
}