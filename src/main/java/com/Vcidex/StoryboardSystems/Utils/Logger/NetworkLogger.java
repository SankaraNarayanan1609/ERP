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

    public static void start(WebDriver driver) {
        devTools = ((org.openqa.selenium.chrome.ChromeDriver)driver).getDevTools();
        devTools.createSession();
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));

        devTools.addListener(Network.requestWillBeSent(), (RequestWillBeSent req) -> {
            logger.debug("BROWSER-NET ▶ {} {}", req.getRequest().getMethod(), req.getRequest().getUrl());
        });
        devTools.addListener(Network.responseReceived(), (ResponseReceived resp) -> {
            logger.debug("BROWSER-NET ◀ {} {}", resp.getResponse().getStatus(), resp.getResponse().getUrl());
        });
    }

    public static void stop() {
        if (devTools != null) devTools.close();
    }
}