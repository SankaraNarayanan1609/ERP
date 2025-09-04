package com.Vcidex.StoryboardSystems.Utils.Logger;

import com.Vcidex.StoryboardSystems.Utils.TestContext;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ────────────────────────────────────────────────────────────────
 * ✅ ValidationLogger – for assertion + traceable validation logging
 * - Auto-incremented step numbers
 * - WebDriver-based screenshots on failure
 * - SLF4J + ExtentReports logging
 * - Per-thread failure collection
 * ────────────────────────────────────────────────────────────────
 */
public class ValidationLogger {

    private static final Logger log = LoggerFactory.getLogger(ValidationLogger.class);
    private static final AtomicInteger counter = new AtomicInteger(0);
    private static final ThreadLocal<List<String>> threadFailures = ThreadLocal.withInitial(ArrayList::new);

    /** Call at the start of every test */
    public static void reset() {
        counter.set(0);
        threadFailures.get().clear();
        log.info("🔄 ValidationLogger reset | Test='{}' | Thread={}", TestContext.getCurrentTestName(), Thread.currentThread().getId());
    }

    // ─────────────────────────────────────────────
    // Numeric + String Assertion (with ExtentTest)
    // ─────────────────────────────────────────────

    public static void assertEquals(String label, BigDecimal expected, BigDecimal actual, ExtentTest node) {
        int step = counter.incrementAndGet();
        if (expected.compareTo(actual) == 0) {
            logPass(node, step, label, "expected='" + expected + "'");
        } else {
            logFail(node, step, label, "expected='" + expected + "', actual='" + actual + "'");
        }
    }

    public static void assertEquals(String label, int expected, int actual, ExtentTest node) {
        int step = counter.incrementAndGet();
        if (expected == actual) {
            logPass(node, step, label, "expected='" + expected + "'");
        } else {
            logFail(node, step, label, "expected='" + expected + "', actual='" + actual + "'");
        }
    }

    public static void assertEquals(String label, String expected, String actual, ExtentTest node) {
        int step = counter.incrementAndGet();
        if (expected.equals(actual)) {
            logPass(node, step, label, "expected='" + expected + "'");
        } else {
            logFail(node, step, label, "expected='" + expected + "', actual='" + actual + "'");
        }
    }

    public static void assertTrue(String label, boolean condition, ExtentTest node) {
        int step = counter.incrementAndGet();
        if (condition) {
            logPass(node, step, label, "condition=true");
        } else {
            logFail(node, step, label, "condition=false");
        }
    }

    // ─────────────────────────────────────────────
    // WebDriver Screenshot Assertions
    // ─────────────────────────────────────────────

    public static void assertTrue(String label, boolean condition, WebDriver driver) {
        int step = counter.incrementAndGet();
        if (condition) {
            log.debug("[Step#{}] ✅ {} = true | session={}", step, label, getSessionId(driver));
        } else {
            String msg = "❌ " + label + " = false";
            log.error("[Step#{}] {}", step, msg);
            threadFailures.get().add(msg);
            DiagnosticsLogger.onFailure(driver, label, new RuntimeException(msg));
        }
    }

    // ─────────────────────────────────────────────
    // assertAll (Soft failure summary)
    // ─────────────────────────────────────────────

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

    // ─────────────────────────────────────────────
    // Helper Methods
    // ─────────────────────────────────────────────

    private static void logPass(ExtentTest node, int step, String label, String details) {
        String msg = "Step#" + step + " ✅ " + label + " (" + details + ")";
        log.debug(msg);
        node.pass(msg);
    }

    private static void logFail(ExtentTest node, int step, String label, String details) {
        String msg = "Step#" + step + " ❌ " + label + " (" + details + ")";
        log.error(msg);
        node.fail(msg);
        threadFailures.get().add(label + " mismatch → " + details);
    }

    private static String getSessionId(WebDriver driver) {
        if (driver instanceof RemoteWebDriver r) {
            try {
                return r.getSessionId().toString();
            } catch (Exception ignored) {}
        }
        return "n/a";
    }
}
