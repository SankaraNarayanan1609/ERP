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

public class ErrorLogger {
    private static final Logger logger = LoggerFactory.getLogger(ErrorLogger.class);
    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private static String ts() {
        return LocalDateTime.now().format(fmt);
    }

    public static void logException(Exception e, String context, WebDriver driver) {
        // … existing stack/screenshot …

        // attach to Extent
        try {
            String path = target.toString();
            ExtentTestManager.getTest().addScreenCaptureFromPath(path);
        } catch(Exception ignore){}

        // known-issues map
        Map<String,String> known = Map.of(
                "StaleElementReferenceException", "JIRA-1234"
        );
        known.entrySet().stream()
                .filter(en -> e.getClass().getSimpleName().contains(en.getKey()))
                .findFirst()
                .ifPresent(en -> logger.error("Known issue: {} (see {})", en.getKey(), en.getValue()));
    }

    public static void retry(String action, int attempt, boolean willRetry) {
        logger.warn("[{}] Retry #{} for: {} → {}", ts(), attempt, action,
                willRetry ? "will retry" : "giving up");
    }

    public static void recovery(String action, boolean success) {
        logger.info("[{}] {}: {}", ts(), success ? "Recovered" : "Recovery failed", action);
    }
}