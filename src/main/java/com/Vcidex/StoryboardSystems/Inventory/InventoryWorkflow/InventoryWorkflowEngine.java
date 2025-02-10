package com.Vcidex.StoryboardSystems.Inventory.InventoryWorkflow;

import java.util.List;
import com.Vcidex.StoryboardSystems.Common.Workflow.WorkflowOrchestrator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class InventoryWorkflowEngine {
    private WorkflowOrchestrator orchestrator;

    private static final Logger logger = LogManager.getLogger(InventoryWorkflowEngine.class);

    public static void processInward(List<String> productNames) {
        logger.info("📦 Processing Inward for Products: {}", productNames);
        // Logic for inward processing
    }
}
