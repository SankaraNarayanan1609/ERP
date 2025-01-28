package com.Vcidex.StoryboardSystems.Purchase.Pages.Vendor;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class Vendor_Base_Page {
    protected WebDriver driver;

    // Constructor
    public Vendor_Base_Page(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    // Common Elements
    @FindBy(id = "vendorCode")
    protected WebElement vendorCode;

    @FindBy(id = "vendorName")
    protected WebElement vendorName;

    @FindBy(id = "contactPerson")
    protected WebElement contactPerson;

    @FindBy(id = "contactNumber")
    protected WebElement contactNumber;

    @FindBy(id = "contactEmail")
    protected WebElement contactEmail;

    @FindBy(id = "billingEmail")
    protected WebElement billingEmail;

    @FindBy(id = "taxNumber")
    protected WebElement taxNumber;

    @FindBy(id = "taxSegment")
    protected WebElement taxSegment;

    @FindBy(id = "averageLeadTime")
    protected WebElement averageLeadTime;

    @FindBy(id = "address1")
    protected WebElement address1;

    @FindBy(id = "address2")
    protected WebElement address2;

    @FindBy(id = "city")
    protected WebElement city;

    @FindBy(id = "postalCode")
    protected WebElement postalCode;

    @FindBy(id = "country")
    protected WebElement country;

    @FindBy(id = "region")
    protected WebElement region;

    @FindBy(id = "currency")
    protected WebElement currency;

    @FindBy(id = "paymentTerms")
    protected WebElement paymentTerms;

    @FindBy(id = "creditDays")
    protected WebElement creditDays;

    @FindBy(id = "backButton")
    protected WebElement backButton;

    // Common Methods
    public void enterVendorName(String name) {
        vendorName.clear();
        vendorName.sendKeys(name);
    }

    public void enterContactPerson(String person) {
        contactPerson.clear();
        contactPerson.sendKeys(person);
    }

    public void enterContactNumber(String number) {
        contactNumber.clear();
        contactNumber.sendKeys(number);
    }

    public void enterContactEmail(String email) {
        contactEmail.clear();
        contactEmail.sendKeys(email);
    }

    public void enterBillingEmail(String email) {
        billingEmail.clear();
        billingEmail.sendKeys(email);
    }

    public void enterTaxNumber(String taxNum) {
        taxNumber.clear();
        taxNumber.sendKeys(taxNum);
    }

    public void enterAddress(String addr1, String addr2) {
        address1.clear();
        address1.sendKeys(addr1);
        address2.clear();
        address2.sendKeys(addr2);
    }

    public void enterCity(String cityValue) {
        city.clear();
        city.sendKeys(cityValue);
    }

    public void enterPostalCode(String code) {
        postalCode.clear();
        postalCode.sendKeys(code);
    }

    public void selectCountry(String countryValue) {
        country.clear();
        country.sendKeys(countryValue);
    }

    public void selectRegion(String regionValue) {
        region.clear();
        region.sendKeys(regionValue);
    }

    public void selectCurrency(String currencyValue) {
        currency.clear();
        currency.sendKeys(currencyValue);
    }

    public void enterPaymentTerms(String terms) {
        paymentTerms.clear();
        paymentTerms.sendKeys(terms);
    }

    public void enterCreditDays(String days) {
        creditDays.clear();
        creditDays.sendKeys(days);
    }

    public void clickBackButton() {
        backButton.click();
    }
}