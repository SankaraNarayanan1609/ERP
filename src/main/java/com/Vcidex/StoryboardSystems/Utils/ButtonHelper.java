package com.Vcidex.StoryboardSystems.Utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ButtonHelper {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final JavascriptExecutor js;

    public ButtonHelper(WebDriver driver, Duration timeout) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, timeout);
        this.js     = (JavascriptExecutor) driver;
    }

    /**
     * Clicks any button/input-submit matching label:
     *   • <button>…<span>Label</span></button>
     *   • <button> Label </button>
     *   • <button title="Label">icon</button>
     *   • <input type='submit' value='Label'/>
     */
    public void click(String label) {
        String xp = String.format(
                // buttons with inner text or span text
                "//button[" +
                        "contains(normalize-space(string(.)), '%1$s')" +
                        "]" +
                        // or buttons with title="Label"
                        "|//button[contains(@title, '%1$s')]" +
                        // or input[type=submit] with value="Label"
                        "|//input[@type='submit' and contains(normalize-space(@value), '%1$s')]",
                label
        );

        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xp)));

        js.executeScript("arguments[0].scrollIntoView({block:'center'});", btn);

        try {
            btn.click();
        } catch (ElementClickInterceptedException e) {
            js.executeScript("arguments[0].click();", btn);
        }
    }

    /**
     * Clicks your button, then:
     *   1) waits for the given modalLocator to disappear,
     *   2) waits for a toast-success containing toastText.
     */
    public void clickAndAwait(String label, By modalLocator, String toastText) {
        click(label);

        // 1) modal goes away
        wait.until(ExpectedConditions.invisibilityOfElementLocated(modalLocator));
        // 2) success toast appears
        String toastXpath = String.format(
                "//div[@id='toast-container']" +
                        "//*[contains(@class,'toast-success')" +
                        " and contains(normalize-space(.), '%s')]",
                toastText
        );
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(toastXpath)));
    }
}
