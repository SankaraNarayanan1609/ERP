package com.Vcidex.StoryboardSystems;

// ─── Imports ─────────────────────────────────────────────────────────────
import com.Vcidex.StoryboardSystems.Purchase.Factory.ApiMasterDataProvider;
import com.Vcidex.StoryboardSystems.Utils.Config.ConfigManager;
import com.Vcidex.StoryboardSystems.Utils.DataFactory.PurchaseOrderDataFactory;
import com.Vcidex.StoryboardSystems.Utils.ThreadSafeDriverManager;
import com.Vcidex.StoryboardSystems.Utils.UIEventListener;
import com.Vcidex.StoryboardSystems.Utils.Logger.ReportManager;
import org.json.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v134.network.Network;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.openqa.selenium.support.events.WebDriverListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Parameters;
import org.testng.annotations.Optional;
import java.io.File;
import java.time.Duration;
import java.util.List;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

public abstract class TestBase {

    private static final Logger logger = LoggerFactory.getLogger(TestBase.class);

    // ─── Shared Fields for All Child Test Classes ───────────────────────
    protected static PurchaseOrderDataFactory factory;
    protected WebDriver driver;

    protected String appName;
    protected String baseUrl;
    protected String companyCode;
    protected String userId;
    private String loginUrl;

    public static void initDataFactory(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken = bearerToken.substring("Bearer ".length());
        }
        String env = System.getProperty("env", "test");
        JSONObject appConfig = ConfigManager.getAppConfig(env, "StoryboardSystems");
        String apiBase = appConfig.getString("apiBase");
        factory = new PurchaseOrderDataFactory(
                new ApiMasterDataProvider(apiBase, bearerToken)
        );
        logger.info("✅ Initialized data factory with JWT");
    }

    @BeforeSuite(alwaysRun = true)
    @Parameters({ "appName", "companyCode", "userId" })
    public void setupSuite(
            @Optional String paramAppName,
            @Optional String paramCompanyCode,
            @Optional String paramUserId
    ) {
        String env = System.getProperty("env", "test");
        this.appName     = paramAppName     != null ? paramAppName     : "StoryboardSystems";
        this.companyCode = paramCompanyCode != null ? paramCompanyCode : "vcidex";
        this.userId      = paramUserId      != null ? paramUserId      : "vcx288";

        JSONObject appConfig = ConfigManager.getAppConfig(env, appName);
        String loginUrl = appConfig.getString("loginUrl");
        logger.info("App Config: {}", appConfig);

        // ─── ChromeOptions WITHOUT headless ────────────────────────────
        ChromeOptions opts = new ChromeOptions();
        // remove any "--headless" flags here
        opts.setPageLoadStrategy(PageLoadStrategy.EAGER);
        WebDriver raw = new ChromeDriver(opts);

        // wrap with your listener
        driver = new EventFiringDecorator(new UIEventListener()).decorate(raw);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(5));

        ThreadSafeDriverManager.setDriver(driver);
        ReportManager.setDriver(driver);
        logger.info("🚀 WebDriver ready");

        // ─── bump page‐load timeout for the login nav ────────────────
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));

        // ─── do the real UI login ────────────────────────────────────
        LoginManager lm = new LoginManager(driver, null);
        lm.loginViaUi(appName, companyCode, userId);

        // ─── restore your normal (short) page‐load timeout ───────────
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(5));

        // ─── grab the JWT out of localStorage ───────────────────────
        String jwt = (String)((JavascriptExecutor)driver)
                .executeScript("return window.localStorage.getItem('token');");
        if (jwt == null) {
            throw new RuntimeException("UI login succeeded but no token found in localStorage");
        }
        logger.info("✅ Retrieved JWT from UI login");

        // ─── initialize your data factory ────────────────────────────
        initDataFactory(jwt);
    }

    private String fetchJwtViaApi(String companyCode, String userId) {
        try {
            String env     = System.getProperty("env", "test");
            String apiBase = ConfigManager
                    .getAppConfig(env, appName)
                    .getString("apiBase");   // "https://erplite.storyboarderp.com"

            // Build the exact JSON payload your UI sends
            String body = new JSONObject()
                    .put("companyCode", companyCode)
                    .put("userId",      userId)
                    .put("password",    "s")
                    .toString();

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(apiBase + "/StoryboardAPI/api/Login/UserLogin"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> resp = HttpClient.newHttpClient()
                    .send(req, HttpResponse.BodyHandlers.ofString());

            if (resp.statusCode() != 200) {
                throw new RuntimeException("Login API failed ("
                        + resp.statusCode() + "): " + resp.body());
            }

            // Parse the token out of the JSON response
            JSONObject root = new JSONObject(resp.body());
            return root.getString("token");
        } catch (Exception e) {
            throw new RuntimeException("Could not fetch JWT", e);
        }
    }

    @AfterSuite(alwaysRun = true)
    public void tearDownSuite() {
        try {
            ReportManager.flush();
        } catch (Exception e) {
            System.err.println("❌ Report flush failed: " + e.getMessage());
        }
    }
}
