package com.Vcidex.StoryboardSystems.Utils.Logger;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import org.openqa.selenium.*;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.List;

import static com.Vcidex.StoryboardSystems.Utils.Logger.ExtentTestManager.getTest;

public class UIActionLogger {
    private static final Logger logger = LoggerFactory.getLogger(UIActionLogger.class);

    public static void debug(String message) {
        logger.debug(message);
    }

    // ───── Click ─────────────────────────────────────────
    public static void click(WebDriver driver, By locator, String name) {
        click(driver, locator, name, getTest());
    }

    public static void click(WebDriver driver, By locator, String name, ExtentTest node) {
        logger.debug("Click → {} | {}", name, locator);
        node.info("🖱 Click ▶ " + name);
        driver.findElement(locator).click();
        node.pass("✅ Clicked " + name);
    }

    // ───── Type ─────────────────────────────────────────
    public static void type(WebDriver driver, By locator, String value, String name) {
        type(driver, locator, value, name, getTest());
    }

    public static void type(WebDriver driver, By locator, String value, String name, ExtentTest node) {
        logger.debug("Type → {} | '{}' | {}", name, value, locator);
        node.info("⌨️ Type ▶ " + name + " : '" + value + "'");
        WebElement el = driver.findElement(locator);
        el.clear();
        el.sendKeys(value);
        node.pass("✅ Typed '" + value + "' into " + name);
    }

