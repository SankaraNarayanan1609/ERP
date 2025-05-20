package com.Vcidex.StoryboardSystems.Utils.Logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v130.network.Network;
import org.openqa.selenium.devtools.v130.network.model.RequestWillBeSent;
import org.openqa.selenium.devtools.v130.network.model.ResponseReceived;

import java.util.Optional;

public class NetworkLogger {
    private static final Logger logger = LoggerFactory.getLogger(NetworkLogger.class);
    private static DevTools devTools;
    private static final boolean ENABLED = Boolean.getBoolean("network.logging.enabled");

    public static void start(WebDriver driver) {
        if (!ENABLED) return;
        devTools = ((org.openqa.selenium.chrome.ChromeDriver) driver).getDevTools();
        devTools.createSession();
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));

        devTools.addListener(Network.requestWillBeSent(), (RequestWillBeSent req) -> {
            logger.debug("▶ {} {}", req.getRequest().getMethod(), req.getRequest().getUrl());
        });
        devTools.addListener(Network.responseReceived(), (ResponseReceived resp) -> {
            int status = resp.getResponse().getStatus();
            String url   = resp.getResponse().getUrl();
            logger.debug("◀ {} {}", status, url);
            if (status >= 400) {
                try {
                    var body = devTools.send(Network.getResponseBody(resp.getRequestId()));
                    logger.error("Body: {}", body.getBody());
                } catch (Exception ex) {
                    logger.error("Could not fetch body: {}", ex.getMessage());
                }
            }
        });
    }

    public static void stop() {
        if (devTools != null) {
            try { devTools.close(); }
            catch (Exception ignore) {}
        }
    }
}