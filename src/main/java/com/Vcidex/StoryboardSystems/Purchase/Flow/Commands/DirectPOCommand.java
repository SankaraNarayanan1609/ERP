// src/main/java/com/Vcidex/StoryboardSystems/Purchase/Commands/DirectPOCommand.java
package com.Vcidex.StoryboardSystems.Purchase.Flow.Commands;

import org.openqa.selenium.WebDriver;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseOrderData;
import com.Vcidex.StoryboardSystems.Purchase.Model.PurchaseScenario;
import com.Vcidex.StoryboardSystems.Purchase.Flow.Commands.StepCommand;
import com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order.DirectPO;

public class DirectPOCommand implements StepCommand {
    @Override
    public void execute(WebDriver driver,
                        PurchaseOrderData pageData,
                        PurchaseScenario scen) {
        DirectPO page = new DirectPO(driver);
        page.submitDirectPO(pageData);   // only pageData fields
        page.submit();
    }
}