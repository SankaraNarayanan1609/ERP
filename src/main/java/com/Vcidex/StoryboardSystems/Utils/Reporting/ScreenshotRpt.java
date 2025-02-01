package com.Vcidex.StoryboardSystems.Utils.Reporting;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class ScreenshotRpt {
    private static final Logger logger = LogManager.getLogger(ScreenshotRpt.class);

    public static void captureScreenshot(WebDriver driver, String fileName) {
        if (driver == null) {
            logger.warn("⚠️ Screenshot not taken - WebDriver is null.");
            return;
        }

        try {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String screenshotPath = "./screenshots/" + fileName + "_" + System.nanoTime() + ".png"; // ✅ Unique filename
            FileUtils.copyFile(screenshot, new File(screenshotPath));
            logger.info("📸 Screenshot saved: {}", screenshotPath);
        } catch (IOException e) {
            logger.error("❌ Failed to capture screenshot", e);
        }
    }
}

