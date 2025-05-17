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
    private static final boolean REQUESTS_AT_DEBUG = Boolean.getBoolean("api.request.debug");

    private static String ts() {
        return LocalDateTime.now().format(fmt);
    }

    /** Summary at INFO or DEBUG if “chatty” flag set. */
    public static void logRequest(String method, String endpoint, Map<String,String> headers, String body) {
        if (REQUESTS_AT_DEBUG) {
            logger.debug("▶ {} {}", method, endpoint);
        } else {
            logger.info("▶ {} {}", method, endpoint);
        }
        logger.debug("   Req Headers: {}", redact(headers));
        if (body != null && body.length() < 500) {
            logger.debug("   Req Body: {}", body);
        }
    }

    /** Logs status, time, headers count, body length, and error bodies. */
    public static void logResponse(
            int status,
            String body,
            long timeMs,
            Map<String,List<String>> respHeaders,
            String context,
            WebDriver driver
    ) {
        logger.info("◀ {} | {}ms | headers={}", status, timeMs, respHeaders.size());
        logger.debug("   Resp Headers: {}", respHeaders);
        logger.debug("   Resp Body length: {}", (body==null?0:body.length()));

        // Placeholder for contract/schema validation:
        // boolean valid = JsonSchemaValidator.validate(body);
        // logger.debug("   Schema valid: {}", valid);

        if (status / 100 != 2) {
            logger.error("Error Body: {}", body);
            ErrorLogger.logException(
                    new RuntimeException("API error ("+status+"): "+body),
                    context,
                    driver
            );
        } else if (body != null && body.length() < 500) {
            logger.debug("   Body: {}", body);
        }
    }

    private static String redact(Map<String,String> headers) {
        var copy = Map.copyOf(headers);
        if (copy.containsKey("Authorization")) {
            var mod = new java.util.HashMap<>(copy);
            mod.put("Authorization","<REDACTED>");
            return mod.toString();
        }
        return copy.toString();
    }
}