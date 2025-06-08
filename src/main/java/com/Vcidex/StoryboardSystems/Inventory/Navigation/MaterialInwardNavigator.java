package com.Vcidex.StoryboardSystems.Inventory.Navigation;

import com.Vcidex.StoryboardSystems.Common.NavigationManager;
import com.Vcidex.StoryboardSystems.Inventory.Pages.Inward.MaterialInwardPage;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.WebDriver;


/**
 * Encapsulates the sequence of steps needed to reach
 * and drive the Material Inward “Select Purchase Order”
 * screen and open its Add-Inward modal.
 */
public class MaterialInwardNavigator {

    private final WebDriver driver;
    private final NavigationManager nav;
    private final ExtentTest        rootTest;

    public MaterialInwardNavigator(WebDriver driver,
                                   NavigationManager nav,
                                   ExtentTest rootTest) {
        this.driver   = driver;
        this.nav      = nav;
        this.rootTest = rootTest;
    }

    /**
     * 1) Clicks through your menu to Material Inward
     * 2) Asserts the “Select Purchase Order” header is visible
     * 3) Returns the POM for further interactions
     */
    public MaterialInwardPage openSelectPurchaseOrderScreen() {
        ExtentTest node = rootTest.createNode("🔨 Open Material Inward (Select-PO)");

        nav.goTo("Inventory", "Inward", "Material Inward");

        // now pass *our* driver into the POM
        MaterialInwardPage page = new MaterialInwardPage(driver);
        page.assertOnSelectPurchaseOrder(node);
        return page;
    }

    /**
     * Finds & clicks the “Select” button for the given PO-ref on that screen.
     */
    public void selectPurchaseOrder(MaterialInwardPage page, String poRef) {
        ExtentTest node = rootTest.createNode("🗃 Select Purchase Order: " + poRef);
        page.selectPurchaseOrder(poRef, node);
    }

    /**
     * After a PO is selected, opens the “Add Inward” modal.
     */
    public void openAddInwardModal(MaterialInwardPage page) {
        ExtentTest node = rootTest.createNode("➕ Open Add Inward modal");
        page.clickAddInward(node);
    }
}