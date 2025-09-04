package com.Vcidex.StoryboardSystems.Purchase.Support;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

/** ISP/DIP: abstraction to read the success message text after submit. */
public interface SuccessMessageReader {
    Optional<String> read(WebDriver driver);

    /** Default composite reader for common toast/alert/swal patterns. */
    static SuccessMessageReader defaultReader() {
        return new UiSuccessMessageReader();
    }

    // Default implementation kept package-private.
    class UiSuccessMessageReader implements SuccessMessageReader {
        private final List<By> locators = List.of(
                // Toastr-style
                By.cssSelector(".toast-success, .toast-message"),
                // Bootstrap alert
                By.cssSelector(".alert-success"),
                // SweetAlert2
                By.cssSelector(".swal2-popup .swal2-title"),
                By.cssSelector(".swal2-popup .swal2-html-container")
        );

        private final Duration timeout = Duration.ofSeconds(6);

        @Override
        public Optional<String> read(WebDriver driver) {
            for (By by : locators) {
                try {
                    WebElement el = new WebDriverWait(driver, timeout)
                            .until(ExpectedConditions.visibilityOfElementLocated(by));
                    String txt = el.getText();
                    if (txt == null || txt.isBlank()) {
                        try {
                            WebElement inner = el.findElement(By.cssSelector(".toast-message, .swal2-html-container"));
                            txt = inner.getText();
                        } catch (Exception ignore) {}
                    }
                    if (txt != null && !txt.isBlank()) return Optional.of(txt.trim());
                } catch (Exception ignore) {
                    // try next locator
                }
            }
            return Optional.empty();
        }
    }
}