// File: src/main/java/com/Vcidex/StoryboardSystems/Purchase/Pages/Purchase_Order/DirectPO.java

/**
 * Page Object for "Direct PO" form inside the Purchase module.
 *
 * This class handles:
 * - Opening the modal
 * - Filling header & product rows
 * - Handling optional renewal flow
 * - Performing validations (totals)
 * - Submitting or saving the form
 *
 * Dependencies:
 * - Extends BasePage for access to reusable actions (click, type, wait)
 * - Uses ReportManager for ExtentReport integration
 * - Works with PurchaseOrderData and LineItem objects
 */

package com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order;

// ─── Logging Utilities ───────────────────────────────────────────────
import static com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger.step;
import static com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger.group;

// ─── Core Framework Components ──────────────────────────────────────
import com.Vcidex.StoryboardSystems.Common.BasePage; // Base class for all page objects
import com.Vcidex.StoryboardSystems.Utils.FlatpickrDatePicker; // Custom calendar handler

// ─── POJO / Data Models ─────────────────────────────────────────────
import com.Vcidex.StoryboardSystems.Purchase.POJO.LineItem;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseOrderData;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseOrderLine;

// ─── Logger + Performance Tools ─────────────────────────────────────
import com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger.Layer;
import com.Vcidex.StoryboardSystems.Utils.Logger.PerformanceLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.ReportManager;

