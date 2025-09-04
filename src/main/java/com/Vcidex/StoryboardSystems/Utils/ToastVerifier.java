package com.Vcidex.StoryboardSystems.Utils;

import com.Vcidex.StoryboardSystems.Utils.Logger.DiagnosticsLogger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * ✅ ToastVerifier
 * Captures and verifies toast messages and types with detailed logging.
 * Recommended usage: `ToastVerifier.waitAndCapture(driver)` + `ToastAssertor.assertToast(...)`
 */
public class ToastVerifier {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private String expectedText;
    private ToastType toastType;
    private long minMs = 0, maxMs = 10_000;

    private ToastVerifier(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public static ToastVerifier with(WebDriver driver) {
        return new ToastVerifier(driver);
    }

    public ToastVerifier type(ToastType toastType) {
        this.toastType = toastType;
        return this;
    }

    public ToastVerifier text(String expectedText) {
        this.expectedText = expectedText;
        return this;
    }

    public ToastVerifier durationBetween(long minMs, long maxMs) {
        this.minMs = minMs;
        this.maxMs = maxMs;
        return this;
    }

    /**
     * ❗ Deprecated: Prefer ToastVerifier.waitAndCapture() + ToastAssertor.assertToast()
     */
    @Deprecated
    public void verify() {
        long start = System.currentTimeMillis();

        try {
            By locator = By.xpath("//div[@id='toast-container']" +
                    "/*[contains(@class,'" + toastType.cssClass + "') and contains(@class,'ngx-toastr')]");

            WebElement toast = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));

            String actual = toast.findElement(By.cssSelector(".toast-message")).getText().trim();
            if (!actual.equals(expectedText)) {
                throw new AssertionError("Toast text mismatch. Expected='" + expectedText + "', Actual='" + actual + "'");
            }

            String bg = toast.getCssValue("background-color");
            if (!bg.equals(toastType.expectedBgColor)) {
                throw new AssertionError("Toast background mismatch. Expected='" + toastType.expectedBgColor + "', Actual='" + bg + "'");
            }

            boolean hasIcon = !toast.findElements(By.cssSelector("i, svg")).isEmpty();
            if (!hasIcon) {
                throw new AssertionError("Toast icon missing – expected visible <i> or <svg>");
            }

            wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
            long duration = System.currentTimeMillis() - start;
            if (duration < minMs || duration > maxMs) {
                throw new AssertionError("Toast duration " + duration + "ms not in range [" + minMs + ".." + maxMs + "]");
            }

        } catch (Exception e) {
            DiagnosticsLogger.onFailure(driver, "Toast verification failed", e);
            throw new RuntimeException("Toast verify() failed", e);
        }
    }

    /**
     * ✅ Captures toast message and type (non-assertive)
     * @param driver Active WebDriver
     * @return ToastResult with type + message
     */
    public static ToastResult waitAndCapture(WebDriver driver) {
        By toastLocator = By.xpath("//div[@id='toast-container']/*[contains(@class,'ngx-toastr')]");

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement toast = wait.until(ExpectedConditions.presenceOfElementLocated(toastLocator));
            String classes = toast.getAttribute("class");

            ToastType type = ToastType.fromClass(classes);
            if (type == null) {
                DiagnosticsLogger.onFailure(driver, "Unknown toast class: " + classes, new RuntimeException("Unknown toast type"));
                type = ToastType.INFO; // fallback to INFO
            }

            String message = "";
            try {
                message = toast.findElement(By.cssSelector(".toast-message")).getText().trim();
            } catch (Exception e) {
                DiagnosticsLogger.onFailure(driver, "Toast found but .toast-message missing", e);
                throw new RuntimeException("Toast appeared but .toast-message missing", e);
            }

            if (message.isBlank()) {
                DiagnosticsLogger.onFailure(driver, "Toast appeared but message is blank", new RuntimeException("Blank toast message"));
                throw new RuntimeException("Toast message was blank");
            }

            return new ToastResult(type, message);

        } catch (TimeoutException e) {
            DiagnosticsLogger.onFailure(driver, "Toast not found within timeout", e);
            throw new RuntimeException("Toast not found", e);
        } catch (Exception e) {
            DiagnosticsLogger.onFailure(driver, "Unexpected toast capture error", e);
            throw new RuntimeException("Toast capture failed", e);
        }
    }

    /**
     * ✅ Static nested POJO for Toast Result
     */
    public static class ToastResult {
        private final ToastType type;
        private final String message;

        public ToastResult(ToastType type, String message) {
            this.type = type;
            this.message = message;
        }

        public ToastType getType() {
            return type;
        }

        public String getMessage() {
            return message;
        }
    }
}