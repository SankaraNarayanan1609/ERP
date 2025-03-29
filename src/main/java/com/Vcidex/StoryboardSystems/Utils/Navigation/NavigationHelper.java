package com.Vcidex.StoryboardSystems.Utils.Navigation;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.Vcidex.StoryboardSystems.Utils.Reporting.ErrorHandler;

import java.time.Duration;

public class NavigationHelper {
    private static final Logger logger = LoggerFactory.getLogger(NavigationHelper.class);
    private WebDriver driver;
    private WebDriverWait wait;

    public NavigationHelper(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20)); // Adjusted to 20 seconds
    }

    // ✅ Click using JavaScript with Retry using ErrorHandler
    private void clickUsingJS(By locator, String text) {
        ErrorHandler.executeSafely(driver, () -> {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
            try {
                retryClick(element);
            } catch (InterruptedException e) {
                logger.error("Clicking on '{}' failed due to interruption.", text, e);
                Thread.currentThread().interrupt(); // Preserve interrupt status
                throw new RuntimeException("Clicking interrupted for: " + text, e);
            }
            logger.info("Clicked on '{}' using JavaScript.", text);
        }, "Clicking on " + text, false, "N/A");
    }

    private void retryClick(WebElement element) throws InterruptedException {
        for (int i = 0; i < 3; i++) {
            try {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
                logger.info("Clicked successfully on attempt {}", i + 1);
                return;
            } catch (Exception e) {
                logger.warn("Retrying click on attempt {} due to error: {}", i + 1, e.getMessage());
                Thread.sleep(2000); // Wait 2 seconds before retrying
            }
        }
        throw new RuntimeException("Failed to click element after 3 attempts.");
    }

    // ✅ Click Module using Button
    private void clickModuleByText(String text) {
        By locator = By.xpath("//button[contains(@class,'head-menu-item-name-btn') and contains(text(),'" + text + "')]");
        clickUsingJS(locator, "Module: " + text);
    }

    // ✅ Click Menu using Span
    private void clickMenuByText(String text) {
        By locator = By.xpath("//span[contains(@class,'sidenav-link-text') and contains(text(),'" + text + "')]");
        clickUsingJS(locator, "Menu: " + text);
    }

    // ✅ Click Sub-Menu using Span
    private void clickSubMenuByText(String text) {
        By locator = By.xpath("//span[contains(@class,'sublevel-link-text') and contains(text(),'" + text + "')]");
        clickUsingJS(locator, "Sub-Menu: " + text);
    }

    // ✅ Navigate to Module, Menu, and Submenu using ErrorHandler
    public void navigateToModuleAndMenu(String moduleName, String menuName, String subMenuName) {
        ErrorHandler.executeSafely(driver, () -> {
            logger.info("Navigating to module: {}, menu: {}, sub-menu: {}", moduleName, menuName, subMenuName);
            clickModuleByText(moduleName);
            clickMenuByText(menuName);
            clickSubMenuByText(subMenuName);
        }, "Navigating to Module and Menu", false, "N/A"); // Make isSubmit = false
    }
}