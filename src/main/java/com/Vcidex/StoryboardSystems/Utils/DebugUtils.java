// File: src/main/java/com/Vcidex/StoryboardSystems/Utils/DebugUtils.java
package com.Vcidex.StoryboardSystems.Utils;

import org.openqa.selenium.*;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.ScriptTimeoutException;

public class DebugUtils {
    /** Only responsible for waiting for Angular stability. */
    public static void waitForAngular(WebDriver driver) {
        if (!(driver instanceof JavascriptExecutor)) return;
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            js.executeAsyncScript(
                    "var callback = arguments[arguments.length - 1];" +
                            "if (window.getAllAngularTestabilities) {" +
                            "  var testabilities = window.getAllAngularTestabilities();" +
                            "  function check() {" +
                            "    if (testabilities.every(t => t.isStable())) callback('ready');" +
                            "    else setTimeout(check, 100);" +
                            "  } check();" +
                            "} else { callback('notAngular'); }"
            );
        } catch (ScriptTimeoutException|JavascriptException e) {
            System.out.println("Angular wait skipped: " + e);
        }
    }
}