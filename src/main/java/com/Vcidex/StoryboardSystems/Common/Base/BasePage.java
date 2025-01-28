package com.Vcidex.StoryboardSystems.Common.Base;

import com.Vcidex.StoryboardSystems.Utils.Config.ConfigLoader;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.time.Duration;

public class BasePage {
    protected static final Logger logger = LoggerFactory.getLogger(BasePage.class);
    protected WebDriver driver;
    protected WebDriverWait wait;

    static {
        // Directly call the static method to load the config
        ConfigLoader.load("config.properties");  // Load config once
    }


    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(getTimeoutFromConfig()));
    }


    // Custom exception to handle automation-related errors
    public static class CustomAutomationException extends RuntimeException {
        public CustomAutomationException(String message, Throwable cause) {
            super(message, cause);
        }

        public CustomAutomationException(String message) {
            super(message);
        }
    }

    // Specific exception for element not found
    public static class ElementNotFoundException extends CustomAutomationException {
        public ElementNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }

        public ElementNotFoundException(String message) {
            super(message);
        }
    }

    // Specific exception for timeout errors
    public static class TimeoutException extends CustomAutomationException {
        public TimeoutException(String message, Throwable cause) {
            super(message, cause);
        }

        public TimeoutException(String message) {
            super(message);
        }
    }

    // Specific exception for invalid element state
    public static class InvalidElementStateException extends CustomAutomationException {
        public InvalidElementStateException(String message, Throwable cause) {
            super(message, cause);
        }

        public InvalidElementStateException(String message) {
            super(message);
        }
    }

    public String getText(By locator) {
        return driver.findElement(locator).getText();
    }


    // Fetch the timeout value from config.properties using ConfigLoader
    private static int getTimeoutFromConfig() {
        String timeout = ConfigLoader.get("WebDriver.timeout"); // Use 'get' instead of 'getProperty'
        if (timeout != null) {
            try {
                return Integer.parseInt(timeout);
            } catch (NumberFormatException e) {
                logger.warn("Invalid timeout value in config. Using default value.");
            }
        }
        return 10; // Default timeout if not found or invalid
    }

    // Helper method to find elements with waits
    public WebElement findElement(By locator) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (Exception e) {
            logger.error("Failed to find element: {}", locator, e);
            throw new ElementNotFoundException("Element not found: " + locator, e);
        }
    }

    // Helper method to generate XPath for sibling element based on the label text
    protected String generateXPathForSibling(String labelText, String tagName) {
        return String.format(".//*[text()='%s']/following-sibling::%s", labelText, tagName);
    }

    // General WebDriver Methods
    public void click(By locator) {
        try {
            WebElement element = findElement(locator);
            element.click();
            logger.info("Clicked on element: {}", locator);
        } catch (Exception e) {
            logger.error("Click failed for element: {}", locator, e);
            throw new InvalidElementStateException("Click failed for element: " + locator, e);
        }
    }

    public void sendKeys(By locator, String text) {
        try {
            WebElement element = findElement(locator);
            element.clear();
            element.sendKeys(text);
            logger.info("Text '{}' entered into element: {}", text, locator);
        } catch (Exception e) {
            logger.error("Text entry failed for element: {}", locator, e);
            throw new InvalidElementStateException("Failed to enter text for element: " + locator, e);
        }
    }

    // Method to enter text using a sibling element's label text
    public void enterTextUsingFollowingSibling(By parentLocator, String labelText, String inputText) {
        try {
            String xpath = String.format(".//*[text()='%s']/following-sibling::input", labelText);
            WebElement inputField = (parentLocator != null)
                    ? driver.findElement(parentLocator).findElement(By.xpath(xpath))
                    : driver.findElement(By.xpath(xpath));

            inputField.clear();
            inputField.sendKeys(inputText);
            logger.info("Entered text '{}' in input field with label '{}'", inputText, labelText);
        } catch (Exception e) {
            logger.error("Failed to enter text '{}' for label '{}'", inputText, labelText, e);
            throw new InvalidElementStateException("Text entry failed for label: " + labelText, e);
        }
    }

    // Method to select a dropdown using visible text
    public void selectDropdownUsingVisibleText(By parentLocator, String labelText, String visibleText) {
        try {
            String xpath = String.format(".//*[text()='%s']/following-sibling::select", labelText);
            WebElement dropdown = driver.findElement(By.xpath(xpath));
            Select select = new Select(dropdown);
            select.selectByVisibleText(visibleText);
            logger.info("Selected '{}' in dropdown with label '{}'", visibleText, labelText);
        } catch (Exception e) {
            logger.error("Failed to select '{}' in dropdown for label '{}'", visibleText, labelText, e);
            throw new InvalidElementStateException("Dropdown selection failed for label: " + labelText, e);
        }
    }

    // Method to retrieve text from an element by its label
    public String getTextFromElementByLabel(String labelText) {
        try {
            String xpath = String.format(".//*[text()='%s']/following-sibling::*", labelText);
            WebElement element = driver.findElement(By.xpath(xpath));
            String text = element.getText();
            logger.info("Retrieved text '{}' for label '{}'", text, labelText);
            return text;
        } catch (Exception e) {
            logger.error("Failed to retrieve text for label '{}'", labelText, e);
            throw new ElementNotFoundException("Failed to get text for label: " + labelText, e);
        }
    }

    // Method to capture a full-page screenshot
    public void captureFullPageScreenshot(String fileName) {
        try {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destination = new File("./screenshots/" + fileName + ".png");
            Files.copy(screenshot.toPath(), destination.toPath());
            logger.info("Full page screenshot saved at: {}", destination.getAbsolutePath());
        } catch (Exception e) {
            logger.error("Failed to capture full page screenshot", e);
            throw new CustomAutomationException("Screenshot capture failed", e);
        }
    }

    // Scroll to an element
    public void scrollToElement(By locator) {
        try {
            WebElement element = findElement(locator);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
            logger.info("Scrolled to element: {}", locator);
        } catch (Exception e) {
            logger.error("Failed to scroll to element: {}", locator, e);
            throw new CustomAutomationException("Failed to scroll to element: " + locator, e);
        }
    }

    // Wait for element's attribute to change
    public void waitForAttributeToBe(By locator, String attribute, String value) {
        try {
            wait.until(ExpectedConditions.attributeToBe(locator, attribute, value));
            logger.info("Attribute '{}' of element {} is now '{}'", attribute, locator, value);
        } catch (Exception e) {
            logger.error("Failed to wait for attribute '{}' of element {} to be '{}'", attribute, locator, value, e);
            throw new TimeoutException("Failed to wait for attribute: " + attribute, e);
        }
    }

    // Wait for a specific text to be present in an element
    public void waitForTextToBePresent(By locator, String text) {
        try {
            wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
            logger.info("Text '{}' is now present in element: {}", text, locator);
        } catch (Exception e) {
            logger.error("Failed to wait for text '{}' in element: {}", text, locator, e);
            throw new TimeoutException("Failed to wait for text: " + text, e);
        }
    }

    // Wait for an element to disappear from the page
    public void waitForElementToDisappear(By locator) {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
            logger.info("Element is no longer visible: {}", locator);
        } catch (Exception e) {
            logger.error("Failed to wait for element to disappear: {}", locator, e);
            throw new TimeoutException("Failed to wait for element to disappear: " + locator, e);
        }
    }

    // Execute a JavaScript command
    public void executeJavaScript(String script, Object... args) {
        try {
            JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
            jsExecutor.executeScript(script, args);
            logger.info("Executed JavaScript: {}", script);
        } catch (Exception e) {
            logger.error("Failed to execute JavaScript: {}", script, e);
            throw new CustomAutomationException("JavaScript execution failed", e);
        }
    }
}
