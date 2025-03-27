package com.Vcidex.StoryboardSystems.Common.Base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;

import java.util.logging.Level;

public class DriverManager {

    private static final boolean HEADLESS_MODE = false; // Toggle headless mode

    // ✅ Configure Chrome Options with Logging
    public static ChromeOptions getChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        if (HEADLESS_MODE) {
            options.addArguments("--headless=new", "--disable-gpu");
        }

        // Enable browser logs
        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.BROWSER, Level.ALL);
        options.setCapability("goog:loggingPrefs", logPrefs);

        return options;
    }

    // ✅ Configure Edge Options with Logging
    public static EdgeOptions getEdgeOptions() {
        EdgeOptions options = new EdgeOptions();
        if (HEADLESS_MODE) {
            options.addArguments("--headless=new", "--disable-gpu");
        }

        // Enable browser logs
        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.BROWSER, Level.ALL);
        options.setCapability("goog:loggingPrefs", logPrefs);

        return options;
    }

    // ✅ Configure Firefox Options with Logging
    // ✅ Configure Firefox Options with Logging
    public static FirefoxOptions getFirefoxOptions() {
        FirefoxOptions options = new FirefoxOptions();
        if (HEADLESS_MODE) {
            options.addArguments("--headless");
        }

        // Enable detailed logs by setting system property
        System.setProperty("webdriver.firefox.logfile", "./browser-logs/firefox.log");

        return options;
    }

    // ✅ Initialize Driver Based on Browser Type
    public enum BrowserType {
        CHROME, FIREFOX, EDGE
    }

    public static WebDriver initializeDriver(BrowserType browserType) {
        switch (browserType) {
            case CHROME:
                WebDriverManager.chromedriver().setup();
                return new ChromeDriver(getChromeOptions());
            case EDGE:
                WebDriverManager.edgedriver().setup();
                return new EdgeDriver(getEdgeOptions());
            case FIREFOX:
                WebDriverManager.firefoxdriver().setup();
                return new FirefoxDriver(getFirefoxOptions());
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browserType);
        }
    }
}