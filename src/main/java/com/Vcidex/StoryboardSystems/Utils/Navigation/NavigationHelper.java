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
            retryClick(element); // No InterruptedException, so no need for try-catch here
            logger.info("Clicked on '{}' using JavaScript.", text);
            return null; // Fix: Explicitly return null
        }, "Clicking on ");
    }

    private void retryClick(WebElement element) {
        for (int i = 0; i < 3; i++) {
            try {
                this.wait.until(ExpectedConditions.elementToBeClickable(element));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
                logger.info("Clicked successfully on attempt {}", i + 1);
                return;
            } catch (Exception e) {
                logger.warn("Retrying click (attempt {}): {}", i + 1, e.getMessage());
            }
        }
        throw new RuntimeException("Failed to click element after 3 attempts.");
    }

    // ✅ Click Module using Button
    private void clickModuleByText(String text)throws InterruptedException {
        By locator = By.xpath("//button[contains(@class,'head-menu-item-name-btn') and contains(text(),'" + text + "')]");
        clickUsingJS(locator, "Module: " + text);
        Thread.sleep(1000); // TEMP: helps check if it’s a timing issue
        logger.info("✅ Module clicked. Waiting before clicking menu...");

    }

    // ✅ Click Menu using Span
    private void clickMenuByText(String text)throws InterruptedException {
        By locator = By.xpath("//span[contains(@class,'sidenav-link-text') and contains(text(),'" + text + "')]");
        clickUsingJS(locator, "Menu: " + text);
        Thread.sleep(1000); //Unhandled exception: java.lang.InterruptedException
        logger.info("✅ Menu clicked. Waiting before clicking menu...");
    }

    // ✅ Click Sub-Menu using Span
    private void clickSubMenuByText(String text) throws InterruptedException{
        By locator = By.xpath("//span[contains(@class,'sublevel-link-text') and contains(text(),'" + text + "')]");
        clickUsingJS(locator, "Sub-Menu: " + text);
        Thread.sleep(1000); // TEMP: helps check if it’s a timing issue
        logger.info("✅ SubMenu clicked. Waiting before clicking menu...");
    }

    // ✅ Navigate to Module, Menu, and Submenu using ErrorHandler
    // ✅ Navigate to Module, Menu, and Submenu using ErrorHandler
    public void navigateToModuleAndMenu(String moduleName, String menuName, String subMenuName) {
        ErrorHandler.executeSafely(driver, () -> {
            logger.info("Navigating to module: {}, menu: {}, sub-menu: {}", moduleName, menuName, subMenuName);

            try {
                clickModuleByText(moduleName);
                clickMenuByText(menuName);
                clickSubMenuByText(subMenuName);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore the interrupt status
                logger.error("❌ Navigation interrupted: {}", e.getMessage(), e);
            }

            return null;
        }, "Navigating to Module and Menu");
    }

    // ✅ Overload to accept NavigationData
    public void navigateToModuleAndMenu(NavigationData navData) {
        navigateToModuleAndMenu(
                navData.getModuleName(),
                navData.getMenuName(),
                navData.getSubMenuName()
        );
    }
}