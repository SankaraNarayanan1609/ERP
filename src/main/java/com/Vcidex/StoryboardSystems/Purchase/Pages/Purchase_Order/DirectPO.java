package com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order;

import com.Vcidex.StoryboardSystems.Common.BasePage;
import com.Vcidex.StoryboardSystems.Utils.FlatpickrDatePicker;
import com.Vcidex.StoryboardSystems.Purchase.POJO.LineItem;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseOrderData;
import com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger.Layer;
import com.Vcidex.StoryboardSystems.Utils.Logger.PerformanceLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.ReportManager;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger.*;

public class DirectPO extends BasePage {
    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // ─── Locators ───────────────────────────────────────────────────────────
    private final By directPOButton     = By.xpath("//button[contains(.,'Direct PO') and @data-bs-target='#myModaladd']");
    private final By branchNameDropdown = By.xpath("//ng-select[@formcontrolname='branch_name']");
    private final By poRefNoInput       = By.xpath("//input[@formcontrolname='po_no']");
    private final By poDatePicker       = By.xpath("//input[@formcontrolname='po_date']");
    private final By expectedDatePicker = By.xpath("//input[@formcontrolname='expected_date']");
    private final By vendorNameDropdown = By.xpath("//ng-select[@formcontrolname='vendor_companyname']");
    private final By billToInput        = By.xpath("//textarea[@formcontrolname='address1']");
    private final By shipToInput        = By.xpath("//textarea[@formcontrolname='shipping_address']");
    private final By requestorContact   = By.xpath("//input[@formcontrolname='Requestor_details']");
    private final By deliveryTermsInput = By.xpath("//input[@formcontrolname='delivery_terms']");
    private final By paymentTermsInput  = By.xpath("//input[@formcontrolname='payment_terms']");
    private final By dispatchModeInput  = By.xpath("//input[@formcontrolname='dispatch_mode']");
    private final By exchangeRateInput  = By.xpath("//input[@formcontrolname='exchange_rate']");
    private final By coverNoteInput     = By.xpath("//textarea[@formcontrolname='po_covernote']");

    private final By renewalYesRadio    = By.cssSelector("input[formcontrolname='renewal_mode'][value='Y']");
    private final By renewalNoRadio     = By.cssSelector("input[formcontrolname='renewal_mode'][value='N']");
    private final By renewalDatePicker  = By.xpath("//input[@formcontrolname='renewal_date']");
    private final By frequencyDropdown  = By.xpath("//ng-select[@formcontrolname='frequency_terms']");

    private final By addProductBtn      = By.xpath("//button[contains(@class,'bg-success') and .//i[contains(@class,'fa-plus')]]");
    private final By descriptionInput   = By.xpath("//textarea[@formcontrolname='product_remarks']");
    private final By quantityInput      = By.xpath("//input[@formcontrolname='productquantity']");
    private final By priceInput         = By.xpath("//input[@formcontrolname='unitprice']");
    private final By discountInput      = By.xpath("//input[@formcontrolname='productdiscount']");
    private final By termsEditor        = By.cssSelector(".angular-editor-wrapper");
    private final By submitBtn          = By.xpath("//button[@type='submit' and normalize-space(.)='Submit']");

    // NEW: scoped “save” button inside each row
    private final By saveRowBtn        = By.cssSelector("button.btn.btn-icon.btn-sm.bg-success");

    private final List<LineItem> addedLineItems = new ArrayList<>();
    private static final By summaryRows = By.cssSelector(
            ".modal.show table.table-striped.table-bordered tbody tr:not(:first-child)"
    );

    public DirectPO(WebDriver driver) {
        super(driver);
    }

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

    public void fillForm(PurchaseOrderData d, ExtentTest node) {
        ReportManager.setTest(node);
        PerformanceLogger.start("DirectPO_fillForm");
        group("Fill Direct PO form", () -> {
            step(Layer.UI, "Fill header", () -> fillHeader(d));
            if (d.isRenewal()) {
                group("Renewal flow", () -> {
                    step("Click Renewal Yes", wrap(() -> click(renewalYesRadio, "Renewal Yes")));
                    step("Pick Renewal Date", wrap(() ->
                            FlatpickrDatePicker.pickDateAndClose(driver, renewalDatePicker,
                                    d.getRenewalDate().format(fmt), "Renewal Date")
                    ));
                    step("Select Frequency", wrap(() ->
                            selectFromNgSelect("frequency_terms", d.getFrequency())
                    ));
                });
            } else {
                step("Click Renewal No", wrap(() -> click(renewalNoRadio, "Renewal No")));
            }

            // 3) line items
            waitForOverlayClear();
            List<LineItem> items = d.getLineItems();
            for (int i = 0; i < items.size(); i++) {
                int row = i + 1;
                LineItem it = items.get(i);
                group("Product row " + row, () -> addLineItem(it));
                addedLineItems.add(it);
            }

            step(Layer.UI, "Set Terms & Conditions", () ->
                    setInnerHtml(termsEditor, d.getTermsEditorText())
            );
        });
        PerformanceLogger.end("DirectPO_fillForm");
    }

