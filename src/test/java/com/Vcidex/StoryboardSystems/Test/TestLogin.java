package com.Vcidex.StoryboardSystems.Test;

import com.Vcidex.StoryboardSystems.Common.Authentication.AuthConfig;
import com.Vcidex.StoryboardSystems.Common.Authentication.LoginManager;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;

public class TestLogin {

    public static void main(String[] args) {
        // Setup WebDriver using WebDriverManager to avoid local paths
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();

        try {
            // Perform login tests for different environments
            testLogin(driver, "dev", 0);
            testLogin(driver, "test", 1);
            testLogin(driver, "prod", 0);
        } finally {
            // Close the driver after tests
            driver.quit();
        }
    }

    public static void testLogin(WebDriver driver, String environment, int userIndex) {
        // Fetch app URL and user credentials from AuthConfig
        String appUrl = AuthConfig.getAppUrl(environment);

        // Navigate to the app URL
        driver.get(appUrl);

        // Create an instance of LoginManager to perform login
        LoginManager loginManager = new LoginManager(driver);

        // Perform login using the environment and user index
        loginManager.login(environment, userIndex);

        // Assertion to verify the login was successful
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("dashboard"), "Login failed: URL does not contain 'dashboard'");

        System.out.println("Login action performed for user index " + userIndex + " in the " + environment + " environment.");
    }
}
