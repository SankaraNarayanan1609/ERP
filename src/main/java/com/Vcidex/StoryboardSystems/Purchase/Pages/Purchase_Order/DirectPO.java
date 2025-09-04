package com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order;

import com.Vcidex.StoryboardSystems.Common.BasePage;
import com.Vcidex.StoryboardSystems.Purchase.Logging.POLogFormatter;
import com.Vcidex.StoryboardSystems.Purchase.POJO.LineItem;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseOrderData;
import com.Vcidex.StoryboardSystems.Purchase.Support.PoNumberExtractor;
import com.Vcidex.StoryboardSystems.Purchase.Support.SuccessMessageReader;
import com.Vcidex.StoryboardSystems.Purchase.Support.PurchaseOrderMemory;
import com.Vcidex.StoryboardSystems.Utils.FlatpickrDatePicker;
import com.Vcidex.StoryboardSystems.Utils.Logger.DiagnosticsLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.PerformanceLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.ReportManager;
import com.Vcidex.StoryboardSystems.Utils.ToastAssertor;
import com.Vcidex.StoryboardSystems.Utils.ToastType;
import com.Vcidex.StoryboardSystems.Utils.ToastVerifier;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Optional;
import java.util.regex.*;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DirectPO extends BasePage {

    // ── initables (not final) so default ctor compiles
    private ExtentTest node;
    private PoNumberExtractor poExtractor;
    private SuccessMessageReader successReader;
    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    // Header locators
    private final By poRefNoInput       = By.xpath("//input[@formcontrolname='po_no']");
    private final By poDatePicker       = By.xpath("//input[@formcontrolname='po_date']");
    private final By expectedDatePicker = By.xpath("//input[@formcontrolname='expected_date']");
    private final By billToInput        = By.xpath("//textarea[@formcontrolname='address1']");
    private final By shipToInput        = By.xpath("//textarea[@formcontrolname='shipping_address']");
    private final By deliveryTermsInput = By.xpath("//input[@formcontrolname='delivery_terms']");
    private final By paymentTermsInput  = By.xpath("//input[@formcontrolname='payment_terms']");
    private final By requestorContact   = By.xpath("//input[@formcontrolname='Requestor_details']");
    private final By dispatchModeInput  = By.xpath("//input[@formcontrolname='dispatch_mode']");
    private final By exchangeRateInput  = By.xpath("//input[@formcontrolname='exchange_rate']");
    private final By coverNoteInput     = By.xpath("//textarea[@formcontrolname='po_covernote']");

    private final By renewalYesRadio    = By.cssSelector("input[formcontrolname='renewal_mode'][value='Y']");
    private final By renewalNoRadio     = By.cssSelector("input[formcontrolname='renewal_mode'][value='N']");
    private final By renewalDatePicker  = By.xpath("//input[@formcontrolname='renewal_date']");

    // Lines
    private final By addProductBtn  = By.xpath("//table[contains(@class,'table')]/tbody//button[contains(@class,'bg-success')][.//i[contains(@class,'fa-plus')]]");
    private final By descriptionInp = By.cssSelector("textarea[formcontrolname='product_remarks']");
    private final By quantityInp    = By.cssSelector("input[formcontrolname='productquantity']");
    private final By priceInp       = By.cssSelector("input[formcontrolname='unitprice']");
    private final By discountInp    = By.cssSelector("input[formcontrolname='productdiscount']");
    private final By summaryRows    = By.cssSelector("table.table-striped.table-bordered tbody tr");
    private final By entryRows      = By.xpath("//table[contains(@class,'table')]/tbody//tr[.//ng-select[@formcontrolname='product_name']]");

    // Footer
    private final By submitBtn      = By.xpath("//button[@type='submit' and .//span[normalize-space()='Submit']]");
    private final List<LineItem> addedLineItems = new ArrayList<>();

    /** Default ctor used by navigator – wire sensible defaults. */
    public DirectPO(WebDriver driver) {
        super(driver);
        this.node = ReportManager.getTest();
        this.successReader = SuccessMessageReader.defaultReader();
        this.poExtractor = defaultExtractor();
    }

    /** Injectable ctor for tests/customization. */
    public DirectPO(WebDriver driver, ExtentTest node, PoNumberExtractor extractor, SuccessMessageReader reader) {
        super(driver);
        this.node = node != null ? node : ReportManager.getTest();
        this.poExtractor = extractor != null ? extractor : defaultExtractor();
        this.successReader = reader != null ? reader : SuccessMessageReader.defaultReader();
    }

    // Convenience overload to match existing navigator usage
    public DirectPO(WebDriver driver, ExtentTest node) {
        this(driver); // wires default SuccessMessageReader + default extractor
        this.node = (node != null ? node : ReportManager.getTest());
    }


    /** Minimal default extractor if DI not provided. */
    private static PoNumberExtractor defaultExtractor() {
        return text -> {
            if (text == null) return Optional.empty();
            Pattern p = Pattern.compile("(PO[-/ ]?\\d{4,}|[A-Z]{2,}\\s*/?-?[A-Z0-9]{2,}[-/][A-Z0-9-]{4,})");
            Matcher m = p.matcher(text);
            return m.find() ? Optional.of(m.group(1).trim()) : Optional.empty();
        };
    }

    public void fillForm(PurchaseOrderData data) {
        addedLineItems.clear();
        ReportManager.setTest(node);
        PerformanceLogger.start("DirectPO_fillForm");

        ReportManager.group("📝 Fill Direct PO Form", () -> {
            // --- Page readiness & route echo ---
            ReportManager.group("🔧 Page Ready Check", () -> {
                new WebDriverWait(driver, Duration.ofSeconds(20)).until(ExpectedConditions.or(
                        ExpectedConditions.urlContains("PmrTrnDirectpoAdd"),
                        ExpectedConditions.visibilityOfElementLocated(By.cssSelector("ng-select[formcontrolname='branch_name']"))
                ));
                ReportManager.info("🌐 Current URL: <i>" + driver.getCurrentUrl() + "</i>");
            });

            // --- Header (planned vs UI echo) ---
            ReportManager.group("📋 Header Section", () -> {
                ReportManager.table(POLogFormatter.headerTable(data), "Planned Header Values");
                fillHeader(data);                            // does the typing/selecting
                ReportManager.table(headerEchoTable(), "Header Snapshot After Fill");
            });

            // --- Renewal ---
            if (data.isRenewal()) {
                ReportManager.group("🔁 Renewal Section", () -> {
                    click(renewalYesRadio, "Renewal Yes");
                    if (data.getRenewalDate() != null) {
                        FlatpickrDatePicker.pickDateAndClose(driver, renewalDatePicker, data.getRenewalDate().format(fmt), "Renewal Date");
                    }
                });
            } else {
                if (isPresent(renewalNoRadio)) {
                    click(renewalNoRadio, "Renewal No");
                    ReportManager.info("🔁 Renewal: <b>No</b>");
                }
            }

            waitForOverlayClear();

            // --- Line Items ---
            if (data.getLineItems() == null || data.getLineItems().isEmpty()) {
                var t = ReportManager.getTest();
                if (t != null) t.warning("⚠️ No line items to add");
            } else {
                ReportManager.group("📦 Line Items", () -> {
                    for (int i = 0; i < data.getLineItems().size(); i++) {
                        final int index = i;
                        final LineItem item = data.getLineItems().get(i);
                        final String productName = (item.getProductName() == null || item.getProductName().isBlank())
                                ? "Unknown Product" : item.getProductName().trim();
                        final String title = "🧾 Line Item " + (index + 1) + ": " + productName;
                        item.setIndex(index + 1);
                        ReportManager.group(title, () -> addLineItem(item, index));
                    }
                });

                // Summary
                ReportManager.group("📦 Line Items Summary", () -> {
                    List<String[]> rows = new ArrayList<>();
                    rows.add(new String[]{"#", "Product", "Qty", "Price", "Disc %", "Total", "Status", "Reason"});
                    for (LineItem li : data.getLineItems()) {
                        rows.add(new String[]{
                                String.valueOf(li.getIndex()),
                                nz(li.getProductName()),
                                String.valueOf(li.getQuantity()),
                                nb(li.getPrice()),
                                nb(li.getDiscountPct()),
                                nb(li.computeTotalAmount()),
                                li.isSuccess() ? "✅ PASS" : "❌ FAIL",
                                li.isSuccess() ? "-" : nz(li.getFailureReason())
                        });
                    }
                    ReportManager.getTest().info(
                            MarkupHelper.createTable(rows.toArray(new String[0][]))
                    );
                    long passed = data.getLineItems().stream().filter(LineItem::isSuccess).count();
                    long failed = data.getLineItems().size() - passed;
                    ReportManager.info("📊 Items: <b>" + passed + " pass</b>, <b>" + failed + " fail</b>");
                    if (failed > 0) throw new AssertionError("Some line items failed validation. Check the summary.");
                });
            }
        });

        PerformanceLogger.end("DirectPO_fillForm");
    }

    /** Submit and return parsed PO number + raw success message. */
    public SubmitResult submitAndCapture() {
        ReportManager.setTest(node);

        ReportManager.group("Submit Section", () -> {
            click(submitBtn, "Submit PO");
            ReportManager.pass("✅ Submitted – awaiting success...");
        });

        // ---- BOUNDED CAPTURE (no hangs) ----
        final long tStart = System.currentTimeMillis();
        final long HARD_TIMEOUT_MS = 12_000; // overall cap for this stage
        String raw = "";
        String po  = null;

        try {
            ReportManager.info("⏳ Reading success message (toast/alert)…");
            raw = readSuccessMessageBounded(Duration.ofMillis(HARD_TIMEOUT_MS));
            ReportManager.info("🔎 Raw success: <i>" + (raw == null ? "" : raw) + "</i>");
        } catch (Exception e) {
            ReportManager.warn("⚠️ Success read failed quickly: " + e.getClass().getSimpleName() + " – " + nz(e.getMessage()));
            raw = "";
        }

        // Parse PO from whatever we found (may be empty)
        try {
            po = poExtractor.extract(nz(raw)).orElse(null);
        } catch (Exception ignore) { /* keep po = null */ }

        // Fallbacks if parser failed and we still have a few ms left
        if (po == null || po.isBlank()) {
            long remaining = HARD_TIMEOUT_MS - (System.currentTimeMillis() - tStart);
            if (remaining > 0) {
                ReportManager.info("🧭 Fallback parse: trying DOM/URL for PO number…");
                try {
                    String domText = driver.findElement(By.tagName("body")).getText();
                    po = poExtractor.extract(nz(domText)).orElse(po);
                } catch (Exception ignore) {}

                try {
                    String url = driver.getCurrentUrl();
                    po = (po == null || po.isBlank()) ? poExtractor.extract(nz(url)).orElse(null) : po;
                } catch (Exception ignore) {}
            }
        }

        if (po != null && !po.isBlank()) {
            ReportManager.pass("📌 Captured PO Number: <b>" + po + "</b>");
        } else {
            ReportManager.warn("⚠️ Could not parse PO number from success.");
        }

        // Don’t hang here either
        try {
            waitForOverlayClear(); // your method; assume it’s bounded; if not, add a cap internally
        } catch (Exception e) {
            ReportManager.warn("⚠️ Overlay clear skipped: " + e.getMessage());
        }

        return new SubmitResult(po, nz(raw));
    }

    // ---------- internals ----------
    private void fillHeader(PurchaseOrderData d) {
        // Ensure page is ready
        new WebDriverWait(driver, Duration.ofSeconds(20)).until(ExpectedConditions.or(
                ExpectedConditions.urlContains("PmrTrnDirectpoAdd"),
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("ng-select[formcontrolname='branch_name']"))
        ));

        // Row 1
        selectNgWithLog("branch_name", d.getBranchName(), "Branch");
        typeWithLog(poRefNoInput, d.getPoRefNo(), "PO Ref No");
        if (d.getPoDate() != null)
            FlatpickrDatePicker.pickDateAndClose(driver, poDatePicker, d.getPoDate().format(fmt), "PO Date");
        if (d.getExpectedDate() != null)
            FlatpickrDatePicker.pickDateAndClose(driver, expectedDatePicker, d.getExpectedDate().format(fmt), "Expected Date");

        // Row 2
        selectNgWithLog("vendor_companyname", d.getVendorName(), "Vendor");
        waitForAutoFillIfAny(billToInput);
        typeWithLog(billToInput, d.getBillTo(), "Bill To");
        waitForAutoFillIfAny(shipToInput);
        typeWithLog(shipToInput, d.getShipTo(), "Ship To");

        // Row 3
        selectNgWithLog("employee_name", d.getRequestedBy(), "Requested By");
        typeWithLog(deliveryTermsInput, d.getDeliveryTerms(), "Delivery Terms");
        typeWithLog(paymentTermsInput,  d.getPaymentTerms(),  "Payment Terms");

        // Row 4
        typeWithLog(requestorContact, d.getRequestorContactDetails(), "Requestor Contact");
        typeWithLog(dispatchModeInput, d.getDispatchMode(), "Dispatch Mode");
        selectNgWithLog("currency_code", d.getCurrency(), "Currency");
        if (d.getExchangeRate() != null)
            typeWithLog(exchangeRateInput, d.getExchangeRate().toPlainString(), "Exchange Rate");

        // Cover Note
        typeWithLog(coverNoteInput, d.getCoverNote(), "Cover Note");

        waitForOverlayClear();
    }

    private boolean isPresent(By by) {
        return !driver.findElements(by).isEmpty();
    }

    private void addLineItem(LineItem item, int index) {
        waitForOverlayClear();

        int rowCountBefore = driver.findElements(summaryRows).size();

        List<WebElement> entryList = wait.until(d -> d.findElements(entryRows));
        if (entryList.isEmpty()) {
            WebElement addBtn = waitUntilVisible(addProductBtn);
            scrollIntoView(addProductBtn);
            safeClick(addBtn);
            entryList = wait.until(d -> d.findElements(entryRows));
        }
        WebElement row = entryList.get(0);

        // Product
        selectFromNgSelectInRow(row, "product_name", item.getProductName());

        WebElement productLabel = row.findElement(By.cssSelector(
                "ng-select[formcontrolname='product_name'] .ng-value-label"));
        wait.until(d -> {
            try { return item.getProductName().equals(productLabel.getText().trim()); }
            catch (StaleElementReferenceException ignored) { return false; }
        });
        try { wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div.ng-dropdown-panel"))); }
        catch (TimeoutException ignored) {}

        // Description / Qty / Price / Discount (quiet)
        if (item.getDescription() != null)
            typeInRowQuiet(row, descriptionInp, item.getDescription(), "Description"); // Cannot resolve method 'typeInRowQuiet' in 'DirectPO'

        String qtyStr      = BigDecimal.valueOf(item.getQuantity()).stripTrailingZeros().toPlainString();
        String priceStr    = item.getPrice() != null ? item.getPrice().stripTrailingZeros().toPlainString() : "0";
        String discountStr = item.getDiscountPct() != null ? item.getDiscountPct().stripTrailingZeros().toPlainString() : "0";

        typeInRowQuiet(row, quantityInp, qtyStr,   "Quantity"); row.findElement(quantityInp).sendKeys(Keys.TAB);
        typeInRowQuiet(row, priceInp,    priceStr, "Price");    row.findElement(priceInp).sendKeys(Keys.TAB);
        typeInRowQuiet(row, discountInp, discountStr, "Discount"); row.findElement(discountInp).sendKeys(Keys.TAB);

        wait.until(ExpectedConditions.textToBePresentInElementValue(row.findElement(quantityInp), qtyStr));
        wait.until(ExpectedConditions.textToBePresentInElementValue(row.findElement(priceInp), priceStr));

        // Save (+)
        WebElement rowPlusBtn = row.findElement(By.xpath(".//td[last()]//button[.//i[contains(@class,'fa-plus')]]"));
        safeClick(rowPlusBtn);

        // Toast & per-item table
        try {
            ToastVerifier.ToastResult actualToast = ToastVerifier.waitAndCapture(driver);
            ToastAssertor.assertToast(driver, actualToast, ToastType.SUCCESS, "Product Added Successfully", item);
        } catch (Exception e) {
            item.setSuccess(false);
            item.setFailureReason(e.getMessage());
            DiagnosticsLogger.onFailure(driver, "Line Item: " + item.getProductName(), e);
        }
        ReportManager.table(POLogFormatter.lineItemRow(item), "Line Item " + (index + 1));

        waitForRowToIncrease(summaryRows, rowCountBefore, "LineItem Save");
        addedLineItems.add(item);

        ReportManager.infoHtml("🔎 Branch chip: " +
                ((String)((JavascriptExecutor)driver).executeScript(
                        "var el=(document.querySelector('.modal.show')||document.body)"
                                + ".querySelector(\"ng-select[formcontrolname='branch_name'] .ng-value\");"
                                + "return el? el.textContent.trim():'';")));
    }

    private void maybeTypeQuiet(By locator, String value, String name) {
        if (value != null && !value.isBlank()) typeQuiet(locator, value, name);
    }
    private static String nz(String s){ return s == null ? "" : s; }
    private static String nb(BigDecimal b){ return POLogFormatter.nb(b); }
    private static final boolean VERBOSE_HEADER_LOGS = false;

    /** Tiny auto-fill wait after vendor select (quiet). */
    private void waitForAutoFillIfAny(By locator){
        try {
            new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofMillis(900))
                    .until(d -> {
                        try {
                            WebElement el = d.findElement(locator);
                            String val = el.getAttribute("value");
                            return val != null && !val.isBlank();
                        } catch (StaleElementReferenceException | NoSuchElementException e) { return false; }
                    });
        } catch (TimeoutException ignored) {}
    }

    public void submitDirectPO() {
        ReportManager.setTest(node);
        SubmitResult res = submitAndCapture();
        String po = res.poNumber();

        if (po != null && !po.isBlank()) {
            PurchaseOrderMemory.set(po);
            ReportManager.getTest().info("📌 Captured System PO: " + po);
        } else {
            ReportManager.getTest().warning("⚠️ Could not capture system PO number");
        }
    }

    /** Tries toast/alerts first, then whole page text. */
    private String grabSystemPoNo() {
        String fromToasts = extractFromElements(driver.findElements(
                By.xpath("//*[contains(@class,'toast') or contains(@class,'swal2') or contains(@class,'alert')]//*[self::div or self::span or self::p or self::h2 or self::h3 or self::h4]")));
        if (fromToasts != null) return fromToasts;

        try { return extractFromText(driver.findElement(By.tagName("body")).getText()); }
        catch (Exception ignore) { return null; }
    }

    private String extractFromElements(java.util.List<WebElement> nodes) {
        for (WebElement el : nodes) {
            try {
                String s = extractFromText(el.getText());
                if (s != null) return s;
            } catch (StaleElementReferenceException ignored) {}
        }
        return null;
    }

    private String extractFromText(String text) {
        if (text == null) return null;
        Pattern p = Pattern.compile("([A-Z]{2,}\\s*/?-?[A-Z0-9]{2,}[-/][A-Z0-9-]{4,}|PO-\\d{4,})");
        Matcher m = p.matcher(text);
        return m.find() ? m.group(1).trim() : null;
    }

    private WebElement visibleContainer() {
        List<WebElement> modal = driver.findElements(By.cssSelector(".modal.show"));
        if (!modal.isEmpty()) return modal.get(0);
        List<WebElement> page = driver.findElements(By.cssSelector("app-purchase-order form"));
        if (!page.isEmpty()) return page.get(0);
        return driver.findElement(By.tagName("body")); // fallback
    }

    private WebElement findInScope(By locator) {
        WebElement scope = visibleContainer();
        List<WebElement> candidates = scope.findElements(locator);
        if (candidates.isEmpty() && !"body".equalsIgnoreCase(scope.getTagName())) {
            candidates = driver.findElements(locator);
        }
        return candidates.isEmpty() ? null : candidates.get(0);
    }

    private boolean presentInScope(By locator) {
        return findInScope(locator) != null;
    }

    private void selectFromNgSelectScoped(String formControlName, String value, String name) {
        if (value == null || value.isBlank()) return;
        WebElement scope = visibleContainer();
        By ng = By.cssSelector("ng-select[formcontrolname='" + formControlName + "']");
        WebElement box = findInScope(ng);
        if (box == null) throw new NoSuchElementException("ng-select not found in scope: " + formControlName);

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", box);
        box.click();

        By panelInput = By.cssSelector("div.ng-dropdown-panel input[type='text']");
        WebElement inp = new WebDriverWait(driver, Duration.ofSeconds(8))
                .until(ExpectedConditions.visibilityOfElementLocated(panelInput));
        inp.clear();
        inp.sendKeys(value);

        By option = By.xpath("//div[contains(@class,'ng-option')][.//*[normalize-space()='" + value + "']]");
        WebElement opt = new WebDriverWait(driver, Duration.ofSeconds(8))
                .until(ExpectedConditions.elementToBeClickable(option));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", opt);

        try { new WebDriverWait(driver, Duration.ofSeconds(2))
                .until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div.ng-dropdown-panel"))); } catch (Exception ignore) {}

        ReportManager.info("🔽 " + name + ": <b>" + value + "</b>");
    }

    private void typeQuietScoped(By locator, String value, String name) {
        if (value == null) return;
        WebElement el = findInScope(locator);
        if (el == null) {
            ReportManager.getTest().warning("⚠️ Cannot find field in scope: " + name);
            return;
        }
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", el);
        el.clear();
        el.sendKeys(value);
        ReportManager.info("⌨️ " + name + ": <i>" + value + "</i>");
    }
    private String readSuccessMessageBounded(Duration maxWait) {
        long deadline = System.currentTimeMillis() + Math.max(1500, maxWait.toMillis());
        String last = null;

        // 1) Try your injected reader (toast/swal) with short, repeated attempts
        while (System.currentTimeMillis() < deadline) {
            try {
                var opt = successReader.read(driver);
                if (opt.isPresent()) {
                    String val = opt.get();
                    if (val != null && !val.isBlank()) return val;
                    last = val;
                }
            } catch (Exception ignore) { /* swallow and retry */ }

            try { Thread.sleep(250); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); break; }
        }

        // 2) As a fallback, scrape any visible toast/alert text quickly
        try {
            List<WebElement> nodes = driver.findElements(
                    By.xpath("//*[contains(@class,'toast') or contains(@class,'swal2') or contains(@class,'alert')]//*[self::div or self::span or self::p or self::h2 or self::h3 or self::h4]")
            );
            StringBuilder sb = new StringBuilder();
            for (WebElement el : nodes) {
                try {
                    String t = el.getText();
                    if (t != null && !t.isBlank()) {
                        if (sb.length() > 0) sb.append(" | ");
                        sb.append(t.trim());
                    }
                } catch (StaleElementReferenceException ignored) {}
            }
            if (sb.length() > 0) return sb.toString();
        } catch (Exception ignore) {}

        // 3) Nothing found – return last (possibly blank) to keep flow moving
        return last == null ? "" : last;
    }

    /** Immutable result of submit. */
    public record SubmitResult(String poNumber, String rawMessage) {}

    // --- logging helpers (inside DirectPO) ---

    private void selectNgWithLog(String formControl, String value, String label) {
        if (value == null || value.isBlank()) return;
        selectFromNgSelect(formControl, value);
        if (VERBOSE_HEADER_LOGS) {
            String chip = readNgSelectLabel(formControl);
            ReportManager.info("🔽 " + label + " → <b>" + nz(clean(chip)) + "</b>");
        }
    }

    private void typeWithLog(By locator, String value, String label) {
        if (value == null) return;
        typeQuiet(locator, value, label);
        if (VERBOSE_HEADER_LOGS) {
            String echo = readInputValue(locator);
            ReportManager.info("⌨️ " + label + " → <i>" + nz(clean(echo)) + "</i>");
        }
    }

    private String[][] headerEchoTable() {
        String branch = clean(readNgSelectLabel("branch_name"));
        String vendor = clean(readNgSelectLabel("vendor_companyname"));
        String emp    = clean(readNgSelectLabel("employee_name"));
        String curr   = clean(readNgSelectLabel("currency_code"));
        String poDt   = normalizeDate(clean(readInputValue(poDatePicker)));
        String expDt  = normalizeDate(clean(readInputValue(expectedDatePicker)));
        String exch   = clean(readInputValue(exchangeRateInput));

        return new String[][]{
                {"Branch (UI)",        branch},
                {"Vendor (UI)",        vendor},
                {"Requested By (UI)",  emp},
                {"Currency (UI)",      curr},
                {"PO Date (UI)",       poDt},
                {"Expected Date (UI)", expDt},
                {"Exchange Rate (UI)", exch}
        };
    }

    /**
     * Normalize dates read from UI (dd-MM-yyyy → yyyy-MM-dd).
     * Keeps original if already in yyyy-MM-dd or anything else.
     */
    /** Normalize dd-MM-yyyy → yyyy-MM-dd; otherwise return as-is. */
    private String normalizeDate(String raw){
        if (raw != null && raw.matches("\\d{2}-\\d{2}-\\d{4}")) {
            String[] p = raw.split("-");
            return p[2] + "-" + p[1] + "-" + p[0];
        }
        return raw;
    }

    /** Trim and remove odd bullets/nbsp that appear in some tenants. */
    private String clean(String s){
        if (s == null) return "";
        // kill the ”»” bullet and non-breaking spaces
        return s.replace("»", "").replace("\u00A0", " ").trim();
    }
    private String readNgSelectLabel(String formControl) {
        try {
            return (String) ((JavascriptExecutor) driver).executeScript(
                    "var el=document.querySelector(\"ng-select[formcontrolname='"+formControl+"'] .ng-value\");" +
                            "return el? el.textContent.trim():'';");
        } catch (Exception e) { return ""; }
    }
    private String readInputValue(By locator) {
        try { return driver.findElement(locator).getAttribute("value"); }
        catch (Exception e) { return ""; }
    }

    public void ensureBranch(String branchName) {
        if (branchName == null || branchName.isBlank()) return;

        By branchNg = By.cssSelector("ng-select[formcontrolname='branch_name'] .ng-select-container");

        WebElement box = new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.elementToBeClickable(branchNg));

        // Current label
        String current = "";
        try {
            current = (String) jsExecutor.executeScript(
                    "var el=document.querySelector(\"ng-select[formcontrolname='branch_name'] .ng-value\");" +
                            "return el? el.textContent.trim():'';");
        } catch (Exception ignored) {}

        if (branchName.equalsIgnoreCase(current == null ? "" : current.trim())) {
            ReportManager.info("Branch already set to: " + current);
            return;
        }

        box.click();

        // Type-to-filter, if available
        By filter = By.cssSelector("ng-dropdown-panel input[type='text'], ng-dropdown-panel [role='combobox']");
        if (!driver.findElements(filter).isEmpty()) {
            WebElement f = driver.findElement(filter);
            f.sendKeys(Keys.chord(Keys.CONTROL, "a"));
            f.sendKeys(branchName);
        }

        // Pick the option
        By opt = By.xpath(
                "//ng-dropdown-panel//div[contains(@class,'ng-option') and not(contains(@class,'disabled'))]" +
                        "[normalize-space(.)=" + com.Vcidex.StoryboardSystems.Common.BasePage.xpathLiteral(branchName) + "]"
        );
        WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement choice = w.until(ExpectedConditions.elementToBeClickable(opt));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", choice);
        try { choice.click(); } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", choice);
        }

        // Use BasePage waiters
        waitForAngularRequestsToFinish();
        waitForOverlayClear();
    }
}