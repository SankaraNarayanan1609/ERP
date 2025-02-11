package com.Vcidex.StoryboardSystems.Purchase.PurchaseWorkflow;

import com.Vcidex.StoryboardSystems.Common.Workflow.WorkflowOrchestrator;
import com.Vcidex.StoryboardSystems.Common.Workflow.WorkflowState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class PurchaseWorkflowEngine {
    private static final Logger logger = LogManager.getLogger(PurchaseWorkflowEngine.class);
    private final WorkflowOrchestrator orchestrator;

    public PurchaseWorkflowEngine(String clientID, String poId) { // ✅ Accepts 2 arguments
        this.orchestrator = new WorkflowOrchestrator(clientID, poId); // ✅ Correct number of arguments
    }

    public void startWorkflow() {
        while (!orchestrator.getCurrentState().equals(WorkflowState.COMPLETED)) {
            orchestrator.nextStep(); // ✅ No error now
        }
    }

    public void processInvoice(List<String> productNames) {
        logger.info("📄 Processing Invoice for Products: {}", productNames);
        // Implement actual invoice processing logic here
    }
}
