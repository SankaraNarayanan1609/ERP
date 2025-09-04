// File: src/main/java/com/Vcidex/StoryboardSystems/Purchase/Pages/Purchase_Order/RaisePO.java
package com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order;

import com.Vcidex.StoryboardSystems.Common.BasePage;
import com.Vcidex.StoryboardSystems.Purchase.POJO.LineItem;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseOrderData;
import com.Vcidex.StoryboardSystems.Purchase.Support.PoNumberExtractor;
import com.Vcidex.StoryboardSystems.Purchase.Support.RegexPoNumberExtractor;
import com.Vcidex.StoryboardSystems.Purchase.Support.SuccessMessageReader;
import com.Vcidex.StoryboardSystems.Utils.FlatpickrDatePicker;
import com.Vcidex.StoryboardSystems.Utils.Logger.ReportManager;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

import static com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger.Layer;
import static com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger.step;

public class RaisePO extends BasePage {

    private final By pageHx = By.xpath(
            "//*[self::h1 or self::h2 or self::h3][contains(.,'Purchase Order')] | //app-purchase-order"
    );
    private static final String VENDOR_FCN = "vendor_name";
    private static final String[] VENDOR_FCNS = {"vendor_companyname", "vendor_name"};

    private final By expectedDate = By.xpath(
            "//input[@formcontrolname='expected_date' or @placeholder='Expected Date' or @name='expected_date']"
    );
    private final By firstRowCheckbox = By.xpath("//table//tbody//tr[1]//input[@type='checkbox']");
    private final By submitBtn = By.xpath(
            "//*[self::button or self::a][contains(@class,'btn')][contains(.,'Submit') or contains(.,'Save')]"
    );
    private final By poRefNoField = By.xpath(
            "//input[@formcontrolname='po_ref_no' or @name='po_ref_no' or @placeholder='PO Ref. No']"
    );

    private final PoNumberExtractor poParser;
    private final SuccessMessageReader toastReader;

    public record SubmitResult(String poNumber, String rawMessage) {}

    /** Always chain to the full constructor so fields are initialized. */
    public RaisePO(WebDriver driver) {
        this(driver, new RegexPoNumberExtractor(), SuccessMessageReader.defaultReader());
    }

    public RaisePO(WebDriver driver,
                   PoNumberExtractor poParser,
                   SuccessMessageReader toastReader) {
        super(driver);
        this.poParser = poParser;
        this.toastReader = toastReader;
        waitUntilVisible(pageHx, "Purchase Order form");
    }

    // ───────────────────────────────────────────────────────────────
    // High-level flows (overloads)
    // ───────────────────────────────────────────────────────────────

    /** From Indent: vendor + expected + check-all + SAME price for all rows + submit */
    public SubmitResult fillAndSubmit(String indentNo, String vendorName, String unitPrice, LocalDate expected) {
        ExtentTest node = ReportManager.getTest().createNode("📝 Fill PO (from Indent, single price)");
        ReportManager.setTest(node);

        setPoRefFromIndent(indentNo, node);
        pickVendorSmart(vendorName, node);
        setExpectedDate(expected, node);
        tickAllRows(node);
        setPriceOnAllRows(unitPrice, node);
        return submitAndCapture(node);
    }

    /** From Indent: vendor + expected + check-all + ROW-WISE prices + submit */
    public SubmitResult fillAndSubmit(String indentNo, String vendorName, List<String> prices, LocalDate expected) {
        ExtentTest node = ReportManager.getTest().createNode("📝 Fill PO (from Indent, row-wise prices)");
        ReportManager.setTest(node);

        setPoRefFromIndent(indentNo, node);
        pickVendorSmart(vendorName, node);
        setExpectedDate(expected, node);
        tickAllRows(node);
        setPricesForRows(prices, node);
        return submitAndCapture(node);
    }

    /** From Indent + Factory Data: check-all + map prices from PurchaseOrderData + submit */
    public SubmitResult fillAndSubmit(String indentNo, PurchaseOrderData data) {
        ExtentTest node = ReportManager.getTest().createNode("📝 Fill PO (from Indent + Factory Data)");
        ReportManager.setTest(node);

        setPoRefFromIndent(indentNo, node);
        if (data != null) {
            pickVendorSmart(data.getVendorName(), node);
            if (data.getExpectedDate() != null) setExpectedDate(data.getExpectedDate(), node);
        }
        tickAllRows(node);
        setPricesFromData(data, node);
        return submitAndCapture(node);
    }

