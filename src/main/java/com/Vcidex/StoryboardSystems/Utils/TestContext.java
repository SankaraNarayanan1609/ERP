package com.Vcidex.StoryboardSystems.Utils;

import org.testng.ITestResult;

public class TestContext {
    private static final ThreadLocal<String> currentTest = new ThreadLocal<>();

    public static void setCurrentTestName(ITestResult result) {
        currentTest.set(result.getMethod().getMethodName());
    }

    public static String getCurrentTestName() {
        return currentTest.get();
    }

    public static void clear() {
        currentTest.remove();
    }
}