// ─── External Libraries ─────────────────────────────────────────────
import com.aventstack.extentreports.ExtentTest; // Used for step-wise test reporting
import org.openqa.selenium.*; // Selenium WebDriver + WebElement
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DirectPO extends BasePage {

    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // ─── Locators for Header Section ──────────────────────────────────────────
    private final By directPOButton     = By.xpath("//button[contains(.,'Direct PO') and @data-bs-target='#myModaladd']");
    private final By branchNameDropdown = By.xpath("//ng-select[@formcontrolname='branch_name']");
    private final By poRefNoInput       = By.xpath("//input[@formcontrolname='po_no']");
    private final By poDatePicker       = By.xpath("//input[@formcontrolname='po_date']");
    private final By expectedDatePicker = By.xpath("//input[@formcontrolname='expected_date']");
    private final By vendorNameDropdown = By.xpath("//ng-select[@formcontrolname='vendor_companyname']");
    private final By billToInput        = By.xpath("//textarea[@formcontrolname='address1']");
    private final By shipToInput        = By.xpath("//textarea[@formcontrolname='shipping_address']");
    private final By requestedByDropdown= By.xpath("//ng-select[@formcontrolname='employee_name']");
    private final By requestorContact   = By.xpath("//input[@formcontrolname='Requestor_details']");
    private final By deliveryTermsInput = By.xpath("//input[@formcontrolname='delivery_terms']");
    private final By paymentTermsInput  = By.xpath("//input[@formcontrolname='payment_terms']");
    private final By dispatchModeInput  = By.xpath("//input[@formcontrolname='dispatch_mode']");
    private final By currencyDropdown   = By.xpath("//ng-select[@formcontrolname='currency_code']");
    private final By exchangeRateInput  = By.xpath("//input[@formcontrolname='exchange_rate']");
    private final By coverNoteInput     = By.xpath("//textarea[@formcontrolname='po_covernote']");

    // ─── Renewal Fields ───────────────────────────────────────────────────────
    private final By renewalYesRadio    = By.cssSelector("input[formcontrolname='renewal_mode'][value='Y']");
    private final By renewalNoRadio     = By.cssSelector("input[formcontrolname='renewal_mode'][value='N']");
    private final By renewalDatePicker  = By.xpath("//input[@formcontrolname='renewal_date']");
    private final By frequencyDropdown  = By.xpath("//ng-select[@formcontrolname='frequency_terms']");

    // ─── Line Item Fields ─────────────────────────────────────────────────────
    private final By addProductBtn      = By.cssSelector("button.btn.btn-icon.btn-sm.bg-success.me-1");
    private final By descriptionInput   = By.xpath("//textarea[@formcontrolname='product_remarks']");
    private final By quantityInput      = By.xpath("//input[@formcontrolname='productquantity']");
    private final By priceInput         = By.xpath("//input[@formcontrolname='unitprice']");
    private final By discountInput      = By.xpath("//input[@formcontrolname='productdiscount']");
    private final By saveProductBtn     = addProductBtn;

    // ─── Terms and Summary Fields ─────────────────────────────────────────────
    private final By termsEditor        = By.cssSelector(".angular-editor-wrapper");
    private final By netAmountInput     = By.xpath("//input[@formcontrolname='totalamount']");
    private final By grandTotalInput    = By.xpath("//input[@formcontrolname='grandtotal']");

    // ─── Form Action Buttons ──────────────────────────────────────────────────
    private final By saveDraftBtn       = By.xpath("//button[text()='Save As Draft']");
    private final By submitBtn          = By.xpath("//button[@type='submit' and normalize-space(.)='Submit']");
    private final By cancelBtn          = By.cssSelector("button.btn-primary.btn-sm.text-white.me-4");
    private final By draftHistoryBtn    = By.cssSelector("button.btn.btn-icon.btn-sm.bg-secondary.cursor-pointer.me-3");

    /**
     * Constructs the DirectPO page object and inherits BasePage functionality.
     *
     * @param driver The Selenium WebDriver instance
     */
    public DirectPO(WebDriver driver) {
        super(driver);
    }

    /**
     * Opens the Direct PO modal and waits until the form is fully loaded.
     *
     * @param node ExtentTest node used to attach step logs
     */
    public void openDirectPOModal(ExtentTest node) {
        ReportManager.setTest(node);
        PerformanceLogger.start("openDirectPOModal");

        step(Layer.UI, "Open Direct PO modal", () -> {
            waitForOverlayClear();
            click(directPOButton, "Direct PO button");
            return null;
        });

        step(Layer.UI, "Wait for modal to load", () -> {
            waitUntilVisible(branchNameDropdown, "Branch dropdown");
            return null;
        });

        PerformanceLogger.end("openDirectPOModal");
    }

    /**
     * Retrieves all line items from the displayed PO table.
     *
     * @return List of product name and quantity as PurchaseOrderLine
     */
    public List<PurchaseOrderLine> getLineItems() {
        List<PurchaseOrderLine> lines = new ArrayList<>();
        List<WebElement> rows = driver.findElements(
                By.cssSelector("table#yourPOtableId tbody tr")
        );
        for (WebElement row : rows) {
            String name = row.findElement(By.cssSelector("td.productColumn")).getText().trim();
            int qty = Integer.parseInt(row.findElement(By.cssSelector("td.qtyColumn")).getText().trim());
            lines.add(new PurchaseOrderLine(name, qty));
        }
        return lines;
    }

    /**
     * Fills the Direct PO form fields using dynamic test data.
     *
     * @param d    Data object representing the PO to be filled
     * @param node ExtentTest reporting node
     */
    public void fillForm(PurchaseOrderData d, ExtentTest node) {
        ReportManager.setTest(node);
        PerformanceLogger.start("DirectPO_fillForm");

        group("Fill Direct PO form", () -> {
            waitForOverlayClear();

            /**
             * Each step() logs a UI interaction into ExtentReport with a description.
             * It's part of custom logging via MasterLogger.
             */

            step(Layer.UI, "Fill header fields", () -> {
                selectFromNgSelect("branch_name", d.getBranchName());
                type(poRefNoInput, d.getPoRefNo(), "PO Ref No");
                FlatpickrDatePicker.pickDateAndClose(driver, poDatePicker, d.getPoDate().format(fmt), "PO Date");
                FlatpickrDatePicker.pickDateAndClose(driver, expectedDatePicker, d.getExpectedDate().format(fmt), "Expected Date");
                selectFromNgSelect("vendor_companyname", d.getVendorName());
                type(billToInput, d.getBillTo(), "Bill To");
                type(shipToInput, d.getShipTo(), "Ship To");
                selectFromNgSelect("employee_name", d.getRequestedBy());
                type(requestorContact, d.getRequestorContactDetails(), "Contact");
                type(deliveryTermsInput, d.getDeliveryTerms(), "Delivery Terms");
                type(paymentTermsInput, d.getPaymentTerms(), "Payment Terms");
                type(dispatchModeInput, d.getDispatchMode(), "Dispatch Mode");
                selectFromNgSelect("currency_code", d.getCurrency());
                type(exchangeRateInput, d.getExchangeRate() != null ? d.getExchangeRate().toPlainString() : "", "Exchange Rate");
                type(coverNoteInput, d.getCoverNote(), "Cover Note");
                return null;
            });

            if (d.isRenewal()) {
                step(Layer.UI, "Handle Renewal flow", () -> {
                    click(renewalYesRadio, "Renewal Yes");
                    FlatpickrDatePicker.pickDateAndClose(driver, renewalDatePicker, d.getRenewalDate().format(fmt), "Renewal Date");
                    selectFromNgSelect("frequency_terms", d.getFrequency());
                    return null;
                });
            } else {
                click(renewalNoRadio, "Renewal No");
            }

            // Loop through line items and fill product details
            List<LineItem> items = d.getLineItems();
            for (int i = 0; i < items.size(); i++) {
                int row = i + 1;
                LineItem it = items.get(i);

                group("Product row " + row, () -> {
                    click(addProductBtn, "Add Product");
                    selectFromNgSelect("product_name", it.getProductName());

                    // Manual description typing (textarea)
                    WebElement desc = findElement(descriptionInput);
                    desc.clear();
                    desc.sendKeys(it.getDescription());

                    type(quantityInput, String.valueOf(it.getQuantity()), "Quantity");
                    type(priceInput, it.getPrice() != null ? it.getPrice().toPlainString() : "0", "Price");

                    String discount = it.getDiscountPct() != null
                            ? it.getDiscountPct().toPlainString() : "0";
                    type(discountInput, discount, "Discount %");

                    click(saveProductBtn, "Save Product");
                    waitForOverlayClear();
                });
            }

            // Write custom terms using JS editor
            step(Layer.UI, "Fill Terms and Conditions", () -> {
                WebElement editor = findElement(termsEditor);
                jsExecutor.executeScript("arguments[0].scrollIntoView({block:'center'});", editor);
                jsExecutor.executeScript("arguments[0].innerHTML = arguments[1];", editor, d.getTermsEditorText());
                return null;
            });
        });

        PerformanceLogger.end("DirectPO_fillForm");
    }

    /**
     * Submits the PO form, waits for Angular stability and overlay clearance.
     *
     * @param submitNode Logging node for submission
     */
    public void submitDirectPO(ExtentTest submitNode) {
        ReportManager.setTest(submitNode);
        click(submitBtn, "Submit");
        waitForAngularRequestsToFinish();
        waitForOverlayClear();
    }

    /** Saves PO as Draft with performance logging. */
    public void saveDraft() {
        PerformanceLogger.start("saveDraft");
        step(Layer.UI, "Save Draft", () -> {
            click(saveDraftBtn, "Save Draft");
            return null;
        });
        PerformanceLogger.end("saveDraft");
    }

    /** Cancels the PO form and exits. */
    public void cancel() {
        PerformanceLogger.start("cancel");
        step(Layer.UI, "Cancel", () -> {
            click(cancelBtn, "Cancel");
            return null;
        });
        PerformanceLogger.end("cancel");
    }

    /** Opens the Draft History section for this PO. */
    public void viewDraftHistory() {
        PerformanceLogger.start("viewDraftHistory");
        step(Layer.UI, "View Draft History", () -> {
            click(draftHistoryBtn, "Draft History");
            return null;
        });
        PerformanceLogger.end("viewDraftHistory");
    }

    /**
     * Validates line total for given row index.
     *
     * @param rowIndex 0-based index of row
     * @param expected Expected total value
     */
    public void assertLineTotal(int rowIndex, BigDecimal expected) {
        PerformanceLogger.start("assertLineTotal");
        String actual = findElement(By.xpath("(//input[@formcontrolname='producttotal_amount'])[" + (rowIndex + 1) + "]")).getAttribute("value");
        if (!expected.toPlainString().equals(actual)) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
        PerformanceLogger.end("assertLineTotal");
    }

    /**
     * Validates net amount field matches expected value.
     */
    public void assertNetAmount(BigDecimal expected) {
        PerformanceLogger.start("assertNetAmount");
        String actual = findElement(netAmountInput).getAttribute("value");
        if (!expected.toPlainString().equals(actual)) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
        PerformanceLogger.end("assertNetAmount");
    }

    /**
     * Validates grand total field matches expected value.
     */
    public void assertGrandTotal(BigDecimal expected) {
        PerformanceLogger.start("assertGrandTotal");
        String actual = findElement(grandTotalInput).getAttribute("value");
        if (!expected.toPlainString().equals(actual)) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
        PerformanceLogger.end("assertGrandTotal");
    }
}