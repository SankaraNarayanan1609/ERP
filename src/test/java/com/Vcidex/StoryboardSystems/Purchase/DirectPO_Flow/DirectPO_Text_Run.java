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

    // Recursion guard variable
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

            executeSafely((Assert.ThrowingRunnable) () -> { //java.lang.RuntimeException: ❌ Failed to execute: Fill Purchase Order Details
                Thread.sleep(3000);
            }, "Post-login debug wait");


            executeSafely((Assert.ThrowingRunnable) () -> {
                NavigationHelper navigator = new NavigationHelper(getDriver());
                navigator.navigateToModuleAndMenu(NavigationData.PO);
            }, "Navigate to Purchase > Purchase > Purchase Order");

            Direct_PO directPO = new Direct_PO(getDriver());

            executeSafely((Assert.ThrowingRunnable) () -> {
                logger.info("🖱️ Attempting to click Direct PO button...");
                directPO.clickDirectPOButton();
            }, "Click Direct PO Button");

            // ✅ Guard against recursion - make sure the PO is not created more than once
            if (!isDirectPOCreated) {
                isDirectPOCreated = true;
                // Additional debug log
                logger.info("Recursion guard passed, proceeding to create PO");

                executeSafely((Assert.ThrowingRunnable) () -> {
                    logger.info("Filling Purchase Order details...");
                    directPO.createDirectPO(
                            scenarioData.get("Branch Name"),
                            scenarioData.get("Vendor Name"),
                            scenarioData.get("Currency"),
                            scenarioData.get("Quantity"),
                            scenarioData.get("Price"),
                            scenarioData.get("TermsAndConditions")
                    );
                }, "Fill Purchase Order Details");

                // Rest of the code...

            } else {
                logger.warn("⚠️ Recursion prevented, Direct PO has already been created. Skipping...");
            }

        } catch (Exception e) { //'catch' without 'try' //
            logger.error("❌ Exception during runSingleScenario execution: {}", e.getMessage(), e);
            throw e; //
        } finally {
            // Resetting the recursion guard after each test run to ensure no residual state
            isDirectPOCreated = false;
        }
    }
}