package com.Vcidex.StoryboardSystems;

import com.Vcidex.StoryboardSystems.Purchase.Factory.ApiMasterDataProvider;
import com.Vcidex.StoryboardSystems.Purchase.Factory.PurchaseOrderDataFactory;
import com.Vcidex.StoryboardSystems.Utils.Config.ConfigManager;
import com.Vcidex.StoryboardSystems.Utils.ThreadSafeDriverManager;
import com.Vcidex.StoryboardSystems.Utils.UIEventListener;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.openqa.selenium.support.events.WebDriverListener;
import org.openqa.selenium.JavascriptExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import java.util.HashMap;
import java.util.Map;

public abstract class TestBase {
    private static final Logger logger = LoggerFactory.getLogger(TestBase.class);

    protected static PurchaseOrderDataFactory factory;
    protected WebDriver driver;

    public static void initDataFactory(String bearerToken) {
        String env = System.getProperty("env", "test");
        JSONObject app = ConfigManager.getAppConfig(env, "StoryboardSystems");
        String apiBase = app.getString("apiBase");
        ApiMasterDataProvider apiProvider = new ApiMasterDataProvider(apiBase, bearerToken);
        factory = new PurchaseOrderDataFactory(apiProvider);
        logger.info("✅ Initialized data factory with API token");
    }

    protected String extractTokenFromLocalStorage() {
        return (String) ((JavascriptExecutor) driver)
                .executeScript("return window.localStorage.getItem('token');");
    }

    /**
     * Use this method to simulate an authenticated session by populating localStorage.
     * Call it after opening the base URL and before navigation/actions.
     */
    protected String appName = "StoryboardSystems";
    protected String companyCode = "vcidex";
    protected String userId = "vcx288";
    protected void setupSession() {
        driver.get("https://erplite.storyboarderp.com/v4/#/auth/login");
        LoginManager loginManager = new LoginManager(driver, null);
        loginManager.loginViaUi(appName, companyCode, userId);
        logger.info("✅ Logged in via UI automation.");
        // Give Angular time to save token to localStorage
        try{
            Thread.sleep(1500); // Unhandled exception: java.lang.InterruptedException
            driver.navigate().refresh();
            Thread.sleep(1000);
            driver.navigate().refresh();
            String url = driver.getCurrentUrl();
            String token = (String) ((JavascriptExecutor) driver)
                    .executeScript("return window.localStorage.getItem('token');");
            logger.info("🔎 Post-login URL: " + url);
            logger.info("🔑 Post-login token: " + token);
        } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        logger.warn("Setup session interrupted: " + e.getMessage());
    }

    }

    @BeforeSuite(alwaysRun = true)
    public void setupSuite() {
        WebDriver raw;
        String browser = System.getProperty("browser", "chrome");
        switch (browser.toLowerCase()) {
            case "chrome":
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--disable-blink-features=AutomationControlled");
                options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
                options.setExperimentalOption("useAutomationExtension", false);
                options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36");
                // options.addArguments("--headless=new"); // Uncomment if you need headless

                raw = new ChromeDriver(options);

                // Stealth/anti-detect JavaScript patches
                JavascriptExecutor js = (JavascriptExecutor) raw;
                try {
                    js.executeScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");
                    js.executeScript("Object.defineProperty(navigator, 'plugins', {get: () => [1,2,3]})");
                    js.executeScript("Object.defineProperty(navigator, 'languages', {get: () => ['en-US', 'en']})");
                    js.executeScript("window.chrome = { runtime: {} };");
                    raw.manage().window().maximize();
                } catch (Exception e) {
                    logger.warn("⚠️ Stealth JS patch failed: " + e.getMessage());
                }
                break;
            case "firefox":
                raw = new FirefoxDriver();
                break;
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browser);
        }
        raw.manage().window().maximize();

        WebDriverListener listener = new UIEventListener();
        driver = new EventFiringDecorator(listener).decorate(raw);

        ThreadSafeDriverManager.setDriver(driver);
        logger.info("🚀 WebDriver initialized with UIEventListener");

        // *** INJECT SESSION LOCAL STORAGE AFTER DRIVER INIT ***
        setupSession(); // <-- This logs you in via Local Storage spoofing!
    }

    @AfterSuite(alwaysRun = true)
    public void teardownSuite() {
        ThreadSafeDriverManager.removeDriver();
        if (driver != null) {
            driver.quit();
        }
    }
}