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
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DirectPO extends BasePage {
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final WebDriverWait wait;

    // Locators
    private final By loadingOverlay   = By.cssSelector(".spinner-overlay, .modal-backdrop");
    private final By directPOButton   = By.xpath("//button[contains(.,'Direct PO') and @data-bs-target='#myModaladd']");

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
    private final By submitBtn             = By.xpath("//button[contains(@class,'btn-success') and contains(@class,'text-white') and .//span[normalize-space()='Submit']]");
    private final By cancelBtn             = By.cssSelector("button.btn-primary.btn-sm.text-white.me-4");
    private final By draftHistoryBtn       = By.cssSelector("button.btn.btn-icon.btn-sm.bg-secondary.cursor-pointer.me-3");

    public DirectPO(WebDriver driver) {
        super(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        TestContextLogger.logTestStart("DirectPOPage init", driver);
    }

    /** Click the Direct PO button to open the form dialog */
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

            // Wait for the modal’s first field to appear
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
            // (content unchanged, your logic here)
        } catch (Exception e) {
            ErrorLogger.logException(e, "DirectPOPage.fillForm", driver);
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
    private void setDate(By locator, LocalDate date, String label, ExtentTest node) {
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