// UIActionLogger.java
package com.Vcidex.StoryboardSystems.Utils.Logger;

import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static com.Vcidex.StoryboardSystems.Utils.Logger.ReportManager.getTest;
import static com.Vcidex.StoryboardSystems.Utils.Logger.DiagnosticsLogger.onFailure;

/**
 * Logs user interactions such as click, type, and select operations.
 * Delegates failure diagnostics to DiagnosticsLogger.
 */
public class UIActionLogger {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(UIActionLogger.class);
    private static final Duration SHORT_WAIT = Duration.ofSeconds(3);
    private static final Duration DEFAULT_WAIT = Duration.ofSeconds(12);

    /** Log a debug message. */
    public static void debug(String message) {
        logger.debug(message);
    }

    // ───── Click ─────────────────────────────────────────
    public static void click(WebDriver driver, By locator, String name) {
        click(driver, locator, name, getTest());
    }

    public static void click(WebDriver driver, By locator, String name, ExtentTest node) {
        try {
            logger.debug("Click → {} | {}", name, locator);
            node.info("🖱 Click ▶ " + name);
            WebElement el = new WebDriverWait(driver, DEFAULT_WAIT)
                    .until(ExpectedConditions.elementToBeClickable(locator));
            el.click();
            node.pass("✅ Clicked " + name);
        } catch (Exception e) {
            onFailure(driver, "Click: " + name);
            throw e;
        }
    }

    // ───── Type ─────────────────────────────────────────
    public static void type(WebDriver driver, By locator, String value, String name) {
        type(driver, locator, value, name, getTest());
    }

    public static void type(WebDriver driver, By locator, String value, String name, ExtentTest node) {
        try {
            logger.debug("Type → {} | '{}' | {}", name, value, locator);
            node.info("⌨️ Type ▶ " + name + " : '" + value + "'");
            WebElement el = new WebDriverWait(driver, DEFAULT_WAIT)
                    .until(ExpectedConditions.visibilityOfElementLocated(locator));
            el.clear();
            el.sendKeys(value);
            node.pass("✅ Typed '" + value + "' into " + name);
        } catch (Exception e) {
            onFailure(driver, "Type: " + name);
            throw e;
        }
    }

    public static void submit(WebDriver driver, By locator, String name) {
        submit(driver, locator, name, getTest());
    }

    public static void submit(WebDriver driver, By locator, String name, ExtentTest node) {
        try {
            logger.debug("Submit → {} | {}", name, locator);
            node.info("📤 Submit ▶ " + name);
            WebElement el = new WebDriverWait(driver, DEFAULT_WAIT)
                    .until(ExpectedConditions.elementToBeClickable(locator));
            // either use the native submit()…
            el.submit();
            // …or if that doesn’t work for your app, fallback to click():
            // el.click();
            node.pass("✅ Submitted " + name);
        } catch (Exception e) {
            onFailure(driver, "Submit: " + name);
            throw e;
        }
    }

    // ───── Select (Ng-Select) ───────────────────────────
    public static void selectFromNgSelect(WebDriver driver, String formControlName, String valueToSelect, ExtentTest node) {
        String context = "Select: " + formControlName;
        try {
            WebDriverWait wait = new WebDriverWait(driver, DEFAULT_WAIT);
            // wait overlay
            By overlay = By.cssSelector(".spinner-overlay, .modal-backdrop");
            try { wait.until(ExpectedConditions.invisibilityOfElementLocated(overlay)); } catch (TimeoutException ignored) {}

            // open
            By container = By.xpath(
                    "//ng-select[@formcontrolname='" + formControlName + "']//div[contains(@class,'ng-select-container')]"
            );
            WebElement cont = wait.until(ExpectedConditions.elementToBeClickable(container));
            ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView({block:'center'});", cont);
            cont.click();

            // type filter
            By input = By.xpath(
                    "//ng-select[@formcontrolname='" + formControlName + "' and contains(@class,'ng-select-opened')]//input[@type='text']"
            );
            WebElement inputBox = wait.until(ExpectedConditions.visibilityOfElementLocated(input));
            inputBox.clear();
            inputBox.sendKeys(valueToSelect);

            // wait options
            Thread.sleep(300);
            By panel = By.cssSelector(".ng-dropdown-panel");
            wait.until(ExpectedConditions.visibilityOfElementLocated(panel));

            // log options
            List<WebElement> opts = driver.findElements(
                    By.cssSelector(".ng-dropdown-panel .ng-option")
            );
            StringBuilder sb = new StringBuilder();
            for (WebElement o : opts) sb.append("[").append(o.getText()).append("] ");
            node.info("Options after filter = " + sb);

            // select
            String xpath = "//div[contains(@class,'ng-dropdown-panel')]//span[normalize-space(text())='" + valueToSelect + "']";
            boolean clicked = false;
            for (int i = 1; i <= 3; i++) {
                try {
                    WebElement opt = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
                    ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", opt);
                    opt.click();
                    node.pass("✅ Selected " + valueToSelect + " on attempt " + i);
                    clicked = true;
                    break;
                } catch (Exception ex) {
                    node.info("Attempt " + i + " failed: " + ex.getClass().getSimpleName());
                    Thread.sleep(200);
                }
            }
            if (!clicked) throw new NoSuchElementException("Could not select '" + valueToSelect + "'");
        } catch (Exception e) {
            onFailure(driver, context);
            throw new RuntimeException(e);
        }
    }

    // ───── Safe JS Click ──────────────────────────────────
    private static void safeClick(WebDriver driver, WebElement el) {
        try {
            new WebDriverWait(driver, SHORT_WAIT).until(d -> {
                Point p = el.getLocation();
                return el.isDisplayed() && el.isEnabled() &&
                        ((JavascriptExecutor)d).executeScript(
                                "return document.elementFromPoint(arguments[0],arguments[1])===arguments[2];",
                                p.getX()+el.getSize().getWidth()/2,
                                p.getY()+el.getSize().getHeight()/2,
                                el
                        ).equals(true);
            });
            el.click();
        } catch (Exception e) {
            ((JavascriptExecutor)driver).executeScript("arguments[0].click();", el);
        }
    }

    /**
     * Delegate UI failure handling entirely to DiagnosticsLogger
     */
    public static void failure(WebDriver driver, String context) {
        onFailure(driver, context);
    }
}