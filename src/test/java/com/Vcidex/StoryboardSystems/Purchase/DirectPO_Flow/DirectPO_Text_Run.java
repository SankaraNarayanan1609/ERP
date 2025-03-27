package com.Vcidex.StoryboardSystems.Purchase.DirectPO_Flow;

import com.Vcidex.StoryboardSystems.Common.Authentication.LoginManager;
import com.Vcidex.StoryboardSystems.Common.Base.TestBase;
import com.Vcidex.StoryboardSystems.Utils.Data.DataProviderManager;
import com.Vcidex.StoryboardSystems.Utils.Reporting.ExtentTestManager;
import com.Vcidex.StoryboardSystems.Utils.Reporting.ErrorHandler;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.WebDriver;
import java.util.Map;

import static com.Vcidex.StoryboardSystems.Utils.ThreadSafeDriverManager.getDriver;

public class DirectPO_Text_Run {

    @Test(dataProvider = "SingleScenarioProvider", dataProviderClass = DataProviderManager.class)
    @Parameters("scenarioName")
    public void runSingleScenario(String scenarioID, Map<String, String> scenarioData) {
        ExtentTest test = ExtentTestManager.createTest("Test Scenario: " + scenarioID);
        test.info("🚀 Running Single Scenario: " + scenarioID);

        WebDriver driver = getDriver();

        try {
            // ✅ Step 1: Login using safeExecute
            ErrorHandler.safeExecute(driver, () -> {
                new LoginManager().login("vcidex", "Superadmin", "s");
                test.info("✅ Login successful!");
            }, scenarioID, false, "N/A");

            // ✅ Step 2: Validate scenario data using safeExecute
            ErrorHandler.safeExecute(driver, () -> {
                if (scenarioData == null || scenarioData.isEmpty()) {
                    throw new IllegalArgumentException("❌ No data found for scenario: " + scenarioID);
                }

                String branchName = scenarioData.get("Branch Name");
                if (branchName == null || branchName.isEmpty()) {
                    throw new IllegalArgumentException("❌ Branch Name is missing in the scenario data.");
                }
                test.info("📍 Branch: " + branchName);
            }, scenarioID, false, "N/A");

            test.info("✅ Test completed for scenario: " + scenarioID);
        } catch (Exception e) {
            test.fail("❌ Test Failed for scenario: " + scenarioID + " - " + e.getMessage());
            ErrorHandler.handleException(driver, e, scenarioID);
        } finally {
            ExtentTestManager.flushReports();
        }
    }
}