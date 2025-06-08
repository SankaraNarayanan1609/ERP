package com.Vcidex.StoryboardSystems.Purchase.Navigation;

import com.Vcidex.StoryboardSystems.Common.NavigationManager;
import com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order.DirectPO;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.WebDriver;

/**
 * Encapsulates exactly the steps needed to reach and open the
 * Direct PO modal from anywhere in your app.
 */
public class DirectPONavigator {
    private final WebDriver          driver;
    private final NavigationManager  nav;
    private final ExtentTest         rootTest;

    public DirectPONavigator(WebDriver driver,
                             NavigationManager nav,
                             ExtentTest rootTest) {
        this.driver   = driver;
        this.nav      = nav;
        this.rootTest = rootTest;
    }

    /**
     * Navigate menus → instantiate DirectPO → open the modal → return it.
     */
    public DirectPO openDirectPO() {
        // create a child node under your main report
        ExtentTest node = rootTest.createNode("🔨 Open Direct PO modal");

        // 1) Click through the nav menus
        nav.goTo("Purchase", "Purchase", "Purchase Order");

        // 2) Build the POM and open its modal
        DirectPO poPage = new DirectPO(driver);
        poPage.openDirectPOModal(node);

        // 3) Return the ready-to-use page object
        return poPage;
    }
}