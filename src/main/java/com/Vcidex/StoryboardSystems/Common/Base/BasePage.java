package com.Vcidex.StoryboardSystems.Common.Base;

import com.Vcidex.StoryboardSystems.Utils.Logger.ErrorLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.PerformanceLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.UIActionLogger;
import com.aventstack.extentreports.ExtentTest;
import com.Vcidex.StoryboardSystems.Utils.Logger.ExtentTestManager;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.internal.Require;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected Actions actions;
    protected JavascriptExecutor jsExecutor;
    private static final int TIMEOUT = Integer.parseInt(
            System.getProperty("WebDriver.timeout", "10")
    );

    public BasePage(WebDriver driver) {
        this.driver = Require.nonNull("Driver must be set", driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT));
        this.actions = new Actions(driver);
        this.jsExecutor = (JavascriptExecutor) driver;
    }

    // ---------------- Element Interaction Methods ----------------

    public WebElement findElement(By locator) {
        try {
            WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            UIActionLogger.click(driver, locator, "(lookup) " + locator);
            return el;
        } catch (Exception e) {
            UIActionLogger.failure(driver, "findElement:" + locator);
            throw e;
        }
    }

    public void click(By locator, String name) {
        try {
            UIActionLogger.click(driver, locator, name);
        } catch (Exception e) {
            UIActionLogger.failure(driver, "click:" + name);
            throw e;
        }
    }

    public void click(By locator) {
        click(locator, locator.toString());
    }

    public void type(By locator, String value, String name) {
        try {
            UIActionLogger.type(driver, locator, value, name);
        } catch (Exception e) {
            UIActionLogger.failure(driver, "type:" + name);
            throw e;
        }
    }

    public void type(By locator, String value) {
        type(locator, value, locator.toString());
    }

    public void submit(By locator, String pageName) {
        try {
            UIActionLogger.submit(driver, locator, pageName);
        } catch (Exception e) {
            UIActionLogger.failure(driver, "submit:" + pageName);
            throw e;
        }
    }

    protected void waitForOption(By dropdown, String optionText) {
        // e.g. wait until the option appears in the overlay/panel
        wait.until(driver ->
                findElements(By.xpath(
                        "//body//div[contains(@class,'ng-dropdown-panel')]//span[text()='"+optionText+"']"
                )).size() > 0
        );
    }

    public boolean isElementPresent(By locator) {
        try {
            return driver.findElements(locator).size() > 0;
        } catch (Exception e) {
            UIActionLogger.failure(driver, "isElementPresent:" + locator);
            return false;
        }
    }

    // ---------------- Getters & Reads ----------------

    public String getText(By locator, String name) {
        try {
            String text = driver.findElement(locator).getText();
            UIActionLogger.debug("UI GetText → " + name + " = '" + text + "'");
            return text;
        } catch (Exception e) {
            UIActionLogger.failure(driver, "getText:" + name);
            throw e;
        }
    }

    public String getText(By locator) {
        return getText(locator, locator.toString());
    }

    public String getAttribute(By locator, String attributeName) {
        try {
            String attr = driver.findElement(locator).getAttribute(attributeName);
            UIActionLogger.debug("UI GetAttr → " + locator + "[" + attributeName + "] = '" + attr + "'");
            return attr;
        } catch (Exception e) {
            UIActionLogger.failure(driver, "getAttribute:" + attributeName);
            throw e;
        }
    }

    // ---------------- Waits & Performance ----------------

    public WebElement waitUntilVisible(By locator) {
        String key = "waitVisible:" + locator;
        PerformanceLogger.start(key);
        try {
            WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            PerformanceLogger.end(key);
            return el;
        } catch (Exception e) {
            PerformanceLogger.end(key);
            ErrorLogger.logException(e, "waitUntilVisible:" + locator, driver);
            throw e;
        }
    }

    // ---------------- Dropdowns & Misc ----------------

    public void selectByText(By locator, String value) {
        try {
            UIActionLogger.debug("UI Select → " + value + " @ " + locator);
            new Select(findElement(locator)).selectByVisibleText(value);
        } catch (Exception e) {
            UIActionLogger.failure(driver, "selectDropdown:" + value);
            throw e;
        }
    }

    public void scrollIntoView(By locator) {
        try {
            UIActionLogger.debug("UI Scroll → " + locator);
            jsExecutor.executeScript(
                    "arguments[0].scrollIntoView({behavior:'smooth', block:'center'});",
                    findElement(locator)
            );
        } catch (Exception e) {
            UIActionLogger.failure(driver, "scrollIntoView:" + locator);
            throw e;
        }
    }

    public void moveToElement(By locator) {
        try {
            UIActionLogger.debug("UI Hover → " + locator);
            actions.moveToElement(findElement(locator)).perform();
        } catch (Exception e) {
            UIActionLogger.failure(driver, "moveToElement:" + locator);
            throw e;
        }
    }

    public void executeJavaScript(String script, Object... args) {
        try {
            UIActionLogger.debug("UI JS → " + script);
            jsExecutor.executeScript(script, args);
        } catch (Exception e) {
            UIActionLogger.failure(driver, "executeJavaScript");
            throw e;
        }
    }

    // ---------------- Test Integration ----------------
    protected ExtentTest test = ExtentTestManager.getTest();

    public void performAction(Runnable action, String description) {
        try {
            test.info("🚀 Action: " + description);
            action.run();
            test.pass("✅ Success: " + description);
        } catch (Exception e) {
            test.fail("❌ Failed: " + description + " | " + e.getMessage());
            ErrorLogger.logException(new Exception(description, e), description, driver);
            throw e;
        }
    }
}