    public void submitDirectPO(ExtentTest submitNode) {
        ReportManager.setTest(submitNode);
        click(submitBtn, "Submit");
        waitForAngularRequestsToFinish();
        waitForOverlayClear();
    }

    // ─── Helpers ────────────────────────────────────────────────────────────
    private void fillHeader(PurchaseOrderData d) {
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
        type(exchangeRateInput, d.getExchangeRate().toPlainString(), "Exchange Rate");
        type(coverNoteInput, d.getCoverNote(), "Cover Note");
        waitForOverlayClear();
    }

    private void addLineItem(LineItem it) {
        // 1) open new blank row
        click(addProductBtn, "Add Product");
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                descriptionInput, addedLineItems.size()));

        // 2) locate the <tr> we just added
        WebElement descField = driver.findElements(descriptionInput)
                .get(addedLineItems.size());
        WebElement inputRow  = descField.findElement(By.xpath("./ancestor::tr"));

        // 3) row-level ng-select helper (fixed to target the real <ng-dropdown-panel>)
        selectFromNgSelectInRow(inputRow, "product_name", it.getProductName());

        // 4) dismiss any stray calendar
        jsExecutor.executeScript(
                "window.dispatchEvent(new KeyboardEvent('keydown',{key:'Escape'}));");
        waitForOverlayClear();

        // 5) fill description, qty, price, discount
        inputRow.findElement(descriptionInput)
                .sendKeys(it.getDescription());
        inputRow.findElement(quantityInput)
                .sendKeys(String.valueOf(it.getQuantity()));
        inputRow.findElement(priceInput)
                .sendKeys(it.getPrice().toPlainString());
        inputRow.findElement(discountInput)
                .sendKeys(it.getDiscountPct() != null
                        ? it.getDiscountPct().toPlainString()
                        : "0");

        // 6) guard: wait until our Qty & selected‐product label appear
        WebElement qtyField     = inputRow.findElement(quantityInput);
        WebElement selectedProd = inputRow.findElement(
                By.cssSelector("ng-select[formcontrolname='product_name'] .ng-value")
        );

        String actualProd     = selectedProd.getText().trim();
        String actualQty      = qtyField.getAttribute("value").trim();
        String actualPrice    = inputRow.findElement(priceInput)
                .getAttribute("value").trim();
        String actualDiscount = inputRow.findElement(discountInput)
                .getAttribute("value").trim();

        if (actualProd.isEmpty()
                || actualQty.isEmpty()
                || actualPrice.isEmpty()
                || actualDiscount.isEmpty()) {
            throw new IllegalStateException(String.format(
                    "Cannot save line—missing data: product='%s', qty='%s', price='%s', discount='%s'",
                    actualProd, actualQty, actualPrice, actualDiscount
            ));
        }

        // finally click the scoped Save
        safeClick(inputRow.findElement(saveRowBtn));

        // 8) final summary-table check
        String expectedTax = it.getTaxPrefix();
        wait.withTimeout(Duration.ofSeconds(10))
                .pollingEvery(Duration.ofMillis(300))
                .until(d -> {
                    List<WebElement> rows = d.findElements(summaryRows);
                    return rows.size() == addedLineItems.size() + 1
                            && rows.get(rows.size() - 1)
                            .getText().contains(expectedTax);
                });

        // 9) record it
        addedLineItems.add(it);
        log.info("✅ Product row added successfully: {} (tax={})",
                it.getProductName(), expectedTax);
    }

    private void setInnerHtml(By locator, String html) {
        WebElement e = findElement(locator);
        jsExecutor.executeScript(
                "arguments[0].scrollIntoView({block:'center'});" +
                        "arguments[0].innerHTML=arguments[1];",
                e, html
        );
    }

    public List<LineItem> getLineItems() {
        return new ArrayList<>(addedLineItems);
    }
}