    public static void selectFromNgSelect(WebDriver driver, String formControlName, String valueToSelect, ExtentTest node) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(12));
        By loadingOverlay = By.cssSelector(".spinner-overlay, .modal-backdrop");

        // (1) Wait for any spinner/backdrop to disappear
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(loadingOverlay));
        } catch (TimeoutException ignored) {
            // Maybe no overlay was present—no problem
        }

        // (2) Click the ng-select container to open the dropdown
        By ngSelectContainer = By.xpath(
                "//ng-select[@formcontrolname='" + formControlName + "']" +
                        "//div[contains(@class,'ng-select-container')]"
        );
        WebElement container = wait.until(ExpectedConditions.elementToBeClickable(ngSelectContainer));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", container
        );
        container.click();

        // (3) Wait for the filter input to appear inside the opened ng-select
        By searchInput = By.xpath(
                "//ng-select[@formcontrolname='" + formControlName + "' and contains(@class,'ng-select-opened')]" +
                        "//div[contains(@class,'ng-input')]//input[@type='text']"
        );
        WebElement inputBox = wait.until(ExpectedConditions.visibilityOfElementLocated(searchInput));

        // (4) Type into the input to filter options
        inputBox.clear();
        inputBox.sendKeys(valueToSelect);

        // (5) Small pause so Ng-Select has time to filter & re-render
        try { Thread.sleep(300); } catch (InterruptedException ignored) {}

        // (6) Wait for the dropdown panel to be visible
        By panelLocator = By.xpath("//div[contains(@class,'ng-dropdown-panel')]");
        wait.until(ExpectedConditions.visibilityOfElementLocated(panelLocator));

        // (7) (Optional) Log all currently visible option texts
        List<WebElement> allOptions = driver.findElements(
                By.xpath("//div[contains(@class,'ng-dropdown-panel')]//div[@role='option']//span")
        );
        StringBuilder opts = new StringBuilder();
        for (WebElement el : allOptions) {
            opts.append("[").append(el.getText()).append("] ");
        }
        node.info("Dropdown options (after typing) = " + opts);

        // (8) Build an XPath that matches exactly the <span> you want
        String optionXPath =
                "//div[contains(@class,'ng-dropdown-panel')]//div[@role='option']//span[normalize-space(text())='"
                        + valueToSelect + "']";

        // (9) Now retry‐locate + click in a small loop to handle DOM re-render races
        final int MAX_ATTEMPTS = 3;
        boolean clicked = false;
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                // 9a) Re-find the element fresh
                WebElement optionToClick = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath(optionXPath)
                ));
                // 9b) Scroll it into view
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].scrollIntoView({block:'center'});", optionToClick
                );

                // 9c) Try a normal click
                optionToClick.click();
                clicked = true;
                node.pass("Selected [" + valueToSelect + "] on attempt " + attempt);
                break;
            } catch (StaleElementReferenceException | NoSuchElementException | TimeoutException innerEx) {
                // If stale / not found / not clickable, retry one more time
                node.info("Attempt " + attempt + " to click '" + valueToSelect + "' failed with: "
                        + innerEx.getClass().getSimpleName() + ". Retrying...");
                // Short sleep before retrying (to give Angular a moment)
                try { Thread.sleep(200); } catch (InterruptedException ignored) {}
            } catch (ElementClickInterceptedException intercepted) {
                // As a last‐resort fallback, do a JS click (in case a tiny overlay still intercepts)
                try {
                    WebElement optionToClick = driver.findElement(By.xpath(optionXPath));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", optionToClick);
                    clicked = true;
                    node.pass("Selected [" + valueToSelect + "] via JS click on attempt " + attempt);
                    break;
                } catch (StaleElementReferenceException | NoSuchElementException retryEx) {
                    node.info("JS‐click fallback on attempt " + attempt + " also got: "
                            + retryEx.getClass().getSimpleName() + ". Will retry loop.");
                    try { Thread.sleep(200); } catch (InterruptedException ignored) {}
                }
            }
        }

        if (!clicked) {
            node.fail("Failed to select [" + valueToSelect + "] after " + MAX_ATTEMPTS + " attempts");
            throw new NoSuchElementException("Could not find/click option '" + valueToSelect + "'");
        }
    }

    /**
     * Utility: Safely click an element, only when it’s not covered or overlapped by another element.
     */
    private static void safeClick(WebDriver driver, WebElement element) {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
        try {
            shortWait.until(d -> {
                if (!element.isDisplayed() || !element.isEnabled())
                    return false;
                Point point = element.getLocation();
                JavascriptExecutor js = (JavascriptExecutor) driver;
                String script =
                        "var elem = arguments[0];" +
                                "var rect = elem.getBoundingClientRect();" +
                                "return document.elementFromPoint(rect.left + rect.width/2, rect.top + rect.height/2) === elem;";
                return (Boolean) js.executeScript(script, element);
            });
            element.click();
        } catch (Exception e) {
            // Fallback to JS click (last resort)
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    // ───── Submit ────────────────────────────────────────
    public static void submit(WebDriver driver, By locator, String label) {
        submit(driver, locator, label, getTest());
    }

    public static void submit(WebDriver driver, By locator, String label, ExtentTest node) {
        logger.debug("Submit → {} | {}", label, locator);
        node.info("📤 Submit ▶ " + label);
        driver.findElement(locator).submit();
        node.pass("✅ Submitted " + label);
    }

    // ───── Snapshot & Failure ────────────────────────────
    public static void snapshot(WebDriver driver, String context) {
        try {
            String html = driver.getPageSource();
            Markup domBlock = MarkupHelper.createCodeBlock("<!-- DOM snapshot: " + context + " -->\n" + html, "html");
            getTest().info(domBlock);
        } catch (Exception e) {
            logger.warn("Could not capture DOM snapshot: {}", e.getMessage());
        }
    }

    public static void failure(WebDriver driver, String context) {
        logger.error("UI Failure context: {}", context);
        captureScreenshot(driver, context);
        try {
            driver.manage().logs().get(LogType.BROWSER).forEach(entry ->
                    logger.error("BROWSER-CONSOLE {} | {}", entry.getLevel(), entry.getMessage())
            );
        } catch (Exception e) {
            logger.error("Could not fetch console logs: {}", e.getMessage());
        }
    }

    private static void captureScreenshot(WebDriver driver, String context) {
        try {
            Path targetDir = Path.of("logs", "screenshots");
            Files.createDirectories(targetDir);
            String fileName = context.replaceAll("\\W+", "_") + "_" + System.currentTimeMillis() + ".png";
            Path target = targetDir.resolve(fileName);
            Path temp = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE).toPath();
            Files.copy(temp, target, StandardCopyOption.REPLACE_EXISTING);
            logger.error("Screenshot saved: {}", target);
        } catch (Exception e) {
            logger.error("Screenshot capture failed: {}", e.getMessage());
        }
    }
}