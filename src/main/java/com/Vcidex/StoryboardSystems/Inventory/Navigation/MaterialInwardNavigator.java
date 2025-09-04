// File: src/main/java/com/Vcidex/StoryboardSystems/Inventory/Navigation/MaterialInwardNavigator.java
package com.Vcidex.StoryboardSystems.Inventory.Navigation;

import com.Vcidex.StoryboardSystems.Common.BasePage;
import com.Vcidex.StoryboardSystems.Common.NavigationManager;
import com.Vcidex.StoryboardSystems.Inventory.Pages.Inward.MaterialInwardPage;
import com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger.Layer;
import com.Vcidex.StoryboardSystems.Utils.Logger.ReportManager;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.*;

public class MaterialInwardNavigator extends BasePage {
    private final NavigationManager nav;
    private final ExtentTest        rootTest;

    public MaterialInwardNavigator(WebDriver driver, NavigationManager nav, ExtentTest rootTest) {
        super(driver);
        this.nav      = nav;
        this.rootTest = rootTest;
    }

    public MaterialInwardPage openMaterialInwardScreen() {
        try (ReportManager.Scope s = ReportManager.with(rootTest.createNode("🔨 Navigate to Material Inwards"))) {
            MasterLogger.step(Layer.UI, "Navigate ▶ Inventory (header) → Inwards → Material Inwards", () -> {
                nav.open(
                        "Inventory", "Inwards", "Material Inwards",
                        d -> !d.findElements(By.xpath(
                                "//*[self::h1 or self::h2 or self::h3][contains(normalize-space(.),'Material Inward')]" +
                                        " | //app-material-inward | //*[@data-test='material-inward']"
                        )).isEmpty()
                );
                return null;
            });
        }
        return new MaterialInwardPage(driver);
    }

    /** Old behavior (kept): modal-only path. */
    public MaterialInwardPage openAddInwardModal() {
        MaterialInwardPage page = openMaterialInwardScreen();
        try (ReportManager.Scope s = ReportManager.with(rootTest.createNode("➕ Open Add Inward modal"))) {
            MasterLogger.step(Layer.UI, "Click Add Inward", () -> {
                page.clickAddInward(ReportManager.getTest());
                nav.waitUntilVisible(By.cssSelector("#addInwardModal .modal-content"), "Add Inward modal");
                return null;
            });
        }
        return page;
    }

    /** New tolerant behavior: accepts either modal OR routed page. */
    public MaterialInwardPage openAddInward() {
        MaterialInwardPage page = openMaterialInwardScreen();

        try (ReportManager.Scope s = ReportManager.with(rootTest.createNode("➕ Open Add Inward (modal or routed)"))) {
            MasterLogger.step(Layer.UI, "Click Add Inward button", () -> {
                page.clickAddInward(ReportManager.getTest()); // primary
                By addBtn = By.xpath(
                        "//*[self::button or self::a][contains(normalize-space(.),'Add Inward')]"
                                + " | //*[self::button or self::a][@title='Add Inward']"
                                + " | //*[@data-test='add-inward']"
                );
                if (!driver.findElements(addBtn).isEmpty()) {
                    WebElement b = driver.findElement(addBtn);
                    try { b.click(); } catch (Exception ignore) {
                        ((JavascriptExecutor) driver).executeScript(
                                "arguments[0].scrollIntoView({block:'center'}); arguments[0].click();", b);
                    }
                }
                return null;
            });

            By modal   = By.cssSelector("#addInwardModal .modal-content, [role='dialog'] .modal-content");
            By routed  = By.cssSelector("[data-test='inward-form'], form[formgroupname='inward'], app-material-inward form");
            By titleHx = By.xpath("//*[self::h1 or self::h2 or self::h3][contains(normalize-space(.),'Add Inward')]");

            long end = System.currentTimeMillis() + java.time.Duration.ofSeconds(45).toMillis();
            while (System.currentTimeMillis() < end) {
                nav.waitForAngularRequestsToFinish();
                nav.waitForOverlayClear();

                if (!driver.findElements(modal).isEmpty()) { waitUntilVisible(modal, "Add Inward modal"); return page; }
                if (!driver.findElements(routed).isEmpty()
                        || driver.getCurrentUrl().toLowerCase().contains("/inward")
                        || driver.getCurrentUrl().toLowerCase().contains("materialinward")) {
                    waitUntilVisible(routed, "Add Inward form (routed)"); return page;
                }
                if (!driver.findElements(titleHx).isEmpty()) { return page; }
                sleep(150);
            }
            throw new TimeoutException("Add Inward did not show a modal or route to a page within timeout");
        }
    }

    public void selectPurchaseOrder(MaterialInwardPage page, String poRef) {
        try (ReportManager.Scope s = ReportManager.with(rootTest.createNode("🗃 Select Purchase Order: " + poRef))) {
            MasterLogger.step(Layer.UI, "Wait for PO list & select", () -> {
                nav.waitUntilVisible(By.xpath("//h3[contains(normalize-space(.),'Purchase Orders')]"), "Purchase Orders heading");
                nav.waitUntilVisible(By.cssSelector("table#purchaseOrderList tbody tr"), "PO rows");
                page.selectPurchaseOrder(poRef, ReportManager.getTest());
                return null;
            });
        }
    }
}