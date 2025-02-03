package com.Vcidex.StoryboardSystems.Purchase.PurchaseWorkflow;

import com.Vcidex.StoryboardSystems.Common.Workflow.WorkflowOrchestrator;

public class PurchaseWorkflowEngine {
    private WorkflowOrchestrator orchestrator = new WorkflowOrchestrator();

    public void startWorkflow() {
        while (!orchestrator.getCurrentState().equals(WorkflowState.COMPLETED)) {
            orchestrator.nextStep();
        }
    }
}