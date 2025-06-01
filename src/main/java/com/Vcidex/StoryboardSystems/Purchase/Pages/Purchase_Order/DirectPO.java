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
import com.Vcidex.StoryboardSystems.Utils.FlatpickrDatePicker;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DirectPO extends BasePage {
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final WebDriverWait wait;

    /**
     * Locators
     */
    private final By loadingOverlay   = By.cssSelector(".spinner-overlay, .modal-backdrop");
    private final By directPOButton   = By.xpath("//button[contains(.,'Direct PO') and @data-bs-target='#myModaladd']");

    /**
     * Header locators
     */
    private final By branchNameDropdown    = By.xpath("//ng-select[@formcontrolname='branch_name']");
    private final By poRefNoInput          = By.xpath("//input[@formcontrolname='po_no']");
    private final By poDatePicker          = By.xpath("//input[@formcontrolname='po_date']");
    private final By expectedDatePicker    = By.xpath("//input[@formcontrolname='expected_date']");
    private final By vendorNameDropdown    = By.xpath("//ng-select[@formcontrolname='vendor_companyname']");

    //private final By vendorDetailsInput    = By.xpath("//input[@formcontrolname='vendor_details']");
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

    /**
     *Product-grid locators
     */
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

    /**
     * Footer locators
     */
    private final By termsDropdown         = By.xpath("//ng-select[@formcontrolname='template_name']");
    private final By termsEditor           = By.cssSelector(".angular-editor-wrapper");
    private final By netAmountInput        = By.xpath("//input[@formcontrolname='totalamount']");
    private final By addOnChargesInput     = By.xpath("//input[@formcontrolname='addoncharge']");
    private final By additionalDiscountInput = By.xpath("//input[@formcontrolname='additional_discount']");
    private final By freightChargesInput   = By.xpath("//input[@formcontrolname='freightcharges']");
    private final By additionalTaxDropdown = By.xpath("//ng-select[@formcontrolname='tax_name4']");
    private final By roundOffInput         = By.xpath("//input[@formcontrolname='roundoff']");
    private final By grandTotalInput       = By.xpath("//input[@formcontrolname='grandtotal']");

    /**
     *     Action buttons
     */
    private final By saveDraftBtn          = By.xpath("//button[text()='Save As Draft']");
    private final By submitBtn             = By.xpath("//button[contains(@class,'btn-success') and contains(@class,'text-white') and .//span[normalize-space()='Submit']]");
    private final By cancelBtn             = By.cssSelector("button.btn-primary.btn-sm.text-white.me-4");
    private final By draftHistoryBtn       = By.cssSelector("button.btn.btn-icon.btn-sm.bg-secondary.cursor-pointer.me-3");

    public DirectPO(WebDriver driver) {
        super(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        TestContextLogger.logTestStart("DirectPOPage init", driver);
    }

    /**
     * Click the Direct PO button to open the Direct PO page
     **/
    public void openDirectPOModal(ExtentTest node) {
        PerformanceLogger.start("openDirectPOModal");
        try {
            // Wait for any overlay/spinner to vanish
            wait.until(ExpectedConditions.invisibilityOfElementLocated(loadingOverlay));

            // Scroll the button into view
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(directPOButton));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", btn);

            // Try normal click, fallback to JS if intercepted
            try {
                UIActionLogger.click(driver, directPOButton, "Open Direct PO Modal", node);
            } catch (ElementClickInterceptedException ex) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
                node.info("⚡ Fallback JS click used to open Direct PO modal");
            }

            /**
             * Wait for the modal’s first field to appear
              */
            wait.until(ExpectedConditions.visibilityOfElementLocated(branchNameDropdown));
            node.pass("✅ Direct PO modal opened");
        } catch (Exception e) {
            ErrorLogger.logException(e, "DirectPOPage.openDirectPOModal", driver);
            throw e;
        } finally {
            PerformanceLogger.end("openDirectPOModal");
        }
    }

    public void fillForm(PurchaseOrderData d, ExtentTest rootNode) {
        PerformanceLogger.start("DirectPO_fillForm");
        try {
            // ───────────── HEADER FIELDS ─────────────
            UIActionLogger.selectFromNgSelect(driver, "branch_name", d.getBranchName(), rootNode);
            UIActionLogger.type(driver, poRefNoInput, d.getPoRefNo(), "PO Ref No", rootNode);

            String expectedDateStr = d.getExpectedDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            FlatpickrDatePicker.pickFlatpickrDate(driver, expectedDatePicker, expectedDateStr, "Expected Date", rootNode);

            UIActionLogger.selectFromNgSelect(driver, "vendor_companyname", d.getVendorName(), rootNode);
            UIActionLogger.type(driver, billToInput, d.getBillTo(), "Bill To", rootNode);
            UIActionLogger.type(driver, shipToInput, d.getShipTo(), "Ship To", rootNode);
            UIActionLogger.selectFromNgSelect(driver, "employee_name", d.getRequestedBy(), rootNode);
            UIActionLogger.type(driver, requestorContactInput, d.getRequestorContactDetails(), "Requestor Contact", rootNode);

            UIActionLogger.type(driver, deliveryTermsInput, d.getDeliveryTerms(), "Delivery Terms", rootNode);
            UIActionLogger.type(driver, paymentTermsInput, d.getPaymentTerms(), "Payment Terms", rootNode);
            UIActionLogger.type(driver, dispatchModeInput, d.getDispatchMode(), "Dispatch Mode", rootNode);

            UIActionLogger.selectFromNgSelect(driver, "currency_code", d.getCurrency(), rootNode);
            UIActionLogger.type(driver, exchangeRateInput,
                    d.getExchangeRate() != null ? d.getExchangeRate().toPlainString() : "",
                    "Exchange Rate", rootNode);

            UIActionLogger.type(driver, coverNoteInput, d.getCoverNote(), "Cover Note", rootNode);

            // ───────────── RENEWAL (if present) ─────────────
            if (d.isRenewal()) {
                findElement(renewalYesRadio).click();
                String renewalDateStr = d.getRenewalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                FlatpickrDatePicker.pickFlatpickrDate(driver, renewalDatePicker, renewalDateStr, "Renewal Date", rootNode);
                UIActionLogger.selectFromNgSelect(driver, "frequency_terms", d.getFrequency(), rootNode);
            } else {
                findElement(renewalNoRadio).click();
            }

            // ───────────── LINE ITEMS ─────────────
            List<LineItem> lineItems = d.getLineItems();
            for (int i = 0; i < lineItems.size(); i++) {
                if (i > 0) {
                    UIActionLogger.click(driver, addProductBtn, "Add Product Row", rootNode);
                }
                LineItem item = lineItems.get(i);

                //
                // ── MANUAL ng-select, but don’t block on a non-existent <input>
                //
                // STEP A: Locate & scroll the <ng-select> container into view
                By productNameContainer = By.xpath(
                        "//ng-select[@formcontrolname='product_name']" +
                                "//div[contains(@class,'ng-select-container')]"
                );
                WebElement dropdownElement = findElement(productNameContainer);

                String scrollScript =
                        "var el = arguments[0];" +
                                "var rect = el.getBoundingClientRect();" +
                                "var targetY = window.pageYOffset + rect.top - (window.innerHeight * 0.3);" +
                                "window.scrollTo(0, targetY);";
                ((JavascriptExecutor) driver).executeScript(scrollScript, dropdownElement);

                try { Thread.sleep(150); } catch (InterruptedException ignored) {}

                // STEP B: Click the container to open the dropdown
                dropdownElement.click();

                /**
                *STEP C: If there is a search <input ng-input> inside the panel, type into it.
                 *       If not, skip typing and go straight to clicking the option.
                */
                String trimmedName = item.getProductName() == null ? "" : item.getProductName().trim();
                By ngSearchInput = By.xpath(
                        "//body//div[contains(@class,'ng-dropdown-panel')]//input[contains(@class,'ng-input')]"
                );
                try {
                    WebElement searchInput = wait.until(
                            ExpectedConditions.visibilityOfElementLocated(ngSearchInput)
                    );

                    // Clear any leftovers and type the product name
                    searchInput.clear();
                    searchInput.sendKeys(trimmedName);
                } catch (TimeoutException e) {

                    // If no searchable input appeared—just proceed to click the option by text.
                    rootNode.info("ℹ️ No search-box inside ng-dropdown-panel; skipping typing step.");
                }

                // STEP D: Wait for an <ng-option> whose text contains our trimmedName, then click it.
                By matchingOption = By.xpath(
                        "//body//div[contains(@class,'ng-dropdown-panel')]" +
                                "//div[contains(@class,'ng-option') and contains(normalize-space(.),'" + trimmedName + "')]"
                );
                WebElement option = wait.until(
                        ExpectedConditions.elementToBeClickable(matchingOption)
                );
                option.click();

                /**
                * ── END manual ng-select sequence
                */

                // ───────── Fill the rest of that row’s fields ─────────
                UIActionLogger.type(driver, descriptionInput, item.getDescription(), "Description", rootNode);
                UIActionLogger.type(driver, quantityInput, String.valueOf(item.getQuantity()), "Quantity", rootNode);

                UIActionLogger.type(
                        driver,
                        priceInput,
                        item.getPrice() != null ? item.getPrice().toPlainString() : "",
                        "Unit Price",
                        rootNode
                );
                UIActionLogger.type(
                        driver,
                        discountInput,
                        item.getDiscountPct() != null ? item.getDiscountPct().toPlainString() : "",
                        "Discount %",
                        rootNode
                );
                
                // (add any other fields like discountValue/taxPrefix/taxRate as needed)
                UIActionLogger.click(driver, saveProductBtn, "Save Product", rootNode);
            }

            // ───────────── FOOTER / TOTALS ─────────────
            UIActionLogger.selectFromNgSelect(driver, "template_name", d.getTermsAndConditions(), rootNode);

            if (d.getTermsEditorText() != null && !d.getTermsEditorText().isEmpty()) {
                WebElement editor = findElement(termsEditor);
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].innerHTML = arguments[1];",
                        editor, d.getTermsEditorText()
                );
                rootNode.info("📝 Filled Terms Editor Text");
            }

            UIActionLogger.type(driver, addOnChargesInput,
                    d.getAddOnCharges() != null ? d.getAddOnCharges().toPlainString() : "",
                    "Add-on Charges", rootNode);
            UIActionLogger.type(driver, additionalDiscountInput,
                    d.getAdditionalDiscount() != null ? d.getAdditionalDiscount().toPlainString() : "",
                    "Additional Discount", rootNode);
