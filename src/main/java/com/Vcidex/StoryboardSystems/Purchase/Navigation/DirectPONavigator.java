// File: src/main/java/com/Vcidex/StoryboardSystems/Purchase/Navigation/DirectPONavigator.java
package com.Vcidex.StoryboardSystems.Purchase.Navigation;

import com.Vcidex.StoryboardSystems.Common.NavigationManager;
import com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order.DirectPO;
import com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger.Layer;
import com.Vcidex.StoryboardSystems.Utils.Logger.ReportManager;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.WebDriver;

public class DirectPONavigator {
    private final WebDriver         driver;
    private final NavigationManager nav;
    private final ExtentTest        rootTest;

    public DirectPONavigator(
            WebDriver driver,
            NavigationManager nav,
            ExtentTest rootTest
    ) {
        this.driver   = driver;
        this.nav      = nav;
        this.rootTest = rootTest;
    }

    /**
     * Navigate → open & wait for the Direct PO modal.
     * Returns a ready-to-use DirectPO page object.
     */
    public DirectPO openDirectPO() {
        ExtentTest node = rootTest.createNode("🔨 Open Direct PO modal");
        ReportManager.setTest(node);

        // 1) click through menus
        MasterLogger.step(Layer.UI,
                "Navigate to Purchase Order page",
                () -> {
                    nav.goTo("Purchase", "Purchase", "Purchase Order");
                    return null;
                }
        );

        // 2) open the modal
        DirectPO poPage = new DirectPO(driver);
        MasterLogger.step(Layer.UI,
                "Open Direct PO modal window",
                () -> {
                    poPage.openDirectPOModal(ReportManager.getTest());
                    return null;
                }
        );

        // 3) restore test context
        ReportManager.setTest(rootTest);
        return poPage;
    }
}