// File: src/main/java/com/Vcidex/StoryboardSystems/Purchase/Pages/Purchase_Order/DirectPOPage.java
package com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order;

import com.Vcidex.StoryboardSystems.Common.Base.BasePage;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseOrderData;
import com.Vcidex.StoryboardSystems.Purchase.POJO.LineItem;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DirectPOPage extends BasePage {
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Header locators
    private final By branchNameDropdown      = By.xpath("//ng-select[@formcontrolname='branch_name']");
    private final By poRefNoInput            = By.xpath("//input[@formcontrolname='po_no']");
    private final By poDatePicker            = By.xpath("//input[@formcontrolname='po_date']");
    private final By expectedDatePicker      = By.xpath("//input[@formcontrolname='expected_date']");
    private final By vendorNameDropdown      = By.xpath("//ng-select[@formcontrolname='vendor_companyname']");
    private final By vendorDetailsInput      = By.xpath("//input[@formcontrolname='vendor_details']");
    private final By billToInput             = By.xpath("//textarea[@formcontrolname='address1']");
    private final By shipToInput             = By.xpath("//textarea[@formcontrolname='shipping_address']");
    private final By requestedByDropdown     = By.xpath("//ng-select[@formcontrolname='employee_name']");
    private final By requestorContactInput   = By.xpath("//input[@formcontrolname='Requestor_details']");
    private final By deliveryTermsInput      = By.xpath("//input[@formcontrolname='delivery_terms']");
    private final By paymentTermsInput       = By.xpath("//input[@formcontrolname='payment_terms']");
    private final By dispatchModeInput       = By.xpath("//input[@formcontrolname='dispatch_mode']");
    private final By currencyDropdown        = By.xpath("//ng-select[@formcontrolname='currency_code']");
    private final By exchangeRateInput       = By.xpath("//input[@formcontrolname='exchange_rate']");
    private final By coverNoteInput          = By.xpath("//textarea[@formcontrolname='po_covernote']");
    private final By renewalYesRadio         = By.cssSelector("input[formcontrolname='renewal_mode'][value='Y']");
    private final By renewalNoRadio          = By.cssSelector("input[formcontrolname='renewal_mode'][value='N']");
    private final By renewalDatePicker       = By.xpath("//input[@formcontrolname='renewal_date']");
    private final By frequencyDropdown       = By.xpath("//ng-select[@formcontrolname='frequency_terms']");

    // Product-grid locators
    private final By addProductBtn           = By.cssSelector("button.btn.btn-icon.btn-sm.bg-success.me-1");
    private final By productGroupDropdown    = By.xpath("//ng-select[@formcontrolname='productgroup_name']");
    private final By productCodeDropdown     = By.xpath("//ng-select[@formcontrolname='product_code']");
    private final By productNameDropdown     = By.xpath("//ng-select[@formcontrolname='product_name']");
    private final By descriptionInput        = By.xpath("//textarea[@formcontrolname='product_remarks']");
    private final By quantityInput           = By.xpath("//input[@formcontrolname='productquantity']");
    private final By priceInput              = By.xpath("//input[@formcontrolname='unitprice']");
    private final By discountInput           = By.xpath("//input[@formcontrolname='productdiscount']");
    private final By discountValueInput      = By.xpath("//input[@formcontrolname='productdiscount_amountvalue']");
    private final By taxPrefixDropdown       = By.xpath("//ng-select[@formcontrolname='tax_prefix']");
    private final By taxRateInput            = By.xpath("//input[@formcontrolname='taxamount1']");
    private final By totalAmountInput        = By.xpath("//input[@formcontrolname='producttotal_amount']");
    private final By saveProductBtn          = addProductBtn;
    // after addProductBtn
    private final By editProductBtn   = By.cssSelector("button.btn.btn-icon.btn-sm.bg-etdark.me-2");
    private final By deleteProductBtn = By.cssSelector("button.btn.btn-icon.btn-sm.bg-danger.me-2");


    // Footer locators
    private final By termsDropdown           = By.xpath("//ng-select[@formcontrolname='template_name']");
    private final By termsEditor             = By.cssSelector(".angular-editor-wrapper");
    private final By netAmountInput          = By.xpath("//input[@formcontrolname='totalamount']");
    private final By addOnChargesInput       = By.xpath("//input[@formcontrolname='addoncharge']");
    private final By additionalDiscountInput = By.xpath("//input[@formcontrolname='additional_discount']");
    private final By freightChargesInput     = By.xpath("//input[@formcontrolname='freightcharges']");
    private final By additionalTaxDropdown   = By.xpath("//ng-select[@formcontrolname='tax_name4']");
    private final By roundOffInput           = By.xpath("//input[@formcontrolname='roundoff']");
    private final By grandTotalInput         = By.xpath("//input[@formcontrolname='grandtotal']");

    // Action buttons
    private final By saveDraftBtn            = By.xpath("//button[text()='Save As Draft']");
    private final By submitBtn               = By.xpath("//button[@type='submit']");
    private final By cancelBtn               = By.cssSelector("button.btn-primary.btn-sm.text-white.me-4");
    private final By draftHistoryBtn         = By.cssSelector("button.btn.btn-icon.btn-sm.bg-secondary.cursor-pointer.me-3");

    public DirectPOPage(WebDriver driver) {
        super(driver);
    }

    /** Fills every field using data from the POJO. */
    public void fillForm(PurchaseOrderData d) {
        selectByText(branchNameDropdown,    d.getBranchName());
        setDate(poDatePicker,               d.getPoDate());
        setDate(expectedDatePicker,         d.getExpectedDate());
        selectByText(vendorNameDropdown,    d.getVendorName());
        type(vendorDetailsInput,            d.getVendorDetails());
        type(billToInput,                   d.getBillTo());
        type(shipToInput,                   d.getShipTo());
        selectByText(requestedByDropdown,   d.getRequestedBy());
        type(requestorContactInput,         d.getRequestorContactDetails());
        type(deliveryTermsInput,            d.getDeliveryTerms());
        type(paymentTermsInput,             d.getPaymentTerms());
        type(dispatchModeInput,             d.getDispatchMode());
        selectByText(currencyDropdown,      d.getCurrency());
        type(exchangeRateInput,             d.getExchangeRate().toPlainString());
        type(coverNoteInput,                d.getCoverNote());

        if (d.isRenewal()) {
            click(renewalYesRadio);
            setDate(renewalDatePicker, d.getRenewalDate());
            selectByText(frequencyDropdown, d.getFrequency());
        } else {
            click(renewalNoRadio);
        }

        for (LineItem li : d.getLineItems()) {
            click(addProductBtn);
            selectByText(productGroupDropdown, li.getProductGroup());
            selectByText(productCodeDropdown,  li.getProductCode());
            selectByText(productNameDropdown,  li.getProductName());
            type(descriptionInput,             li.getDescription());
            type(quantityInput,                String.valueOf(li.getQuantity()));
            type(priceInput,                   li.getPrice().toPlainString());
            type(discountInput,                li.getDiscountPct().toPlainString());
            type(discountValueInput,           li.getDiscountAmt().toPlainString());

            selectByText(taxPrefixDropdown,    li.getTaxRate().toPlainString());
            type(taxRateInput,                 li.getTaxRate().toPlainString());
            click(saveProductBtn);
            waitUntilVisible(totalAmountInput);
        }

        selectByText(termsDropdown,         d.getTermsAndConditions());
        findElement(termsEditor).sendKeys(d.getTermsEditorText());
        waitUntilVisible(netAmountInput);
        type(addOnChargesInput,             d.getAddOnCharges().toPlainString());
        type(additionalDiscountInput,       d.getAdditionalDiscount().toPlainString());
        type(freightChargesInput,           d.getFreightCharges().toPlainString());
        selectByText(additionalTaxDropdown, d.getAdditionalTax());
        type(roundOffInput,                 d.getRoundOff().toPlainString());
        assertNetAmount(d.getNetAmount());
        assertGrandTotal(d.getGrandTotal());
        // grandTotalInput is read-only
    }
    public void assertLineTotal(int rowIndex, BigDecimal expected) {
        By locator = By.xpath(
                "(//input[@formcontrolname='producttotal_amount'])[" + (rowIndex+1) + "]"
        );
        String actual = findElement(locator).getAttribute("value");
        if (!actual.equals(expected.toPlainString())) {
            throw new AssertionError(
                    "Line["+rowIndex+"] total mismatch: expected="+expected+" but was="+actual
            );
        }
    }

    public void editLastProduct(LineItem li) {
        click(editProductBtn);
        // fill fields as in add…
    }

    public void deleteLastProduct() {
        click(deleteProductBtn);
        // confirm modal if needed…
    }

    public void saveDraft() {
        click(saveDraftBtn);
    }

    public String submitAndCaptureRef() {
        click(submitBtn);
        waitUntilVisible(poRefNoInput);
        return getAttribute(poRefNoInput, "value");
    }

    /** Assert that the calculated net‐amount matches the POJO. */
    public void assertNetAmount(BigDecimal expected) {
        String actual = getAttribute(netAmountInput, "value");
        if (!actual.equals(expected.toPlainString())) {
            throw new AssertionError(
                    "NetAmount mismatch: expected="+expected+ " but was="+actual
            );
        }
    }

    /** Similarly for Grand Total */
    public void assertGrandTotal(BigDecimal expected) {
        String actual = getAttribute(grandTotalInput, "value");
        if (!actual.equals(expected.toPlainString())) {
            throw new AssertionError(
                    "GrandTotal mismatch: expected="+expected+ " but was="+actual
            );
        }
    }

    public void cancel() {
        click(cancelBtn);
    }

    public void viewDraftHistory() {
        click(draftHistoryBtn);
    }

    /** Helper: type a date string then ENTER for flatpickr */
    private void setDate(By locator, LocalDate date) {
        type(locator, date.format(fmt));
        findElement(locator).sendKeys(Keys.ENTER);
    }
}