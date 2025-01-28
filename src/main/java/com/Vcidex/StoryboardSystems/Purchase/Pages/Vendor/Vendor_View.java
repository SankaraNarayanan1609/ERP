package com.Vcidex.StoryboardSystems.Purchase.Pages.Vendor;

import org.openqa.selenium.WebDriver;

public class Vendor_View extends Vendor_Base_Page {

    // Constructor
    public Vendor_View(WebDriver driver) {
        super(driver);
    }

    // Methods to view vendor details
    public String viewVendorName() {
        return vendorName.getAttribute("value");
    }

    public String viewContactPerson() {
        return contactPerson.getAttribute("value");
    }

    public String viewContactNumber() {
        return contactNumber.getAttribute("value");
    }

    public String viewContactEmail() {
        return contactEmail.getAttribute("value");
    }

    public String viewBillingEmail() {
        return billingEmail.getAttribute("value");
    }

    public String viewTaxNumber() {
        return taxNumber.getAttribute("value");
    }

    // Additional view methods for other fields if needed
}