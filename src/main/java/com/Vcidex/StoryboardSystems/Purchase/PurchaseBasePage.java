package com.Vcidex.StoryboardSystems.Purchase;

import com.Vcidex.StoryboardSystems.Common.Base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class PurchaseBasePage extends BasePage {

    // Constructor
    public PurchaseBasePage(WebDriver driver) {
        super(driver);
    }

    // Helper method to dynamically create XPath for labels
    protected By getFollowingSiblingLocator(String labelText) {
        return By.xpath(String.format("//label[text()='%s']/following-sibling::*", labelText));
    }

    // Methods for common interactions using dynamically built XPaths

    public void selectBranchName(String branch) {
        selectDropdownUsingVisibleText(getFollowingSiblingLocator(PurchaseConstants.BRANCH_NAME_LABEL), branch);
    }

    public void selectVendorName(String vendor) {
        selectDropdownUsingVisibleText(getFollowingSiblingLocator(PurchaseConstants.VENDOR_NAME_LABEL), vendor);
    }

    public void enterDeliveryTerms(String terms) {
        enterTextUsingFollowingSibling(getFollowingSiblingLocator(PurchaseConstants.DELIVERY_TERMS_LABEL), terms);
    }

    public void enterPaymentTerms(String terms) {
        enterTextUsingFollowingSibling(getFollowingSiblingLocator(PurchaseConstants.PAYMENT_TERMS_LABEL), terms);
    }

    public void selectCurrency(String currency) {
        selectDropdownUsingVisibleText(getFollowingSiblingLocator(PurchaseConstants.CURRENCY_LABEL), currency);
    }

    public void enterQuantity(String quantity) {
        enterTextUsingFollowingSibling(getFollowingSiblingLocator(PurchaseConstants.QUANTITY_LABEL), quantity);
    }

    public void enterPrice(String price) {
        enterTextUsingFollowingSibling(getFollowingSiblingLocator(PurchaseConstants.PRICE_LABEL), price);
    }

    public void enterDiscount(String discount) {
        enterTextUsingFollowingSibling(getFollowingSiblingLocator(PurchaseConstants.DISCOUNT_LABEL), discount);
    }

    public void enterAddOnCharges(String charges) {
        enterTextUsingFollowingSibling(getFollowingSiblingLocator(PurchaseConstants.ADD_ON_CHARGES_LABEL), charges);
    }

    public void enterAdditionalDiscount(String discount) {
        enterTextUsingFollowingSibling(getFollowingSiblingLocator(PurchaseConstants.ADDITIONAL_DISCOUNT_LABEL), discount);
    }

    public void enterFreightCharges(String charges) {
        enterTextUsingFollowingSibling(getFollowingSiblingLocator(PurchaseConstants.FREIGHT_CHARGES_LABEL), charges);
    }

    public void selectAdditionalTax(String tax) {
        selectDropdownUsingVisibleText(getFollowingSiblingLocator(PurchaseConstants.ADDITIONAL_TAX_LABEL), tax);
    }

    public void enterRoundOff(String roundOff) {
        enterTextUsingFollowingSibling(getFollowingSiblingLocator(PurchaseConstants.ROUND_OFF_LABEL), roundOff);
    }

    // Methods for viewing data using dynamically built XPaths

    public String getVendorDetails() {
        return getText(getFollowingSiblingLocator(PurchaseConstants.VENDOR_DETAILS_LABEL));
    }

    public String getExchangeRate() {
        return getText(getFollowingSiblingLocator(PurchaseConstants.EXCHANGE_RATE_LABEL));
    }

    public String getTotalAmount() {
        return getText(getFollowingSiblingLocator(PurchaseConstants.TOTAL_AMOUNT_LABEL));
    }

    public String getGrandTotal() {
        return getText(getFollowingSiblingLocator(PurchaseConstants.GRAND_TOTAL_LABEL));
    }
}
