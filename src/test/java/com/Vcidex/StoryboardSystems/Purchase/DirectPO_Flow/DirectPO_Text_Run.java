package com.Vcidex.StoryboardSystems.Purchase.DirectPO_Flow;

import com.Vcidex.StoryboardSystems.Common.Base.TestBase;
import com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order.Direct_PO;
import com.Vcidex.StoryboardSystems.Common.Authentication.LoginManager;
import com.Vcidex.StoryboardSystems.Utils.Config.ConfigManager;
import com.Vcidex.StoryboardSystems.Utils.Data.DataProviderManager;
import com.Vcidex.StoryboardSystems.Utils.Navigation.NavigationHelper;
import com.Vcidex.StoryboardSystems.Utils.Navigation.NavigationData;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.Map;

import static com.Vcidex.StoryboardSystems.Utils.Reporting.ErrorHandler.executeSafely;

public class DirectPO_Text_Run extends TestBase {

    @Test(dataProvider = "SingleScenarioProvider", dataProviderClass = DataProviderManager.class)
    public void runSingleScenario(String scenarioID, Map<String, String> scenarioData) {
        try {
            logger.info("▶️ Starting Scenario ID: {}", scenarioID);

            String environment = System.getProperty("env", "test");
            String userId = System.getProperty("userId", "0");

            Map<String, String> user = ConfigManager.getUserById(environment, userId);
            if (user == null) {
                String err = "❌ No user found with userId: " + userId + " in environment: " + environment;
                logger.error(err);
                throw new RuntimeException(err);
            }

            logger.info("🔐 Logging in with user ID: {}", userId);

            executeSafely(() -> {
                LoginManager loginManager = new LoginManager();
                loginManager.login(userId);
            }, "Login to application");

            executeSafely(() -> Thread.sleep(3000), "Post-login debug wait");

            executeSafely(() -> {
                NavigationHelper navigator = new NavigationHelper(getDriver());
                navigator.navigateToModuleAndMenu(NavigationData.PO);
            }, "Navigate to Purchase > Purchase > Purchase Order");

            Direct_PO directPO = new Direct_PO(getDriver());

            executeSafely(() -> {
                logger.info("🖱️ Attempting to click Direct PO button...");
                directPO.clickDirectPOButton();
            }, "Click Direct PO Button");

            // ✅ Fill form using single method
            executeSafely(() -> directPO.fillPurchaseOrderDetails(
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
            ), "Fill Purchase Order Details");

            executeSafely(() -> logger.info("📃 Submitting PO..."), "Log before clicking Submit");
            executeSafely(() -> directPO.clickSubmit(), "Click Submit button");

            executeSafely(() -> {
                String confirmationMessage = directPO.getConfirmationMessage();
                logger.info("📬 Received confirmation message: {}", confirmationMessage);
                Assert.assertTrue(confirmationMessage.contains("PO"),
                        "❌ Expected confirmation to contain 'PO', but got: " + confirmationMessage);
            }, "Verify confirmation message content");

            executeSafely(() -> logger.info("✅ Scenario [{}] Passed.", scenarioID), "Final scenario success log");

        } catch (Exception e) {
            logger.error("❌ Exception during runSingleScenario execution: {}", e.getMessage(), e);
            throw e; // Re-throw so TestNG marks it as failed
        }
    }
}