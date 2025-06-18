package com.Vcidex.StoryboardSystems.Purchase.Flow.Commands;

import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseOrderData;
import com.Vcidex.StoryboardSystems.Purchase.Model.PurchaseScenario;
import com.Vcidex.StoryboardSystems.Purchase.Flow.Commands.StepCommand;
import com.Vcidex.StoryboardSystems.Inventory.POJO.MaterialInwardData;
import com.Vcidex.StoryboardSystems.Inventory.MaterialInwardDataFactory;
import com.Vcidex.StoryboardSystems.Inventory.Pages.Inward.MaterialInwardPage;
import com.Vcidex.StoryboardSystems.Utils.Logger.ReportManager;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.WebDriver;

public class GRNCommand implements StepCommand {

    @Override
    public void execute(WebDriver driver,
                        PurchaseOrderData data,
                        PurchaseScenario scen) {

        // 1) Turn your PO data into the page’s data type
        MaterialInwardData mid = MaterialInwardDataFactory.create();

        // 2) Grab the current ExtentTest so all page calls can log to the right node
        ExtentTest node = ReportManager.getTest();

        MaterialInwardPage page = new MaterialInwardPage(driver);

        // 3) Walk through the Material Inward flow
        page.assertOnSelectPurchaseOrder(node);
        page.selectPurchaseOrder(data.getPoRefNo(), node);
        page.clickAddInward(node);

        // This method now takes (MaterialInwardData, ExtentTest)
        page.fillInwardDetails(mid, node);

        // Submit also needs the test node
        page.clickSubmit(node);

        // Finally, verify it landed in the list
        page.assertInwardListed(mid.getDcNo(), node);
    }
}
