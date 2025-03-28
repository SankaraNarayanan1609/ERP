//package com.Vcidex.StoryboardSystems.Purchase.DirectPO_Flow;
//
//import com.Vcidex.StoryboardSystems.Utils.Data.DataProviderManager;
//import org.testng.annotations.Test;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import java.util.Map;
//public class DirectPO_FullRun {
//    private static final Logger logger = LogManager.getLogger(DirectPO_FullRun.class);
//
//    @Test(dataProvider = "AllScenariosProvider", dataProviderClass = DataProviderManager.class)
//    public void runAllScenarios(String scenarioID, Map<String, Map<String, String>> scenarioData) {
//        logger.info("🚀 Running Scenario: {}", scenarioID);
//
//        String poId = scenarioData.getOrDefault("PO_Details", Map.of()).get("poId"); // ✅ Extract PO ID
//        WorkflowOrchestrator orchestrator = new WorkflowOrchestrator("ClientID", poId); // ✅ Pass poId
//        PurchaseWorkflowEngine workflowEngine = new PurchaseWorkflowEngine("ClientID", poId); // ✅ Pass poId
//
//        workflowEngine.startWorkflow();
//    }
//}
