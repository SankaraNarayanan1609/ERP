package com.Vcidex.StoryboardSystems.Common.Base;

import com.Vcidex.StoryboardSystems.Utils.ThreadSafeDriverManager;
import com.Vcidex.StoryboardSystems.Utils.WebDriverFactory;
import com.Vcidex.StoryboardSystems.Utils.Reporting.ExtentTestManager;
import com.Vcidex.StoryboardSystems.Utils.Reporting.ErrorHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.lang.reflect.Method;

public class TestBase {
    private static final Logger logger = LogManager.getLogger(TestBase.class);

    @BeforeSuite
    public void setupLogging() {
        logger.info("🚀 Test Execution Started - Centralized Logging Enabled.");
    }

    @BeforeClass
    @Parameters({"browser", "headless"})
    public void setUp(@Optional("chrome") String browser, @Optional("false") String headless) {
        if (ThreadSafeDriverManager.getDriver() == null) {
            logger.info("🚀 Initializing WebDriver...");
            boolean isHeadless = Boolean.parseBoolean(headless);
            WebDriver driver = WebDriverFactory.getDriver(browser, isHeadless);
            ThreadSafeDriverManager.setDriver(driver);
            driver.manage().window().maximize();
        }
    }

    @BeforeMethod
    public void initializeTest(Method method) {
        ExtentTestManager.createTest(method.getName());
        logger.info("🟢 Test Initialized: {}", method.getName());
    }

    @AfterMethod
    public void tearDownTest(ITestResult result) {
        WebDriver driver = ThreadSafeDriverManager.getDriver();
        String testName = result.getName();

        if (result.getStatus() == ITestResult.FAILURE) {
            logger.error("❌ Test Failed: {}", result.getThrowable());
            ErrorHandler.captureScreenshot(driver, testName, ErrorHandler.ScreenshotStatus.FAIL);
            ErrorHandler.captureBrowserLogs(driver, testName);
            ErrorHandler.captureNetworkLogs(driver, testName);
            ExtentTestManager.getTest().fail(result.getThrowable());
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            logger.info("✅ Test Passed: {}", testName);
            ExtentTestManager.getTest().pass("Test Passed Successfully");
        } else {
            logger.warn("⚠️ Test Skipped: {}", testName);
            ExtentTestManager.getTest().skip("Test Skipped: " + result.getThrowable());
        }
    }

    @AfterClass
    public void tearDown() {
        if (ThreadSafeDriverManager.getDriver() != null) {
            logger.info("🔧 Closing WebDriver...");
            ThreadSafeDriverManager.getDriver().quit();
            ThreadSafeDriverManager.removeDriver();
        }
    }

    @AfterSuite
    public void generateReports() {
        logger.info("📊 Finalizing Reports...");
        ExtentTestManager.flushReports();
    }

    public WebDriver getDriver() {
        return ThreadSafeDriverManager.getDriver();
    }
}