package com.Vcidex.StoryboardSystems.Utils.Reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

public class ExtentTestManager {
    private static final ThreadLocal<ExtentTest> testThreadLocal = new ThreadLocal<>();

    public static ExtentTest createTest(String testName) {
        ExtentReports extent = ExtentManager.getInstance();
        ExtentTest test = extent.createTest(testName);
        testThreadLocal.set(test);
        return test;
    }

    public static ExtentTest getTest() {
        return testThreadLocal.get();
    }

    public static void flushReports() {
        ExtentManager.flushReports();
    }
}
