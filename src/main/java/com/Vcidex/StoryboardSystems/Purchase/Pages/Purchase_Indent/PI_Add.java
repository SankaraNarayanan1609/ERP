package com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Indent;

import com.Vcidex.StoryboardSystems.Common.BasePage;
import com.Vcidex.StoryboardSystems.Purchase.POJO.IndentData;
import com.Vcidex.StoryboardSystems.Purchase.Support.SuccessMessageReader;
import com.Vcidex.StoryboardSystems.Utils.FlatpickrDatePicker;
import com.Vcidex.StoryboardSystems.Utils.Logger.ReportManager;
import com.Vcidex.StoryboardSystems.Utils.Logger.PerformanceLogger;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class PI_Add extends BasePage {
    private final ExtentTest node;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Header
    private final By branchNg     = By.xpath("//ng-select[@formcontrolname='branch_name']");
    private final By requestedBy  = By.xpath("//ng-select[@formcontrolname='employee_name']");
    private final By remarksTa    = By.xpath("//textarea[contains(@formcontrolname,'remark') or contains(@formcontrolname,'note')]");
    private final By indentDate   = By.xpath("//input[@formcontrolname='indent_date' or @name='indent_date']");
    private final By requiredDate = By.xpath("//input[@formcontrolname='required_date' or @name='required_date']");

    // Cost center candidates
    private static final By[] CC_CANDIDATES = new By[]{
            By.xpath("//ng-select[@formcontrolname='costcenter_name']"),
            By.xpath("//ng-select[@formcontrolname='costcenter']"),
            By.xpath("//ng-select[@formcontrolname='cost_center_name']"),
            By.xpath("//ng-select[@formcontrolname='costcenterName']"),
            By.xpath("(//label[normalize-space()='Cost Center' or contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'cost center')]/following::*[self::ng-select or self::div[contains(@class,'ng-select')]])[1]")
    };

    // Lines
    private final By addRowBtn    = By.xpath("//button[contains(@class,'bg-success') and .//i[contains(@class,'fa-plus')]]");
    private final By entryRows    = By.xpath("//table[contains(@class,'table')]/tbody//tr[.//ng-select[@formcontrolname='product_name']]");
    private final By lineQty      = By.cssSelector("input[formcontrolname='productquantity']");
    private final By lineDesc     = By.cssSelector("textarea[formcontrolname='product_remarks']");
    private final By lineNeedDate = By.cssSelector("input[formcontrolname='required_date'], input[formcontrolname='needed_by']");
    private final By rpLineDesc   = By.cssSelector("textarea[formcontrolname='display_field']");
    private final By rpLineQty    = By.cssSelector("input[formcontrolname='qty_requested']");
    private final By rpRowAddBtn  = By.xpath("//table[@id='productdetails_list']//button[@title='Add']");

    // Footer/submit
    private static final By[] SUBMIT_CANDIDATES = new By[]{
            By.xpath("//button[normalize-space()='Submit']"),
            By.xpath("//button[contains(normalize-space(.),'Submit')]"),
            By.xpath("//button[contains(normalize-space(.),'Save') and not(contains(normalize-space(.),'Draft'))]"),
            By.cssSelector("button.btn-success, button.bg-success"),
            By.xpath("//button[.//i[contains(@class,'fa-check')]]"),
            By.cssSelector("button[type='submit']"),
            By.cssSelector("input[type='submit']"),
            By.cssSelector("input[type='button'][value*='Submit' i], input[type='button'][value*='Save' i]"),
            By.xpath("//app-purchase-indentaddsubmit//button[not(@disabled)]"),
            By.xpath("//app-pmr-trn-raise-requisitionaddsubmit//button[not(@disabled)]"),
            By.cssSelector("[data-test='submit'], [data-testid='submit']"),
            By.cssSelector("[role='button'][data-action='submit']")
    };

    private final SuccessMessageReader successReader = SuccessMessageReader.defaultReader();

    public PI_Add(WebDriver d, ExtentTest node) {
        super(d);
        this.node = node;
        waitUntilPresent(By.xpath(
                "//h3[contains(normalize-space(),'Purchase Indent')]" +
                        " | //h3[contains(normalize-space(),'Raise Purchase Indent')]" +
                        " | //app-pmr-trn-raise-requisition"
        ));
        waitForAngularAndIdle();
    }
    public PI_Add(WebDriver d) { this(d, ReportManager.getTest()); }

    /** Creates a PI and returns the generated Indent No. */
    public String create(IndentData d) {
        ReportManager.setTest(node);
        PerformanceLogger.start("PI_Add_create");

        ReportManager.group("🧾 Create Purchase Indent", () -> {
            ReportManager.group("📋 Header Section", () -> fillHeader(d));
            if (d.getItems() != null && !d.getItems().isEmpty()) {
                ReportManager.group("📦 Line Items", () -> addLines(d));
                if (!driver.findElements(By.cssSelector("#productdetails_list tbody tr")).isEmpty()) {
                    ReportManager.getTest().info("✅ Inline grid has rows; proceeding to submit.");
                }
                ReportManager.group("📦 Line Items Summary", () -> lineItemsSummary(d));
            } else {
                Optional.ofNullable(ReportManager.getTest())
                        .ifPresent(t -> t.warning("⚠️ No line items provided. Form may remain invalid."));
            }
        });

        String indentNo = submitAndCaptureIndentNo();
        PerformanceLogger.end("PI_Add_create");

        // ✅ log + return null → caller will do summary snapshot/diff

        if (indentNo == null || indentNo.isBlank()) {
            String ccUI = readCostCenterLabelAny();
            ReportManager.warn("Submit didn’t echo Indent No; will rely on Summary diff. CC (UI): <b>" + ccUI + "</b>");
            super.dumpInvalidControlsIfAny();
            return null;
        }
        return indentNo;
    }

    // ---------- sections ----------
    private void fillHeader(IndentData d) {
        ReportManager.table(headerTable(d), "Planned Header Values");

        if (d.getBranchName() != null && !d.getBranchName().isBlank()) {
            selectFromNgSelect("branch_name", d.getBranchName());
            String branchChosen = readNgSelectLabel("branch_name");

            com.Vcidex.StoryboardSystems.Purchase.Support.PurchaseIndentMemory
                    .set(branchChosen, com.Vcidex.StoryboardSystems.Purchase.Support.PurchaseIndentMemory.indent());
        }
        if (d.getRequestedBy() != null && !d.getRequestedBy().isBlank()) {
            selectFromNgSelect("employee_name", d.getRequestedBy());
        }

        // Cost Center
        boolean hasCc = (locateCostCenter() != null);
        ReportManager.getTest().info("ℹ️ Cost Center control present? <b>" + hasCc + "</b>");
        if (hasCc && d.getCostCenterName() != null && !d.getCostCenterName().isBlank()) {
            String chosen = selectCostCenterIfPresent(d.getCostCenterName());
            ensureCcCommitted();
            if (chosen != null && !chosen.isBlank()) {
                ReportManager.getTest().info("🏷️ Cost Center: <b>" + chosen + "</b>");
            }
        }

        // Dates
        if (!driver.findElements(indentDate).isEmpty() && d.getIndentDate() != null) {
            FlatpickrDatePicker.pickDateAndClose(driver, indentDate, d.getIndentDate().format(FMT), "Indent Date");
        }
        if (!driver.findElements(requiredDate).isEmpty() && d.getRequiredDate() != null) {
            FlatpickrDatePicker.pickDateAndClose(driver, requiredDate, d.getRequiredDate().format(FMT), "Required Date");
        }

        // Remarks
        if (d.getRemarks() != null && !d.getRemarks().isBlank()) {
            typeQuiet(remarksTa, d.getRemarks(), "Remarks");
        }

        waitForOverlayClear();
        ReportManager.table(headerEchoTable(), "Header Snapshot After Fill");
    }

    private void addLine_RaisePI(IndentData.IndentItem it) {
        // 1) Pick product name (trust this to try auto-fill)
        selectFromNgSelect("product_name", it.getProductName());

        // 2) If tenant didn’t commit group/code, backfill first options
        if (!isNgValid("productgroup_name")) {
            String g = selectFirstNgOption("productgroup_name");
            ReportManager.info("Auto-fill missing → Product Group: <b>"+g+"</b>");
        }
        if (!isNgValid("product_code")) {
            String c = selectFirstNgOption("product_code");
            ReportManager.info("Auto-fill missing → Product Code: <b>"+c+"</b>");
        }

        // 3) Rest of the row
        if (it.getDescription() != null) typeQuiet(rpLineDesc, it.getDescription(), "Line Description");
        String qtyStr = java.math.BigDecimal.valueOf(it.getQuantity()).stripTrailingZeros().toPlainString();
        typeQuiet(rpLineQty, qtyStr, "Qty Requested");
        WebElement add = waitUntilClickable(rpRowAddBtn, "Add Line");
        safeClick(add);
        waitForOverlayClear();
    }

    private void addLine_Legacy(IndentData.IndentItem it) {
        List<WebElement> rows = driver.findElements(entryRows);
        if (rows.isEmpty()) safeClick(waitUntilVisible(addRowBtn, "Add Line"));
        if (rows.isEmpty()) rows = driver.findElements(entryRows);
        WebElement row = rows.get(0);

        selectFromNgSelectInRow(row, "product_name", it.getProductName());
        String qtyStr = java.math.BigDecimal.valueOf(it.getQuantity()).stripTrailingZeros().toPlainString();
        typeQuiet(lineQty, qtyStr, "Qty");
        if (it.getDescription() != null) typeQuiet(lineDesc, it.getDescription(), "Description");
        if (it.getNeededBy() != null) {
            FlatpickrDatePicker.pickDateAndClose(driver, lineNeedDate, it.getNeededBy().format(FMT), "Needed By");
        }
        WebElement plus = row.findElement(By.xpath(".//td[last()]//button[.//i[contains(@class,'fa-plus')]]"));
        safeClick(plus);
        waitForOverlayClear();
    }

    private void addLines(IndentData d) {
        boolean hasNewGrid = !driver.findElements(By.id("productdetails_list")).isEmpty();
        if (hasNewGrid) {
            IndentData.IndentItem it = d.getItems().get(0);
            ReportManager.group("🧾 New Page Line (inline Add)", () -> addLine_RaisePI(it));
        } else {
            ReportManager.group("🧾 Legacy Lines", () -> {
                for (IndentData.IndentItem it : d.getItems()) {
                    String title = "• " + safe(it.getProductName());
                    ReportManager.group(title, () -> addLine_Legacy(it));
                }
            });
        }
    }

    private void lineItemsSummary(IndentData d) {
        List<String[]> rows = new java.util.ArrayList<>();
        rows.add(new String[]{"#", "Product", "Qty", "Needed By", "Description"});
        int i = 1;
        for (IndentData.IndentItem it : d.getItems()) {
            rows.add(new String[]{ String.valueOf(i++), safe(it.getProductName()), nzQty(it),
                    itHasNeededBy(it) ? it.getNeededBy().format(FMT) : "", safe(it.getDescription())});
        }
        ReportManager.getTest().info(MarkupHelper.createTable(rows.toArray(new String[0][])));
    }

    // ---------- submit & capture ----------
    private void clickSubmitIndentScoped() {
        try { driver.switchTo().activeElement().sendKeys(Keys.TAB); } catch (Exception ignored) {}
        jsExecutor.executeScript("window.scrollTo({top: document.body.scrollHeight, behavior:'instant'});");
        waitForOverlayClear();

        WebElement btn = findSubmitCandidateIn(visibleContainer());
        if (btn == null) btn = findSubmitCandidateIn(driver.findElement(By.tagName("body")));

        long end = System.currentTimeMillis() + 6000;
        while (btn == null && System.currentTimeMillis() < end) {
            waitForAngularAndIdle();
            btn = findSubmitCandidateIn(driver.findElement(By.tagName("body")));
            if (btn == null) try { Thread.sleep(200); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
        }

        if (btn != null) {
            jsExecutor.executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
            try {
                new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.elementToBeClickable(btn));
                btn.click();
            } catch (Exception e) {
                jsExecutor.executeScript("arguments[0].click();", btn);
            }
            return;
        }

        if (dispatchSubmitEventFallback()) return;

        super.dumpInvalidControlsIfAny();
        throw new NoSuchElementException("Visible enabled Submit/Save button not found (after wide search + JS submit fallback).");
    }

    private String submitAndCaptureIndentNo() {
        // Make sure we log invalids from the BasePage version (no shadowing)
        super.dumpInvalidControlsIfAny();

        clickSubmitIndentScoped();
        waitForAngularRequestsToFinish();
        waitForOverlayClear();

        String msg = tryReadSuccessMessage();
        if (msg != null) {
            String no = parseIndentNo(msg);
            if (no != null && !no.isBlank()) return no;
        }
        try {
            String body = driver.findElement(By.tagName("body")).getText();
            String no = parseIndentNo(body);
            if (no != null && !no.isBlank() && no.matches(".*\\d.*")) return no;
        } catch (Exception ignored) {}

        String fb = fallbackIndentNo();
        return (fb == null || fb.isBlank()) ? null : fb;
    }

    private String tryReadSuccessMessage() {
        if (successReader != null) {
            for (String m : new String[]{"tryRead","read","readToast","waitAndRead","waitAndCapture","capture","getLastMessage","getMessage","getText"}) {
                String msg = invokeReaderIfExists(m);
                if (msg != null && !msg.isBlank()) return msg;
            }
        }
        return waitAndReadAnyToastText(6);
    }

    private String invokeReaderIfExists(String methodName) {
        try {
            Method m = successReader.getClass().getMethod(methodName, WebDriver.class);
            Object out = m.invoke(successReader, driver);
            return out == null ? null : out.toString().trim();
        } catch (NoSuchMethodException nsme) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private String waitAndReadAnyToastText(int maxSeconds) {
        By[] locators = new By[] {
                By.cssSelector(".ngx-toastr .toast-success, .toast.toast-success, .alert.alert-success, .alert-success"),
                By.cssSelector(".swal2-container .swal2-title, .swal2-container .swal2-html-container"),
                By.cssSelector(".iziToast.iziToast-success"),
                By.cssSelector("[role='alert'].alert-success, [data-test='success-message']")
        };
        long end = System.currentTimeMillis() + maxSeconds * 1000L;
        while (System.currentTimeMillis() < end) {
            for (By by : locators) {
                try {
                    WebElement el = new WebDriverWait(driver, Duration.ofMillis(600))
                            .until(ExpectedConditions.visibilityOfElementLocated(by));
                    String text = el.getText();
                    if (text != null && !text.isBlank()) return text.trim();
                } catch (Exception ignored) { }
            }
            try { Thread.sleep(200); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
        }
        return null;
    }

    // ---------- helpers ----------
    private WebElement visibleContainer() {
        List<WebElement> modal = driver.findElements(By.cssSelector(".modal.show"));
        if (!modal.isEmpty()) return modal.get(0);
        List<WebElement> form = driver.findElements(By.cssSelector("app-purchase-indent form, form[novalidate]"));
        if (!form.isEmpty()) return form.get(0);
        return driver.findElement(By.tagName("body"));
    }

    private boolean isEffectivelyEnabled(WebElement el) {
        try {
            if (!el.isDisplayed()) return false;
            String disabledAttr = String.valueOf(el.getAttribute("disabled"));
            if ("true".equalsIgnoreCase(disabledAttr) || "disabled".equalsIgnoreCase(disabledAttr)) return false;
            String aria = String.valueOf(el.getAttribute("aria-disabled"));
            if ("true".equalsIgnoreCase(aria)) return false;
            String cls = String.valueOf(el.getAttribute("class"));
            if (cls != null && cls.toLowerCase().contains("disabled")) return false;
            String pe = (String) jsExecutor.executeScript("return window.getComputedStyle(arguments[0]).getPropertyValue('pointer-events');", el);
            return !"none".equalsIgnoreCase(pe);
        } catch (Throwable t) { return false; }
    }

    private WebElement findSubmitCandidateIn(WebElement scope) {
        for (By by : SUBMIT_CANDIDATES) {
            List<WebElement> els = scope.findElements(by);
            for (WebElement el : els) if (isEffectivelyEnabled(el)) return el;
        }
        return null;
    }

    private String[][] headerTable(IndentData d) {
        return new String[][]{
                {"Branch",          safe(d.getBranchName())},
                {"Requested By",    safe(d.getRequestedBy())},
                {"Cost Center",     safe(d.getCostCenterName())},
                {"Indent Date",     d.getIndentDate()   != null ? d.getIndentDate().format(FMT)   : ""},
                {"Required Date",   d.getRequiredDate() != null ? d.getRequiredDate().format(FMT) : ""},
                {"Remarks",         safe(d.getRemarks())}
        };
    }

    private String[][] headerEchoTable() {
        String branch = readNgSelectLabel("branch_name");
        String emp    = readNgSelectLabel("employee_name");
        String cc     = readCostCenterLabelAny();
        String ind    = readInputValue(indentDate);
        String req    = readInputValue(requiredDate);

        return new String[][]{
                {"Branch (UI)",    branch},
                {"Requested By",   emp},
                {"Cost Center",    cc},
                {"Indent Date",    ind},
                {"Required Date",  req}
        };
    }

    private String readNgSelectLabel(String formControl) {
        try {
            return (String) jsExecutor.executeScript(
                    "var el=document.querySelector(\"ng-select[formcontrolname='"+formControl+"'] .ng-value\");" +
                            "return el? el.textContent.trim():'';");
        } catch (Exception e) { return ""; }
    }

    private String readCostCenterLabelAny() {
        for (By cc : CC_CANDIDATES) {
            try {
                WebElement el = driver.findElement(cc);
                String text = (String) jsExecutor.executeScript(
                        "var v=arguments[0].querySelector('.ng-value'); return v? v.textContent.trim():'';", el);
                if (text != null && !text.isBlank()) return text.trim();
            } catch (Exception ignored) {}
        }
        return "";
    }

    private String readInputValue(By locator) {
        try { return driver.findElement(locator).getAttribute("value"); }
        catch (Exception e) { return ""; }
    }

    private static String safe(String s){ return s == null ? "" : s.trim(); }
    private static String nzQty(IndentData.IndentItem it){
        try { return java.math.BigDecimal.valueOf(it.getQuantity()).stripTrailingZeros().toPlainString(); }
        catch (Exception e){ return ""; }
    }
    private static boolean itHasNeededBy(IndentData.IndentItem it){
        try { return it.getNeededBy() != null; } catch (Exception e){ return false; }
    }

    private String parseIndentNo(String raw) {
        if (raw == null) return null;
        java.util.regex.Matcher m1 = java.util.regex.Pattern.compile(
                "(?i)\\bIndent\\s*(?:No|#|:)\\s*[-:]?\\s*([A-Za-z0-9/_-]*\\d[A-Za-z0-9/_-]*)"
        ).matcher(raw);
        if (m1.find()) return m1.group(1).trim();
        java.util.regex.Matcher m2 = java.util.regex.Pattern.compile(
                "(?i)\\bIndent\\b[^\\r\\n]{0,40}?\\b([A-Za-z0-9/_-]*\\d[A-Za-z0-9/_-]*)"
        ).matcher(raw);
        if (m2.find()) return m2.group(1).trim();
        return null;
    }

    private String fallbackIndentNo() {
        for (By by : java.util.List.of(
                By.cssSelector(".indent-number, [data-test='indent-no']"),
                By.xpath("//*[contains(.,'Indent') and contains(.,'No')]/following::strong[1]")
        )) {
            try {
                String t = driver.findElement(by).getText();
                if (t != null) {
                    t = t.trim();
                    if (t.matches(".*\\d.*")) return t;
                }
            } catch (NoSuchElementException ignored) {}
        }
        return null;
    }

    private boolean dispatchSubmitEventFallback() {
        try {
            Object ok = jsExecutor.executeScript(
                    "var form = document.querySelector(\"app-purchase-indent form, app-pmr-trn-raise-requisition form, form[novalidate], form\");" +
                            "if(!form) return false;" +
                            "var evt = new Event('submit', {bubbles:true, cancelable:true});" +
                            "return form.dispatchEvent(evt);");
            return Boolean.TRUE.equals(ok);
        } catch (Exception e) { return false; }
    }

    /** Locate CC ng-select using any supported pattern. */
    private WebElement locateCostCenter() {
        for (By by : CC_CANDIDATES) {
            List<WebElement> els = driver.findElements(by);
            if (!els.isEmpty() && els.get(0).isDisplayed()) return els.get(0);
        }
        return null;
    }

    /** Robust CC select: tries desired label; falls back to first; returns final label. */
    private String selectCostCenterIfPresent(String label) {
        WebElement cc = locateCostCenter();
        if (cc == null) {
            ReportManager.getTest().info("ℹ️ Cost Center field not present on this screen; skipping.");
            return "";
        }
        String current = (String) jsExecutor.executeScript("var v=arguments[0].querySelector('.ng-value'); return v? v.textContent.trim():'';", cc);
        if (current != null && current.equalsIgnoreCase(label.trim())) return current;

        String finalLabel;
        if (label != null && !label.isBlank()) {
            finalLabel = selectCostCenter(cc, label);
        } else {
            finalLabel = selectFirstCostCenter(cc);
        }
        return finalLabel;
    }

    /** Select by label (scoped), commit, wait valid, return final label. */
    private String selectCostCenter(WebElement cc, String desiredLabel) {
        String canon = desiredLabel.replaceAll("\\s+"," ").trim();

        WebElement box = new WebDriverWait(driver, Duration.ofSeconds(15)).until(d -> {
            try {
                WebElement el = cc.findElement(By.cssSelector("div.ng-select-container"));
                return (el.isDisplayed()) ? el : null;
            } catch (Exception e) { return null; }
        });
        jsExecutor.executeScript("arguments[0].scrollIntoView({block:'center'});", box);
        try { box.click(); } catch (Exception e) { jsExecutor.executeScript("arguments[0].click();", box); }

        WebElement input = new WebDriverWait(driver, Duration.ofSeconds(8)).until(d -> {
            try { return cc.findElement(By.cssSelector("div.ng-input input")); }
            catch (Exception e) { return null; }
        });
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        input.sendKeys(canon);

        By panel = By.xpath("//ng-dropdown-panel[not(contains(@class,'ng-select-hidden'))]");
        new WebDriverWait(driver, Duration.ofSeconds(8)).until(ExpectedConditions.visibilityOfElementLocated(panel));

        By exact = By.xpath("//ng-dropdown-panel//div[contains(@class,'ng-option') and not(contains(@class,'disabled'))][normalize-space(.)=" + xpathLiteral(canon) + "]");
        By contains = By.xpath("//ng-dropdown-panel//div[contains(@class,'ng-option') and not(contains(@class,'disabled'))][contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), translate(" + xpathLiteral(canon) + ",'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'))]");

        WebElement opt;
        try {
            opt = new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.elementToBeClickable(exact));
        } catch (TimeoutException te) {
            opt = new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.elementToBeClickable(contains));
        }
        jsExecutor.executeScript("arguments[0].scrollIntoView({block:'center'});", opt);
        try { opt.click(); } catch (Exception e) { jsExecutor.executeScript("arguments[0].click();", opt); }

        commitNgSelect(cc, panel);
        return waitNgSelectCommittedAndGetText(cc);
    }

    /** Auto-pick first enabled option, commit, wait valid, return label. */
    private String selectFirstCostCenter(WebElement cc) {
        WebElement box = new WebDriverWait(driver, Duration.ofSeconds(15)).until(d -> {
            try {
                WebElement el = cc.findElement(By.cssSelector("div.ng-select-container"));
                return (el.isDisplayed()) ? el : null;
            } catch (Exception e) { return null; }
        });
        jsExecutor.executeScript("arguments[0].scrollIntoView({block:'center'});", box);
        try { box.click(); } catch (Exception e) { jsExecutor.executeScript("arguments[0].click();", box); }

        By panel = By.xpath("//ng-dropdown-panel[not(contains(@class,'ng-select-hidden'))]");
        new WebDriverWait(driver, Duration.ofSeconds(8)).until(ExpectedConditions.visibilityOfElementLocated(panel));

        By firstEnabled = By.xpath("//ng-dropdown-panel//div[contains(@class,'ng-option') and not(contains(@class,'disabled'))][1]");
        WebElement opt = new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.elementToBeClickable(firstEnabled));
        String label = opt.getText() == null ? "" : opt.getText().trim();

        jsExecutor.executeScript("arguments[0].scrollIntoView({block:'center'});", opt);
        try { opt.click(); } catch (Exception e) { jsExecutor.executeScript("arguments[0].click();", opt); }

        commitNgSelect(cc, panel);

        String finalLabel = waitNgSelectCommittedAndGetText(cc);
        if (finalLabel == null || finalLabel.isBlank()) finalLabel = label;
        return finalLabel;
    }

    /** Force-commit an ng-select (TAB, ESC, blur) and wait until the dropdown closes. */
    private void commitNgSelect(WebElement cc, By panel) {
        try { actions.sendKeys(Keys.TAB).perform(); } catch (Exception ignored) {}
        try { actions.sendKeys(Keys.ESCAPE).perform(); } catch (Exception ignored) {}
        try { jsExecutor.executeScript("arguments[0].dispatchEvent(new Event('blur',{bubbles:true}));", cc); } catch (Exception ignored) {}
        try { waitPanelClosed(panel); } catch (Exception ignored) {}
        try {
            new WebDriverWait(driver, Duration.ofSeconds(3)).until(d -> {
                try {
                    WebElement cont = cc.findElement(By.cssSelector(".ng-select-container"));
                    String expanded = cont.getAttribute("aria-expanded");
                    return !"true".equalsIgnoreCase(String.valueOf(expanded));
                } catch (Exception e) { return true; }
            });
        } catch (Exception ignored) {}
    }

    /** Wait until .ng-value has text and control is ng-valid, then return the label. */
    private String waitNgSelectCommittedAndGetText(WebElement cc) {
        long end = System.currentTimeMillis() + 6000;
        String text = "";
        while (System.currentTimeMillis() < end) {
            try {
                text = (String) jsExecutor.executeScript(
                        "var v=arguments[0].querySelector('.ng-value'); return v? v.textContent.trim():'';", cc);
                String cls = cc.getAttribute("class");
                boolean valid = cls != null && cls.contains("ng-valid") && !cls.contains("ng-invalid");
                if (text != null && !text.isBlank() && valid) break;
            } catch (StaleElementReferenceException sere) {
                cc = locateCostCenter();
            } catch (Exception ignored) { }
            try { Thread.sleep(150); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
        return text == null ? "" : text.trim();
    }

    private void ensureCcCommitted() {
        // Try the most common FC name first, but don’t assume only one exists
        String[] names = new String[]{"costcenter_name","costcenter","cost_center_name","costcenterName"};
        for (String fc : names) {
            if (driver.findElements(By.cssSelector("ng-select[formcontrolname='"+fc+"']")).isEmpty()) continue;

            // If still invalid after your select logic, pick first as a safety net
            if (!isNgValid(fc)) {
                ReportManager.info("ℹ️ CC still invalid after select → committing fallback for <b>"+fc+"</b>");
                String picked = selectFirstNgOption(fc);   // from BasePage
                ReportManager.info("🏷️ CC fallback picked: <b>"+picked+"</b>");
            }

            // Final commit + log
            try { actions.sendKeys(Keys.TAB).perform(); } catch (Exception ignored) {}
            waitForAngularRequestsToFinish();
            waitForOverlayClear();

            boolean ok = isNgValid(fc);
            String label = readNgLabel(fc);
            ReportManager.info("✅ CC valid? <b>"+ok+"</b> • Label: <b>"+label+"</b>");
        }
    }

    /** Is an Angular control valid (by formcontrolname on the host element)? */
    public boolean isNgValid(String formControl) {
        try {
            WebElement el = driver.findElement(
                    By.cssSelector("[formcontrolname='" + formControl + "']"));
            String cls = el.getAttribute("class");
            return cls != null && cls.contains("ng-valid") && !cls.contains("ng-invalid");
        } catch (Exception e) { return false; }
    }

    /** Read the visible chip/label from an ng-select by formcontrolname. */
    public String readNgLabel(String formControl) {
        try {
            return (String) jsExecutor.executeScript(
                    "var el=document.querySelector(\"[formcontrolname='"+formControl+"'] .ng-value\");" +
                            "return el? el.textContent.trim():'';");
        } catch (Exception e) { return ""; }
    }

    /** Open an ng-select and pick the first enabled option, then commit. */
    public String selectFirstNgOption(String formControl) {
        By boxBy = By.cssSelector("ng-select[formcontrolname='"+formControl+"'] .ng-select-container");
        WebElement box = new WebDriverWait(driver, LONG_WAIT)
                .until(ExpectedConditions.elementToBeClickable(boxBy));
        scrollIntoView(box);
        try { box.click(); } catch (Exception e) { jsClick(box); }

        By panel = By.xpath("//ng-dropdown-panel[not(contains(@class,'ng-select-hidden'))]");
        waitPanelOpen(panel);

        By firstEnabled = By.xpath(
                "//ng-dropdown-panel//div[contains(@class,'ng-option') and not(contains(@class,'disabled'))][1]");
        WebElement opt = new WebDriverWait(driver, LONG_WAIT)
                .until(ExpectedConditions.elementToBeClickable(firstEnabled));
        String label = opt.getText()==null? "" : opt.getText().trim();
        try { opt.click(); } catch (Exception e) { jsClick(opt); }

        try { waitPanelClosed(panel); } catch (Exception ignored) {}
        try { actions.sendKeys(Keys.TAB).perform(); } catch (Exception ignored) {}
        waitForOverlayClear();
        return label;
    }
}