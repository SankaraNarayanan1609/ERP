// ExtentTestManager.java
package com.Vcidex.StoryboardSystems.Utils.Logger;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

public class ExtentTestManager {
    private static final ThreadLocal<ExtentTest> testThreadLocal = new ThreadLocal<>();

    public static ExtentTest createTest(String name) {
        ExtentReports rep = ExtentManager.getInstance();
        ExtentTest test = rep.createTest(name);
        testThreadLocal.set(test);
        TestContextLogger.info("📝 Extent Test created: " + name);
        return test;
    }

    public static ExtentTest getTest() {
        ExtentTest t = testThreadLocal.get();
        if (t == null) {
            throw new IllegalStateException("Call createTest() first");
        }
        return t;
    }

    public static void flushReports() {
        ExtentManager.flushReports();
    }
}