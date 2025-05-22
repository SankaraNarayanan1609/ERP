package com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order;

import com.Vcidex.StoryboardSystems.Common.Base.BasePage;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseOrderData;
import com.Vcidex.StoryboardSystems.Purchase.POJO.LineItem;
import com.Vcidex.StoryboardSystems.Utils.Logger.UIActionLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.ValidationLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.PerformanceLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.ErrorLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.TestContextLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.ExtentTestManager;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DirectPO extends BasePage {
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    //Click Direct PO Button
    private final By directPOButton = By.xpath("//button[@data-bs-target='#myModaladd' and normalize-space(.)='Direct PO']");

    /** NEW: click the Direct PO button to open the form dialog */

    // Header locators
    private final By branchNameDropdown    = By.xpath("//ng-select[@formcontrolname='branch_name']");
    private final By poRefNoInput          = By.xpath("//input[@formcontrolname='po_no']");
    private final By poDatePicker          = By.xpath("//input[@formcontrolname='po_date']");
    private final By expectedDatePicker    = By.xpath("//input[@formcontrolname='expected_date']");
    private final By vendorNameDropdown    = By.xpath("//ng-select[@formcontrolname='vendor_companyname']");
    private final By vendorDetailsInput    = By.xpath("//input[@formcontrolname='vendor_details']");
    private final By billToInput           = By.xpath("//textarea[@formcontrolname='address1']");
    private final By shipToInput           = By.xpath("//textarea[@formcontrolname='shipping_address']");
    private final By requestedByDropdown   = By.xpath("//ng-select[@formcontrolname='employee_name']");
    private final By requestorContactInput = By.xpath("//input[@formcontrolname='Requestor_details']");
    private final By deliveryTermsInput    = By.xpath("//input[@formcontrolname='delivery_terms']");
    private final By paymentTermsInput     = By.xpath("//input[@formcontrolname='payment_terms']");
    private final By dispatchModeInput     = By.xpath("//input[@formcontrolname='dispatch_mode']");
    private final By currencyDropdown      = By.xpath("//ng-select[@formcontrolname='currency_code']");
    private final By exchangeRateInput     = By.xpath("//input[@formcontrolname='exchange_rate']");
    private final By coverNoteInput        = By.xpath("//textarea[@formcontrolname='po_covernote']");
    private final By renewalYesRadio       = By.cssSelector("input[formcontrolname='renewal_mode'][value='Y']");
    private final By renewalNoRadio        = By.cssSelector("input[formcontrolname='renewal_mode'][value='N']");
    private final By renewalDatePicker     = By.xpath("//input[@formcontrolname='renewal_date']");
    private final By frequencyDropdown     = By.xpath("//ng-select[@formcontrolname='frequency_terms']");

    // Product‐grid locators
    private final By addProductBtn         = By.cssSelector("button.btn.btn-icon.btn-sm.bg-success.me-1");
    private final By productGroupDropdown  = By.xpath("//ng-select[@formcontrolname='productgroup_name']");
    private final By productCodeDropdown   = By.xpath("//ng-select[@formcontrolname='product_code']");
    private final By productNameDropdown   = By.xpath("//ng-select[@formcontrolname='product_name']");
    private final By descriptionInput      = By.xpath("//textarea[@formcontrolname='product_remarks']");
    private final By quantityInput         = By.xpath("//input[@formcontrolname='productquantity']");
    private final By priceInput            = By.xpath("//input[@formcontrolname='unitprice']");
    private final By discountInput         = By.xpath("//input[@formcontrolname='productdiscount']");
    private final By discountValueInput    = By.xpath("//input[@formcontrolname='productdiscount_amountvalue']");
    private final By taxPrefixDropdown     = By.xpath("//ng-select[@formcontrolname='tax_prefix']");
    private final By taxRateInput          = By.xpath("//input[@formcontrolname='taxamount1']");
    private final By totalAmountInput      = By.xpath("//input[@formcontrolname='producttotal_amount']");
    private final By saveProductBtn        = addProductBtn;
    private final By editProductBtn        = By.cssSelector("button.btn.btn-icon.btn-sm.bg-etdark.me-2");
    private final By deleteProductBtn      = By.cssSelector("button.btn.btn-icon.btn-sm.bg-danger.me-2");

    // Footer locators
    private final By termsDropdown         = By.xpath("//ng-select[@formcontrolname='template_name']");
    private final By termsEditor           = By.cssSelector(".angular-editor-wrapper");
    private final By netAmountInput        = By.xpath("//input[@formcontrolname='totalamount']");
    private final By addOnChargesInput     = By.xpath("//input[@formcontrolname='addoncharge']");
    private final By additionalDiscountInput = By.xpath("//input[@formcontrolname='additional_discount']");
    private final By freightChargesInput   = By.xpath("//input[@formcontrolname='freightcharges']");
    private final By additionalTaxDropdown = By.xpath("//ng-select[@formcontrolname='tax_name4']");
    private final By roundOffInput         = By.xpath("//input[@formcontrolname='roundoff']");
    private final By grandTotalInput       = By.xpath("//input[@formcontrolname='grandtotal']");

    // Action buttons
    private final By saveDraftBtn          = By.xpath("//button[text()='Save As Draft']");
    private final By submitBtn             = By.xpath("//button[@type='submit']");
    private final By cancelBtn             = By.cssSelector("button.btn-primary.btn-sm.text-white.me-4");
    private final By draftHistoryBtn       = By.cssSelector("button.btn.btn-icon.btn-sm.bg-secondary.cursor-pointer.me-3");

    public DirectPO(WebDriver driver) {
        super(driver);
        TestContextLogger.logTestStart("DirectPOPage init", driver);
    }

    /** NEW: click the Direct PO button to open the form dialog */
    public void openDirectPOModal(ExtentTest node) {
        PerformanceLogger.start("openDirectPOModal");
        try {
            UIActionLogger.click(driver, directPOButton, "Open Direct PO Modal", node);
            // wait for the form’s first field to be visible (adjust as needed)
            wait.until(ExpectedConditions.visibilityOfElementLocated(branchNameDropdown));
            node.pass("✅ Direct PO modal opened");
        } catch (Exception e) {
            ErrorLogger.logException(e, "DirectPOPage.openDirectPOModal", driver);
            throw e;
        } finally {
            PerformanceLogger.end("openDirectPOModal");
        }
    }


    /**
     * Fill the Direct PO form, grouped into three report sections:
     *   1) DetailsGroup
     *   2) ProductGroup
     *   3) FinGroup
     */


    public void fillForm(PurchaseOrderData d, ExtentTest rootNode) {
        PerformanceLogger.start("DirectPO_fillForm");
        try {
            // ─── 1) DetailsGroup ─────────────────────────────────────────────────
            ExtentTest detailsNode = rootNode.createNode("🔧 Details Group");
            UIActionLogger.click(driver, branchNameDropdown,    "Select Branch", detailsNode);
            selectByText(branchNameDropdown, d.getBranchName());
            setDate(poDatePicker,       d.getPoDate(),       "PO Date",        detailsNode);
            setDate(expectedDatePicker, d.getExpectedDate(), "Expected Date",  detailsNode);

            UIActionLogger.click(driver, vendorNameDropdown,   "Select Vendor",  detailsNode);
            selectByText(vendorNameDropdown, d.getVendorName());
            UIActionLogger.type(driver, vendorDetailsInput,    d.getVendorDetails(), "Vendor Details", detailsNode);
            UIActionLogger.type(driver, billToInput,           d.getBillTo(),        "Bill To",        detailsNode);
            UIActionLogger.type(driver, shipToInput,           d.getShipTo(),        "Ship To",        detailsNode);

            UIActionLogger.click(driver, requestedByDropdown,  "Requestor",         detailsNode);
            selectByText(requestedByDropdown, d.getRequestedBy());
            UIActionLogger.type(driver, requestorContactInput, d.getRequestorContactDetails(), "Requestor Contact", detailsNode);

            UIActionLogger.type(driver, deliveryTermsInput,    d.getDeliveryTerms(), "Delivery Terms", detailsNode);
            UIActionLogger.type(driver, paymentTermsInput,     d.getPaymentTerms(),  "Payment Terms",  detailsNode);
            UIActionLogger.type(driver, dispatchModeInput,     d.getDispatchMode(),  "Dispatch Mode",  detailsNode);

            UIActionLogger.click(driver, currencyDropdown,     "Currency",          detailsNode);
            selectByText(currencyDropdown, d.getCurrency());
            UIActionLogger.type(driver, exchangeRateInput,    d.getExchangeRate().toPlainString(), "Exchange Rate", detailsNode);

            UIActionLogger.type(driver, coverNoteInput,        d.getCoverNote(),    "Cover Note",     detailsNode);

            if (d.isRenewal()) {
                UIActionLogger.click(driver, renewalYesRadio,  "Renewal Yes",       detailsNode);
                setDate(renewalDatePicker, d.getRenewalDate(), "Renewal Date",     detailsNode);
                UIActionLogger.click(driver, frequencyDropdown,"Frequency",         detailsNode);
                selectByText(frequencyDropdown, d.getFrequency());
            } else {
                UIActionLogger.click(driver, renewalNoRadio,   "Renewal No",        detailsNode);
            }

            // ─── 2) ProductGroup ─────────────────────────────────────────────────
            ExtentTest productNode = rootNode.createNode("📦 Product Group");
            for (int i = 0; i < d.getLineItems().size(); i++) {
                LineItem li = d.getLineItems().get(i);

                UIActionLogger.click(driver, addProductBtn,      "Add Product", productNode);
                selectByText(productGroupDropdown, li.getProductGroup());
                selectByText(productCodeDropdown,  li.getProductCode());
                selectByText(productNameDropdown,  li.getProductName());

                UIActionLogger.type(driver, descriptionInput,    li.getDescription(),      "Description",      productNode);
                UIActionLogger.type(driver, quantityInput,       String.valueOf(li.getQuantity()), "Quantity", productNode);
                UIActionLogger.type(driver, priceInput,          li.getPrice().toPlainString(),    "Price",    productNode);
                UIActionLogger.type(driver, discountInput,       li.getDiscountPct().toPlainString(), "Discount%", productNode);
                UIActionLogger.type(driver, discountValueInput,  li.getDiscountAmt().toPlainString(),  "Discount Amt", productNode);

                UIActionLogger.click(driver, taxPrefixDropdown,  "Tax Prefix", productNode);
                selectByText(taxPrefixDropdown, li.getTaxPrefix());
                UIActionLogger.type(driver, taxRateInput,        li.getTaxRate().toPlainString(),  "Tax Rate", productNode);

                UIActionLogger.click(driver, saveProductBtn,     "Save Product", productNode);
                waitUntilVisible(totalAmountInput);

                li.computeTotal();
                ValidationLogger.assertEquals(
                        "Line total row " + i,
                        li.getTotalAmount().toPlainString(),
                        findElement(By.xpath("(//input[@formcontrolname='producttotal_amount'])[" + (i+1) + "]"))
                                .getAttribute("value"),
                        productNode
                );
            }

            // ─── 3) FinGroup ────────────────────────────────────────────────────
            ExtentTest finNode = rootNode.createNode("🏁 Financials Group");
            UIActionLogger.click(driver, termsDropdown,       "Select T&C",     finNode);
            selectByText(termsDropdown, d.getTermsAndConditions());
            findElement(termsEditor).sendKeys(d.getTermsEditorText());
            waitUntilVisible(netAmountInput);

            UIActionLogger.type(driver, addOnChargesInput,      d.getAddOnCharges().toPlainString(),      "Add-On Charges",      finNode);
            UIActionLogger.type(driver, additionalDiscountInput, d.getAdditionalDiscount().toPlainString(), "Additional Discount", finNode);
            UIActionLogger.type(driver, freightChargesInput,     d.getFreightCharges().toPlainString(),     "Freight Charges",    finNode);

            UIActionLogger.click(driver, additionalTaxDropdown,"Additional Tax", finNode);
            selectByText(additionalTaxDropdown, d.getAdditionalTax());
            UIActionLogger.type(driver, roundOffInput,         d.getRoundOff().toPlainString(),          "Round Off",          finNode);

            ValidationLogger.assertEquals(
                    "Net Amount",
                    d.getNetAmount().toPlainString(),
                    findElement(netAmountInput).getAttribute("value"),
                    finNode
            );
            ValidationLogger.assertEquals(
                    "Grand Total",
                    d.getGrandTotal().toPlainString(),
                    findElement(grandTotalInput).getAttribute("value"),
                    finNode
            );

        } catch (Exception e) {
            ErrorLogger.logException(e, "DirectPOPage.fillForm", driver);
            throw e;
        } finally {
            PerformanceLogger.end("DirectPO_fillForm");
        }
    }

    /** Overload: submit under given node */
    public String submitAndCaptureRef(ExtentTest node) {
        PerformanceLogger.start("submitAndCaptureRef");
        try {
            UIActionLogger.click(driver, submitBtn, "Submit PO", node);
            waitUntilVisible(poRefNoInput);
            String ref = getAttribute(poRefNoInput, "value");
            node.info("Captured PO Ref: " + ref);
            return ref;
        } catch (Exception e) {
            ErrorLogger.logException(e, "DirectPOPage.submitAndCaptureRef", driver);
            throw e;
        } finally {
            PerformanceLogger.end("submitAndCaptureRef");
        }
    }

    /** Backwards‐compatible: submit under root test */
    public String submitAndCaptureRef() {
        return submitAndCaptureRef(ExtentTestManager.getTest());
    }

    public void saveDraft() {
        PerformanceLogger.start("saveDraft");
        try {
            UIActionLogger.click(driver, saveDraftBtn, "Save Draft");
        } catch (Exception e) {
            ErrorLogger.logException(e, "DirectPOPage.saveDraft", driver);
            throw e;
        } finally {
            PerformanceLogger.end("saveDraft");
        }
    }

    public void cancel() {
        PerformanceLogger.start("cancel");
        try {
            UIActionLogger.click(driver, cancelBtn, "Cancel");
        } catch (Exception e) {
            ErrorLogger.logException(e, "DirectPOPage.cancel", driver);
            throw e;
        } finally {
            PerformanceLogger.end("cancel");
        }
    }

    public void viewDraftHistory() {
        PerformanceLogger.start("viewDraftHistory");
        try {
            UIActionLogger.click(driver, draftHistoryBtn, "View Draft History");
        } catch (Exception e) {
            ErrorLogger.logException(e, "DirectPOPage.viewDraftHistory", driver);
            throw e;
        } finally {
            PerformanceLogger.end("viewDraftHistory");
        }
    }

    public void assertLineTotal(int rowIndex, BigDecimal expected) {
        PerformanceLogger.start("assertLineTotal");
        try {
            By locator = By.xpath("(//input[@formcontrolname='producttotal_amount'])[" + (rowIndex+1) + "]");
            ValidationLogger.assertEquals(
                    "Line[" + rowIndex + "] total",
                    expected.toPlainString(),
                    findElement(locator).getAttribute("value"),
                    driver
            );
        } finally {
            PerformanceLogger.end("assertLineTotal");
        }
    }

    public void assertNetAmount(BigDecimal expected) {
        PerformanceLogger.start("assertNetAmount");
        try {
            ValidationLogger.assertEquals(
                    "NetAmount",
                    expected.toPlainString(),
                    findElement(netAmountInput).getAttribute("value"),
                    driver
            );
        } finally {
            PerformanceLogger.end("assertNetAmount");
        }
    }

    public void assertGrandTotal(BigDecimal expected) {
        PerformanceLogger.start("assertGrandTotal");
        try {
            ValidationLogger.assertEquals(
                    "GrandTotal",
                    expected.toPlainString(),
                    findElement(grandTotalInput).getAttribute("value"),
                    driver
            );
        } finally {
            PerformanceLogger.end("assertGrandTotal");
        }
    }

    // Helper: type a date then ENTER, logs into the given node
    private void setDate(By locator, LocalDate date, String label, ExtentTest node) {
        PerformanceLogger.start("setDate");
        try {
            String formatted = date.format(fmt); // Cannot resolve symbol 'fmt'
            UIActionLogger.type(driver, locator, formatted, label, node);
            findElement(locator).sendKeys(Keys.ENTER);
        } finally {
            PerformanceLogger.end("setDate");
        }
    }
}