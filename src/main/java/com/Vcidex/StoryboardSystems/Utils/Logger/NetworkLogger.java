package com.Vcidex.StoryboardSystems.Utils.Logger;

import com.Vcidex.StoryboardSystems.Utils.Config.ConfigManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v130.network.Network;
import org.openqa.selenium.devtools.v130.network.model.RequestWillBeSent;
import org.openqa.selenium.devtools.v130.network.model.ResponseReceived;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class NetworkLogger {
    private static final Logger log = LoggerFactory.getLogger(NetworkLogger.class);
    private static DevTools devTools;
    private static final boolean ENABLED = Boolean.parseBoolean(ConfigManager.getProperty("network.logging.enabled", "false"));

    public static void start(WebDriver driver) {
        if (!ENABLED || !(driver instanceof ChromeDriver chromeDriver)) return;

        devTools = chromeDriver.getDevTools();
        devTools.createSession();
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));

        devTools.addListener(Network.requestWillBeSent(), (RequestWillBeSent req) ->
                log.debug("▶ {} {}", req.getRequest().getMethod(), req.getRequest().getUrl())
        );

        devTools.addListener(Network.responseReceived(), (ResponseReceived resp) -> {
            int status = resp.getResponse().getStatus();
            String url = resp.getResponse().getUrl();
            log.debug("◀ {} {}", status, url);

            if (status >= 400) {
                try {
                    var body = devTools.send(Network.getResponseBody(resp.getRequestId()));
                    String content = body.getBody();
                    log.error("Body: {}", content.length() > 500 ? content.substring(0, 500) + "..." : content);
                } catch (Exception ex) {
                    log.error("Could not fetch body: {}", ex.getMessage());
                }
            }
        });
    }

    public static void stop() {
        if (devTools != null) {
            try {
                devTools.close();
            } catch (Exception ignore) {
            }
        }
    }
}