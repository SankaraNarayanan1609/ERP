package com.Vcidex.StoryboardSystems.Utils.Reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentManager {
    private static ExtentReports extent;

    private ExtentManager() {}

    public static synchronized ExtentReports getInstance() {
        if (extent == null) {
            synchronized (ExtentManager.class) {
                if (extent == null) {
                    extent = new ExtentReports();
                    ExtentSparkReporter reporter = new ExtentSparkReporter("test-output/ExtentReport.html");
                    extent.attachReporter(reporter);
                }
            }
        }
        return extent;
    }

    public ExtentTest createTest(String testName) {
        return getInstance().createTest(testName);
    }

    public static void flushReports() {
        getInstance().flush();
    }
}