// File: ReceiveInvoicePage.java
package com.Vcidex.StoryboardSystems.Purchase.Pages.Invoice;

import com.Vcidex.StoryboardSystems.Common.BasePage;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseInvoiceData;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseOrderLine;
import com.Vcidex.StoryboardSystems.Utils.FlatpickrDatePicker;
import com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.PerformanceLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.ReportManager;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.*;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReceiveInvoicePage extends BasePage {

    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ReceiveInvoicePage(WebDriver driver) {
        super(driver);
    }

    // ──────── Locators (Only inputs) ───────────────────────
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

    // ──────── New Getters (for cross-page validation only) ───────────────────────

    public String getBranchName() {
        return getDropdownValue("branch_name");
    }

    public String getVendorName() {
        return getDropdownValue("vendor_name");
    }

    public String getBillTo() {
        return getInputValue("vendor_details");
    }

    public String getShipTo() {
        return getInputValue("ship_to");
    }

    public String getDeliveryTerms() {
        return getInputValue("delivery_terms");
    }

    public String getPaymentTerms() {
        return getDropdownValue("payment_terms");
    }

    public String getDispatchMode() {
        return getInputValue("dispatch_mode");
    }

    public String getCurrency() {
        return getDropdownValue("currency_name");
    }

    public String getExchangeRate() {
        return getInputValue("exchange_rate");
    }

    public String getTermsEditorText() {
        return findElement(termsEditor).getText().trim();
    }

    public String getNetAmount() {
        return getInputValue("net_amount");
    }

    public String getGrandTotal() {
        return getInputValue("grand_total");
    }

    // ──────── Helpers for Getters ───────────────────────

    private String getInputValue(String formControlName) {
        By input = By.xpath("//input[@formcontrolname='" + formControlName + "']");
        return findElement(input).getAttribute("value").trim();
    }

    private String getDropdownValue(String formControlName) {
        By dropdown = By.xpath("//ng-select[@formcontrolname='" + formControlName + "']//div[contains(@class,'ng-value')]");
        return findElement(dropdown).getText().trim();
    }

    // ──────── Form Filling & Submit Methods ───────────────────────

    public void fillInvoiceForm(PurchaseInvoiceData d, ExtentTest node) {
        ReportManager.setTest(node);
        PerformanceLogger.start("fillInvoice");

        MasterLogger.group("Fill Purchase Invoice", () -> {
            waitForOverlayClear();

            MasterLogger.step(MasterLogger.Layer.UI, "Type Invoice Ref = " + d.getInvoiceRefNo(), () -> {
                type(invoiceRefInput, d.getInvoiceRefNo(), "Invoice Ref No");
                return null;
            });

            MasterLogger.step(MasterLogger.Layer.UI, "Pick Invoice Date = " + d.getInvoiceDate().format(fmt), () -> {
                FlatpickrDatePicker.pickDateAndClose(driver, invoiceDateInput, d.getInvoiceDate().format(fmt), "Invoice Date");
                return null;
            });

            MasterLogger.step(MasterLogger.Layer.UI, "Pick Due Date = " + d.getDueDate().format(fmt), () -> {
                FlatpickrDatePicker.pickDateAndClose(driver, dueDateInput, d.getDueDate().format(fmt), "Due Date");
                return null;
            });

            MasterLogger.step(MasterLogger.Layer.UI, "Type Remarks", () -> {
                type(remarksTextArea, d.getRemarks(), "Remarks");
                return null;
            });

            MasterLogger.step(MasterLogger.Layer.UI, "Select Payment Terms", () -> {
                selectFromNgSelect("payment_terms", d.getPaymentTerms());
                return null;
            });

            MasterLogger.step(MasterLogger.Layer.UI, "Select Purchase Type", () -> {
                selectFromNgSelect("purchasetype_name", d.getPurchaseType());
                return null;
            });

            MasterLogger.step(MasterLogger.Layer.UI, "Type Billing Email", () -> {
                type(billingEmailInput, d.getBillingEmail(), "Billing Email");
                return null;
            });

            MasterLogger.step(MasterLogger.Layer.UI, "Select Terms Template", () -> {
                selectFromNgSelect("template_name", d.getTermsTemplate());
                return null;
            });

            MasterLogger.step(MasterLogger.Layer.UI, "Type Terms content", () -> {
                WebElement editor = findElement(termsEditor);
                jsExecutor.executeScript("arguments[0].scrollIntoView({block:'center'});", editor);
                jsExecutor.executeScript("arguments[0].innerHTML = arguments[1];", editor, d.getTermsContent());
                return null;
            });
        });

        PerformanceLogger.end("ReceiveInvoice_fillForm");
    }

    public void submitInvoice(ExtentTest node) {
        ReportManager.setTest(node);

        MasterLogger.step(MasterLogger.Layer.UI, "Submit Invoice", () -> {
            click(submitBtn, "Submit");
            waitForAngularRequestsToFinish();
            waitForOverlayClear();
            return null;
        });
    }

    /**
     * Extracts table rows into PurchaseOrderLine objects
     */
    public List<PurchaseOrderLine> getLineItems() {
        List<PurchaseOrderLine> lines = new ArrayList<>();
        List<WebElement> rows = driver.findElements(By.cssSelector("table#invoiceTable tbody tr"));

        for (WebElement row : rows) {
            String name = row.findElement(By.cssSelector("td.productColumn")).getText().trim();
            int qty = Integer.parseInt(row.findElement(By.cssSelector("td.qtyColumn")).getText().trim());
            lines.add(new PurchaseOrderLine(name, qty));
        }

        return lines;
    }
}