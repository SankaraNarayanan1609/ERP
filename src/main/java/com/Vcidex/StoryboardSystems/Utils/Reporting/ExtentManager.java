package com.Vcidex.StoryboardSystems.Utils.Reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExtentManager {

    private static ExtentReports extent;

    /**
     * Initialize and return an ExtentReports instance using ExtentSparkReporter.
     * Singleton implementation ensures a single instance across the test suite.
     *
     * @return ExtentReports instance.
     */
    public static synchronized ExtentReports getInstance() {
        if (extent == null) {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String reportDir = System.getProperty("user.dir") + "/test-output/";
            String reportFile = reportDir + "ExtentReport_" + timestamp + ".html";

            // Ensure directory exists
            new File(reportDir).mkdirs();

            ExtentSparkReporter reporter = new ExtentSparkReporter(reportFile);

            // Configure Report Settings
            reporter.config().setReportName("Storyboard Systems - Test Execution Report");
            reporter.config().setDocumentTitle("Storyboard Systems Test Report");
            reporter.config().setTimelineEnabled(true);

            extent = new ExtentReports();
            extent.attachReporter(reporter);
            System.out.println("📜 [INFO] Extent Report initialized at: " + reportFile);
        }
        return extent;
    }

    /**
     * Flush the ExtentReports to write all test results to the report file.
     */
    public static void flushReports() {
        if (extent != null) {
            try {
                extent.flush();
                System.out.println("✅ Reports generated successfully.");
            } catch (Exception e) {
                System.err.println("❗ Error while generating reports: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("⚠️ No reports to flush. ExtentReports instance is null.");
        }
    }
}