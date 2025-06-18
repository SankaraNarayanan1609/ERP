// src/main/java/com/Vcidex/StoryboardSystems/Purchase/Commands/DebitNoteCommand.java
package com.Vcidex.StoryboardSystems.Purchase.Flow.Commands;

import org.openqa.selenium.WebDriver;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseOrderData;
import com.Vcidex.StoryboardSystems.Purchase.Model.PurchaseScenario;
import com.Vcidex.StoryboardSystems.Purchase.Steps.StepCommand;
import com.Vcidex.StoryboardSystems.Purchase.Pages.DebitNotePage;

public class DebitNoteCommand implements StepCommand {
    @Override
    public void execute(WebDriver driver,
                        PurchaseOrderData data,
                        PurchaseScenario scen) {
        DebitNotePage page = new DebitNotePage(driver);
        page.fillDebitNoteForm(data);
        page.submit();
        // TODO: add logging/assertions
    }
}
