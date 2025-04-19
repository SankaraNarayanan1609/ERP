package com.Vcidex.StoryboardSystems.Purchase.DirectPO_Flow;

import com.Vcidex.StoryboardSystems.Common.Base.TestBase;
import com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order.Direct_PO;
import com.Vcidex.StoryboardSystems.Common.Authentication.LoginManager;
import com.Vcidex.StoryboardSystems.Utils.Config.ConfigManager;
import com.Vcidex.StoryboardSystems.Utils.Data.DataProviderManager;
import com.Vcidex.StoryboardSystems.Utils.Navigation.NavigationHelper;
import com.Vcidex.StoryboardSystems.Utils.Navigation.NavigationData;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.Map;

import static com.Vcidex.StoryboardSystems.Utils.Reporting.ErrorHandler.executeSafely;

public class DirectPOTest extends TestBase {

    private static boolean isDirectPOCreated = false;

    @Test(dataProvider = "SingleScenarioProvider", dataProviderClass = DataProviderManager.class)
    public void runSingleScenario(String scenarioID, Map<String, String> scenarioData) {
        try {
            logger.info("▶️ Starting Scenario ID: {}", scenarioID);
            logger.info("Initial recursion guard state: {}", isDirectPOCreated);

            String environment = System.getProperty("env", "test");
            String userId = System.getProperty("userId", "0");

            Map<String, String> user = ConfigManager.getUserById(environment, userId);
            if (user == null) {
                String err = "❌ No user found with userId: " + userId + " in environment: " + environment;
                logger.error(err);
                throw new RuntimeException(err);
            }

            logger.info("🔐 Logging in with user ID: {}", userId);

            executeSafely((Assert.ThrowingRunnable) () -> {
                LoginManager loginManager = new LoginManager();
                loginManager.login(userId);
            }, "Login to application");

            // Optional: Replace with explicit wait
            executeSafely((Assert.ThrowingRunnable) () -> {
                Thread.sleep(3000);
            }, "Post-login wait (3s)");

            executeSafely((Assert.ThrowingRunnable) () -> {
                NavigationHelper navigator = new NavigationHelper(getDriver());
                navigator.navigateToModuleAndMenu(NavigationData.PO);
            }, "Navigate to Purchase > Purchase > Purchase Order");

            Direct_PO directPO = new Direct_PO(getDriver());

            executeSafely((Assert.ThrowingRunnable) () -> {
                logger.info("🖱️ Attempting to click Direct PO button...");
                directPO.clickDirectPOButton();
            }, "Click Direct PO Button");

            if (!isDirectPOCreated) {
                isDirectPOCreated = true;

                executeSafely((Assert.ThrowingRunnable) () -> {
                    logger.info("Filling Purchase Order details...");
                    directPO.createDirectPO(
                            scenarioData.get("Branch Name"),
                            scenarioData.get("Vendor Name"),
                            scenarioData.get("Currency"),
                            scenarioData.get("Quantity"),
                            scenarioData.get("Price"),
                            scenarioData.get("TermsAndConditions"),
                            scenarioData.get("Day"),
                            scenarioData.get("Month"),
                            scenarioData.get("Year")
                    );
                }, "Fill Purchase Order Details");

                // 🧩 Add your next steps (GRN, Invoice, Payment...)

            } else {
                logger.warn("⚠️ Recursion prevented, Direct PO has already been created. Skipping...");
            }

        } catch (Exception e) {
            logger.error("❌ Exception during runSingleScenario execution: {}", e.getMessage(), e);
            throw e;
        } finally {
            isDirectPOCreated = false;
        }
    }
}