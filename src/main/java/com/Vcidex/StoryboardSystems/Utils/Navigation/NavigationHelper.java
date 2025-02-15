package com.Vcidex.StoryboardSystems.Utils.Navigation;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class NavigationHelper {
    private static final Logger logger = LoggerFactory.getLogger(NavigationHelper.class);
    private WebDriver driver;
    private WebDriverWait wait;

    public NavigationHelper(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10)); // Adjust timeout as needed
    }

    public void navigateToModuleAndMenu(String moduleName, String menuName, String subMenuName) {
        logger.info("Navigating to module: {}, menu: {}, sub-menu: {}", moduleName, menuName, subMenuName);
        clickModuleByText(moduleName);
        clickMenuByText(menuName);
        clickSubMenuByText(subMenuName);
    }

    private void clickModuleByText(String text) {
        By locator = By.xpath("//div[contains(text(),'" + text + "')]");
        clickElement(locator);
    }
    private void clickMenuByText(String text) {
        By locator = By.xpath("//div[contains(@class,'menu') and contains(text(),'" + text + "')]");
        clickElement(locator);
    }
    private void clickSubMenuByText(String text) {
        By locator = By.xpath("//div[contains(@class,'submenu') and contains(text(),'" + text + "')]");
        clickElement(locator);
    }

    private void clickElement(By locator) {
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            element.click();
            logger.info("Clicked on element with locator: {}", locator);
        } catch (Exception e) {
            logger.error("Failed to click element with locator: {}", locator, e);
            throw new RuntimeException("Failed to click element: " + locator, e);
        }
    }
}
