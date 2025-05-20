package com.Vcidex.StoryboardSystems.Purchase.Pages.Agreement;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class Agreement_Edit extends PurchaseBasePage {

    // Constructor
    public Agreement_Edit(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    // Fields inherited from CreateAgreementPage
    @FindBy(id = "agreementRefNo")
    private WebElement agreementRefNo;

    @FindBy(id = "agreementDate")
    private WebElement agreementDate;

    @FindBy(id = "renewalDateField")
    private WebElement renewalDateField;

    // Unique field for AgreementEditPage
    @FindBy(id = "updateButton")
    private WebElement updateButton;

    // Methods to interact with fields

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

    public void clickUpdateButton() {
        updateButton.click();
    }
}
