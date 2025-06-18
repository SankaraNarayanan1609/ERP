// src/main/java/com/Vcidex/StoryboardSystems/Purchase/Commands/PurchaseIndentCommand.java
package com.Vcidex.StoryboardSystems.Purchase.Flow.Commands;

import org.openqa.selenium.WebDriver;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseOrderData;
import com.Vcidex.StoryboardSystems.Purchase.Model.PurchaseScenario;
import com.Vcidex.StoryboardSystems.Purchase.Steps.StepCommand;
import com.Vcidex.StoryboardSystems.Purchase.Pages.PurchaseIndentPage;

public class PurchaseIndentCommand implements StepCommand {
    @Override
    public void execute(WebDriver driver,
                        PurchaseOrderData data,
                        PurchaseScenario scen) {
        PurchaseIndentPage page = new PurchaseIndentPage(driver);
        page.fillIndentForm(data);
        page.submit();
        // TODO: add logging/assertions
    }
}
