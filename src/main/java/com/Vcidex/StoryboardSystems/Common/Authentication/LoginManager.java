package com.Vcidex.StoryboardSystems.Common.Authentication;

import com.Vcidex.StoryboardSystems.Common.Base.BasePage;
import com.Vcidex.StoryboardSystems.Utils.Config.ConfigManager;
import com.Vcidex.StoryboardSystems.Utils.ThreadSafeDriverManager;
import com.Vcidex.StoryboardSystems.Utils.Reporting.ErrorHandler;
import com.Vcidex.StoryboardSystems.Utils.Reporting.ExtentTestManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class LoginManager extends BasePage {
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
        ErrorHandler.executeSafely(driver, () -> {
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

            if (driver.getCurrentUrl().equals("data:,") || driver.getCurrentUrl().isEmpty()) {
                driver.get(appUrl);
                logger.info("🌐 Navigated to app URL: {}", appUrl);
            }

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            wait.until(ExpectedConditions.visibilityOfElementLocated(companyCodeField));

            logger.info("📥 Filling login credentials for userId: {}", userId);
            sendKeys(companyCodeField, companyCode);
            sendKeys(usernameField, username);
            sendKeys(passwordField, password);
            click(loginButton);

            // Wait until dashboard is visible (confirm login success)
            wait.until(ExpectedConditions.presenceOfElementLocated(postLoginLocator));
            wait.until(ExpectedConditions.visibilityOfElementLocated(postLoginLocator));
            logger.info("🌟 Login dashboard element detected");

            ExtentTestManager.getTest().info("✅ Login successful for user: " + username);
            logger.info("✅ Login successful for user: {}", username);
            return null;
        }, "LoginManager");
    }
}