package com.Vcidex.StoryboardSystems.Common.Workflow;

public class WorkflowOrchestrator {
    private WorkflowState currentState;

    public WorkflowOrchestrator() {
        this.currentState = WorkflowState.PURCHASE_INDENT; // Default start
    }

    public void nextStep() {
        switch (currentState) {
            case PURCHASE_INDENT:
                currentState = WorkflowState.DIRECT_PO;
                break;
            case DIRECT_PO:
                if (RuleEngine.evaluateRule("IS_SERVICE_PRODUCT")) {
                    currentState = WorkflowState.INVOICE;
                } else {
                    currentState = WorkflowState.INWARD;
                }
                break;
            case INWARD:
                currentState = WorkflowState.INVOICE;
                break;
            case INVOICE:
                if (RuleEngine.evaluateRule("APPROVAL_REQUIRED")) {
                    currentState = WorkflowState.INVOICE_APPROVAL;
                } else {
                    currentState = WorkflowState.PAYMENT;
                }
                break;
            case INVOICE_APPROVAL:
                currentState = WorkflowState.PAYMENT;
                break;
            case PAYMENT:
                currentState = WorkflowState.COMPLETED;
                break;
            default:
                System.out.println("✅ Workflow Completed!");
                break;
        }
        System.out.println("🔄 Current State: " + currentState);
    }
}