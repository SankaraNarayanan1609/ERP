package com.Vcidex.StoryboardSystems.Purchase.PurchaseWorkflow;

import com.Vcidex.StoryboardSystems.Common.Workflow.WorkflowStrategy;

public class PurchaseStrategyHandler {
    private WorkflowStrategy strategy;

    public void setStrategy(WorkflowStrategy strategy) {
        this.strategy = strategy;
    }

    public void executePayment() {
        if (strategy != null) {
            strategy.executePayment();
        }
    }
}

