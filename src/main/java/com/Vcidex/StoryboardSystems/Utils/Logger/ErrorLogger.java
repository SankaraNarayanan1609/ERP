// ErrorLogger.java
package com.Vcidex.StoryboardSystems.Utils.Logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import com.Vcidex.StoryboardSystems.Utils.Logger.ExtentTestManager;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

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
        logger.error("❌ [{}] Exception in: {}", ts(), context);
        logger.error("Type: {}", e.getClass().getName());
        logger.error("Message: {}", e.getMessage());

        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        logger.error("Stacktrace:\n{}", sw);

        try {
            Path targetDir = Path.of("logs", "screenshots");
            Files.createDirectories(targetDir);
            String fileName = context.replaceAll("\\W+","_")
                    + "_" + System.currentTimeMillis() + ".png";
            Path target = targetDir.resolve(fileName);

            Files.copy(
                    ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE).toPath(),
                    target,
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING
            );
            logger.error("Screenshot saved: {}", target);

            // embed a thumbnail into Extent
            ExtentTestManager.getTest()
                    .addScreenCaptureFromPath(target.toString(), "Failure @ " + context);
        } catch (Exception ex) {
            logger.error("Screenshot failed: {}", ex.getMessage());
        }

        KNOWN_ISSUES.forEach((key, jira) -> {
            if (e.getClass().getSimpleName().contains(key)) {
                logger.error("Known issue: {} → {}", key, jira);
            }
        });
    }

    /**
     * Logs a retry attempt.
     * @param action   the name of the action being retried
     * @param attempt  which retry attempt this is (1, 2, …)
     * @param willRetry whether another retry will happen
     */
    public static void retry(String action, int attempt, boolean willRetry) {
        logger.warn("Retry #{} for: {} → {}", attempt, action, willRetry ? "will retry" : "giving up");
    }

    /**
     * Logs the outcome of a recovery attempt.
     * @param action  the action that was being recovered
     * @param success whether recovery succeeded
     */
    public static void recovery(String action, boolean success) {
        logger.info("{}: {}", success ? "Recovered" : "Recovery failed", action);
    }
}