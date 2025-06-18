package com.Vcidex.StoryboardSystems.Purchase.Flow.Commands;

import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseOrderData;
import com.Vcidex.StoryboardSystems.Purchase.Model.PurchaseScenario;
import org.openqa.selenium.WebDriver;

public interface StepCommand {
    void execute(WebDriver driver,
                 PurchaseOrderData pageData,
                 PurchaseScenario scen);
}