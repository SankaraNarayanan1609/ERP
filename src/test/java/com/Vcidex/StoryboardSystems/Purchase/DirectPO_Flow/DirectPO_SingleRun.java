package com.Vcidex.StoryboardSystems.Purchase.DirectPO_Flow;

import com.Vcidex.StoryboardSystems.Utils.Data.DataProviderManager;
import com.Vcidex.StoryboardSystems.Common.Workflow.WorkflowOrchestrator;
import com.Vcidex.StoryboardSystems.Purchase.PurchaseWorkflow.PurchaseWorkflowEngine;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class DirectPO_SingleRun {
    private static final Logger logger = LogManager.getLogger(DirectPO_SingleRun.class);

    @Test(dataProvider = "SingleScenarioProvider", dataProviderClass = DataProviderManager.class)
    @Parameters("scenarioName")
    public void runSingleScenario(String scenarioID, Map<String, Map<String, String>> scenarioData) {
        logger.info("🚀 Running Single Scenario: {}", scenarioID);

        // ✅ Create workflow instance
        String poId = scenarioData.getOrDefault("PO_Details", Map.of()).get("poId"); // ✅ Extract PO ID
        WorkflowOrchestrator orchestrator = new WorkflowOrchestrator("ClientID", poId); // ✅ Pass poId
        PurchaseWorkflowEngine workflowEngine = new PurchaseWorkflowEngine("ClientID", poId); // ✅ Pass poId


        // ✅ Workflow executes pages dynamically
        workflowEngine.startWorkflow();//Required type:String Provided:Map<java.lang.String,java.util.Map<java.lang.String,java.lang.String>>
    }
}
