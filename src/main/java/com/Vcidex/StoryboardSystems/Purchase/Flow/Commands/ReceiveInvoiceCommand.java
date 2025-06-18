// src/main/java/com/Vcidex/StoryboardSystems/Purchase/Commands/ReceiveInvoiceCommand.java
package com.Vcidex.StoryboardSystems.Purchase.Flow.Commands;

import org.openqa.selenium.WebDriver;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseOrderData;
import com.Vcidex.StoryboardSystems.Purchase.Model.PurchaseScenario;
import com.Vcidex.StoryboardSystems.Purchase.Steps.StepCommand;
import com.Vcidex.StoryboardSystems.Purchase.Pages.ReceiveInvoicePage;

public class ReceiveInvoiceCommand implements StepCommand {
    @Override
    public void execute(WebDriver driver,
                        PurchaseOrderData data,
                        PurchaseScenario scen) {
        ReceiveInvoicePage page = new ReceiveInvoicePage(driver);
        page.fillInvoiceForm(data);
        page.submit();
        // TODO: add logging/assertions
    }
}