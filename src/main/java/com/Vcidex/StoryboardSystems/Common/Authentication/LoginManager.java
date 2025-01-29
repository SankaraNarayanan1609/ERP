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

            driver.get(appUrl);
            performLogin(companyCode, username, password);
            validateLoginSuccess(username, companyCode);

        } catch (Exception e) {
            logger.error("Login failed", e);
            throw new AuthenticationException("Login failed for user " + userIndex, e);
        }
    }

    private void performLogin(String companyCode, String username, String password) {
        sendKeys(companyCodeField, companyCode);
        sendKeys(usernameField, username);
        sendKeys(passwordField, password);
        click(loginButton);
    }

    private void validateLoginSuccess(String username, String companyCode) {
        if (!isElementVisible(dashboardElement)) {
            throw new AuthenticationException("Login failed for " + username, new Throwable("Dashboard not found"));
        }
        logger.info("Login successful for user: {} (Company: {})", username, companyCode);
    }

    public static class AuthenticationException extends RuntimeException {
        public AuthenticationException(String message) {
            super(message);
        }

        public AuthenticationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
