package com.Vcidex.StoryboardSystems.Utils.Reporting;

import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v131.network.Network;
import org.openqa.selenium.devtools.v131.network.model.RequestId;
import org.openqa.selenium.devtools.v131.network.model.Response;
import org.openqa.selenium.devtools.v131.network.model.Request;
import org.openqa.selenium.devtools.v131.log.Log;
import org.openqa.selenium.devtools.v131.network.model.WebSocketFrameSent;
import org.openqa.selenium.devtools.v131.network.model.WebSocketFrameReceived;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.Optional;
import java.util.Map;

public class TestLogger {
    private final ChromeDriver driver;
    private final DevTools devTools;

    public TestLogger(ChromeDriver driver) {
        this.driver = driver;
        this.devTools = driver.getDevTools();
        devTools.createSession();
        enableLogging();
    }

    private void enableLogging() {
        // ✅ Enable Network Monitoring
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));

        // ✅ Capture API Requests
        devTools.addListener(Network.requestWillBeSent(), request -> {
            System.out.println("📡 API Request Sent:");
            Request requestData = request.getRequest();
            System.out.println("Method: " + requestData.getMethod());
            System.out.println("URL: " + requestData.getUrl());
            System.out.println("Headers: " + requestData.getHeaders());
            requestData.getPostData().ifPresent(postData -> System.out.println("Payload: " + postData));
        });

        // ✅ Capture API Responses
        devTools.addListener(Network.responseReceived(), response -> {
            System.out.println("✅ API Response Received:");
            Response responseData = response.getResponse();
            System.out.println("Status: " + responseData.getStatus());
            System.out.println("URL: " + responseData.getUrl());
            System.out.println("Headers: " + responseData.getHeaders());

            // ✅ Log Redirects (301, 302)
            if (responseData.getStatus() == 301 || responseData.getStatus() == 302) {
                System.out.println("🔄 Redirected to: " + responseData.getHeaders().get("Location"));
            }

            // ✅ Log Failed API Calls (4xx & 5xx errors)
            if (responseData.getStatus() >= 400) {
                System.out.println("🚨 API Error: " + responseData.getStatus() + " for URL: " + responseData.getUrl());
            }

            // ✅ Log Response Time
            long requestTime = response.getTimestamp().toEpochMilli();
            long responseTime = System.currentTimeMillis();
            System.out.println("⏳ Response Time: " + (responseTime - requestTime) + " ms");

            // ✅ Capture Response Body
            RequestId requestId = response.getRequestId();
            try {
                Object responseBodyRaw = devTools.send(Network.getResponseBody(requestId));
                if (responseBodyRaw instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> responseBodyMap = (Map<String, Object>) responseBodyRaw;
                    if (responseBodyMap.containsKey("body")) {
                        System.out.println("Response Body: " + responseBodyMap.get("body"));
                    } else {
                        System.out.println("⚠️ No Response Body Available.");
                    }
                } else {
                    System.out.println("⚠️ Unexpected Response Body Format.");
                }
            } catch (Exception e) {
                System.out.println("⚠️ Failed to retrieve response body.");
                e.printStackTrace();
            }
        });

        // ✅ Capture Network Failures (Timeouts, DNS Failures, CORS, etc.)
        devTools.addListener(Network.loadingFailed(), failure -> {
            System.out.println("❌ Network Failure:");
            System.out.println("URL: " + failure.getRequestId());
            System.out.println("Error: " + failure.getErrorText());
            if (failure.getCanceled() != null && failure.getCanceled()) {
                System.out.println("🚫 Request Canceled by Browser.");
            }
        });

        // ✅ Enable WebSocket Monitoring
        devTools.addListener(Network.webSocketCreated(), ws -> System.out.println("📡 WebSocket Created: " + ws.getUrl()));
        devTools.addListener(Network.webSocketFrameSent(), (WebSocketFrameSent frame) -> System.out.println("📤 WebSocket Sent: " + frame.getResponse()));
        devTools.addListener(Network.webSocketFrameReceived(), (WebSocketFrameReceived frame) -> System.out.println("📥 WebSocket Received: " + frame.getResponse()));

        // ✅ Enable Console Log Monitoring
        devTools.send(Log.enable());
        devTools.addListener(Log.entryAdded(), logEntry -> {
            System.out.println("📝 Console Log:");
            System.out.println("Level: " + logEntry.getLevel());
            System.out.println("Text: " + logEntry.getText());
            logEntry.getStackTrace().ifPresent(stack -> {
                System.out.println("🔍 Stack Trace:");
                stack.getCallFrames().forEach(frame -> {
                    System.out.println("📌 " + frame.getFunctionName() + " at " + frame.getUrl() + ":" + frame.getLineNumber());
                });
            });
        });

        // ✅ Enable Security Warnings Monitoring
        devTools.addListener(Network.securityStateChanged(), security -> {
            System.out.println("🔐 Security Warning: " + security.getSecurityState());
            if (security.getExplanations().isPresent()) {
                security.getExplanations().get().forEach(explanation -> {
                    System.out.println("❗ " + explanation.getDescription());
                });
            }
        });

        // ✅ Capture Request & Response Size
        devTools.addListener(Network.responseReceived(), response -> {
            Response responseData = response.getResponse();
            if (responseData.getEncodedDataLength() > 0) {
                System.out.println("📦 Response Size: " + (responseData.getEncodedDataLength() / 1024) + " KB");
            }
        });
    }

    public void logNetworkRequest() {
        System.out.println("📡 [LOG] Network Request Captured.");
    }

    public void logApiResponse() {
        System.out.println("✅ [LOG] API Response Captured.");
    }

    public void logConsoleLogs() {
        System.out.println("📝 [LOG] Capturing Console Logs...");
    }
}
