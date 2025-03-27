package com.Vcidex.StoryboardSystems.Utils.Reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExtentManager {
    private static ExtentReports extent;

    private ExtentManager() {}

    public static synchronized ExtentReports getInstance() {
        if (extent == null) {
            synchronized (ExtentManager.class) {
                if (extent == null) {
                    // ✅ Generate Unique Report Name
                    String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String reportDir = System.getProperty("user.dir") + "/test-output/";
                    String reportFile = reportDir + "ExtentReport_" + timestamp + ".html";

                    // ✅ Ensure "test-output" folder exists
                    File directory = new File(reportDir);
                    if (!directory.exists() && !directory.mkdirs()) {
                        throw new RuntimeException("❌ Failed to create test-output directory.");
                    }

                    // ✅ Initialize ExtentReports
                    extent = new ExtentReports();
                    ExtentSparkReporter reporter = new ExtentSparkReporter(reportFile);
                    extent.attachReporter(reporter);

                    System.out.println("📜 [DEBUG] Extent Report Initialized at: " + reportFile);
                }
            }
        }
        return extent;
    }

    public static void flushReports() {
        if (extent != null) {
            try {
                System.out.println("🔄 [DEBUG] Flushing Extent Reports...");
                extent.flush();
                System.out.println("✅ [DEBUG] Extent Reports saved.");
            } catch (Exception e) {
                System.out.println("⚠️ [ERROR] Failed to flush reports: " + e.getMessage());
            }
        } else {
            System.out.println("⚠️ [ERROR] Extent Reports instance was null.");
        }
    }
}
