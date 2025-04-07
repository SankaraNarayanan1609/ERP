package com.Vcidex.StoryboardSystems.Utils;

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
import java.time.Duration;
import java.util.logging.Level;

public class WebDriverFactory {

    private static final boolean HEADLESS_MODE = false; // Toggle headless mode

    public enum BrowserType {
        CHROME, FIREFOX, EDGE
    }

    public static WebDriver getDriver() {
        // Return default WebDriver instance if context allows
        return ThreadSafeDriverManager.getDriver(); //Cannot resolve symbol 'driverThreadLocal'
    }


    // ✅ Initialize Driver Based on Browser Type
    public static WebDriver getDriver(String browser, boolean headless) {
        WebDriver driver = ThreadSafeDriverManager.getDriver();
        if (driver != null) {
            return driver; // Return existing driver if already initialized
        }

        switch (BrowserType.valueOf(browser.toUpperCase())) {
            case FIREFOX:
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver(getFirefoxOptions(headless));
                break;
            case EDGE:
                WebDriverManager.edgedriver().setup();
                driver = new EdgeDriver(getEdgeOptions(headless));
                break;
            case CHROME:
            default:
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver(getChromeOptions(headless));
        }

        // Set driver and configurations
        ThreadSafeDriverManager.setDriver(driver);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));

        System.out.println("✅ WebDriver Initialized for browser: " + browser);
        return driver;
    }

    public static void quitDriver() {
        WebDriver driver = ThreadSafeDriverManager.getDriver();
        if (driver != null) {
            driver.quit();
            ThreadSafeDriverManager.removeDriver();
            System.out.println("🔴 WebDriver Quit Successfully");
        }
    }

    // ✅ Configure Chrome Options with Logging
    private static ChromeOptions getChromeOptions(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        if (headless || HEADLESS_MODE) {
            options.addArguments("--headless=new", "--disable-gpu");
        }

        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.BROWSER, Level.ALL);
        options.setCapability("goog:loggingPrefs", logPrefs);

        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        return options;
    }

    // ✅ Configure Edge Options with Logging
    private static EdgeOptions getEdgeOptions(boolean headless) {
        EdgeOptions options = new EdgeOptions();
        if (headless || HEADLESS_MODE) {
            options.addArguments("--headless=new", "--disable-gpu");
        }

        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.BROWSER, Level.ALL);
        options.setCapability("goog:loggingPrefs", logPrefs);

        return options;
    }

    // ✅ Configure Firefox Options with Logging
    private static FirefoxOptions getFirefoxOptions(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        if (headless || HEADLESS_MODE) {
            options.addArguments("--headless");
        }

        System.setProperty("webdriver.firefox.logfile", "./browser-logs/firefox.log");
        return options;
    }
}