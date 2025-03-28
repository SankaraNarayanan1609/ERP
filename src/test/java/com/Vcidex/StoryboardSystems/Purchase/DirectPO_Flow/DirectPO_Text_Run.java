package com.Vcidex.StoryboardSystems.Purchase.DirectPO_Flow;

import com.Vcidex.StoryboardSystems.Common.Base.TestBase;
import com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order.Direct_PO;
import com.Vcidex.StoryboardSystems.Common.Authentication.LoginManager;
import com.Vcidex.StoryboardSystems.Utils.Data.DataProviderManager;
import com.Vcidex.StoryboardSystems.Utils.Reporting.ExtentTestManager;
import com.Vcidex.StoryboardSystems.Utils.Reporting.ErrorHandler;
import com.Vcidex.StoryboardSystems.Utils.Reporting.RetryAnalyzer;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.WebDriver;
import java.util.Map;

public class DirectPO_Text_Run extends TestBase {

    @Test(dataProvider = "SingleScenarioProvider", dataProviderClass = DataProviderManager.class, retryAnalyzer = RetryAnalyzer.class)
    @Parameters("scenarioName")
    public void runSingleScenario(String scenarioID, Map<String, String> scenarioData) {
        ExtentTest test = ExtentTestManager.createTest("Test Scenario: " + scenarioID);
        WebDriver driver = getDriver();

        ErrorHandler.safeExecute(driver, () -> {
            try {
                // ✅ Step 1: Login using LoginManager
                new LoginManager().login("vcidex", "Superadmin", "s");
                test.info("✅ Login successful!");

                // ✅ Step 2: Validate scenario data
                if (scenarioData == null || scenarioData.isEmpty()) {
                    throw new IllegalArgumentException("❌ No data found for scenario: " + scenarioID);
                }

                String branchName = scenarioData.get("Branch Name");
                if (branchName == null || branchName.isEmpty()) {
                    throw new IllegalArgumentException("❌ Branch Name is missing in the scenario data.");
                }
                test.info("📍 Branch: " + branchName);

                // ✅ Extract FilePath and Terms with Defaults
                String filePath = scenarioData.getOrDefault("FilePath", "C:\\default\\path\\default.xlsx");
                String terms = scenarioData.getOrDefault("Terms", "Standard Terms");

                test.info("Using FilePath: " + filePath + " and Terms: " + terms);

                // ✅ Step 3: Run DirectPO POM
                Direct_PO directPO = new Direct_PO(driver);
                directPO.createDirectPO(filePath, terms);
                test.info("✅ Direct PO created successfully!");
            } catch (Exception e) {
                ErrorHandler.handleException(driver, e, scenarioID);
            }
        }, "Run Single Scenario", false, "DirectPO_Flow");

        ExtentTestManager.flushReports(); // Ensure report is flushed
    }
}