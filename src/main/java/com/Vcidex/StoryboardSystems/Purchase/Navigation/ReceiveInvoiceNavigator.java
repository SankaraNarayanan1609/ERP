package com.Vcidex.StoryboardSystems.Purchase.Navigation;

import com.Vcidex.StoryboardSystems.Common.NavigationManager;
import com.Vcidex.StoryboardSystems.Purchase.Pages.Invoice.ReceiveInvoiceListPage;
import com.Vcidex.StoryboardSystems.Purchase.Pages.Invoice.ReceiveInvoicePage;
import com.Vcidex.StoryboardSystems.Purchase.Pages.Invoice.ReceiveInvoiceTab;
import com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger.Layer;
import com.Vcidex.StoryboardSystems.Utils.Logger.ReportManager;
import com.aventstack.extentreports.ExtentTest;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.*;

public class ReceiveInvoiceNavigator {
    private final WebDriver driver;
    private final NavigationManager nav;
    private final ExtentTest rootTest;

    public ReceiveInvoiceNavigator(WebDriver driver, NavigationManager nav, ExtentTest rootTest) {
        this.driver = driver;
        this.nav = nav;
        this.rootTest = rootTest;
    }

    /** Backwards-compatible overload (no branch preselect). */
    public ReceiveInvoicePage openReceiveInvoicePage(@Nullable String orderRefNo, boolean isService) {
        return openReceiveInvoicePage(orderRefNo, isService, null);
    }

    /**
     * Navigate to Receive Invoice and (optionally) preselect Branch before trying to pick a row.
     * @param orderRefNo PO reference to filter/select; if null/blank, first row will be selected.
     * @param isService  choose Service tab when true; else Products.
     * @param branchName branch to set on the list if the Branch ng-select is present.
     */
    public ReceiveInvoicePage openReceiveInvoicePage(@Nullable String orderRefNo, boolean isService, @Nullable String branchName) {
        try (ReportManager.Scope s = ReportManager.with(rootTest.createNode("📄 Open Receive Invoice Page"))) {

            MasterLogger.step(Layer.UI, "Navigate ▶ Purchase → Payable → Invoice", () -> {
                nav.open(
                        "Purchase", "Payable", "Invoice",
                        d -> !d.findElements(By.xpath(
                                "//*[self::h1 or self::h2 or self::h3][contains(normalize-space(.),'Invoice Summary')]" +
                                        " | //app-invoice-summary | //*[@data-test='invoice-summary']"
                        )).isEmpty()
                );
                return null;
            });

            MasterLogger.step(Layer.UI, "Click '+ Receive Invoice'", () -> {
                By recvBtn = By.xpath("//button[contains(@title,'Receive Invoice') or contains(normalize-space(.),'Receive Invoice')]");
                try {
                    nav.waitUntilClickable(recvBtn, "Receive Invoice").click();
                } catch (Exception e) {
                    WebElement elm = driver.findElement(recvBtn);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true); arguments[0].click();", elm);
                }
                return null;
            });

            MasterLogger.step(Layer.VALIDATION, "Validate 'Receive Invoice' title", () -> {
                nav.waitUntilVisible(
                        By.xpath("//*[self::h1 or self::h2 or self::h3][contains(normalize-space(.),'Receive Invoice')]"),
                        "Receive Invoice title"
                );
                return null;
            });

            // Switch tab first so Branch filter belongs to the active tab content
            ReceiveInvoiceTab tab = isService ? ReceiveInvoiceTab.SERVICE : ReceiveInvoiceTab.PRODUCTS;
            ReceiveInvoiceListPage list = new ReceiveInvoiceListPage(driver, rootTest).switchTo(tab);

            // --- NEW: set Branch if the control exists on this tenant/page ---
            selectBranchIfPresent(branchName);

            // --- NEW: wait until at least one selectable row is present (some lists load slowly) ---
            waitForAnySelectableRow();

            if (orderRefNo == null || orderRefNo.isBlank()) {
                return list.selectFirstRow();
            }
            return list.selectByOrderRef(orderRefNo);
        }
    }

    /** Choose branch in ng-select if present; if branchName is null, chooses the first option. */
    private void selectBranchIfPresent(@Nullable String branchName) {
        // Try by formcontrolname first, then by label fallback
        By branchNg = By.xpath(
                "//div[contains(@class,'tab-pane') and contains(@class,'active')]"
                        + "//ng-select[@formcontrolname='branch_name' or @name='branch_name']"
                        + " | //div[contains(@class,'tab-pane') and contains(@class,'active')]"
                        + "//label[normalize-space()='Branch' or contains(normalize-space(),'Branch')]/following::*[contains(@class,'ng-select')][1]"
        );
        if (driver.findElements(branchNg).isEmpty()) {
            return; // no branch selector on this screen; nothing to do
        }

        MasterLogger.step(Layer.UI, "Select Branch" + (branchName != null ? (" = " + branchName) : " (first option)"), () -> {
            WebElement ng = nav.waitUntilClickable(branchNg, "Branch ng-select");
            // open dropdown
            try {
                ng.click();
            } catch (Exception e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", ng);
            }

            // type-to-filter if we have a target branch
            if (branchName != null && !branchName.isBlank()) {
                By input = By.cssSelector("div.ng-dropdown-panel input[type='text'], ng-select input[type='text']");
                if (!driver.findElements(input).isEmpty()) {
                    WebElement inp = driver.findElement(input);
                    inp.clear();
                    inp.sendKeys(branchName);
                }
                By option = By.xpath("//div[contains(@class,'ng-option') and .//*[normalize-space()='" + branchName + "']]");
                nav.waitUntilClickable(option, "Branch option").click();
            } else {
                // pick first option
                By first = By.cssSelector("div.ng-dropdown-panel .ng-option");
                nav.waitUntilClickable(first, "First Branch option").click();
            }

            // wait for grid reload after branch change
            nav.waitForAngularRequestsToFinish();
            nav.waitForOverlayClear();
            return null;
        });
    }

    /** Wait until a selectable row is visible in the currently active tab. */
    private void waitForAnySelectableRow() {
        By anyRowSelect = By.xpath(
                "//div[contains(@class,'tab-pane') and contains(@class,'active')]//table//tbody" +
                        "//tr[not(contains(@class,'dataTables_empty'))]//button[normalize-space()='Select' or @title='Select']"
        );
        // 30s is NavigationManager's default; this avoids the missing Duration overload
        nav.waitUntilClickable(anyRowSelect, "Invoice list row (Select)");
    }
}