package com.Vcidex.StoryboardSystems.Utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
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

    // ─── Global Browser Mode Settings ─────────────────────────────────────────────
    private static final boolean HEADLESS_MODE = false;
    private static final Duration IMPLICIT_WAIT = Duration.ofSeconds(10);
    private static final Duration PAGE_LOAD_TIMEOUT = Duration.ofSeconds(30);
    private static final Duration SCRIPT_TIMEOUT = Duration.ofSeconds(30);

    public enum BrowserType {
        CHROME, FIREFOX, EDGE
    }

    /**
     * Get WebDriver with default browser and default headless flag
     */
    public static WebDriver getDriver() {
        WebDriver driver = ThreadSafeDriverManager.getDriver();
        if (driver != null) return driver;

        return getDriver("chrome", HEADLESS_MODE); // Default
    }

    /**
     * Creates a WebDriver for given browser in headless/GUI mode
     */
    public static WebDriver getDriver(String browser, boolean headless) {
        WebDriver driver = ThreadSafeDriverManager.getDriver();
        if (driver != null) return driver;

        WebDriver raw = switch (BrowserType.valueOf(browser.toUpperCase())) {
            case FIREFOX -> { WebDriverManager.firefoxdriver().setup(); yield new FirefoxDriver(getFirefoxOptions(headless)); }
            case EDGE    -> { WebDriverManager.edgedriver().setup();  yield new EdgeDriver(getEdgeOptions(headless)); }
            default      -> { WebDriverManager.chromedriver().setup(); yield new ChromeDriver(getChromeOptions(headless)); }
        };

        // Decorate with UIEventListener (Selenium 4)
        var decorated = new org.openqa.selenium.support.events.EventFiringDecorator<>(new UIEventListener()).decorate(raw);

        decorated.manage().window().setPosition(new Point(0, 0));
        decorated.manage().window().setSize(new Dimension(1280, 800));

        // IMPORTANT: Explicit waits only
        decorated.manage().timeouts().implicitlyWait(Duration.ZERO);
        decorated.manage().timeouts().pageLoadTimeout(PAGE_LOAD_TIMEOUT);
        decorated.manage().timeouts().scriptTimeout(SCRIPT_TIMEOUT);

        // Optional: start CDP network logging (config-gated inside)
        com.Vcidex.StoryboardSystems.Utils.Logger.NetworkLogger.start(decorated);

        ThreadSafeDriverManager.setDriver(decorated);
        System.out.println("✅ WebDriver Initialized for " + browser.toUpperCase() + " | Headless: " + headless);
        return decorated;
    }

    public static void quitDriver() {
        WebDriver driver = ThreadSafeDriverManager.getDriver();
        if (driver != null) {
            try { com.Vcidex.StoryboardSystems.Utils.Logger.NetworkLogger.stop(); } catch (Exception ignore) {}
            driver.quit();
            ThreadSafeDriverManager.removeDriver();
            System.out.println("🔴 WebDriver Quit Successfully");
        }
    }

    // ─── Chrome Config ─────────────────────────────────────────────
    private static ChromeOptions getChromeOptions(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        if (headless || HEADLESS_MODE) {
            options.addArguments("--headless=new", "--disable-gpu");
        }

        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.BROWSER, Level.ALL);
        options.setCapability("goog:loggingPrefs", logPrefs);

        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("--disable-blink-features=AutomationControlled");

        return options;
    }

    // ─── Edge Config ───────────────────────────────────────────────
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

    // ─── Firefox Config ─────────────────────────────────────────────
    private static FirefoxOptions getFirefoxOptions(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        if (headless || HEADLESS_MODE) {
            options.addArguments("--headless");
        }

        System.setProperty("webdriver.firefox.logfile", "./browser-logs/firefox.log");
        return options;
    }
}