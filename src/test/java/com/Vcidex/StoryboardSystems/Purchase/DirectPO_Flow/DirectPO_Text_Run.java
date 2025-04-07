package com.Vcidex.StoryboardSystems.Purchase.DirectPO_Flow;

import com.Vcidex.StoryboardSystems.Common.Base.TestBase;
import com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order.Direct_PO;
import com.Vcidex.StoryboardSystems.Common.Authentication.LoginManager;
import com.Vcidex.StoryboardSystems.Utils.Config.ConfigManager;
import com.Vcidex.StoryboardSystems.Utils.Data.DataProviderManager;
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

            executeSafely(() -> Thread.sleep(3000), "Post-login debug wait");//Unhandled exception: java.lang.InterruptedException

            executeSafely(() -> logger.info("🧭 Post-login URL: {}", getDriver().getCurrentUrl()), "Log current URL");
            executeSafely(() -> logger.info("🔎 Driver state: {}", getDriver()), "Log WebDriver instance");

            Direct_PO directPO = new Direct_PO(getDriver());

            if (!scenarioData.containsKey("Branch") || !scenarioData.containsKey("Vendor")) {
                String err = "❌ Required data missing: Branch or Vendor";
                logger.error(err);
                throw new RuntimeException(err);
            }

            logger.info("📦 [{}] Scenario Data: {}", scenarioID, scenarioData);

            executeSafely(() -> directPO.navigateToPO(), "Navigate to Direct PO section");

            executeSafely(() -> {
                if (!directPO.isDirectPOPageLoaded()) {
                    logger.warn("⏳ Direct PO page not yet loaded. Retrying (attempt 1)...");
                    directPO.clickDirectPOButton();
                }
            }, "First attempt to load Direct PO page");

            executeSafely(() -> Thread.sleep(1000), "Wait after first retry");

            executeSafely(() -> {
                if (!directPO.isDirectPOPageLoaded()) {
                    logger.warn("⏳ Direct PO page still not loaded. Retrying (attempt 2)...");
                    directPO.clickDirectPOButton();
                } else {
                    logger.info("✅ Direct PO page is fully loaded.");
                }
            }, "Second attempt to load Direct PO page");

            executeSafely(() -> Thread.sleep(1000), "Wait after second retry");

            executeSafely(() -> {
                WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
                boolean isLoaded = wait.until(ExpectedConditions.textToBePresentInElementLocated(
                        By.xpath("//h4"), "Direct Purchase Order"));
                Assert.assertTrue(isLoaded, "❌ Direct PO Page did not load correctly.");
            }, "Wait for Direct PO page title");

            executeSafely(() -> logger.info("🪪 Direct PO Page title found."), "Log confirmation of PO title");

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
            executeSafely(() -> directPO.clickSubmitButton(), "Click Submit button");

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