// File: src/main/java/com/Vcidex/StoryboardSystems/Purchase/Navigation/PurchaseIndentNavigator.java
package com.Vcidex.StoryboardSystems.Purchase.Navigation;

import com.Vcidex.StoryboardSystems.Common.NavigationManager;
import com.Vcidex.StoryboardSystems.Purchase.POJO.IndentData;
import com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Indent.PI_Add;
import com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order.RaisePO;
import com.Vcidex.StoryboardSystems.Purchase.Support.PurchaseIndentMemory;
import com.Vcidex.StoryboardSystems.Utils.Logger.ReportManager;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.LinkedHashSet;

public class PurchaseIndentNavigator {
    private final WebDriver driver;
    private final NavigationManager nav;
    private final ExtentTest root;
    private final WebDriverWait wait;

    public PurchaseIndentNavigator(WebDriver driver, NavigationManager nav, ExtentTest root) {
        this.driver = driver;
        this.nav = nav;
        this.root = root;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    /** Go to Purchase Indent add screen, create one and return indent number. */
    public String createIndent(IndentData data) {
        if (root != null) ReportManager.setTest(root);
        ReportManager.info("[Navigator] createIndent() – start");

        // 1) Go to PI Summary and take BEFORE snapshot
        nav.open("Purchase", "Purchase", "Purchase Indent",
                d -> !d.findElements(By.xpath(
                        "//*[self::h1 or self::h2 or self::h3][contains(normalize-space(.),'Purchase Indent')] | //app-purchase-indent"
                )).isEmpty());
        nav.waitForOverlayClear();
        java.util.Set<String> before = snapshotAllPiRefs(driver);
        ReportManager.info("📸 BEFORE snapshot size: " + before.size());

        // 2) Open Raise PI
        By raisePiBtn = By.xpath("//button[.//i[contains(@class,'fa-plus')] and contains(normalize-space(.),'Raise PI')]");
        if (!fastPollClick(raisePiBtn, 3000)) {
            try { nav.waitUntilClickable(raisePiBtn, "Raise PI").click(); }
            catch (Exception e) { tryClickByCssTextContains("button", "Raise PI"); }
        }
        new WebDriverWait(driver, Duration.ofSeconds(12)).until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("app-pmr-trn-raise-requisition")),
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//h3[contains(normalize-space(),'Raise Purchase Indent')]")),
                ExpectedConditions.urlMatches("(?i).*/pmr/.*requisition.*|.*purchaseindentadd.*")
        ));

        // 3) Fill & submit (may or may not return the number)
        String echoed = new PI_Add(driver, root).create(data);

        // 4) Ensure we are back on SUMMARY (some tenants don’t auto-navigate)
        nav.open("Purchase", "Purchase", "Purchase Indent",
                d -> !d.findElements(By.xpath(
                        "//*[self::h1 or self::h2 or self::h3][contains(normalize-space(.),'Purchase Indent')] | //app-purchase-indent"
                )).isEmpty());
        nav.waitForOverlayClear();

        // 5) AFTER snapshot and diff
        java.util.Set<String> after = snapshotAllPiRefs(driver);
        ReportManager.info("📸 AFTER snapshot size: " + after.size());
        java.util.Set<String> newOnes = setDiff(after, before);

        String picked = newOnes.stream().findFirst().orElse(null);
        if (picked == null && echoed != null && !echoed.isBlank()) picked = echoed.trim();

        if (picked == null) {
            ReportManager.warn("Couldn’t detect new PI via diff and no toast number. Consider adding branch/date heuristics.");
            throw new IllegalStateException("Unable to determine the newly created PI Ref.No from summary.");
        }

        ReportManager.info("📌 New PI captured by diff: <b>" + picked + "</b> (Branch: " + data.getBranchName() + ")");
        com.Vcidex.StoryboardSystems.Purchase.Support.PurchaseIndentMemory
                .set(com.Vcidex.StoryboardSystems.Purchase.Support.PurchaseIndentMemory.branch(), picked);
        PurchaseIndentMemory.set(data.getBranchName(), picked);
        return picked;
    }

    /** Micro-poll the DOM every ~100ms (<= maxMillis) and JS-click as soon as found & enabled. */
    private boolean fastPollClick(By by, int maxMillis) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        long end = System.currentTimeMillis() + maxMillis;

        while (System.currentTimeMillis() < end) {
            try {
                List<WebElement> els = driver.findElements(by); // Cannot resolve symbol 'List'
                if (!els.isEmpty()) { // Cannot resolve method 'isEmpty()'
                    WebElement el = els.get(0); // Cannot resolve method 'get(int)'
                    // ensure in view; click via JS to avoid overlay/intercept
                    js.executeScript("arguments[0].scrollIntoView({block:'center'});", el);
                    js.executeScript("arguments[0].click();", el);
                    return true;
                }
            } catch (StaleElementReferenceException | NoSuchElementException ignored) {
                // retry next tick
            }
            try { Thread.sleep(100); } catch (InterruptedException ignored) {}
        }
        return false;
    }

    /** Ultra-simple fallback: find the first <tag> with innerText containing the needle and JS-click it. */
    private boolean tryClickByCssTextContains(String tag, String needle) {
        String script =
                "var n=arguments[1].toLowerCase();" +
                        "var els=document.querySelectorAll(arguments[0]);" +
                        "for (var i=0;i<els.length;i++) {" +
                        "  var t=(els[i].innerText||'').trim().toLowerCase();" +
                        "  if (t.indexOf(n)>-1) { els[i].scrollIntoView({block:'center'}); els[i].click(); return true; }" +
                        "}" +
                        "return false;";
        Object ok = ((JavascriptExecutor) driver).executeScript(script, tag, needle);
        return Boolean.TRUE.equals(ok);
    }

    /** On "SELECT PURCHASE INDENT" → pick Branch and select the row that matches indentNo, then land on PO form. */
    public RaisePO raisePOFromIndent(String branchName, String indentNo) {
        try (ReportManager.Scope s = ReportManager.with(root.createNode("⬆️ Raise PO from Indent (" + indentNo + ")"))) {

            // Fallbacks from memory (branch used in PI + last PI ref)
            if (branchName == null || branchName.isBlank()) {
                branchName = PurchaseIndentMemory.branch();
            }
            if (indentNo == null || indentNo.isBlank()) {
                indentNo = PurchaseIndentMemory.indent();
            }
            if (indentNo == null || indentNo.isBlank()) {
                throw new IllegalStateException("No PI Ref.No provided or remembered for branch '" + branchName + "'");
            }

            // Purchase → Purchase → Purchase Order (Summary)
            nav.open("Purchase", "Purchase", "Purchase Order",
                    d -> !d.findElements(By.xpath(
                            "//*[self::h1 or self::h2 or self::h3][contains(normalize-space(.),'Purchase Order Summary')]"
                                    + " | //app-purchase-order-summary")).isEmpty()
            );

            // Click “Raise PO”
            By raiseBtn = By.xpath("//button[contains(.,'Raise PO') or contains(@title,'Raise PO')]");
            WebElement b = nav.waitUntilClickable(raiseBtn, "Raise PO");
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", b);

            // Wait for “SELECT PURCHASE INDENT”
            nav.waitUntilVisible(
                    By.xpath("//*[self::h1 or self::h2 or self::h3][contains(normalize-space(.),'SELECT PURCHASE INDENT')]"),
                    "Select Purchase Indent title"
            );

            // ==== Branch filter (use branch from PI) ====
            if (branchName != null && !branchName.isBlank()) {
                By branchNg = By.xpath(
                        "//ng-select[@formcontrolname='branch_name' or @name='branch_name']"
                                + " | //label[normalize-space()='Branch' or contains(normalize-space(),'Branch')]"
                                + "/following::*[contains(@class,'ng-select')][1]"
                );
                if (!driver.findElements(branchNg).isEmpty()) {
                    WebElement ng = nav.waitUntilClickable(branchNg, "Branch ng-select");
                    ng.click();
                    By inp = By.cssSelector("div.ng-dropdown-panel input[type='text']");
                    if (!driver.findElements(inp).isEmpty()) {
                        WebElement i = driver.findElement(inp);
                        i.clear();
                        i.sendKeys(branchName);
                    }
                    By option = By.xpath("//div[contains(@class,'ng-option')][.//*[normalize-space()='" + branchName + "']]");
                    nav.waitUntilClickable(option, "Branch option").click();
                    nav.waitForAngularRequestsToFinish();
                    nav.waitForOverlayClear();
                }
            }

            // Hide DataTables “processing” spinner (if any)
            By processing = By.cssSelector(".dataTables_processing");
            try { wait.until(ExpectedConditions.invisibilityOfElementLocated(processing)); } catch (Exception ignore) {}

            // Optional global search
            By searchBox = By.cssSelector("div.dataTables_filter input[type='search'], input[placeholder*='Search']");
            if (!driver.findElements(searchBox).isEmpty()) {
                WebElement sb = driver.findElement(searchBox);
                sb.clear();
                sb.sendKeys(indentNo);
                nav.waitForAngularRequestsToFinish();
                nav.waitForOverlayClear();
                try { wait.until(ExpectedConditions.invisibilityOfElementLocated(processing)); } catch (Exception ignore) {}
            }

            // Select the PI row (per-table robust search + click "Select")
            if (!selectPiInAnyTable(driver, indentNo)) {
                throw new NoSuchElementException("PI '" + indentNo + "' not found in any DataTable.");
            }

            // --- NEW: handle intermediate confirm page after selecting PI ---
            try {
                // Give the route a moment to change to "...Addconfirm..."
                new WebDriverWait(driver, Duration.ofSeconds(3))
                        .until(ExpectedConditions.or(
                                ExpectedConditions.urlContains("PurchaseorderAddconfirm"),
                                ExpectedConditions.presenceOfElementLocated(
                                        By.xpath("//*[self::h1 or self::h2 or self::h3][contains(.,'Confirm') or contains(.,'Confirmation')]"))
                        ));

                if (driver.getCurrentUrl().toLowerCase().contains("purchaseorderaddconfirm")) {
                    By proceed = By.xpath(
                            "//button[not(@disabled) and (" +
                                    "normalize-space()='Proceed' or normalize-space()='Confirm' or " +
                                    "contains(.,'Create Purchase Order') or contains(.,'Raise PO')" +
                                    ")]"
                    );
                    WebElement go = new WebDriverWait(driver, Duration.ofSeconds(10))
                            .until(ExpectedConditions.elementToBeClickable(proceed));
                    ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView({block:'center'});", go);
                    try { go.click(); } catch (Exception e) {
                        ((JavascriptExecutor)driver).executeScript("arguments[0].click();", go);
                    }
                }
            } catch (TimeoutException ignore) {
                // No confirm page in this tenant – continue
            }

            // Land on PO form
            new WebDriverWait(driver, Duration.ofSeconds(30)).until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(By.cssSelector("app-purchase-order form, [data-test='po-form']")),
                    ExpectedConditions.urlContains("PmrTrnPurchaseorderAdd") // final add page in this tenant
            ));

            // Build PO page and enforce same branch defensively
            RaisePO page = new RaisePO(driver);
            if (branchName != null && !branchName.isBlank()) {
                page.ensureBranch(branchName, ReportManager.getTest());
            }
            return page;
        }
    }
    // --- SNAPSHOT HELPERS -------------------------------------------------------
    /** Prefer DataTables API if present; else fallback to DOM pager. */
    private Set<String> snapshotAllPiRefs(WebDriver driver) {
        try {
            return snapshotAllPiRefs_viaDTAPI(driver);
        } catch (Exception ignore) {
            return snapshotAllPiRefs_viaDOM(driver);
        }
    }

    @SuppressWarnings("unchecked")
    private static Set<String> snapshotAllPiRefs_viaDTAPI(WebDriver driver) {
        String js =
                "const out = new Set();" +
                        "const toArray = x => Array.prototype.slice.call(x);" +
                        "const tables = toArray(document.querySelectorAll('table.dataTable'));" +
                        "for (const tbl of tables) {" +
                        "  const ths = toArray(tbl.querySelectorAll('thead th'));" +
                        "  const colIdx = ths.findIndex(th => /\\bRef\\.\\s*No\\b/i.test((th.textContent||'').trim()));" +
                        "  if (colIdx < 0) continue;" +
                        "  if (!window.jQuery || !jQuery.fn.DataTable) { continue; }" +
                        "  const api = jQuery(tbl).DataTable();" +
                        "  const info = api.page.info();" +
                        "  const current = info.page;" +
                        "  const pages = info.pages || 1;" +
                        "  for (let p = 0; p < pages; p++) {" +
                        "    api.page(p).draw(false);" +
                        "    const rows = toArray(tbl.querySelectorAll('tbody tr'));" +
                        "    for (const tr of rows) {" +
                        "      if ((tr.className||'').includes('dataTables_empty')) continue;" +
                        "      const tds = toArray(tr.querySelectorAll('td'));" +
                        "      if (tds[colIdx]) {" +
                        "        const ref = (tds[colIdx].textContent||'').trim();" +
                        "        if (ref) out.add(ref);" +
                        "      }" +
                        "    }" +
                        "  }" +
                        "  api.page(current).draw(false);" +
                        "}" +
                        "return Array.from(out);";

        Object res = ((JavascriptExecutor) driver).executeScript(js);
        List<String> list = (List<String>) res;
        return new LinkedHashSet<>(list);
    }

    private static Set<String> snapshotAllPiRefs_viaDOM(WebDriver driver) {
        Set<String> out = new LinkedHashSet<>();
        List<WebElement> tables = driver.findElements(By.cssSelector("table.dataTable"));

        for (WebElement tbl : tables) {
            // find "Ref. No" column
            int refIdx = -1;
            List<WebElement> ths = tbl.findElements(By.cssSelector("thead th"));
            for (int i = 0; i < ths.size(); i++) {
                String h = ths.get(i).getText() == null ? "" : ths.get(i).getText().trim();
                if (h.matches("(?i).*\\bRef\\.\\s*No\\b.*")) { refIdx = i; break; }
            }
            if (refIdx < 0) continue;

            // paginator (nearest following container)
            WebElement paginate = null;
            try {
                paginate = tbl.findElement(By.xpath("following::div[contains(@class,'dataTables_paginate')][1]"));
            } catch (NoSuchElementException ignored) {}

            // go to first page
            if (paginate != null) {
                while (true) {
                    List<WebElement> prev = paginate.findElements(By.xpath(
                            ".//a[contains(@class,'paginate_button') and normalize-space()='Previous' and not(contains(@class,'disabled'))]"
                    ));
                    if (prev.isEmpty()) break;
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", prev.get(0));
                    new WebDriverWait(driver, java.time.Duration.ofSeconds(2)).until(d -> true);
                }
            }

            // iterate pages
            boolean more = true;
            while (more) {
                for (WebElement tr : tbl.findElements(By.cssSelector("tbody tr"))) {
                    String cls = tr.getAttribute("class") == null ? "" : tr.getAttribute("class");
                    if (cls.contains("dataTables_empty")) continue;
                    List<WebElement> tds = tr.findElements(By.cssSelector("td"));
                    if (refIdx < tds.size()) {
                        String ref = tds.get(refIdx).getText() == null ? "" : tds.get(refIdx).getText().trim();
                        if (!ref.isEmpty()) out.add(ref);
                    }
                }
                if (paginate == null) break;
                List<WebElement> next = paginate.findElements(By.xpath(
                        ".//a[contains(@class,'paginate_button') and normalize-space()='Next' and not(contains(@class,'disabled'))]"
                ));
                if (next.isEmpty()) {
                    more = false;
                } else {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", next.get(0));
                    new WebDriverWait(driver, java.time.Duration.ofSeconds(2)).until(d -> true);
                }
            }
        }
        return out;
    }

    private static boolean selectPiInAnyTable(WebDriver driver, String piRef) {
        java.util.List<WebElement> tables = driver.findElements(By.cssSelector("table.dataTable"));
        for (WebElement tbl : tables) {
            WebElement filter = null;
            try { filter = tbl.findElement(By.xpath("preceding::div[contains(@class,'dataTables_filter')][1]//input")); } catch (Exception ignored) {}
            if (filter != null) {
                filter.sendKeys(Keys.chord(Keys.CONTROL,"a"));
                filter.sendKeys(piRef);

                new WebDriverWait(driver, java.time.Duration.ofSeconds(5)).until(d ->
                        !tbl.findElements(By.xpath(".//tbody//tr[td[contains(normalize-space(.),"+ xpLit(piRef) +")] and not(contains(@class,'dataTables_empty'))]")).isEmpty() // Cannot resolve method 'xpLit' in 'PurchaseIndentNavigator'
                );

                java.util.List<WebElement> btns = tbl.findElements(By.xpath(
                        ".//tbody//tr[td[contains(normalize-space(.),"+ xpLit(piRef) +")]]//button[@title='Select' or normalize-space()='Select']"
                ));
                if (!btns.isEmpty()) {
                    ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView({block:'center'});", btns.get(0));
                    try { btns.get(0).click(); } catch (Exception e) {
                        ((JavascriptExecutor)driver).executeScript("arguments[0].click();", btns.get(0));
                    }
                    return true;
                }
            }
        }
        return false;
    }

    // Escape any single quotes in a Java string so it can be used as an XPath literal.
    private static String xpLit(String s) {
        if (s == null) return "''";
        if (!s.contains("'")) return "'" + s + "'";
        // turn "ab'cd" into concat('ab',"'",'cd')
        return "concat('" + s.replace("'", "',\"'\",'") + "')";
    }

    private static Set<String> setDiff(Set<String> after, Set<String> before) {
        Set<String> out = new LinkedHashSet<>(after);
        out.removeAll(before);
        return out;
    }
}