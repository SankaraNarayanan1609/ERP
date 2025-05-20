package com.Vcidex.StoryboardSystems.Utils.Logger;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

public class ExtentTestManager {
    private static final ThreadLocal<ExtentTest> testThreadLocal = new ThreadLocal<>();

    /** Simple test creation */
    public static ExtentTest createTest(String name) {
        ExtentReports rep = ExtentManager.getInstance();
        ExtentTest test = rep.createTest(name);
        testThreadLocal.set(test);
        TestContextLogger.info("📝 Extent Test created: " + name);
        return test;
    }

    /** Create a test and assign one or more categories (e.g. modules) */
    public static ExtentTest createTest(String name, String... categories) {
        ExtentTest test = createTest(name);
        for (String cat : categories) {
            test.assignCategory(cat);
        }
        return test;
    }

    public static ExtentTest getTest() {
        ExtentTest t = testThreadLocal.get();
        if (t == null) throw new IllegalStateException("Call createTest() first");
        return t;
    }

    public static void flushReports() {
        ExtentManager.flushReports();
    }
}