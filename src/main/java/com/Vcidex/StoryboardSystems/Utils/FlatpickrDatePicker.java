// File: src/main/java/com/Vcidex/StoryboardSystems/Utils/FlatpickrDatePicker.java
package com.Vcidex.StoryboardSystems.Utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class FlatpickrDatePicker {
    /**
     * Robustly picks a date in a Flatpickr-based input.
     *
     * @param driver       the WebDriver
     * @param locator      locator to the element that actually triggers Flatpickr.
     *                     (It may be an <input class="flatpickr-input">, or a nearby icon/span that your app bound to Flatpickr.)
     * @param dateToSelect a String in "yyyy-MM-dd" format (e.g. "2025-06-08")
     * @param label        a friendly label for logging (e.g. "Expected Date")
     */
    public static void pickFlatpickrDate(WebDriver driver, By locator, String dateToSelect, String label) {
        WebDriverWait wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(20));

        try {
            // 1) Parse the target date (ISO format)
            LocalDate targetDate;
            try {
                targetDate = LocalDate.parse(dateToSelect, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (DateTimeParseException e) {
                throw new RuntimeException("❌ Could not parse date '" + dateToSelect + "' for " + label, e);
            }

            // 2) Scroll the trigger into view and click it (normal click, fallback to JS if blocked)
            WebElement trigger = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", trigger);
            try {
                trigger.click();
            } catch (ElementClickInterceptedException ex) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", trigger);
            } catch (WebDriverException e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", trigger);
            }

            // 3) Wait specifically for the open calendar container (Flatpickr adds “open” when it’s fully visible).
            By openCalendar = By.cssSelector(".flatpickr-calendar.open");
            wait.until(ExpectedConditions.visibilityOfElementLocated(openCalendar));

            // 4a) Wait for the month dropdown inside that open calendar
            By monthDropdownLocator = By.cssSelector(".flatpickr-calendar.open .flatpickr-monthDropdown-months");
            WebElement monthSelectElement = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(monthDropdownLocator)
            );

            // 4b) Set the month by visible text
            Select monthDropdown = new Select(monthSelectElement);
            String desiredMonthName = targetDate.getMonth()
                    .getDisplayName(java.time.format.TextStyle.FULL, Locale.ENGLISH);
            monthDropdown.selectByVisibleText(desiredMonthName);

            // 5) Set the year
            By yearInputLocator = By.cssSelector(".flatpickr-calendar.open .cur-year");
            WebElement yearInput = wait.until(ExpectedConditions.elementToBeClickable(yearInputLocator));
            yearInput.clear();
            yearInput.sendKeys(String.valueOf(targetDate.getYear()));
            yearInput.sendKeys(Keys.ENTER);

            // 6) Build a day‐locator that only lives inside the visible (.open) calendar
            String ariaLabel = desiredMonthName + " " + targetDate.getDayOfMonth() + ", " + targetDate.getYear();
            By dayLocator = By.cssSelector(
                    ".flatpickr-calendar.open " +
                            ".flatpickr-day[aria-label=\"" + ariaLabel + "\"]" +
                            ":not(.prevMonthDay):not(.nextMonthDay)"
            );
            WebElement dayCell = wait.until(ExpectedConditions.elementToBeClickable(dayLocator));
            dayCell.click();

        } catch (TimeoutException toe) {
            throw new RuntimeException(
                    "❌ Could not pick date for " + label +
                            ": timed out waiting for calendar elements (" + toe.getMessage() + ")", toe
            );
        } catch (Exception e) {
            throw new RuntimeException(
                    "❌ Unexpected error picking date for " + label + ": " + e.getMessage(), e
            );
        }
    }
}