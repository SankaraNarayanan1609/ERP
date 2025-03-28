package com.Vcidex.StoryboardSystems.Purchase.DirectPO_Flow;

import com.Vcidex.StoryboardSystems.Common.Base.TestBase;
import com.Vcidex.StoryboardSystems.Utils.Reporting.ErrorHandler;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

public class ScreenshotTest extends TestBase {

    @Test
    public void testScreenshotCapture() {
        // Get the active WebDriver instance
        WebDriver driver = getDriver();
        // Call the screenshot capture method
        ErrorHandler.captureScreenshot(driver, "TestCapture", "Debug");
    }
}