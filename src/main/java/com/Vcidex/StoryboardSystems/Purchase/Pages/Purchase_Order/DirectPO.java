// File: src/main/java/com/Vcidex/StoryboardSystems/Purchase/Pages/Purchase_Order/DirectPO.java
package com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order;

import static com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger.step;
import static com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger.group;

import com.Vcidex.StoryboardSystems.Common.BasePage;
import com.Vcidex.StoryboardSystems.Purchase.POJO.LineItem;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseOrderData;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseOrderLine;
import com.Vcidex.StoryboardSystems.Utils.FlatpickrDatePicker;
import com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger.Layer;
import com.Vcidex.StoryboardSystems.Utils.Logger.PerformanceLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.ReportManager;
import com.aventstack.extentreports.ExtentTest;

import org.openqa.selenium.*;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DirectPO extends BasePage {
    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // ─── Locators ────────────────────────────────────────────────────────────
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
    private final By renewalYesRadio    = By.cssSelector("input[formcontrolname='renewal_mode'][value='Y']");
    private final By renewalNoRadio     = By.cssSelector("input[formcontrolname='renewal_mode'][value='N']");
    private final By renewalDatePicker  = By.xpath("//input[@formcontrolname='renewal_date']");
    private final By frequencyDropdown  = By.xpath("//ng-select[@formcontrolname='frequency_terms']");
    private final By addProductBtn      = By.cssSelector("button.btn.btn-icon.btn-sm.bg-success.me-1");
    private final By descriptionInput   = By.xpath("//textarea[@formcontrolname='product_remarks']");
    private final By quantityInput      = By.xpath("//input[@formcontrolname='productquantity']");
    private final By priceInput         = By.xpath("//input[@formcontrolname='unitprice']");
    private final By discountInput      = By.xpath("//input[@formcontrolname='productdiscount']");
    private final By saveProductBtn     = addProductBtn;
    private final By termsDropdown      = By.xpath("//ng-select[@formcontrolname='template_name']");
    private final By termsEditor        = By.cssSelector(".angular-editor-wrapper");
    private final By netAmountInput     = By.xpath("//input[@formcontrolname='totalamount']");
    private final By grandTotalInput    = By.xpath("//input[@formcontrolname='grandtotal']");
    private final By saveDraftBtn       = By.xpath("//button[text()='Save As Draft']");
    private final By submitBtn          = By.xpath("//button[@type='submit' and normalize-space(.)='Submit']");
    private final By cancelBtn          = By.cssSelector("button.btn-primary.btn-sm.text-white.me-4");
    private final By draftHistoryBtn    = By.cssSelector("button.btn.btn-icon.btn-sm.bg-secondary.cursor-pointer.me-3");

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

        step(Layer.UI, "Wait for Direct PO modal", () -> {
            waitUntilVisible(branchNameDropdown, "Branch dropdown");
            return null;
        });

        PerformanceLogger.end("openDirectPOModal");
    }
    public List<PurchaseOrderLine> getLineItems() {
        List<PurchaseOrderLine> lines = new ArrayList<>();
        List<WebElement> rows = driver.findElements(
                By.cssSelector("table#yourPOtableId tbody tr")
        );
        for (WebElement r : rows) {
            String name = r.findElement(By.cssSelector("td.productColumn"))
                    .getText().trim();
            int    qty  = Integer.parseInt(
                    r.findElement(By.cssSelector("td.qtyColumn"))
                            .getText().trim()
            );
            lines.add(new PurchaseOrderLine(name, qty));
        }
        return lines;
    }

    public void fillForm(PurchaseOrderData d, ExtentTest node) {
        ReportManager.setTest(node);
        PerformanceLogger.start("DirectPO_fillForm");

        group("Fill Direct PO form", () -> {
            waitForOverlayClear();

            step(Layer.UI, "Select branch = " + d.getBranchName(), () -> {
                selectFromNgSelect("branch_name", d.getBranchName());
                return null;
            });
            step(Layer.UI, "Type PO Ref No = " + d.getPoRefNo(), () -> {
                type(poRefNoInput, d.getPoRefNo(), "PO Ref No");
                return null;
            });
            step(Layer.UI, "Pick PO Date = " + d.getPoDate().format(fmt), () -> {
                FlatpickrDatePicker.pickDateAndClose(driver, poDatePicker, d.getPoDate().format(fmt), "PO Date");
                return null;
            });
            step(Layer.UI, "Pick Expected Date = " + d.getExpectedDate().format(fmt), () -> {
                FlatpickrDatePicker.pickDateAndClose(driver, expectedDatePicker, d.getExpectedDate().format(fmt), "Expected Date");
                return null;
            });
            step(Layer.UI, "Select vendor = " + d.getVendorName(), () -> {
                selectFromNgSelect("vendor_companyname", d.getVendorName());
                return null;
            });
            step(Layer.UI, "Type Bill To = " + d.getBillTo(), () -> {
                type(billToInput, d.getBillTo(), "Bill To");
                return null;
            });
            step(Layer.UI, "Type Ship To = " + d.getShipTo(), () -> {
                type(shipToInput, d.getShipTo(), "Ship To");
                return null;
            });
            step(Layer.UI, "Select Requested By = " + d.getRequestedBy(), () -> {
                selectFromNgSelect("employee_name", d.getRequestedBy());
                return null;
            });
            step(Layer.UI, "Type Requestor Contact = " + d.getRequestorContactDetails(), () -> {
                type(requestorContact, d.getRequestorContactDetails(), "Requestor Contact");
                return null;
            });
            step(Layer.UI, "Type Delivery Terms = " + d.getDeliveryTerms(), () -> {
                type(deliveryTermsInput, d.getDeliveryTerms(), "Delivery Terms");
                return null;
            });
            step(Layer.UI, "Type Payment Terms = " + d.getPaymentTerms(), () -> {
                type(paymentTermsInput, d.getPaymentTerms(), "Payment Terms");
                return null;
            });
            step(Layer.UI, "Type Dispatch Mode = " + d.getDispatchMode(), () -> {
                type(dispatchModeInput, d.getDispatchMode(), "Dispatch Mode");
                return null;
            });
            step(Layer.UI, "Select Currency = " + d.getCurrency(), () -> {
                selectFromNgSelect("currency_code", d.getCurrency());
                return null;
            });
            step(Layer.UI, "Type Exchange Rate = " +
                    (d.getExchangeRate() != null ? d.getExchangeRate().toPlainString() : ""), () -> {
                type(exchangeRateInput,
                        d.getExchangeRate() != null ? d.getExchangeRate().toPlainString() : "",
                        "Exchange Rate");
                return null;
            });
            step(Layer.UI, "Type Cover Note = " + d.getCoverNote(), () -> {
                type(coverNoteInput, d.getCoverNote(), "Cover Note");
                return null;
            });

            if (d.isRenewal()) {
                step(Layer.UI, "Select Renewal Yes", () -> { click(renewalYesRadio, "Renewal Yes"); return null; });
                step(Layer.UI, "Pick Renewal Date = " + d.getRenewalDate().format(fmt), () -> {
                    FlatpickrDatePicker.pickDateAndClose(driver, renewalDatePicker, d.getRenewalDate().format(fmt), "Renewal Date");
                    return null;
                });
                step(Layer.UI, "Select Frequency = " + d.getFrequency(), () -> {
                    selectFromNgSelect("frequency_terms", d.getFrequency());
                    return null;
                });
            } else {
                step(Layer.UI, "Select Renewal No", () -> { click(renewalNoRadio, "Renewal No"); return null; });
            }

            List<LineItem> items = d.getLineItems();
            for (int i = 0; i < items.size(); i++) {
                int row = i + 1;
                LineItem it = items.get(i);
                group("Product row " + row, () -> {
                    step(Layer.UI, "Add product row " + row, () -> { click(addProductBtn, "Add product row " + row); return null; });
                    step(Layer.UI, "Select product = " + it.getProductName(), () -> {
                        selectFromNgSelect("product_name", it.getProductName());
                        return null;
                    });
                    WebElement desc = findElement(descriptionInput);
                    desc.clear();
                    desc.sendKeys(it.getDescription());
                    step(Layer.UI, "Type Description = " + it.getDescription(), () -> null);
                    step(Layer.UI, "Type Quantity = " + it.getQuantity(), () -> {
                        type(quantityInput, String.valueOf(it.getQuantity()), "Quantity");
                        return null;
                    });
                    step(Layer.UI, "Type Price = " + it.getPrice(), () -> {
                        type(priceInput, it.getPrice() != null ? it.getPrice().toPlainString() : "0", "Price");
                        return null;
                    });
                    step(Layer.UI, "Type Discount % = " + it.getDiscountPct(), () -> {
                        String pct = it.getDiscountPct() != null
                                ? it.getDiscountPct().toPlainString()
                                : "0";
                        type(discountInput, pct, "Discount %");
                        return null;
                    });

                    step(Layer.UI, "Save product row " + row, () -> { click(saveProductBtn, "Save product row " + row); return null; });
                    waitForOverlayClear();
                });
            }

            step(Layer.UI, "Type terms editor text = " + d.getTermsEditorText(), () -> {
                WebElement editor = findElement(termsEditor);
                jsExecutor.executeScript("arguments[0].scrollIntoView({block:'center', inline:'center'});", editor);
                jsExecutor.executeScript("arguments[0].innerHTML = arguments[1];", editor, d.getTermsEditorText());
                return null;
            });
        });

        PerformanceLogger.end("DirectPO_fillForm");
    }

    /**
     * Clicks Submit, waits for Angular & overlays, then reads the PO Ref off the input.
     */
    /**
     * Clicks Submit, waits for Angular & overlays. No longer tries to read back the input.
     */
    public void submitDirectPO(ExtentTest submitNode) {
        ReportManager.setTest(submitNode);

        click(submitBtn, "Submit");
        waitForAngularRequestsToFinish();
        waitForOverlayClear();
    }

    public void saveDraft() {
        PerformanceLogger.start("saveDraft");
        step(Layer.UI, "Save Draft", () -> {
            click(saveDraftBtn, "Save Draft");
            return null;
        });
        PerformanceLogger.end("saveDraft");
    }

    public void cancel() {
        PerformanceLogger.start("cancel");
        step(Layer.UI, "Cancel", () -> {
            click(cancelBtn, "Cancel");
            return null;
        });
        PerformanceLogger.end("cancel");
    }

    public void viewDraftHistory() {
        PerformanceLogger.start("viewDraftHistory");
        step(Layer.UI, "View Draft History", () -> {
            click(draftHistoryBtn, "View Draft History");
            return null;
        });
        PerformanceLogger.end("viewDraftHistory");
    }

    public void assertLineTotal(int rowIndex, BigDecimal expected) {
        PerformanceLogger.start("assertLineTotal");
        String actual = findElement(
                By.xpath("(//input[@formcontrolname='producttotal_amount'])[" + (rowIndex + 1) + "]")
        ).getAttribute("value");
        if (!expected.toPlainString().equals(actual)) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
        PerformanceLogger.end("assertLineTotal");
    }

    public void assertNetAmount(BigDecimal expected) {
        PerformanceLogger.start("assertNetAmount");
        String actual = findElement(netAmountInput).getAttribute("value");
        if (!expected.toPlainString().equals(actual)) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
        PerformanceLogger.end("assertNetAmount");
    }

    public void assertGrandTotal(BigDecimal expected) {
        PerformanceLogger.start("assertGrandTotal");
        String actual = findElement(grandTotalInput).getAttribute("value");
        if (!expected.toPlainString().equals(actual)) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
        PerformanceLogger.end("assertGrandTotal");
    }
}