package com.Vcidex.StoryboardSystems.Purchase.Pages.Vendor;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class Vendor_Add extends Vendor_Base_Page {

    // Constructor
    public Vendor_Add(WebDriver driver) {
        super(driver);
    }

    // Specific Element for Add Page
    @FindBy(id = "submitButton")
    private WebElement submitButton;

    // Specific Method for Add Page
    public void clickSubmitButton() {
        submitButton.click();
    }

    public void addVendor(String name, String contactPerson, String contactNumber, String contactEmail,
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
        clickSubmitButton();
    }
}