package com.Vcidex.StoryboardSystems.Utils.Helpers;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

public class DropdownUtils {

    private final WebDriver driver;

    public DropdownUtils(WebDriver driver) {
        this.driver = driver;
    }

    public void selectByVisibleText(By locator, String text) {
        new Select(driver.findElement(locator)).selectByVisibleText(text);
    }
}