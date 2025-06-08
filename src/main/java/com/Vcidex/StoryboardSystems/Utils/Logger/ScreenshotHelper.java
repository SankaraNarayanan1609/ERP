// ScreenshotHelper.java
package com.Vcidex.StoryboardSystems.Utils.Logger;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.UUID;

/**
 * Handles screenshot file creation.
 */
public class ScreenshotHelper {
    private static final Path DIR = Path.of("logs", "screenshots");
    static {
        try { Files.createDirectories(DIR); } catch (IOException e) {
            throw new RuntimeException("Could not create screenshot dir", e);
        }
    }

    public static Path capture(WebDriver driver, String context) {
        try {
            String name = context.replaceAll("\\W+","_")
                    + "_" + Instant.now().toEpochMilli()
                    + "_" + UUID.randomUUID() + ".png";
            Path target = DIR.resolve(name);
            Files.copy(
                    ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE).toPath(),
                    target
            );
            return target;
        } catch (IOException e) {
            throw new RuntimeException("Screenshot failed: " + e.getMessage(), e);
        }
    }
}