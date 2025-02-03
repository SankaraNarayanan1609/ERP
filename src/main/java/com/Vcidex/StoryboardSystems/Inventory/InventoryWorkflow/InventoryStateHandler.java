package com.Vcidex.StoryboardSystems.Inventory.InventoryWorkflow;

import com.Vcidex.StoryboardSystems.Common.Workflow.WorkflowState;

public class InventoryStateHandler {
    private WorkflowState state;

    public void updateState(WorkflowState newState) {
        this.state = newState;
    }

    public WorkflowState getCurrentState() {
        return state;
    }
}

