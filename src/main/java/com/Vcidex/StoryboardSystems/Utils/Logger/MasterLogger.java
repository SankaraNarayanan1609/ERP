package com.Vcidex.StoryboardSystems.Utils.Logger;

import com.aventstack.extentreports.ExtentTest;
import io.restassured.response.Response;

public class MasterLogger {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MasterLogger.class);

    public enum Layer { UI, API, DB, VALIDATION, ASSERT, WAIT }

    @FunctionalInterface
    public interface SupplierWithException<T> {
        T get() throws Exception;
    }

    // ─── Functional step with result and default node ────────────────────────────
    public static <T> T step(Layer layer, String actionName, SupplierWithException<T> step) {
        return step(layer, actionName, ReportManager.getTest(), step);
    }

    // ─── Functional step with result and custom node ─────────────────────────────
    public static <T> T step(
            Layer layer,
            String actionName,
            ExtentTest node,
            SupplierWithException<T> step
    ) {
        try {
            T result = step.get();
            String rc = extractReturnCode(layer, result);
            node.pass("✅ " + actionName + (rc != null ? " (RC=" + rc + ")" : ""));
            return result;
        } catch (Exception e) {
            String rc = determineCode(layer, e);
            node.fail("❌ " + actionName + " → " + rc + " – " + e.getMessage());
            DiagnosticsLogger.onFailure(ReportManager.getDriver(), actionName);
            throw new RuntimeException(e);
        }
    }

    // ─── Void step with Runnable and default node ────────────────────────────────
    public static void step(Layer layer, String actionName, Runnable step) {
        step(layer, actionName, ReportManager.getTest(), step);
    }

    // ─── Void step with Runnable and custom node ─────────────────────────────────
    public static void step(
            Layer layer,
            String actionName,
            ExtentTest node,
            Runnable step
    ) {
        try {
            step.run();
            node.pass("✅ " + actionName);
        } catch (Exception e) {
            String rc = determineCode(layer, e);
            node.fail("❌ " + actionName + " → " + rc + " – " + e.getMessage());
            DiagnosticsLogger.onFailure(ReportManager.getDriver(), actionName);
            throw new RuntimeException(e);
        }
    }

    // ─── Timed step helper ───────────────────────────────────────────────────────
    public static void stepWithTimer(String key, Runnable step) {
        PerformanceLogger.start(key);
        try {
            step.run();
            ReportManager.getTest().pass("✅ " + key + " (timed)");
        } catch (Exception e) {
            ReportManager.getTest().fail("❌ " + key + ": " + e.getMessage());
            throw e;
        } finally {
            PerformanceLogger.end(key);
        }
    }

    // ─── Grouped node helper ────────────────────────────────────────────────────
    public static void group(String groupName, Runnable body) {
        ExtentTest parent = ReportManager.getTest();
        ExtentTest child  = parent.createNode(groupName);
        ReportManager.setTest(child);
        try {
            body.run();
            child.pass("✅ " + groupName);
        } catch (Exception e) {
            child.fail("❌ " + groupName + " → " + e.getClass().getSimpleName() + ": " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            ReportManager.setTest(parent);
        }
    }

    // ─── Return‐code extraction for API layer ───────────────────────────────────
    private static <T> String extractReturnCode(Layer layer, T result) {
        if (layer == Layer.API && result instanceof Response) {
            return String.valueOf(((Response) result).getStatusCode());
        }
        return null;
    }

    // ─── Error code determination for FAIL paths ────────────────────────────────
    private static String determineCode(Layer layer, Exception e) {
        return switch (layer) {
            case VALIDATION, ASSERT -> e.getMessage();
            default              -> e.getClass().getSimpleName();
        };
    }

    // ─── StepBlock + helpers ────────────────────────────────────────────────────

    @FunctionalInterface
    public interface StepBlock {
        Void execute() throws Exception;
    }

    /**
     * Void‐returning step helper (uses UI layer and default test node).
     */
    public static void step(String message, StepBlock action) {
        step(Layer.UI, message, ReportManager.getTest(), action);
    }

    /**
     * Missing overload: Void‐returning step with Layer + default node.
     */
    public static void step(Layer layer, String message, StepBlock action) {
        step(layer, message, ReportManager.getTest(), action);
    }

    /**
     * Void‐returning step with custom node.
     */
    public static void step(
            Layer layer,
            String message,
            ExtentTest node,
            StepBlock action
    ) {
        try {
            action.execute();
            node.pass("✅ " + message);
        } catch (Exception e) {
            String rc = determineCode(layer, e);
            node.fail("❌ " + message + " → " + rc + " – " + e.getMessage());
            DiagnosticsLogger.onFailure(ReportManager.getDriver(), message);
            throw new RuntimeException(e);
        }
    }

    /**
     * Wraps a Runnable into a StepBlock.
     */
    public static StepBlock wrap(Runnable r) {
        return () -> {
            r.run();
            return null;
        };
    }

    // ─── Info / Warn / Error log pass-throughs to ExtentReport ─────────────────────

    public static void info(String message) {
        log.info(message);
        try {
            ExtentTest test = ReportManager.getTest();
            if (test != null) test.info(message);
        } catch (Exception ignored) {}
    }

    public static void warn(String message) {
        log.warn(message);
        try {
            ExtentTest test = ReportManager.getTest();
            if (test != null) test.warning(message); // This ensures warning shows in HTML report
        } catch (Exception ignored) {}
    }

    public static void error(String message) {
        log.error(message);
        try {
            ExtentTest test = ReportManager.getTest();
            if (test != null) test.fail("❌ " + message);
        } catch (Exception ignored) {}
    }
}