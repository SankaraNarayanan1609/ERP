package com.Vcidex.StoryboardSystems.Purchase;

import com.Vcidex.StoryboardSystems.Common.Base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class PurchaseBasePage extends BasePage {

    // Constructor
    public PurchaseBasePage(WebDriver driver) {
        super(driver);
    }

    // Methods for common interactions using BasePage methods and constants for labels

    public void selectBranchName(String branch) {
        selectDropdownUsingVisibleText((By) null, PurchaseConstants.BRANCH_NAME_LABEL, branch);
    }

    public void selectVendorName(String vendor) {
        selectDropdownUsingVisibleText((By) null, PurchaseConstants.VENDOR_NAME_LABEL, vendor);
    }

    public void enterDeliveryTerms(String terms) {
        enterTextUsingFollowingSibling(null, PurchaseConstants.DELIVERY_TERMS_LABEL, terms);
    }

    public void enterPaymentTerms(String terms) {
        enterTextUsingFollowingSibling(null, PurchaseConstants.PAYMENT_TERMS_LABEL, terms);
    }

    public void selectCurrency(String currency) {
        selectDropdownUsingVisibleText((By) null, PurchaseConstants.CURRENCY_LABEL, currency);
    }

    public void enterQuantity(String quantity) {
        enterTextUsingFollowingSibling(null, PurchaseConstants.QUANTITY_LABEL, quantity);
    }

    public void enterPrice(String price) {
        enterTextUsingFollowingSibling(null, PurchaseConstants.PRICE_LABEL, price);
    }

    public void enterDiscount(String discount) {
        enterTextUsingFollowingSibling(null, PurchaseConstants.DISCOUNT_LABEL, discount);
    }

    public void enterAddOnCharges(String charges) {
        enterTextUsingFollowingSibling(null, PurchaseConstants.ADD_ON_CHARGES_LABEL, charges);
    }

    public void enterAdditionalDiscount(String discount) {
        enterTextUsingFollowingSibling(null, PurchaseConstants.ADDITIONAL_DISCOUNT_LABEL, discount);
    }

    public void enterFreightCharges(String charges) {
        enterTextUsingFollowingSibling(null, PurchaseConstants.FREIGHT_CHARGES_LABEL, charges);
    }

    public void selectAdditionalTax(String tax) {
        selectDropdownUsingVisibleText((By) null, PurchaseConstants.ADDITIONAL_TAX_LABEL, tax);
    }

    public void enterRoundOff(String roundOff) {
        enterTextUsingFollowingSibling(null, PurchaseConstants.ROUND_OFF_LABEL, roundOff);
    }

    // Methods for viewing data using constants

    public String getVendorDetails() {
        return getTextFromElementByLabel(PurchaseConstants.VENDOR_DETAILS_LABEL);
    }

    public String getExchangeRate() {
        return getTextFromElementByLabel(PurchaseConstants.EXCHANGE_RATE_LABEL);
    }

    public String getTotalAmount() {
        return getTextFromElementByLabel(PurchaseConstants.TOTAL_AMOUNT_LABEL);
    }

    public String getGrandTotal() {
        return getTextFromElementByLabel(PurchaseConstants.GRAND_TOTAL_LABEL);
    }
}
