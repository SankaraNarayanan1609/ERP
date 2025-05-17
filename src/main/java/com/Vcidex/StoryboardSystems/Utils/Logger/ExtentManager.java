package com.Vcidex.StoryboardSystems.Utils.Logger;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExtentManager {
    private static ExtentReports extent;
    private static final String reportDir = System.getProperty("user.dir") + "/test-output/";

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(ExtentManager::flushReports));
    }

    public static synchronized ExtentReports getInstance() {
        if (extent == null) {
            String ts = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String name = System.getProperty("report.name","ExtentReport");
            String file = reportDir + name + "_" + ts + ".html";
            new File(reportDir).mkdirs();

            ExtentSparkReporter rep = new ExtentSparkReporter(file);
            rep.config().setReportName("Storyboard Systems Report");
            rep.config().setDocumentTitle("Storyboard Systems Test Report");
            rep.config().setTimelineEnabled(true);

            extent = new ExtentReports();
            extent.attachReporter(rep);
            TestContextLogger.info("📜 Extent Report initialized at: " + file);
        }
        return extent;
    }

    public static void flushReports() {
        if (extent != null) {
            extent.flush();
            TestContextLogger.info("✅ Reports generated successfully.");
        } else {
            TestContextLogger.info("⚠️ No reports to flush.");
        }
    }
}