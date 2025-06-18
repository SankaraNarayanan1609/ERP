/**
 * Page Object for the "Receive Invoice" page in the Purchase module.
 * This class handles form entry for creating a Purchase Invoice, including:
 * - Reference number, dates, remarks
 * - Terms and Conditions selection
 * - Editing the rich text editor
 * - Submitting the invoice
 */
package com.Vcidex.StoryboardSystems.Purchase.Pages.Invoice;

import com.Vcidex.StoryboardSystems.Common.BasePage;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseInvoiceData;
import com.Vcidex.StoryboardSystems.Utils.FlatpickrDatePicker;
import com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.PerformanceLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.ReportManager;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.*;
import java.time.format.DateTimeFormatter;

public class ReceiveInvoicePage extends BasePage {

    // Used for formatting LocalDate to string before passing to date picker
    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Constructor for initializing this Page Object.
     *
     * @param driver the active WebDriver from the test
     */
    public ReceiveInvoicePage(WebDriver driver) {
        super(driver);
    }

    // ─── Locators for each invoice input field ──────────────────────────────

    private final By invoiceRefInput      = By.xpath("//input[@formcontrolname='invoice_ref_no']");
    private final By invoiceDateInput     = By.xpath("//input[@formcontrolname='invoice_date']");
    private final By dueDateInput         = By.xpath("//input[@formcontrolname='due_date']");
    private final By remarksTextArea      = By.xpath("//textarea[@formcontrolname='invoice_remarks']");
    private final By paymentTermsDropdown = By.xpath("//ng-select[@formcontrolname='payment_terms']");
    private final By purchaseTypeDropdown = By.xpath("//ng-select[@formcontrolname='purchasetype_name']");
    private final By billingEmailInput    = By.xpath("//input[@formcontrolname='billing_mail']");
    private final By termsDropdown        = By.xpath("//ng-select[@formcontrolname='template_name']");
    private final By termsEditor          = By.cssSelector(".angular-editor-wrapper");
    private final By submitBtn            = By.xpath("//button[@type='submit' and contains(.,'Submit')]");

    /**
     * Fills in the invoice form with the given data.
     * This includes all mandatory and non-mandatory fields like ref no, dates,
     * dropdowns, editor input, and remarks.
     *
     * @param d    The data object representing invoice details
     * @param node The ExtentTest node for logging and reporting
     */
    public void fillInvoiceForm(PurchaseInvoiceData d, ExtentTest node) {
        ReportManager.setTest(node);
        PerformanceLogger.start("PurchaseInvoice_fillForm");

        MasterLogger.group("Fill Purchase Invoice", () -> {
            waitForOverlayClear();  // Ensure form is not blocked by loader/spinner

            // Invoice Reference
            MasterLogger.step(MasterLogger.Layer.UI, "Type Invoice Ref = " + d.getInvoiceRefNo(), () -> {
                type(invoiceRefInput, d.getInvoiceRefNo(), "Invoice Ref No");
                return null;
            });

            // Invoice Date (using date picker component)
            MasterLogger.step(MasterLogger.Layer.UI, "Pick Invoice Date = " + d.getInvoiceDate().format(fmt), () -> {
                FlatpickrDatePicker.pickDateAndClose(driver, invoiceDateInput, d.getInvoiceDate().format(fmt), "Invoice Date");
                return null;
            });

            // Due Date
            MasterLogger.step(MasterLogger.Layer.UI, "Pick Due Date = " + d.getDueDate().format(fmt), () -> {
                FlatpickrDatePicker.pickDateAndClose(driver, dueDateInput, d.getDueDate().format(fmt), "Due Date");
                return null;
            });

            // Remarks
            MasterLogger.step(MasterLogger.Layer.UI, "Type Remarks", () -> {
                type(remarksTextArea, d.getRemarks(), "Remarks");
                return null;
            });

            // Payment Terms dropdown
            MasterLogger.step(MasterLogger.Layer.UI, "Select Payment Terms", () -> {
                selectFromNgSelect("payment_terms", d.getPaymentTerms());
                return null;
            });

            // Purchase Type: Product or Service
            MasterLogger.step(MasterLogger.Layer.UI, "Select Purchase Type", () -> {
                selectFromNgSelect("purchasetype_name", d.getPurchaseType());
                return null;
            });

            // Billing Email input
            MasterLogger.step(MasterLogger.Layer.UI, "Type Billing Email", () -> {
                type(billingEmailInput, d.getBillingEmail(), "Billing Email");
                return null;
            });

            // Terms & Conditions template dropdown
            MasterLogger.step(MasterLogger.Layer.UI, "Select Terms Template", () -> {
                selectFromNgSelect("template_name", d.getTermsTemplate());
                return null;
            });

            // Rich text editor field for terms content
            MasterLogger.step(MasterLogger.Layer.UI, "Type Terms content", () -> {
                WebElement editor = findElement(termsEditor);
                jsExecutor.executeScript("arguments[0].scrollIntoView({block:'center'});", editor);
                jsExecutor.executeScript("arguments[0].innerHTML = arguments[1];", editor, d.getTermsContent());
                return null;
            });
        });

        PerformanceLogger.end("ReceiveInvoice_fillForm");
    }

    /**
     * Submits the invoice after filling the form.
     * Waits for Angular/network requests to complete before proceeding.
     *
     * @param node ExtentTest node for logging
     */
    public void submitInvoice(ExtentTest node) {
        ReportManager.setTest(node);

        MasterLogger.step(MasterLogger.Layer.UI, "Submit Invoice", () -> {
            click(submitBtn, "Submit");
            waitForAngularRequestsToFinish();
            waitForOverlayClear();
            return null;
        });
    }
}