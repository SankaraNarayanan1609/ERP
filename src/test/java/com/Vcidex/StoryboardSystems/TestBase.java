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
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.openqa.selenium.support.events.WebDriverListener;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import java.time.Duration;
import java.io.File;

import static org.openqa.selenium.support.ui.ExpectedConditions.jsReturnsValue;

public abstract class TestBase {

    private static final Logger logger = LoggerFactory.getLogger(TestBase.class);

    // Default timeout used in WebDriver waits
    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    // ─── Shared Fields for All Child Test Classes ───────────────────────
    protected static PurchaseOrderDataFactory factory; // Used by factory.createDirectPO()
    protected WebDriver driver;

    protected String appName;       // E.g., "StoryboardSystems"
    protected String baseUrl;  // API base URL from config.json
    protected String token;    // Token pulled after UI login
    protected String companyCode;   // E.g., "vcidex"
    protected String userId;        // E.g., "vcx288"
    private String loginUrl;        // URL pulled from config.json

    /**
     * Initializes test data factory using token from UI login.
     *
     * @param bearerToken full "Bearer …" token from localStorage
     */
    public static void initDataFactory(String bearerToken) {
        // strip off the "Bearer " prefix, if present
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken = bearerToken.substring("Bearer ".length());
        }

        // load your app config to get the API base URL
        String env = System.getProperty("env", "test");
        JSONObject appConfig = ConfigManager.getAppConfig(env, "StoryboardSystems");
        String apiBase = appConfig.getString("apiBase");
        if (apiBase == null || apiBase.isEmpty()) {
            throw new IllegalArgumentException("Base URL cannot be null or empty.");
        }

        // **DON'T** read apiToken from config.json (it doesn’t exist);
        // just pass the UI-login token into your data factory
        factory = new PurchaseOrderDataFactory(
                new ApiMasterDataProvider(apiBase, bearerToken)
        );
        logger.info("✅ Initialized data factory with UI login token");
    }

    /**
     * Logs into the app via UI and ensures token is available after login.
     * Called inside setupSuite() after browser is ready.
     */
    protected void setupSession() {
        driver.get(loginUrl);

        // Logs in using LoginManager
        new LoginManager(driver, null).loginViaUi(appName, companyCode, userId);
        logger.info("✅ Logged in via UI");

        try {
            // Wait for token to appear in localStorage
            WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
            wait.until(jsReturnsValue("return !!window.localStorage.getItem('token');"));
        } catch (TimeoutException e) {
            logger.warn("⚠️ Timeout while waiting for token in localStorage");
        }

        // Log for debugging
        String url   = driver.getCurrentUrl();
        String token = (String)((JavascriptExecutor) driver)
                .executeScript("return window.localStorage.getItem('token');");
        logger.info("🔎 Post-login URL: {}", url);
        logger.info("🔑 Post-login token: {}", token);
    }

    /**
     * Runs once before all tests in the suite.
     * - Initializes browser
     * - Loads app URL
     * - Logs in
     * - Registers driver with listeners and reports
     */


    @BeforeSuite(alwaysRun = true)
    @Parameters({ "appName", "companyCode", "userId" })
    public void setupSuite(
            @Optional String paramAppName,
            @Optional String paramCompanyCode,
            @Optional String paramUserId
    ) {
        String env = System.getProperty("env", "test");

        // fallback parameters
        this.appName     = paramAppName     != null ? paramAppName     : "StoryboardSystems";
        this.companyCode = paramCompanyCode != null ? paramCompanyCode : "vcidex";
        this.userId      = paramUserId      != null ? paramUserId      : "vcx288";

        JSONObject appConfig = ConfigManager.getAppConfig(env, appName);
        loginUrl = appConfig.getString("loginUrl");

        logger.info("App Config: {}", appConfig);
        logger.info("API Base URL: {}", appConfig.getString("apiBase"));

        // pick browser
        String browser = System.getProperty("browser", "chrome").toLowerCase();

        // ─── generate a unique temp profile for Chrome ─────────────────────────
        String tmpDir     = System.getProperty("java.io.tmpdir");
        String profileDir = tmpDir + File.separator + "chrome-profile-" + System.currentTimeMillis();
        new File(profileDir).mkdirs();

        WebDriver raw = switch (browser) {
            case "chrome" -> {
                ChromeOptions opts = new ChromeOptions();
                opts.addArguments("--disable-blink-features=AutomationControlled");
                opts.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
                opts.setExperimentalOption("useAutomationExtension", false);
                opts.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
                // point ChromeDriver at our fresh profile
                opts.addArguments("--user-data-dir=" + profileDir);

                ChromeDriver chromeDriver = new ChromeDriver(opts);
                JavascriptExecutor js = chromeDriver;
                try {
                    js.executeScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");
                    js.executeScript("Object.defineProperty(navigator, 'plugins',   {get: () => [1,2,3]})");
                    js.executeScript("Object.defineProperty(navigator, 'languages',{get: () => ['en-US','en']})");
                    js.executeScript("window.chrome = { runtime: {} };");
                } catch (Exception e) {
                    logger.warn("⚠️ Stealth JS patch failed: {}", e.getMessage());
                }
                yield chromeDriver;
            }

            case "firefox" -> new FirefoxDriver();

            default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
        };

        // register and wrap your driver
        WebDriverListener listener = new UIEventListener();
        driver = new EventFiringDecorator(listener).decorate(raw);

        // now set your window + timeouts on the decorated driver
        driver.manage().window().maximize();

        // give async scripts up to 60s instead of the default 30
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(60));

        // (optional) other timeouts
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));

        // register for reporting / thread-safety
        ThreadSafeDriverManager.setDriver(driver);
        ReportManager.setDriver(driver);

        logger.info("🚀 WebDriver initialized and registered with 60s script timeout");

        // login and init data factory
        setupSession();
        String bearerToken = (String)((JavascriptExecutor) driver)
                .executeScript("return window.localStorage.getItem('token');");
        initDataFactory(bearerToken);
    }

    /**
     * Cleans up driver after all tests complete.
     */
    @AfterSuite(alwaysRun = true)
    public void tearDownSuite() {
        try {
            ReportManager.flush(); // Always try to write the report
        } catch (Exception e) {
            System.err.println("❌ Failed to flush Extent Report: " + e.getMessage());
        }
    }
}