package com.Vcidex.StoryboardSystems.Purchase.PurchaseWorkflow;

import com.Vcidex.StoryboardSystems.Common.Workflow.WorkflowState;

public class PurchaseStateHandler {
    private WorkflowState state;

    public void updateState(WorkflowState newState) {
        this.state = newState;
    }

    public WorkflowState getCurrentState() {
        return state;
    }
}
