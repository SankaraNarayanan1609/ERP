package com.Vcidex.StoryboardSystems.Common.Navigation;

import com.Vcidex.StoryboardSystems.Utils.Logger.UIActionLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.ErrorLogger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class NavigationManager {
    private final WebDriver driver;

    public NavigationManager(WebDriver driver) {
        this.driver = driver;
    }

    public void goTo(String menu, String subMenu, String page) {
        try {
            UIActionLogger.click(driver,
                    By.xpath("//a[normalize-space()='" + menu + "']"),
                    menu
            );
            UIActionLogger.click(driver,
                    By.xpath("//a[normalize-space()='" + subMenu + "']"),
                    subMenu
            );
            UIActionLogger.click(driver,
                    By.xpath("//a[normalize-space()='" + page + "']"),
                    page
            );
        } catch (TimeoutException te) {
            UIActionLogger.failure(driver, "goTo(" + menu + "→" + subMenu + "→" + page + ")");
            ErrorLogger.logException(te, "Navigation timeout to " + page, driver);
            throw te;
        } catch (Exception e) {
            UIActionLogger.failure(driver, "goTo(" + menu + "→" + subMenu + "→" + page + ")");
            ErrorLogger.logException(e, "Navigation error to " + page, driver);
            throw e;
        }
    }
}