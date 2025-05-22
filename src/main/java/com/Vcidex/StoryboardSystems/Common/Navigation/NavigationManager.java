// src/main/java/com/Vcidex/StoryboardSystems/Common/Navigation/NavigationManager.java
package com.Vcidex.StoryboardSystems.Common.Navigation;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class NavigationManager {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public NavigationManager(WebDriver driver) {
        this.driver = driver;
        // 30s timeout; feel free to adjust or externalize
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    private By moduleBtn(String module) {
        // Matches: <button class="head-menu-item-name-btn ...">Purchase</button>
        return By.xpath(String.format(
                "//button[contains(@class,'head-menu-item-name-btn') and normalize-space(text())='%s']",
                module
        ));
    }

    private By menuLink(String menu) {
        // The <i> inside the <a> has title="Purchase", so walk up to the <a>
        return By.xpath(String.format(
                "//div[contains(@class,'sidenav')]//i[@title='%s']/ancestor::a[1]",
                menu
        ));
    }

    private By submenuLink(String submenu) {
        // Matches: <span class="sublevel-link-text">Purchase Order</span>
        return By.xpath(String.format(
                "//ul[contains(@class,'sublevel-nav')]//span[normalize-space(text())='%s']/ancestor::a[1]",
                submenu
        ));
    }

    /** Clicks Module → Menu → SubMenu in order, waiting for each to be clickable. */
    public void goTo(String module, String menu, String submenu) {
        // 1) Top‐bar module
        WebElement mod = wait.until(
                ExpectedConditions.elementToBeClickable(moduleBtn(module))
        );
        mod.click();

        // 2) Sidebar menu
        WebElement m = wait.until(
                ExpectedConditions.elementToBeClickable(menuLink(menu))
        );
        m.click();

        // 3) Submenu under sublevel‐nav
        WebElement sub = wait.until(
                ExpectedConditions.elementToBeClickable(submenuLink(submenu))
        );
        sub.click();
    }
}