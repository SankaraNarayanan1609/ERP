// File: src/main/java/com/Vcidex/StoryboardSystems/Inventory/Navigation/MaterialInwardNavigator.java
package com.Vcidex.StoryboardSystems.Inventory.Navigation;

import com.Vcidex.StoryboardSystems.Common.NavigationManager;
import com.Vcidex.StoryboardSystems.Inventory.Pages.Inward.MaterialInwardPage;
import com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger.Layer;
import com.Vcidex.StoryboardSystems.Utils.Logger.ReportManager;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class MaterialInwardNavigator {
    private final WebDriver         driver;
    private final NavigationManager nav;
    private final ExtentTest        rootTest;

    public MaterialInwardNavigator(
            WebDriver driver,
            NavigationManager nav,
            ExtentTest rootTest
    ) {
        this.driver   = driver;
        this.nav      = nav;
        this.rootTest = rootTest;
    }

    /**
     * 1) Navigate to the Material Inward screen and return its page object.
     */
    public MaterialInwardPage openAddInwardModal() {
        // ── 1) make sure we’re on the right screen ──
        MaterialInwardPage page = openMaterialInwardScreen();

        // ── 2) click “Add Inward” ──
        ExtentTest node = rootTest.createNode("➕ Open Add Inward modal");
        ReportManager.setTest(node);
        MasterLogger.step(Layer.UI, "Click Add Inward", () -> {
            page.clickAddInward(ReportManager.getTest());
            // new: wait for spinner/overlay to disappear
            nav.waitForOverlayClear();
            // new: wait for the actual modal container
            nav.waitUntilVisible(
                    By.cssSelector("#addInwardModal .modal-content"),
                    "Waiting for Add Inward modal"
            );
            return null;
        });

        // switch back to the root test context
        ReportManager.setTest(rootTest);
        return page;
    }

    private MaterialInwardPage openMaterialInwardScreen() {
        ExtentTest navNode = rootTest.createNode("🔨 Navigate to Material Inward");
        ReportManager.setTest(navNode);

        MasterLogger.step(Layer.UI, "Ensure no overlays remain", () -> {
            nav.waitForOverlayClear();
            return null;
        });
        MasterLogger.step(Layer.UI, "Navigate ▶ Inventory → Inwards → Material Inward", () -> {
            nav.goTo("Inventory", "Inwards", "Material Inward");
            return null;
        });

        ReportManager.setTest(rootTest);
        return new MaterialInwardPage(driver);
    }

    /**
     * Click the “Select” button for the given PO reference on the listing page.
     */
    public void selectPurchaseOrder(MaterialInwardPage page, String poRef) {
        ExtentTest node = rootTest.createNode("🗃 Select Purchase Order: " + poRef);
        ReportManager.setTest(node);

        MasterLogger.step(Layer.UI,
                "Click Select for PO " + poRef,
                () -> {
                    // ensure the table rows are present
                    nav.waitUntilVisible(
                            By.cssSelector("table#purchaseOrderList tbody tr"),
                            "Waiting for PO list to render"
                    );
                    page.selectPurchaseOrder(poRef, ReportManager.getTest());
                    return null;
                }
        );

        ReportManager.setTest(rootTest);
    }
}