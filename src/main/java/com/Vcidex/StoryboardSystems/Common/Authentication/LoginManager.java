package com.Vcidex.StoryboardSystems.Common.Authentication;

import com.Vcidex.StoryboardSystems.Common.Base.BasePage;
import com.Vcidex.StoryboardSystems.Utils.Config.ConfigManager;
import com.Vcidex.StoryboardSystems.Utils.Logger.ErrorLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.ExtentTestManager;
import com.Vcidex.StoryboardSystems.Utils.Logger.PerformanceLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.TestContextLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.UIActionLogger;
import com.Vcidex.StoryboardSystems.Utils.ThreadSafeDriverManager;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * Handles login across multiple applications, companies, and users.
 */
public class LoginManager extends BasePage {
    private static final Logger logger = LoggerFactory.getLogger(LoginManager.class);

    private final By companyCodeField = By.xpath("//input[@placeholder='Enter CompanyCode']");
    private final By usernameField    = By.xpath("//input[@placeholder='Enter UserCode']");
    private final By passwordField    = By.xpath("//input[@placeholder='Enter Password']");
    private final By loginButton      = By.xpath("//button[@id='kt_sign_in_submit']");
    private final By postLoginLocator = By.xpath("//b[normalize-space()='Member Dashboard']");

    public LoginManager(WebDriver driver) {
        super(driver);
    }

    public LoginManager() {
        this(ThreadSafeDriverManager.getDriver());
    }

    /**
     * Logs in using the specified app, company, and user.
     * @param appName     the application key from config.json
     * @param companyCode the companyCode (database) key
     * @param userId      the userId under that company
     */
    public void login(String appName, String companyCode, String userId) {
        // Mark test context and start performance
        TestContextLogger.logTestStart("Login", driver);
        PerformanceLogger.start("LoginManager.login");

        try {
            String env = System.getProperty("env", "test");

            // 1) Load app configuration
            JSONObject app = ConfigManager.getAppConfig(env, appName);
            String appUrl = app.getString("appUrl");

            // 2) Load user credentials
            JSONObject user = ConfigManager.getUserConfig(env, appName, companyCode, userId);
            String username = user.getString("userId");
            String password = user.getString("password");

            // 3) Navigate to application URL if needed
            String current = driver.getCurrentUrl();
            if (current == null || current.isEmpty() || "data:,".equals(current)) {
                driver.get(appUrl);
                ExtentTestManager.getTest().info("🌐 Navigated to " + appUrl);
                logger.info("Navigated to {}", appUrl);
            }

            // 4) Wait for login form visibility
            int timeout = Integer.parseInt(ConfigManager.getProperty("timeout", "30"));
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
            wait.until(ExpectedConditions.visibilityOfElementLocated(companyCodeField));

            // 5) Fill credentials and submit
            ExtentTestManager.getTest().info("📥 Logging in as " + username + "@" + companyCode);
            logger.info("Logging in user={} company={}", username, companyCode);

            UIActionLogger.type(driver, companyCodeField, companyCode, "Company Code");
            UIActionLogger.type(driver, usernameField,    username,      "Username");
            UIActionLogger.type(driver, passwordField,    password,      "Password");
            UIActionLogger.click(driver, loginButton,     "Login Button");

            // 6) Wait for post-login dashboard
            wait.until(ExpectedConditions.visibilityOfElementLocated(postLoginLocator));

            ExtentTestManager.getTest().pass("✅ Login successful for " + username);
            logger.info("Login successful for {}", username);
        } catch (Exception e) {
            ErrorLogger.logException(e, "LoginManager.login", driver);
            throw e;
        } finally {
            PerformanceLogger.end("LoginManager.login");
            TestContextLogger.logTestEnd("Login");
        }
    }
}