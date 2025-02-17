package com.Vcidex.StoryboardSystems.Common.Authentication;

import com.Vcidex.StoryboardSystems.Common.Base.BasePage;
import com.Vcidex.StoryboardSystems.Utils.Config.ConfigManager;
import com.Vcidex.StoryboardSystems.Utils.ThreadSafeDriverManager;
import com.Vcidex.StoryboardSystems.Utils.Reporting.ScreenshotRpt;
import com.Vcidex.StoryboardSystems.Utils.Reporting.ExtentTestManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;

import static java.lang.Thread.sleep;

public class LoginManager extends BasePage {
    private static final Logger logger = LogManager.getLogger(LoginManager.class);

    private final By companyCodeField = By.xpath("//input[@placeholder='Enter CompanyCode']");
    private final By usernameField = By.xpath("//input[@placeholder='Enter UserCode']");
    private final By passwordField = By.xpath("//input[@placeholder='Enter Password']");
    private final By loginButton = By.xpath("//button[@id='kt_sign_in_submit']");

    public LoginManager() {
        super(ThreadSafeDriverManager.getDriver());
    }

    public void login(String companyCode, String userCode, String password) throws InterruptedException {
        int maxRetries = 3;
        int attempt = 0;

        // ✅ Load URL from configuration
        String appUrl = ConfigManager.getConfig("test", "appUrl"); // Adjust based on env

        // ✅ Navigate to the login page if not already done
        if (driver.getCurrentUrl().equals("data:,") || driver.getCurrentUrl().isEmpty()) {
            driver.get(appUrl);
            logger.info("🌐 Navigated to URL: {}", appUrl);
        }

        while (attempt < maxRetries) {
            try {
                logger.info("🔑 [LoginManager] Attempting login...");

                // ✅ Wait for Company Code field
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
                wait.until(ExpectedConditions.visibilityOfElementLocated(companyCodeField));

                // ✅ Perform login steps
                sendKeys(companyCodeField, companyCode);
                sendKeys(usernameField, userCode);
                sendKeys(passwordField, password);
                click(loginButton);

                logger.info("✅ Login successful.");
                return;

            } catch (Exception e) {
                attempt++;
                logger.warn("⚠️ Login attempt {} failed: {}", attempt, e.getMessage());
                if (attempt >= maxRetries) {
                    throw new RuntimeException("❌ Failed to log in after multiple attempts.", e);
                }
                Thread.sleep(3000);
            }
        }
    }
}