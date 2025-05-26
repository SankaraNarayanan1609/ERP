package com.Vcidex.StoryboardSystems.Utils;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;

public class DebugUtils {
    // Wait for Angular apps to finish (safe to call always, skips if not Angular)
    public static void waitForAngular(WebDriver driver) {
        if (!(driver instanceof JavascriptExecutor)) return;
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            js.executeAsyncScript(
                    "var callback = arguments[arguments.length - 1];" +
                            "if (window.getAllAngularTestabilities) {" +
                            "  var testabilities = window.getAllAngularTestabilities();" +
                            "  var count = testabilities.length;" +
                            "  var done = false;" +
                            "  function check() {" +
                            "    if (!done && testabilities.every(function(t){return t.isStable()})) {" +
                            "      done = true; callback('ready');" +
                            "    } else { setTimeout(check, 100); }" +
                            "  }" +
                            "  check();" +
                            "} else { callback('notAngular'); }"
            );
        } catch (Exception e) {
            System.out.println("Angular wait skipped: " + e);
        }
    }

    // Print session/auth token at a step
    public static void logSessionToken(WebDriver driver, String step) {
        if (!(driver instanceof JavascriptExecutor)) return;
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            Object token = js.executeScript("return window.localStorage.getItem('token') || window.sessionStorage.getItem('token');");
            System.out.println("[" + step + "] TOKEN: " + token);
        } catch (Exception e) {
            System.out.println("Token check error: " + e);
        }
    }

    // Print browser console logs at a step
    public static void logBrowserConsole(WebDriver driver, String step) {
        try {
            System.out.println("--- BROWSER LOGS [" + step + "] ---");
            driver.manage().logs().get("browser").forEach(logEntry ->
                    System.out.println(logEntry.getMessage())
            );
        } catch (Exception e) {
            System.out.println("Console log fetch error: " + e);
        }
    }
}