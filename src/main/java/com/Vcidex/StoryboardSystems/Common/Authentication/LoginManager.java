package com.Vcidex.StoryboardSystems.Common.Authentication;

import com.Vcidex.StoryboardSystems.Common.Base.BasePage;
import com.Vcidex.StoryboardSystems.Utils.Config.ConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.Collections;

public class LoginManager extends BasePage {
    private static final Logger logger = LogManager.getLogger(LoginManager.class);

    private final By companyCodeField = By.xpath("//label[text()='Company Code']/following-sibling::input");
    private final By usernameField = By.xpath("//label[text()='Username']/following-sibling::input");
    private final By passwordField = By.xpath("//label[text()='Password']/following-sibling::input");
    private final By loginButton = By.id("loginBtn");
    private final By dashboardElement = By.id("dashboard");

    public LoginManager(WebDriver driver) {
        super(driver, Collections.emptyMap());    }

    public void login(String environment, int userIndex) {
        try {
            logger.info("🔑 Attempting login for user index {} in environment: {}", userIndex, environment);

            // 🔄 Fetch credentials dynamically
            String appUrl = ConfigManager.getAppUrl(environment);
            String companyCode = ConfigManager.getUserData(environment, userIndex, "companyCode");
            String username = ConfigManager.getUserData(environment, userIndex, "userName");
            String password = ConfigManager.getUserData(environment, userIndex, "password");

            driver.get(appUrl);
            enterText(companyCodeField, companyCode);
            enterText(usernameField, username);
            enterText(passwordField, password);
            click(loginButton);

            // ✅ Retry login if the dashboard is not visible
            if (!isDashboardVisible()) {
                logger.warn("⚠️ First login attempt failed. Retrying...");
                click(loginButton);
            }

            // ✅ Final check for successful login
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

    /**
     * ✅ Checks if the dashboard is visible (Replaces `isElementPresent()`).
     *
     * @return True if dashboard is visible, otherwise false.
     */
    private boolean isDashboardVisible() {
        try {
            return findElement(dashboardElement) != null;
        } catch (Exception e) {
            return false;
        }
    }
}
