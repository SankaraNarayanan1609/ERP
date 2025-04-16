package com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order;

import com.Vcidex.StoryboardSystems.Purchase.PurchaseBasePage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;


public class Direct_PO extends PurchaseBasePage {
    private static final Logger logger = LogManager.getLogger(Direct_PO.class);

    public void clickDirectPOButton() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        By buttonLocator = By.xpath("//button[contains(text(),'Direct PO')]");
        WebElement directPOButton = wait.until(ExpectedConditions.elementToBeClickable(buttonLocator));
        directPOButton.click();
        logger.info("✅ Clicked on Direct PO Button");
    }

    // Constructor
    public Direct_PO(WebDriver driver) {
        super(driver);
    }

    // Locators
    private By quantityField = By.xpath("//input[@formcontrolname='productquantity']");
    private By priceField = By.xpath("//input[@formcontrolname='productprice']");
    private By termsConditionsDropdown = By.xpath("//ng-select[@formcontrolname='Terms & Conditions']");
    private By submitButton = By.xpath("//button[@formcontrolname='Submit']");

    public void selectBranch(String branch) {
        selectBranchName(branch);
    }

    public void selectVendor(String vendor) {
        selectVendorName(vendor);
    }

    public void selectCurrency(String currency) {
        selectCurrency(currency);
    }

    public void enterQuantity(String qty) {
        sendKeys(quantityField, qty, "Quantity");
    }

    public void enterPrice(String price) {
        sendKeys(priceField, price, "Price");
    }

    public void selectTermsAndConditions(String option) {
        selectDropdownUsingVisibleText(termsConditionsDropdown, option);
    }

    public void clickSubmit() {
        click(submitButton, "Submit Button");
    }

    // You can add high-level business flow if needed:
    public void createDirectPO(String branch, String vendor, String currency, String quantity, String unitPrice, String termsAndConditions
    ) {
        selectBranch(branch);
        selectVendor(vendor);
        selectCurrency(currency);
        enterQuantity(quantity);
        enterPrice(unitPrice);
        selectTermsAndConditions(termsAndConditions);
        clickSubmit();
    }
}
