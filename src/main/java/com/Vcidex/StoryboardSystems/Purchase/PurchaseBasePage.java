package com.Vcidex.StoryboardSystems.Purchase;

import com.Vcidex.StoryboardSystems.Utils.Helpers.LocatorUtils;
import com.Vcidex.StoryboardSystems.Common.Base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Reporter;

public class PurchaseBasePage extends BasePage {

    // Locators for all the fields in the Purchase module
    private By branchNameDropdown = By.xpath("//ng-select[@formcontrolname='branch_name']");
    private By vendorNameDropdown = By.xpath("//ng-select[@formcontrolname='vendor_companyname']");
    private By vendorDetailsInput = By.xpath("//input[@formcontrolname='vendor_details']");
    private By shipToInput = By.xpath("//input[@formcontrolname='shipping_address']");
    private By productGroupDropdown = By.xpath("//ng-select[@formcontrolname='product_group']");
    private By productCodeDropdown = By.xpath("//ng-select[@formcontrolname='product_code']");
    private By productNameDropdown = By.xpath("//ng-select[@formcontrolname='product_name']");
    private By descriptionInput = By.xpath("//input[@formcontrolname='description']");
    private By quantityInput = By.xpath("//input[@formcontrolname='quantity']");
    private By priceInput = By.xpath("//input[@formcontrolname='price']");
    private By discountInput = By.xpath("//input[@formcontrolname='discount']");
    private By taxInput = By.xpath("//input[@formcontrolname='tax']");
    private By taxRateInput = By.xpath("//input[@formcontrolname='tax_rate']");
    private By totalAmountInput = By.xpath("//input[@formcontrolname='total_amount']");
    private By termsDropdown = By.xpath("//ng-select[@formcontrolname='terms']");
    private By termsConditionsEditor = By.xpath("//input[@formcontrolname='terms_conditions']");
    private By netAmountInput = By.xpath("//input[@formcontrolname='net_amount']");
    private By addOnChargesInput = By.xpath("//input[@formcontrolname='add_on_charges']");
    private By additionalDiscountInput = By.xpath("//input[@formcontrolname='additional_discount']");
    private By freightChargesInput = By.xpath("//input[@formcontrolname='freight_charges']");
    private By additionalTaxDropdown = By.xpath("//ng-select[@formcontrolname='additional_tax']");
    private By roundOffInput = By.xpath("//input[@formcontrolname='round_off']");
    private By grandTotalInput = By.xpath("//input[@formcontrolname='grand_total']");

    public PurchaseBasePage(WebDriver driver) {
        super(driver);
    }

    // -------------------------- Element Interaction Methods ---------------------------

    public void selectBranchName(String branchName) {
        if (branchName == null || branchName.trim().isEmpty()) return;
        selectDropdownUsingVisibleText(branchNameDropdown, LocatorUtils.escapeXPathText(branchName));
    }

    public void selectVendorName(String vendorName) {
        if (vendorName == null || vendorName.trim().isEmpty()) return;
        selectDropdownUsingVisibleText(vendorNameDropdown, LocatorUtils.escapeXPathText(vendorName));
    }

    public void enterVendorDetails(String vendorDetails) {
        if (vendorDetails == null || vendorDetails.trim().isEmpty()) return;
        sendKeys(vendorDetailsInput, LocatorUtils.escapeXPathText(vendorDetails));
    }

    public void enterShipTo(String shipTo) {
        if (shipTo == null || shipTo.trim().isEmpty()) return;
        sendKeys(shipToInput, LocatorUtils.escapeXPathText(shipTo));
    }

    public void selectProductGroup(String productGroup) {
        if (productGroup == null || productGroup.trim().isEmpty()) return;
        selectDropdownUsingVisibleText(productGroupDropdown, LocatorUtils.escapeXPathText(productGroup));
    }

    public void selectProductCode(String productCode) {
        if (productCode == null || productCode.trim().isEmpty()) return;
        selectDropdownUsingVisibleText(productCodeDropdown, LocatorUtils.escapeXPathText(productCode));
    }

    public void selectProductName(String productName) {
        if (productName == null || productName.trim().isEmpty()) return;
        selectDropdownUsingVisibleText(productNameDropdown, LocatorUtils.escapeXPathText(productName));
    }

    public void enterDescription(String desc) {
        if (desc == null || desc.trim().isEmpty()) {
            Reporter.log("⚠️ Description is empty. Skipping enterDescription().", true);
            return;
        }
        sendKeys(descriptionInput, desc);
    }

    public void enterQuantity(String quantity) {
        if (quantity == null || quantity.trim().isEmpty()) return;
        sendKeys(quantityInput, LocatorUtils.escapeXPathText(quantity));
    }

    public void enterPrice(String price) {
        if (price == null || price.trim().isEmpty()) return;
        sendKeys(priceInput, LocatorUtils.escapeXPathText(price));
    }

    public void enterDiscount(String discount) {
        if (discount == null || discount.trim().isEmpty()) {
            Reporter.log("⚠️ Discount is empty. Skipping enterDiscount().", true);
            return;
        }
        sendKeys(discountInput, discount);
    }
    public void enterTax(String tax) {
        if (tax == null || tax.trim().isEmpty()) return;
        sendKeys(taxInput, LocatorUtils.escapeXPathText(tax));
    }

    public void enterTaxRate(String taxRate) {
        if (taxRate == null || taxRate.trim().isEmpty()) return;
        sendKeys(taxRateInput, LocatorUtils.escapeXPathText(taxRate));
    }

    public void enterTotalAmount(String totalAmount) {
        if (totalAmount == null || totalAmount.trim().isEmpty()) return;
        sendKeys(totalAmountInput, LocatorUtils.escapeXPathText(totalAmount));
    }

    public void selectTermsAndConditions(String terms) {
        if (terms == null || terms.trim().isEmpty()) return;
        selectDropdownUsingVisibleText(termsDropdown, LocatorUtils.escapeXPathText(terms));
    }

    public void enterTermsConditions(String termsConditions) {
        if (termsConditions == null || termsConditions.trim().isEmpty()) return;
        sendKeys(termsConditionsEditor, LocatorUtils.escapeXPathText(termsConditions));
    }

    public String getNetAmount() {
        return getText(netAmountInput);
    }

    public void enterAddOnCharges(String addOnCharges) {
        if (addOnCharges == null || addOnCharges.trim().isEmpty()) return;
        sendKeys(addOnChargesInput, LocatorUtils.escapeXPathText(addOnCharges));
    }

    public void enterAdditionalDiscount(String discount) {
        if (discount == null || discount.trim().isEmpty()) {
            Reporter.log("⚠️ Additional Discount is empty. Skipping enterAdditionalDiscount().", true);
            return;
        }
        sendKeys(additionalDiscountInput, discount);
    }

    public void enterFreightCharges(String freight) {
        if (freight == null || freight.trim().isEmpty()) {
            Reporter.log("⚠️ Freight Charges is empty. Skipping enterFreightCharges().", true);
            return;
        }
        sendKeys(freightChargesInput, freight);
    }

    public void selectAdditionalTax(String additionalTax) {
        if (additionalTax == null || additionalTax.trim().isEmpty()) return;
        selectDropdownUsingVisibleText(additionalTaxDropdown, LocatorUtils.escapeXPathText(additionalTax));
    }

    public void enterRoundOff(String roundOff) {
        if (roundOff == null || roundOff.trim().isEmpty()) return;
        sendKeys(roundOffInput, LocatorUtils.escapeXPathText(roundOff));
    }

    public String getGrandTotal() {
        return getText(grandTotalInput);
    }
}