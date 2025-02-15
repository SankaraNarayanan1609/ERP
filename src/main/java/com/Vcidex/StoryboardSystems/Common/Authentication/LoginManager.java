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

        while (attempt < maxRetries) {
            try {
                logger.info("🔑 [LoginManager] Attempting login...");

                // Enter Company Code
                By companyCodeField = By.xpath("//input[@placeholder='Enter CompanyCode']");
                sendKeys(companyCodeField, companyCode);

                // Enter User Code
                By userCodeField = By.xpath("//input[@placeholder='Enter UserCode']");
                sendKeys(userCodeField, userCode);

                // Enter Password
                By passwordField = By.xpath("//input[@placeholder='Enter Password']");
                sendKeys(passwordField, password);

                // Click Login Button
                By loginButton = By.xpath("//button[@id='kt_sign_in_submit']");
                click(loginButton);

                logger.info("✅ Login attempt successful (dashboard check skipped).");
                return; // Exit the loop after successful click

            } catch (Exception e) {
                attempt++;
                logger.warn("⚠️ Login attempt {} failed: {}", attempt, e.getMessage());

                if (attempt >= maxRetries) {
                    throw new RuntimeException("❌ Failed to log in after multiple attempts.", e);
                }

                // Optional: Sleep between retries
                sleep(3000);
            }
        }
    }
}