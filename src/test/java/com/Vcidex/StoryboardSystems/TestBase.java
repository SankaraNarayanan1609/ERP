package com.Vcidex.StoryboardSystems;

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

import static org.openqa.selenium.support.ui.ExpectedConditions.jsReturnsValue;

public abstract class TestBase {
    private static final Logger logger = LoggerFactory.getLogger(TestBase.class);
    private static final String LOGIN_URL = "https://erplite.storyboarderp.com/v4/#/auth/login";
    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    protected static PurchaseOrderDataFactory factory;
    protected WebDriver driver;

    protected String appName     = "StoryboardSystems";
    protected String companyCode = "vcidex";
    protected String userId      = "vcx288";

    public static void initDataFactory(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken = bearerToken.substring("Bearer ".length());
        }
        String env = System.getProperty("env", "test");
        JSONObject app = ConfigManager.getAppConfig(env, "StoryboardSystems");
        String apiBase = app.getString("apiBase");
        factory = new PurchaseOrderDataFactory(new ApiMasterDataProvider(apiBase, bearerToken));
        logger.info("✅ Initialized data factory with API token");
    }

    protected void setupSession() {
        driver.get(LOGIN_URL);
        new LoginManager(driver, null).loginViaUi(appName, companyCode, userId);
        logger.info("✅ Logged in via UI");

        try {
            WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
            wait.until(jsReturnsValue("return !!window.localStorage.getItem('token');"));
        } catch (TimeoutException e) {
            logger.warn("⚠️ Timeout while waiting for token in localStorage");
        }

        String url   = driver.getCurrentUrl();
        String token = (String)((JavascriptExecutor) driver)
                .executeScript("return window.localStorage.getItem('token');");
        logger.info("🔎 Post-login URL: {}", url);
        logger.info("🔑 Post-login token: {}", token);
    }

    @BeforeSuite(alwaysRun = true)
    public void setupSuite() {
        String browser = System.getProperty("browser", "chrome").toLowerCase();
        WebDriver raw = switch (browser) {
            case "chrome" -> {
                ChromeOptions opts = new ChromeOptions();
                opts.addArguments("--disable-blink-features=AutomationControlled");
                opts.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
                opts.setExperimentalOption("useAutomationExtension", false);
                opts.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64)"
                        + " AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36");
                ChromeDriver chromeDriver = new ChromeDriver(opts);
                JavascriptExecutor js = chromeDriver;
                try {
                    js.executeScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");
                    js.executeScript("Object.defineProperty(navigator, 'plugins', {get: () => [1,2,3]})");
                    js.executeScript("Object.defineProperty(navigator, 'languages', {get: () => ['en-US','en']})");
                    js.executeScript("window.chrome = { runtime: {} };");
                } catch (Exception e) {
                    logger.warn("⚠️ Stealth JS patch failed: {}", e.getMessage());
                }
                yield chromeDriver;
            }
            case "firefox" -> new FirefoxDriver();
            default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
        };

        WebDriverListener listener = new UIEventListener();
        driver = new EventFiringDecorator(listener).decorate(raw);
        driver.manage().window().maximize();

        ThreadSafeDriverManager.setDriver(driver);
        ReportManager.setDriver(driver);
        logger.info("🚀 WebDriver initialized and registered");

        setupSession();
    }

    @AfterSuite(alwaysRun = true)
    public void teardownSuite() {
        ThreadSafeDriverManager.removeDriver();
        if (driver != null) {
            driver.quit();
            logger.info("🔴 WebDriver session ended");
        }
    }
}