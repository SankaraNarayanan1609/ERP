package com.Vcidex.StoryboardSystems.Common;

import com.Vcidex.StoryboardSystems.Utils.Logger.UIActionLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.ErrorLogger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.Vcidex.StoryboardSystems.Utils.DebugUtils;

import java.io.File;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class NavigationManager {
    private final WebDriver driver;
    private final int NAV_TIMEOUT = 20; // seconds

    public NavigationManager(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Builds the xpath for a given label and container.
     */
    private static String buildXpath(String label, String containerClass) {
        if ("head-content-left".equals(containerClass)) {
            // Module/top nav
            return "//div[contains(@class,'head-content-left')]//button[contains(@class,'head-menu-item-name-btn') and normalize-space(text())='" + label + "']";
        } else if ("sidenav-nav".equals(containerClass)) {
            // Side nav menu: title in <i title="">
            return "//ul[contains(@class,'sidenav-nav')]//a[.//i[@title='" + label + "']]";
        } else if ("sublevel-nav".equals(containerClass)) {
            // Submenu: click the <a> that has the <span> with the text
            return "//ul[contains(@class,'sublevel-nav')]//a[.//span[contains(@class,'sublevel-link-text') and normalize-space(text())='" + label + "']]";
        } else if (containerClass != null && !containerClass.isEmpty()) {
            // Fallback for other containers (not expected for nav)
            return "//*[contains(@class,'" + containerClass + "')]//*[normalize-space(text())='" + label + "']";
        } else {
            // Fallback global search
            return "//*[normalize-space(text())='" + label + "']";
        }
    }

    // Wait for clickable, tries Actions and JS, retries on failure
    private void clickNavWithRetry(String label, String containerClass, int timeoutSeconds) {
        By locator = By.xpath(buildXpath(label, containerClass));
        int attempts = 3;
        Exception last = null;
        for (int i = 0; i < attempts; i++) {
            try {
                // Wait for overlays/spinners
                new WebDriverWait(driver, Duration.ofSeconds(20))
                        .until(ExpectedConditions.invisibilityOfElementLocated(
                                By.cssSelector("ngx-spinner, .cdk-overlay-backdrop, .modal-backdrop, .loader")));
            } catch (Exception ignored) {}

            try {
                new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                        .until(ExpectedConditions.visibilityOfElementLocated(locator));
                WebElement el = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                        .until(ExpectedConditions.elementToBeClickable(locator));
                try {
                    new Actions(driver).moveToElement(el).pause(Duration.ofMillis(150)).click().perform();
                } catch (Exception ex) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
                }
                DebugUtils.waitForAngular(driver);
                new WebDriverWait(driver, Duration.ofSeconds(20))
                        .until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("ngx-spinner, .cdk-overlay-backdrop, .modal-backdrop, .loader")));
                // Screenshot after click for debugging
                try {
                    File src = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
                    java.nio.file.Files.copy(src.toPath(), Paths.get("after_click_" + label.replaceAll("\\s+", "_") + ".png"), StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception e) {}
                UIActionLogger.click(driver, locator, label);
                return;
            } catch (Exception e) {
                last = e;
                try { Thread.sleep(500); } catch (InterruptedException ignored) {}
            }
        }
        throw new RuntimeException("Could not click '" + label + "' in '" + containerClass + "'", last);
    }

    private void openSideMenuAndSubmenu(String menuLabel, String subMenuLabel, int timeoutSeconds) {
        By menuLocator = By.xpath("//ul[contains(@class,'sidenav-nav')]//a[.//i[@title='" + menuLabel + "']]");
        By submenuSpanLocator = By.xpath("//ul[contains(@class,'sublevel-nav')]//span[contains(@class,'sublevel-link-text') and normalize-space(text())='" + subMenuLabel + "']");
        By submenuLinkLocator = By.xpath("//ul[contains(@class,'sublevel-nav')]//a[.//span[contains(@class,'sublevel-link-text') and normalize-space(text())='" + subMenuLabel + "']]");

        Exception last = null;
        for (int attempt = 1; attempt <= 5; attempt++) {
            try {
                WebElement menu = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                        .until(ExpectedConditions.visibilityOfElementLocated(menuLocator));
                new Actions(driver).moveToElement(menu).pause(Duration.ofMillis(600)).perform();
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", menu);
                menu.click();
                Thread.sleep(600); // Give time for submenu to appear

                // Defensive: Wait up to 5 seconds for submenus to be present after menu click
                List<WebElement> allSubmenus = null;
                for (int wait=0; wait<5; wait++) {
                    allSubmenus = driver.findElements(submenuSpanLocator);
                    if (!allSubmenus.isEmpty()) break;
                    Thread.sleep(500);
                }

                System.out.println("Attempt #" + attempt + " [DEBUG] Submenus found:");
                for (WebElement sm : allSubmenus) System.out.println("  -> [" + sm.getText().trim() + "]");

                WebElement submenuSpan = null;
                for (WebElement sm : allSubmenus) {
                    if (sm.getText().trim().equals(subMenuLabel)) {
                        submenuSpan = sm;
                        break;
                    }
                }
                if (submenuSpan == null) throw new RuntimeException("Submenu '" + subMenuLabel + "' not found in DOM!");

                WebElement submenuLink = submenuSpan.findElement(By.xpath("./ancestor::a[1]"));
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", submenuLink);

                try {
                    new Actions(driver).moveToElement(submenuLink).pause(Duration.ofMillis(150)).click().perform();
                } catch (Exception ex) {
                    try {
                        submenuLink.click();
                    } catch (Exception e2) {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submenuLink);
                    }
                }

                // Wait for URL to update or the header
                new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                        .until(ExpectedConditions.or(
                                ExpectedConditions.urlContains("purchaseorder"),
                                ExpectedConditions.visibilityOfElementLocated(By.xpath("//h3[contains(text(),'Purchase Order')]"))
                        ));

                UIActionLogger.click(driver, submenuLinkLocator, subMenuLabel);
                return;
            } catch (Exception e) {
                last = e;
                try {
                    File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                    java.nio.file.Files.copy(src.toPath(), Paths.get("submenu_nav_attempt_" + attempt + ".png"), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception ignore) {}
                System.out.println("[DEBUG] Submenu click failed attempt #" + attempt + ": " + e.getMessage());
                try { Thread.sleep(600); } catch (InterruptedException ignored) {}
            }
        }
        throw new RuntimeException("Could not click submenu '" + subMenuLabel + "' under menu '" + menuLabel + "'", last);
    }

    public void goTo(String module, String menu, String subMenu) {
        int TIMEOUT = NAV_TIMEOUT;
        try {
            clickNavWithRetry(module, "head-content-left", TIMEOUT);
            openSideMenuAndSubmenu(menu, subMenu, TIMEOUT);

            // Final: Confirm we are on correct page (validate header/submenu loaded)
            boolean found = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT))
                    .until(d -> {
                        String url = d.getCurrentUrl();
                        // Change this condition as needed for your app:
                        return url.toLowerCase().contains("purchaseorder") ||
                                d.findElements(By.xpath("//h4[contains(text(),'Purchase Order')]")).size() > 0;
                    });
            if (!found) throw new RuntimeException("Did not reach expected page after navigation!");
        } catch (Exception e) {
            // Log the DOM for failure analysis
            try {
                String html = driver.findElement(By.tagName("body")).getAttribute("outerHTML");
                System.out.println("[DEBUG] Final body HTML on nav failure:\n" + html);
            } catch (Exception ignore) {}
            // Screenshot for debug
            try {
                File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                java.nio.file.Files.copy(src.toPath(), Paths.get("nav_failure.png"), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                System.out.println("[DEBUG] Screenshot taken: nav_failure.png");
            } catch (Exception ignore) {}
            UIActionLogger.failure(driver, "goTo(" + module + "→" + menu + "→" + subMenu + ")");
            ErrorLogger.logException(e, "Navigation error to " + module, driver);
            throw new RuntimeException("Navigation failed! " + e.getMessage(), e);
        }
    }

    /**
     * Overload for backward compatibility: navigates a path of labels, using global search.
     */
    public void goTo(String... labels) {
        for (String label : labels) {
            clickNavWithRetry(label, null, NAV_TIMEOUT);
        }
    }

    /**
     * Flexible navigation: pass navigation steps as an ordered Map: label->containerClass.
     */
    public void navigatePath(Map<String, String> labelToContainerClass) {
        for (Map.Entry<String, String> step : labelToContainerClass.entrySet()) {
            clickNavWithRetry(step.getKey(), step.getValue(), NAV_TIMEOUT);
        }
    }
}