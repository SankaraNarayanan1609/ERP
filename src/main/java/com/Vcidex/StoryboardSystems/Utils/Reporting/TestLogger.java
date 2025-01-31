package com.Vcidex.StoryboardSystems.Utils.Reporting;

import com.Vcidex.StoryboardSystems.Common.Base.BasePage;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v131.network.Network;
import org.openqa.selenium.devtools.v131.network.model.RequestId;
import org.openqa.selenium.devtools.v131.network.model.Response;
import org.openqa.selenium.devtools.v131.log.Log;
import org.openqa.selenium.devtools.v131.runtime.Runtime;
import com.aventstack.extentreports.ExtentTest;

import java.util.Optional;
import java.util.Map;

public class TestLogger {
    private final DevTools devTools;
    private final ExtentTest testLogger;
    private final ChromeDriver driver; // ✅ Store driver reference

    public TestLogger(ChromeDriver driver) {
        this.driver = driver; // ✅ Assign driver
        this.devTools = driver.getDevTools();
        devTools.createSession();
        this.testLogger = ExtentTestManager.getTest();
        enableLogging();
    }

    private void enableLogging() {
        // ✅ Enable Network Monitoring
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));

        // ✅ Capture API Requests
        devTools.addListener(Network.requestWillBeSent(), request -> {
            StringBuilder logMessage = new StringBuilder("📡 API Request Sent: \nMethod: " + request.getRequest().getMethod()
                            + "\nURL: " + request.getRequest().getUrl()
                            + "\nHeaders: " + request.getRequest().getHeaders());

                            request.getRequest().getPostData().ifPresent(postData ->
                            logMessage.append("\nPayload: ").append(postData)); // ✅ Corrected

            System.out.println(logMessage);
            testLogger.info(logMessage.toString()); // ✅ Fix applied
        });

        // ✅ Capture API Responses
        devTools.addListener(Network.responseReceived(), response -> {
            Response responseData = response.getResponse();
            String logMessage = "✅ API Response Received: \nStatus: " + responseData.getStatus()
                    + "\nURL: " + responseData.getUrl()
                    + "\nHeaders: " + responseData.getHeaders();

            System.out.println(logMessage);
            testLogger.info(logMessage);

            if (responseData.getStatus() >= 400) {
                String errorLog = "🚨 API Error: " + responseData.getStatus() + " for URL: " + responseData.getUrl();
                System.out.println(errorLog);
                testLogger.fail(errorLog);

                // ✅ Capture Screenshot on API Failure
                BasePage basePage = new BasePage(driver);
                basePage.captureScreenshot("API_Failure_" + System.currentTimeMillis(),
                        "API request failed. Capturing screenshot for debugging.");
            }
        });

        // ✅ Capture JavaScript Errors
        devTools.send(Runtime.enable());
        devTools.addListener(Runtime.exceptionThrown(), exception -> {
            String jsError = "🚨 JavaScript Error: " + exception.getExceptionDetails().getText();
            System.out.println(jsError);
            testLogger.fail(jsError);
        });

        // ✅ Capture User Interactions
        devTools.send(Log.enable());
        devTools.addListener(Log.entryAdded(), logEntry -> {
            if ("userInput".equalsIgnoreCase(logEntry.getSource().toString())) {
                String userAction = "🖱️ User Interaction: " + logEntry.getText();
                System.out.println(userAction);
                testLogger.info(userAction);
            }
        });
    }

    // ✅ Define logNetworkRequest()
    public void logNetworkRequest() {
        if (testLogger != null) {
            testLogger.info("📡 [LOG] Capturing Network Request...");
        }
    }

    // ✅ Define logApiResponse()
    public void logApiResponse() {
        if (testLogger != null) {
            testLogger.info("✅ [LOG] Capturing API Response...");
        }
    }


    // ✅ Define logConsoleLogs()
    public void logConsoleLogs() {
        if (testLogger != null) {
            testLogger.info("📝 [LOG] Capturing Console Logs...");
        }

        devTools.addListener(Log.entryAdded(), logEntry -> {
            if (logEntry.getLevel().toString().equalsIgnoreCase("SEVERE")) {
                String errorLog = "🚨 JavaScript Console Error: " + logEntry.getText();
                System.out.println(errorLog);
                testLogger.fail(errorLog);

                // ✅ Capture Screenshot on JavaScript Console Error
                BasePage basePage = new BasePage(driver);
                basePage.captureScreenshot("Console_Error_" + System.currentTimeMillis(),
                        "JavaScript Console Error detected. Capturing screenshot.");
            }
        });
    }
}
