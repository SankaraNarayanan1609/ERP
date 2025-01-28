package com.Vcidex.StoryboardSystems.Purchase.Pages.Vendor;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class Vendor_Edit extends Vendor_Base_Page {

    // Constructor
    public Vendor_Edit(WebDriver driver) {
        super(driver);
    }

    // Specific Element for Edit Page
    @FindBy(id = "updateButton")
    private WebElement updateButton;

    // Specific Method for Edit Page
    public void clickUpdateButton() {
        updateButton.click();
    }

    public void editVendor(String name, String contactPerson, String contactNumber, String contactEmail,
                           String billingEmail, String taxNumber, String address1, String address2,
                           String city, String postalCode, String country, String region,
                           String currency, String paymentTerms, String creditDays) {
        enterVendorName(name);
        enterContactPerson(contactPerson);
        enterContactNumber(contactNumber);
        enterContactEmail(contactEmail);
        enterBillingEmail(billingEmail);
        enterTaxNumber(taxNumber);
        enterAddress(address1, address2);
        enterCity(city);
        enterPostalCode(postalCode);
        selectCountry(country);
        selectRegion(region);
        selectCurrency(currency);
        enterPaymentTerms(paymentTerms);
        enterCreditDays(creditDays);
        clickUpdateButton();
    }
}
