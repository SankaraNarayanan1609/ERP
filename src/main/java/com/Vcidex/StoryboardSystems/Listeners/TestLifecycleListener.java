package com.Vcidex.StoryboardSystems.Listeners;

import com.Vcidex.StoryboardSystems.Utils.ThreadSafeDriverManager;
import com.Vcidex.StoryboardSystems.Utils.Logger.ReportManager;
import com.Vcidex.StoryboardSystems.Utils.Logger.ScreenshotHelper;
import com.Vcidex.StoryboardSystems.Utils.Logger.TestContextLogger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.nio.file.Path;

public class TestLifecycleListener implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        String name = result.getMethod().getMethodName();
        TestContextLogger.info("🔄 Starting test: " + name);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String name = result.getMethod().getMethodName();
        TestContextLogger.info("✅ Test succeeded: " + name);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String name = result.getMethod().getMethodName();
        Throwable t = result.getThrowable();
        TestContextLogger.info("❌ Test failed: " + name + " → " + t);

        // SRP: ScreenshotHelper owns capture
        WebDriver driver = ThreadSafeDriverManager.getDriver();
        if (driver != null) {
            Path shot = ScreenshotHelper.capture(driver, name);
            TestContextLogger.info("📸 Screenshot saved to: " + shot);
            // Optionally attach to Extent report
            try {
                ReportManager.getTest()
                        .addScreenCaptureFromPath(shot.toString());
            } catch (Exception e) {
                TestContextLogger.info("⚠️ Could not attach screenshot to report: " + e);
            }
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String name = result.getMethod().getMethodName();
        TestContextLogger.info("⏭️ Test skipped: " + name);
    }

    // no-op; we don’t need onStart per-test-suite hooks here
    @Override public void onStart(ITestContext context) { }

    /** At the very end of this <test> tag, flush everything */
    @Override
    public void onFinish(ITestContext context) {
        TestContextLogger.info("🛑 All tests finished; flushing report.");
        ReportManager.flush();
    }
}