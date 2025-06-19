// ScreenshotHelper.java
package com.Vcidex.StoryboardSystems.Utils.Logger;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Solely responsible for capturing and writing PNG screenshots.
 */
public class ScreenshotHelper {
    private static final Path SCREENSHOT_DIR = Path.of("logs", "screenshots");
    static {
        try {
            Files.createDirectories(SCREENSHOT_DIR);
        } catch (IOException e) {
            throw new RuntimeException("Could not create screenshot dir", e);
        }
    }

    public static Path capture(WebDriver driver, String context) {
        try {
            String fileName = context.replaceAll("\\W+", "_")
                    + "_" + Instant.now().toEpochMilli()
                    + "_" + UUID.randomUUID() + ".png";
            Path target = SCREENSHOT_DIR.resolve(fileName);
            Files.copy(
                    ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE).toPath(),
                    target
            );
            return target;
        } catch (IOException e) {
            throw new RuntimeException("Screenshot failed: " + e.getMessage(), e);
        }
    }
    public static Optional<Path> safeCapture(WebDriver driver, String context) {
        try {
            Path path = capture(driver, context);
            return Optional.of(path);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static String captureBase64(WebDriver driver) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
    }
}