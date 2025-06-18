// src/main/java/com/Vcidex/StoryboardSystems/Purchase/Commands/PurchaseReturnCommand.java
package com.Vcidex.StoryboardSystems.Purchase.Flow.Commands;

import org.openqa.selenium.WebDriver;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseOrderData;
import com.Vcidex.StoryboardSystems.Purchase.Model.PurchaseScenario;
import com.Vcidex.StoryboardSystems.Purchase.Steps.StepCommand;
import com.Vcidex.StoryboardSystems.Purchase.Pages.PurchaseReturnPage;

public class PurchaseReturnCommand implements StepCommand {
    @Override
    public void execute(WebDriver driver,
                        PurchaseOrderData data,
                        PurchaseScenario scen) {
        PurchaseReturnPage page = new PurchaseReturnPage(driver);
        page.selectRejectedItems(data);
        page.submitReturn();
        // TODO: add logging/assertions
    }
}
