// ThreadSafeDriverManager.java
package com.Vcidex.StoryboardSystems.Utils;

import org.openqa.selenium.WebDriver;

/**
 * Solely responsible for holding/retrieving the thread-local WebDriver.
 */
public class ThreadSafeDriverManager {
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }
    public static void setDriver(WebDriver driver) {
        driverThreadLocal.set(driver);
    }
    public static void removeDriver() {
        driverThreadLocal.remove();
    }
}