package com.Vcidex.StoryboardSystems;

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
    private final WebDriver driver;
    private final ExtentTest node;

    private static final By companyCodeField = By.xpath("//input[@placeholder='Enter CompanyCode']");
    private static final By usernameField    = By.xpath("//input[@placeholder='Enter UserCode']");
    private static final By passwordField    = By.xpath("//input[@placeholder='Enter Password']");
    private static final By loginButton      = By.xpath("//button[@id='kt_sign_in_submit']");
    private static final By postLoginLocator = By.xpath("//app-welcome-page");
    private static final By spinnerLocator   = By.cssSelector(".ngx-spinner-overlay, ngx-spinner, .cdk-overlay-backdrop, .modal-backdrop, .loader");

    public LoginManager(WebDriver driver, ExtentTest node) {
        this.driver = driver;
        this.node   = node;
    }

    public void loginViaUi(String appName, String companyCode, String userId) {
        String env = System.getProperty("env", "test");
        JSONObject user = ConfigManager.getUserConfig(env, appName, companyCode, userId);
        String password = user.getString("password");

        driver.get("https://erplite.storyboarderp.com/v4/#/auth/login");

        waitForElement(companyCodeField).sendKeys(companyCode);
        waitForElement(usernameField).sendKeys(userId);
        waitForElement(passwordField).sendKeys(password);

        waitForSpinnerToDisappear();
        driver.findElement(loginButton).click();

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

    private WebElement waitForElement(By locator) {
        return new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    private void waitForSpinnerToDisappear() {
        new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.invisibilityOfElementLocated(spinnerLocator));
    }

    public Map<String, String> loginViaApiAndGetSession(String appName, String companyCode, String userId) {
        String env = System.getProperty("env", "test");
        JSONObject user = ConfigManager.getUserConfig(env, appName, companyCode, userId);
        JSONObject app  = ConfigManager.getAppConfig(env, appName);
        String password = user.getString("password");

        JSONObject auth = ConfigManager.getAuthConfig(env);
        String authUrl   = auth.getString("authUrl");
        String tokenPath = auth.getString("tokenPath");

        JSONObject body = new JSONObject()
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
            throw new RuntimeException("Could not fetch API token: " + resp.asString());
        }

        String token = resp.jsonPath().getString("token");
        if (token != null && token.toLowerCase().startsWith("bearer ")) {
            token = token.substring(7);
        }

        Map<String, String> sessionData = new HashMap<>();
        sessionData.put("c_code", companyCode);
        sessionData.put("company_name", app.optString("companyName", ""));
        sessionData.put("employee_emailid", user.optString("email", ""));
        sessionData.put("employee_mobileno", user.optString("mobile", ""));
        sessionData.put("token", "Bearer " + token);
        sessionData.put("user_gid", user.optString("gid", userId));

        return sessionData;
    }
}