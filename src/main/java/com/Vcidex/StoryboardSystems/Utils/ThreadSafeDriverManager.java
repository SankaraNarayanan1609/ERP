// ThreadSafeDriverManager.java
package com.Vcidex.StoryboardSystems.Utils;

import org.openqa.selenium.WebDriver;

public class ThreadSafeDriverManager {
    public static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    public static WebDriver driver() {
        return driver.get();
    }

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
