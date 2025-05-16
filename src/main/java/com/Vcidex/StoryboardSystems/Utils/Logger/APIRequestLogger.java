package com.Vcidex.StoryboardSystems.Utils.Logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openqa.selenium.WebDriver;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class APIRequestLogger {
    private static final Logger logger = LoggerFactory.getLogger(APIRequestLogger.class);
    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private static String ts() {
        return LocalDateTime.now().format(fmt);
    }

    public static void logRequest(String method, String endpoint, Map<String,String> headers, String body) {
        logger.debug("▶ {} {}", method, endpoint);
        logger.debug("   Headers: {}", redact(headers));
    }

    public static void logResponse(int status, String body, long timeMs, Map<String, List<String>> respHeaders, String context, WebDriver driver) {
        logger.info("◀ {} | {}ms | headersCount={}", status, timeMs, respHeaders.size());
        logger.debug("   Body length: {}", body == null ? 0 : body.length());
        logger.debug("   Resp Headers: {}", respHeaders);
        // schema check placeholder:
        // boolean valid = JsonSchemaValidator.validate(body);
        // logger.debug("   Schema valid: {}", valid);
        if (status/100 != 2) {
            logger.error("Error Body: {}", body);
            ErrorLogger.logException(new RuntimeException("HTTP "+status), context, driver);
        }
    }


    private static String redact(Map<String,String> headers) {
        var copy = Map.copyOf(headers);
        if (copy.containsKey("Authorization")) {
            var mod = new java.util.HashMap<>(copy);
            mod.put("Authorization", "<REDACTED>");
            return mod.toString();
        }
        return copy.toString();
    }
}