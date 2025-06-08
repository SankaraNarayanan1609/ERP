// ErrorLogger.java
package com.Vcidex.StoryboardSystems.Utils.Logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openqa.selenium.WebDriver;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Logs exception details and known-issue annotations.
 * Delegates diagnostics (screenshots+console) to DiagnosticsLogger.
 */
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

        DiagnosticsLogger.onFailure(driver, context);

        KNOWN_ISSUES.forEach((key, jira) -> {
            if (e.getClass().getSimpleName().contains(key)) {
                logger.error("Known issue: {} → {}", key, jira);
            }
        });
    }
}