//            UIActionLogger.type(driver, freightChargesInput,
//                    d.getFreightCharges() != null ? d.getFreightCharges().toPlainString() : "",
//                    "Freight Charges", rootNode);
            UIActionLogger.selectFromNgSelect(driver, "tax_name4", d.getAdditionalTax(), rootNode);

            rootNode.pass("✅ All form fields filled from PurchaseOrderData");

        } catch (Exception e) {
            ErrorLogger.logException(e, "DirectPOPage.fillForm", driver);
            rootNode.fail("❌ Exception in fillForm: " + e.getMessage());
            throw e;
        } finally {
            PerformanceLogger.end("DirectPO_fillForm");
        }
    }

    public String submitAndCaptureRef(ExtentTest node) {
        PerformanceLogger.start("submitAndCaptureRef");
        try {
            // Wait for overlays/spinners to disappear
            wait.until(ExpectedConditions.invisibilityOfElementLocated(loadingOverlay));

            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(submitBtn));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", btn);

            try {
                UIActionLogger.click(driver, submitBtn, "Submit PO", node);
            } catch (ElementClickInterceptedException ex) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
                node.info("⚡ Fallback JS click used to submit PO");
            }
            // Wait for PO number input as confirmation of success
            wait.until(ExpectedConditions.visibilityOfElementLocated(poRefNoInput));
            String ref = findElement(poRefNoInput).getAttribute("value");
            node.info("Captured PO Ref: " + ref);
            return ref;
        } catch (Exception e) {
            ErrorLogger.logException(e, "DirectPOPage.submitAndCaptureRef", driver);
            throw e;
        } finally {
            PerformanceLogger.end("submitAndCaptureRef");
        }
    }

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
            By locator = By.xpath("(//input[@formcontrolname='producttotal_amount'])[" + (rowIndex + 1) + "]");
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
    private void setDate(By locator, java.time.LocalDate date, String label, ExtentTest node) {
        PerformanceLogger.start("setDate");
        try {
            String formatted = date.format(fmt);
            UIActionLogger.type(driver, locator, formatted, label, node);
            findElement(locator).sendKeys(Keys.ENTER);
        } finally {
            PerformanceLogger.end("setDate");
        }
    }
}