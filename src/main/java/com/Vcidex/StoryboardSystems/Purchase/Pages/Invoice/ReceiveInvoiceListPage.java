package com.Vcidex.StoryboardSystems.Purchase.Pages.Invoice;

import com.Vcidex.StoryboardSystems.Common.BasePage;
import com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.ReportManager;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.List;

public class ReceiveInvoiceListPage extends BasePage {
    private final ExtentTest node;

    // Active tab scoping
    private final By activeTabRoot        = By.cssSelector("div.tab-pane.active");
    private final By activeTabRows        = By.cssSelector("div.tab-pane.active table tbody tr");
    private final By activeFilterInput    = By.cssSelector("div.tab-pane.active div.dataTables_filter input");
    private final By activeSelectButtons  = By.xpath("//div[contains(@class,'tab-pane') and contains(@class,'active')]//table//tbody//tr[not(contains(@class,'dataTables_empty'))]//button[normalize-space()='Select']");
    private final By invoiceFormAnchor    = By.xpath("//input[@formcontrolname='invoice_ref_no']");
    private final By processingBadge      = By.cssSelector("div.dataTables_processing");

    public ReceiveInvoiceListPage(WebDriver driver, ExtentTest node) {
        super(driver);
        this.node = node;
    }

    public ReceiveInvoiceListPage switchTo(ReceiveInvoiceTab tab) {
        ReportManager.setTest(node);
        MasterLogger.step(MasterLogger.Layer.UI, "Switch tab → " + tab, () -> {
            // Preferred: using pane id
            By tabSelector = By.cssSelector("ul.nav-tabs a[data-bs-toggle='tab'][href='#" + tab.paneId() + "']");
            try {
                waitUntilClickable(tabSelector, "Invoice tab").click();
            } catch (Exception e) {
                // Fallback by text (Products/Service)
                By textTab = By.xpath("//ul[contains(@class,'nav-tabs')]//a[normalize-space()='" + (tab == ReceiveInvoiceTab.SERVICE ? "Service" : "Products") + "']");
                waitUntilClickable(textTab, "Invoice tab (text)").click();
            }
            waitForOverlayClear();
            wait.withTimeout(Duration.ofSeconds(10))
                    .until(d -> !d.findElements(activeTabRows).isEmpty());
            return null;
        });
        return this;
    }

    /** Preferred path: click the row whose Order Ref matches; fallback to first row if not found. */
    public ReceiveInvoicePage selectByOrderRef(String orderRefNo) {
        ReportManager.setTest(node);
        MasterLogger.step(MasterLogger.Layer.UI, "Select PO row for: " + orderRefNo, () -> {
            // 1) Try to find/select without filtering (fast path)
            if (!clickRowSelectFor(orderRefNo, Duration.ofSeconds(5))) {
                // 2) Use DataTables search in active tab (scoped filter)
                WebElement filter = waitUntilVisible(activeFilterInput, "Search");
                filter.sendKeys(Keys.chord(Keys.CONTROL, "a"));
                filter.sendKeys(orderRefNo);
                // DataTables filters on keyup; send ENTER to be explicit
                filter.sendKeys(Keys.ENTER);

                // Wait for processing to finish (if present)
                try { wait.until(ExpectedConditions.invisibilityOfElementLocated(processingBadge)); }
                catch (TimeoutException ignored) {}

                if (!clickRowSelectFor(orderRefNo, Duration.ofSeconds(8))) {
                    ReportManager.getTest().warning("Could not find row for '" + orderRefNo + "'. Falling back to first row.");
                    clickFirstRowSelect();
                }
            }
            return null;
        });
        return waitForFormOpen();
    }

    /** When there is no orderRef supplied, select the best visible row. */
    public ReceiveInvoicePage selectFirstRow() {
        ReportManager.setTest(node);
        MasterLogger.step(MasterLogger.Layer.UI, "Select first visible PO row", () -> {
            clickFirstRowSelect();
            return null;
        });
        return waitForFormOpen();
    }

    // ---------- internals ----------

    private ReceiveInvoicePage waitForFormOpen() {
        waitForOverlayClear();
        wait.until(ExpectedConditions.visibilityOfElementLocated(invoiceFormAnchor));
        return new ReceiveInvoicePage(driver);
    }

    private void clickFirstRowSelect() {
        waitUntilVisible(activeTabRoot, "Active tab");
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(activeSelectButtons));
        try {
            btn.click();
        } catch (Exception e) {
            jsExecutor.executeScript("arguments[0].scrollIntoView({block:'center'}); arguments[0].click();", btn);
        }
        waitForOverlayClear();
    }

    /** Try to click the Select button for a specific order ref within timeout; returns true if clicked. */
    private boolean clickRowSelectFor(String orderRefNo, Duration timeout) {
        String canon = orderRefNo.replaceAll("\\s+", ""); // remove spaces so "VCX /-..." matches "VCX/-..."
        String literal = escapeXpathLiteral(canon);

        String rowBtnXpath =
                "//div[contains(@class,'tab-pane') and contains(@class,'active')]//table//tbody//tr[" +
                        "td[translate(normalize-space(.), ' ', '')=" + literal + " or " +
                        "contains(translate(normalize-space(.), ' ', ''), " + literal + ")]" +
                        "]//button[normalize-space()='Select']";

        By selectBtn = By.xpath(rowBtnXpath);

        try {
            WebElement btn = new org.openqa.selenium.support.ui.WebDriverWait(driver, timeout)
                    .until(ExpectedConditions.elementToBeClickable(selectBtn));
            try {
                btn.click();
            } catch (Exception e) {
                jsExecutor.executeScript("arguments[0].scrollIntoView({block:'center'}); arguments[0].click();", btn);
            }
            waitForOverlayClear();
            return true;
        } catch (TimeoutException notFound) {
            return false;
        }
    }

    /** Properly escape any quote combination for XPath string literal usage. */
    public static String escapeXpathLiteral(String s) {
        if (!s.contains("'")) return "'" + s + "'";
        if (!s.contains("\"")) return "\"" + s + "\"";
        // both quotes present → concat split parts with quotes
        StringBuilder sb = new StringBuilder("concat(");
        String[] parts = s.split("'");
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) sb.append(", \"'\", ");
            sb.append('"').append(parts[i]).append('"');
        }
        sb.append(')');
        return sb.toString();
    }
}