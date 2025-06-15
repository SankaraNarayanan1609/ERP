// File: src/main/java/com/Vcidex/StoryboardSystems/Utils/FlatpickrDatePicker.java
package com.Vcidex.StoryboardSystems.Utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class FlatpickrDatePicker {
    private static final Duration TIMEOUT = Duration.ofSeconds(20);
    private static final DateTimeFormatter ISO_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    /**
     * New overload: accepts a LocalDate and formats to yyyy-MM-dd for flatpickr.
     */
    public static void pickFlatpickrDate(WebDriver driver, By locator, LocalDate date, String label) {
        // format to ISO
        String isoDate = date.format(ISO_FMT);
        pickFlatpickrDate(driver, locator, isoDate, label);
    }

    /**
     * Legacy method (string-based) – unchanged for backward compatibility.
     */
    public static void pickFlatpickrDate(WebDriver driver, By locator, String dateToSelect, String label) {
        WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
        LocalDate targetDate;
        try {
            targetDate = LocalDate.parse(dateToSelect, ISO_FMT);
        } catch (Exception e) {
            throw new RuntimeException("❌ Could not parse date '" + dateToSelect + "' for " + label, e);
        }

        WebElement trigger = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", trigger);
        try {
            trigger.click();
        } catch (WebDriverException ex) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", trigger);
        }

        By openCalendar = By.cssSelector(".flatpickr-calendar.open");
        wait.until(ExpectedConditions.visibilityOfElementLocated(openCalendar));

        String desiredMonthName = targetDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        Select monthDropdown = new Select(
                wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".flatpickr-calendar.open .flatpickr-monthDropdown-months"))));
        monthDropdown.selectByVisibleText(desiredMonthName);

        WebElement yearInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".flatpickr-calendar.open .cur-year")));
        yearInput.clear();
        yearInput.sendKeys(String.valueOf(targetDate.getYear()), Keys.ENTER);

        String ariaLabel = desiredMonthName + " " + targetDate.getDayOfMonth() + ", " + targetDate.getYear();
        WebElement dayCell = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(
                        ".flatpickr-calendar.open " +
                                ".flatpickr-day[aria-label=\"" + ariaLabel + "\"]" +
                                ":not(.prevMonthDay):not(.nextMonthDay)"
                )));
        dayCell.click();
    }

    /** Combines pickFlatpickrDate + ESC + wait-for-close */
    public static void pickDateAndClose(WebDriver driver, By locator, LocalDate date, String label) {
        pickFlatpickrDate(driver, locator, date, label);
        closeCalendar(driver, locator);
    }

    /** Legacy method for string-based */
    public static void pickDateAndClose(WebDriver driver, By locator, String dateToSelect, String label) {
        pickFlatpickrDate(driver, locator, dateToSelect, label);
        closeCalendar(driver, locator);
    }

    private static void closeCalendar(WebDriver driver, By locator) {
        WebElement input = driver.findElement(locator);
        input.sendKeys(Keys.ESCAPE);
        new WebDriverWait(driver, TIMEOUT)
                .until(ExpectedConditions.invisibilityOfElementLocated(
                        By.cssSelector(".flatpickr-calendar, .flatpickr-monthDropdown-months")));
        try { Thread.sleep(200); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
    }
}