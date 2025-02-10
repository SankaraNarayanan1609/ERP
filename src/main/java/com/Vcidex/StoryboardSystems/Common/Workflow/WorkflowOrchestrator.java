package com.Vcidex.StoryboardSystems.Common.Workflow;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.List;

public class WorkflowOrchestrator {
    private static final Logger logger = LogManager.getLogger(WorkflowOrchestrator.class);
    private WorkflowState currentState;
    private final String clientID;

    public WorkflowOrchestrator(String clientID) {
        this.currentState = WorkflowState.PURCHASE_INDENT; // Default start
        this.clientID = clientID;
    }

    public void nextStep(String poId) {
        String productType = RuleEngine.getProductType(poId);
        String workflowStage = RuleEngine.getWorkflowStageForProductType(productType);

        logger.info("🔄 Current PO: {} | Product Type: {} | Workflow Stage: {}", poId, productType, workflowStage);

        switch (currentState) {
            case PURCHASE_INDENT:
                currentState = WorkflowState.DIRECT_PO;
                break;
            case DIRECT_PO:
                currentState = workflowStage.equals("INVOICE") ? WorkflowState.INVOICE : WorkflowState.INWARD;
                break;
            case INWARD:
                currentState = WorkflowState.INVOICE;
                break;
            case INVOICE:
                if (FeatureToggleService.isApprovalRequired(clientID, "INVOICE_APPROVAL")) {
                    List<String> approvalLevels = FeatureToggleService.getApprovalChain(clientID, "INVOICE_APPROVAL");
                    logger.info("🔍 Invoice Approval Needed: Approval Levels -> {}", approvalLevels);
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
                logger.info("✅ Workflow Completed!");
                break;
            default:
                logger.info("✅ Workflow Completed!");
                break;
        }

        logger.info("🔄 New Workflow State: {}", currentState);
    }

    public WorkflowState getCurrentState() {
        return currentState;
    }
}