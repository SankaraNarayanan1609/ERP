package com.Vcidex.StoryboardSystems.Common.Authentication;

import com.Vcidex.StoryboardSystems.Common.Base.BasePage;
import com.Vcidex.StoryboardSystems.Utils.Config.ConfigManager;
import com.Vcidex.StoryboardSystems.Utils.Reporting.ErrorHandler;
import com.Vcidex.StoryboardSystems.Utils.Reporting.ExtentTestManager;
import com.Vcidex.StoryboardSystems.Utils.ThreadSafeDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class LoginManager extends BasePage {
    public LoginManager(WebDriver driver) {
        super(driver);
    }
    private static final Logger logger = LogManager.getLogger(LoginManager.class);
    private final By companyCodeField = By.xpath("//input[@placeholder='Enter CompanyCode']");
    private final By usernameField = By.xpath("//input[@placeholder='Enter UserCode']");
    private final By passwordField = By.xpath("//input[@placeholder='Enter Password']");
    private final By loginButton = By.xpath("//button[@id='kt_sign_in_submit']");
    private final By postLoginLocator = By.xpath("//b[normalize-space()='Member Dashboard']");

    public LoginManager() {
        super(ThreadSafeDriverManager.getDriver());
    }

    public void login(String userId) {
        String env = System.getProperty("env", "test");
        String appUrl = ConfigManager.getConfig(env, "appUrl");

        List<Map<String, String>> users = ConfigManager.getConfigList(env, "users");
        if (users.isEmpty()) {
            throw new RuntimeException("❌ No users found in config.json for environment: " + env);
        }

        Map<String, String> user = users.stream()
                .filter(u -> u.get("userId").equals(userId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("❌ User with ID " + userId + " not found in config"));

        String companyCode = user.get("companyCode");
        String username = user.get("userName");
        String password = user.get("password");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        // Navigate to app URL if current URL is blank
        ErrorHandler.executeSafely(driver, () -> {
            String currentUrl = driver.getCurrentUrl();
            if (currentUrl.equals("data:,") || currentUrl.isEmpty()) {
                driver.get(appUrl);
                ExtentTestManager.getTest().info("🌐 Navigated to app URL: " + appUrl);
                logger.info("🌐 Navigated to app URL: {}", appUrl);
            }
            return null;
        }, "Navigate to App URL");

        // Wait for login fields
        ErrorHandler.executeSafely(driver, () -> {
            wait.until(ExpectedConditions.visibilityOfElementLocated(companyCodeField));
            return null;
        }, "Wait for Company Code Field");

        logger.info("📥 Filling login credentials for userId: {}", userId);
        ExtentTestManager.getTest().info("📥 Filling login credentials for userId: " + userId);

        // Fill credentials
        ErrorHandler.executeSafely(driver, () -> {
            sendKeys(companyCodeField, companyCode);
            return null;
        }, "Enter Company Code");

        ErrorHandler.executeSafely(driver, () -> {
            sendKeys(usernameField, username);
            return null;
        }, "Enter Username");

        ErrorHandler.executeSafely(driver, () -> {
            sendKeys(passwordField, password);
            return null;
        }, "Enter Password");

        // Click login
        ErrorHandler.executeSafely(driver, () -> {
            click(loginButton);
            return null;
        }, "Click Login Button");

        // Wait for dashboard
        ErrorHandler.executeSafely(driver, () -> {
            wait.until(ExpectedConditions.presenceOfElementLocated(postLoginLocator));
            wait.until(ExpectedConditions.visibilityOfElementLocated(postLoginLocator));
            return null;
        }, "Wait for Dashboard after Login");

        ExtentTestManager.getTest().info("✅ Login successful for user: " + username);
        logger.info("✅ Login successful for user: {}", username);
    }
}