/**
 * LoginManager handles login functionality for both:
 *  - UI login: used by end-to-end Selenium tests like DirectPOTest
 *  - API login: used by backend validations or session preparation
 *
 * This class is directly called in `DirectPOTest` during `@BeforeMethod`
 * to perform a UI login and extract token from localStorage.
 *
 * It supports:
 * - Waiting for login fields to be interactable
 * - Submitting login
 * - Detecting successful redirection
 * - Returning session token if needed for backend or RestAssured usage
 */

package com.Vcidex.StoryboardSystems;

// ─── External Libraries ─────────────────────────────────────────────
import com.Vcidex.StoryboardSystems.Utils.Config.ConfigManager;
import com.aventstack.extentreports.ExtentTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class LoginManager {

    // ─── Dependencies ───────────────────────────────────────────────
    private final WebDriver driver;      // The Selenium driver controlling the browser
    private final ExtentTest node;       // For logging login steps into ExtentReport

    // ─── Locators for Login Page Elements ───────────────────────────
    private static final By companyCodeField = By.xpath("//input[@placeholder='Enter CompanyCode']");
    private static final By usernameField    = By.xpath("//input[@placeholder='Enter UserCode']");
    private static final By passwordField    = By.xpath("//input[@placeholder='Enter Password']");
    private static final By loginButton      = By.xpath("//button[@id='kt_sign_in_submit']");

    private static final By postLoginLocator = By.xpath("//app-welcome-page"); // Dashboard success indicator
    private static final By spinnerLocator   = By.cssSelector(
            ".ngx-spinner-overlay, ngx-spinner, .cdk-overlay-backdrop, .modal-backdrop, .loader"
    );

    /**
     * Constructor takes driver and test logging node.
     * @param driver Selenium driver instance
     * @param node   ExtentTest node (can be null if reporting not required)
     */
    public LoginManager(WebDriver driver, ExtentTest node) {
        this.driver = driver;
        this.node   = node;
    }

    /**
     * Performs login using UI:
     * - Fills company code, username, password
     * - Clicks submit
     * - Waits for dashboard redirect or welcome page
     *
     * @param appName     Name of app as per config.json (e.g., "StoryboardSystems")
     * @param companyCode Company short code (e.g., "vcidex")
     * @param userId      Username to login (e.g., "vcx288")
     */
    public void loginViaUi(String appName, String companyCode, String userId) {
        // Step 1: Get current environment (e.g., "test", "stage") and fetch credentials from config
        String env = System.getProperty("env", "test");
        JSONObject user = ConfigManager.getUserConfig(env, appName, companyCode, userId);
        String password = user.getString("password");

        // Step 2: Launch login page
        driver.get("https://erplite.storyboarderp.com/v4/#/auth/login");

        // Step 3: Fill in login form
        waitForElement(companyCodeField).sendKeys(companyCode);
        waitForElement(usernameField).sendKeys(userId);
        waitForElement(passwordField).sendKeys(password);

        // Step 4: Wait for any loading spinner to disappear before clicking login
        waitForSpinnerToDisappear();

        // Step 5: Click on login
        driver.findElement(loginButton).click();

        // Step 6: Confirm login success by waiting for dashboard or welcome screen
        try {
            new WebDriverWait(driver, Duration.ofSeconds(30))
                    .until(ExpectedConditions.or(
                            ExpectedConditions.urlContains("/dashboard"),
                            ExpectedConditions.urlContains("/WelcomePage"),
                            ExpectedConditions.presenceOfElementLocated(postLoginLocator)
                    ));
            System.out.println("✅ UI login successful → redirected to dashboard");
        } catch (TimeoutException e) {
            System.out.println("[DEBUG] Login failed or did not redirect. Current URL: " + driver.getCurrentUrl());
            throw new RuntimeException("Login failed or timed out; not redirected.");
        }
    }

    /**
     * Utility to wait until a specific element is present in DOM.
     * @param locator The By locator for the input field
     * @return WebElement after it becomes visible
     */
    private WebElement waitForElement(By locator) {
        return new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /**
     * Waits until loading spinner (overlay) disappears from screen.
     * This ensures all Angular async operations are completed.
     */
    private void waitForSpinnerToDisappear() {
        new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.invisibilityOfElementLocated(spinnerLocator));
    }
}