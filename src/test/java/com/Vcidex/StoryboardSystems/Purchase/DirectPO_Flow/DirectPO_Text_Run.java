package com.Vcidex.StoryboardSystems.Purchase.DirectPO_Flow;

import com.Vcidex.StoryboardSystems.Common.Base.TestBase;
import com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order.Direct_PO;
import com.Vcidex.StoryboardSystems.Common.Authentication.LoginManager;
import com.Vcidex.StoryboardSystems.Utils.Data.DataProviderManager;
import org.testng.annotations.Test;
import org.testng.Assert;
import java.util.Map;

public class DirectPO_Text_Run extends TestBase {

    @Test(dataProvider = "SingleScenarioProvider", dataProviderClass = DataProviderManager.class)
    public void runSingleScenario(String scenarioID, Map<String, String> scenarioData) {
        LoginManager loginManager = new LoginManager();
        loginManager.login("vcidex", "Superadmin", "s");

        Direct_PO directPO = new Direct_PO(getDriver());
        directPO.navigateToDirectPO();

        String filePath = scenarioData.getOrDefault("FilePath", "C:\\default\\path\\default.xlsx");
        String terms = scenarioData.getOrDefault("Terms", "Standard Terms");

        directPO.uploadFile(filePath);
        directPO.selectTermsConditions(terms);
        directPO.clickSubmitButton();

        String confirmationMessage = directPO.getConfirmationMessage();
        Assert.assertTrue(confirmationMessage.contains("PO"), "PO creation failed!");

        String poNumber = directPO.fetchPONumberFromConfirmation();
        Assert.assertNotNull(poNumber, "PO Number not found in confirmation message.");
    }
}