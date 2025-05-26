package com.Vcidex.StoryboardSystems.Common.Navigation;

import com.Vcidex.StoryboardSystems.Utils.Logger.UIActionLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.ErrorLogger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.Vcidex.StoryboardSystems.Utils.DebugUtils;

import java.io.File; // <--- Required!
import java.nio.file.Paths; // <--- Required!
import java.nio.file.StandardCopyOption; // <--- Required!
import java.time.Duration;

public class NavigationManager {
    private final WebDriver driver;
    private final int NAV_TIMEOUT = 20; // seconds

    public NavigationManager(WebDriver driver) {
        this.driver = driver;
    }

    private void clickAndWait(String label) {
        By locator = By.xpath(
                "//*[self::a or self::span or self::button or self::div or self::li][normalize-space(text())='" + label + "']"
        );
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(NAV_TIMEOUT));
        wait.until(ExpectedConditions.elementToBeClickable(locator));
        WebElement el = driver.findElement(locator);

        // Use Actions for reliable click
        Actions actions = new Actions(driver);
        actions.moveToElement(el).pause(Duration.ofMillis(300)).click().perform();

        DebugUtils.waitForAngular(driver);
        DebugUtils.logSessionToken(driver, "After Login");
        DebugUtils.logBrowserConsole(driver, "After Login");


        try {
            WebDriverWait waitSpinner = new WebDriverWait(driver, Duration.ofSeconds(10));
            waitSpinner.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("ngx-spinner")));
        } catch (Exception ignored) {}

        // LOG after click
        try {
            File src = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
            java.nio.file.Files.copy(src.toPath(), Paths.get("after_click_" + label + ".png"),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            System.out.println("Failed to capture screenshot after clicking: " + label + " - " + e.getMessage());
        }
        // Check for auto-logout
        if (driver.getCurrentUrl().contains("/auth/login")) {
            throw new RuntimeException("Detected auto-logout after clicking: " + label);
        }
        UIActionLogger.click(driver, locator, label);
    }

    public void goTo(String module, String menu, String subMenu) {
        try {
            clickAndWait(module);
            clickAndWait(menu);
            clickAndWait(subMenu);
        } catch (TimeoutException te) {
            UIActionLogger.failure(driver, "goTo(" + module + "→" + menu + "→" + subMenu + ")");
            ErrorLogger.logException(te, "Navigation timeout to " + module, driver);
            throw te;
        } catch (Exception e) {
            UIActionLogger.failure(driver, "goTo(" + module + "→" + menu + "→" + subMenu + ")");
            ErrorLogger.logException(e, "Navigation error to " + module, driver);
            throw e;
        }
    }
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
}