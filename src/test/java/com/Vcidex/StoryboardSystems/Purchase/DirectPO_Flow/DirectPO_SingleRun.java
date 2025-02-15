package com.Vcidex.StoryboardSystems.Purchase.DirectPO_Flow;

import com.Vcidex.StoryboardSystems.Common.Authentication.LoginManager;
import com.Vcidex.StoryboardSystems.Common.Base.TestBase;
import com.Vcidex.StoryboardSystems.Utils.Data.DataProviderManager;
import com.Vcidex.StoryboardSystems.Utils.Reporting.ExtentTestManager;
import com.Vcidex.StoryboardSystems.Common.Workflow.WorkflowEngine;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.WebDriver;

import java.util.Collections;
import java.util.Map;

public class DirectPO_SingleRun extends TestBase {
    private static final Logger logger = LogManager.getLogger(DirectPO_SingleRun.class);

    @Test(dataProvider = "SingleScenarioProvider", dataProviderClass = DataProviderManager.class)
    @Parameters("scenarioName")
    public void runSingleScenario(String scenarioID, Map<String, String> scenarioData) {
        ExtentTest test = ExtentTestManager.createTest("Test Scenario: " + scenarioID);
        test.info("🚀 Running Single Scenario: " + scenarioID);

        WebDriver driver = getDriver();

        try {
            // ✅ Step 1: Login
            logger.info("🔑 Initiating login process...");
            LoginManager loginManager = new LoginManager();
            loginManager.login("vcidex", "Superadmin", "s");
            test.info("✅ Login successful!");

            // ✅ Step 2: Validate scenario data
            if (scenarioData == null || scenarioData.isEmpty()) {
                throw new RuntimeException("❌ No data found for scenario: " + scenarioID);
            }

            String branchName = scenarioData.get("Branch Name");
            test.info("📍 Branch: " + branchName);

            // ✅ Step 3: Initialize and start workflow
            logger.info("🔄 Initializing workflow...");
            WorkflowEngine workflowEngine = new WorkflowEngine("ClientID");
            workflowEngine.executeWorkflow(Collections.singletonList(branchName));

            test.pass("✅ Workflow execution completed successfully.");

        } catch (Exception e) {
            test.fail("❌ Exception: " + e.getMessage());
            logger.error("❌ [Test] Exception occurred: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            ExtentTestManager.flushReports();
        }
    }
}