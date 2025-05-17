package com.Vcidex.StoryboardSystems.Utils.Logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.Capabilities;

import java.util.UUID;

public class TestContextLogger {
    private static final Logger logger = LoggerFactory.getLogger(TestContextLogger.class);
    private static final String TEST_RUN_ID = UUID.randomUUID().toString();

    /** Call at @BeforeMethod or via listener on test start. */
    public static void logTestStart(String testName, WebDriver driver) {
        logger.info("=== TEST START: {} ===", testName);
        logger.info("TestRun ID: {}", TEST_RUN_ID);
        logger.info("Thread ID: {}", Thread.currentThread().getId());
        logger.info("Environment: {} | BaseURL: {}",
                System.getProperty("env.name","unknown"),
                System.getProperty("base.url","unknown")
        );
        logger.info("Java Version: {}", System.getProperty("java.version"));

        if (driver instanceof RemoteWebDriver) {
            Capabilities caps = ((RemoteWebDriver) driver).getCapabilities();
            logger.info("Browser: {} {}", caps.getBrowserName(), caps.getBrowserVersion());
            try {
                String session = ((RemoteWebDriver) driver).getSessionId().toString();
                logger.info("Session ID: {}", session);
            } catch (Exception ignore) {}
        }

        logger.info("OS: {} {}", System.getProperty("os.name"), System.getProperty("os.version"));
    }

    /** Call at @AfterMethod or via listener on test end. */
    public static void logTestEnd(String testName) {
        // Summarize any performance metrics if you wish:
        PerformanceLogger.printSummary();
        logger.info("=== TEST END: {} ===", testName);
    }

    /** General-purpose INFO-level logging. */
    public static void info(String message) {
        logger.info(message);
    }
}