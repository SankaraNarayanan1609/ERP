// src/main/java/com/Vcidex/StoryboardSystems/TestBase.java
package com.Vcidex.StoryboardSystems;

import com.Vcidex.StoryboardSystems.Purchase.Factory.ApiMasterDataProvider;
import com.Vcidex.StoryboardSystems.Purchase.Factory.PurchaseOrderDataFactory;
import com.Vcidex.StoryboardSystems.Utils.Config.ConfigManager;
import com.Vcidex.StoryboardSystems.Utils.ThreadSafeDriverManager;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import static io.restassured.RestAssured.given;

public abstract class TestBase {
    private static final Logger logger = LoggerFactory.getLogger(TestBase.class);

    protected static PurchaseOrderDataFactory factory;
    protected WebDriver driver;

    @BeforeSuite(alwaysRun = true)
    public void setupSuite() {
        // ── 1) WebDriver ────────────────────────────────────────────────────────
        String browser = System.getProperty("browser", "chrome");
        switch (browser.toLowerCase()) {
            case "chrome":
                driver = new ChromeDriver();
                break;
            case "firefox":
                driver = new FirefoxDriver();
                break;
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browser);
        }
        driver.manage().window().maximize();
        ThreadSafeDriverManager.setDriver(driver);

        // ── 2) OAuth2 client_credentials ──────────────────────────────────────
        String env           = System.getProperty("env", "test");
        JSONObject auth      = ConfigManager.getAuthConfig(env);
        String authUrl       = auth.getString("authUrl");
        String tokenPath     = auth.getString("tokenPath");
        String clientId      = auth.getString("clientId");
        String clientSecret  = auth.getString("clientSecret");

        String accessToken = "";
        try {
            Response resp = given()
                    .baseUri(authUrl)
                    .contentType("application/x-www-form-urlencoded")
                    .formParam("grant_type",    "client_credentials")
                    .formParam("client_id",     clientId)
                    .formParam("client_secret", clientSecret)
                    .when()
                    .post(tokenPath);


            // ←— LOGGING ADDED HERE —————————————————————————————
            logger.error("Token request returned HTTP {}: {}",
                    resp.statusCode(),
                    resp.asString());
            // ————————————————————————————————————————————————

            if (resp.statusCode() == 200) {
                accessToken = resp.jsonPath().getString("access_token");
            } else {
                System.err.println("⚠️ Warning: token endpoint returned "
                        + resp.statusCode() + " – proceeding with empty token");
            }
        } catch (Exception e) {
            logger.error("Error fetching OAuth token: {}", e.getMessage(), e);
            System.err.println("⚠️ Warning: error fetching OAuth token ("
                    + e.getMessage() + ") – proceeding with empty token");
        }

        // ── 3) Data‐factory ─────────────────────────────────────────────────────
        JSONObject app     = ConfigManager.getAppConfig(env, "StoryboardSystems");
        String     apiBase = app.getString("apiBase");
        ApiMasterDataProvider apiProvider =
                new ApiMasterDataProvider(apiBase, accessToken);
        factory = new PurchaseOrderDataFactory(apiProvider);
    }

    @AfterSuite(alwaysRun = true)
    public void teardownSuite() {
        ThreadSafeDriverManager.removeDriver();
        if (driver != null) {
            driver.quit();
        }
    }
}
