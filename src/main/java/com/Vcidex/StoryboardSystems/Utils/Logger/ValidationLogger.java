// File: ValidationLogger.java

/**
 * ─────────────────────────────────────────────────────────────────────────────
 * ValidationLogger.java
 *
 * ✅ Purpose:
 *   Centralized utility to log validation/assertion steps in tests.
 *
 * 🔍 Features:
 *   - Auto-increments validation step count
 *   - Prints readable PASS/FAIL logs for every validation
 *   - Logs to ExtentReport (via node) and SLF4J logger
 *   - Captures screenshots on failure via ErrorLogger
 *   - Tracks session ID, method, and test for traceability
 *
 * 🔄 Usage:
 *   - Use assertEquals() and assertTrue() with WebDriver or ExtentTest
 *   - Step counter resets at the start of each test
 *
 * ❌ Does not support soft assertions (by user decision)
 *
 * 🧠 Designed to support both Web UI and group-based reporting
 * ─────────────────────────────────────────────────────────────────────────────
 */
package com.Vcidex.StoryboardSystems.Utils.Logger;

import com.Vcidex.StoryboardSystems.Utils.TestContext;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;

public class ValidationLogger {

    private static final Logger logger = LoggerFactory.getLogger(ValidationLogger.class);
    private static final AtomicInteger counter = new AtomicInteger(0);

    // Thread-safe failure collector for soft assertions
    private static final ThreadLocal<List<String>> threadFailures =
            ThreadLocal.withInitial(ArrayList::new);

    // ─────────────────────────────────────────────────────────────────────────────

    public static void reset() {
        counter.set(0);
        threadFailures.get().clear();
        logger.info(
                "🔄 Reset ValidationLogger for test='{}' | JVM user='{}' | Java='{}' | OS='{}' | Thread={}",
                TestContext.getCurrentTestName(),
                System.getProperty("user.name"),
                System.getProperty("java.version"),
                System.getProperty("os.name") + " " + System.getProperty("os.version"),
                Thread.currentThread().getId()
        );
    }

    // ────────────────────────────────────────────────
    // Assertions with WebDriver – takes screenshot
    // ────────────────────────────────────────────────

    public static void assertEquals(String label, String expected, String actual, WebDriver driver) {
        int step = counter.incrementAndGet();
        String test = TestContext.getCurrentTestName();
        String method = getCallingMethod();
        String session = getSessionId(driver);

        if (expected.equals(actual)) {
            logger.debug("[{}.{}] Step#{} PASS ✅ {} (expected='{}') session={}",
                    test, method, step, label, expected, session);
        } else {
            String failMsg = label + ": expected='" + expected + "', actual='" + actual + "'";
            logger.error("[{}.{}] Step#{} FAIL ❌ {} session={}", test, method, step, failMsg, session);
            threadFailures.get().add(failMsg);
            ErrorLogger.logException(new Exception(failMsg), test + ":" + label, driver);
        }
    }

    public static void assertTrue(String label, boolean condition, WebDriver driver) {
        int step = counter.incrementAndGet();
        String test = TestContext.getCurrentTestName();
        String method = getCallingMethod();
        String session = getSessionId(driver);

        if (condition) {
            logger.debug("[{}.{}] Step#{} PASS ✅ {} session={}", test, method, step, label, session);
        } else {
            String failMsg = label + " was false";
            logger.error("[{}.{}] Step#{} FAIL ❌ {} session={}", test, method, step, failMsg, session);
            threadFailures.get().add(failMsg);
            ErrorLogger.logException(new Exception(failMsg), test + ":" + label, driver);
        }
    }

    // ────────────────────────────────────────────────
    // Assertions with ExtentTest – for grouped logs
    // ────────────────────────────────────────────────

    public static void assertEquals(String label, String expected, String actual, ExtentTest node) {
        int step = counter.incrementAndGet();
        String test = TestContext.getCurrentTestName();
        String method = getCallingMethod();

        if (expected.equals(actual)) {
            logger.debug("[{}.{}] Step#{} PASS ✅ {} (expected='{}')", test, method, step, label, expected);
            node.pass("Step#" + step + " ✅ " + label + " (expected='" + expected + "')");
        } else {
            String failMsg = label + ": expected='" + expected + "', actual='" + actual + "'";
            logger.error("[{}.{}] Step#{} FAIL ❌ {}", test, method, step, failMsg);
            node.fail("Step#" + step + " ❌ " + failMsg);
            threadFailures.get().add(failMsg);
        }
    }

    public static void assertTrue(String label, boolean condition, ExtentTest node) {
        int step = counter.incrementAndGet();
        String test = TestContext.getCurrentTestName();
        String method = getCallingMethod();

        if (condition) {
            logger.debug("[{}.{}] Step#{} PASS ✅ {}", test, method, step, label);
            node.pass("Step#" + step + " ✅ " + label + " (condition is true)");
        } else {
            String failMsg = label + " condition was false";
            logger.error("[{}.{}] Step#{} FAIL ❌ {}", test, method, step, failMsg);
            node.fail("Step#" + step + " ❌ " + failMsg);
            threadFailures.get().add(failMsg);
        }
    }

    // ────────────────────────────────────────────────
    // Soft assertion summary (call once at end)
    // ────────────────────────────────────────────────

    public static void assertAll(String context) {
        List<String> failures = threadFailures.get();
        if (!failures.isEmpty()) {
            String summary = String.join("\n", failures);
            ReportManager.getTest().fail("❌ Validation failed in [" + context + "]:\n" + summary);
            threadFailures.get().clear();
            throw new AssertionError("Validation failed in [" + context + "]:\n" + summary);
        } else {
            ReportManager.getTest().pass("✅ All validations passed in [" + context + "]");
        }
    }

    // ────────────────────────────────────────────────
    // Internal helpers
    // ────────────────────────────────────────────────

    private static String getCallingMethod() {
        return Thread.currentThread().getStackTrace()[3].getMethodName();
    }

    private static String getSessionId(WebDriver driver) {
        if (driver instanceof RemoteWebDriver) {
            try {
                return ((RemoteWebDriver) driver).getSessionId().toString();
            } catch (Exception ignored) {
            }
        }
        return "n/a";
    }
}