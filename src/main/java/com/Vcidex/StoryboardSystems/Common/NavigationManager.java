// File: src/main/java/com/Vcidex/StoryboardSystems/Common/NavigationManager.java
package com.Vcidex.StoryboardSystems.Common;

import static com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger.step;

import com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger.Layer;
import com.Vcidex.StoryboardSystems.Utils.Logger.PerformanceLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.ReportManager;
import com.aventstack.extentreports.ExtentTest;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class NavigationManager extends BasePage {
    private static final int NAV_TIMEOUT = 20; // seconds
    private static final Duration TIMEOUT = Duration.ofSeconds(NAV_TIMEOUT);
    private static final By OVERLAYS = By.cssSelector(
            "ngx-spinner, .cdk-overlay-backdrop, .modal-backdrop, .loader"
    );

    public NavigationManager(WebDriver driver) {
        super(driver);
    }

    /**
     * Navigate top‐level module → side menu → submenu.
     */
    public void goTo(String module, String menu, String subMenu) {
        ExtentTest test = ReportManager.getTest();
        PerformanceLogger.start("Navigation: " + module + "→" + menu + "→" + subMenu);

        // 1) Click module button
        step(Layer.UI, "Click module: " + module, () -> {
            clickNavItem(module, "head-content-left");
            return null;
        });

        // 2) Expand side menu & click menu
        step(Layer.UI, "Click side menu: " + menu, () -> {
            clickNavItem(menu, "sidenav-nav");
            return null;
        });

        // 3) Click submenu
        step(Layer.UI, "Click submenu: " + subMenu, () -> {
            clickNavItem(subMenu, "sublevel-nav");
            return null;
        });

        // 4) Wait for page load (URL or header check)
        step(Layer.UI, "Verify page loaded for: " + subMenu, () -> {
            wait.until(d ->
                    d.getCurrentUrl().toLowerCase().contains(subMenu.replaceAll("\\s+", "").toLowerCase())
                            || d.findElements(By.xpath("//*[contains(text(),'" + subMenu + "')]")).size() > 0
            );
            return null;
        });

        PerformanceLogger.end("Navigation: " + module + "→" + menu + "→" + subMenu);
    }

    /**
     * Backwards‐compatible: click a sequence of labels globally.
     */
    public void goTo(String... labels) {
        for (String label : labels) {
            clickNavItem(label, null);
        }
    }

    /**
     * Flexible map‐based navigation.
     */
    public void navigatePath(Map<String, String> labelToContainerClass) {
        for (var e : labelToContainerClass.entrySet()) {
            clickNavItem(e.getKey(), e.getValue());
        }
    }

    /**
     * Click a navigation item by label and container class, with retries.
     */
    private void clickNavItem(String label, String containerClass) {
        By locator = By.xpath(buildXpath(label, containerClass));

        // wait for any overlay to disappear
        waitForOverlayClear();

        // retry loop
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                // ensure clickable
                WebElement el = waitUntilClickable(locator);

                // try normal click
                try {
                    el.click();
                } catch (WebDriverException e) {
                    // fallback to JS
                    jsExecutor.executeScript("arguments[0].click();", el);
                }

                // short pause for Angular/spinners
                waitForOverlayClear();
                return;
            } catch (Exception e) {
                if (attempt == 3) {
                    throw new RuntimeException(
                            "Failed to click nav item '" + label + "' in '" + containerClass + "' after 3 tries", e
                    );
                }
                // pause before retry
                try { Thread.sleep(500); } catch (InterruptedException ignored) {}
            }
        }
    }

    /**
     * Build the XPath for a nav label and container.
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
        // global fallback
        return "//*[normalize-space(text())='" + label + "']";
    }
}