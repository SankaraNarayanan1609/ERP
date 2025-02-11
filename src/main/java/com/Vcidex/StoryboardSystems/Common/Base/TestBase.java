package com.Vcidex.StoryboardSystems.Common.Base;

import com.Vcidex.StoryboardSystems.Utils.ThreadSafeDriverManager;
import com.Vcidex.StoryboardSystems.Utils.WebDriverFactory;
import com.Vcidex.StoryboardSystems.Utils.Config.ConfigManager;
import com.Vcidex.StoryboardSystems.Utils.Reporting.ExtentTestManager;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;

public class TestBase {
    private static final Logger logger = LogManager.getLogger(TestBase.class);

    @BeforeSuite
    public void setupLogging() {
        logger.info("🚀 Test Execution Started - Centralized Logging Enabled.");
    }

    @BeforeClass
    public void setUp() {
        if (ThreadSafeDriverManager.getDriver() == null) {
            logger.info("🚀 Initializing WebDriver...");
            String browser = ConfigManager.getProperty("browser", "chrome");
            boolean headless = Boolean.parseBoolean(ConfigManager.getProperty("headless", "false"));

            WebDriver driver = WebDriverFactory.getDriver(browser, headless);
            ThreadSafeDriverManager.setDriver(driver);
            driver.manage().window().maximize();
        }
    }

    public WebDriver getDriver() {
        return ThreadSafeDriverManager.getDriver();
    }

    @AfterMethod
    public void tearDownTest(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            logger.error("❌ Test failed: {}", result.getThrowable());
            captureScreenshot("TestFailure_" + System.currentTimeMillis());
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

    public void captureScreenshot(String fileName) {
        try {
            File screenshot = ((TakesScreenshot) ThreadSafeDriverManager.getDriver()).getScreenshotAs(OutputType.FILE);
            String screenshotPath = "./screenshots/" + fileName + ".png";
            File destination = new File(screenshotPath);

            FileUtils.copyFile(screenshot, destination);
            logger.info("📸 Screenshot saved at: {}", destination.getAbsolutePath());

            ExtentTest test = ExtentTestManager.getTest();
            if (test != null) {
                test.fail("Screenshot on Failure", MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
            }
        } catch (IOException e) {
            logger.error("❌ Screenshot capture failed", e);
        }
    }
}