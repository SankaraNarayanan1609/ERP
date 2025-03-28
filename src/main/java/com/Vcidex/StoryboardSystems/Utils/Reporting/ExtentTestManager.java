package com.Vcidex.StoryboardSystems.Utils.Reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

public class ExtentTestManager {

    private static final ThreadLocal<ExtentTest> testThreadLocal = new ThreadLocal<>();

    /**
     * Create a new test in ExtentReports and associate it with the current thread.
     *
     * @param testName Name of the test to be displayed in the report.
     * @return ExtentTest object for logging test information.
     */
    public static ExtentTest createTest(String testName) {
        ExtentReports extent = ExtentManager.getInstance();
        ExtentTest test = extent.createTest(testName);
        testThreadLocal.set(test);
        return test;
    }

    /**
     * Retrieve the ExtentTest object associated with the current thread.
     *
     * @return ExtentTest instance.
     * @throws IllegalStateException if no test has been created using createTest().
     */
    public static ExtentTest getTest() {
        ExtentTest test = testThreadLocal.get();
        if (test == null) {
            throw new IllegalStateException("ExtentTest not initialized. Please call createTest() before using getTest().");
        }
        return test;
    }

    /**
     * Flush the reports and save them to the configured location.
     */
    public static void flushReports() {
        ExtentManager.flushReports();
    }
}