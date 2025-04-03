package com.Vcidex.StoryboardSystems.Common.Authentication;

import com.Vcidex.StoryboardSystems.Common.Base.BasePage;
import com.Vcidex.StoryboardSystems.Utils.Config.ConfigManager;
import com.Vcidex.StoryboardSystems.Utils.ThreadSafeDriverManager;
import com.Vcidex.StoryboardSystems.Utils.Reporting.ErrorHandler;
import com.Vcidex.StoryboardSystems.Utils.Reporting.ExtentTestManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class LoginManager extends BasePage {
    private static final Logger logger = LogManager.getLogger(LoginManager.class);

    private final By companyCodeField = By.xpath("//input[@placeholder='Enter CompanyCode']");
    private final By usernameField = By.xpath("//input[@placeholder='Enter UserCode']");
    private final By passwordField = By.xpath("//input[@placeholder='Enter Password']");
    private final By loginButton = By.xpath("//button[@id='kt_sign_in_submit']");

    public LoginManager() {
        super(ThreadSafeDriverManager.getDriver());
    }

    public void login(String userId) {
        ErrorHandler.executeSafely(driver, () -> {
            // Fetch environment dynamically (default to 'test' if not set)
            String env = System.getProperty("env", "test");
            String appUrl = ConfigManager.getConfig(env, "appUrl");

            // Load user data from JSON
            List<Map<String, String>> users = ConfigManager.getConfigList(env, "users");
            if (users.isEmpty()) {
                throw new RuntimeException("❌ No users found in config.json for environment: " + env);
            }

            // Find user by userId
            Map<String, String> user = users.stream()
                    .filter(u -> u.get("userId").equals(userId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("❌ User with ID " + userId + " not found in config"));

            String companyCode = user.get("companyCode");
            String username = user.get("userName");
            String password = user.get("password");

            if (driver.getCurrentUrl().equals("data:,") || driver.getCurrentUrl().isEmpty()) {
                driver.get(appUrl);
                logger.info("🌐 Navigated to URL: {}", appUrl);
            }

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            wait.until(ExpectedConditions.visibilityOfElementLocated(companyCodeField));

            sendKeys(companyCodeField, companyCode);
            sendKeys(usernameField, username);
            sendKeys(passwordField, password);
            click(loginButton);

            logger.info("✅ Login successful for user: {}", username);
            ExtentTestManager.getTest().info("Login successful for user: " + username);
        }, "LoginManager", false, "");
    }
}
