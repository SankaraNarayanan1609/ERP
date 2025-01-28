package com.Vcidex.StoryboardSystems.Purchase.Test;

import com.Vcidex.StoryboardSystems.Common.Base.BasePage;
import com.Vcidex.StoryboardSystems.Utils.ThreadSafeDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class TestConfigLoader {
    public static void main(String[] args) {
        // Initialize WebDriver (Example: ChromeDriver)
        WebDriver driver = new ChromeDriver();
        ThreadSafeDriverManager.setDriver(driver);

        // Navigate to the URL
        driver.get("https://erplite.storyboarderp.com");

        // Create BasePage instance
        BasePage basePage = new BasePage(driver);

        // Example operation using BasePage
        System.out.println("Driver timeout loaded successfully.");

        // Cleanup
        driver.quit();
        ThreadSafeDriverManager.removeDriver();
    }
}
