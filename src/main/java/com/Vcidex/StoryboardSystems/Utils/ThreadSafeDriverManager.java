// ThreadSafeDriverManager.java
package com.Vcidex.StoryboardSystems.Utils;

import org.openqa.selenium.WebDriver;

public class ThreadSafeDriverManager {
    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    public static WebDriver getDriver() {
        return driver.get();
    }

    public static void setDriver(WebDriver driverInstance) {
        driver.set(driverInstance);
    }

    public static void removeDriver() {
        driver.remove();
    }
}
