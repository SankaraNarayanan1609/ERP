package com.Vcidex.StoryboardSystems.Purchase.Pages.Agreement;

import com.Vcidex.StoryboardSystems.Purchase.PurchaseBasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class Create_Agreement extends PurchaseBasePage {

    // Constructor
    public Create_Agreement(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    // Page-specific elements for Create Agreement
    @FindBy(id = "agreementRefNo")
    private WebElement agreementRefNo;

    @FindBy(id = "agreementDate")
    private WebElement agreementDate;

    @FindBy(id = "renewalDateField")
    private WebElement renewalDateField;

    @FindBy(id = "submitButton")
    private WebElement submitButton;

    // Methods to interact with elements

    public void enterAgreementRefNo(String refNo) {
        agreementRefNo.clear();
        agreementRefNo.sendKeys(refNo);
    }

    public void enterAgreementDate(String date) {
        agreementDate.clear();
        agreementDate.sendKeys(date);
    }

    public void enterRenewalDate(String renewalDate) {
        renewalDateField.clear();
        renewalDateField.sendKeys(renewalDate);
    }

    public void clickSubmitButton() {
        submitButton.click();
    }
}
