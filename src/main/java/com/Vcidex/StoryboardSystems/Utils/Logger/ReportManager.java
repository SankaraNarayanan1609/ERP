// ReportManager.java
package com.Vcidex.StoryboardSystems.Utils.Logger;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Combines ExtentReports setup and per-thread ExtentTest management.
 */
public class ReportManager {
    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> testThreadLocal = new ThreadLocal<>();
    private static final String REPORT_DIR = System.getProperty("user.dir") + "/test-output/";
    private static final String DEFAULT_NAME = "ExtentReport";

    /** Initialize the ExtentReports instance (once). */
    public static synchronized void initialize() {
        if (extent == null) {
            String ts = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String name = System.getProperty("report.name", DEFAULT_NAME);
            String file = REPORT_DIR + name + "_" + ts + ".html";
            new File(REPORT_DIR).mkdirs();

            ExtentSparkReporter spark = new ExtentSparkReporter(file);
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

    /** Create a root test and attach it to current thread. */
    public static ExtentTest createTest(String name) {
        initialize();
        ExtentTest test = extent.createTest(name);
        testThreadLocal.set(test);
        TestContextLogger.info("📝 Extent Test created: " + name);
        return test;
    }

    /** Create a root test with categories. */
    public static ExtentTest createTest(String name, String... categories) {
        ExtentTest test = createTest(name);
        for (String cat : categories) {
            test.assignCategory(cat);
        }
        return test;
    }

    /** Create a child node under the current test. */
    public static ExtentTest createNode(String name) {
        return getTest().createNode(name);
    }

    /** Retrieve the current thread's ExtentTest. */
    public static ExtentTest getTest() {
        ExtentTest t = testThreadLocal.get();
        if (t == null) {
            throw new IllegalStateException("No ExtentTest found; call createTest() first");
        }
        return t;
    }

    /** Flush and write out all pending reports. */
    public static void flush() {
        if (extent != null) {
            extent.flush();
            TestContextLogger.info("✅ Reports generated successfully.");
        } else {
            TestContextLogger.info("⚠️ No reports to flush.");
        }
    }
}