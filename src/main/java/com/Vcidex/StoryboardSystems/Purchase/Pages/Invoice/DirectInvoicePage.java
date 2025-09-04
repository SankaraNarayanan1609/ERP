// File: src/main/java/com/Vcidex/StoryboardSystems/Purchase/Pages/Invoice/DirectInvoicePage.java
package com.Vcidex.StoryboardSystems.Purchase.Pages.Invoice;

// ─── Imports ────────────────────────────────────────────────────────
import com.Vcidex.StoryboardSystems.Common.BasePage;
import com.Vcidex.StoryboardSystems.Utils.FlatpickrDatePicker;
import com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger.Layer;
import com.Vcidex.StoryboardSystems.Utils.Logger.ReportManager;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.*;

import java.time.format.DateTimeFormatter;

import static com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger.group;
import static com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger.step;

/**
 * Page Object for Direct Invoice page (Purchase module)
 * Handles form input, product rows, header details and submission actions.
 *
 * Reuses base framework utilities for date picker, logging, and reusable UI actions.
 */
public class DirectInvoicePage extends BasePage {

    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // ─── Header Locators ─────────────────────────────────────────────
    private final By branchDropdown        = By.xpath("//ng-select[@formcontrolname='branch_name']");
    private final By invoiceRefInput       = By.xpath("//input[@formcontrolname='invoice_ref_no']");
    private final By invoiceDateInput      = By.xpath("//input[@formcontrolname='invoice_date']");
    private final By vendorDropdown        = By.xpath("//ng-select[@formcontrolname='vendor_companyname']");
    private final By vendorDetailsTextarea = By.xpath("//textarea[@formcontrolname='vendor_details']");
    private final By vendorAddressInput    = By.xpath("//textarea[@formcontrolname='address1']");
    private final By shipToTextarea        = By.xpath("//textarea[@formcontrolname='shipping_address']");
    private final By currencyDropdown      = By.xpath("//ng-select[@formcontrolname='currency_code']");
    private final By exchangeRateInput     = By.xpath("//input[@formcontrolname='exchange_rate']");
    private final By deliveryTermsInput    = By.xpath("//input[@formcontrolname='delivery_terms']");
    private final By paymentTermsDropdown  = By.xpath("//ng-select[@formcontrolname='payment_terms']");
    private final By dispatchModeInput     = By.xpath("//input[@formcontrolname='dispatch_mode']");
    private final By dueDateInput          = By.xpath("//input[@formcontrolname='due_date']");
    private final By purchaseTypeDropdown  = By.xpath("//ng-select[@formcontrolname='purchasetype_name']");
    private final By billingEmailInput     = By.xpath("//input[@formcontrolname='billing_mail']");
    private final By remarksTextarea       = By.xpath("//textarea[@formcontrolname='invoice_remarks']");

    // ─── Product Table Fields ────────────────────────────────────────
    private final By addProductBtn         = By.cssSelector("button.btn.btn-icon.btn-sm.bg-success.me-1");
    private final By productGroupDropdown  = By.xpath("//ng-select[@formcontrolname='productgroup_name']");
    private final By productCodeDropdown   = By.xpath("//ng-select[@formcontrolname='product_code']");
    private final By productNameDropdown   = By.xpath("//ng-select[@formcontrolname='product_name']");
    private final By productTypeInput      = By.xpath("//input[@formcontrolname='producttype_name']");
    private final By descriptionTextarea   = By.xpath("//textarea[@formcontrolname='product_remarks']");
    private final By quantityInput         = By.xpath("//input[@formcontrolname='productquantity']");
    private final By priceInput            = By.xpath("//input[@formcontrolname='unitprice']");
    private final By discountPctInput      = By.xpath("//input[@formcontrolname='productdiscount']");
    private final By discountAmtInput      = By.xpath("//input[@formcontrolname='productdiscount_amountvalue']");
    private final By taxNameInput          = By.xpath("//input[@formcontrolname='tax_prefix']");
    private final By taxRateInput          = By.xpath("//input[@formcontrolname='taxamount1']");
    private final By totalAmountInput      = By.xpath("//input[@formcontrolname='producttotal_amount']");

    // ─── Terms and Summary ───────────────────────────────────────────
    private final By termsDropdown         = By.xpath("//ng-select[@formcontrolname='template_name']");
    private final By termsEditor           = By.cssSelector("div.angular-editor-textarea");
    private final By netAmountInput        = By.xpath("//input[@formcontrolname='totalamount']");
    private final By addonChargesInput     = By.xpath("//input[@formcontrolname='addoncharge']");
    private final By additionalDiscInput   = By.xpath("//input[@formcontrolname='additional_discount']");
    private final By shippingChargesInput  = By.xpath("//input[@formcontrolname='shipping_charges']");
    private final By shippingTaxDropdown   = By.xpath("//ng-select[@formcontrolname='tax_name5']");
    private final By shippingTaxAmtInput   = By.xpath("//input[@formcontrolname='tax_amount5']");
    private final By additionalTaxDropdown = By.xpath("//ng-select[@formcontrolname='tax_name4']");
    private final By additionalTaxAmtInput = By.xpath("//input[@formcontrolname='tax_amount4']");
    private final By roundOffInput         = By.xpath("//input[@formcontrolname='roundoff']");
    private final By grandTotalInput       = By.xpath("//input[@formcontrolname='grandtotal']");

