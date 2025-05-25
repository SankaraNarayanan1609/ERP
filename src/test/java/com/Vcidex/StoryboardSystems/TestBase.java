// src/main/java/com/Vcidex/StoryboardSystems/TestBase.java
package com.Vcidex.StoryboardSystems;

import com.Vcidex.StoryboardSystems.Purchase.Factory.ApiMasterDataProvider;
import com.Vcidex.StoryboardSystems.Purchase.Factory.PurchaseOrderDataFactory;
import com.Vcidex.StoryboardSystems.Utils.Config.ConfigManager;
import com.Vcidex.StoryboardSystems.Utils.ThreadSafeDriverManager;
import com.Vcidex.StoryboardSystems.Utils.Logger.UIActionLogger;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.openqa.selenium.support.events.WebDriverListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

public abstract class TestBase {
    private static final Logger logger = LoggerFactory.getLogger(TestBase.class);

    protected static PurchaseOrderDataFactory factory;
    protected WebDriver               driver;

    /** Called by LoginManager after a successful API-login */
    public static void initDataFactory(String bearerToken) {
        String env     = System.getProperty("env", "test");
        JSONObject app = ConfigManager.getAppConfig(env, "StoryboardSystems");
        String apiBase = app.getString("apiBase");
        ApiMasterDataProvider apiProvider =
                new ApiMasterDataProvider(apiBase, bearerToken);
        factory = new PurchaseOrderDataFactory(apiProvider);
        logger.info("✅ Initialized data factory with API token");
    }

    @BeforeSuite(alwaysRun = true)
    public void setupSuite() {
        // 1) Create the “raw” driver
        WebDriver raw;
        String browser = System.getProperty("browser", "chrome");
        switch (browser.toLowerCase()) {
            case "chrome":  raw = new ChromeDriver();  break;
            case "firefox": raw = new FirefoxDriver(); break;
            default: throw new IllegalArgumentException("Unsupported browser: " + browser);
        }
        raw.manage().window().maximize();

        // 2) Decorate with our UI listener
        WebDriverListener listener = new UIEventListener();
        driver = new EventFiringDecorator(listener).decorate(raw);

        ThreadSafeDriverManager.setDriver(driver);
        logger.info("🚀 WebDriver initialized with UIEventListener");
    }

    @AfterSuite(alwaysRun = true)
    public void teardownSuite() {
        ThreadSafeDriverManager.removeDriver();
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * A lightweight listener that hooks into clicks, sendKeys and navigation
     * and delegates to your existing UIActionLogger.
     */
    private static class UIEventListener implements WebDriverListener {
        @Override
        public void beforeGet(WebDriver driver, String url) {
            UIActionLogger.debug("Navigate ▶ " + url);
        }
        @Override
        public void afterGet(WebDriver driver, String url) {
            UIActionLogger.debug("Landed on ▶ " + url);
        }
        @Override
        public void beforeClick(WebElement element) {
            UIActionLogger.debug("Click ▶ " + describe(element));
        }
        @Override
        public void afterClick(WebElement element) {
            // note: here we only have the element, not the original By,
            // so this logs the action—your page-objects still drive the details
            UIActionLogger.click(driverOf(element), byOf(element), describe(element));
        }
        @Override
        public void beforeSendKeys(WebElement element, CharSequence... keys) {
            UIActionLogger.debug("Type ▶ " + describe(element) + " : '" + String.join("", keys) + "'");
        }
        @Override
        public void afterSendKeys(WebElement element, CharSequence... keys) {
            UIActionLogger.type(driverOf(element), byOf(element), String.join("", keys), describe(element));
        }

        // ── Helpers ─────────────────────────────────────────────────────────
        private String describe(WebElement el) {
            try { return el.toString(); }
            catch (Exception e) { return "<unknown>"; }
        }
        private org.openqa.selenium.By byOf(WebElement el) {
            // fallback if you can't extract the real locator:
            return org.openqa.selenium.By.xpath(el.toString());
        }
        private WebDriver driverOf(WebElement el) {
            // every WebElement wraps an internal RemoteWebDriver:
            return ((org.openqa.selenium.remote.RemoteWebElement) el).getWrappedDriver();
        }
    }
}