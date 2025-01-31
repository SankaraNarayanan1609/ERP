package com.Vcidex.StoryboardSystems.Utils.Reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentManager {
    private static ExtentReports extent;

    public static synchronized ExtentReports getInstance() { // ✅ Fixed method definition
        if (extent == null) {
            extent = new ExtentReports();
            ExtentSparkReporter reporter = new ExtentSparkReporter("test-output/ExtentReport.html");
            extent.attachReporter(reporter);
        }
        return extent; // ✅ Correctly placed return statement
    }
}
