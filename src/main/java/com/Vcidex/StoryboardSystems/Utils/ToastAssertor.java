package com.Vcidex.StoryboardSystems.Utils;

import com.Vcidex.StoryboardSystems.Purchase.POJO.LineItem;
import com.Vcidex.StoryboardSystems.Utils.Logger.DiagnosticsLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.ReportManager;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import org.openqa.selenium.WebDriver;

import java.util.Objects;

/**
 * ✅ ToastAssertor
 * Verifies captured toast against expected type/message and logs to Extent.
 */
public final class ToastAssertor {
    private ToastAssertor() {}

    /** Single expected message (contains match). */
    public static void assertToast(WebDriver driver,
                                   ToastVerifier.ToastResult actual,
                                   ToastType expectedType,
                                   String expectedMessage,
                                   LineItem item) {
        assertToast(driver, actual, expectedType,
                expectedMessage == null ? new String[0] : new String[]{expectedMessage},
                item);
    }

    /** Multiple acceptable expected messages (pass if any matches). */
    public static void assertToast(WebDriver driver,
                                   ToastVerifier.ToastResult actual,
                                   ToastType expectedType,
                                   String[] expectedMessages,
                                   LineItem item) {
        boolean ok = true;
        StringBuilder why = new StringBuilder();

        if (actual == null) {
            ok = false;
            why.append("No toast appeared. ");
        } else {
            if (actual.getType() != expectedType) {
                ok = false;
                why.append("Type mismatch (expected ").append(expectedType)
                        .append(", got ").append(actual.getType()).append("). ");
            }

            String msg = actual.getMessage();
            if (msg == null || msg.isBlank()) {
                ok = false;
                why.append("Toast message was blank. ");
            } else if (expectedMessages != null && expectedMessages.length > 0) {
                boolean anyMatch = false;
                for (String exp : expectedMessages) {
                    if (exp != null && !exp.isBlank() && msg.contains(exp)) {
                        anyMatch = true; break;
                    }
                }
                if (!anyMatch) {
                    ok = false;
                    why.append("Message mismatch (expected one of ")
                            .append(String.join(" | ", expectedMessages))
                            .append(", got '").append(msg).append("'). ");
                }
            }
        }

        if (item != null) {
            item.setSuccess(ok);
            item.setFailureReason(ok ? null : why.toString());
        }

        if (ok) {
            ReportManager.getTest().pass(
                    MarkupHelper.createLabel("Toast OK: " + safe(actual == null ? null : actual.getMessage()),
                            ExtentColor.GREEN)
            );
        } else {
            ReportManager.getTest().fail(
                    MarkupHelper.createLabel("Toast FAIL: " + why, ExtentColor.RED)
            );
            DiagnosticsLogger.onFailure(driver, "Toast validation failed",
                    new AssertionError(why.toString()));
        }
    }

    private static String safe(String s){ return s == null ? "" : s; }
}