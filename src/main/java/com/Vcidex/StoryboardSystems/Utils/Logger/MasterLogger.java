// File: MasterLogger.java
package com.Vcidex.StoryboardSystems.Utils.Logger;

import com.aventstack.extentreports.ExtentTest;
import io.restassured.response.Response;

public class MasterLogger {

    public enum Layer { UI, API, DB, VALIDATION, ASSERT, WAIT }

    @FunctionalInterface
    public interface SupplierWithException<T> {
        T get() throws Exception;
    }

    /**
     * Functional step logger with result (e.g., API call, get text).
     */
    public static <T> T step(Layer layer, String actionName, SupplierWithException<T> step) {
        return step(layer, actionName, ReportManager.getTest(), step);
    }

    /**
     * Functional step logger with custom ExtentTest node.
     */
    public static <T> T step(Layer layer, String actionName, ExtentTest node, SupplierWithException<T> step) {
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

    /**
     * Void step logger (UI click, assert, etc.).
     */
    public static void step(Layer layer, String actionName, Runnable step) {
        step(layer, actionName, ReportManager.getTest(), step);
    }

    /**
     * Void step logger with custom ExtentTest.
     */
    public static void step(Layer layer, String actionName, ExtentTest node, Runnable step) {
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

    /**
     * Creates a grouped node in ExtentReports and executes the block inside it.
     */
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

    private static <T> String extractReturnCode(Layer layer, T result) {
        if (layer == Layer.API && result instanceof Response) {
            return String.valueOf(((Response) result).getStatusCode());
        }
        return null;
    }

    private static String determineCode(Layer layer, Exception e) {
        return switch (layer) {
            case VALIDATION, ASSERT -> e.getMessage();
            default -> e.getClass().getSimpleName();
        };
    }

    @FunctionalInterface
    public interface StepBlock {
        Void execute() throws Exception;
    }

    public static void step(String message, StepBlock action) {
        step(Layer.UI, message, ReportManager.getTest(), action);
    }

    public static void step(Layer layer, String message, ExtentTest node, StepBlock action) {
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

    public static StepBlock wrap(Runnable r) {
        return () -> {
            r.run();
            return null;
        };
    }
}