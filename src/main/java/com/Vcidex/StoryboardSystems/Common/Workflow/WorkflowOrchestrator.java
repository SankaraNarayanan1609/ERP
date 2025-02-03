package com.Vcidex.StoryboardSystems.Common.Workflow;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorkflowOrchestrator {
    private static final Logger logger = LogManager.getLogger(WorkflowOrchestrator.class);
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
                if (FeatureToggleService.isApprovalRequired("INVOICE_APPROVAL")) {
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
                logger.info("✅ Workflow Completed!");
                break;
        }
        logger.info("🔄 Current State: " + currentState);
    }
}