    // ───────────────────────────────────────────────────────────────
    // PO Ref derivation (PI → PO)
    // ───────────────────────────────────────────────────────────────

    public static String derivePoRefFromIndent(String indentNo) {
        if (indentNo == null || indentNo.isBlank()) return null;
        String flat = indentNo.replaceAll("[^A-Za-z0-9]", "");
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d{6,})$").matcher(flat);
        if (m.find()) return "PO" + m.group(1);
        String tail = flat.replaceFirst("^[A-Za-z]+", "");
        return tail.isBlank() ? ("PO-" + flat) : ("PO" + tail);
    }

    public void setPoRefFromIndent(String indentNo, ExtentTest node) {
        String poRef = derivePoRefFromIndent(indentNo);
        step(Layer.UI, "Set PO Ref from PI (" + indentNo + ") → " + poRef, () -> {
            if (poRef != null) typeQuiet(poRefNoField, poRef, "PO Ref No");
            return null;
        });
    }

    // ───────────────────────────────────────────────────────────────
    // Vendor / dates / ticking
    // ───────────────────────────────────────────────────────────────

    public void pickVendor(String vendorName, ExtentTest node) {
        step(Layer.UI, "Select Vendor = " + vendorName, () -> {
            selectFromNgSelect(VENDOR_FCN, vendorName);
            return null;
        });
    }

    private void pickVendorSmart(String vendorName, ExtentTest node) {
        if (vendorName == null || vendorName.isBlank()) return;
        for (String fcn : VENDOR_FCNS) {
            try {
                selectFromNgSelect(fcn, vendorName);
                ReportManager.info("🔽 Vendor (" + fcn + "): <b>" + vendorName + "</b>");
                return;
            } catch (org.openqa.selenium.NoSuchElementException | TimeoutException ignored) {
                // try next
            }
        }
        throw new org.openqa.selenium.NoSuchElementException("Vendor ng-select not found for any of: " + Arrays.toString(VENDOR_FCNS));
    }

    public void setExpectedDate(LocalDate date, ExtentTest node) {
        step(Layer.UI, "Set Expected Date = " + date, () -> {
            if (exists(expectedDate)) FlatpickrDatePicker.pickDateAndClose(driver, expectedDate, date, "Expected Date");
            return null;
        });
    }

    public void tickFirstRow(ExtentTest node) {
        step(Layer.UI, "Tick product checkbox (first row)", () -> {
            scrollIntoView(firstRowCheckbox);
            click(firstRowCheckbox, "First-row checkbox");
            return null;
        });
    }

    public void tickAllRows(ExtentTest node) {
        step(Layer.UI, "Tick ALL product checkboxes", () -> {
            waitForOverlayClear();
            WebElement table = summaryTable();
            tickAllProductRows(table);
            return null;
        });
    }

    // ───────────────────────────────────────────────────────────────
    // Pricing APIs
    // ───────────────────────────────────────────────────────────────

    /** SAME price for all rows */
    public void setPriceOnAllRows(String price, ExtentTest node) {
        step(Layer.UI, "Enter Price for ALL rows = " + price, () -> {
            waitForOverlayClear();
            setPriceForAllRows(price);
            return null;
        });
    }

    /** List overload: row i ← prices[i] */
    public void setPricesForRows(List<String> prices, ExtentTest node) {
        if (prices == null || prices.isEmpty()) {
            ReportManager.warn("No prices provided for setPricesForRows; nothing to do.");
            return;
        }
        step(Layer.UI, "Enter row-wise prices (" + prices.size() + ")", () -> {
            waitForOverlayClear();
            WebElement table = summaryTable();
            tickAllProductRows(table); // ensure editable
            int priceCol = priceColumnIndex(table);
            List<WebElement> rows = bodyRows(table);

            int n = Math.min(prices.size(), rows.size());
            for (int i = 0; i < n; i++) {
                String p = prices.get(i);
                if (p == null || p.isBlank()) continue;
                WebElement row = rows.get(i);
                ensureRowChecked(row);
                WebElement input = priceInputInRow(row, priceCol);
                typeValueAndCommit(input, p);
                ReportManager.info("💲 Row " + (i + 1) + " → " + p);
            }
            if (prices.size() > rows.size()) {
                ReportManager.warn("Provided " + prices.size() + " prices but only " + rows.size() + " rows exist; extras ignored.");
            } else if (rows.size() > prices.size()) {
                ReportManager.info("Table has " + rows.size() + " rows; only first " + prices.size() + " priced (rest unchanged).");
            }
            return null;
        });
    }

    /** Map overload: match by Product name exactly */
    public void setPricesByProduct(Map<String,String> productToPrice, ExtentTest node) {
        if (productToPrice == null || productToPrice.isEmpty()) {
            ReportManager.warn("No product->price map provided; nothing to do.");
            return;
        }
        step(Layer.UI, "Enter prices by product name (" + productToPrice.size() + ")", () -> {
            waitForOverlayClear();
            WebElement table = summaryTable();
            tickAllProductRows(table);
            int priceCol   = priceColumnIndex(table);
            int productCol = productColumnIndex(table);
            List<WebElement> rows = bodyRows(table);

            Set<String> matched = new HashSet<>();
            for (WebElement row : rows) {
                String prod = cellText(row, productCol);
                if (productToPrice.containsKey(prod)) {
                    String price = productToPrice.get(prod);
                    if (price != null && !price.isBlank()) {
                        ensureRowChecked(row);
                        WebElement input = priceInputInRow(row, priceCol);
                        typeValueAndCommit(input, price);
                        matched.add(prod);
                        ReportManager.info("💲 Product '" + prod + "' → " + price);
                    }
                }
            }
            for (String key : productToPrice.keySet()) {
                if (!matched.contains(key)) ReportManager.warn("No matching row found for product: " + key);
            }
            return null;
        });
    }

    /** Use factory data: prefer Product Code, fallback Product name */
    public void setPricesFromData(PurchaseOrderData data, ExtentTest node) {
        if (data == null || data.getLineItems() == null || data.getLineItems().isEmpty()) {
            ReportManager.warn("No line items in PurchaseOrderData; nothing to price.");
            return;
        }

        step(Layer.UI, "Map prices from PurchaseOrderData to PO table", () -> {
            waitForOverlayClear();
            WebElement table    = summaryTable();
            int codeCol         = productCodeColumnIndex(table);
            int nameCol         = productColumnIndex(table);
            int priceCol        = priceColumnIndex(table);

            Map<String,String> priceByCode = new HashMap<>();
            Map<String,String> priceByName = new HashMap<>();
            for (LineItem li : data.getLineItems()) {
                if (li == null) continue;
                String pStr = (li.getPrice() == null) ? null : li.getPrice().stripTrailingZeros().toPlainString();
                if (pStr == null || pStr.isBlank()) continue;
                if (li.getProductCode() != null && !li.getProductCode().isBlank())
                    priceByCode.put(li.getProductCode().trim(), pStr);
                if (li.getProductName() != null && !li.getProductName().isBlank())
                    priceByName.put(li.getProductName().trim(), pStr);
            }

            tickAllProductRows(table);

            int priced = 0, missing = 0;
            for (WebElement row : bodyRows(table)) {
                String code = cellText(row, codeCol);
                if (code.isBlank()) code = anyCellText(row, codeCol).trim(); // hidden cell fallback
                String name = cellText(row, nameCol);

                String price = null;
                if (!code.isBlank() && priceByCode.containsKey(code)) {
                    price = priceByCode.get(code);
                } else if (!name.isBlank() && priceByName.containsKey(name)) {
                    price = priceByName.get(name);
                }

                if (price != null && !price.isBlank()) {
                    ensureRowChecked(row);
                    WebElement input = priceInputInRow(row, priceCol);
                    typeValueAndCommit(input, price);
                    priced++;
                    ReportManager.info("💲 " + (code.isBlank() ? name : code) + " → " + price);
                } else {
                    missing++;
                    ReportManager.warn("No price found in data for row: code='" + code + "', product='" + name + "'");
                }
            }

            ReportManager.info("✅ Prices applied: " + priced + " row(s); 🔎 no data for: " + missing + " row(s).");
            return null;
        });
    }

    // ───────────────────────────────────────────────────────────────
    // Submit
    // ───────────────────────────────────────────────────────────────

    public SubmitResult submitAndCapture(ExtentTest node) {
        step(Layer.UI, "Submit PO", () -> {
            waitForOverlayClear();
            scrollIntoView(submitBtn);
            click(submitBtn, "Submit");
            return null;
        });

        Optional<String> raw = toastReader.read(driver);
        String po = raw.flatMap(poParser::extract).orElseGet(this::readPoRefFromField);

        if (po == null || po.isBlank()) {
            ReportManager.info("⚠ PO number not found in toast or field.");
        } else {
            ReportManager.info("✅ Captured PO No: " + po);
        }
        return new SubmitResult(po, raw.orElse(""));
    }

    // ───────────────────────────────────────────────────────────────
    // Helpers
    // ───────────────────────────────────────────────────────────────

    private boolean exists(By by) { return !driver.findElements(by).isEmpty(); }

    private String readPoRefFromField() {
        try {
            WebElement f = driver.findElement(poRefNoField);
            String v = f.getAttribute("value");
            if (v != null && !v.isBlank()) return v.trim();
        } catch (org.openqa.selenium.NoSuchElementException ignored) {}
        return null;
    }

    private static final String BRANCH_FCN = "branch_name";
    public void ensureBranch(String branchName, ExtentTest node) {
        step(Layer.UI, "Ensure Branch = " + branchName, () -> {
            selectFromNgSelect(BRANCH_FCN, branchName);
            return null;
        });
    }

    /** Find the PO summary table reliably */
    private WebElement summaryTable() {
        List<WebElement> byId = driver.findElements(By.id("GetPurchaseOrder1"));
        if (!byId.isEmpty()) return byId.get(0);
        By anyPriceTable = By.xpath(
                "//table[.//thead//th[normalize-space()='Price' or normalize-space()='Unit Price' or normalize-space()='Rate']]"
        );
        return waitUntilVisible(anyPriceTable, "PO Summary Table");
    }

    /** 1-based index of the Price/Rate column inside the given table */
    private int priceColumnIndex(WebElement table) {
        List<WebElement> ths = table.findElements(By.cssSelector("thead th"));
        String[] wants = {"Unit Price", "Price", "Rate"};
        for (int i = 0; i < ths.size(); i++) {
            String h = ths.get(i).getText().replaceAll("\\s+", " ").trim();
            for (String w : wants) if (h.equalsIgnoreCase(w)) return i + 1;
        }
        throw new org.openqa.selenium.NoSuchElementException("Price/Rate column not found in header.");
    }

    /** 1-based index of the Product column */
    private int productColumnIndex(WebElement table) {
        List<WebElement> ths = table.findElements(By.cssSelector("thead th"));
        for (int i = 0; i < ths.size(); i++) {
            String h = ths.get(i).getText().replaceAll("\\s+"," ").trim();
            if (h.equalsIgnoreCase("Product")) return i + 1;
        }
        throw new org.openqa.selenium.NoSuchElementException("'Product' column not found in header.");
    }

    /** 1-based index of the Product Code column (handles hidden/variant labels) */
    private int productCodeColumnIndex(WebElement table) {
        List<WebElement> ths = table.findElements(By.cssSelector("thead th"));
        for (int i = 0; i < ths.size(); i++) {
            String h = ths.get(i).getText().replaceAll("\\s+"," ").trim();
            if (h.equalsIgnoreCase("Product Code")) return i + 1;
        }
        for (int i = 0; i < ths.size(); i++) {
            String h = ths.get(i).getText().replaceAll("\\s+"," ").trim();
            if (h.equalsIgnoreCase("ProductCode") || h.equalsIgnoreCase("Code")) return i + 1;
        }
        throw new org.openqa.selenium.NoSuchElementException("'Product Code' column not found in header.");
    }

    /** All body rows (visible) */
    private List<WebElement> bodyRows(WebElement table) {
        return table.findElements(By.cssSelector("tbody tr"));
    }

    /** Read trimmed text of a cell in a given row (1-based column). */
    private String cellText(WebElement row, int colIndex) {
        WebElement td = row.findElement(By.xpath("./td[" + colIndex + "]"));
        String s = td.getText();
        return s == null ? "" : s.replace("\u00A0", " ").trim();
    }

    /** Use textContent for hidden cells (e.g., code columns that are visually hidden). */
    private String anyCellText(WebElement row, int colIndex) {
        WebElement td = row.findElement(By.xpath("./td[" + colIndex + "]"));
        try {
            Object v = ((JavascriptExecutor) driver).executeScript("return arguments[0].textContent || '';", td);
            return v == null ? "" : String.valueOf(v).trim();
        } catch (Exception e) { return ""; }
    }

    /** Ensure the row checkbox is ticked (robust with JS fallback). */
    private void ensureRowChecked(WebElement row) {
        List<WebElement> cbs = row.findElements(By.cssSelector("td input[type='checkbox']"));
        if (!cbs.isEmpty()) {
            WebElement cb = cbs.get(0);
            jsExecutor.executeScript("arguments[0].scrollIntoView({block:'center'});", cb);
            if (!cb.isSelected()) {
                try {
                    new WebDriverWait(driver, Duration.ofSeconds(3))
                            .until(ExpectedConditions.elementToBeClickable(cb))
                            .click();
                } catch (ElementClickInterceptedException e) {
                    checkViaJs(cb);
                } catch (Exception e) {
                    // last resort: Actions, then JS
                    try {
                        new Actions(driver).moveToElement(cb).pause(Duration.ofMillis(50)).click().perform();
                    } catch (Exception ignore) {
                        checkViaJs(cb);
                    }
                }
            }
        }
    }

    /** Try header #checkAll; fallback: tick each row checkbox (both with JS-safe fallbacks). */
    private void tickAllProductRows(WebElement table) {
        // 1) Header checkbox if present
        List<WebElement> headerCb = table.findElements(By.cssSelector("thead input#checkAll, thead input[type='checkbox']#checkAll"));
        if (!headerCb.isEmpty()) {
            WebElement cb = headerCb.get(0);
            jsExecutor.executeScript("arguments[0].scrollIntoView({block:'center'});", cb);
            boolean clicked = false;
            try {
                new WebDriverWait(driver, Duration.ofSeconds(3))
                        .until(ExpectedConditions.elementToBeClickable(cb))
                        .click();
                clicked = true;
            } catch (ElementClickInterceptedException e) {
                checkViaJs(cb);
                clicked = true;
            } catch (Exception e) {
                // Try Actions, then JS
                try {
                    new Actions(driver).moveToElement(cb).pause(Duration.ofMillis(50)).click().perform();
                    clicked = true;
                } catch (Exception ignore) {
                    checkViaJs(cb);
                    clicked = true;
                }
            }

            // Verify rows actually became checked; some UIs don't propagate reliably
            if (clicked && allRowsChecked(table)) return;
        }

        // 2) Row-level checkboxes (robust path)
        for (WebElement row : bodyRows(table)) {
            ensureRowChecked(row);
        }
    }

    /** Are all visible row checkboxes selected? */
    private boolean allRowsChecked(WebElement table) {
        List<WebElement> rows = bodyRows(table);
        if (rows.isEmpty()) return true;
        for (WebElement row : rows) {
            List<WebElement> cbs = row.findElements(By.cssSelector("td input[type='checkbox']"));
            if (!cbs.isEmpty() && !cbs.get(0).isSelected()) return false;
        }
        return true;
    }

    /** Force-check a checkbox via JS and fire events to satisfy Angular. */
    private void checkViaJs(WebElement cb) {
        try {
            jsExecutor.executeScript(
                    "const el=arguments[0];" +
                            "if(!el.checked){ el.checked = true; }" +
                            "el.dispatchEvent(new Event('click',  {bubbles:true}));" +
                            "el.dispatchEvent(new Event('input',  {bubbles:true}));" +
                            "el.dispatchEvent(new Event('change', {bubbles:true}));",
                    cb
            );
        } catch (Exception ignore) { /* best-effort */ }
    }

    /** Find the editable input for the Price cell in this row.
     *  1) Try header-based index (works on simple tables).
     *  2) Fallback: anchor to the Discount block (td containing div.d-flex / colspan)
     *     and take the editable input in the td immediately BEFORE it.
     *  3) Final fallback: take the last editable input that appears BEFORE the Discount block.
     */
    private WebElement priceInputInRow(WebElement row, int priceCol) {
        // -------- 1) Header-indexed lookup (fast path) --------
        try {
            WebElement td = row.findElement(By.xpath("./td[" + priceCol + "]"));
            List<WebElement> inputs = td.findElements(By.cssSelector("input[type='text']:not([readonly])"));
            if (!inputs.isEmpty()) return inputs.get(0);
        } catch (Exception ignored) {
            // fall through to heuristic
        }

        // -------- 2) Heuristic: use the Discount block as an anchor --------
        // Locate the Discount TD (it contains a div.d-flex, and often has colspan=2)
        List<WebElement> discountCells = row.findElements(
                By.xpath(".//td[.//div[contains(@class,'d-flex')] or @colspan]")
        );

        if (!discountCells.isEmpty()) {
            // The Price cell is the TD immediately preceding the Discount TD
            // Find that TD and take its first editable input
            List<WebElement> prevCells = row.findElements(
                    By.xpath(".//td[.//div[contains(@class,'d-flex')] or @colspan]/preceding-sibling::td[1]")
            );
            if (!prevCells.isEmpty()) {
                WebElement priceTd = prevCells.get(0);
                List<WebElement> inputs = priceTd.findElements(By.xpath(".//input[not(@readonly)]"));
                if (!inputs.isEmpty()) return inputs.get(0);
            }

            // 2b) Still not found? Take the LAST editable input that appears BEFORE the Discount TD
            // (this will naturally skip the "Total Amount" input which is after Discount)
            List<WebElement> inputsBeforeDiscount = row.findElements(
                    By.xpath(".//td[following-sibling::td[.//div[contains(@class,'d-flex')] or @colspan]]//input[not(@readonly)]")
            );
            if (!inputsBeforeDiscount.isEmpty()) {
                return inputsBeforeDiscount.get(inputsBeforeDiscount.size() - 1);
            }
        }

        // -------- 3) Final defensive fallback --------
        // Take the second editable input in the row (typical order: Quantity, Price, [Discount], …)
        List<WebElement> anyEditable = row.findElements(By.xpath(".//input[not(@readonly)]"));
        if (anyEditable.size() >= 2) {
            return anyEditable.get(1); // index 1 → second editable input (usually Price)
        }

        throw new org.openqa.selenium.NoSuchElementException(
                "Price input not found for row; header index=" + priceCol +
                        " (tbody has hidden columns, used Discount-anchor fallback but no editable input was found)."
        );
    }

    /** Send value and force Angular change detection */
    private void typeValueAndCommit(WebElement input, String value) {
        jsExecutor.executeScript("arguments[0].scrollIntoView({block:'center'});", input);
        try {
            input.sendKeys(Keys.chord(Keys.CONTROL, "a"));
            input.sendKeys(Keys.DELETE);
        } catch (Exception ignore) { input.clear(); }
        input.sendKeys(value);
        input.sendKeys(Keys.TAB);
        try {
            jsExecutor.executeScript(
                    "const el=arguments[0], v=arguments[1];" +
                            "el.value=v;" +
                            "el.dispatchEvent(new Event('input', {bubbles:true}));" +
                            "el.dispatchEvent(new Event('change', {bubbles:true}));", input, value);
        } catch (Exception ignored) {}
    }

    /** Set the same price to all rows */
    private void setPriceForAllRows(String price) {
        WebElement table = summaryTable();
        int pCol = priceColumnIndex(table);
        List<WebElement> rows = bodyRows(table);
        if (rows.isEmpty()) throw new IllegalStateException("No product rows present.");
        for (WebElement row : rows) {
            WebElement input = priceInputInRow(row, pCol);
            typeValueAndCommit(input, price);
        }
    }

    /** (Optional) first-row only */
    private void setPriceFirstRowOnly(String price) {
        WebElement table = summaryTable();
        int pCol = priceColumnIndex(table);
        WebElement first = bodyRows(table).get(0);
        WebElement input = priceInputInRow(first, pCol);
        typeValueAndCommit(input, price);
    }
}