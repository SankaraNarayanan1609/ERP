package com.Vcidex.StoryboardSystems.Utils.Helpers;

import com.Vcidex.StoryboardSystems.Utils.Config.ConfigManager;
import com.Vcidex.StoryboardSystems.Utils.Logger.ErrorHandler;
import com.Vcidex.StoryboardSystems.Utils.Logger.ExtentTestManager;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class WaitUtils {

    private static final Logger logger = LoggerFactory.getLogger(WaitUtils.class);

    public static void waitUntilVisible(WebDriver driver, By locator, int timeoutInSeconds) {
        new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds))
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static boolean isElementClickable(WebDriver driver, By locator, int timeoutInSeconds) {
        return ErrorHandler.executeSafely(driver, () -> {
            new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds)).until(ExpectedConditions.elementToBeClickable(locator));
            return true;
        }, "isElementClickable");
    }

    public static void waitForLoaderToDisappear(WebDriver driver) {
        String loaderXPath = ConfigManager.getProperty("loader.xpath", "//div[contains(@class, 'loader')]");
        try {
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(loaderXPath)));
            ExtentTestManager.getTest().info("⏳ Waiting for loader to disappear: " + loaderXPath);
            logger.info("⏳ Waiting for loader to disappear: {}", loaderXPath);
        } catch (TimeoutException e) {
            ExtentTestManager.getTest().warning("⚠️ Loader did not disappear in time.");
            logger.warn("⚠️ Loader did not disappear in time.");
        }
    }
}