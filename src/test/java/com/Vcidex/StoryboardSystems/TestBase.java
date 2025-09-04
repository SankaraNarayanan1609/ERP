package com.Vcidex.StoryboardSystems;

import com.Vcidex.StoryboardSystems.Purchase.Factory.ApiMasterDataProvider;
import com.Vcidex.StoryboardSystems.Utils.Config.ConfigManager;
import com.Vcidex.StoryboardSystems.Utils.DataFactory.PurchaseIndentDataFactory;
import com.Vcidex.StoryboardSystems.Utils.DataFactory.PurchaseOrderDataFactory;
import com.Vcidex.StoryboardSystems.Utils.TestContext;
import com.Vcidex.StoryboardSystems.Utils.ThreadSafeDriverManager;
import com.Vcidex.StoryboardSystems.Utils.UIEventListener;
import com.Vcidex.StoryboardSystems.Utils.Logger.ReportManager;
import com.aventstack.extentreports.ExtentTest;
import org.json.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;
import org.openqa.selenium.logging.LoggingPreferences;

import java.time.Duration;
import java.util.logging.Level;

public abstract class TestBase {

    private static final Logger logger = LoggerFactory.getLogger(TestBase.class);

    protected WebDriver driver;
    protected String appName;
    protected String baseUrl;
    protected String companyCode;
    protected String userId;
    private String loginUrl;

    // Shared across tests
    protected ApiMasterDataProvider api;
    protected PurchaseOrderDataFactory poFactory;
    protected PurchaseIndentDataFactory indentFactory;

    @BeforeSuite(alwaysRun = true)
    @Parameters({ "appName", "companyCode", "userId" })
    public void setupSuite(
            @Optional String paramAppName,
            @Optional String paramCompanyCode,
            @Optional String paramUserId
    ) {
        final String env   = System.getProperty("env", "test");
        final String build = System.getProperty("build", "local");

        this.appName     = (paramAppName     != null) ? paramAppName     : "StoryboardSystems";
        this.companyCode = (paramCompanyCode != null) ? paramCompanyCode : "vcidex";
        this.userId      = (paramUserId      != null) ? paramUserId      : "superadmin";

        TestContext.set("userId", userId);
        TestContext.set("companyCode", companyCode);
        TestContext.set("appName", appName);

        JSONObject appConfig = ConfigManager.getAppConfig(env, appName);
        this.loginUrl = appConfig.getString("loginUrl");
        String apiBase = appConfig.getString("apiBase");
        logger.info("App Config: {}", appConfig);

        // Init Extent
        ReportManager.init(env, build);
        System.out.println("📄 Extent report will be at: " +
                ReportManager.outDir().resolve("index.html").toAbsolutePath());

        // WebDriver boot
        ChromeOptions opts = new ChromeOptions();
        opts.setPageLoadStrategy(PageLoadStrategy.EAGER);
        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.BROWSER, Level.ALL);
        opts.setCapability("goog:loggingPrefs", logPrefs);

        WebDriver raw = new ChromeDriver(opts);
        driver = new EventFiringDecorator(new UIEventListener()).decorate(raw);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(5));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        ThreadSafeDriverManager.setDriver(driver);
        logger.info("🚀 WebDriver ready");

        // UI login
        LoginManager lm = new LoginManager(driver, appName, companyCode, userId);
        lm.loginViaUi();

        ExtentTest root = ReportManager.createTest("🔧 Suite Setup: " + appName + " (" + env + ")", "Setup");
        ReportManager.setTest(root);
        ReportManager.table(new String[][]{
                {"User ID",     userId},
                {"Environment", env},
                {"Company",     companyCode},
                {"App",         appName}
        }, "Login");

        // Relax pageLoad timeout after landing
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(5));

        // 🔑 Grab JWT from localStorage (strip prefix if present)
        String jwt = (String)((JavascriptExecutor)driver)
                .executeScript("return window.localStorage.getItem('token');");
        if (jwt == null) {
            throw new RuntimeException("UI login succeeded but no token found in localStorage");
        }
        if (jwt.startsWith("Bearer ")) {
            jwt = jwt.substring("Bearer ".length());
        }

        // ✅ Build API provider & data factories with the authenticated token
        api           = new ApiMasterDataProvider(apiBase, jwt); // 2-arg ctor
        poFactory     = new PurchaseOrderDataFactory(api);
        indentFactory = new PurchaseIndentDataFactory(api);

        logger.info("✅ Factories initialized with JWT against {}", apiBase);
    }

    @AfterSuite(alwaysRun = true)
    public void tearDownSuite() {
        try {
            ReportManager.flush();
            System.out.println("🧾 Extent report flushed: " +
                    ReportManager.outDir().resolve("index.html").toAbsolutePath());
        } catch (Exception e) {
            System.err.println("❌ Report flush failed: " + e.getMessage());
        }
    }
}