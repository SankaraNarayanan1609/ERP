package com.Vcidex.StoryboardSystems.Utils.Reporting;

import com.Vcidex.StoryboardSystems.Common.Base.BasePage;
import org.openqa.selenium.WebDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;

import java.io.File;

public class TestListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        Object testClass = result.getInstance();
        if (testClass instanceof BasePage) {
            WebDriver driver = ((BasePage) testClass).driver;

            if (driver != null) {
                String screenshotName = result.getName() + "_Failure_" + System.currentTimeMillis();
                BasePage basePage = (BasePage) testClass;
                basePage.captureScreenshot(screenshotName);// ✅ Capture Screenshot
                Reporter.log("📸 Screenshot captured: " + screenshotName, true);
            }
        }
    }
}
