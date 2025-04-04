package com.Vcidex.StoryboardSystems.Purchase.DirectPO_Flow;

import com.Vcidex.StoryboardSystems.Common.Base.TestBase;
import com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order.Direct_PO;
import com.Vcidex.StoryboardSystems.Common.Authentication.LoginManager;
import com.Vcidex.StoryboardSystems.Utils.Config.ConfigManager;
import com.Vcidex.StoryboardSystems.Utils.Data.DataProviderManager;
import org.testng.annotations.Test;
import org.testng.Assert;
import java.util.Map;

public class DirectPO_Text_Run extends TestBase {

    @Test(dataProvider = "SingleScenarioProvider", dataProviderClass = DataProviderManager.class)
    public void runSingleScenario(String scenarioID, Map<String, String> scenarioData) {
        String environment = System.getProperty("env", "test");
        String userId = System.getProperty("userId", "0");

        Map<String, String> user = ConfigManager.getUserById(environment, userId);
        if (user == null) {
            throw new RuntimeException("❌ No user found with userId: " + userId + " in environment: " + environment);
        }

        LoginManager loginManager = new LoginManager();
        loginManager.login(userId);

        Direct_PO directPO = new Direct_PO(getDriver());

        String terms = scenarioData.getOrDefault("Terms", "Standard Terms");

        if (!scenarioData.containsKey("Branch") || !scenarioData.containsKey("Vendor")) {
            throw new RuntimeException("❌ Required data missing: Branch/Vendor");
        }

        System.out.println("Scenario Data: " + scenarioData);

        // Navigate to Direct PO
        directPO.navigateToPO();
        if (!directPO.isDirectPOPageLoaded()) {
            directPO.clickDirectPOButton();
        }

        // ✅ Call fillPurchaseOrderDetails with extracted data
        directPO.fillPurchaseOrderDetails(
                scenarioData.get("Branch"),
                scenarioData.get("Vendor"),
                scenarioData.get("Currency"),
                scenarioData.get("Quantity"),
                scenarioData.get("Price"),
                scenarioData.get("Discount"),
                scenarioData.get("AddOnCharges"),
                scenarioData.get("AdditionalDiscount"),
                scenarioData.get("FreightCharges"),
                scenarioData.get("AdditionalTax"),
                scenarioData.get("RoundOff")
        );

        directPO.selectTermsConditions(terms);
        directPO.clickSubmitButton();

        String confirmationMessage = directPO.getConfirmationMessage();
        Assert.assertTrue(confirmationMessage.contains("PO"), "❌ PO number not found in confirmation message!");
    }
}