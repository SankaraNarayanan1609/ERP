package com.Vcidex.StoryboardSystems.Purchase.DirectPO_Flow;

import com.Vcidex.StoryboardSystems.Common.Authentication.LoginManager;
import com.Vcidex.StoryboardSystems.Common.Base.TestBase;
import com.Vcidex.StoryboardSystems.Utils.Data.DataProviderManager;
import com.Vcidex.StoryboardSystems.Utils.Reporting.ExtentTestManager;
import com.Vcidex.StoryboardSystems.Common.Workflow.WorkflowOrchestrator;
import com.Vcidex.StoryboardSystems.Purchase.PurchaseWorkflow.PurchaseWorkflowEngine;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.WebDriver;

import java.util.Map;

public class DirectPO_SingleRun extends TestBase {
    private static final Logger logger = LogManager.getLogger(DirectPO_SingleRun.class);

    @Test(dataProvider = "SingleScenarioProvider", dataProviderClass = DataProviderManager.class)
    @Parameters("scenarioName")
    public void runSingleScenario(String scenarioID, Map<String, String> scenarioData) {
        ExtentTest test = ExtentTestManager.createTest("Test Scenario: " + scenarioID);
        test.info("🚀 Running Single Scenario: " + scenarioID);

        WebDriver driver = getDriver(); // ✅ Fetch WebDriver from ThreadSafeDriverManager

        try {
            // ✅ Step 1: Perform Login
            logger.info("🔑 Logging in before executing the workflow...");
            LoginManager loginManager = new LoginManager();
            loginManager.login("test", 0); // ✅ Pass environment & user index
            test.info("✅ Login successful!");

            // ✅ Step 2: Validate Scenario Data
            if (scenarioData == null || scenarioData.isEmpty()) {
                test.fail("❌ No data found for scenario: " + scenarioID);
                throw new RuntimeException("No data found for scenario: " + scenarioID);
            }

            String branchName = scenarioData.get("Branch Name");
            test.info("📌 Extracted Branch Name: " + branchName);

            String vendorName = scenarioData.getOrDefault("Vendor Name", "Unknown");
            test.info("🏢 Vendor Name: " + vendorName);

            String paymentTerms = scenarioData.getOrDefault("Payment Terms", "Not Provided");
            test.info("💳 Payment Terms: " + paymentTerms);

            // ✅ Step 3: Execute Workflow
            WorkflowOrchestrator orchestrator = new WorkflowOrchestrator("ClientID", branchName);
            PurchaseWorkflowEngine workflowEngine = new PurchaseWorkflowEngine("ClientID", branchName);

            test.info("🔄 Starting workflow execution for Branch: " + branchName);
            workflowEngine.startWorkflow();
            test.pass("✅ Workflow execution completed for Branch: " + branchName);

        } catch (Exception e) {
            test.fail("❌ Exception occurred: " + e.getMessage());
            logger.error("❌ Exception during test execution: {}", e.getMessage(), e);
            throw new RuntimeException("Test execution failed", e);
        } finally {
            ExtentTestManager.flushReports();
        }
    }
}