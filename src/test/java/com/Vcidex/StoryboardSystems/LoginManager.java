package com.Vcidex.StoryboardSystems;

import com.Vcidex.StoryboardSystems.Utils.Config.ConfigManager;
import com.Vcidex.StoryboardSystems.Utils.Logger.PerformanceLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.TestContextLogger;
import com.aventstack.extentreports.ExtentTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;

import static io.restassured.RestAssured.given;

public class LoginManager {
    private final WebDriver driver;
    private final ExtentTest node;

    // UI locators
    private final By companyCodeField = By.xpath("//input[@placeholder='Enter CompanyCode']");
    private final By usernameField    = By.xpath("//input[@placeholder='Enter UserCode']");
    private final By passwordField    = By.xpath("//input[@placeholder='Enter Password']");
    private final By loginButton      = By.xpath("//button[@id='kt_sign_in_submit']");
    private final By postLoginLocator = By.xpath("//app-welcome-page");
    private final By spinnerLocator   = By.cssSelector("ngx-spinner");

    public LoginManager(WebDriver driver, ExtentTest node) {
        this.driver = driver;
        this.node   = node;
    }

    public void login(String appName, String companyCode, String userId) {
        TestContextLogger.logTestStart("Login", driver);
        PerformanceLogger.start("LoginManager.login");
        try {
            String env     = System.getProperty("env", "test");
            JSONObject app = ConfigManager.getAppConfig(env, appName);
            JSONObject user = ConfigManager.getUserConfig(env, appName, companyCode, userId);

            String appUrl   = app.getString("appUrl");
            String password = user.getString("password");

            if (driver.getCurrentUrl().startsWith("data:,")) {
                driver.get(appUrl);
            }
            int timeout = Integer.parseInt(ConfigManager.getProperty("timeout", "30"));
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));

            wait.until(ExpectedConditions.visibilityOfElementLocated(companyCodeField));
            node.info("📥 Logging in as " + userId + "@" + companyCode);

// Fill and blur all fields to trigger Angular validation
            driver.findElement(companyCodeField).clear();
            driver.findElement(companyCodeField).sendKeys(companyCode);
            driver.findElement(companyCodeField).sendKeys(Keys.TAB);

            driver.findElement(usernameField).clear();
            driver.findElement(usernameField).sendKeys(userId);
            driver.findElement(usernameField).sendKeys(Keys.TAB);

            driver.findElement(passwordField).clear();
            driver.findElement(passwordField).sendKeys(password);
            driver.findElement(passwordField).sendKeys(Keys.TAB);

            try { Thread.sleep(200); } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted during login", e);
            }

            WebElement btn = driver.findElement(loginButton);
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block: 'center'});", btn
            );
            try {
                btn.click();
            } catch (ElementClickInterceptedException e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            }

// Debug info
            System.out.println("Login attempted. Current URL: " + driver.getCurrentUrl());
            System.out.println("Page title: " + driver.getTitle());

            try {
                File src = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
                java.nio.file.Files.copy(src.toPath(), java.nio.file.Paths.get("login_post_click_debug.png"),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                System.out.println("Failed to save debug screenshot: " + e.getMessage());
            }

// Wait for spinner/overlay to go (if present)
            try {
                wait.until(ExpectedConditions.invisibilityOfElementLocated(spinnerLocator));
            } catch (TimeoutException ignored) {}

            node.pass("✅ UI login successful for " + userId);

            JSONObject auth     = ConfigManager.getAuthConfig(env);
            String     authUrl   = auth.getString("authUrl");
            String     tokenPath = auth.getString("tokenPath");
            JSONObject body      = new JSONObject()
                    .put("user_code",     userId)
                    .put("company_code",  companyCode)
                    .put("user_password", password);

            Response resp = given()
                    .baseUri(authUrl)
                    .contentType(ContentType.JSON)
                    .body(body.toString())
                    .when()
                    .post(tokenPath);

            if (resp.statusCode() != 200) {
                node.fail("❌ API-login failed: HTTP " + resp.statusCode() + " → " + resp.asString());
                throw new RuntimeException("Could not fetch API token");
            }

            String bearer = resp.jsonPath().getString("token");
            if (bearer != null && bearer.toLowerCase().startsWith("bearer ")) {
                bearer = bearer.substring(7);
            }
            node.pass("🔑 Obtained API token via UserLogin");

            TestBase.initDataFactory(bearer);

        } finally {
            PerformanceLogger.end("LoginManager.login");
            TestContextLogger.logTestEnd("Login");
        }
    }
}