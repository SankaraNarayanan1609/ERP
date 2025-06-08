package com.Vcidex.StoryboardSystems.Utils.Logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.Capabilities;

import java.util.UUID;

public class TestContextLogger {
    private static final Logger logger = LoggerFactory.getLogger(TestContextLogger.class);
    private static final String TEST_RUN_ID = UUID.randomUUID().toString();

    /** General-purpose INFO-level logging. */
    public static void info(String message) {
        logger.info(message);
    }
}