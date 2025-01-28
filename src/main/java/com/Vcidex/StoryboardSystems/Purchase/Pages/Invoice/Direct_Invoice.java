package com.Vcidex.StoryboardSystems.Purchase.Pages.Invoice;

import com.Vcidex.StoryboardSystems.Purchase.PurchaseBasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class Direct_Invoice extends PurchaseBasePage {

    // Constructor
    public Direct_Invoice(WebDriver driver) {
        super(driver);
    }

    // Unique Elements for Raise_PO
    @FindBy(id = "InvoiceRefNo")
    private WebElement invoiceRefNoInput;

    @FindBy(id = "InvoiceDate")
    private WebElement invoiceDate;

    @FindBy(id = "DueDate")
    private WebElement dueDate;

    @FindBy(id = "PurchaseTypeDropdown")
    private WebElement purchaseTypeDropdown;

    @FindBy(id = "BillingEmail")
    private WebElement billingEmailInput;

    @FindBy(id = "UploadFile")
    private WebElement uploadFile;

    @FindBy(id = "SubmitButton")
    private WebElement submitButton;

    // Methods for Raise_PO

    // Enters an invoice reference number
    public void enterInvoiceRefNo(String invoiceRefNo) {
        invoiceRefNoInput.sendKeys(invoiceRefNo);
    }

    // Selects an invoice date
    public void selectInvoiceDate(String date) {
        invoiceDate.sendKeys(date);
    }

    // Selects a due date
    public void selectDueDate(String date) {
        dueDate.sendKeys(date);
    }

    // Selects a purchase type from the dropdown
    public void selectPurchaseType(String purchaseType) {
        purchaseTypeDropdown.sendKeys(purchaseType);
    }

    // Enters a billing email address
    public void enterBillingEmail(String billingEmail) {
        billingEmailInput.sendKeys(billingEmail);
    }

    // Uploads a file
    public void uploadFile(String filePath) {
        uploadFile.sendKeys(filePath);
    }

    // Clicks the submit button to submit the PO form
    public void clickSubmitButton() {
        submitButton.click();
    }
}
