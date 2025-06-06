package com.Vcidex.StoryboardSystems.Utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import java.util.Map;

public class LocalStorageHelper {
    public static void setLocalStorage(WebDriver driver, Map<String, String> data) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        data.forEach((k, v) -> {
            js.executeScript(String.format("window.localStorage.setItem('%s','%s');", k, v));
        });
    }
}