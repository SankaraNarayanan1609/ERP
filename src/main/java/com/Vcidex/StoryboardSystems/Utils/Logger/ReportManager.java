// ReportManager.java
package com.Vcidex.StoryboardSystems.Utils.Logger;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportManager {
    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> testThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    private static final String REPORT_DIR   = System.getProperty("user.dir") + "/test-output/";
    private static final String DEFAULT_NAME = "ExtentReport";

    private static synchronized void initialize() {
        if (extent == null) {
            String ts   = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String name = System.getProperty("report.name", DEFAULT_NAME);
            new File(REPORT_DIR).mkdirs();

            ExtentSparkReporter spark = new ExtentSparkReporter(REPORT_DIR + name + "_" + ts + ".html");
            spark.config().setReportName("Storyboard Systems Report");
            spark.config().setDocumentTitle("Storyboard Systems Test Report");
            spark.config().setTimelineEnabled(true);

            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("OS", System.getProperty("os.name"));
            extent.setSystemInfo("Java", System.getProperty("java.version"));
            extent.setSystemInfo("Environment", System.getProperty("env.name", "unknown"));
        }
    }

    /** Call once you create your WebDriver (e.g. in TestBase). */
    public static void setDriver(WebDriver driver) {
        driverThreadLocal.set(driver);
    }

    /** Used by MasterLogger and DiagnosticsLogger. */
    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    public static ExtentTest createTest(String name) {
        initialize();
        ExtentTest test = extent.createTest(name);
        testThreadLocal.set(test);
        TestContextLogger.info("📝 Extent Test created: " + name);
        return test;
    }

    public static ExtentTest createTest(String name, String... categories) {
        ExtentTest test = createTest(name);
        for (String cat : categories) {
            test.assignCategory(cat);
        }
        return test;
    }

    public static ExtentTest createNode(String name) {
        return getTest().createNode(name);
    }

    public static ExtentTest getTest() {
        ExtentTest t = testThreadLocal.get();
        if (t == null) {
            throw new IllegalStateException("No ExtentTest found; call createTest() first");
        }
        return t;
    }

    /** Switch the current ExtentTest (used by MasterLogger.group()). */
    public static void setTest(ExtentTest test) {
        testThreadLocal.set(test);
    }

    public static void flush() {
        if (extent != null) {
            extent.flush();
            TestContextLogger.info("✅ Reports generated successfully.");
        } else {
            TestContextLogger.info("⚠️ No reports to flush.");
        }
    }
}