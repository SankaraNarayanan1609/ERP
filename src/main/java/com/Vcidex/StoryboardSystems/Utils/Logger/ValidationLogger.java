package com.Vcidex.StoryboardSystems.Utils.Logger;

import com.Vcidex.StoryboardSystems.Utils.TestContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Centralized validation logger for assertions.
 * Tracks step counts per test, auto-injects test/method names,
 * records driver session ID, and delegates screenshots on failure.
 */
public class ValidationLogger {
    private static final Logger logger = LoggerFactory.getLogger(ValidationLogger.class);
    private static final AtomicInteger counter = new AtomicInteger(0);

    public static void reset() {
        counter.set(0);
        logger.info(
                "🔄 Reset ValidationLogger for test='{}' | JVM user='{}' | Java='{}' | OS='{}' | Thread={}",
                TestContext.getCurrentTestName(),
                System.getProperty("user.name"),
                System.getProperty("java.version"),
                System.getProperty("os.name") + " " + System.getProperty("os.version"),
                Thread.currentThread().getId()
        );
    }

    public static void assertEquals(String label, String expected, String actual, WebDriver driver) {
        int step = counter.incrementAndGet();
        String testName = TestContext.getCurrentTestName();
        String method  = getCallingMethod();
        String session = getSessionId(driver);

        if (expected.equals(actual)) {
            logger.debug(
                    "[{}.{}] Step#{} PASS ✅ {} (expected='{}') session={}",
                    testName, method, step, label, expected, session
            );
        } else {
            logger.error(
                    "[{}.{}] Step#{} FAIL ❌ {} (expected='{}', actual='{}') session={}",
                    testName, method, step, label, expected, actual, session
            );
            ErrorLogger.logException(
                    new Exception(label + ": expected '" + expected + "' but got '" + actual + "'"),
                    testName + ":" + label,
                    driver
            );
        }
    }

    public static void assertTrue(String label, boolean condition, WebDriver driver) {
        int step = counter.incrementAndGet();
        String testName = TestContext.getCurrentTestName();
        String method  = getCallingMethod();
        String session = getSessionId(driver);

        if (condition) {
            logger.debug(
                    "[{}.{}] Step#{} PASS ✅ {} session={}",
                    testName, method, step, label, session
            );
        } else {
            logger.error(
                    "[{}.{}] Step#{} FAIL ❌ {} session={}",
                    testName, method, step, label, session
            );
            ErrorLogger.logException(
                    new Exception(label + " was false"),
                    testName + ":" + label,
                    driver
            );
        }
    }

    // ──────────────────────────── Internals ────────────────────────────

    private static String getCallingMethod() {
        return Thread.currentThread().getStackTrace()[3].getMethodName();
    }

    private static String getSessionId(WebDriver driver) {
        if (driver instanceof RemoteWebDriver) {
            try {
                return ((RemoteWebDriver) driver).getSessionId().toString();
            } catch (Exception ignored) { }
        }
        return "n/a";
    }
}