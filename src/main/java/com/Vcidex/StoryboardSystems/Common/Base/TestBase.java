package com.Vcidex.StoryboardSystems.Common.Base;

import com.Vcidex.StoryboardSystems.Utils.WebDriverFactory;
import com.Vcidex.StoryboardSystems.Utils.Reporting.ExtentTestManager;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.lang.reflect.Method;

public class TestBase {
    protected static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    protected ExtentTest test;

    @BeforeClass
    @Parameters({"browser", "headless"})
    public void setUp(@Optional("chrome") String browser, @Optional("false") boolean headless) {
        driver.set(WebDriverFactory.getDriver(browser, headless));
        getDriver().manage().window().maximize();
    }

    @BeforeMethod
    public void startTest(Method method) {
        test = ExtentTestManager.createTest(method.getName());
    }

    @AfterMethod
    public void tearDownTest(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            test.fail("Test failed: " + result.getThrowable());
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            test.pass("Test passed successfully.");
        }
    }

    @AfterClass
    public void tearDown() {
        if (getDriver() != null) {
            getDriver().quit();
            driver.remove();
        }
        ExtentTestManager.flushReports();
    }

    public static WebDriver getDriver() {
        return driver.get();
    }
}
