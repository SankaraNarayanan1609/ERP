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

        import static com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger.*;

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

    private final List<LineItem> addedLineItems = new ArrayList<>();

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
    public void fillForm(PurchaseOrderData d, ExtentTest node) {
        ReportManager.setTest(node);
        PerformanceLogger.start("DirectPO_fillForm");

        // ─── Runtime Debugging for Initial Diagnosis ───────────────────────────────
        System.out.println("✅ Entered fillForm() for DirectPO");
        System.out.println("➡️  PO Ref No: " + d.getPoRefNo());
        System.out.println("➡️  Vendor Name: " + d.getVendorName());
        System.out.println("➡️  Branch Name: " + d.getBranchName());
        System.out.println("➡️  Currency: " + d.getCurrency());
        System.out.println("➡️  Product Line Items Count: " + (d.getLineItems() == null ? "NULL" : d.getLineItems().size()));
        System.out.println("➡️  Is Renewal Flow: " + d.isRenewal());

        // ─── Temporarily Skip Logic for Debugging ─────────────────────────────────
        // Comment everything below if you are only testing debug entry and data

        group("Fill Direct PO form", () -> {
            step("Wait for overlay to clear", wrap(this::waitForOverlayClear));

            step(Layer.UI, "Fill header fields", () -> {
                step("Select Branch", wrap(() -> {
                    System.out.println("🟡 Trying to select branch: " + d.getBranchName());
                    selectFromNgSelect("branch_name", d.getBranchName());
                    System.out.println("🟢 Branch selected");
                }));
                step("Type PO Ref No", wrap(() -> {
                    System.out.println("🟡 Typing PO Ref No: " + d.getPoRefNo());
                    type(poRefNoInput, d.getPoRefNo(), "PO Ref No");
                    System.out.println("🟢 PO Ref typed");
                }));                step("Pick PO Date", wrap(() -> FlatpickrDatePicker.pickDateAndClose(driver, poDatePicker, d.getPoDate().format(fmt), "PO Date")));
                step("Pick Expected Date", wrap(() -> FlatpickrDatePicker.pickDateAndClose(driver, expectedDatePicker, d.getExpectedDate().format(fmt), "Expected Date")));
                step("Select Vendor", wrap(() -> {
                    System.out.println("🟡 Trying to select vendor: " + d.getVendorName());
                    selectFromNgSelect("vendor_companyname", d.getVendorName());
                    System.out.println("🟢 Vendor selected");
                }));                step("Type Bill To", wrap(() -> type(billToInput, d.getBillTo(), "Bill To")));
                step("Type Ship To", wrap(() -> type(shipToInput, d.getShipTo(), "Ship To")));
                step("Select Requested By", wrap(() -> selectFromNgSelect("employee_name", d.getRequestedBy())));
                step("Type Requestor Contact", wrap(() -> type(requestorContact, d.getRequestorContactDetails(), "Contact")));
                step("Type Delivery Terms", wrap(() -> type(deliveryTermsInput, d.getDeliveryTerms(), "Delivery Terms")));
                step("Type Payment Terms", wrap(() -> type(paymentTermsInput, d.getPaymentTerms(), "Payment Terms")));
                step("Type Dispatch Mode", wrap(() -> type(dispatchModeInput, d.getDispatchMode(), "Dispatch Mode")));
                step("Select Currency", wrap(() -> selectFromNgSelect("currency_code", d.getCurrency())));
                step("Type Exchange Rate", wrap(() -> type(exchangeRateInput, d.getExchangeRate() != null ? d.getExchangeRate().toPlainString() : "", "Exchange Rate")));
                step("Type Cover Note", wrap(() -> type(coverNoteInput, d.getCoverNote(), "Cover Note")));
                return null;
            });

            if (d.isRenewal()) {
                group("Renewal flow", () -> {
                    step("Click Renewal Yes", wrap(() -> click(renewalYesRadio, "Renewal Yes")));
                    step("Pick Renewal Date", wrap(() -> FlatpickrDatePicker.pickDateAndClose(driver, renewalDatePicker, d.getRenewalDate().format(fmt), "Renewal Date")));
                    step("Select Frequency", wrap(() -> selectFromNgSelect("frequency_terms", d.getFrequency())));
                });
            } else {
                step("Click Renewal No", wrap(() -> click(renewalNoRadio, "Renewal No")));
            }

            List<LineItem> items = d.getLineItems();

            // 🔧 Debug: Print all line items being passed to UI form
            System.out.println("🔧 DEBUG: Line Items to be added = " + items.size());
            items.forEach(li -> System.out.println(" - " + li.getProductName() + " | " + li.getProductCode()));

            for (int i = 0; i < items.size(); i++) {
                int row = i + 1;
                LineItem it = items.get(i);

                group("Product row " + row, () -> {
                    step("Click Add Product", wrap(() -> click(addProductBtn, "Add Product")));
                    step("Select Product", wrap(() -> {
                        System.out.println("🔍 Attempting to select product: " + it.getProductName() + " | Code: " + it.getProductCode());
                        if (it.getProductName() == null || it.getProductName().trim().isEmpty()) {
                            System.out.println("❌ Product name is null or empty. Skipping this line item.");
                            return;
                        }
                        selectFromNgSelect("product_name", it.getProductName());
                        System.out.println("✅ Selected product: " + it.getProductName());
                    }));
                    step("Enter Description", wrap(() -> {
                        WebElement desc = findElement(descriptionInput);
                        desc.clear();
                        desc.sendKeys(it.getDescription());
                    }));
                    step("Enter Quantity", wrap(() -> type(quantityInput, String.valueOf(it.getQuantity()), "Quantity")));
                    step("Enter Price", wrap(() -> type(priceInput, it.getPrice() != null ? it.getPrice().toPlainString() : "0", "Price")));
                    step("Enter Discount", wrap(() -> {
                        String discount = it.getDiscountPct() != null ? it.getDiscountPct().toPlainString() : "0";
                        type(discountInput, discount, "Discount %");
                    }));
                    step("Click Save Product", wrap(() -> click(saveProductBtn, "Save Product")));
                    step("Wait for overlay", wrap(this::waitForOverlayClear));
                    addedLineItems.add(it);
                });
            }

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
        System.out.println("📄 [UI] Table row count = " + driver.findElements(By.cssSelector("table tbody tr")).size());
        driver.findElements(By.cssSelector("table tbody tr")).forEach(row -> {
            System.out.println("   - " + row.getText());
        });

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
    /**
     * Returns all line items that were added in the form.
     * Used for chaining data to Inward and Invoice steps.
     */
    public List<LineItem> getLineItems() {
        return new ArrayList<>(addedLineItems); // Return a defensive copy
    }
}