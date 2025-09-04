/**
 * LoginManager handles UI-based login for ERP systems.
 * It uses WebDriver to:
 * - Load login page
 * - Fill credentials from config
 * - Wait for dashboard redirect
 * - Validate login success
 * - Used by TestBase during setup
 */

package com.Vcidex.StoryboardSystems;

import com.Vcidex.StoryboardSystems.Utils.Config.ConfigManager;
import org.json.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginManager {

    // ─── Required Fields ───────────────────────────────────────────────
    private final WebDriver driver;
    private final String appName;
    private final String companyCode;
    private final String userId;

    // ─── Locators for Login Page ───────────────────────────────────────
    private static final By companyCodeField = By.xpath("//input[@placeholder='Enter CompanyCode']");
    private static final By usernameField    = By.xpath("//input[@placeholder='Enter UserCode']");
    private static final By passwordField    = By.xpath("//input[@placeholder='Enter Password']");
    private static final By loginButton      = By.xpath("//button[@id='kt_sign_in_submit']");

    private static final By postLoginLocator = By.xpath("//app-welcome-page"); // Any dashboard element
    private static final By spinnerLocator   = By.cssSelector(
            ".ngx-spinner-overlay, ngx-spinner, .cdk-overlay-backdrop, .modal-backdrop, .loader"
    );

    /**
     * Constructs LoginManager with all required context.
     *
     * @param driver       WebDriver instance
     * @param appName      Application name (e.g., "StoryboardSystems")
     * @param companyCode  Company short code (e.g., "vcidex")
     * @param userId       Login user code (e.g., "vcx288")
     */
    public LoginManager(WebDriver driver, String appName, String companyCode, String userId) {
        this.driver = driver;
        this.appName = appName;
        this.companyCode = companyCode;
        this.userId = userId;
    }

    /**
     * Executes UI login by reading credentials from config and validating success.
     * - Navigates to login page
     * - Fills credentials from config
     * - Waits for dashboard redirect
     * - Throws exception if login fails
     */
    public void loginViaUi() {
        // Step 1: Resolve from config
        String env     = ConfigManager.getEnv();       // from system/env or config
        JSONObject user = ConfigManager.getUserConfig(env, appName, companyCode, userId);
        String password = user.getString("password");

        String loginUrl = ConfigManager.getLoginUrl();

        // Step 2: Launch login page
        driver.get(loginUrl);

        // Step 3: Fill login form
        waitForElement(companyCodeField).sendKeys(companyCode);
        waitForElement(usernameField).sendKeys(userId);
        waitForElement(passwordField).sendKeys(password);

        // Step 4: Wait for spinner before login
        waitForSpinnerToDisappear();

        // Step 5: Submit login
        driver.findElement(loginButton).click();

        // Step 6: Confirm login success
        try {
            new WebDriverWait(driver, Duration.ofSeconds(30))
                    .until(ExpectedConditions.or(
                            ExpectedConditions.urlContains("/dashboard"),
                            ExpectedConditions.urlContains("/WelcomePage"),
                            ExpectedConditions.urlContains("/hrm/HrmTrnMemberdhashboard"),
                            ExpectedConditions.presenceOfElementLocated(postLoginLocator)
                    ));
            System.out.println("✅ UI login successful → redirected to dashboard");
        } catch (TimeoutException e) {
            System.out.println("[DEBUG] Login failed. URL: " + driver.getCurrentUrl());
            throw new RuntimeException("❌ Login failed or timed out.");
        }
    }

    /**
     * Waits until a specific element is present in DOM.
     */
    private WebElement waitForElement(By locator) {
        return new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /**
     * Waits for any loading spinners or overlays to disappear.
     */
    private void waitForSpinnerToDisappear() {
        new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.invisibilityOfElementLocated(spinnerLocator));
    }
}