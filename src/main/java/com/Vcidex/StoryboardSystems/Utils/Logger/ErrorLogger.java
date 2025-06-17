package com.Vcidex.StoryboardSystems.Utils.Logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openqa.selenium.WebDriver;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class ErrorLogger {
    private static final Logger log = LoggerFactory.getLogger(ErrorLogger.class);
    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final Map<String, String> KNOWN_ISSUES = Map.of(
            "StaleElementReferenceException", "JIRA-1234"
    );

    private static String ts() {
        return LocalDateTime.now().format(fmt);
    }

    public static void logException(Exception e, String context, WebDriver driver) {
        log.error("❌ [{}] Exception in: {}", ts(), context);
        log.error("Type: {}", e.getClass().getName());
        log.error("Message: {}", e.getMessage());

        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        log.error("Stacktrace:\n{}", sw);

        DiagnosticsLogger.onFailure(driver, context);

        KNOWN_ISSUES.forEach((key, jira) -> {
            if (e.getClass().getSimpleName().contains(key)) {
                log.error("Known issue: {} → {}", key, jira);
            }
        });
    }
}