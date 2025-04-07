package com.Vcidex.StoryboardSystems.Purchase.DirectPO_Flow;

import com.Vcidex.StoryboardSystems.Common.Base.TestBase;
import com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order.Direct_PO;
import com.Vcidex.StoryboardSystems.Common.Authentication.LoginManager;
import com.Vcidex.StoryboardSystems.Utils.Config.ConfigManager;
import com.Vcidex.StoryboardSystems.Utils.Data.DataProviderManager;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;
import org.testng.Assert;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
import java.util.Map;

public class DirectPO_Text_Run extends TestBase {

    @Test(dataProvider = "SingleScenarioProvider", dataProviderClass = DataProviderManager.class)
    public void runSingleScenario(String scenarioID, Map<String, String> scenarioData) throws InterruptedException {
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
            LoginManager loginManager = new LoginManager();
            loginManager.login(userId);

            Thread.sleep(3000); // Optional debugging delay
            logger.info("🧭 Post-login URL: {}", getDriver().getCurrentUrl());

            Direct_PO directPO = new Direct_PO(getDriver());
//            String terms = scenarioData.getOrDefault("Terms", "Standard Terms");
//
            if (!scenarioData.containsKey("Branch") || !scenarioData.containsKey("Vendor")) {
                String err = "❌ Required data missing: Branch or Vendor";
                logger.error(err);
                throw new RuntimeException(err);
            }

            logger.info("📦 [{}] Scenario Data: {}", scenarioID, scenarioData);

            // Navigate to Direct PO section
            directPO.navigateToPO();
            logger.info("🔁 Navigated to Direct PO section.");

            for (int i = 0; i < 2; i++) {
                if (!directPO.isDirectPOPageLoaded()) {
                    logger.warn("⏳ Direct PO page not yet loaded. Retrying (attempt {})...", i + 1);
                    directPO.clickDirectPOButton();
                    Thread.sleep(1000);
                } else {
                    logger.info("✅ Direct PO page is fully loaded.");
                    break;
                }
            }

            // Wait for Direct PO page header to appear
            WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
            boolean isLoaded = wait.until(ExpectedConditions.textToBePresentInElementLocated(
                    By.xpath("//h4"), "Direct Purchase Order"));
            Assert.assertTrue(isLoaded, "❌ Direct PO Page did not load correctly.");
            logger.info("🪪 Direct PO Page title found.");

            logger.info("📝 Filling Purchase Order details...");
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

            logger.info("📃 Selecting terms and submitting PO...");
            //directPO.selectTermsConditions(terms);
            directPO.clickSubmitButton();

            String confirmationMessage = directPO.getConfirmationMessage();
            logger.info("📬 Received confirmation message: {}", confirmationMessage);

            Assert.assertTrue(confirmationMessage.contains("PO"),
                    "❌ Expected confirmation to contain 'PO', but got: " + confirmationMessage);
            logger.info("✅ Scenario [{}] Passed.", scenarioID);

        } catch (Exception e) {
            logger.error("❌ Exception during runSingleScenario execution: {}", e.getMessage(), e);
            throw e; // Re-throw so TestNG marks it as failed and tearDownTest() can log/report it
        }
    }
}