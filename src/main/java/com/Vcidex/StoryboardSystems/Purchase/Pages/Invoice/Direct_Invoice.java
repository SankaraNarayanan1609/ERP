package com.Vcidex.StoryboardSystems.Purchase.Pages.Invoice;

import com.Vcidex.StoryboardSystems.Purchase.PurchaseBasePage;
import com.Vcidex.StoryboardSystems.Purchase.PurchaseLogs;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class Direct_Invoice extends PurchaseBasePage {
    private final By invRefNoInput    = By.id("InvoiceRefNo");
    private final By invDateInput     = By.id("InvoiceDate");
    private final By dueDateInput     = By.id("DueDate");
    private final By purchaseTypeDrop = By.id("PurchaseTypeDropdown");
    private final By billingEmail     = By.id("BillingEmail");
    private final By uploadFileInput  = By.id("UploadFile");
    private final By submitButton     = By.id("SubmitButton");
    private final By invNumberLocator = By.cssSelector(".invoice-number"); // adjust as needed

    public Direct_Invoice(WebDriver driver) {
        super(driver);
    }

    /**
     * Complete “create direct invoice” flow.
     * Returns the new invoice number.
     */
    public String createInvoice(InvoiceData data) {
        // 1) start
        logger.info("🧾 Creating Invoice for PO#: " + data.getLinkedPoNumber());

        // 2) fill form
        sendKeys(invRefNoInput, data.getInvoiceRefNo());
        selectDropdownUsingVisibleText(purchaseTypeDrop, data.getPurchaseType());
        sendKeys(invDateInput, data.getInvoiceDate().toString());
        sendKeys(dueDateInput, data.getDueDate().toString());
        sendKeys(billingEmail, data.getBillingEmail());

        if (data.getFilePath() != null) {
            sendKeys(uploadFileInput, data.getFilePath());
            logger.info("📎 Attached file: " + data.getFilePath());
        }

        // 3) submit
        logger.info(PurchaseLogs.Invoice.created(data.getInvoiceRefNo()));
        click(submitButton, "Submit Invoice");

        // 4) capture
        String invNumber = getText(invNumberLocator, "Invoice Number");
        return invNumber;
    }
}