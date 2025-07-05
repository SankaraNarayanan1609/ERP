// File: src/main/java/com/Vcidex/StoryboardSystems/Inventory/Navigation/MaterialInwardNavigator.java
package com.Vcidex.StoryboardSystems.Inventory.Navigation;

import com.Vcidex.StoryboardSystems.Common.NavigationManager;
import com.Vcidex.StoryboardSystems.Inventory.Pages.Inward.MaterialInwardPage;
import com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger.Layer;
import com.Vcidex.StoryboardSystems.Utils.Logger.ReportManager;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

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

    private MaterialInwardPage openMaterialInwardScreen() {
        ExtentTest navNode = rootTest.createNode("🔨 Navigate to Material Inwards");
        ReportManager.setTest(navNode);

        // 1) clear any overlays
        MasterLogger.step(Layer.UI, "Ensure no overlays remain", () -> {
            nav.waitForOverlayClear();
            return null;
        });

        // 2) click “Inwards → Material Inward” (with JS fallback)
        MasterLogger.step(Layer.UI, "Navigate ▶ Inventory → Inwards → Material Inwards", () -> {
            nav.goTo("Inventory", "Inwards", "Material Inwards");
            return null;
        });

        // 4) wait for the screen heading
        MasterLogger.step(Layer.VALIDATION, "Validate heading 'Material Inwards'", () -> {
            nav.waitUntilVisible(
                    By.xpath("//h3[contains(normalize-space(),'Material Inward')] | //h1[contains(normalize-space(),'Material Inward')]"),
                    "Waiting for Material Inwards heading"
            );
            return null;
        });

        ReportManager.setTest(rootTest);
        return new MaterialInwardPage(driver);
    }

    public MaterialInwardPage openAddInwardModal() {
        // 1) reach the listing page
        MaterialInwardPage page = openMaterialInwardScreen();

        // 2) click “Add Inward”
        ExtentTest node = rootTest.createNode("➕ Open Add Inward modal");
        ReportManager.setTest(node);
        MasterLogger.step(Layer.UI, "Click Add Inward", () -> {
            page.clickAddInward(ReportManager.getTest());
            nav.waitForOverlayClear();
            nav.waitUntilVisible(
                    By.cssSelector("#addInwardModal .modal-content"),
                    "Waiting for Add Inward modal"
            );
            return null;
        });
        ReportManager.setTest(rootTest);
        return page;
    }

    public void selectPurchaseOrder(MaterialInwardPage page, String poRef) {
        ExtentTest node = rootTest.createNode("🗃 Select Purchase Order: " + poRef);
        ReportManager.setTest(node);
        MasterLogger.step(Layer.UI,
                "Click Select for PO " + poRef,
                () -> {
                    // first wait for the PO list heading or filter if any
                    nav.waitUntilVisible(
                            By.xpath("//h3[contains(text(),'Purchase Orders')]"),
                            "Waiting for PO list heading"
                    );
                    // then for the rows
                    nav.waitUntilVisible(
                            By.cssSelector("table#purchaseOrderList tbody tr"),
                            "Waiting for PO rows"
                    );
                    page.selectPurchaseOrder(poRef, ReportManager.getTest());
                    return null;
                }
        );
        ReportManager.setTest(rootTest);
    }
}