package com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order;

import com.Vcidex.StoryboardSystems.Common.BasePage;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseOrderData;
import com.Vcidex.StoryboardSystems.Purchase.POJO.LineItem;
import com.Vcidex.StoryboardSystems.Utils.FlatpickrDatePicker;
import com.Vcidex.StoryboardSystems.Utils.Logger.ErrorLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.PerformanceLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.UIActionLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.ValidationLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.TestContextLogger;
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
     * Product-grid locators
     */
    private final By addProductBtn           = By.cssSelector("button.btn.btn-icon.btn-sm.bg-success.me-1");
    private final By productNameDropdownBulk = By.xpath("//ng-select[@formcontrolname='product_name']");
    private final By descriptionInput        = By.xpath("//textarea[@formcontrolname='product_remarks']");
    private final By quantityInput           = By.xpath("//input[@formcontrolname='productquantity']");
    private final By priceInput              = By.xpath("//input[@formcontrolname='unitprice']");
    private final By discountInput           = By.xpath("//input[@formcontrolname='productdiscount']");
    private final By saveProductBtn          = addProductBtn; // same CSS

    /**
     * Footer locators
     */
    private final By termsDropdown           = By.xpath("//ng-select[@formcontrolname='template_name']");
    private final By termsEditor             = By.cssSelector(".angular-editor-wrapper");
    private final By netAmountInput          = By.xpath("//input[@formcontrolname='totalamount']");
    private final By addOnChargesInput       = By.xpath("//input[@formcontrolname='addoncharge']");
    private final By additionalDiscountInput = By.xpath("//input[@formcontrolname='additional_discount']");
    private final By freightChargesInput     = By.xpath("//input[@formcontrolname='freightcharges']");
    private final By roundOffInput           = By.xpath("//input[@formcontrolname='roundoff']");
    private final By grandTotalInput         = By.xpath("//input[@formcontrolname='grandtotal']");

    /**
     * Action buttons
     */
    private final By saveDraftBtn = By.xpath("//button[text()='Save As Draft']");

    /**
     * “Submit” button locator adjusted to match <button type="submit">Submit</button>
     */
    private final By submitBtn = By.xpath("//button[@type='submit' and normalize-space(.)='Submit']");
    private final By cancelBtn = By.cssSelector("button.btn-primary.btn-sm.text-white.me-4");
    private final By draftHistoryBtn = By.cssSelector("button.btn.btn-icon.btn-sm.bg-secondary.cursor-pointer.me-3");

    public DirectPO(WebDriver driver) {
        super(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        TestContextLogger.logTestStart("DirectPOPage init", driver); // Cannot resolve method 'logTestStart' in 'TestContextLogger'
    }

    public void openDirectPOModal(ExtentTest node) {
        PerformanceLogger.start("openDirectPOModal");
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(loadingOverlay));
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(directPOButton));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", btn);
            try {
                UIActionLogger.click(driver, directPOButton, "Open Direct PO Modal", node);
            } catch (ElementClickInterceptedException ex) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
                node.info("⚡ Fallback JS click used to open Direct PO modal");
            }
            wait.until(ExpectedConditions.visibilityOfElementLocated(branchNameDropdown));
            node.pass("✅ Direct PO modal opened");
        } catch (Exception e) {
            ErrorLogger.logException(e, "DirectPOPage.openDirectPOModal", driver);
            throw e;
        } finally {
            PerformanceLogger.end("openDirectPOModal");
        }
    }

    private void pickAndCloseDate(By dateLocator, String dateStr, String fieldName) {
        FlatpickrDatePicker.pickFlatpickrDate(driver, dateLocator, dateStr, fieldName);

        // Immediately send ESC to make sure Flatpickr is fully closed
        WebElement dateInput = driver.findElement(dateLocator);
        dateInput.sendKeys(Keys.ESCAPE);

        // Wait until any Flatpickr calendar panel is gone (just in case)
        By flatpickrPanel = By.cssSelector(".flatpickr-calendar, .flatpickr-monthDropdown-months");
        wait.until(ExpectedConditions.invisibilityOfElementLocated(flatpickrPanel));

        // Finally click a neutral spot (e.g. header div) to ensure nothing is intercepting
        try {
            WebElement header = driver.findElement(By.cssSelector(".card-title"));
            header.click();
        } catch (NoSuchElementException ignore) {
            // If header not found, proceed anyway
        }

        // Tiny pause so Angular can settle any leftover overlays
        try { Thread.sleep(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    private void scrollAboveFooter(WebElement element, int footerHeightPx) {
        String script =
                "const el = arguments[0];\n" +
                        "const footerHeight = arguments[1];\n" +
                        "const rect = el.getBoundingClientRect();\n" +
                        "const viewportHeight = window.innerHeight || document.documentElement.clientHeight;\n" +
                        "const desiredViewportY = (viewportHeight - footerHeight) / 2;\n" +
                        "const delta = rect.top - desiredViewportY;\n" +
                        "window.scrollBy(0, delta);\n";
        ((JavascriptExecutor) driver).executeScript(script, element, footerHeightPx);
    }

    public void fillForm(PurchaseOrderData d, ExtentTest rootNode) {
        PerformanceLogger.start("DirectPO_fillForm");
        try {
            // ─── PRE‐HEADER WAIT & SCROLL ───
            try {
                WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(2));
                shortWait.until(ExpectedConditions.presenceOfElementLocated(loadingOverlay));
                rootNode.info("ℹ️ Overlay appeared, now waiting up to 20s for it to disappear...");
                wait.until(ExpectedConditions.invisibilityOfElementLocated(loadingOverlay));
                rootNode.info("✅ Overlay is now gone.");
            } catch (TimeoutException t) {
                rootNode.info("ℹ️ No overlay found; skipping overlay‐invisibility wait.");
            }

            rootNode.info("ℹ️ Waiting for branchNameDropdown to be visible...");
            WebElement branchElem = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(branchNameDropdown)
            );
            rootNode.info("✅ Found branch dropdown. Scrolling into view...");
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({ block: 'center' });", branchElem
            );

            // ─── HEADER FIELDS ───
            UIActionLogger.selectFromNgSelect(driver, "branch_name", d.getBranchName().trim(), rootNode);
            UIActionLogger.type(driver, poRefNoInput, d.getPoRefNo(), "PO Ref No", rootNode);

            String poDateStr = d.getPoDate().format(fmt);
            pickAndCloseDate(poDatePicker, poDateStr, "PO Date");

            String expectedDateStr = d.getExpectedDate().format(fmt);
            pickAndCloseDate(expectedDatePicker, expectedDateStr, "Expected Date");

            UIActionLogger.selectFromNgSelect(driver, "vendor_companyname", d.getVendorName().trim(), rootNode);
            UIActionLogger.type(driver, billToInput, d.getBillTo(), "Bill To", rootNode);
            UIActionLogger.type(driver, shipToInput, d.getShipTo(), "Ship To", rootNode);
            UIActionLogger.selectFromNgSelect(driver, "employee_name", d.getRequestedBy(), rootNode);
            UIActionLogger.type(driver, requestorContactInput, d.getRequestorContactDetails(), "Requestor Contact", rootNode);
            UIActionLogger.type(driver, deliveryTermsInput, d.getDeliveryTerms(), "Delivery Terms", rootNode);
            UIActionLogger.type(driver, paymentTermsInput, d.getPaymentTerms(), "Payment Terms", rootNode);
            UIActionLogger.type(driver, dispatchModeInput, d.getDispatchMode(), "Dispatch Mode", rootNode);
            UIActionLogger.selectFromNgSelect(driver, "currency_code", d.getCurrency(), rootNode);
            UIActionLogger.type(
                    driver,
                    exchangeRateInput,
                    d.getExchangeRate() != null ? d.getExchangeRate().toPlainString() : "",
                    "Exchange Rate",
                    rootNode
            );
            UIActionLogger.type(driver, coverNoteInput, d.getCoverNote(), "Cover Note", rootNode);

            // ─── RENEWAL (if present) ───
            if (d.isRenewal()) {
                findElement(renewalYesRadio).click();
                String renewalDateStr = d.getRenewalDate().format(fmt);
                pickAndCloseDate(renewalDatePicker, renewalDateStr, "Renewal Date");
                UIActionLogger.selectFromNgSelect(driver, "frequency_terms", d.getFrequency(), rootNode);
            } else {
                findElement(renewalNoRadio).click();
            }

            // ─── PRODUCT GRID ───
            List<LineItem> lineItems = d.getLineItems();
            By productSelectLocator = By.xpath("//ng-select[@formcontrolname='product_name']");

            if (!lineItems.isEmpty()) {
                WebElement firstAddBtn = wait.until(
                        ExpectedConditions.elementToBeClickable(addProductBtn)
                );
                scrollAboveFooter(firstAddBtn, 100);
                try {
                    UIActionLogger.click(driver, addProductBtn, "Add Product Row → row 1", rootNode);
                } catch (ElementClickInterceptedException ex) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", firstAddBtn);
                    rootNode.info("⚡ Fallback JS click used for Add Product row 1");
                }
                wait.until(ExpectedConditions.invisibilityOfElementLocated(loadingOverlay));

                wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(productSelectLocator, 0));
                WebElement firstRowDropdown = wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.xpath("(//ng-select[@formcontrolname='product_name'])[1]")
                        )
                );
                scrollAboveFooter(firstRowDropdown, 100);
                try { Thread.sleep(200); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
            }

            for (int i = 0; i < lineItems.size(); i++) {
                int rowIndex = i + 1; // 1-based
                LineItem item = lineItems.get(i);

                if (i > 0) {
                    WebElement addBtn = wait.until(
                            ExpectedConditions.elementToBeClickable(addProductBtn)
                    );
                    scrollAboveFooter(addBtn, 100);
                    try {
                        UIActionLogger.click(driver, addProductBtn, "Add Product Row → row " + rowIndex, rootNode);
                    } catch (ElementClickInterceptedException ex) {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", addBtn);
                        rootNode.info("⚡ Fallback JS click used for Add Product row " + rowIndex);
                    }
                    wait.until(ExpectedConditions.invisibilityOfElementLocated(loadingOverlay));

                    // wait for the new row's product dropdown to be clickable
                    By thisRowDropdown = By.xpath(
                            "(//ng-select[@formcontrolname='product_name'])[" + rowIndex + "]"
                    );
                            WebElement newRowDropdown = wait.until(
                                ExpectedConditions.elementToBeClickable(thisRowDropdown)
                            );
                            scrollAboveFooter(newRowDropdown, 100);
                    try { Thread.sleep(200); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
                }

                fillOneProductRow(
                        rowIndex,
                        item.getProductName().trim(),
                        String.valueOf(item.getQuantity()),
                        item.getPrice() != null ? item.getPrice().toPlainString() : "0",
                        rootNode
                );
            }

            // ─── FINAL SCROLL & CLICK “Submit” ───

            // (a) Wait for any overlays/spinners to disappear
            By anyOverlay = By.cssSelector(".spinner-overlay, .modal-backdrop, .blockUI");
            wait.until(ExpectedConditions.invisibilityOfElementLocated(anyOverlay));

            // (b) Wait until the Submit button is clickable
            wait.until(ExpectedConditions.elementToBeClickable(submitBtn));

            // (c) Re-grab the Submit element right before clicking (avoid StaleElementReference)
            WebElement freshSubmit = driver.findElement(submitBtn);

            // (d) Scroll the Submit button into view (centered)
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({ block: 'center' });",
                    freshSubmit
            );

            // (e) Tiny pause so that any sticky footer or Angular rendering can finish
            try {
                Thread.sleep(150);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }

            // (f) Finally attempt a normal click; if intercepted, fall back to JS click
            try {
                UIActionLogger.click(driver, submitBtn, "Submit PO", rootNode);
            } catch (ElementClickInterceptedException ex) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", freshSubmit);
                rootNode.info("⚡ Fallback JS click used for Submit PO");
            }

            rootNode.info("✅ DirectPO form successfully submitted.");

        } catch (Exception e) {
            ErrorLogger.logException(e, "DirectPOPage.fillForm", driver);
            rootNode.fail("❌ Exception in fillForm: " + e.getMessage());
            throw e;
        } finally {
            PerformanceLogger.end("DirectPO_fillForm");
        }
    }

    private void fillOneProductRow(int rowIndex, String productName,
                                   String quantity, String price, ExtentTest node) {
        By dropdownContainer = By.xpath(
                "(//ng-select[@formcontrolname='product_name'])[" + rowIndex + "]" +
                        "//div[contains(@class,'ng-select-container')]"
        );
        WebElement container = wait.until(ExpectedConditions.elementToBeClickable(dropdownContainer));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({ block: 'center' });", container
        );
        try {
            container.click();
        } catch (ElementClickInterceptedException ex) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", container);
            node.info("⚡ Fallback JS click to open product dropdown (row " + rowIndex + ")");
        }

        By filterInput = By.xpath(
                "(//ng-select[@formcontrolname='product_name'])[" + rowIndex + "]" +
                        "//input[@role='combobox' or @aria-autocomplete='list']"
        );
        WebElement inputEl = wait.until(ExpectedConditions.visibilityOfElementLocated(filterInput));
        inputEl.clear();
        inputEl.sendKeys(productName);
        node.info("✏️ Typed '" + productName + "' into filter (row " + rowIndex + ")");

        inputEl.sendKeys(Keys.ARROW_DOWN);
        inputEl.sendKeys(Keys.ENTER);
        node.info("⏎ Sent Arrow_Down + Enter to select the first match (row " + rowIndex + ")");

        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.cssSelector("div.ng-dropdown-panel")
        ));

        By qtyForRow   = By.xpath("(//input[@formcontrolname='productquantity'])[" + rowIndex + "]");
        By priceForRow = By.xpath("(//input[@formcontrolname='unitprice'])[" + rowIndex + "]");

        WebElement qtyEl  = wait.until(ExpectedConditions.elementToBeClickable(qtyForRow));
        WebElement priceEl = wait.until(ExpectedConditions.visibilityOfElementLocated(priceForRow));

        qtyEl.clear();
        qtyEl.sendKeys(quantity);
        node.info("✏️ Typed quantity '" + quantity + "' for row " + rowIndex);

        if (priceEl.isEnabled()) {
            priceEl.clear();
            priceEl.sendKeys(price);
            node.info("✏️ Typed unit price '" + price + "' for row " + rowIndex);
        } else {
            node.info("↳ Price field is read-only for row " + rowIndex + "; skipping.");
        }

        By saveBtnForRow = By.xpath(
                "(//button[contains(@class,'btn-icon') and contains(@class,'bg-success')])[" + rowIndex + "]"
        );
        WebElement saveBtn = wait.until(ExpectedConditions.elementToBeClickable(saveBtnForRow));
        scrollAboveFooter(saveBtn, 100);
        try {
            saveBtn.click();
        } catch (ElementClickInterceptedException ex) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", saveBtn);
            node.info("⚡ Fallback JS click on Save (row " + rowIndex + ")");
        }
        node.info("💾 Clicked Save for row " + rowIndex);

        wait.until(ExpectedConditions.invisibilityOfElementLocated(loadingOverlay));
    }

    public void submitPO(ExtentTest node) {
        PerformanceLogger.start("submitPO");
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(loadingOverlay));

            // Wait until “Submit” is clickable
            wait.until(ExpectedConditions.elementToBeClickable(submitBtn));
            WebElement btn = driver.findElement(submitBtn);

            boolean isDisplayed = btn.isDisplayed();
            boolean isEnabled = btn.isEnabled();
            node.info("⏱ [DEBUG] Submit isDisplayed=" + isDisplayed + ", isEnabled=" + isEnabled);

            scrollAboveFooter(btn, 100);

            // Extra small loop to ensure it really became enabled
            long endTime = System.currentTimeMillis() + 3_000;
            while (System.currentTimeMillis() < endTime && !btn.isEnabled()) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
                btn = driver.findElement(submitBtn);
            }
            node.info("⏱ [DEBUG] After waiting, submit.isEnabled=" + btn.isEnabled());

            try {
                UIActionLogger.click(driver, submitBtn, "Submit PO", node);
            } catch (ElementClickInterceptedException ex) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
                node.info("⚡ Fallback JS click used for Submit PO");
            }

            node.info("✅ Submit button click attempted");
        }
        catch (Exception e) {
            ErrorLogger.logException(e, "DirectPOPage.submitPO", driver);
            throw e;
        }
        finally {
            PerformanceLogger.end("submitPO");
        }
    }

    public String submitAndCaptureRef(ExtentTest node) {
        PerformanceLogger.start("submitAndCaptureRef");
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(loadingOverlay));

            wait.until(ExpectedConditions.elementToBeClickable(submitBtn));
            WebElement btn = driver.findElement(submitBtn);
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({ block: 'center' });", btn
            );

            UIActionLogger.click(driver, submitBtn, "Submit PO", node);

            // Now wait for the inline card to disappear by checking that branch dropdown is gone
            wait.until(ExpectedConditions.invisibilityOfElementLocated(branchNameDropdown));
            node.info("✅ Direct PO form closed");

            // Back on the summary page, capture the new PO # from the first row of the grid:
            By firstRowPORef = By.xpath("//table[@id='purchaseOrderTbl']//tbody/tr[1]/td[2]");
            WebElement poCell = wait.until(ExpectedConditions.visibilityOfElementLocated(firstRowPORef));
            wait.until(drv -> !poCell.getText().trim().isEmpty());

            String poRef = poCell.getText().trim();
            node.info("Captured new PO # from summary table: " + poRef);
            return poRef;
        }
        catch (Exception e) {
            ErrorLogger.logException(e, "DirectPOPage.submitAndCaptureRef", driver);
            throw e;
        }
        finally {
            PerformanceLogger.end("submitAndCaptureRef");
        }
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
}