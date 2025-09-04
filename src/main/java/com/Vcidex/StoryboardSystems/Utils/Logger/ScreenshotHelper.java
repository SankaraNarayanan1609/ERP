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

public class ScreenshotHelper {

    private static Path root() {
        Path od = ReportManager.outDir();
        Path r = (od != null ? od : Path.of("test-output")).resolve("assets").resolve("screens");
        try { Files.createDirectories(r); } catch (IOException e) { throw new RuntimeException("Could not create screenshot dir", e); }
        return r;
    }

    public static Path capture(WebDriver driver, String context) {
        try {
            String fileName = context.replaceAll("\\W+", "_")
                    + "_" + Instant.now().toEpochMilli()
                    + "_" + UUID.randomUUID() + ".png";
            Path target = root().resolve(fileName);
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