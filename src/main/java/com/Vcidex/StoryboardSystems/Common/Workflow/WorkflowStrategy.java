package com.Vcidex.StoryboardSystems.Common.Workflow;

public interface WorkflowStrategy {
    void executePayment();
}

class SinglePaymentStrategy implements WorkflowStrategy {
    @Override
    public void executePayment() {
        System.out.println("Processing Single Invoice Payment...");
    }
}

class BulkPaymentStrategy implements WorkflowStrategy {
    @Override
    public void executePayment() {
        System.out.println("Processing Bulk Invoice Payment...");
    }
}
