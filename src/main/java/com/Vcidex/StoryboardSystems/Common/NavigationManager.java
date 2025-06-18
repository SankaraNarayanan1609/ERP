// File: src/main/java/com/Vcidex/StoryboardSystems/Common/NavigationManager.java

/**
 * NavigationManager is responsible for handling all top-level and side-menu navigation
 * within the SBS ERP application UI.
 *
 * It provides multiple flexible methods to go from one module to another and ensures
 * that spinners/loaders/overlays are handled during page transitions.
 *
 * This class extends BasePage to reuse WebDriver, WebDriverWait, JavaScriptExecutor, and logger utilities.
 */
package com.Vcidex.StoryboardSystems.Common;

import static com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger.step;

import com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.PerformanceLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.ReportManager;
import com.aventstack.extentreports.ExtentTest;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.Map;

public class NavigationManager extends BasePage {

    // Navigation timeout duration in seconds
    private static final int NAV_TIMEOUT = 20;

    // Converted timeout into Duration object (used by waits)
    private static final Duration TIMEOUT = Duration.ofSeconds(NAV_TIMEOUT);

    // CSS selectors for various types of overlays/spinners that must disappear before interacting with the page
    private static final By OVERLAYS = By.cssSelector(
            "ngx-spinner, .cdk-overlay-backdrop, .modal-backdrop, .loader"
    );

    /**
     * Constructor that passes WebDriver to the BasePage class.
     *
     * @param driver WebDriver instance from test setup
     */
    public NavigationManager(WebDriver driver) {
        super(driver);
    }

    /**
     * Navigate using 3-step structured flow: top-level module → side menu → submenu.
     * Used for standard application flows like "Purchase → PO → Direct PO".
     *
     * @param module  Top-level module name (e.g., "Purchase")
     * @param menu    Menu name in side nav (e.g., "PO")
     * @param subMenu Submenu name under the menu (e.g., "Direct PO")
     */
    public void goTo(String module, String menu, String subMenu) {
        ExtentTest test = ReportManager.getTest();
        PerformanceLogger.start("Navigation: " + module + "→" + menu + "→" + subMenu);

        // Step 1: Click on the top module like Purchase, Sales, Inventory etc.
        step(MasterLogger.Layer.UI, "Click module: " + module, () -> {
            clickNavItem(module, "head-content-left");
            return null;
        });

        // Step 2: Click on the side menu within that module
        step(MasterLogger.Layer.UI, "Click side menu: " + menu, () -> {
            clickNavItem(menu, "sidenav-nav");
            return null;
        });

        // Step 3: Click on the submenu item under the selected menu
        step(MasterLogger.Layer.UI, "Click submenu: " + subMenu, () -> {
            clickNavItem(subMenu, "sublevel-nav");
            return null;
        });

        // Step 4: Wait until the target page is loaded (validated using URL or page header)
        step(MasterLogger.Layer.UI, "Verify page loaded for: " + subMenu, () -> {
            wait.until(d ->
                    d.getCurrentUrl().toLowerCase().contains(subMenu.replaceAll("\\s+", "").toLowerCase())
                            || d.findElements(By.xpath("//*[contains(text(),'" + subMenu + "')]")).size() > 0
            );
            return null;
        });

        PerformanceLogger.end("Navigation: " + module + "→" + menu + "→" + subMenu);
    }

    /**
     * Waits for a given element (usually a spinner or overlay) to become invisible.
     * Useful to ensure page has finished loading before proceeding.
     *
     * @param locator CSS or XPath locator for the element
     * @param message Step description for logging
     */
    public void waitUntilInvisible(By locator, String message) {
        step(MasterLogger.Layer.WAIT, message, () -> {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
            return null;
        });
    }

    /**
     * Fallback navigation method for older flows where we only have a sequence of labels
     * without needing module/menu separation.
     *
     * @param labels Sequence of clickable text labels (e.g., {"Purchase", "PO", "Direct PO"})
     */
    public void goTo(String... labels) {
        for (String label : labels) {
            clickNavItem(label, null);
        }
    }

    /**
     * Dynamic map-based navigation, allowing different container class mappings for each label.
     *
     * @param labelToContainerClass Map where key = label text, value = container class name
     */
    public void navigatePath(Map<String, String> labelToContainerClass) {
        for (var e : labelToContainerClass.entrySet()) {
            clickNavItem(e.getKey(), e.getValue());
        }
    }

    /**
     * Attempts to click a navigation item using either direct click or JavaScript fallback.
     * Retries up to 3 times if interaction fails.
     *
     * @param label          The text label of the clickable element (e.g., "Purchase")
     * @param containerClass Optional container class to narrow down search (e.g., "sidenav-nav")
     */
    private void clickNavItem(String label, String containerClass) {
        By locator = By.xpath(buildXpath(label, containerClass));

        // Ensure spinners/overlays are not present before interaction
        waitForOverlayClear();

        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                // Wait until element is clickable
                WebElement el = waitUntilClickable(locator);

                try {
                    // Try normal Selenium click
                    el.click();
                } catch (WebDriverException e) {
                    // If click fails, use JavaScript to click (useful for hidden/obstructed elements)
                    jsExecutor.executeScript("arguments[0].click();", el);
                }

                // Wait again in case the click triggers loaders/spinners
                waitForOverlayClear();
                return;

            } catch (Exception e) {
                // If all 3 attempts fail, throw meaningful error
                if (attempt == 3) {
                    throw new RuntimeException(
                            "Failed to click nav item '" + label + "' in '" + containerClass + "' after 3 tries", e
                    );
                }

                // Wait before retrying
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {}
            }
        }
    }

    /**
     * Builds the XPath expression to locate a clickable navigation element.
     *
     * @param label          The visible text to match
     * @param containerClass Specific container class (e.g., 'sidenav-nav') to scope the search
     * @return XPath string that can be used to locate the element
     */
    private static String buildXpath(String label, String containerClass) {
        if ("head-content-left".equals(containerClass)) {
            return "//div[contains(@class,'head-content-left')]"
                    + "//button[contains(@class,'head-menu-item-name-btn') and normalize-space(text())='"
                    + label + "']";
        }
        if ("sidenav-nav".equals(containerClass)) {
            return "//ul[contains(@class,'sidenav-nav')]"
                    + "//a[.//i[@title='" + label + "']]";
        }
        if ("sublevel-nav".equals(containerClass)) {
            return "//ul[contains(@class,'sublevel-nav')]"
                    + "//a[.//span[contains(@class,'sublevel-link-text') and normalize-space(text())='"
                    + label + "']]";
        }

        if (containerClass != null && !containerClass.isEmpty()) {
            return "//*[contains(@class,'" + containerClass + "')]"
                    + "//*[normalize-space(text())='" + label + "']";
        }

        // Fallback XPath for generic clickable element with matching label
        return "//*[normalize-space(text())='" + label + "']";
    }
}