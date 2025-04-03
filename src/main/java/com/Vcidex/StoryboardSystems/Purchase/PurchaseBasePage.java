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

//    public void enterDeliveryTerms(String terms) {
//        sendKeys(getFollowingSiblingLocator(PurchaseConstants.DELIVERY_TERMS_LABEL), terms);
//    }
//
//    public void enterPaymentTerms(String terms) {
//        sendKeys(getFollowingSiblingLocator(PurchaseConstants.PAYMENT_TERMS_LABEL), terms);
//    }

    public void selectCurrency(String currency) {
        selectDropdownUsingVisibleText(getFollowingSiblingLocator(PurchaseConstants.CURRENCY_LABEL), currency);
    }

    public void enterQuantity(String quantity) {
        sendKeys(getFollowingSiblingLocator(PurchaseConstants.QUANTITY_LABEL), quantity);
    }

    public void enterPrice(String price) {
        sendKeys(getFollowingSiblingLocator(PurchaseConstants.PRICE_LABEL), price);
    }

    public void enterDiscount(String discount) {
        sendKeys(getFollowingSiblingLocator(PurchaseConstants.DISCOUNT_LABEL), discount);
    }

    public void enterAddOnCharges(String charges) {
        sendKeys(getFollowingSiblingLocator(PurchaseConstants.ADD_ON_CHARGES_LABEL), charges);
    }

    public void enterAdditionalDiscount(String discount) {
        sendKeys(getFollowingSiblingLocator(PurchaseConstants.ADDITIONAL_DISCOUNT_LABEL), discount);
    }

    public void enterFreightCharges(String charges) {
        sendKeys(getFollowingSiblingLocator(PurchaseConstants.FREIGHT_CHARGES_LABEL), charges);
    }

    public void selectAdditionalTax(String tax) {
        selectDropdownUsingVisibleText(getFollowingSiblingLocator(PurchaseConstants.ADDITIONAL_TAX_LABEL), tax);
    }

    public void enterRoundOff(String roundOff) {
        sendKeys(getFollowingSiblingLocator(PurchaseConstants.ROUND_OFF_LABEL), roundOff);
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

    public void fillPurchaseOrderDetails(String branch, String vendor, String currency, String quantity,
                                         String price, String discount, String addOnCharges,
                                         String additionalDiscount, String freightCharges,
                                         String additionalTax, String roundOff) {
        if (branch != null && !branch.isEmpty()) {
            selectBranchName(branch);
        }
        if (branch != null && !branch.isEmpty()) {
            selectVendorName(vendor);
        }
        if (branch != null && !branch.isEmpty()) {
            selectCurrency(currency);
        }
        if (branch != null && !branch.isEmpty()) {
            enterQuantity(quantity);
        }
        if (branch != null && !branch.isEmpty()) {
            enterPrice(price);
        }
        if (branch != null && !branch.isEmpty()) {
            enterDiscount(discount);
        }
        if (branch != null && !branch.isEmpty()) {
            enterAddOnCharges(addOnCharges);
        }
        if (branch != null && !branch.isEmpty()) {
            enterAdditionalDiscount(additionalDiscount);
        }
        if (branch != null && !branch.isEmpty()) {
            enterFreightCharges(freightCharges);
        }
        if (branch != null && !branch.isEmpty()) {
        }
            selectAdditionalTax(additionalTax);
        if (branch != null && !branch.isEmpty()) {
            enterRoundOff(roundOff);
        }
    }
}