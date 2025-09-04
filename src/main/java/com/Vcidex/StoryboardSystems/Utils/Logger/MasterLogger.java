package com.Vcidex.StoryboardSystems.Utils.Logger;

import com.Vcidex.StoryboardSystems.Utils.ThreadSafeDriverManager;
import com.aventstack.extentreports.ExtentTest;
import io.restassured.response.Response;
import org.slf4j.LoggerFactory;

public class MasterLogger {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(MasterLogger.class);

    public enum Layer { UI, API, DB, VALIDATION, ASSERT, WAIT }

    @FunctionalInterface
    public interface SupplierWithException<T> { T get() throws Exception; }

    // Generic step (returns result)
    public static <T> T step(Layer layer, String actionName, SupplierWithException<T> step) {
        return step(layer, actionName, ReportManager.getTest(), step);
    }

    public static <T> T step(Layer layer, String actionName, ExtentTest node, SupplierWithException<T> step) {
        try {
            T result = step.get();
            String rc = extractReturnCode(layer, result);
            if (node != null) node.pass("✅ " + actionName + (rc != null ? " (RC=" + rc + ")" : ""));
            return result;
        } catch (Exception e) {
            DiagnosticsLogger.onFailure(
                    ThreadSafeDriverManager.getDriver(),
                    actionName,
                    e
            );
            if (e instanceof RuntimeException re) throw re;
            throw new RuntimeException(e);
        }
    }

    // Step without return value
    public static void step(Layer layer, String actionName, Runnable step) {
        step(layer, actionName, ReportManager.getTest(), step);
    }

    public static void step(Layer layer, String actionName, ExtentTest node, Runnable step) {
        try {
            step.run();
            if (node != null) node.pass("✅ " + actionName);
        } catch (Exception e) {
            DiagnosticsLogger.onFailure(
                    ThreadSafeDriverManager.getDriver(),
                    actionName,
                    e
            );
            if (e instanceof RuntimeException re) throw re;
            throw new RuntimeException(e);
        }
    }

    // Step with performance timing
    public static void stepWithTimer(String key, Runnable step) {
        PerformanceLogger.start(key);
        try {
            step.run();
            ExtentTest t = ReportManager.getTest();
            if (t != null) t.pass("✅ " + key + " (timed)");
        } catch (Exception e) {
            DiagnosticsLogger.onFailure(
                    ThreadSafeDriverManager.getDriver(),
                    key,  // fixed: was an undefined variable
                    e
            );
            if (e instanceof RuntimeException re) throw re;
            throw new RuntimeException(e);
        } finally {
            PerformanceLogger.end(key);
        }
    }

    public static void group(String name, Runnable block) { ReportManager.group(name, block); }

    private static <T> String extractReturnCode(Layer layer, T result) {
        if (layer == Layer.API && result instanceof Response res) return String.valueOf(res.getStatusCode());
        return null;
    }

    @FunctionalInterface public interface StepBlock { void execute() throws Exception; }

    public static void step(String message, StepBlock action) {
        step(Layer.UI, message, ReportManager.getTest(), action);
    }

    public static void step(Layer layer, String message, StepBlock action) {
        step(layer, message, ReportManager.getTest(), action);
    }

    public static void step(Layer layer, String message, ExtentTest node, StepBlock action) {
        try {
            action.execute();
            if (node != null) node.pass("✅ " + message);
        } catch (Exception e) {
            DiagnosticsLogger.onFailure(
                    ThreadSafeDriverManager.getDriver(),
                    message, // fixed: use message here
                    e
            );
            if (e instanceof RuntimeException re) throw re;
            throw new RuntimeException(e);
        }
    }

    // Info/warn/error passthrough
    public static void info(String message) {
        log.info(message);
        try {
            ExtentTest test = ReportManager.getTest();
            if (test != null) test.info(message);
            else log.warn("⚠️ ExtentTest null, skipping: {}", message);
        } catch (Exception e) {
            log.warn("⚠️ Failed to log to ExtentReport: {}", e.getMessage());
        }
    }

    public static void warn(String message) {
        log.warn(message);
        try {
            ExtentTest test = ReportManager.getTest();
            if (test != null) test.warning(message);
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