package com.Vcidex.StoryboardSystems.Utils.Logger;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

/**
 * Manages the lifecycle of ExtentTest instances on a per-thread basis,
 * and now supports creation of nested nodes for grouping steps.
 */
public class ExtentTestManager {
    private static final ThreadLocal<ExtentTest> testThreadLocal = new ThreadLocal<>();

    /**
     * Create a new root test.
     * @param name the test name
     * @return the new ExtentTest
     */
    public static ExtentTest createTest(String name) {
        ExtentReports rep = ExtentManager.getInstance();
        ExtentTest test = rep.createTest(name);
        testThreadLocal.set(test);
        TestContextLogger.info("📝 Extent Test created: " + name);
        return test;
    }

    /**
     * Create a new root test and assign it one or more categories.
     * @param name       the test name
     * @param categories one or more category names (e.g. modules)
     * @return the new ExtentTest
     */
    public static ExtentTest createTest(String name, String... categories) {
        ExtentTest test = createTest(name);
        for (String cat : categories) {
            test.assignCategory(cat);
        }
        return test;
    }

    /**
     * Create a child node under the current test.
     * All logs sent to this node will appear as a collapsible section in the report.
     * @param name the node (group) name
     * @return the new child ExtentTest node
     */
    public static ExtentTest createNode(String name) {
        return getTest().createNode(name);
    }

    /**
     * Retrieve the current test or node for the calling thread.
     * @return the current ExtentTest
     * @throws IllegalStateException if no test has been created yet
     */
    public static ExtentTest getTest() {
        ExtentTest t = testThreadLocal.get();
        if (t == null) {
            throw new IllegalStateException("No ExtentTest found; call createTest() first");
        }
        return t;
    }

    /** Flush and write out all pending reports. */
    public static void flushReports() {
        ExtentManager.flushReports();
    }
}