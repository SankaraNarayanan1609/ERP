package com.Vcidex.StoryboardSystems.Common;

import com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.PerformanceLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.ReportManager;
import com.aventstack.extentreports.ExtentTest;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class NavigationManager extends BasePage {

    // single, unified timeout for all navigation clicks
    private static final Duration NAV_TIMEOUT = Duration.ofSeconds(10);

    public NavigationManager(WebDriver driver) {
        super(driver);
    }

    /**
     * Navigate in one shot: module → menu → submenu → verify.
     */
    public void goTo(String module, String menu, String subMenu) {
        ExtentTest test = ReportManager.getTest();
        String key = "Navigate: " + module + " → " + menu + " → " + subMenu;
        PerformanceLogger.start(key);

        MasterLogger.step(MasterLogger.Layer.UI, key, () -> {
            // clear any overlays once up front
            waitForOverlayClear();

            // click top-level, side-nav and submenu all under one node
            clickNavItem(module, "head-content-left");
            clickNavItem(menu,   "sidenav-nav");
            clickNavItem(subMenu,"sublevel-nav");

            // final verification: URL contains submenu or header visible
            wait.until(d ->
                    d.getCurrentUrl().toLowerCase().contains(subMenu.replaceAll("\\s+", "").toLowerCase())
                            || !d.findElements(By.xpath("//*[contains(normalize-space(.),'" + subMenu + "')]")).isEmpty()
            );
        });

        PerformanceLogger.end(key);
    }

    /**
     * Clicks a nav item once, with a single wait and JS fallback.
     */
    private void clickNavItem(String label, String containerClass) {
        By locator = By.xpath(buildXpath(label, containerClass));

        // wait for it to be clickable (scrollIntoView + overlay cleared in safeClick)
        WebElement el = new WebDriverWait(driver, NAV_TIMEOUT)
                .until(ExpectedConditions.elementToBeClickable(locator));

        safeClick(el);
        waitForOverlayClear();
    }

    /**
     * Builds the XPath expression to locate a clickable navigation element.
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
                    + "//*[contains(normalize-space(.),'" + label + "')]";
        }
        if (containerClass != null && !containerClass.isEmpty()) {
            return "//*[contains(@class,'" + containerClass + "')]"
                    + "//*[normalize-space(text())='" + label + "']";
        }
        return "//*[normalize-space(text())='" + label + "']";
    }
}