package com.Vcidex.StoryboardSystems.Utils;

import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class FlatpickrDatePicker {

    /**
     * Selects a date from a Flatpickr-powered date picker by interacting with the UI (month, year, day).
     *
     * @param driver     WebDriver instance
     * @param dateInput  Locator for the input element that triggers the calendar
     * @param dateValue  String in "dd-MM-yyyy" format (e.g., "31-05-2025")
     * @param name       Field label for logging
     * @param node       ExtentTest node for logging
     */
    public static void pickFlatpickrDate(WebDriver driver, By dateInput, String dateValue, String name, ExtentTest node) {
        try {
            // Parse the input date (dd-MM-yyyy)
            String[] parts = dateValue.split("-");
            String day = parts[0];
            int monthIndex = Integer.parseInt(parts[1]) - 1; // Flatpickr is zero-based
            String year = parts[2];
            String[] monthNames = {
                    "January", "February", "March", "April", "May", "June",
                    "July", "August", "September", "October", "November", "December"
            };
            String monthName = monthNames[monthIndex];

            // 1. Click the input to open the picker
            WebElement input = driver.findElement(dateInput);
            if (!input.isDisplayed()) {
                throw new RuntimeException("Date input not visible for " + name);
            }
            // Do not remove readonly, just click
            input.click();

            // 2. Wait for calendar popup to be visible
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(8));
            WebElement calendar = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".flatpickr-calendar.open")));

            // 3. Set month from dropdown
            WebElement monthSelect = calendar.findElement(By.cssSelector(".flatpickr-monthDropdown-months"));
            Select selectMonth = new Select(monthSelect);
            selectMonth.selectByValue(String.valueOf(monthIndex));

            // 4. Set year by typing into the year input or using arrows if typing fails
            WebElement yearInput = calendar.findElement(By.cssSelector(".flatpickr-current-month input.cur-year"));
            try {
                yearInput.clear();
                yearInput.sendKeys(year);
                yearInput.sendKeys(Keys.ENTER); // Often Flatpickr needs Enter to confirm
            } catch (ElementNotInteractableException ex) {
                // fallback: use up/down arrows until match, not always needed but for rare UIs
                int displayedYear = Integer.parseInt(yearInput.getAttribute("value"));
                int targetYear = Integer.parseInt(year);
                WebElement arrowUp = calendar.findElement(By.cssSelector(".numInputWrapper .arrowUp"));
                WebElement arrowDown = calendar.findElement(By.cssSelector(".numInputWrapper .arrowDown"));
                while (displayedYear != targetYear) {
                    if (displayedYear < targetYear) {
                        arrowUp.click();
                        displayedYear++;
                    } else {
                        arrowDown.click();
                        displayedYear--;
                    }
                    Thread.sleep(100); // Allow UI to update
                }
            }

            // 5. Click the day by aria-label
            String ariaLabel = String.format("%s %d, %s", monthName, Integer.valueOf(day), year);
            By dayLocator = By.xpath("//span[contains(@class,'flatpickr-day') and @aria-label='" + ariaLabel + "' and not(contains(@class, 'prevMonthDay')) and not(contains(@class, 'nextMonthDay'))]");
            WebElement dayElem = wait.until(ExpectedConditions.elementToBeClickable(dayLocator));
            dayElem.click();

            // 6. Optionally, wait until input value is updated
            wait.until(ExpectedConditions.attributeContains(input, "value", dateValue));

            node.pass("✅ Selected date '" + dateValue + "' for " + name);
        } catch (Exception e) {
            node.fail("❌ Could not pick date for " + name + ": " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
