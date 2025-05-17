package com.Vcidex.StoryboardSystems.Utils.Logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import com.Vcidex.StoryboardSystems.Utils.Logger.ExtentTestManager;

public class ErrorLogger {
    private static final Logger logger = LoggerFactory.getLogger(ErrorLogger.class);
    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final Map<String,String> KNOWN_ISSUES = Map.of(
            "StaleElementReferenceException", "JIRA-1234"
    );

    private static String ts() {
        return LocalDateTime.now().format(fmt);
    }

    public static void logException(Exception e, String context, WebDriver driver) {
        logger.error("❌ Exception in: {}", context);
        logger.error("Type: {}", e.getClass().getName());
        logger.error("Message: {}", e.getMessage());

        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        logger.error("Stacktrace:\n{}", sw);

        try {
            Path target = Path.of("logs/errors", context.replaceAll("\\W+","_") + ".png");
            Files.createDirectories(target.getParent());
            Files.copy(
                    ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE).toPath(),
                    target,
                    StandardCopyOption.REPLACE_EXISTING
            );
            logger.error("Screenshot saved: {}", target.toString());

            // attach to Extent
            ExtentTestManager.getTest().addScreenCaptureFromPath(target.toString());
        } catch (Exception ex) {
            logger.error("Screenshot failed: {}", ex.getMessage());
        }

        KNOWN_ISSUES.forEach((key,jira) -> {
            if (e.getClass().getSimpleName().contains(key)) {
                logger.error("Known issue: {} → {}", key, jira);
            }
        });
    }

    public static void retry(String action, int attempt, boolean willRetry) {
        logger.warn("Retry #{} for: {} → {}", attempt, action, willRetry ? "will retry" : "giving up");
    }

    public static void recovery(String action, boolean success) {
        logger.info("{}: {}", success ? "Recovered" : "Recovery failed", action);
    }
}