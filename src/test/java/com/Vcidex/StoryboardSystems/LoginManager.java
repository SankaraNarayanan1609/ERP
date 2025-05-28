package com.Vcidex.StoryboardSystems;

import com.Vcidex.StoryboardSystems.Utils.Config.ConfigManager;

import com.aventstack.extentreports.ExtentTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class LoginManager {
    private final WebDriver driver;
    private final ExtentTest node;

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

    public Map<String, String> loginViaApiAndGetSession(String appName, String companyCode, String userId) {
        String env     = System.getProperty("env", "test");
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

        // Build sessionData using values from config and/or API response
        Map<String, String> sessionData = new HashMap<>();
        sessionData.put("c_code", companyCode); // Usually fixed per test run, but should still be from config
        sessionData.put("company_name", app.optString("companyName", "")); // from config
        sessionData.put("employee_emailid", user.optString("email", "")); // from config/user block
        sessionData.put("employee_mobileno", user.optString("mobile", "")); // from config/user block
        sessionData.put("token", "Bearer " + token); // from API
        sessionData.put("user_gid", user.optString("gid", userId)); // from config/user block or fallback to userId

        return sessionData;
    }

    public void loginViaUi(String appName, String companyCode, String userId) {
        String env = System.getProperty("env", "test");
        JSONObject user = ConfigManager.getUserConfig(env, appName, companyCode, userId);
        String password = user.getString("password");

        driver.get("https://erplite.storyboarderp.com/v4/#/auth/login");

        // Fill login form
        driver.findElement(companyCodeField).clear();
        driver.findElement(companyCodeField).sendKeys(companyCode);

        driver.findElement(usernameField).clear();
        driver.findElement(usernameField).sendKeys(userId);

        driver.findElement(passwordField).clear();
        driver.findElement(passwordField).sendKeys(password);

        // Wait for overlays to disappear before clicking login!
        new WebDriverWait(driver, java.time.Duration.ofSeconds(20))
                .until(ExpectedConditions.invisibilityOfElementLocated(
                        By.cssSelector(".ngx-spinner-overlay, ngx-spinner, .cdk-overlay-backdrop, .modal-backdrop, .loader")));

        List<WebElement> overlays = driver.findElements(By.cssSelector(".ngx-spinner-overlay, ngx-spinner, .cdk-overlay-backdrop, .modal-backdrop, .loader"));
        for (WebElement overlay : overlays) {
            if (overlay.isDisplayed()) {
                System.out.println("Overlay present: " + overlay.getAttribute("class"));
            }
        }

        driver.findElement(loginButton).click();

        // Wait for login to finish
        new WebDriverWait(driver, java.time.Duration.ofSeconds(30))
                .until(ExpectedConditions.or(
                        ExpectedConditions.urlContains("/dashboard"), // Adjust as needed
                        ExpectedConditions.presenceOfElementLocated(postLoginLocator)
                ));
    }
}