    // ─── Footer Action Buttons ───────────────────────────────────────
    private final By saveDraftBtn          = By.xpath("//button[span[contains(text(),'Save As Draft')]]");
    private final By submitBtn             = By.xpath("//button[@type='submit' and span[contains(text(),'Submit')]]");
    private final By cancelBtn             = By.xpath("//button[span[contains(text(),'Cancel')]]");

    public DirectInvoicePage(WebDriver driver) {
        super(driver);
    }

    /** Fills in the core header fields of the invoice form. */
    public void fillInvoiceHeader(ExtentTest test) {
        ReportManager.setTest(test);
        group("Fill Direct Invoice Header", () -> {
            waitForOverlayClear();

            // in fillInvoiceHeader(...)
            step(Layer.UI, "Select Branch", () -> {
                selectFromNgSelect("branch_name", "Vcidex Solutions Pvt Ltd");
                return null;
            });
            step(Layer.UI, "Enter Invoice Ref No", () -> {
                type(invoiceRefInput, "INV-001", "Invoice Ref No");
                return null;
            });
            step(Layer.UI, "Pick Invoice Date", () -> {
                FlatpickrDatePicker.pickDateAndClose(driver, invoiceDateInput, "2024-06-20", "Invoice Date");
                return null;
            });
            step(Layer.UI, "Select Vendor", () -> {
                selectFromNgSelect("vendor_companyname", "ACME Supplies");
                return null;
            });
            step(Layer.UI, "Fill Address Fields", () -> {
                type(vendorAddressInput, "ACME Towers, Chennai", "Vendor Address");
                type(shipToTextarea, "Chennai Warehouse", "Ship To");
                return null;
            });
            step(Layer.UI, "Set Currency", () -> {
                selectFromNgSelect("currency_code", "INR");
                return null;
            });
            step(Layer.UI, "Enter Delivery & Dispatch", () -> {
                type(deliveryTermsInput, "7 Days", "Delivery Terms");
                type(dispatchModeInput, "Courier", "Dispatch Mode");
                return null;
            });
            step(Layer.UI, "Pick Due Date", () -> {
                FlatpickrDatePicker.pickDateAndClose(driver, dueDateInput, "2024-06-25", "Due Date");
                return null;
            });
            step(Layer.UI, "Choose Purchase Type", () -> {
                selectFromNgSelect("purchasetype_name", "General");
                return null;
            });

            type(billingEmailInput, "invoices@vcidex.com", "Billing Email");
            type(remarksTextarea, "This is a direct invoice", "Remarks");
        });
    }

    /** Adds a product row in the invoice table. */
    public void addProductRow() {
        group("Add Product Row", () -> {
            click(addProductBtn, "Add Product");
            selectFromNgSelect("productgroup_name", "Electronics");
            selectFromNgSelect("product_code", "ELEC123");
            selectFromNgSelect("product_name", "Mouse");
            type(descriptionTextarea, "Wireless Mouse", "Description");
            type(quantityInput, "2", "Quantity");
            type(priceInput, "450", "Unit Price");
            type(discountPctInput, "5", "Discount %");
        });
    }

    /** Fills the terms and conditions section. */
    public void fillTermsAndConditions() {
        group("Fill Terms and Conditions", () -> {
            selectFromNgSelect("template_name", "Default Terms");
            jsExecutor.executeScript("arguments[0].innerHTML = arguments[1];",
                    findElement(termsEditor), "Goods once sold will not be taken back.");
        });
    }

    /** Fills the invoice summary section. */
    public void fillSummaryFields() {
        group("Fill Summary Fields", () -> {
            type(addonChargesInput, "100", "Add On Charges");
            type(additionalDiscInput, "50", "Additional Discount");
            type(shippingChargesInput, "70", "Shipping Charges");

            selectFromNgSelect("tax_name5", "GST 5%");
            selectFromNgSelect("tax_name4", "Service Tax");

            type(roundOffInput, "1", "Round Off");
        });
    }

    /** Clicks Submit and waits for overlay clear. */
    public void submit() {
        step(Layer.UI, "Submit Invoice", () -> {
            click(submitBtn, "Submit");
            waitForAngularRequestsToFinish();
            waitForOverlayClear();
            return null;
        });
    }
}