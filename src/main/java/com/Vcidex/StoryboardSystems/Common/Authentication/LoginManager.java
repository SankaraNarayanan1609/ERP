package com.Vcidex.StoryboardSystems.Common.Authentication;

import com.Vcidex.StoryboardSystems.Common.Base.BasePage;
import com.Vcidex.StoryboardSystems.Utils.Config.ConfigManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginManager extends BasePage {
    private static final Logger logger = LoggerFactory.getLogger(LoginManager.class);

    private final By companyCodeField = By.xpath("//label[text()='Company Code']/following-sibling::input");
    private final By usernameField = By.xpath("//label[text()='Username']/following-sibling::input");
    private final By passwordField = By.xpath("//label[text()='Password']/following-sibling::input");
    private final By loginButton = By.id("loginBtn");
    private final By dashboardElement = By.id("dashboard");

    public LoginManager(WebDriver driver) {
        super(driver);
    }

    public void login(String environment, int userIndex) {
        try {
            // 🔄 Fetching credentials dynamically from ConfigManager
            String appUrl = ConfigManager.getAppUrl(environment);
            String companyCode = ConfigManager.getUserData(environment, userIndex, "companyCode");
            String username = ConfigManager.getUserData(environment, userIndex, "userName");
            String password = ConfigManager.getUserData(environment, userIndex, "password");

            logger.info("Attempting login with user: {} for environment: {}", username, environment);

            driver.get(appUrl);
            performLogin(companyCode, username, password);
            validateLoginSuccess(username, companyCode);

        } catch (Exception e) {
            logger.error("❌ Login failed for user {} in environment {}!", userIndex, environment, e);
            captureScreenshot("Login_Failure_" + System.currentTimeMillis());
            throw new RuntimeException("Login failed for user " + userIndex, e);
        }
    }

    private void performLogin(String companyCode, String username, String password) {
        try {
            sendKeys(companyCodeField, companyCode);
            sendKeys(usernameField, username);
            sendKeys(passwordField, password);
            click(loginButton);
        } catch (Exception e) {
            logger.error("❌ Login fields not found or not interactable!", e);
            captureScreenshot("Login_Field_Error_" + System.currentTimeMillis());
            throw new RuntimeException("Login field interaction failed", e);
        }
    }

    private void validateLoginSuccess(String username, String companyCode) {
        if (!isElementPresent(dashboardElement)) { // ✅ Fix applied
            logger.error("❌ Login failed: Dashboard not found for user {}", username);
            captureScreenshot("Login_Failure_" + System.currentTimeMillis()); // ✅ Fix applied
            throw new RuntimeException("Login failed for " + username + ". Dashboard not visible.");
        }
        logger.info("✅ Login successful for user: {} (Company: {})", username, companyCode);
    }
}
