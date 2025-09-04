package com.Vcidex.StoryboardSystems.Utils.Logger;

import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.*;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.testng.ITestResult;
import org.testng.Reporter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

public class DiagnosticsLogger {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DiagnosticsLogger.class);

    public static void onFailure(WebDriver driver, String context, Throwable error) {
        ExtentTest test = ReportManager.getTest();

        Throwable root = getRootCause(error);
        String rootMessage = getRootCauseMessage(root);
        String xpath = extractXPathFromMessage(error.getMessage());
        String action = guessUiActionFromContext(context);
        String page = inferPageFromContext(context);
        String retryInfo = getRetryAttemptInfo();
        String currentUrl = safeGet(() -> driver != null ? driver.getCurrentUrl() : null, "Unavailable");
        String thread = Thread.currentThread().getName();

        StringBuilder sb = new StringBuilder();
        sb.append("❌ Step Failed: ").append(context).append("<br>");
        sb.append("🔸 <b>UI Action:</b> ").append(action).append("<br>");
        if (xpath != null) sb.append("🔹 <b>Target Element:</b> ").append(xpath).append("<br>");
        sb.append("🕓 <b>Error:</b> ").append(error.getClass().getSimpleName()).append("<br>");
        sb.append("📍 <b>Page:</b> ").append(page).append("<br>");
        sb.append("🔁 <b>").append(retryInfo).append("</b><br>");
        sb.append("🌐 <b>Current URL:</b> ").append(currentUrl).append("<br>");
        sb.append("🧵 <b>Thread:</b> ").append(thread).append("<br>");
        sb.append("⚠️ <b>Probable Cause:</b> ").append(inferCause(error)).append("<br>");
        sb.append("🧪 <b>Suggestions:</b><br>").append(inferSuggestions(error));

        // Header
        if (test != null) test.fail(sb.toString());

        // Screenshot (base64 keeps the report portable)
        try {
            if (driver != null && test != null) {
                String b64 = ScreenshotHelper.captureBase64(driver);
                test.addScreenCaptureFromBase64String(b64, "📸 " + context);
            }
        } catch (Exception ignore){}

        // Root cause
        if (rootMessage.contains("\n") || rootMessage.contains("at ")) {
            log.warn("⚠️ Root cause contains raw stacktrace-like content: {}", rootMessage);
        }
        if (test != null) test.fail("🧩 <b>Root Cause:</b><br>" + formatHtmlSafe(rootMessage));

        // Smart stack
        String smartTrace = getSmartStackTrace(error);
        if (!smartTrace.isBlank() && test != null) {
            test.fail("🧱 <b>Stack Summary:</b><br>" + formatHtmlSafe(smartTrace));
        }

        // Browser console (if available)
        try {
            if (driver != null && test != null) {
                List<LogEntry> logs = driver.manage().logs().get(LogType.BROWSER).getAll();
                if (!logs.isEmpty()) {
                    StringBuilder c = new StringBuilder("<details><summary>Browser Console</summary><pre>");
                    for (LogEntry le : logs) {
                        c.append(le.getLevel()).append(" ").append(le.getMessage()).append("\n");
                    }
                    c.append("</pre></details>");
                    test.info(c.toString());
                }
            }
        } catch (Throwable ignored) {}

        log.error("❌ Step Failed: {} | Root cause: {}", context, rootMessage);
    }

    private static String extractXPathFromMessage(String msg) {
        if (msg == null) return null;
        int start = msg.indexOf("By.xpath: ");
        if (start == -1) return null;
        return msg.substring(start + 10).split("\n")[0].trim();
    }

    private static String guessUiActionFromContext(String context) {
        String lower = context.toLowerCase();
        if (lower.contains("click")) return "Click";
        if (lower.contains("type")) return "Type";
        if (lower.contains("select")) return "Select from Dropdown";
        if (lower.contains("hover")) return "Mouse Hover";
        if (lower.contains("upload")) return "File Upload";
        return "UI Interaction";
    }

    private static String inferPageFromContext(String context) {
        String lower = context.toLowerCase();
        if (lower.contains("direct po")) return "Purchase → Purchase Order → Direct PO Modal";
        if (lower.contains("po")) return "Purchase → Purchase Order";
        if (lower.contains("invoice")) return "Purchase → Invoice → Direct Invoice";
        if (lower.contains("payment")) return "Finance → Payment";
        if (lower.contains("grn") || lower.contains("material inward")) return "Inventory → Material Inward";
        if (lower.contains("login")) return "Login Page";
        if (lower.contains("dashboard")) return "Dashboard";
        if (lower.contains("vendor") || lower.contains("master")) return "Master → Vendor";
        if (lower.contains("product")) return "Master → Product";
        return "Unknown Page";
    }

    private static String getRootCauseMessage(Throwable ex) {
        if (ex == null || ex.getMessage() == null) return "No message available.";
        return ex.getMessage().split("\n")[0].trim();
    }

    private static Throwable getRootCause(Throwable throwable) {
        Throwable cause = throwable;
        while (cause != null && cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }
        return cause;
    }

    private static String getSmartStackTrace(Throwable ex) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        String[] lines = sw.toString().split("\n");

        StringBuilder result = new StringBuilder();
        int added = 0;

        for (String line : lines) {
            line = line.trim();
            if (line.contains("Expected condition failed") || line.contains("waiting for")) {
                result.append("🧪 ").append(line).append("<br>");
                added++;
            } else if (line.contains("By.") || line.contains("xpath") || line.contains("cssSelector")) {
                result.append("📍 ").append(line).append("<br>");
                added++;
            } else if (line.toLowerCase().contains("timed out")) {
                result.append("⏳ ").append(line).append("<br>");
                added++;
            }
            if (added >= 5) break;
        }
        return (added == 0) ? "" : result.toString().trim();
    }

    private static String inferCause(Throwable e) {
        if (e instanceof TimeoutException)
            return "Element not rendered in DOM or overlay blocking interaction.";
        if (e instanceof ElementNotInteractableException)
            return "Element is present but not interactable (maybe hidden or disabled).";
        return "Unknown – check DOM, visibility, overlay or timing issues.";
    }

    private static String inferSuggestions(Throwable e) {
        StringBuilder suggestions = new StringBuilder();
        if (e instanceof TimeoutException || e instanceof ElementNotInteractableException) {
            suggestions.append("<ul>");
            suggestions.append("<li>Ensure element is visible and not inside a hidden container</li>");
            suggestions.append("<li>Wait for overlays/animations to finish</li>");
            suggestions.append("<li>Scroll into view if element is off-screen</li>");
            suggestions.append("</ul>");
        } else {
            suggestions.append("<ul>");
            suggestions.append("<li>Debug DOM structure</li>");
            suggestions.append("<li>Recheck locator or increase wait time</li>");
            suggestions.append("</ul>");
        }
        return suggestions.toString();
    }

    private static String getRetryAttemptInfo() {
        try {
            ITestResult result = Reporter.getCurrentTestResult();
            int current = (result != null && result.getAttribute("retryCount") instanceof Integer rc) ? rc + 1 : 1;
            int max = RetryAnalyzer.getMaxRetry() + 1;
            return "Retry Attempt: " + current + " of " + max;
        } catch (Exception ignored) {
            return "Retry Attempt: 1 of 1";
        }
    }

    @FunctionalInterface
    private interface SupplierWithException<T> { T get() throws Exception; }

    private static String safeGet(SupplierWithException<String> action, String fallback) {
        try { String v = action.get(); return v == null ? fallback : v; }
        catch (Exception e) { return fallback; }
    }

    private static String formatHtmlSafe(String text) {
        if (text == null) return "";
        return text.replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\n", "<br>")
                .replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
    }
}