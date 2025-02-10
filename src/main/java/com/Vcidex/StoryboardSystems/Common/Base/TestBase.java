package com.Vcidex.StoryboardSystems.Common.Base;

import com.Vcidex.StoryboardSystems.Utils.Reporting.ExtentTestManager;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver; // ✅ Fix: Import ChromeDriver
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;

public class TestBase {
    protected WebDriver driver;
    protected static final Logger logger = LogManager.getLogger(TestBase.class);

    @BeforeClass
    public void setUp() {
        logger.info("🚀 Setting up WebDriver...");
        driver = new ChromeDriver();  // ✅ Now works
        driver.manage().window().maximize();
    }

    @AfterMethod
    public void tearDownTest(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            logger.error("❌ Test failed: ", result.getThrowable());
            captureScreenshot("TestFailure_" + System.currentTimeMillis());
        }
    }

    @AfterClass
    public void tearDown() {
        logger.info("🔧 Closing WebDriver...");
        if (driver != null) {
            driver.quit();
        }
    }

    public void captureScreenshot(String fileName) {
        try {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
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