// File: LoginManager.java
package com.Vcidex.StoryboardSystems.Common.Authentication;

import com.Vcidex.StoryboardSystems.Common.Base.BasePage;
import com.Vcidex.StoryboardSystems.Utils.Config.ConfigManager;
import com.Vcidex.StoryboardSystems.Utils.Logger.TestContextLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.PerformanceLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.ExtentTestManager;
import com.aventstack.extentreports.ExtentTest;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginManager extends BasePage {
    private final ExtentTest node;

    private final By companyCodeField = By.xpath("//input[@placeholder='Enter CompanyCode']");
    private final By usernameField    = By.xpath("//input[@placeholder='Enter UserCode']");
    private final By passwordField    = By.xpath("//input[@placeholder='Enter Password']");
    private final By loginButton      = By.xpath("//button[@id='kt_sign_in_submit']");
    private final By postLoginLocator = By.xpath("//b[normalize-space()='Member Dashboard']");

    /**
     * Use when *you* create the login node in your test:
     *     ExtentTest loginNode = ExtentTestManager.createNode("🔑 Login");
     *     new LoginManager(driver, loginNode).login(...);
     */
    public LoginManager(WebDriver driver, ExtentTest node) {
        super(driver, node);
        this.node = node;
    }

    /** Convenience: logs into the *root* node if you don’t pass one. */
    public LoginManager(WebDriver driver) {
        this(driver, ExtentTestManager.getTest());
    }

    public void login(String appName, String companyCode, String userId) {
        TestContextLogger.logTestStart("Login", driver);
        PerformanceLogger.start("LoginManager.login");
        try {
            String env    = System.getProperty("env", "test");
            JSONObject app  = ConfigManager.getAppConfig(env, appName);
            JSONObject user = ConfigManager.getUserConfig(env, appName, companyCode, userId);

            String appUrl   = app.getString("appUrl");
            String username = user.getString("userId");
            String password = user.getString("password");

            // 1) Navigate if we're on a blank page
            if (driver.getCurrentUrl().contains("data:,")) {
                performAction(() -> driver.get(appUrl),
                        "🌐 Navigate to " + appUrl);
            }

            // 2) Wait for form
            int timeout = Integer.parseInt(ConfigManager.getProperty("timeout", "30"));
            new WebDriverWait(driver, Duration.ofSeconds(timeout))
                    .until(ExpectedConditions.visibilityOfElementLocated(companyCodeField));

            node.info("📥 Logging in as " + username + "@" + companyCode);

            // 3) Enter creds + click
            type(companyCodeField, companyCode, "Company Code");
            type(usernameField,    username,      "Username");
            type(passwordField,    password,      "Password");

            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.elementToBeClickable(loginButton));
            click(loginButton, "Login Button");

            // 4) Wait for dashboard
            new WebDriverWait(driver, Duration.ofSeconds(timeout))
                    .until(ExpectedConditions.visibilityOfElementLocated(postLoginLocator));

            node.pass("✅ Login successful for " + username);
        } finally {
            PerformanceLogger.end("LoginManager.login");
            TestContextLogger.logTestEnd("Login");
        }
    }
}