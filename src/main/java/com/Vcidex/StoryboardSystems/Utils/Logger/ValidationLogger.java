package com.Vcidex.StoryboardSystems.Utils.Logger;

import com.Vcidex.StoryboardSystems.Utils.TestContext;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Centralized validation logger for assertions.
 * Tracks step counts per test, auto-injects test/method names,
 * records driver session ID, delegates screenshots on failure,
 * and now supports grouping under any ExtentTest node.
 */
public class ValidationLogger {
    private static final Logger logger = LoggerFactory.getLogger(ValidationLogger.class);
    private static final AtomicInteger counter = new AtomicInteger(0);

    /** Reset step counter at test start. */
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

    //
    // ─── EXISTING DRIVER-BASED API ────────────────────────────────────────────────
    //

    public static void assertEquals(String label, String expected, String actual, WebDriver driver) {
        int step       = counter.incrementAndGet();
        String test    = TestContext.getCurrentTestName();
        String method  = getCallingMethod();
        String session = getSessionId(driver);

        if (expected.equals(actual)) {
            logger.debug(
                    "[{}.{}] Step#{} PASS ✅ {} (expected='{}') session={}",
                    test, method, step, label, expected, session
            );
        } else {
            logger.error(
                    "[{}.{}] Step#{} FAIL ❌ {} (expected='{}', actual='{}') session={}",
                    test, method, step, label, expected, actual, session
            );
            ErrorLogger.logException(
                    new Exception(label + ": expected '" + expected + "' but got '" + actual + "'"),
                    test + ":" + label,
                    driver
            );
        }
    }

    public static void assertTrue(String label, boolean condition, WebDriver driver) {
        int step       = counter.incrementAndGet();
        String test    = TestContext.getCurrentTestName();
        String method  = getCallingMethod();
        String session = getSessionId(driver);

        if (condition) {
            logger.debug(
                    "[{}.{}] Step#{} PASS ✅ {} session={}",
                    test, method, step, label, session
            );
        } else {
            logger.error(
                    "[{}.{}] Step#{} FAIL ❌ {} session={}",
                    test, method, step, label, session
            );
            ErrorLogger.logException(
                    new Exception(label + " was false"),
                    test + ":" + label,
                    driver
            );
        }
    }

    //
    // ─── NEW OVERLOADS FOR GROUPED NODES ──────────────────────────────────────────
    //

    /**
     * Assert equals inside a specific ExtentTest node, for grouping under your custom sections.
     */
    public static void assertEquals(String label, String expected, String actual, ExtentTest node) {
        int step      = counter.incrementAndGet();
        String test   = TestContext.getCurrentTestName();
        String method = getCallingMethod();

        if (expected.equals(actual)) {
            logger.debug(
                    "[{}.{}] Step#{} PASS ✅ {} (expected='{}')",
                    test, method, step, label, expected
            );
            node.pass("Step#" + step + " ✅ " + label + " (expected='" + expected + "')");
        } else {
            logger.error(
                    "[{}.{}] Step#{} FAIL ❌ {} (expected='{}', actual='{}')",
                    test, method, step, label, expected, actual
            );
            node.fail("Step#" + step + " ❌ " + label +
                    " (expected='" + expected + "', actual='" + actual + "')");
        }
    }

    /**
     * Assert true inside a specific ExtentTest node.
     */
    public static void assertTrue(String label, boolean condition, ExtentTest node) {
        int step      = counter.incrementAndGet();
        String test   = TestContext.getCurrentTestName();
        String method = getCallingMethod();

        if (condition) {
            logger.debug(
                    "[{}.{}] Step#{} PASS ✅ {}",
                    test, method, step, label
            );
            node.pass("Step#" + step + " ✅ " + label + " (condition is true)");
        } else {
            logger.error(
                    "[{}.{}] Step#{} FAIL ❌ {}",
                    test, method, step, label
            );
            node.fail("Step#" + step + " ❌ " + label + " (condition is false)");
        }
    }

    // ──────────────────────────── Internals ────────────────────────────

    /** Get the name of the method that called into this logger. */
    private static String getCallingMethod() {
        return Thread.currentThread().getStackTrace()[3].getMethodName();
    }

    /** Fetch the Selenium session ID if available (for driver‐based logs). */
    private static String getSessionId(WebDriver driver) {
        if (driver instanceof RemoteWebDriver) {
            try {
                return ((RemoteWebDriver) driver).getSessionId().toString();
            } catch (Exception ignored) { }
        }
        return "n/a";
    }
}