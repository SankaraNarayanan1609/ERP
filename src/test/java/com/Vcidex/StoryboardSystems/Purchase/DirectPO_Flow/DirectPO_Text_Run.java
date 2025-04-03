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
        // Get environment dynamically (default to 'test' if not specified)
        String environment = System.getProperty("env", "test");

        // Get userId dynamically (default to "0" - Superadmin)
        String userId = System.getProperty("userId", "0");

        // Fetch user credentials based on userId
        Map<String, String> user = ConfigManager.getUserById(environment, userId);
        if (user == null) {
            throw new RuntimeException("❌ No user found with userId: " + userId + " in environment: " + environment);
        }

        // Perform login
        LoginManager loginManager = new LoginManager();
        loginManager.login(userId);  // ✅ Fixed method signature issue

        // Navigate to Direct PO page
        Direct_PO directPO = new Direct_PO(getDriver());  // ✅ Ensured WebDriver instance is passed
        directPO.navigateToDirectPO();

        // Fetch and validate file path
//        String filePath = scenarioData.get("FilePath");
//        if (filePath == null || filePath.isEmpty()) {
//            throw new RuntimeException("❌ File path is missing!");
//        }

        String terms = scenarioData.getOrDefault("Terms", "Standard Terms");

        // Perform Direct PO actions
        //directPO.uploadFile(filePath);
        directPO.selectTermsConditions(terms);

        if (!directPO.isDirectPOPageLoaded()) {
            directPO.clickDirectPOButton();
        } else {
            System.out.println("✅ Direct PO page is already loaded.");
        }

        directPO.clickSubmitButton();
    }
}