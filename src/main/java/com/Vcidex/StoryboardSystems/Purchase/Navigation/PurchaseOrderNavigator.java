package com.Vcidex.StoryboardSystems.Purchase.Navigation;

import com.Vcidex.StoryboardSystems.Common.NavigationManager;
import com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Indent.PI_Add;
import com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order.DirectPO;
import com.Vcidex.StoryboardSystems.Purchase.POJO.IndentData;
import com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order.RaisePO;
import com.Vcidex.StoryboardSystems.Purchase.Support.PurchaseIndentMemory;
import com.Vcidex.StoryboardSystems.Utils.Logger.PerformanceLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.ReportManager;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class PurchaseOrderNavigator {
    private final WebDriver driver;
    private final NavigationManager nav;
    private final ExtentTest root;
    private final WebDriverWait wait;
    private boolean looksLikeAnId(String s) {
        return s != null && !s.isBlank() && s.matches(".*\\d.*") && !s.equalsIgnoreCase("Raise");
    }

    public PurchaseOrderNavigator(WebDriver driver, NavigationManager nav, ExtentTest root) {
        this.driver = driver;
        this.nav = nav;
        this.root = root;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    /** Go to PO Summary → Raise PO → Select Purchase Indent → filter by branch → select the specific indent. */
    /** Go to PO Summary → Raise PO → Select Purchase Indent → filter by branch → select the specific indent. */
    public RaisePO raisePOFromIndent(String branchName, String indentNo) {
        // 🔒 Resolve the effective values first
        String effBranch = (branchName != null && !branchName.isBlank())
                ? branchName
                : com.Vcidex.StoryboardSystems.Purchase.Support.PurchaseIndentMemory.branch();

        String effIndent = (indentNo != null && !indentNo.isBlank())
                ? indentNo
                : com.Vcidex.StoryboardSystems.Purchase.Support.PurchaseIndentMemory.indent();

        if (effIndent == null || effIndent.isBlank())
            throw new IllegalStateException("No PI Ref.No available to select.");
        // (Optional) you may also guard effBranch if your UI requires branch to be chosen

        // ⛳ Navigate to PO Summary and open the dialog
        nav.open("Purchase","Purchase","Purchase Order",
                d -> !d.findElements(By.xpath(
                        "//*[self::h1 or self::h2 or self::h3][contains(normalize-space(.),'Purchase Order Summary')]"
                                + " | //app-purchase-order-summary")).isEmpty());
        WebElement b = nav.waitUntilClickable(By.xpath("//button[contains(.,'Raise PO') or contains(@title,'Raise PO')]"), "Raise PO");
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", b);

        nav.waitUntilVisible(
                By.xpath("//*[self::h1 or self::h2 or self::h3][contains(normalize-space(.),'SELECT PURCHASE INDENT')]"),
                "Select Purchase Indent title"
        );

        // 🎯 Apply Branch from PI on the dialog (use effBranch)
        if (effBranch != null && !effBranch.isBlank()) {
            By branchNg = By.xpath(
                    "//ng-select[@formcontrolname='branch_name' or @name='branch_name']"
                            + " | //label[normalize-space()='Branch' or contains(normalize-space(),'Branch')]"
                            + "/following::*[contains(@class,'ng-select')][1]"
            );

            WebElement ng = nav.waitUntilClickable(branchNg, "Branch ng-select");
            ng.click();

            // ensure panel is open and type to filter
            By inp = By.cssSelector("div.ng-dropdown-panel input[type='text']");
            WebElement i = new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.visibilityOfElementLocated(inp));
            i.clear();
            i.sendKeys(effBranch);

            // exact match option (use xpLit to be safe)
            By option = By.xpath("//ng-dropdown-panel//div[contains(@class,'ng-option')][normalize-space(.)="
                    + xpLit(effBranch) + "]");
            nav.waitUntilClickable(option, "Branch option").click();

            // commit + let the grid reload
            nav.waitForAngularRequestsToFinish();
            nav.waitForOverlayClear();
            try { wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".dataTables_processing"))); }
            catch (Exception ignore) {}
        }

        // 🔎 Narrow by PI number (use effIndent)
        By searchBox = By.cssSelector("div.dataTables_filter input[type='search'], input[placeholder*='Search']");
        if (!driver.findElements(searchBox).isEmpty()) {
            WebElement sb = driver.findElement(searchBox);
            sb.clear();
            sb.sendKeys(effIndent);
            nav.waitForAngularRequestsToFinish();
            nav.waitForOverlayClear();
            try { wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".dataTables_processing"))); }
            catch (Exception ignore) {}
        }

        // ✅ Select the row
        if (!selectPiInAnyTable(driver, effIndent)) {
            throw new NoSuchElementException("PI '" + effIndent + "' not found in any DataTable.");
        }

        // 🧭 Land on PO form
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("app-purchase-order form, [data-test='po-form']")),
                ExpectedConditions.urlContains("purchaseorderadd")
        ));

        RaisePO page = new RaisePO(driver);
        // defensive: ensure PO page branch matches PI branch too
        if (effBranch != null && !effBranch.isBlank()) {
            page.ensureBranch(effBranch, ReportManager.getTest());
        }
        return page;
    }

    /** Open the dedicated Direct PO add page (not modal). */
    public DirectPO openDirectPO() {
        if (root != null) ReportManager.setTest(root);

        PerformanceLogger.start("OpenDirectPO");
        try (ReportManager.Scope scope = ReportManager.with(root.createNode("🧭 Open Direct PO"))) {

            // 1) Navigate to summary
            ReportManager.group("Navigate to Purchase Order Summary", () -> {
                String beforeUrl = driver.getCurrentUrl();
                ReportManager.info("🌐 Before nav URL: <i>" + beforeUrl + "</i>");

                nav.open("Purchase","Purchase","Purchase Order",
                        d -> !d.findElements(By.xpath(
                                "//*[self::h1 or self::h2 or self::h3][contains(normalize-space(.),'Purchase Order Summary')]"
                                        + " | //app-purchase-order-summary"
                        )).isEmpty());

                String afterUrl = driver.getCurrentUrl();
                ReportManager.info("🌐 After nav URL: <i>" + afterUrl + "</i>");
            });

            // 2) Click Direct PO button
            ReportManager.group("Click Direct PO", () -> {
                By directBtn = By.xpath("//button[contains(normalize-space(.),'Direct PO')]");
                long t0 = System.currentTimeMillis();

                WebElement btn = nav.waitUntilClickable(directBtn, "Direct PO");
                String btnText = (btn.getText() == null ? "" : btn.getText().trim());
                ReportManager.info("🔎 Button located. Text: <b>" + btnText + "</b>");

                try {
                    btn.click();
                    ReportManager.info("🖱️ Clicked Direct PO (native click).");
                } catch (WebDriverException e) {
                    ReportManager.info("🖱️ Native click failed – trying JS click.");
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
                }

                ReportManager.info("⏱️ Click path took " + (System.currentTimeMillis() - t0) + " ms.");
            });

            // 3) Wait for Direct PO page
            ReportManager.group("Wait for Direct PO page", () -> {
                WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(25));

                w.until(ExpectedConditions.urlContains("PmrTrnDirectpoAdd"));
                String nowUrl = driver.getCurrentUrl();
                ReportManager.info("🌐 Route ok: <code>" + nowUrl + "</code>");

                w.until(ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("ng-select[formcontrolname='branch_name'] .ng-select-container")
                ));

                nav.waitForOverlayClear();
                ReportManager.pass("✅ Direct PO page detected and stable.");
            });

            return new DirectPO(driver, root); // Cannot resolve constructor 'DirectPO(WebDriver, ExtentTest)'
        } finally {
            PerformanceLogger.end("OpenDirectPO");
        }
    }
    private static String xpLit(String s) {
        if (s == null) return "''";
        if (!s.contains("'")) return "'" + s + "'";
        return "concat('" + s.replace("'", "',\"'\",'") + "')";
    }

    @SuppressWarnings("unchecked")

    /** Search each DataTable's filter for the exact Ref.No and click Select in that row. */
    private static boolean selectPiInAnyTable(WebDriver driver, String piRef){
        java.util.List<WebElement> tables = driver.findElements(By.cssSelector("table.dataTable"));
        for (WebElement tbl : tables) {
            WebElement filter = null;
            try {
                filter = tbl.findElement(By.xpath("preceding::div[contains(@class,'dataTables_filter')][1]//input"));
            } catch (Exception ignored) {}
            if (filter != null) {
                filter.sendKeys(Keys.chord(Keys.CONTROL,"a"));
                filter.sendKeys(piRef);

                new WebDriverWait(driver, java.time.Duration.ofSeconds(5)).until(d->
                        !tbl.findElements(By.xpath(".//tbody//tr[td[contains(normalize-space(.),"+ xpLit(piRef) +")] and not(contains(@class,'dataTables_empty'))]")).isEmpty()
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
}