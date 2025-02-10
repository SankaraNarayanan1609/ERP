package com.Vcidex.StoryboardSystems.Purchase;

import com.Vcidex.StoryboardSystems.Common.Base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class PurchaseBasePage extends BasePage {

    public PurchaseBasePage(WebDriver driver) {
        super(driver);
    }

    protected By getFollowingSiblingLocator(String labelText) {
        return By.xpath(String.format("//label[text()='%s']/following-sibling::*", labelText));
    }

    public void selectBranchName(String branch) {
        selectDropdownUsingVisibleText(getFollowingSiblingLocator(PurchaseConstants.BRANCH_NAME_LABEL), branch);
    }

    public void selectVendorName(String vendor) {
        selectDropdownUsingVisibleText(getFollowingSiblingLocator(PurchaseConstants.VENDOR_NAME_LABEL), vendor);
    }

    public void enterDeliveryTerms(String terms) {
        enterText(getFollowingSiblingLocator(PurchaseConstants.DELIVERY_TERMS_LABEL), terms);
    }

    public void enterPaymentTerms(String terms) {
        enterText(getFollowingSiblingLocator(PurchaseConstants.PAYMENT_TERMS_LABEL), terms);
    }

    public void selectCurrency(String currency) {
        selectDropdownUsingVisibleText(getFollowingSiblingLocator(PurchaseConstants.CURRENCY_LABEL), currency);
    }

    public void enterQuantity(String quantity) {
        enterText(getFollowingSiblingLocator(PurchaseConstants.QUANTITY_LABEL), quantity);
    }

    public void enterPrice(String price) {
        enterText(getFollowingSiblingLocator(PurchaseConstants.PRICE_LABEL), price);
    }

    public void enterDiscount(String discount) {
        enterText(getFollowingSiblingLocator(PurchaseConstants.DISCOUNT_LABEL), discount);
    }

    public void enterAddOnCharges(String charges) {
        enterText(getFollowingSiblingLocator(PurchaseConstants.ADD_ON_CHARGES_LABEL), charges);
    }

    public void enterAdditionalDiscount(String discount) {
        enterText(getFollowingSiblingLocator(PurchaseConstants.ADDITIONAL_DISCOUNT_LABEL), discount);
    }

    public void enterFreightCharges(String charges) {
        enterText(getFollowingSiblingLocator(PurchaseConstants.FREIGHT_CHARGES_LABEL), charges);
    }

    public void selectAdditionalTax(String tax) {
        selectDropdownUsingVisibleText(getFollowingSiblingLocator(PurchaseConstants.ADDITIONAL_TAX_LABEL), tax);
    }

    public void enterRoundOff(String roundOff) {
        enterText(getFollowingSiblingLocator(PurchaseConstants.ROUND_OFF_LABEL), roundOff);
    }

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