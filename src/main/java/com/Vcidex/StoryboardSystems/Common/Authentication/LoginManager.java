package com.Vcidex.StoryboardSystems.Common.Authentication;

import com.Vcidex.StoryboardSystems.Common.Base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginManager extends BasePage {
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
            // Fetch necessary data from AuthConfig
            String appUrl = AuthConfig.getAppUrl(environment);
            String companyCode = AuthConfig.getUserData(environment, userIndex, "companyCode");
            String username = AuthConfig.getUserData(environment, userIndex, "userName");
            String password = AuthConfig.getUserData(environment, userIndex, "password");

            // Navigate to the application URL
            driver.get(appUrl);

            // Perform login action
            performLogin(companyCode, username, password);

            // Validate login success
            validateLoginSuccess(username, companyCode);
        } catch (Exception e) {
            logger.error("An error occurred during login", e);
            throw new RuntimeException("Login failed", e);
        }
    }

    private void performLogin(String companyCode, String username, String password) {
        // Explicit waits for element visibility
        waitForElementToBeVisible(companyCodeField, companyCode);
        waitForElementToBeVisible(usernameField, username);
        waitForElementToBeVisible(passwordField, password);
        click(loginButton);
    }

    private void validateLoginSuccess(String username, String companyCode) {
        if (isElementPresent(dashboardElement)) {
            logger.info("Login successful for user: " + username + " (Company: " + companyCode + ")");
        } else {
            logger.error("Login failed for user: " + username + " (Company: " + companyCode + ")");
            throw new RuntimeException("Login failed: Landing page validation failed.");
        }
    }

    private boolean isElementPresent(By locator) {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Utility to wait for elements to be visible
    private WebDriverWait waitForElementToBeVisible(By locator, String password) {
        return (WebDriverWait) new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
}