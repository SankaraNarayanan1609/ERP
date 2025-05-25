package com.Vcidex.StoryboardSystems;

import com.Vcidex.StoryboardSystems.TestBase;
import com.Vcidex.StoryboardSystems.Utils.Config.ConfigManager;
import com.Vcidex.StoryboardSystems.Utils.Logger.PerformanceLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.TestContextLogger;
import com.aventstack.extentreports.ExtentTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static io.restassured.RestAssured.given;

public class LoginManager {
    private final WebDriver  driver;
    private final ExtentTest node;

    // UI locators
    private final By companyCodeField = By.xpath("//input[@placeholder='Enter CompanyCode']");
    private final By usernameField    = By.xpath("//input[@placeholder='Enter UserCode']");
    private final By passwordField    = By.xpath("//input[@placeholder='Enter Password']");
    private final By loginButton      = By.xpath("//button[@id='kt_sign_in_submit']");
    private final By postLoginLocator = By.xpath("//b[normalize-space()='Member Dashboard']");

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

            // ── A) UI login ───────────────────────────────────────────────
            if (driver.getCurrentUrl().startsWith("data:,")) {
                driver.get(appUrl);
            }
            int timeout = Integer.parseInt(ConfigManager.getProperty("timeout", "30"));
            new WebDriverWait(driver, Duration.ofSeconds(timeout))
                    .until(ExpectedConditions.visibilityOfElementLocated(companyCodeField));
            node.info("📥 Logging in as " + userId + "@" + companyCode);

            driver.findElement(companyCodeField).sendKeys(companyCode);
            driver.findElement(usernameField).sendKeys(userId);
            driver.findElement(passwordField).sendKeys(password);
            driver.findElement(loginButton).click();
            new WebDriverWait(driver, Duration.ofSeconds(timeout))
                    .until(ExpectedConditions.visibilityOfElementLocated(postLoginLocator));
            node.pass("✅ UI login successful for " + userId);

            // ── B) Fetch API token ───────────────────────────────────────
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
                node.fail("❌ API-login failed: HTTP "
                        + resp.statusCode() + " → " + resp.asString());
                throw new RuntimeException("Could not fetch API token");
            }

            String bearer = resp.jsonPath().getString("token");
            if (bearer.toLowerCase().startsWith("bearer ")) {
                bearer = bearer.substring(7);
            }
            node.pass("🔑 Obtained API token via UserLogin");

            // ── C) Wire token into TestBase ──────────────────────────────
            TestBase.initDataFactory(bearer);

        } finally {
            PerformanceLogger.end("LoginManager.login");
            TestContextLogger.logTestEnd("Login");
        }
    }
}