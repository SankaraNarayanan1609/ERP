package com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order;

import com.Vcidex.StoryboardSystems.Purchase.PurchaseBasePage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

public class Direct_PO extends PurchaseBasePage {
    private static final Logger logger = LogManager.getLogger(Direct_PO.class);

    public Direct_PO(WebDriver driver) {
        super(driver);
    }

    // Locators
    private final By quantityField = By.xpath("//input[@formcontrolname='productquantity']");
    private final By priceField = By.xpath("//input[@formcontrolname='unitprice']");
    private final By termsConditionsDropdown = By.xpath("//ng-select[@formcontrolname='Terms & Conditions']");
    private final By submitButton = By.xpath("//button[@formcontrolname='Submit']");
    private final By expectedDateInput = By.xpath("//input[@formcontrolname='expecteddate']");

    // Methods

    public void clickDirectPOButton() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        By buttonLocator = By.xpath("//button[contains(text(),'Direct PO')]");
        WebElement directPOButton = wait.until(ExpectedConditions.elementToBeClickable(buttonLocator));
        directPOButton.click();
        logger.info("✅ Clicked on Direct PO Button");
    }

    public void selectBranch(String branch) {
        selectBranchName(branch); // from base class
    }

    public void selectVendor(String vendor) {
        selectVendorName(vendor); // from base class
    }

    public void selectCurrency(String currency) {
        selectCurrencyName(currency); // from base class
    }

    public void enterQuantity(String qty) {
        sendKeys(quantityField, qty, "Quantity");
    }

    public void enterPrice(String price) {
        sendKeys(priceField, price, "Price");
    }

    public void clickSubmit() {
        click(submitButton, "Submit Button");
    }

    public void selectExpectedDate(String day, String month, String year) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // 1. Open the date picker
        WebElement dateInput = wait.until(ExpectedConditions.elementToBeClickable(expectedDateInput));
        dateInput.click();

        // 2. Select year
        WebElement yearInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@class='numInput cur-year']")));
        yearInput.clear();
        yearInput.sendKeys(year);

        // 3. Select month
        WebElement monthDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//select[@class='flatpickr-monthDropdown-months']")));
        Select monthSelect = new Select(monthDropdown);
        monthSelect.selectByVisibleText(month);

        // 4. Select date
        String fullDate = month + " " + day + ", " + year;
        WebElement dateElement = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//span[@class='flatpickr-day' and @aria-label='" + fullDate + "']")));
        dateElement.click();

        logger.info("📅 Selected Expected Date: " + fullDate);
    }

    // Business Flow
    public void createDirectPO(String branch, String vendor, String currency, String quantity, String unitPrice, String termsAndConditions, String day, String month, String year) {
        selectBranch(branch);
        selectVendor(vendor);
        selectCurrency(currency);
        enterQuantity(quantity);
        enterPrice(unitPrice);
        selectExpectedDate(day, month, year);
        //selectTermsAndConditions(termsAndConditions); // optional
        clickSubmit();
    }
}