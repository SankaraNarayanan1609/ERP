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
import java.util.List;

public class BasePage {
    protected static final Logger logger = LoggerFactory.getLogger(BasePage.class);
    protected WebDriver driver;
    protected WebDriverWait wait;

    static {
        ConfigLoader.load("config.properties");  // Load config once
    }

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(getTimeoutFromConfig()));
    }

    // ✅ Fetch timeout from config.properties
    private static int getTimeoutFromConfig() {
        String timeout = ConfigLoader.get("WebDriver.timeout");
        try {
            return timeout != null ? Integer.parseInt(timeout) : 10;
        } catch (NumberFormatException e) {
            logger.warn("Invalid timeout value in config. Using default value.");
            return 10;
        }
    }

    // ✅ Common Exception Handling
    public static class CustomAutomationException extends RuntimeException {
        public CustomAutomationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class ElementNotFoundException extends CustomAutomationException {
        public ElementNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class TimeoutException extends CustomAutomationException {
        public TimeoutException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class InvalidElementStateException extends CustomAutomationException {
        public InvalidElementStateException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public int getElementCount(By locator) {
        return findElements(locator).size();
    }


    // ✅ Find element with explicit wait
    public WebElement findElement(By locator) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (Exception e) {
            logger.error("Element not found: {}", locator, e);
            throw new ElementNotFoundException("Element not found: " + locator, e);
        }
    }

    // ✅ Find multiple elements
    public List<WebElement> findElements(By locator) {
        return driver.findElements(locator);
    }

    // ✅ Check if element exists (without exceptions)
    public boolean isElementPresent(By locator) {
        return !findElements(locator).isEmpty();
    }

    // ✅ Check if element is visible
    public boolean isElementVisible(By locator) {
        try {
            return findElement(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // ✅ Check if element is clickable
    public boolean isElementClickable(By locator) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(locator));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getTextFromElementByLabel(String labelText) {
        By locator = By.xpath(String.format("//label[text()='%s']/following-sibling::*", labelText));
        return getText(locator);
    }

    public String getElementText(By locator) {
        try {
            WebElement element = findElement(locator);
            String text = element.getText().trim();
            logger.info("Retrieved text '{}' from element '{}'", text, locator);
            return text;
        } catch (Exception e) {
            logger.error("Failed to retrieve text from element: {}", locator, e);
            throw new ElementNotFoundException("Failed to get text from element: " + locator, e);
        }
    }


    // ✅ Click element safely
    public void click(By locator) {
        try {
            WebElement element = findElement(locator);
            element.click();
        } catch (Exception e) {
            logger.warn("Regular click failed, trying JavaScript click for {}", locator);
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", findElement(locator));
        }
    }

    // ✅ Send keys safely
    public void sendKeys(By locator, String text) {
        try {
            WebElement element = findElement(locator);
            element.clear();
            element.sendKeys(text);
            logger.info("Entered text '{}' into element: {}", text, locator);
        } catch (Exception e) {
            logger.error("Text entry failed for element: {}", locator, e);
            throw new InvalidElementStateException("Failed to enter text for element: " + locator, e);
        }
    }

    // ✅ Get text from an element
    public String getText(By locator) {
        try {
            WebElement element = findElement(locator);
            return element.getText();
        } catch (Exception e) {
            logger.error("Failed to get text from element: {}", locator, e);
            throw new ElementNotFoundException("Text retrieval failed for element: " + locator, e);
        }
    }

    public void enterTextUsingFollowingSibling(By parentLocator, String labelText, String inputText) {
        try {
            String xpath = String.format(".//*[text()='%s']/following-sibling::input", labelText);
            WebElement inputField = (parentLocator != null)
                    ? findElement(parentLocator).findElement(By.xpath(xpath))
                    : findElement(By.xpath(xpath));

            inputField.clear();
            inputField.sendKeys(inputText);
            logger.info("Entered text '{}' in input field with label '{}'", inputText, labelText);
        } catch (Exception e) {
            logger.error("Failed to enter text '{}' for label '{}'", inputText, labelText, e);
            throw new InvalidElementStateException("Text entry failed for label: " + labelText, e);
        }
    }

    public void enterTextUsingFollowingSibling(By locator, String inputText) {
        try {
            WebElement inputField = findElement(locator);
            inputField.clear();
            inputField.sendKeys(inputText);
            logger.info("Entered text '{}' in input field with locator '{}'", inputText, locator);
        } catch (Exception e) {
            logger.error("Failed to enter text '{}' for locator '{}'", inputText, locator, e);
            throw new InvalidElementStateException("Text entry failed for locator: " + locator, e);
        }
    }


    // ✅ Select dropdown by visible text
    public void selectDropdownUsingVisibleText(By locator, String visibleText) {
        try {
            Select select = new Select(findElement(locator));
            select.selectByVisibleText(visibleText);
            logger.info("Selected '{}' in dropdown {}", visibleText, locator);
        } catch (Exception e) {
            logger.error("Dropdown selection failed for {}", locator, e);
            throw new InvalidElementStateException("Dropdown selection failed: " + locator, e);
        }
    }

    // ✅ Capture screenshot
    public void captureFullPageScreenshot(String fileName) {
        try {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destination = new File("./screenshots/" + fileName + ".png");
            Files.copy(screenshot.toPath(), destination.toPath());
            logger.info("Screenshot saved at: {}", destination.getAbsolutePath());
        } catch (Exception e) {
            logger.error("Screenshot capture failed", e);
            throw new CustomAutomationException("Screenshot capture failed", e);
        }
    }

    // ✅ Scroll to element smoothly
    public void scrollToElement(By locator) {
        try {
            WebElement element = findElement(locator);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
            logger.info("Scrolled to element: {}", locator);
        } catch (Exception e) {
            logger.error("Failed to scroll to element: {}", locator, e);
            throw new CustomAutomationException("Scroll to element failed: " + locator, e);
        }
    }

    // ✅ Wait for an attribute value change
    public void waitForAttributeToBe(By locator, String attribute, String value) {
        try {
            wait.until(ExpectedConditions.attributeToBe(locator, attribute, value));
            logger.info("Attribute '{}' of element {} is now '{}'", attribute, locator, value);
        } catch (Exception e) {
            logger.error("Failed to wait for attribute '{}' in element {}", attribute, locator, e);
            throw new TimeoutException("Failed to wait for attribute: " + attribute, e);
        }
    }

    // ✅ Wait for text to appear
    public void waitForTextToBePresent(By locator, String text) {
        try {
            wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
            logger.info("Text '{}' is now present in element: {}", text, locator);
        } catch (Exception e) {
            logger.error("Failed to wait for text '{}' in element: {}", text, locator, e);
            throw new TimeoutException("Failed to wait for text: " + text, e);
        }
    }

    // ✅ Wait for page load
    public void waitForPageLoadComplete() {
        new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
        logger.info("Page has completely loaded.");
    }

    // ✅ Execute JavaScript
    public void executeJavaScript(String script, Object... args) {
        try {
            JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
            jsExecutor.executeScript(script, args);
            logger.info("Executed JavaScript: {}", script);
        } catch (Exception e) {
            logger.error("JavaScript execution failed: {}", script, e);
            throw new CustomAutomationException("JavaScript execution failed", e);
        }
    }
}