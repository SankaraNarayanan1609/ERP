package com.Vcidex.StoryboardSystems.Common.Authentication;

import com.Vcidex.StoryboardSystems.Common.Base.BasePage;
import com.Vcidex.StoryboardSystems.Utils.Config.ConfigManager;
import com.Vcidex.StoryboardSystems.Utils.ThreadSafeDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginManager extends BasePage {
    private static final Logger logger = LogManager.getLogger(LoginManager.class);

    private final By companyCodeField = By.xpath("//input[@placeholder='Enter CompanyCode']");
    private final By usernameField = By.xpath("//input[@placeholder='Enter UserCode']");
    private final By passwordField = By.xpath("//input[@placeholder='Enter Password']");
    private final By loginButton = By.xpath("//button[@id='kt_sign_in_submit']");
    private final By dashboardElement = By.id("dashboard");

    public LoginManager() { // ✅ No need to pass WebDriver manually
        super(ThreadSafeDriverManager.getDriver());
    }

    public void login(String environment, int userIndex) {
        try {
            logger.info("🔑 Attempting login for user index {} in environment: {}", userIndex, environment);

            String appUrl = ConfigManager.getAppUrl(environment);
            String companyCode = ConfigManager.getUserData(environment, userIndex, "companyCode");
            String username = ConfigManager.getUserData(environment, userIndex, "userName");
            String password = ConfigManager.getUserData(environment, userIndex, "password");

            driver.get(appUrl);
            sendKeys(companyCodeField, companyCode);
            sendKeys(usernameField, username);
            sendKeys(passwordField, password);
            click(loginButton);

            if (!isDashboardVisible()) {
                logger.warn("⚠️ First login attempt failed. Retrying...");
                click(loginButton);
            }

            if (!isDashboardVisible()) {
                logger.error("❌ Login failed: Dashboard not visible!");
                captureScreenshot("Login_Failure_" + System.currentTimeMillis());
                throw new RuntimeException("Login failed! Dashboard not visible.");
            }

            logger.info("✅ Login successful for user: {} (Environment: {})", username, environment);

        } catch (Exception e) {
            logger.error("❌ Login process failed!", e);
            captureScreenshot("Login_Exception_" + System.currentTimeMillis());
            throw new RuntimeException("Login process encountered an exception", e);
        }
    }

    private boolean isDashboardVisible() {
        try {
            return findElement(dashboardElement) != null;
        } catch (Exception e) {
            return false;
        }
    }
}