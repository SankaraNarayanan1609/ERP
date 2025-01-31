package com.Vcidex.StoryboardSystems.Utils.Reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

public class ExtentTestManager {
    private static final ThreadLocal<ExtentTest> extentTestThreadLocal = new ThreadLocal<>();
    private static final ExtentReports extent = ExtentManager.getInstance(); // ✅ Retrieve from ExtentManager

    public static synchronized ExtentTest createTest(String testName) {
        ExtentTest test = extent.createTest(testName);
        extentTestThreadLocal.set(test);
        return test;
    }

    public static synchronized ExtentTest getTest() {
        ExtentTest test = extentTestThreadLocal.get();
        if (test == null) {
            throw new IllegalStateException("No ExtentTest instance found for this thread.");
        }
        return test;
    }

    public static synchronized void removeTest() {
        extentTestThreadLocal.remove();
    }

    public static void flushReports() {
        extent.flush();
    }
}