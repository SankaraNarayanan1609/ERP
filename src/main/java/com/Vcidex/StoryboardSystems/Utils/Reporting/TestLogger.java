package com.Vcidex.StoryboardSystems.Utils.Reporting;

import com.Vcidex.StoryboardSystems.Common.Base.BasePage;
import com.Vcidex.StoryboardSystems.Utils.Reporting.ScreenshotRpt;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v131.network.Network;
import org.openqa.selenium.devtools.v131.network.model.Response;
import org.openqa.selenium.devtools.v131.log.Log;
import org.openqa.selenium.devtools.v131.runtime.Runtime;
import com.aventstack.extentreports.ExtentTest;

import java.util.Optional;

public class TestLogger {
    private static final Logger logger = LogManager.getLogger(TestLogger.class); // ✅ Log4j2 Logger
    private final DevTools devTools;
    private final ExtentTest testLogger;
    private final ChromeDriver driver; // ✅ Store driver reference

    public TestLogger(ChromeDriver driver) {
        this.driver = driver;
        this.devTools = driver.getDevTools();
        devTools.createSession();
        this.testLogger = ExtentTestManager.getTest();
        enableLogging();
    }

    private void enableLogging() {
        // ✅ Enable Network Monitoring
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));

        // ✅ Capture API Requests
        // ✅ Capture API Requests
        devTools.addListener(Network.requestWillBeSent(), request -> {
            final String logMessage = "📡 API Request Sent: \nMethod: " + request.getRequest().getMethod()
                    + "\nURL: " + request.getRequest().getUrl()
                    + "\nHeaders: " + request.getRequest().getHeaders();

            logger.info(logMessage);
            testLogger.info(logMessage);

            request.getRequest().getPostData().ifPresent(postData -> {
                StringBuilder updatedLogMessage = new StringBuilder(logMessage).append("\nPayload: ").append(postData);
                logger.info(updatedLogMessage.toString());
                testLogger.info(updatedLogMessage.toString());
            });
        });


        // ✅ Capture API Responses
        devTools.addListener(Network.responseReceived(), response -> {
            Response responseData = response.getResponse();
            long timestamp = System.currentTimeMillis(); // ✅ Define only once
            String errorCode = "API_FAILURE_" + responseData.getStatus(); // ✅ Define only once

            if (responseData.getStatus() >= 400) {
                String errorLog = "🚨 [" + errorCode + "] API Error at " + timestamp
                        + " | Status: " + responseData.getStatus()
                        + " | URL: " + responseData.getUrl();

                logger.error(errorLog);
                testLogger.fail(errorLog);

                // ✅ Ensure driver is not null before capturing screenshots
                if (driver != null) {
                    ScreenshotRpt.captureScreenshot(driver, "API_Failure_" + timestamp);
                } else {
                    logger.warn("⚠️ Unable to capture screenshot, WebDriver is null.");
                }

                // ✅ Fail the test properly with TestNG
                org.testng.Assert.fail(errorLog);
            }
        });


        // ✅ Capture JavaScript Errors
        devTools.send(Runtime.enable());
        devTools.addListener(Runtime.exceptionThrown(), exception -> {
            long timestamp = System.currentTimeMillis();
            String errorCode = "JS_ERROR";
            String jsError = "🚨 [" + errorCode + "] JavaScript Error at " + timestamp
                    + " | Details: " + exception.getExceptionDetails().getText();

            logger.error(jsError);
            testLogger.fail(jsError);

            // ✅ Capture Screenshot for JS failures
            ScreenshotRpt.captureScreenshot(driver, "JS_Error_" + timestamp);

            // ✅ Force TestNG failure
            throw new AssertionError(jsError);
        });

        // ✅ Capture User Interactions
        devTools.send(Log.enable());
        devTools.addListener(Log.entryAdded(), logEntry -> {
            String userAction = "🖱️ User Interaction at " + System.currentTimeMillis()
                    + " - " + logEntry.getText();

            logger.info(userAction);
            testLogger.info(userAction);
        });
    }

    // ✅ Define logNetworkRequest()
    public void logNetworkRequest() {
        logger.info("📡 [LOG] Capturing Network Request..."); // ✅ Log4j2
        testLogger.info("📡 [LOG] Capturing Network Request...");
    }

    // ✅ Define logApiResponse()
    public void logApiResponse() {
        logger.info("✅ [LOG] Capturing API Response...");
        testLogger.info("✅ [LOG] Capturing API Response...");
    }

    // ✅ Define logConsoleLogs()
    public void logConsoleLogs() {
        logger.info("📝 [LOG] Capturing Console Logs...");
        testLogger.info("📝 [LOG] Capturing Console Logs...");

        devTools.addListener(Log.entryAdded(), logEntry -> {
            String errorLog = "🚨 JavaScript Console Error: " + logEntry.getText();
            logger.error(errorLog);
            testLogger.fail(errorLog);

            // ✅ Capture Screenshot on JavaScript Console Error
            ScreenshotRpt.captureScreenshot(driver, "Console_Error_" + System.currentTimeMillis());
        });
    }
}
