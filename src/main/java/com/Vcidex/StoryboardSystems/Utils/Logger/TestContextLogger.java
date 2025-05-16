package com.Vcidex.StoryboardSystems.Utils.Logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.Capabilities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class TestContextLogger {
    private static final Logger logger = LoggerFactory.getLogger(TestContextLogger.class);
    private static final String TEST_RUN_ID = UUID.randomUUID().toString();

    // Replace all logger.info("[{}] ...", ts(), …) with:
    logger.info("=== TEST START: {} ===", testName);
    logger.info("TestRun ID: {}", TEST_RUN_ID);
    logger.info("Thread ID: {}", Thread.currentThread().getId());
    logger.info("Environment: {} | BaseURL: {}", envName, baseUrl);
    // JVM info
    logger.info("Java Version: {}", System.getProperty("java.version"));

    if (driver instanceof RemoteWebDriver) {
        Capabilities caps = ((RemoteWebDriver) driver).getCapabilities();
        logger.info("Browser: {} {}", caps.getBrowserName(), caps.getBrowserVersion());
        // sessionId
        String session = ((RemoteWebDriver)driver).getSessionId().toString();
        logger.info("Session ID: {}", session);
    }
    logger.info("OS: {} {}", osName, osVersion);
}