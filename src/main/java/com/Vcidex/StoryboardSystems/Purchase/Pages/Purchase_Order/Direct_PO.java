package com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order;

import com.Vcidex.StoryboardSystems.Purchase.PurchaseBasePage;
import com.Vcidex.StoryboardSystems.Utils.Reporting.ErrorHandler;
import com.Vcidex.StoryboardSystems.Utils.Navigation.NavigationData;
import com.Vcidex.StoryboardSystems.Utils.Navigation.NavigationHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.WebDriver;


import java.time.Duration;

public class Direct_PO extends PurchaseBasePage {
    private static final Logger logger = LogManager.getLogger(Direct_PO.class);

    private final By fileUploadSection = By.xpath("//label[text()='File Upload']/following-sibling::*");
    private final By termsConditionsDropdown = By.xpath("//label[text()='Terms & Conditions']/following-sibling::select");
    private final By submitButton = By.xpath("//button[text()='Submit']");
    private final By confirmationMessage = By.xpath("//div[@id='confirmation-message']");

    // Updating fields to use label-based XPath
    private final By branchField = By.xpath("//label[text()='Branch']/following-sibling::input");
    private final By vendorField = By.xpath("//label[text()='Vendor']/following-sibling::input");
    private final By currencyField = By.xpath("//label[text()='Currency']/following-sibling::input");
    private final By quantityField = By.xpath("//label[text()='Quantity']/following-sibling::input");
    private final By priceField = By.xpath("//label[text()='Price']/following-sibling::input");
    private final By discountField = By.xpath("//label[text()='Discount']/following-sibling::input");
    private final By addOnChargesField = By.xpath("//label[text()='Add-On Charges']/following-sibling::input");
    private final By additionalDiscountField = By.xpath("//label[text()='Additional Discount']/following-sibling::input");
    private final By freightChargesField = By.xpath("//label[text()='Freight Charges']/following-sibling::input");
    private final By additionalTaxField = By.xpath("//label[text()='Additional Tax']/following-sibling::input");
    private final By roundOffField = By.xpath("//label[text()='Round Off']/following-sibling::input");

    private final NavigationHelper navigationHelper;

    public Direct_PO(WebDriver driver) {
        super(driver);
        this.navigationHelper = new NavigationHelper(driver);
    }

    public void performAction(String actionName, Runnable action, boolean isSubmit, String locator) {
        try {
            logger.info("🔄 Performing action: {}", actionName);
            ErrorHandler.executeSafely(driver, () -> {
                action.run();
                return null;
            }, actionName);
            logger.info("✅ Action succeeded: {}", actionName);
        } catch (Exception e) {
            logger.error("❌ Action failed: {}", actionName, e);
            throw new RuntimeException("Failed to perform: " + actionName, e);
        }
    }

    public void navigateToPO() {
        System.out.println("📁 Navigating to Direct PO...");

        navigationHelper.navigateToModuleAndMenu(
                NavigationData.PO.getModuleName(),
                NavigationData.PO.getMenuName(),
                NavigationData.PO.getSubMenuName()
        );
    }

    public boolean isDirectPOPageLoaded() {
        return ErrorHandler.executeSafely(driver, () -> {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            return wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//h1[contains(text(),'Direct Purchase Order')]")
            )) != null;
        }, "Check if Direct PO Page is Loaded");
    }

    public void clickDirectPOButton() {
        By directPOButton = By.xpath("//button[contains(@class,'btn-primary') and contains(@class,'btn-sm') and contains(text(),'Direct PO')]");

        ErrorHandler.executeSafely(driver, () -> {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            WebElement button = wait.until(ExpectedConditions.elementToBeClickable(directPOButton));

            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", button);
            button.click();
            return null;
        }, "Click Direct PO Button");
    }

    public void fillPurchaseOrderDetails(String branch, String vendor, String currency, String quantity,
                                         String price, String discount, String addOnCharges,
                                         String additionalDiscount, String freightCharges,
                                         String additionalTax, String roundOff) {

        performAction("Enter Branch", () -> findElement(branchField).sendKeys(branch), false, "Branch Field");
        performAction("Enter Vendor", () -> findElement(vendorField).sendKeys(vendor), false, "Vendor Field");
        performAction("Enter Currency", () -> findElement(currencyField).sendKeys(currency), false, "Currency Field");
        performAction("Enter Quantity", () -> findElement(quantityField).sendKeys(quantity), false, "Quantity Field");
        performAction("Enter Price", () -> findElement(priceField).sendKeys(price), false, "Price Field");
        performAction("Enter Discount", () -> findElement(discountField).sendKeys(discount), false, "Discount Field");
        performAction("Enter Add-On Charges", () -> findElement(addOnChargesField).sendKeys(addOnCharges), false, "Add-On Charges Field");
        performAction("Enter Additional Discount", () -> findElement(additionalDiscountField).sendKeys(additionalDiscount), false, "Additional Discount Field");
        performAction("Enter Freight Charges", () -> findElement(freightChargesField).sendKeys(freightCharges), false, "Freight Charges Field");
        performAction("Enter Additional Tax", () -> findElement(additionalTaxField).sendKeys(additionalTax), false, "Additional Tax Field");
        performAction("Enter Round Off", () -> findElement(roundOffField).sendKeys(roundOff), false, "Round Off Field");
    }

    public void selectTermsConditions(String terms) {
        performAction("Select Terms & Conditions", () -> {
            WebElement dropdown = findElement(termsConditionsDropdown);
            Select select = new Select(dropdown);
            select.selectByVisibleText(terms);
        }, false, "Terms and Conditions");
    }

    public void clickSubmitButton() {
        performAction("Click Submit Button", () -> {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            WebElement submitButtonElement = wait.until(ExpectedConditions.elementToBeClickable(submitButton));
            submitButtonElement.click();
        }, true, "Submit Button");
    }

    public void createDirectPO(String branch, String vendor, String currency, String quantity,
                               String price, String discount, String addOnCharges,
                               String additionalDiscount, String freightCharges,
                               String additionalTax, String roundOff,
                               String terms) {

        navigateToPO();

        if (!isDirectPOPageLoaded()) {
            throw new RuntimeException("❌ Direct PO Page not loaded. Navigation likely failed.");
        }

        clickDirectPOButton();

        fillPurchaseOrderDetails(branch, vendor, currency, quantity, price, discount, addOnCharges,
                additionalDiscount, freightCharges, additionalTax, roundOff);

        selectTermsConditions(terms);
        clickSubmitButton();
    }

    public String getConfirmationMessage() {
        return ErrorHandler.executeSafely(driver,
                () -> getText(confirmationMessage),
                "Get Confirmation Message");
    }

    public String fetchPONumberFromConfirmation() {
        return ErrorHandler.executeSafely(driver, () -> {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            wait.until(ExpectedConditions.textToBePresentInElementLocated(confirmationMessage, "PO"));
            String text = getText(confirmationMessage);
            System.out.println("📜 Confirmation message: " + text);
            return text.matches(".*(PO\\d+).*") ? text.replaceAll(".*?(PO\\d+).*", "$1") : "PO not found";
        }, "Fetch PO Number");
    }
}