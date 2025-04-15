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

    public boolean isDirectPOPageLoaded() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            return wait.until(ExpectedConditions.textToBePresentInElementLocated(
                    By.xpath("//h4"), "Direct Purchase Order"));
        } catch (Exception e) {
            return false;
        }
    }

    // Constructor
    public Direct_PO(WebDriver driver) {
        super(driver);
    }
    // Locators
    private final By fileUploadSection = By.xpath("//label[text()='File Upload']/following-sibling::*");
    private final By termsConditionsDropdown = By.xpath("//label[text()='Terms & Conditions']/following-sibling::select");
    private final By submitButton = By.xpath("//button[text()='Submit']");
    private final By confirmationMessage = By.xpath("//div[@id='confirmation-message']");
    private final By branchField = By.xpath("//label[text()='Branch']/following-sibling::input");
    private final By vendorField = By.xpath("//label[text()='Vendor']/following-sibling::input");
    private final By currencyField = By.xpath("//label[text()='Currency']/following-sibling::input");
    private final By quantityField = By.xpath("//label[text()='Quantity']/following-sibling::input");
    private final By priceField = By.xpath("//label[text()='Price']/following-sibling::input");

    // Actions

    public void enterBranch(String branch) {
        sendKeys(branchField, branch, "Branch Name");
    }

    public void enterVendor(String vendor) {
        sendKeys(vendorField, vendor, "Vendor Name");
    }

    public void enterCurrency(String currency) {
        sendKeys(currencyField, currency, "Currency Field");
    }

    public void enterQuantity(String qty) {
        sendKeys(quantityField, qty, "Quantity Field");
    }

    public void enterPrice(String price) {
        sendKeys(priceField, price, "Price Field");
    }

    public void selectTermsAndConditions(String option) {
        selectDropdownUsingVisibleText(termsConditionsDropdown, option);
    }

    public void clickSubmit() {
        click(submitButton, "Submit Button");
    }

    public boolean isConfirmationDisplayed() {
        return isElementPresent(confirmationMessage);
    }

    public void scrollToFileUpload() {
        scrollIntoView(fileUploadSection);
    }

    public String getConfirmationMessage() {
        return getText(confirmationMessage);
    }

    // You can add high-level business flow if needed:
    public void createPurchaseOrder(String branch, String vendor, String currency, String qty, String price, String tnc) {
        enterBranch(branch);
        enterVendor(vendor);
        enterCurrency(currency);
        enterQuantity(qty);
        enterPrice(price);
        selectTermsAndConditions(tnc);
        clickSubmit();
    }
}
