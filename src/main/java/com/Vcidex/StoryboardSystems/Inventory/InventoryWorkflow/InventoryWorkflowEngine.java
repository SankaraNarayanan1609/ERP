package com.Vcidex.StoryboardSystems.Inventory.InventoryWorkflow;

import com.Vcidex.StoryboardSystems.Common.Workflow.WorkflowOrchestrator;

public class InventoryWorkflowEngine {
    private WorkflowOrchestrator orchestrator = new WorkflowOrchestrator();

    public void startWorkflow() {
        while (!orchestrator.getCurrentState().equals(WorkflowState.COMPLETED)) {
            orchestrator.nextStep();
        }
    }
}

