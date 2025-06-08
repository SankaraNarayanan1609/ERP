// LifecycleLogger.java
package com.Vcidex.StoryboardSystems.Utils.Logger;

import org.openqa.selenium.WebDriver;

public class LifecycleLogger {
    /** Call in your @BeforeMethod or TestNG listener. */
    public static void onTestStart(String name, WebDriver driver) {
        TestContextLogger.info("=== TEST START: " + name + " ===");
        // (other environment info here if needed…)
    }

    /** Call in your @AfterMethod or TestNG listener. */
    public static void onTestFinish(String name) {
        PerformanceLogger.printSummary();
        TestContextLogger.info("=== TEST END: " + name + " ===");
    }
}